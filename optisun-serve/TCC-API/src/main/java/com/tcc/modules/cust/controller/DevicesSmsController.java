package com.tcc.modules.cust.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.tcc.common.annotation.SysLog;
import com.tcc.modules.s.entity.SSmsOriginalEntity;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tcc.modules.cust.entity.DevicesSmsEntity;
import com.tcc.modules.cust.service.DevicesSmsService;
import com.tcc.common.utils.PageUtils;
import com.tcc.common.utils.R;



/**
 * 下分短信表
 *
 * @author 
 * @email 
 * @date 2024-03-25 09:41:37
 */
@RestController
@RequestMapping("cust/devicessms")
public class DevicesSmsController {
    @Autowired
    private DevicesSmsService devicesSmsService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("cust:devicessms:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = devicesSmsService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("cust:devicessms:info")
    public R info(@PathVariable("id") Integer id){
		DevicesSmsEntity devicesSms = devicesSmsService.getById(id);

        return R.ok().put("devicesSms", devicesSms);
    }

    /**
     * 保存
     */
    @SysLog("手动添加下分短信")
    @RequestMapping("/save")
    @RequiresPermissions("cust:devicessms:save")
    public R save(@RequestBody DevicesSmsEntity devicesSms){
        devicesSms.setReceiveTime(System.currentTimeMillis());
        List<DevicesSmsEntity> list = new ArrayList<>();
        list.add(devicesSms);
        devicesSmsService.parseSms(list);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("cust:devicessms:update")
    public R update(@RequestBody DevicesSmsEntity devicesSms){
		devicesSmsService.updateById(devicesSms);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("cust:devicessms:delete")
    public R delete(@RequestBody Integer[] ids){
		devicesSmsService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
