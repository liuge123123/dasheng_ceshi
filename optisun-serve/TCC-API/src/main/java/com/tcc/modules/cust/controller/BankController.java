package com.tcc.modules.cust.controller;

import java.util.Arrays;
import java.util.Map;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.tcc.modules.base.AbstractBackController;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tcc.modules.cust.entity.BankEntity;
import com.tcc.modules.cust.service.BankService;
import com.tcc.common.utils.PageUtils;
import com.tcc.common.utils.R;



/**
 * 客户银行卡
 *
 * @author 
 * @email 
 * @date 2022-07-19 13:53:03
 */
@RestController
@RequestMapping("cust/bank")
public class BankController extends AbstractBackController {
    @Autowired
    private BankService bankService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("cust:bank:list")
    public R list(@RequestParam Map<String, Object> params){
        params.put("orgId",getOrgId());
        String teamList = Convert.toStr(params.get("team"));
        if(StrUtil.isNotBlank(teamList)){
            String[] checkList =  teamList.split(",");
            params.put("team",checkList);
        }
        else{
            return R.error("请选择人员");
        }
        params.put("key",StrUtil.cleanBlank(Convert.toStr(params.get("key"))));
        PageUtils page = bankService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("cust:bank:info")
    public R info(@PathVariable("id") Long id){
		BankEntity bank = bankService.getById(id);

        return R.ok().put("bank", bank);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("cust:bank:save")
    public R save(@RequestBody BankEntity bank){
		bankService.save(bank);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("cust:bank:update")
    public R update(@RequestBody BankEntity bank){
		bankService.updateById(bank);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("cust:bank:delete")
    public R delete(@RequestBody Long[] ids){
		bankService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
