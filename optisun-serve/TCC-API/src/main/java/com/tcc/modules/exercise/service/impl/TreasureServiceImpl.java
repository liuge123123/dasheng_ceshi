package com.tcc.modules.exercise.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.tcc.common.exception.WTDPException;
import com.tcc.common.utils.*;
import com.tcc.modules.cust.service.CustScoreLogService;
import com.tcc.modules.exercise.entity.TreasureOrderEntity;
import com.tcc.modules.exercise.service.TreasureOrderService;
import com.tcc.modules.l.entity.LOrderEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.tcc.modules.exercise.dao.TreasureDao;
import com.tcc.modules.exercise.entity.TreasureEntity;
import com.tcc.modules.exercise.service.TreasureService;


@Service("treasureService")
public class TreasureServiceImpl extends ServiceImpl<TreasureDao, TreasureEntity> implements TreasureService {
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private TreasureOrderService treasureOrderService;
    @Autowired
    private CustScoreLogService custScoreLogService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<TreasureEntity> page = this.page(
                new Query<TreasureEntity>().getPage(params),
                new LambdaQueryWrapper<TreasureEntity>().orderByDesc(TreasureEntity::getId)
        );
        List<TreasureEntity> list = page.getRecords();
        List<Map<String, Object>> resultList = list.stream().map(item -> {
            Integer count = treasureOrderService.count(new LambdaQueryWrapper<TreasureOrderEntity>()
                    .eq(TreasureOrderEntity::getTreasureId, item.getId())
                    .eq(TreasureOrderEntity::getStatus,0));
            JSONObject json = JSONUtil.parseObj(item);
            json.putOpt("remainder",count);
            return json;
        }).collect(Collectors.toList());
        PageUtils pageUtils = new PageUtils(page);
        pageUtils.setList(resultList);
        return pageUtils;
    }

    @Override
    public BigDecimal recive(Long custId, String code) {
        if(StringUtils.isBlank(code)){
            throw new WTDPException(ResultCode.RESULT_100053);
        }
        Boolean isLock = lock(code);
        if(!isLock){
            throw new WTDPException(ResultCode.RESULT_100054);
        }

        TreasureEntity treasureEntity = this.getOne(new LambdaQueryWrapper<TreasureEntity>()
                .eq(TreasureEntity::getCode, code)
                .eq(TreasureEntity::getStatus,0)
                .last("limit 0,1"));
        if(treasureEntity==null){
            unlock(code);
            throw new WTDPException(ResultCode.RESULT_100057);
        }
        Long validTime = treasureEntity.getCreateTime()+3600L;
        if (validTime < DateUtils.getCurrentTime()) {
            unlock(code);
            throw new WTDPException(ResultCode.RESULT_100055);
        }
        TreasureOrderEntity treasureOrderEntity = treasureOrderService.getOne(new LambdaQueryWrapper<TreasureOrderEntity>()
                .eq(TreasureOrderEntity::getUid, custId)
                .eq(TreasureOrderEntity::getTreasureId, treasureEntity.getId())
                .eq(TreasureOrderEntity::getStatus,1)
                .last("limit 0,1"));
        if(treasureOrderEntity!=null){
            unlock(code);
            throw new WTDPException(ResultCode.RESULT_100056);
        }
        TreasureOrderEntity treasureOrderEntity2 = treasureOrderService.getOne(new LambdaQueryWrapper<TreasureOrderEntity>()
                .eq(TreasureOrderEntity::getTreasureId, treasureEntity.getId())
                .eq(TreasureOrderEntity::getStatus,0)
                .last("limit 0,1"));
        if(treasureOrderEntity2==null){
            unlock(code);
            throw new WTDPException(ResultCode.RESULT_100053);
        }
        boolean flag = treasureOrderService.update(new LambdaUpdateWrapper<TreasureOrderEntity>()
                .eq(TreasureOrderEntity::getId, treasureOrderEntity2.getId())
                .set(TreasureOrderEntity::getUid, custId)
                .set(TreasureOrderEntity::getStatus,1)
                .set(TreasureOrderEntity::getUpdateTime,DateUtils.getCurrentTime()
        ));
        custScoreLogService.scoreChange(custId,1,treasureOrderEntity2.getAmount(),18,"宝箱领取:"+treasureOrderEntity2.getAmount(),treasureOrderEntity2.getId());
        //判断是否抢完
        Integer number = treasureEntity.getNumber();
        Integer count = treasureOrderService.count(new LambdaQueryWrapper<TreasureOrderEntity>()
                .eq(TreasureOrderEntity::getTreasureId, treasureEntity.getId())
                .eq(TreasureOrderEntity::getStatus,1));
        if (number.equals(count)) {
           this.update(new LambdaUpdateWrapper<TreasureEntity>()
                    .eq(TreasureEntity::getId, treasureEntity.getId())
                    .set(TreasureEntity::getStatus,1)
                    );
        }
        return treasureOrderEntity2.getAmount();
    }

    /**
     * 获取锁
     * 12/27/22
     */
    private Boolean lock(String code){
        String redisValue = redisTemplate.opsForValue().get("lock_"+code);
        if(StrUtil.isBlank(redisValue)){
            Long value = DateUtils.getCurrentTime()+5L;
            redisTemplate.opsForValue().set("lock_"+code, value+"", 5, TimeUnit.SECONDS);
            return true;
        }
        return false;
    }
    /**
     * 释放锁
     * 12/27/22
     */
    private Boolean unlock(String code)
    {
        return redisTemplate.delete("lock_"+code);
    }

}