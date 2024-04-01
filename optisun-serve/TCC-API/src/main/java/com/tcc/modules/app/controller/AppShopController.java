package com.tcc.modules.app.controller;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.tcc.common.annotation.AccessLimit;
import com.tcc.common.annotation.SysLog;
import com.tcc.common.utils.*;
import com.tcc.modules.app.annotation.LoginUser;
import com.tcc.modules.app.entity.UserEntity;
import com.tcc.modules.base.AbstractAppController;
import com.tcc.modules.cust.entity.CustEntity;
import com.tcc.modules.cust.entity.CustScoreLogEntity;
import com.tcc.modules.cust.service.CustScoreLogService;
import com.tcc.modules.cust.service.CustService;
import com.tcc.modules.shop.entity.ShopCustCouponEntity;
import com.tcc.modules.shop.entity.ShopGoodsEntity;
import com.tcc.modules.shop.entity.ShopOrderEntity;
import com.tcc.modules.shop.entity.ShopOrderGoodsEntity;
import com.tcc.modules.shop.service.ShopCustCouponService;
import com.tcc.modules.shop.service.ShopGoodsService;
import com.tcc.modules.shop.service.ShopOrderGoodsService;
import com.tcc.modules.shop.service.ShopOrderService;
import com.tcc.modules.t.entity.TGoodsGradeEntity;
import com.tcc.modules.t.service.TGoodsGradeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/app/shop")
@Api("APP商城相关接口")
public class AppShopController extends AbstractAppController {

    @Autowired
    private ShopGoodsService shopGoodsService;

    @Autowired
    private ShopCustCouponService shopCustCouponService;

    @Autowired
    private ShopOrderService shopOrderService;

    @Autowired
    private ShopOrderGoodsService shopOrderGoodsService;

    @Autowired
    private TGoodsGradeService tGoodsGradeService;

    @Autowired
    private CustService custService;

    @Autowired
    private CustScoreLogService custScoreLogService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @ApiOperation("获取分类列表")
    @GetMapping("/category/list")
    public R getCategoryList() {
        List<Map<String, Object>> list = shopGoodsService.listMaps(new LambdaQueryWrapper<ShopGoodsEntity>()
                .select(ShopGoodsEntity::getCategory1)
                .groupBy(ShopGoodsEntity::getCategory1)
                .eq(ShopGoodsEntity::getStatus, 1));
        return R.ok().put("list", list);
    }

    @ApiOperation("获取产品列表")
    @GetMapping("/goods/list")
    public R getGoodsList(@RequestParam Map<String, Object> params) {
        PageUtils page = shopGoodsService.queryPage(params);
        return R.ok().put("page", page);
    }

    @ApiOperation("获取产品详情")
    @GetMapping("/goods/info")
    public R getGoodsInfo(String goodsId) {
        ShopGoodsEntity goods = shopGoodsService.getById(goodsId);
        return R.ok().put("goods", goods);
    }

    @ApiOperation("获取客户优惠券可用")
    @GetMapping("/cust/coupon")
    public R getCoupon() {
        ShopCustCouponEntity coupon = shopCustCouponService.getOne(new LambdaQueryWrapper<ShopCustCouponEntity>()
                .eq(ShopCustCouponEntity::getCustId, getUserId())
                .eq(ShopCustCouponEntity::getUseStatus, 0)
                .ge(ShopCustCouponEntity::getExpireTime, DateUtils.getCurrentTime())
                .orderByAsc(ShopCustCouponEntity::getId)
                .last("limit 0, 1"), false);
        return R.ok().put("coupon", coupon);
    }

    @ApiOperation("获取客户优惠券列表")
    @GetMapping("/cust/coupon/list")
    public R getCouponList() {
        List<ShopCustCouponEntity> list = shopCustCouponService.list(new LambdaQueryWrapper<ShopCustCouponEntity>()
                .eq(ShopCustCouponEntity::getCustId, getUserId()));
        return R.ok().put("list", list);
    }

    @ApiOperation("订单预览")
    @PostMapping("/order/preview")
    public R orderPreview(@LoginUser UserEntity user, @RequestBody Map<String, Object> params) {
        List<Map<String, Object>> cartGoodsList = (List<Map<String, Object>>) params.get("goodsList");
        List<Long> goodsIds = cartGoodsList.stream().map(item -> {
            return MapUtil.getLong(item, "id");
        }).collect(Collectors.toList());
        List<ShopGoodsEntity> goodsList = shopGoodsService.list(new LambdaQueryWrapper<ShopGoodsEntity>()
                .in(ShopGoodsEntity::getId, goodsIds)
                .orderByAsc(ShopGoodsEntity::getId)
        );
        BigDecimal orderMoney = new BigDecimal(0);
        int orderNum = 0;
        List<ShopOrderGoodsEntity> orderGoodsList = new ArrayList<>();
        for (int i = 0; i < goodsIds.size(); i++) {
            ShopGoodsEntity goods = getGoodsById(goodsList, goodsIds.get(i));
            if(goods != null && goods.getStatus() == 1){
                Integer num = Convert.toInt(cartGoodsList.get(i).get("num"));
                orderMoney = orderMoney.add(goods.getPrice().multiply(new BigDecimal(num)));
                orderNum = orderNum + num;
                ShopOrderGoodsEntity orderGoods = new ShopOrderGoodsEntity();
                orderGoods.setGoodsId(goods.getId());
                orderGoods.setGoodsName(goods.getName());
                orderGoods.setGoodsImg(goods.getImg());
                orderGoods.setGoodsPrice(goods.getPrice());
                orderGoods.setGoodsNum(num);
                orderGoodsList.add(orderGoods);
            }
        }
        BigDecimal discount = new BigDecimal(10);
        ShopCustCouponEntity coupon = shopCustCouponService.getOne(new LambdaQueryWrapper<ShopCustCouponEntity>()
                .eq(ShopCustCouponEntity::getCustId, getUserId())
                .eq(ShopCustCouponEntity::getUseStatus, 0)
                .ge(ShopCustCouponEntity::getExpireTime, DateUtils.getCurrentTime())
                .orderByAsc(ShopCustCouponEntity::getId)
                .last("limit 0, 1"), false);

        TGoodsGradeEntity grade = tGoodsGradeService.getById(user.getLevel());
        if(grade != null && grade.getShopDiscount() != null){
            discount = grade.getShopDiscount();
        }
        Long couponId = 0L;
        if(coupon != null && coupon.getDiscount().compareTo(discount) < 0){
            discount = coupon.getDiscount();
            couponId = coupon.getId();
        }
        BigDecimal payMoney = orderMoney.multiply(discount).divide(new BigDecimal(10));
        ShopOrderEntity order = new ShopOrderEntity();
        order.setGoodsList(orderGoodsList);
        order.setOrderNum(orderGoodsList.size());
        order.setOrderMoney(orderMoney);
        order.setOrderPayMoney(payMoney);
        order.setOrderDiscount(discount);
        order.setCouponId(couponId);
        order.setOrderNo(IdUtil.getSnowflake(1, 1).nextIdStr());
        redisTemplate.opsForValue().set("shop:order:" + order.getOrderNo(), Convert.toStr(order.getOrderPayMoney()), 3600 * 24, TimeUnit.SECONDS);
        return R.ok().put("order", order);
    }

