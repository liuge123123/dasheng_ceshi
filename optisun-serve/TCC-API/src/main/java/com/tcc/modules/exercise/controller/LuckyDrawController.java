package com.tcc.modules.exercise.controller;

import com.tcc.common.utils.DateUtils;
import com.tcc.common.utils.PageUtils;
import com.tcc.common.utils.R;
import com.tcc.modules.base.AbstractBackController;
import com.tcc.modules.exercise.entity.LuckyDrawEntity;
import com.tcc.modules.exercise.service.LuckyDrawService;
import com.tcc.modules.exercise.service.PrizeService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;

/**
 * 抽奖
 *
 * @author taoye
 * @email
 * @date 2022-08-04 9:16:56
 */
@RestController
@RequestMapping("/exercise/luckyDraw")
public class LuckyDrawController extends AbstractBackController {

    @Autowired
    private LuckyDrawService luckyDrawService;

    @Autowired
    private PrizeService prizeService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("exercise:luckyDraw:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = luckyDrawService.selectByConditions(params);
        return R.ok().put("page", page);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("exercise:luckyDraw:info")
    public R info(@PathVariable("id") Long id){
        LuckyDrawEntity luckyDraw = luckyDrawService.getById(id);
        return R.ok().put("luckyDraw", luckyDraw);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("exercise:luckyDraw:save")
    public R save(@RequestBody LuckyDrawEntity luckyDraw){
        luckyDraw.setCreateTime(DateUtils.getCurrentTime());
        luckyDraw.setUpdateTime(DateUtils.getCurrentTime());
        luckyDraw.setCreateBy(getUserId());
        luckyDraw.setUpdateBy(getUserId());
        luckyDraw.setDelFlag("0");
        luckyDrawService.save(luckyDraw);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("exercise:luckyDraw:update")
    public R update(@RequestBody LuckyDrawEntity luckyDraw){
        luckyDraw.setUpdateBy(getUserId());
        luckyDraw.setUpdateTime(DateUtils.getCurrentTime());
        luckyDrawService.updateById(luckyDraw);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("exercise:luckyDraw:delete")
    public R delete(@RequestBody Integer[] ids){
        luckyDrawService.removeByIds(Arrays.asList(ids));
        for(Integer item : ids){
            LuckyDrawEntity luckyDraw = new LuckyDrawEntity();
            luckyDraw.setDelFlag("1");
            luckyDraw.setId(Long.valueOf(item));
            luckyDrawService.updateById(luckyDraw);
        }
        return R.ok();
    }

}
