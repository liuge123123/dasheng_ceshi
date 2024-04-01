package com.tcc.modules.exercise.controller;

import java.util.Arrays;
import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tcc.modules.exercise.entity.SignProductEntity;
import com.tcc.modules.exercise.service.SignProductService;
import com.tcc.common.utils.PageUtils;
import com.tcc.common.utils.R;



/**
 * 积分兑换产品信息表
 *
 * @author 
 * @email 
 * @date 2022-10-05 11:40:10
 */
@RestController
@RequestMapping("exercise/signproduct")
public class SignProductController {
    @Autowired
    private SignProductService signProductService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("exercise:signproduct:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = signProductService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("exercise:signproduct:info")
    public R info(@PathVariable("id") Integer id){
		SignProductEntity signProduct = signProductService.getById(id);

        return R.ok().put("signProduct", signProduct);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("exercise:signproduct:save")
    public R save(@RequestBody SignProductEntity signProduct){
		signProductService.save(signProduct);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("exercise:signproduct:update")
    public R update(@RequestBody SignProductEntity signProduct){
		signProductService.updateById(signProduct);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("exercise:signproduct:delete")
    public R delete(@RequestBody Integer[] ids){
		signProductService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
