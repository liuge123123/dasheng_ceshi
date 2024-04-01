package com.tcc.modules.basic.controller;

import java.util.Arrays;
import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tcc.modules.basic.entity.ProductEntity;
import com.tcc.modules.basic.service.ProductService;
import com.tcc.common.utils.PageUtils;
import com.tcc.common.utils.R;



/**
 * 商品表
 *
 * @author 
 * @email 
 * @date 2022-05-30 16:54:43
 */
@RestController
@RequestMapping("basic/product")
public class ProductController {
    @Autowired
    private ProductService productService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("basic:product:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = productService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{productId}")
    @RequiresPermissions("basic:product:info")
    public R info(@PathVariable("productId") String productId){
		ProductEntity product = productService.getById(productId);

        return R.ok().put("product", product);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("basic:product:save")
    public R save(@RequestBody ProductEntity product){
		productService.save(product);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("basic:product:update")
    public R update(@RequestBody ProductEntity product){
		productService.updateById(product);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("basic:product:delete")
    public R delete(@RequestBody String[] productIds){
		productService.removeByIds(Arrays.asList(productIds));

        return R.ok();
    }

}
