package com.tcc.modules.g.controller;

import java.util.Arrays;
import java.util.Map;

import com.tcc.common.utils.DateUtils;
import com.tcc.common.utils.core.text.Convert;
import com.tcc.modules.base.AbstractBackController;
import com.tcc.modules.t.entity.TGoodsGradeEntity;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tcc.modules.g.entity.GGoodsEntity;
import com.tcc.modules.g.service.GGoodsService;
import com.tcc.common.utils.PageUtils;
import com.tcc.common.utils.R;



/**
 * 商品表
 *
 * @author
 * @email
 * @date 2022-07-21 17:55:20
 */
@RestController
@RequestMapping("g/ggoods")
public class GGoodsController extends AbstractBackController {
    @Autowired
    private GGoodsService gGoodsService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("g:ggoods:list")
    public R list(@RequestParam Map<String, Object> params){
        params.put("orgId",getOrgId());
        PageUtils page = gGoodsService.selectByCondition(params);
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("g:ggoods:info")
    public R info(@PathVariable("id") Long id){
		GGoodsEntity gGoods = gGoodsService.getById(id);
        return R.ok().put("gGoods", gGoods);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("g:ggoods:save")
    public R save(@RequestBody GGoodsEntity gGoods){
        gGoods.setUpdateTime(DateUtils.getCurrentTime());
        gGoods.setUpdateBy(getUserId());
        gGoods.setDelFlag("0");
        gGoods.setOrgId(Convert.toLong(getOrgId()));
        gGoods.setCreateTime(DateUtils.getCurrentTime());
        gGoods.setCreateBy(getUserId());
		gGoodsService.save(gGoods);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("g:ggoods:update")
    public R update(@RequestBody GGoodsEntity gGoods){
        gGoods.setUpdateBy(getUserId());
        gGoods.setUpdateTime(DateUtils.getCurrentTime());
		gGoodsService.updateById(gGoods);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("g:ggoods:delete")
    public R delete(@RequestBody Long[] ids){
        for(Long item : ids){
            GGoodsEntity gGoods = new GGoodsEntity();
            gGoods.setDelFlag("1");
            gGoods.setDeleteTime(DateUtils.getCurrentTime());
            gGoods.setId(item);
            gGoodsService.updateById(gGoods);
        }
        return R.ok();
    }

}
