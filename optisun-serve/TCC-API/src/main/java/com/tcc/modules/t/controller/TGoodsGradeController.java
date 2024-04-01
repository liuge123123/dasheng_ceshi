package com.tcc.modules.t.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tcc.common.utils.DateUtils;
import com.tcc.common.utils.core.text.Convert;
import com.tcc.modules.base.AbstractBackController;
import me.chanjar.weixin.common.util.DataUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.tcc.modules.t.entity.TGoodsGradeEntity;
import com.tcc.modules.t.service.TGoodsGradeService;
import com.tcc.common.utils.PageUtils;
import com.tcc.common.utils.R;
import org.springframework.web.servlet.mvc.AbstractController;


/**
 * 商品等级表
 *
 * @author
 * @email
 * @date 2022-07-19 09:17:41
 */
@RestController
@RequestMapping("t/tgoodsgrade")
public class TGoodsGradeController extends AbstractBackController {
    @Autowired
    private TGoodsGradeService tGoodsGradeService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("t:tgoodsgrade:list")
    public R list(@RequestParam Map<String, Object> params){
        params.put("orgId",getOrgId());
        params.put("key", StrUtil.cleanBlank(cn.hutool.core.convert.Convert.toStr(params.get("key"))));
        PageUtils page = tGoodsGradeService.queryPage(params);
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("t:tgoodsgrade:info")
    public R info(@PathVariable("id") Long id){
		TGoodsGradeEntity tGoodsGrade = tGoodsGradeService.getById(id);

        return R.ok().put("tGoodsGrade", tGoodsGrade);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("t:tgoodsgrade:save")
    public R save(@RequestBody TGoodsGradeEntity tGoodsGrade){
        tGoodsGrade.setDelFlag("0");
        tGoodsGrade.setOrgId(Convert.toLong(getOrgId()));
        tGoodsGrade.setCreateBy(getUserId());
        tGoodsGrade.setCreateTime(DateUtils.getCurrentTime());
		tGoodsGradeService.save(tGoodsGrade);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("t:tgoodsgrade:update")
    public R update(@RequestBody TGoodsGradeEntity tGoodsGrade){
        tGoodsGrade.setUpdateBy(getUserId());
        tGoodsGrade.setUpdateTime(DateUtils.getCurrentTime());
		tGoodsGradeService.updateById(tGoodsGrade);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("t:tgoodsgrade:delete")
    public R delete(@RequestBody Long[] ids){
        for(Long item : ids){
                TGoodsGradeEntity goodsGradeEntity = new TGoodsGradeEntity();
                goodsGradeEntity.setDelFlag("1");
                goodsGradeEntity.setDeleteTime(DateUtils.getCurrentTime());
            goodsGradeEntity.setId(item);
            tGoodsGradeService.updateById(goodsGradeEntity);
        }
        return R.ok();
    }

    /***
     * 获取等级列表
     * @return
     */
    @GetMapping("/getListAll")
    public R getListAll(){
      List list =  tGoodsGradeService.listMaps(new QueryWrapper<TGoodsGradeEntity>().eq("org_id",getOrgId()).eq("del_flag","0")
                .select("id,grade_name as gradeName,grade_level as gradeLevel").orderByAsc("id"));
      return R.ok().put("data",list);
    }

}
