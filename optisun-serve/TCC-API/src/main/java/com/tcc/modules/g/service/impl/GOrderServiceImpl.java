package com.tcc.modules.g.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tcc.common.exception.WTDPException;
import com.tcc.common.utils.DateUtils;
import com.tcc.common.utils.ResultCode;
import com.tcc.modules.cust.entity.CustEntity;
import com.tcc.modules.cust.service.CustScoreLogService;
import com.tcc.modules.cust.service.CustService;
import com.tcc.modules.event.Event;
import com.tcc.modules.event.EventConstant;
import com.tcc.modules.event.EventPublisher;
import com.tcc.modules.event.EventType;
import com.tcc.modules.event.data.BalanceChangeData;
import com.tcc.modules.event.data.RebateData;
import com.tcc.modules.g.entity.GGoodsEntity;
import com.tcc.modules.g.entity.GRoomEntity;
import com.tcc.modules.g.service.GGoodsService;
import com.tcc.modules.g.service.GRoomService;
import com.tcc.modules.t.entity.TOrderEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tcc.common.utils.PageUtils;
import com.tcc.common.utils.Query;

import com.tcc.modules.g.dao.GOrderDao;
import com.tcc.modules.g.entity.GOrderEntity;
import com.tcc.modules.g.service.GOrderService;
import org.springframework.transaction.annotation.Transactional;


@Service("gOrderService")
public class GOrderServiceImpl extends ServiceImpl<GOrderDao, GOrderEntity> implements GOrderService {

    @Autowired
    private CustService custService;

    @Autowired
    private GGoodsService gGoodsService;

    @Autowired
    private GRoomService gRoomService;

    @Autowired
    private EventPublisher eventPublisher;

    @Autowired
    private CustScoreLogService custScoreLogService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<GOrderEntity> page = this.page(
                new Query<GOrderEntity>().getPage(params),
                new QueryWrapper<GOrderEntity>().eq("org_id",params.get("orgId")).eq("del_flag","0")
        );

        return new PageUtils(page);
    }
    @Override
    public PageUtils selectByCondition(@Param("condition") Map<String, Object> params) {
        IPage<Map<String,Object>> page = new Query<Map<String,Object>>().getPage(params);
        List<Map<String,Object>> list = baseMapper.selectByCondition(page, params);
        page.setRecords(list);
        return new PageUtils(page);
    }

    @Transactional
    @Override
    public GOrderEntity buy(Long userId, Long goodsId, int num, String orderNo) {
        CustEntity cust = custService.getById(userId);
        GGoodsEntity goods = gGoodsService.getById(goodsId);
//        GRoomEntity room = gRoomService.getById(goods.getRoomId());
//        BigDecimal totalMoney = goods.getPrice().multiply(Convert.toBigDecimal(num));
//        if(totalMoney.compareTo(cust.getBalance()) > 0){
//            throw new WTDPException(ResultCode.RESULT_100003);
//        }


        //校验今日是否完成下单
        Date now = new Date();
        Date todayBegin = DateUtil.beginOfDay(now);
        Date todayEnd = DateUtil.endOfDay(now);
        int count = this.count(new LambdaQueryWrapper<GOrderEntity>()
                .eq(GOrderEntity::getCustId, userId)
                .eq(GOrderEntity::getDelFlag, "0")
                .ge(GOrderEntity::getCreateTime, todayBegin.getTime() / 1000)
                .le(GOrderEntity::getCreateTime, todayEnd.getTime() / 1000));
        if(count > cust.getLevel() + 1){
            throw new WTDPException(ResultCode.RESULT_100002);
        }

        // 保存订单
        GOrderEntity order = new GOrderEntity();
        order.setOrderNo(orderNo);
        order.setCustId(userId);
        order.setOrgId(cust.getOrgId());
        order.setGoodsId(goodsId);
        order.setGoodsName(goods.getGoodsName());
        order.setGoodsImg(goods.getImage());
        order.setCommission(goods.getPrice());
        order.setStatus(2);
        order.setCreateTime(DateUtils.getCurrentTime());
        order.setUpdateTime(DateUtils.getCurrentTime());
        order.setIsNb(cust.getIsNb());
        order.setSalesmanId(cust.getSalesmanId());
        this.save(order);
        // 结算收益
        BalanceChangeData data = new BalanceChangeData();
        data.setFrozenMoney(new BigDecimal(0));
        data.setChangeMoney(order.getCommission());
        data.setDirect(EventConstant.Direct.ADD);
        data.setType(EventConstant.BalanceChangeType.GRABBING_PROFIT);
        data.setCustId(userId);
        data.setSourceId(order.getId());
        eventPublisher.publish(new Event("gSale", EventType.balanceChange, data));
        //custScoreLogService.change(cust, new Event("gSale", EventType.balanceChange, data));
        // 上级返佣
        RebateData rebateData = new RebateData();
        rebateData.setOrderId(order.getId());
        rebateData.setMoney(order.getCommission());
        rebateData.setCustId(order.getCustId());
        rebateData.setType("t");
        eventPublisher.publish(new Event("gSale", EventType.rebate, rebateData));
       // custScoreLogService.rebate(cust, new Event("gSale", EventType.rebate, rebateData));
        return order;
    }
}
