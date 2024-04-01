package com.tcc.modules.t.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
import com.tcc.modules.event.Event;
import com.tcc.modules.event.EventConstant;
import com.tcc.modules.event.EventPublisher;
import com.tcc.modules.event.EventType;
import com.tcc.modules.event.data.BalanceChangeData;
import com.tcc.modules.event.data.RebateData;
import com.tcc.modules.t.dao.TOrderDao;
import com.tcc.modules.t.entity.TGoodsEntity;
import com.tcc.modules.t.entity.TOrderEntity;
import com.tcc.modules.t.service.TGoodsService;
import com.tcc.modules.t.service.TOrderService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;


@Service("tOrderService")
public class TOrderServiceImpl extends ServiceImpl<TOrderDao, TOrderEntity> implements TOrderService {

    @Autowired
    private TOrderService tOrderService;

    @Autowired
    private TGoodsService tGoodsService;

    @Autowired
    private EventPublisher eventPublisher;

    @Autowired
    private CustService custService;

    @Autowired
    private CustScoreLogService custScoreLogService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<TOrderEntity> page = this.page(
                new Query<TOrderEntity>().getPage(params),
                new QueryWrapper<TOrderEntity>().eq("del_flag","0").eq("org_id",params.get("orgId"))
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

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void sale(Long orderId) {
        TOrderEntity order = tOrderService.getOne(new LambdaQueryWrapper<TOrderEntity>().eq(TOrderEntity::getId, orderId).last("for update"));
        TGoodsEntity goods = tGoodsService.getById(order.getGoodsId());
        // 校验订单是否可以出售
        long canSaleTime = order.getCreateTime() + Convert.toLong(goods.getPeriodId()) * 3600L;
        long now = DateUtils.getCurrentTime();
        if(canSaleTime > now || order.getStatus() != 1){
            throw new WTDPException(ResultCode.RESULT_100004);
        }
        // 订单已出不能重复操作
        if(order.getStatus() == 1){
            // 计算订单收益
            BigDecimal profit = order.getParameter2();
            // 修改订单状态
            order.setStatus(2);
            order.setCommission(profit);
            order.setSellTime(DateUtils.getCurrentTime());
            tOrderService.updateById(order);

            // 用固话账户余额变动
            BalanceChangeData balanceChangeData = new BalanceChangeData();
            balanceChangeData.setCustId(order.getCustId());
            balanceChangeData.setType(EventConstant.BalanceChangeType.REFUND_PROFIT);
            balanceChangeData.setDirect(EventConstant.Direct.ADD);
            balanceChangeData.setChangeMoney(profit);
            balanceChangeData.setFrozenMoney(order.getMoney());
            balanceChangeData.setSourceId(orderId);
           eventPublisher.publish(new Event("tSale", EventType.balanceChange, balanceChangeData));
           // CustEntity cust = custScoreLogService.change(new Event("tSale", EventType.balanceChange, balanceChangeData));
            // 上级返佣
            RebateData rebateData = new RebateData();
            rebateData.setOrderId(orderId);
            rebateData.setMoney(order.getCommission());
            rebateData.setCustId(order.getCustId());
            rebateData.setType("t");
            //custScoreLogService.rebate(cust, new Event("tSale", EventType.rebate, rebateData));
            eventPublisher.publish(new Event("tSale", EventType.rebate, rebateData));
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void sale1(Long orderId) {
        TOrderEntity order = tOrderService.getOne(new LambdaQueryWrapper<TOrderEntity>().eq(TOrderEntity::getId, orderId).last("for update"));
        // 订单已出不能重复操作
        if(order.getStatus() != 1){
            throw new WTDPException("该订单已售出，请不要重复操作");
        }
        // 计算订单收益
        BigDecimal profit = order.getParameter2();
        // 修改订单状态
        order.setStatus(2);
        order.setCommission(profit);
        order.setSellTime(DateUtils.getCurrentTime());
        tOrderService.updateById(order);

        // 用固话账户余额变动
        BalanceChangeData balanceChangeData = new BalanceChangeData();
        balanceChangeData.setCustId(order.getCustId());
        balanceChangeData.setType(EventConstant.BalanceChangeType.REFUND_PROFIT);
        balanceChangeData.setDirect(EventConstant.Direct.ADD);
        balanceChangeData.setChangeMoney(profit);
        balanceChangeData.setFrozenMoney(order.getMoney());
        balanceChangeData.setSourceId(orderId);
        eventPublisher.publish(new Event("tSale", EventType.balanceChange, balanceChangeData));
       // CustEntity cust = custScoreLogService.change(new Event("tSale", EventType.balanceChange, balanceChangeData));
        // 上级返佣
        RebateData rebateData = new RebateData();
        rebateData.setOrderId(orderId);
        rebateData.setMoney(order.getCommission());
        rebateData.setCustId(order.getCustId());
        rebateData.setType("t");
        eventPublisher.publish(new Event("tSale", EventType.rebate, rebateData));
      //  custScoreLogService.rebate(cust, new Event("tSale", EventType.rebate, rebateData));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public TOrderEntity buy(Long userId, Long goodsId) {
        CustEntity user = custService.getById(userId);
        TGoodsEntity goods = tGoodsService.getById(goodsId);
        // 下单等级交易
        if(user.getLevel() < goods.getGradeId()){
            throw new WTDPException(ResultCode.RESULT_100018);
        }
        // 校验账户余额
        if(user.getLeftCommissionMoney().compareTo(goods.getPrice()) < 0){
            throw new WTDPException(ResultCode.RESULT_100003);
        }
        if(goods.getStockNum() <= 0){
            // 产品没有库存
            throw new WTDPException(ResultCode.RESULT_100012);
        }
        //校验今日是否下单
        Date now = new Date();
        Date todayBegin = DateUtil.beginOfDay(now);
        Date todayEnd = DateUtil.endOfDay(now);
        int count = tOrderService.count(new LambdaQueryWrapper<TOrderEntity>()
                .eq(TOrderEntity::getCustId, userId)
                .eq(TOrderEntity::getDelFlag, "0")
                .ge(TOrderEntity::getCreateTime, todayBegin.getTime() / 1000)
                .le(TOrderEntity::getCreateTime, todayEnd.getTime() / 1000));
        if(count > 0){
            throw new WTDPException(ResultCode.RESULT_100002);
        }
        // 校验退税订单是否有未完结的订单
        int count1 = tOrderService.count(new LambdaQueryWrapper<TOrderEntity>()
                .eq(TOrderEntity::getCustId, userId)
                .eq(TOrderEntity::getStatus, 1)
                .eq(TOrderEntity::getDelFlag, "0"));
        if(count1 > 0){
            throw new WTDPException(ResultCode.RESULT_100015);
        }
        // 更新产品库存
        tGoodsService.update(new LambdaUpdateWrapper<TGoodsEntity>().eq(TGoodsEntity::getId, goodsId)
                .set(TGoodsEntity::getStockNum, goods.getStockNum() - 1));

        TOrderEntity order = new TOrderEntity();
        order.setCustId(userId);
        order.setOrderNo(IdUtil.getSnowflake(1, 1).nextIdStr());
        order.setGoodsId(goodsId);
        order.setGoodsMoney(goods.getPrice());
        order.setMoney(goods.getPrice());
        order.setParameter1(goods.getEstimateprice().divide(goods.getPrice()).multiply(new BigDecimal(100)));
        order.setParameter2(goods.getEstimateprice());
        order.setCreateTime(DateUtils.getCurrentTime());
        order.setUpdateTime(DateUtils.getCurrentTime());
        order.setStatus(1);
        order.setOrgId(user.getOrgId());
        order.setGradeId(Convert.toLong(goods.getGradeId()));
        order.setSalesmanId(user.getSalesmanId());
        order.setIsNb(user.getIsNb());
        tOrderService.save(order);

        // 扣减账户余额
        BalanceChangeData balanceChangeData = new BalanceChangeData();
        balanceChangeData.setCustId(order.getCustId());
        balanceChangeData.setType(EventConstant.BalanceChangeType.REFUND_BUY);
        balanceChangeData.setDirect(EventConstant.Direct.SUB);
        balanceChangeData.setChangeMoney(new BigDecimal(0));
        balanceChangeData.setFrozenMoney(order.getMoney());
        balanceChangeData.setSourceId(order.getId());
        eventPublisher.publish(new Event("tBuy", EventType.balanceChange, balanceChangeData));
       // custScoreLogService.change(new Event("tSale", EventType.balanceChange, balanceChangeData));

        return order;
    }
}
