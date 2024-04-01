package com.tcc.modules.shop.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.tcc.common.utils.DateUtils;
import com.tcc.modules.cust.entity.CustEntity;
import com.tcc.modules.cust.entity.CustScoreLogEntity;
import com.tcc.modules.cust.service.CustScoreLogService;
import com.tcc.modules.cust.service.CustService;
import com.tcc.modules.shop.entity.ShopCustCouponEntity;
import com.tcc.modules.shop.service.ShopCustCouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tcc.common.utils.PageUtils;
import com.tcc.common.utils.Query;

import com.tcc.modules.shop.dao.ShopOrderDao;
import com.tcc.modules.shop.entity.ShopOrderEntity;
import com.tcc.modules.shop.service.ShopOrderService;
import org.springframework.transaction.annotation.Transactional;


@Service("shopOrderService")
public class ShopOrderServiceImpl extends ServiceImpl<ShopOrderDao, ShopOrderEntity> implements ShopOrderService {

    @Autowired
    private CustService custService;

    @Autowired
    private CustScoreLogService custScoreLogService;

    @Autowired
    private ShopCustCouponService shopCustCouponService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        Long custId = MapUtil.getLong(params, "custId");
        Long couponId = MapUtil.getLong(params, "couponId");
        Integer orderStatus = MapUtil.getInt(params, "orderStatus");
        String orderNo = MapUtil.getStr(params, "orderNo");
        String orderTime = MapUtil.getStr(params, "orderTime");
        String team =  MapUtil.getStr(params, "team");
        String[] teamArr = null;
        if(team != null){
            teamArr = team.split(",");
        }
        Long startTime = null;
        Long endTime = null;
        if(StrUtil.isNotBlank(orderTime)){
            startTime = DateUtil.beginOfDay(DateUtil.parseDate(orderTime)).getTime() / 1000;
            endTime = DateUtil.endOfDay(DateUtil.parseDate(orderTime)).getTime() / 1000;
        }
        IPage<ShopOrderEntity> page = this.page(
                new Query<ShopOrderEntity>().getPage(params),
                new LambdaQueryWrapper<ShopOrderEntity>()
                        .eq(custId != null, ShopOrderEntity::getCustId, custId)
                        .eq(StrUtil.isNotBlank(orderNo), ShopOrderEntity::getOrderNo, orderNo)
                        .eq(couponId != null, ShopOrderEntity::getCouponId, couponId)
                        .eq(orderStatus != null, ShopOrderEntity::getOrderStatus, orderStatus)
                        .ge(startTime != null, ShopOrderEntity::getOrderTime, startTime)
                        .le(endTime != null, ShopOrderEntity::getOrderTime, endTime)
                        .in(teamArr != null, ShopOrderEntity::getSalesmanId, teamArr)
                        .orderByDesc(ShopOrderEntity::getId)
        );

        return new PageUtils(page);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void quitOrder(Long orderId, Long userId) {
        ShopOrderEntity order = getById(orderId);
        // 状态修改为退单
        this.update(new LambdaUpdateWrapper<ShopOrderEntity>()
                .eq(ShopOrderEntity::getId, orderId)
                .set(ShopOrderEntity::getOrderStatus, 2)
                .set(ShopOrderEntity::getQuitTime, DateUtils.getCurrentTime())
                .set(ShopOrderEntity::getQuitUser, userId)
        );
        // 修改优惠券状态为未使用
        if(order.getCouponId() != 0){
            shopCustCouponService.update(new LambdaUpdateWrapper<ShopCustCouponEntity>()
                    .eq(ShopCustCouponEntity::getId, order.getCouponId())
                    .set(ShopCustCouponEntity::getUseStatus, 0)
            );
        }
        CustEntity cust = custService.getById(order.getCustId());
        // 退还支付金额
        BigDecimal payMoney = order.getOrderPayMoney();
        Map<String, Object> params = new HashMap<>();
        params.put("custId", order.getCustId());
        params.put("leftCommissionMoney", payMoney);
        custService.updateMoney(params);

        // 写余额变化记录
        CustScoreLogEntity log = new CustScoreLogEntity();
        log.setCustId(order.getCustId());
        log.setType(14);
        log.setCreateTime(DateUtils.getCurrentTime());
        log.setScore(payMoney);
        //====lxy
        log.setBeforeScore(cust.getLeftRechargeMoney());
        log.setAfterScore(cust.getLeftRechargeMoney().add(payMoney));
        //====lxy
        log.setDirect(1);
        log.setSalesmanId(order.getSalesmanId());
        log.setIsNb(order.getIsNb());
        log.setRemark("商城退单退款：" + payMoney);
        log.setSourceId(order.getId());
        custScoreLogService.save(log);

    }


}