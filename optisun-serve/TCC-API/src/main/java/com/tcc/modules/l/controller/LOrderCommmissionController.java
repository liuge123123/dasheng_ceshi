package com.tcc.modules.l.controller;

import java.util.Arrays;
import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tcc.modules.l.entity.LOrderCommmissionEntity;
import com.tcc.modules.l.service.LOrderCommmissionService;
import com.tcc.common.utils.PageUtils;
import com.tcc.common.utils.R;



/**
 * 理财佣金记录
 *
 * @author 
 * @email 
 * @date 2022-09-23 15:09:39
 */
@RestController
@RequestMapping("l/lordercommmission")
public class LOrderCommmissionController {
    @Autowired
    private LOrderCommmissionService lOrderCommmissionService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("l:lordercommmission:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = lOrderCommmissionService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{orderId}")
    @RequiresPermissions("l:lordercommmission:info")
    public R info(@PathVariable("orderId") Long orderId){
		LOrderCommmissionEntity lOrderCommmission = lOrderCommmissionService.getById(orderId);

        return R.ok().put("lOrderCommmission", lOrderCommmission);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("l:lordercommmission:save")
    public R save(@RequestBody LOrderCommmissionEntity lOrderCommmission){
		lOrderCommmissionService.save(lOrderCommmission);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("l:lordercommmission:update")
    public R update(@RequestBody LOrderCommmissionEntity lOrderCommmission){
		lOrderCommmissionService.updateById(lOrderCommmission);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("l:lordercommmission:delete")
    public R delete(@RequestBody Long[] orderIds){
		lOrderCommmissionService.removeByIds(Arrays.asList(orderIds));

        return R.ok();
    }

}
