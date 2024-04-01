package com.tcc.modules.exercise.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tcc.common.exception.WTDPException;
import com.tcc.common.utils.DateUtils;
import com.tcc.modules.l.entity.LOrderEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tcc.common.utils.PageUtils;
import com.tcc.common.utils.Query;

import com.tcc.modules.exercise.dao.TreasureOrderDao;
import com.tcc.modules.exercise.entity.TreasureOrderEntity;
import com.tcc.modules.exercise.service.TreasureOrderService;


@Service("treasureOrderService")
public class TreasureOrderServiceImpl extends ServiceImpl<TreasureOrderDao, TreasureOrderEntity> implements TreasureOrderService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        LambdaQueryWrapper<TreasureOrderEntity> queryWrapper = new LambdaQueryWrapper<TreasureOrderEntity>()
                .eq(TreasureOrderEntity::getStatus, 1)
                .orderByDesc(TreasureOrderEntity::getId);

        IPage<TreasureOrderEntity> page = this.page(
                new Query<TreasureOrderEntity>().getPage(params),queryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public Boolean setFixedPacket(Long treasureId, BigDecimal amount, Integer number) {
        if((amount.remainder(new BigDecimal(number))).compareTo(BigDecimal.ZERO)==0){ //判断余数是否为0
            BigDecimal packetAmount = amount.divide(new BigDecimal(number.toString()),2, BigDecimal.ROUND_HALF_UP);
            List<TreasureOrderEntity> list = new ArrayList<>();
            for(int i=0;i<number;i++){
                TreasureOrderEntity treasureOrder = new TreasureOrderEntity();
                treasureOrder.setTreasureId(treasureId);
                treasureOrder.setAmount(packetAmount);
                treasureOrder.setStatus(0);
                treasureOrder.setCreateTime(DateUtils.getCurrentTime());
                list.add(treasureOrder);
            }
            return this.saveBatch(list);
        }else{
            throw new WTDPException("宝箱金额不能被整除");
        }
    }

    @Override
    public Boolean setRandomPacket(Long treasureId, BigDecimal amount, Integer number) {
        List<Integer> list = getRandom(amount,number);
        List<TreasureOrderEntity> treasureOrderEntityList = new ArrayList<>();
        for(int i=0;i<list.size();i++){
            TreasureOrderEntity treasureOrder = new TreasureOrderEntity();
            treasureOrder.setTreasureId(treasureId);
            Integer packetAmount = list.get(i);
            treasureOrder.setAmount(new BigDecimal(packetAmount).divide(new BigDecimal(100),2,BigDecimal.ROUND_HALF_UP));
            treasureOrder.setStatus(0);
            treasureOrder.setCreateTime(DateUtils.getCurrentTime());
            treasureOrderEntityList.add(treasureOrder);
        }
        return this.saveBatch(treasureOrderEntityList);
    }

    private List<Integer> getRandom(BigDecimal amount, Integer number){
        BigDecimal total = amount.multiply(new BigDecimal("100"));
        Integer min = 1;//最少分配的金额
        List<Integer> list = new ArrayList<>(); //定义空数组 ，存入结果
        for(int i=1;i<number;i++){
            // $safe_total = ($total - (($num - $i) * $min)) / ($num - $i) + $min;//随机安全上限
            BigDecimal safe_total = (total.subtract((new BigDecimal(number).subtract(new BigDecimal(i))).multiply(new BigDecimal(min))))
                    .divide((new BigDecimal(number).subtract(new BigDecimal(i))), 0, BigDecimal.ROUND_HALF_UP).add(new BigDecimal(min));//随机安全上限
            Random rand = new Random();
            int money = rand.nextInt(safe_total.intValue() - min + 1) + min;
            total = total.subtract(new BigDecimal(money));
            list.add(money);
        }
        list.add(total.intValue());
        return list;
    }
}