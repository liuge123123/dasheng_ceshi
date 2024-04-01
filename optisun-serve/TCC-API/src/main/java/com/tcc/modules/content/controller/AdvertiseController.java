package com.tcc.modules.content.controller;

import java.util.Arrays;
import java.util.Map;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.tcc.common.utils.DateUtils;
import com.tcc.modules.base.AbstractBackController;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tcc.modules.content.entity.AdvertiseEntity;
import com.tcc.modules.content.service.AdvertiseService;
import com.tcc.common.utils.PageUtils;
import com.tcc.common.utils.R;



/**
 * 轮播图片
 *
 * @author 
 * @email 
 * @date 2022-05-30 17:05:32
 */
@RestController
@RequestMapping("content/advertise")
public class AdvertiseController extends AbstractBackController {
    @Autowired
    private AdvertiseService advertiseService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("content:advertise:list")
    public R list(@RequestParam Map<String, Object> params){
        params.put("orgId",getOrgId());
        params.put("key", StrUtil.cleanBlank(Convert.toStr(params.get("key"))));
        PageUtils page = advertiseService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("content:advertise:info")
    public R info(@PathVariable("id") Long id){
		AdvertiseEntity advertise = advertiseService.getById(id);

        return R.ok().put("advertise", advertise);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("content:advertise:save")
    public R save(@RequestBody AdvertiseEntity advertise){
        advertise.setCreateBy(getUserId());
        advertise.setCreateTime(DateUtils.getCurrentTime());
        advertise.setOrgId(getOrgId());
		advertiseService.save(advertise);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("content:advertise:update")
    public R update(@RequestBody AdvertiseEntity advertise){
        advertise.setUpdateBy(getUserId());
        advertise.setUpdateTime(DateUtils.getCurrentTime());
		advertiseService.updateById(advertise);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("content:advertise:delete")
    public R delete(@RequestBody Long[] ids){
		advertiseService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
