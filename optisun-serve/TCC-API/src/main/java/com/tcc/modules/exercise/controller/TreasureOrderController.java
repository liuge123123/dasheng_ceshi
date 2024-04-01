package com.tcc.modules.exercise.controller;

import java.util.Arrays;
import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tcc.modules.exercise.entity.TreasureOrderEntity;
import com.tcc.modules.exercise.service.TreasureOrderService;
import com.tcc.common.utils.PageUtils;
import com.tcc.common.utils.R;



/**
 * 宝箱记录
 *
 * @author 
 * @email 
 * @date 2023-10-06 20:10:42
 */
@RestController
@RequestMapping("exercise/treasureorder")
public class TreasureOrderController {
    @Autowired
    private TreasureOrderService treasureOrderService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("exercise:treasureorder:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = treasureOrderService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("exercise:treasureorder:info")
    public R info(@PathVariable("id") Integer id){
		TreasureOrderEntity treasureOrder = treasureOrderService.getById(id);

        return R.ok().put("treasureOrder", treasureOrder);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("exercise:treasureorder:save")
    public R save(@RequestBody TreasureOrderEntity treasureOrder){
		treasureOrderService.save(treasureOrder);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("exercise:treasureorder:update")
    public R update(@RequestBody TreasureOrderEntity treasureOrder){
		treasureOrderService.updateById(treasureOrder);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("exercise:treasureorder:delete")
    public R delete(@RequestBody Integer[] ids){
		treasureOrderService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
