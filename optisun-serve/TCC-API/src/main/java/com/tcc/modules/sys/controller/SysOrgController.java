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

import com.tcc.modules.sys.entity.SysOrgEntity;
import com.tcc.modules.sys.service.SysOrgService;
import com.tcc.common.utils.PageUtils;
import com.tcc.common.utils.R;



/**
 * 
 *
 * @author 
 * @email 
 * @date 2021-04-19 11:25:56
 */
@RestController
@RequestMapping("sys/sysorg")
public class SysOrgController {
    @Autowired
    private SysOrgService sysOrgService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("sys:sysorg:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = sysOrgService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("sys:sysorg:info")
    public R info(@PathVariable("id") Integer id){
		SysOrgEntity sysOrg = sysOrgService.getById(id);

        return R.ok().put("sysOrg", sysOrg);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("sys:sysorg:save")
    public R save(@RequestBody SysOrgEntity sysOrg){
		sysOrgService.save(sysOrg);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("sys:sysorg:update")
    public R update(@RequestBody SysOrgEntity sysOrg){
		sysOrgService.updateById(sysOrg);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("sys:sysorg:delete")
    public R delete(@RequestBody Integer[] ids){
		sysOrgService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
