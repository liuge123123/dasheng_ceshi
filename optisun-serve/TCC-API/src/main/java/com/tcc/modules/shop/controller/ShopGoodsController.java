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

import com.tcc.modules.shop.entity.ShopGoodsEntity;
import com.tcc.modules.shop.service.ShopGoodsService;
import com.tcc.common.utils.PageUtils;
import com.tcc.common.utils.R;



/**
 * 商城商品表
 *
 * @author 
 * @email 
 * @date 2023-02-21 08:58:33
 */
@RestController
@RequestMapping("shop/shopgoods")
public class ShopGoodsController {
    @Autowired
    private ShopGoodsService shopGoodsService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("shop:shopgoods:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = shopGoodsService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("shop:shopgoods:info")
    public R info(@PathVariable("id") Long id){
		ShopGoodsEntity shopGoods = shopGoodsService.getById(id);

        return R.ok().put("shopGoods", shopGoods);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("shop:shopgoods:save")
    public R save(@RequestBody ShopGoodsEntity shopGoods){
		shopGoodsService.save(shopGoods);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("shop:shopgoods:update")
    public R update(@RequestBody ShopGoodsEntity shopGoods){
		shopGoodsService.updateById(shopGoods);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("shop:shopgoods:delete")
    public R delete(@RequestBody Long[] ids){
		shopGoodsService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
