package com.tcc.modules.l.controller;

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

import com.tcc.modules.l.entity.OrderCountEntity;
import com.tcc.modules.l.service.OrderCountService;
import com.tcc.common.utils.PageUtils;
import com.tcc.common.utils.R;



/**
 * 非福利区订单次数记录
 *
 * @author 
 * @email 
 * @date 2023-12-19 16:33:35
 */
@RestController
@RequestMapping("l/ordercount")
public class OrderCountController {
    @Autowired
    private OrderCountService orderCountService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("l:ordercount:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = orderCountService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("l:ordercount:info")
    public R info(@PathVariable("id") Long id){
		OrderCountEntity orderCount = orderCountService.getById(id);

        return R.ok().put("orderCount", orderCount);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("l:ordercount:save")
    public R save(@RequestBody OrderCountEntity orderCount){
        orderCount.setCreateTime(DateUtils.getCurrentTime());
        orderCount.setUpdateTime(DateUtils.getCurrentTime());
		orderCountService.save(orderCount);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("l:ordercount:update")
    public R update(@RequestBody OrderCountEntity orderCount){
        orderCount.setUpdateTime(DateUtils.getCurrentTime());
		orderCountService.updateById(orderCount);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("l:ordercount:delete")
    public R delete(@RequestBody Long[] ids){
		orderCountService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
