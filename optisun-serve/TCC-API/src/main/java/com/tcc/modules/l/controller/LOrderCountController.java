package com.tcc.modules.l.controller;

import java.util.Arrays;
import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tcc.modules.l.entity.LOrderCountEntity;
import com.tcc.modules.l.service.LOrderCountService;
import com.tcc.common.utils.PageUtils;
import com.tcc.common.utils.R;



/**
 * 理财佣金记录
 *
 * @author
 * @email
 * @date 2022-09-23 15:09:39
 */
@RestController
@RequestMapping("l/lordercount")
public class LOrderCountController {
    @Autowired
    private LOrderCountService LOrderCountService;



    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("l:lordercount:save")
    public R save(@RequestBody LOrderCountEntity lOrderCount){
        LOrderCountService.save(lOrderCount);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("l:lordercount:update")
    public R update(@RequestBody LOrderCountEntity lOrderCount){
        LOrderCountService.updateById(lOrderCount);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("l:lordercount:delete")
    public R delete(@RequestBody Long[] orderIds){
        LOrderCountService.removeByIds(Arrays.asList(orderIds));

        return R.ok();
    }

}
