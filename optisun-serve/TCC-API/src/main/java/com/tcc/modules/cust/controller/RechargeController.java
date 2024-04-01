package com.tcc.modules.cust.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tcc.common.exception.WTDPException;
import com.tcc.common.annotation.SysLog;
import com.tcc.common.utils.DateUtils;
import com.tcc.common.utils.ResultCode;
import com.tcc.common.validator.ValidatorUtils;
import com.tcc.modules.app.annotation.Login;
import com.tcc.modules.base.AbstractBackController;
import com.tcc.modules.cust.entity.CustEntity;
import com.tcc.modules.cust.form.RechargeCheckForm;
import com.tcc.modules.cust.form.UpdateChargeForm;
import com.tcc.modules.cust.service.CustService;
import com.tcc.modules.s.entity.SPlatformBankEntity;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.tcc.modules.cust.entity.RechargeEntity;
import com.tcc.modules.cust.service.RechargeService;
import com.tcc.common.utils.PageUtils;
import com.tcc.common.utils.R;

import javax.servlet.http.HttpServletResponse;


/**
 * 充值记录表
 *
 * @author 
 * @email 
 * @date 2022-07-19 10:14:26
 */
@RestController
@RequestMapping("cust/recharge")
public class RechargeController  extends AbstractBackController {
    @Autowired
    private RechargeService rechargeService;
    @Autowired private CustService custService;

    /**
     * 列表
     */
    @RequestMapping("/list")
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
//        if(params.containsKey("createTime")){
//            params.put("createTime", DateUtil.formatDate(Convert.toDate(params.get("createTime"))));
//        }
//        if(params.containsKey("updateTime")){
//            params.put("updateTime", DateUtil.formatDate(Convert.toDate(params.get("updateTime"))));
//        }
        params.put("key",StrUtil.cleanBlank(Convert.toStr(params.get("key"))));
        PageUtils page = rechargeService.getRechargelist(params);
        return R.ok().put("page", page);
    }
    /**
     * 统计
     */
    @RequestMapping("/count")
    public R count(@RequestParam Map<String, Object> params){
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

        return R.ok().put("data", rechargeService.getCount(params));
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{rechargeId}")
    @RequiresPermissions("cust:recharge:info")
    public R info(@PathVariable("rechargeId") Long rechargeId){
		RechargeEntity recharge = rechargeService.getById(rechargeId);

        return R.ok().put("recharge", recharge);
    }
    /**
     * 修改
     */
    @SysLog("充值更新")
    @RequestMapping("/update")
    @RequiresPermissions("cust:recharge:update")
    public R update(@RequestBody RechargeEntity recharge){
		rechargeService.updateById(recharge);
        return R.ok();
    }

    /**
     * 删除
     */
    @SysLog("充值删除")
    @RequestMapping("/delete")
    @RequiresPermissions("cust:recharge:delete")
    public R delete(@RequestBody Long[] rechargeIds){
		rechargeService.removeByIds(Arrays.asList(rechargeIds));

        return R.ok();
    }

    /**
     * 审核
     * @param form
     * @return
     */
    @SysLog("充值审核")
    @PostMapping("/check")
    public R check(@RequestBody RechargeCheckForm form){
        ValidatorUtils.validateEntity(form);
        form.setRemark("充值成功");
        rechargeService.check(form);
        return R.ok();
    }

    /**
     * 反审核驳回
     * @param form
     * @return
     */
    @SysLog("充值驳回")
    @PostMapping("/reject")
    public R reject(@RequestBody RechargeCheckForm form){
        ValidatorUtils.validateEntity(form);
        rechargeService.reject(form);
        return R.ok();
    }

    /**
     * 修改充值订单
     * @param form
     * @return
     */
    @SysLog("更新充值记录")
    @PostMapping("/updateRecharge")
    public R updateRecharge(@RequestBody UpdateChargeForm form){
        ValidatorUtils.validateEntity(form);
        //判断是否是待付状态
       RechargeEntity model = rechargeService.getById(form.getRechargeId());
       if(model.getStatus()!=0){
           return R.error("该订单已支付或驳回");
       }
        model.setMoneyFront(form.getMoneyFront());
        model.setAmount(form.getAmount());
        model.setTransid(form.getTransid());
        rechargeService.updateById(model);
        return R.ok();
    }
    /***
     * 导出到excel
     * @return
     */
    @RequestMapping("/downExcel")
    public void   downSmsExcel(HttpServletResponse response, @RequestParam Map<String,Object> params) throws IOException {
        params.put("orgId",getOrgId());
        String teamList = Convert.toStr(params.get("team"));
        if(StrUtil.isNotBlank(teamList)){
            String[] checkList =  teamList.split(",");
            params.put("team",checkList);
        }
        else{
            throw new WTDPException("请选择人员");
        }
//        if(params.containsKey("createTime")){
//            params.put("createTime", DateUtil.formatDate(Convert.toDate(params.get("createTime"))));
//        }
//        if(params.containsKey("updateTime")){
//            params.put("updateTime", DateUtil.formatDate(Convert.toDate(params.get("updateTime"))));
//        }
        params.put("key",StrUtil.cleanBlank(Convert.toStr(params.get("key"))));
        rechargeService.downExcel(response,params);
    }
}
