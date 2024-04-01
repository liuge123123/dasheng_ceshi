package com.tcc.modules.cust.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.tcc.common.annotation.SysLog;
import com.tcc.common.exception.WTDPException;
import com.tcc.common.utils.SpringContextUtils;
import com.tcc.common.utils.StringUtils;
import com.tcc.common.validator.ValidatorUtils;
import com.tcc.modules.base.AbstractBackController;
import com.tcc.modules.cust.entity.CustEntity;
import com.tcc.modules.cust.entity.DevicesEntity;
import com.tcc.modules.cust.form.CommissionCheckForm;
import com.tcc.modules.cust.form.RechargeCheckForm;
import com.tcc.modules.cust.service.CustService;
import com.tcc.modules.cust.service.DevicesService;
import com.tcc.modules.exercise.entity.TreasureEntity;
import com.tcc.modules.l.entity.LOrderEntity;
import com.tcc.modules.pay.IPaymentService;
import com.tcc.modules.pay.PayResult;
import com.tcc.modules.pay.TransferResult;
import com.tcc.modules.s.entity.SBankCateEntity;
import com.tcc.modules.s.entity.SPlatformBankEntity;
import com.tcc.modules.s.service.SBankCateService;
import com.tcc.modules.s.service.SPlatformBankService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.tcc.modules.cust.entity.CommissionEntity;
import com.tcc.modules.cust.service.CommissionService;
import com.tcc.common.utils.PageUtils;
import com.tcc.common.utils.R;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * 提现申请
 *
 * @author 
 * @email 
 * @date 2022-07-19 20:16:56
 */
@RestController
@RequestMapping("cust/commission")
@Slf4j
public class CommissionController extends AbstractBackController {
    @Autowired private CommissionService commissionService;

    @Autowired private DevicesService devicesService;
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
        PageUtils page = commissionService.getCommissionList(params);

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
//        if(params.containsKey("createTime")){
//            params.put("createTime", DateUtil.formatDate(Convert.toDate(params.get("createTime"))));
//        }
//        if(params.containsKey("updateTime")){
//            params.put("updateTime", DateUtil.formatDate(Convert.toDate(params.get("updateTime"))));
//        }
        params.put("key",StrUtil.cleanBlank(Convert.toStr(params.get("key"))));

        return R.ok().put("data", commissionService.getCount(params));
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("cust:commission:info")
    public R info(@PathVariable("id") Long id){
		CommissionEntity commission = commissionService.getById(id);

        return R.ok().put("commission", commission);
    }

    /**
     * 保存
     */
    @SysLog("提现新增")
    @RequestMapping("/save")
    @RequiresPermissions("cust:commission:save")
    public R save(@RequestBody CommissionEntity commission){
		commissionService.save(commission);

        return R.ok();
    }

    /**
     * 修改
     */
    @SysLog("提现更新")
    @RequestMapping("/update")
    @RequiresPermissions("cust:commission:update")
    public R update(@RequestBody CommissionEntity commission){
		commissionService.updateById(commission);

        return R.ok();
    }

    /**
     * 删除
     */
    @SysLog("提现删除")
    @RequestMapping("/delete")
    @RequiresPermissions("cust:commission:delete")
    public R delete(@RequestBody Long[] ids){
		commissionService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }
    /**
     * 审核
     * @param form
     * @return
     */
    @SysLog("提现审核")
    @PostMapping("/check")
    public R check(@RequestBody CommissionCheckForm form){
        ValidatorUtils.validateEntity(form);
        commissionService.check(form,getUserId());
        return R.ok();
    }

    @SysLog("提现自动审核")
    @PostMapping("/autocheck")
    public R autocheck(@RequestBody CommissionCheckForm form){
        ValidatorUtils.validateEntity(form);
        CommissionEntity commission = commissionService.getById(form.getId());
        if(commission.getStatus()!=0){
            throw new WTDPException("订单已经审核");
        }
        DevicesEntity devicesEntity = devicesService.getById(form.getChannelId());
        commissionService.update(new LambdaUpdateWrapper<CommissionEntity>()
                .eq(CommissionEntity::getId, form.getId())
                .set(CommissionEntity::getStatus, 3)
                .set(CommissionEntity::getChannelId,form.getChannelId())
                .set(CommissionEntity::getChannelName,devicesEntity.getMobile())
        );
        return R.ok();
    }

    @SysLog("提现收到三方通知")
    @ResponseBody
    @RequestMapping(value= {"/transfernotify/{ifCode}", "/transfernotify/{ifCode}/{payOrderId}"})
    public String transfernotify(HttpServletRequest request, @PathVariable("ifCode") String ifCode, @PathVariable(value = "payOrderId", required = false) String urlOrderId){
        String payOrderId = null;
        String logPrefix = "进入[" +ifCode+ "]代付回调：urlOrderId：["+ StringUtils.defaultIfEmpty(urlOrderId, "") + "] ";
        log.info("===== {} =====" , logPrefix);
        // 参数有误
        if(StringUtils.isEmpty(ifCode)){
            return "ifCode is empty";
        }
        IPaymentService paymentService = SpringContextUtils.getBean(ifCode.toLowerCase() + "PaymentService", IPaymentService.class);
        return paymentService.transfernotify(request,urlOrderId, ifCode);
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
        commissionService.downExcel(response,params);
    }

    /**
     * 查询下级
     * @param params
     * @return
     */
    @GetMapping("/getCustListByAccount")
    public R getCustListByAccount(@RequestParam Map<String, Object> params) {
        List<Map<String, Object>> list = commissionService.getCustListByAccount(params);
        return R.ok().putData(list);
    }

}