    @AccessLimit
    @SysLog("订单支付")
    @Transactional(rollbackFor = Exception.class)
    @ApiOperation("订单支付")
    @PostMapping("/order/buy")
    public R orderBuy(@LoginUser UserEntity user, @RequestBody ShopOrderEntity order) {
        // 校验订单金额
        String cachePayMoneyStr = redisTemplate.opsForValue().get("shop:order:" + order.getOrderNo());
        if(StrUtil.isBlank(cachePayMoneyStr)){
            // 订单过期
            return R.error(ResultCode.RESULT_100039);
        }else {
            BigDecimal cachePayMoney = Convert.toBigDecimal(cachePayMoneyStr);
            cachePayMoney.setScale(4);
            order.getOrderPayMoney().setScale(4);
            if(cachePayMoney.compareTo(order.getOrderPayMoney()) != 0){
                return R.error(ResultCode.RESULT_100040);
            }
        }
        // 校验账户金额
        BigDecimal payMoney = order.getOrderPayMoney();
        if(payMoney.compareTo(user.getLeftCommissionMoney()) > 0){
            return R.error(ResultCode.RESULT_100003);
        }

        // 保存订单
        order.setCustId(user.getCustId());
        order.setSalesmanId(user.getSalesmanId());
        order.setIsNb(user.getIsNb());
        order.setOrderTime(DateUtils.getCurrentTime());
        shopOrderService.save(order);
        order.getGoodsList().forEach(item -> {
            item.setOrderId(order.getId());
        });
        shopOrderGoodsService.saveBatch(order.getGoodsList());

        // 使用优惠券
        if(order.getCouponId() != null && order.getCouponId() != 0L){
            shopCustCouponService.update(new LambdaUpdateWrapper<ShopCustCouponEntity>()
                    .eq(ShopCustCouponEntity::getId, order.getCouponId())
                    .set(ShopCustCouponEntity::getUseStatus, 1)
            );
        }

        // 扣减余额
        Map<String, Object> params = new HashMap<>();
        params.put("custId", user.getCustId());
        params.put("leftCommissionMoney", payMoney.multiply(new BigDecimal(-1)));
        custService.updateMoney(params);

        // 写余额变化记录
        CustScoreLogEntity log = new CustScoreLogEntity();
        log.setCustId(user.getCustId());
        log.setType(14);
        log.setCreateTime(DateUtils.getCurrentTime());
        //====lxy======
        log.setBeforeScore(user.getLeftCommissionMoney());
        log.setAfterScore(user.getLeftCommissionMoney().add(payMoney.multiply(new BigDecimal(-1))));
        //====lxy=====

        log.setScore(payMoney.multiply(new BigDecimal(-1)));
        log.setDirect(2);
        log.setSalesmanId(user.getSalesmanId());
        log.setIsNb(user.getIsNb());
        log.setRemark("商城下单扣减：" + payMoney);
        log.setSourceId(order.getId());
        custScoreLogService.save(log);
        return R.ok().put("order", order);
    }

    @ApiOperation("订单列表")
    @GetMapping("/order/list")
    public R getOrderList(Integer pageNo, Integer pageSize){
        Map<String, Object> params = new HashMap<>();
        params.put(Constant.PAGE, pageNo);
        params.put(Constant.LIMIT, pageSize);
        params.put("custId", getUserId());
        PageUtils page = shopOrderService.queryPage(params);
        return R.ok().put("data", page);
    }

    @ApiOperation("订单详情")
    @GetMapping("/order/info")
    public R getOrderInfo(Long orderId){
        ShopOrderEntity order = shopOrderService.getById(orderId);
        List<ShopOrderGoodsEntity> goodsList = shopOrderGoodsService.list(new LambdaQueryWrapper<ShopOrderGoodsEntity>().eq(ShopOrderGoodsEntity::getOrderId, orderId));
        order.setGoodsList(goodsList);
        return R.ok().put("order", order);
    }

    private ShopGoodsEntity getGoodsById(List<ShopGoodsEntity> goodsList, Long id){
        for (int i = 0; i < goodsList.size(); i++) {
            if(goodsList.get(i).getId().equals(id)){
                return goodsList.get(i);
            }
        }
        return null;
    }


}
