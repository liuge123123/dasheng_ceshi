package com.tcc.modules.cust.controller;

import java.util.Arrays;
import java.util.Map;

import com.tcc.common.utils.DateUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tcc.modules.cust.entity.DevicesEntity;
import com.tcc.modules.cust.service.DevicesService;
import com.tcc.common.utils.PageUtils;
import com.tcc.common.utils.R;



/**
 * 下分设备列表
 *
 * @author 
 * @email 
 * @date 2024-03-20 14:51:59
 */
@RestController
@RequestMapping("cust/devices")
public class DevicesController {
    @Autowired
    private DevicesService devicesService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("cust:devices:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = devicesService.queryPage(params);
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{deviceId}")
    @RequiresPermissions("cust:devices:info")
    public R info(@PathVariable("deviceId") Long deviceId){
		DevicesEntity devices = devicesService.getById(deviceId);

        return R.ok().put("devices", devices);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("cust:devices:save")
    public R save(@RequestBody DevicesEntity devices){
        devices.setCreateTime(DateUtils.getCurrentTime());
		devicesService.save(devices);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("cust:devices:update")
    public R update(@RequestBody DevicesEntity devices){
        devices.setUpdateTime(DateUtils.getCurrentTime());
		devicesService.updateById(devices);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("cust:devices:delete")
    public R delete(@RequestBody Long[] deviceIds){
		devicesService.removeByIds(Arrays.asList(deviceIds));

        return R.ok();
    }

}
