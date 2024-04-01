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

import com.tcc.modules.cust.entity.TaskCommissionEntity;
import com.tcc.modules.cust.service.TaskCommissionService;
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
@RequestMapping("cust/taskcommission")
public class TaskCommissionController {
    @Autowired
    private TaskCommissionService taskCommissionService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("cust:taskcommission:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = taskCommissionService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{orderId}")
    @RequiresPermissions("cust:taskcommission:info")
    public R info(@PathVariable("orderId") Long orderId){
		TaskCommissionEntity taskCommission = taskCommissionService.getById(orderId);

        return R.ok().put("taskCommission", taskCommission);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("cust:taskcommission:save")
    public R save(@RequestBody TaskCommissionEntity taskCommission){
		taskCommissionService.save(taskCommission);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("cust:taskcommission:update")
    public R update(@RequestBody TaskCommissionEntity taskCommission){
		taskCommissionService.updateById(taskCommission);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("cust:taskcommission:delete")
    public R delete(@RequestBody Long[] orderIds){
		taskCommissionService.removeByIds(Arrays.asList(orderIds));

        return R.ok();
    }

}
