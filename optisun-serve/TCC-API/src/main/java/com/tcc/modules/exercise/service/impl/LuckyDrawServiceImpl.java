package com.tcc.modules.exercise.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tcc.common.exception.WTDPException;
import com.tcc.common.utils.DateUtils;
import com.tcc.common.utils.PageUtils;
import com.tcc.common.utils.Query;
import com.tcc.common.utils.ResultCode;
import com.tcc.modules.cust.entity.CustEntity;
import com.tcc.modules.cust.service.CustScoreLogService;
import com.tcc.modules.cust.service.CustService;
import com.tcc.modules.event.EventPublisher;
import com.tcc.modules.exercise.dao.LuckyDrawDao;
import com.tcc.modules.exercise.entity.LuckyDrawEntity;
import com.tcc.modules.exercise.entity.PrizeEntity;
import com.tcc.modules.exercise.entity.RecordEntity;
import com.tcc.modules.exercise.service.LuckyDrawService;
import com.tcc.modules.exercise.service.PrizeService;
import com.tcc.modules.exercise.service.RecordService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service("LuckyDrawService")
public class LuckyDrawServiceImpl extends ServiceImpl<LuckyDrawDao, LuckyDrawEntity> implements LuckyDrawService {

    @Autowired
    private EventPublisher eventPublisher;

    @Autowired
    private RecordService recordService;

    @Autowired
    private PrizeService prizeService;


    @Autowired
    private CustService custService;

    @Autowired
    private CustScoreLogService custScoreLogService;

    @Override
    public Map<String, Object> selectByCondition(@Param("condition") Map<String, Object> params) {
        Map<String, Object> map = baseMapper.selectByCondition(params);
        return map;
    }

    @Override
    public PageUtils selectByConditions(@Param("condition") Map<String, Object> params) {
        IPage<Map<String, Object>> page = new Query<Map<String, Object>>().getPage(params);
        List<Map<String, Object>> list = baseMapper.selectByConditions(page, params);
        page.setRecords(list);
        return new PageUtils(page);
    }

    /**
     * 返回中奖
     *
     * @param actId
     * @param custId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public PrizeEntity excute(Long actId, Long custId, String custName) {
        // 1. 判断是否在抽奖活动时间范围内
        // 2. 限制每天抽奖只能抽3次
        // 3. 按照奖品中奖率抽取一个奖品
        LuckyDrawEntity luckyDraw = this.getById(actId);
        Date now = new Date();
        if (!(now.compareTo(luckyDraw.getStartTime()) >= 0 && now.compareTo(luckyDraw.getEndTime()) <= 0)) {
            throw new WTDPException(ResultCode.RESULT_100020);
        }
        if (luckyDraw.getMaxNum() > 0) {
            Date nowBegin = DateUtil.beginOfDay(now);
            Date nowEnd = DateUtil.endOfDay(now);
            int count = recordService.count(new LambdaUpdateWrapper<RecordEntity>()
                    .eq(RecordEntity::getUserId, custId)
                    .ge(RecordEntity::getDrawTime, nowBegin)
                    .le(RecordEntity::getDrawTime, nowEnd)
            );
            if (count > luckyDraw.getMaxNum()) {
                throw new WTDPException(ResultCode.RESULT_100021);
            }
        }
        //获取客户的剩余抽奖次数
        CustEntity custEntity = custService.getById(custId);
        int luckLeftNum = custEntity.getLuckLeftNum();
        if (luckLeftNum <= 0) {
            throw new WTDPException(ResultCode.RESULT_100031);
        }
        List<PrizeEntity> prizeList = prizeService.list(new LambdaQueryWrapper<PrizeEntity>()
                .eq(PrizeEntity::getDelFlag, 0)
                .gt(PrizeEntity::getPrizeNum, 0)
        );
        long maxNum = 0;
        for (int i = 0; i < prizeList.size(); i++) {
            maxNum += prizeList.get(i).getRatio();
        }
        long random = RandomUtil.randomLong(0, maxNum);
        int currentStart = 0;
        int currentEnd = 0;
        PrizeEntity prize = null;
        for (int i = 0; i < prizeList.size(); i++) {
            currentEnd += prizeList.get(i).getRatio();
            if (random >= currentStart && random <= currentEnd) {
                // 随机数在概率范围内 则认为中奖
                prize = prizeList.get(i);
                break;
            }
        }
        if (prize != null) {
            RecordEntity record = new RecordEntity();
            record.setDrawTime(now);
            record.setLuckyDrawId(actId);
            record.setPrizeId(prize.getId());
            record.setUserId(custId);
            record.setUserName(custName);
            record.setIsHit(1);
            record.setHitPrize(prize.getPrizeName());
            record.setHitPrizeImg(prize.getPrizeImg());
            record.setHitPrizeVal(prize.getPrizeValue());
            record.setCreateTime(DateUtils.getCurrentTime());
            record.setUpdateTime(DateUtils.getCurrentTime());
            recordService.save(record);
            //判断库存数量是否够
            boolean flag = prizeService.update(new LambdaUpdateWrapper<PrizeEntity>()
                    .set(PrizeEntity::getPrizeNum, prize.getPrizeNum() - 1)
                    .eq(PrizeEntity::getId, prize.getId())
                    .gt(PrizeEntity::getPrizeNum, 0)
            );
            if (!flag) {
                throw new WTDPException(ResultCode.RESULT_100032);
            }
            Map<String, Object> custUpdate = new HashMap<>();
            custUpdate.put("custId", custId);
            if ("SCORE".equals(prize.getPrizeType())) {
                //抽奖奖金到佣金余额
//                custUpdate.put("leftCommissionMoney", new BigDecimal(prize.getPrizeValue()));
//                custUpdate.put("totalCommissionMoney", new BigDecimal(prize.getPrizeValue()));
               custUpdate.put("otherCommissionMoney", new BigDecimal(prize.getPrizeValue()));
                custScoreLogService.scoreChange(custId,1, new BigDecimal(prize.getPrizeValue()),9,"中奖赠送金额:"+prize.getPrizeValue());
            }
            custUpdate.put("luckLeftNum", -1);
            custService.updateMoney(custUpdate);
            return prize;
        }else{
            throw new WTDPException(ResultCode.RESULT_100032);
        }

    }
}
