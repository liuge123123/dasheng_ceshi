package com.tcc.modules.cust.controller;

import java.util.Arrays;
import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tcc.modules.cust.entity.VipCommmissionEntity;
import com.tcc.modules.cust.service.VipCommmissionService;
import com.tcc.common.utils.PageUtils;
import com.tcc.common.utils.R;



/**
 * 
 *
 * @author 
 * @email 
 * @date 2022-09-20 20:45:17
 */
@RestController
@RequestMapping("cust/vipcommmission")
public class VipCommmissionController {
    @Autowired
    private VipCommmissionService vipCommmissionService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("cust:vipcommmission:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = vipCommmissionService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{orderId}")
    @RequiresPermissions("cust:vipcommmission:info")
    public R info(@PathVariable("orderId") Long orderId){
		VipCommmissionEntity vipCommmission = vipCommmissionService.getById(orderId);

        return R.ok().put("vipCommmission", vipCommmission);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("cust:vipcommmission:save")
    public R save(@RequestBody VipCommmissionEntity vipCommmission){
		vipCommmissionService.save(vipCommmission);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("cust:vipcommmission:update")
    public R update(@RequestBody VipCommmissionEntity vipCommmission){
		vipCommmissionService.updateById(vipCommmission);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("cust:vipcommmission:delete")
    public R delete(@RequestBody Long[] orderIds){
		vipCommmissionService.removeByIds(Arrays.asList(orderIds));

        return R.ok();
    }

}
