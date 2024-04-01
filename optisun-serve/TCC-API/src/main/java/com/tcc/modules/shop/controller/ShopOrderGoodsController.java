package com.tcc.modules.shop.controller;

import java.util.Arrays;
import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tcc.modules.shop.entity.ShopOrderGoodsEntity;
import com.tcc.modules.shop.service.ShopOrderGoodsService;
import com.tcc.common.utils.PageUtils;
import com.tcc.common.utils.R;



/**
 * 订单商品表
 *
 * @author 
 * @email 
 * @date 2023-02-21 08:58:33
 */
@RestController
@RequestMapping("shop/shopordergoods")
public class ShopOrderGoodsController {
    @Autowired
    private ShopOrderGoodsService shopOrderGoodsService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("shop:shopordergoods:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = shopOrderGoodsService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("shop:shopordergoods:info")
    public R info(@PathVariable("id") Long id){
		ShopOrderGoodsEntity shopOrderGoods = shopOrderGoodsService.getById(id);

        return R.ok().put("shopOrderGoods", shopOrderGoods);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("shop:shopordergoods:save")
    public R save(@RequestBody ShopOrderGoodsEntity shopOrderGoods){
		shopOrderGoodsService.save(shopOrderGoods);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("shop:shopordergoods:update")
    public R update(@RequestBody ShopOrderGoodsEntity shopOrderGoods){
		shopOrderGoodsService.updateById(shopOrderGoods);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("shop:shopordergoods:delete")
    public R delete(@RequestBody Long[] ids){
		shopOrderGoodsService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
