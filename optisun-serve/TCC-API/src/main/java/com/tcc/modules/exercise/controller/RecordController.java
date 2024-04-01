package com.tcc.modules.exercise.controller;

import com.tcc.common.utils.PageUtils;
import com.tcc.common.utils.R;
import com.tcc.modules.base.AbstractBackController;
import com.tcc.modules.exercise.service.RecordService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 抽奖记录
 *
 * @author taoye
 * @email
 * @date 2022-08-05 9:16:56
 */
@RestController
@RequestMapping("/exercise/record")
public class RecordController extends AbstractBackController {

    @Autowired
    private RecordService recordService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("exercise:record:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = recordService.queryPage(params);
        return R.ok().put("page", page);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("exercise:record:info")
    public R info(@PathVariable("id") Long id){
        Map<String,Object> hashMap = new HashMap<>();
        hashMap.put("id",id);
        Map<String,Object> map = recordService.getDetailById(hashMap);
        return R.ok().put("record", map);
    }

}
