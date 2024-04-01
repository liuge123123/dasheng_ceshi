package com.tcc.modules.sys.controller;

import java.util.Arrays;
import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tcc.modules.sys.entity.SysRegionEntity;
import com.tcc.modules.sys.service.SysRegionService;
import com.tcc.common.utils.PageUtils;
import com.tcc.common.utils.R;



/**
 * 
 *
 * @author 
 * @email 
 * @date 2020-12-15 12:54:50
 */
@RestController
@RequestMapping("sys/sysregion")
public class SysRegionController {
    @Autowired
    private SysRegionService sysRegionService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("sys:sysregion:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = sysRegionService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("sys:sysregion:info")
    public R info(@PathVariable("id") Integer id){
		SysRegionEntity sysRegion = sysRegionService.getById(id);

        return R.ok().put("sysRegion", sysRegion);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("sys:sysregion:save")
    public R save(@RequestBody SysRegionEntity sysRegion){
		sysRegionService.save(sysRegion);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("sys:sysregion:update")
    public R update(@RequestBody SysRegionEntity sysRegion){
		sysRegionService.updateById(sysRegion);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("sys:sysregion:delete")
    public R delete(@RequestBody Integer[] ids){
		sysRegionService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
