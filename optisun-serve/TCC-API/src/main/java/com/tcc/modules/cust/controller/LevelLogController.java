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

import com.tcc.modules.cust.entity.LevelLogEntity;
import com.tcc.modules.cust.service.LevelLogService;
import com.tcc.common.utils.PageUtils;
import com.tcc.common.utils.R;



/**
 * 用户升降级表
 *
 * @author 
 * @email 
 * @date 2022-09-20 20:45:17
 */
@RestController
@RequestMapping("cust/levellog")
public class LevelLogController {
    @Autowired
    private LevelLogService levelLogService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("cust:levellog:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = levelLogService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("cust:levellog:info")
    public R info(@PathVariable("id") Long id){
		LevelLogEntity levelLog = levelLogService.getById(id);

        return R.ok().put("levelLog", levelLog);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("cust:levellog:save")
    public R save(@RequestBody LevelLogEntity levelLog){
		levelLogService.save(levelLog);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("cust:levellog:update")
    public R update(@RequestBody LevelLogEntity levelLog){
		levelLogService.updateById(levelLog);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("cust:levellog:delete")
    public R delete(@RequestBody Long[] ids){
		levelLogService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
