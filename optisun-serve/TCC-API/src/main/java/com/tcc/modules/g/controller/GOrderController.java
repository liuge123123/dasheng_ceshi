package com.tcc.modules.g.controller;

import cn.hutool.core.util.StrUtil;
import com.tcc.common.utils.DateUtils;
import com.tcc.common.utils.PageUtils;
import com.tcc.common.utils.R;
import com.tcc.common.utils.core.text.Convert;
import com.tcc.modules.base.AbstractBackController;
import com.tcc.modules.g.entity.GOrderEntity;
import com.tcc.modules.g.service.GOrderService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


/**
 * 订单表
 *
 * @author
 * @email
 * @date 2022-07-21 17:55:20
 */
@RestController
@RequestMapping("g/gorder")
public class GOrderController extends AbstractBackController {
    @Autowired
    private GOrderService gOrderService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("g:gorder:list")
    public R list(@RequestParam Map<String, Object> params) {
        params.put("orgId", getOrgId());
        String teamList = cn.hutool.core.convert.Convert.toStr(params.get("team"));
        if (StrUtil.isNotBlank(teamList)) {
            String[] checkList = teamList.split(",");
            params.put("team", checkList);
        } else {
            return R.error("请选择人员");
        }
        PageUtils page = gOrderService.selectByCondition(params);
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("g:gorder:info")
    public R info(@PathVariable("id") Long id) {
        GOrderEntity gOrder = gOrderService.getById(id);
        return R.ok().put("gOrder", gOrder);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("g:gorder:save")
    public R save(@RequestBody GOrderEntity gOrder) {
        gOrder.setCreateBy(getUserId());
        gOrder.setCreateTime(DateUtils.getCurrentTime());
        gOrder.setDelFlag("0");
        gOrder.setOrgId(Convert.toLong(getOrgId()));
        gOrderService.save(gOrder);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("g:gorder:update")
    public R update(@RequestBody GOrderEntity gOrder) {
        gOrder.setUpdateBy(getUserId());
        gOrder.setUpdateTime(DateUtils.getCurrentTime());
        gOrderService.updateById(gOrder);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("g:gorder:delete")
    public R delete(@RequestBody Long[] ids) {
        for (Long item : ids) {
            GOrderEntity gOrder = new GOrderEntity();
            gOrder.setDelFlag("1");
            gOrder.setDeleteTime(DateUtils.getCurrentTime());
            gOrder.setId(item);
            gOrderService.updateById(gOrder);
        }
        return R.ok();
    }


}
