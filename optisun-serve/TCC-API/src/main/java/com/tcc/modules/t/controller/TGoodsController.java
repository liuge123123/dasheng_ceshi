package com.tcc.modules.t.controller;
import java.util.Arrays;
import java.util.Map;

import cn.hutool.core.util.StrUtil;
import com.tcc.common.utils.DateUtils;
import com.tcc.common.utils.core.text.Convert;
import com.tcc.modules.base.AbstractBackController;
import com.tcc.modules.t.entity.TGoodsGradeEntity;
import io.swagger.models.auth.In;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tcc.modules.t.entity.TGoodsEntity;
import com.tcc.modules.t.service.TGoodsService;
import com.tcc.common.utils.PageUtils;
import com.tcc.common.utils.R;



/**
 * 退单商品表
 *
 * @author
 * @email
 * @date 2022-07-19 09:17:41
 */
@RestController
@RequestMapping("t/tgoods")
public class TGoodsController extends AbstractBackController {
    @Autowired
    private TGoodsService tGoodsService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("t:tgoods:list")
    public R list(@RequestParam Map<String, Object> params){
        params.put("orgId",getOrgId());
        PageUtils page = tGoodsService.queryPage(params);
        return R.ok().put("page", page);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("t:tgoods:info")
    public R info(@PathVariable("id") Integer id){
		TGoodsEntity tGoods = tGoodsService.getById(id);
        return R.ok().put("tGoods", tGoods);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("t:tgoods:save")
    public R save(@RequestBody TGoodsEntity tGoods){
        tGoods.setDelFlag("0");
        tGoods.setOrgId(Convert.toLong(getOrgId()));
        tGoods.setCreateTime(DateUtils.getCurrentTime());
        tGoods.setCreateBy(getUserId());
		tGoodsService.save(tGoods);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("t:tgoods:update")
    public R update(@RequestBody TGoodsEntity tGoods){
        tGoods.setUpdateBy(getUserId());
        tGoods.setCreateTime(DateUtils.getCurrentTime());
		tGoodsService.updateById(tGoods);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("t:tgoods:delete")
    public R delete(@RequestBody Integer[] ids){
		tGoodsService.removeByIds(Arrays.asList(ids));
        for(Integer item : ids){
            TGoodsEntity tGoodsEntity = new TGoodsEntity();
            tGoodsEntity.setDelFlag("1");
            tGoodsEntity.setId(item);
            tGoodsService.updateById(tGoodsEntity);
        }
        return R.ok();
    }

}
