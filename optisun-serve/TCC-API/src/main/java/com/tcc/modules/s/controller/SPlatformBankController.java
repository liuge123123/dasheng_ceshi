package com.tcc.modules.s.controller;

import java.util.Arrays;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tcc.common.annotation.SysLog;
import com.tcc.common.utils.DateUtils;
import com.tcc.modules.base.AbstractBackController;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tcc.modules.s.entity.SPlatformBankEntity;
import com.tcc.modules.s.service.SPlatformBankService;
import com.tcc.common.utils.PageUtils;
import com.tcc.common.utils.R;



/**
 * 平台银行
 *
 * @author 
 * @email 
 * @date 2022-07-19 16:34:54
 */
@RestController
@RequestMapping("s/splatformbank")
public class SPlatformBankController extends AbstractBackController {
    @Autowired
    private SPlatformBankService sPlatformBankService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("s:splatformbank:list")
    public R list(@RequestParam Map<String, Object> params){
        params.put("orgId",getOrgId());
        PageUtils page = sPlatformBankService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("s:splatformbank:info")
    public R info(@PathVariable("id") Integer id){
		SPlatformBankEntity sPlatformBank = sPlatformBankService.getById(id);

        return R.ok().put("sPlatformBank", sPlatformBank);
    }

    /**
     * 保存
     */
    @SysLog("新增银行卡")
    @RequestMapping("/save")
    @RequiresPermissions("s:splatformbank:save")
    public R save(@RequestBody SPlatformBankEntity sPlatformBank){
        sPlatformBank.setCreateTime(DateUtils.getCurrentTime());
        sPlatformBank.setCreateBy(getUserId());
        sPlatformBank.setDelFlag("0");
        sPlatformBank.setOrgId(getOrgId());
		sPlatformBankService.save(sPlatformBank);

        return R.ok();
    }

    /**
     * 修改
     */
    @SysLog("修改银行卡")
    @RequestMapping("/update")
    @RequiresPermissions("s:splatformbank:update")
    public R update(@RequestBody SPlatformBankEntity sPlatformBank){
        sPlatformBank.setUpdateBy(getUserId());
        sPlatformBank.setUpdateTime(DateUtils.getCurrentTime());
		sPlatformBankService.updateById(sPlatformBank);

        return R.ok();
    }

    /**
     * 删除
     */
    @SysLog("删除银行卡")
    @RequestMapping("/delete")
    @RequiresPermissions("s:splatformbank:delete")
    public R delete(@RequestBody Integer[] ids){
        for (Integer id:ids) {
            SPlatformBankEntity model = new SPlatformBankEntity();
            model.setDelFlag("1");
            sPlatformBankService.update(model,
                    new QueryWrapper<SPlatformBankEntity>().eq("id", id));
        }
        return R.ok();
    }
}
