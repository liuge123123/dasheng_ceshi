package com.tcc.modules.s.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tcc.common.utils.DateUtils;
import com.tcc.modules.base.AbstractBackController;
import com.tcc.modules.cust.entity.CustEntity;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tcc.modules.s.entity.SBankCateEntity;
import com.tcc.modules.s.service.SBankCateService;
import com.tcc.common.utils.PageUtils;
import com.tcc.common.utils.R;



/**
 * 银行种类
 *
 * @author 
 * @email 
 * @date 2022-07-19 13:56:50
 */
@RestController
@RequestMapping("s/sbankcate")
public class SBankCateController extends AbstractBackController {
    @Autowired
    private SBankCateService sBankCateService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("s:sbankcate:list")
    public R list(@RequestParam Map<String, Object> params){
        params.put("orgId",getOrgId());
        PageUtils page = sBankCateService.queryPage(params);

        return R.ok().put("page", page);
    }

    /**
     * 获取分类列表
     * @return
     */
    @RequestMapping("/listAll")
    public R listAll(){
      List list =   sBankCateService.listMaps(new QueryWrapper<SBankCateEntity>().eq("org_id",getOrgId()).eq("del_flag","0").select("id,name"));
      return R.ok().put("data",list);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("s:sbankcate:info")
    public R info(@PathVariable("id") Long id){
		SBankCateEntity sBankCate = sBankCateService.getById(id);

        return R.ok().put("sBankCate", sBankCate);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("s:sbankcate:save")
    public R save(@RequestBody SBankCateEntity sBankCate){
        sBankCate.setCreateTime(DateUtils.getCurrentTime());
        sBankCate.setCreateBy(getUserId());
        sBankCate.setOrgId(getOrgId());
        sBankCate.setDelFlag("0");
		sBankCateService.save(sBankCate);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("s:sbankcate:update")
    public R update(@RequestBody SBankCateEntity sBankCate){
        sBankCate.setUpdateBy(getUserId());
        sBankCate.setUpdateTime(DateUtils.getCurrentTime());
		sBankCateService.updateById(sBankCate);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("s:sbankcate:delete")
    public R delete(@RequestBody Long[] ids){
        for (Long id:ids) {
            SBankCateEntity model = new SBankCateEntity();
            model.setDelFlag("1");
             sBankCateService.update(model,
                    new QueryWrapper<SBankCateEntity>().eq("id", id));
        }
        return R.ok();
    }

}
