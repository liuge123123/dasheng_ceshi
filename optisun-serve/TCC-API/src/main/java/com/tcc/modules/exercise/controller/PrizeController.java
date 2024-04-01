package com.tcc.modules.exercise.controller;

import com.tcc.common.utils.DateUtils;
import com.tcc.common.utils.PageUtils;
import com.tcc.common.utils.R;
import com.tcc.modules.base.AbstractBackController;
import com.tcc.modules.exercise.entity.PrizeEntity;
import com.tcc.modules.exercise.service.PrizeService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;

/**
 * 奖品信息
 *
 * @author taoye
 * @email
 * @date 2022-08-05 9:16:56
 */
@RestController
@RequestMapping("/exercise/prize")
public class PrizeController extends AbstractBackController {

    @Autowired
    private PrizeService prizeService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("exercise:prize:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = prizeService.selectByCondition(params);
        return R.ok().put("page", page);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("exercise:prize:info")
    public R info(@PathVariable("id") Long id){
        PrizeEntity prize = prizeService.getById(id);
        return R.ok().put("prize", prize);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("exercise:prize:save")
    public R save(@RequestBody PrizeEntity price){
        price.setCreateTime(DateUtils.getCurrentTime());
        price.setUpdateTime(DateUtils.getCurrentTime());
        price.setCreateBy(getUserId());
        price.setUpdateBy(getUserId());
        price.setDelFlag("0");
        prizeService.save(price);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("exercise:prize:update")
    public R update(@RequestBody PrizeEntity price){
        price.setUpdateBy(getUserId());
        price.setUpdateTime(DateUtils.getCurrentTime());
        prizeService.updateById(price);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("exercise:prize:delete")
    public R delete(@RequestBody Integer[] ids){
        prizeService.removeByIds(Arrays.asList(ids));
        for(Integer item : ids){
            PrizeEntity prize = new PrizeEntity();
            prize.setDelFlag("1");
            prize.setId(Long.valueOf(item));
            prizeService.updateById(prize);
        }
        return R.ok();
    }
}
