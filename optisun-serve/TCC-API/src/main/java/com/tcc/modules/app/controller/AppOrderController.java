package com.tcc.modules.app.controller;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tcc.common.annotation.AccessLimit;
import com.tcc.common.annotation.SysLog;
import com.tcc.common.utils.Constant;
import com.tcc.common.utils.PageUtils;
import com.tcc.common.utils.R;
import com.tcc.common.utils.ResultCode;
import com.tcc.modules.app.annotation.Login;
import com.tcc.modules.app.annotation.LoginUser;
import com.tcc.modules.app.entity.UserEntity;
import com.tcc.modules.base.AbstractAppController;
import com.tcc.modules.g.entity.GGoodsEntity;
import com.tcc.modules.g.entity.GOrderEntity;
import com.tcc.modules.g.service.GGoodsService;
import com.tcc.modules.g.service.GOrderService;
import com.tcc.modules.g.service.GRoomService;
import com.tcc.modules.t.service.TGoodsService;
import com.tcc.modules.t.service.TOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * APP订单相关接口
 */
@Slf4j
@RestController
@RequestMapping("/app/order")
@Api("APP订单相关接口")
public class AppOrderController extends AbstractAppController {

    @Autowired
    private GOrderService gOrderService;

    @Autowired
    private GRoomService gRoomService;

    @Autowired
    private GGoodsService gGoodsService;

    @Autowired
    private TGoodsService tGoodsService;

    @Autowired
    private TOrderService tOrderService;


    @Login
    @ApiOperation("获取订单列表")
    @GetMapping("/g/list")
    public R gList(int pageNo, int pageSize, Integer status) {
        Map<String, Object> params = new HashMap<>();
        params.put(Constant.PAGE, pageNo);
        params.put(Constant.LIMIT, pageSize);
        params.put("custId", getUserId());
        params.put("status", status);
        PageUtils page = gOrderService.selectByCondition(params);
        return R.ok().putData(page);
    }

    @Login
    @ApiOperation("获取抢单产品列表")
    @GetMapping("/g/goods/list")
    public R gGoodsList(Long levelId) {
        List<GGoodsEntity> list = gGoodsService.list(new LambdaQueryWrapper<GGoodsEntity>()
                .eq(GGoodsEntity::getLevel, levelId)
                .eq(GGoodsEntity::getDelFlag, "0")
        );
        return R.ok().putData(list);
    }

    @Login
    @ApiOperation("退税产品列表")
    @GetMapping("/t/goods/list")
    public R tGoodsList(int pageNo, int pageSize, Integer levelId) {
        Map<String, Object> params = new HashMap<>();
        params.put(Constant.PAGE, pageNo);
        params.put(Constant.LIMIT, pageSize);
        params.put("gradeId", levelId);
        params.put("orgId", getOrgId());
        PageUtils page = tGoodsService.queryPage(params);
        return R.ok().putData(page);
    }


    @AccessLimit
    @Login
    @ApiOperation("退税下单")
    @Transactional(rollbackFor = Exception.class)
    @PostMapping("/t/buy")
    @SysLog("退税下单")
    public R tBuy(@RequestBody JSONObject params) {
        Long goodsId = params.getLong("goodsId");
        tOrderService.buy(getUserId(), goodsId);
        return R.ok();

    }

    @Login
    @ApiOperation("获取退税订单列表")
    @GetMapping("/t/list")
    public R tOrderList(int pageNo, int pageSize, Integer status) {
        Map<String, Object> params = new HashMap<>();
        params.put(Constant.PAGE, pageNo);
        params.put(Constant.LIMIT, pageSize);
        params.put("custId", getUserId());
        params.put("status", status);
        params.put("orgId", getOrgId());
        PageUtils page = tOrderService.selectByCondition(params);
        return R.ok().putData(page);
    }

    @AccessLimit
    @Login
    @ApiOperation("退税订单出售")
    @PostMapping("/t/sale")
    @SysLog("退税订单出售")
    public R tSale(@LoginUser UserEntity user, @RequestBody JSONObject params) {
        Long orderId = params.getLong("orderId");
        tOrderService.sale(orderId);
        return R.ok();
    }

    @Login
    @ApiOperation("获取抢单房间产品")
    @GetMapping("/g/goods")
    public R gGoods(@LoginUser UserEntity user, Long level) {
        BigDecimal balance = user.getLeftCommissionMoney();
        List<GGoodsEntity> list = gGoodsService.list(new LambdaQueryWrapper<GGoodsEntity>().eq(GGoodsEntity::getLevel, level).eq(GGoodsEntity::getDelFlag, "0").le(GGoodsEntity::getPrice, balance));
        if (list.size() == 0) {
            return R.error(ResultCode.RESULT_100005);
        }
        int index = RandomUtil.randomInt(list.size());
        GGoodsEntity goods = list.get(index);
        int num = Convert.toInt(Math.floor(balance.divide(goods.getPrice()).doubleValue()));
        JSONObject data = JSONUtil.parseObj(goods);
        data.putOpt("num", num);
        data.putOpt("orderNo", IdUtil.getSnowflake(1, 1).nextIdStr());
        data.putOpt("date", DateUtil.formatDate(new Date()));
        return R.ok().putData(data);
    }

    @AccessLimit
    @Login
    @ApiOperation("抢单")
    @PostMapping("/g/buy")
    @SysLog("客户抢单")
    public R gBuy(@RequestBody JSONObject params) {
        Long userId = getUserId();
        Long goodsId = params.getLong("goodsId");
        int num = params.getInt("num");
        String orderNo = params.getStr("orderNo");
        GOrderEntity order = gOrderService.buy(userId, goodsId, num, orderNo);
        return R.ok().putData(order);
    }

}
