package com.tcc.modules.shop.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.tcc.common.annotation.SysLog;
import com.tcc.modules.base.AbstractBackController;
import com.tcc.modules.shop.entity.ShopOrderGoodsEntity;
import com.tcc.modules.shop.service.ShopOrderGoodsService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tcc.modules.shop.entity.ShopOrderEntity;
import com.tcc.modules.shop.service.ShopOrderService;
import com.tcc.common.utils.PageUtils;
import com.tcc.common.utils.R;



/**
 * 商城订单表
 *
 * @author 
 * @email 
 * @date 2023-02-21 08:58:33
 */
@RestController
@RequestMapping("shop/shoporder")
public class ShopOrderController extends AbstractBackController {
    @Autowired
    private ShopOrderService shopOrderService;

    @Autowired
    private ShopOrderGoodsService shopOrderGoodsService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("shop:shoporder:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = shopOrderService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("shop:shoporder:info")
    public R info(@PathVariable("id") Long id){
		ShopOrderEntity shopOrder = shopOrderService.getById(id);
        List<ShopOrderGoodsEntity> list = shopOrderGoodsService.list(new LambdaQueryWrapper<ShopOrderGoodsEntity>().eq(ShopOrderGoodsEntity::getOrderId, id));
        shopOrder.setGoodsList(list);
        return R.ok().put("shopOrder", shopOrder);
    }

    /**
     * 退单
     */
    @SysLog("商城退单")
    @RequestMapping("/quit")
    @RequiresPermissions("shop:shoporder:quit")
    public R quit(@RequestBody Long orderId){
		shopOrderService.quitOrder(orderId, getUserId());
        return R.ok();
    }

    @SysLog("修改商城订单收货信息")
    @RequestMapping("/updateBuyer")
    public R updateBuyer(@RequestBody ShopOrderEntity shopOrder){
        shopOrderService.update(new LambdaUpdateWrapper<ShopOrderEntity>()
                .eq(ShopOrderEntity::getId, shopOrder.getId())
                .set(ShopOrderEntity::getBuyerName, shopOrder.getBuyerName())
                .set(ShopOrderEntity::getBuyderPhone, shopOrder.getBuyderPhone())
                .set(ShopOrderEntity::getBuyerAddress, shopOrder.getBuyerAddress())
        );
        return R.ok();
    }


}
