package com.tcc.modules.s.controller;

import java.util.Arrays;
import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tcc.modules.s.entity.SSmsEntity;
import com.tcc.modules.s.service.SSmsService;
import com.tcc.common.utils.PageUtils;
import com.tcc.common.utils.R;



/**
 * 短信表
 *
 */
@RestController
@RequestMapping("s/ssms")
public class SSmsController {
    @Autowired
    private SSmsService sSmsService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("s:ssms:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = sSmsService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("s:ssms:info")
    public R info(@PathVariable("id") Long id){
		SSmsEntity sSms = sSmsService.getById(id);

        return R.ok().put("sSms", sSms);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("s:ssms:save")
    public R save(@RequestBody SSmsEntity sSms){
		sSmsService.save(sSms);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("s:ssms:update")
    public R update(@RequestBody SSmsEntity sSms){
		sSmsService.updateById(sSms);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("s:ssms:delete")
    public R delete(@RequestBody Long[] ids){
		sSmsService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
