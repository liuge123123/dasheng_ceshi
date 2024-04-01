package com.tcc.modules.cust.controller;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.tcc.common.annotation.SysLog;
import com.tcc.common.utils.PageUtils;
import com.tcc.common.utils.R;
import com.tcc.modules.base.AbstractBackController;
import com.tcc.modules.cust.entity.CustScoreLogEntity;
import com.tcc.modules.cust.service.CustScoreLogService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;


/**
 * 客户积分变动表
 *
 * @author
 * @email
 * @date 2022-07-19 15:57:58
 */
@RestController
@RequestMapping("cust/custscorelog")
public class CustScoreLogController extends AbstractBackController {
    @Autowired
    private CustScoreLogService custScoreLogService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("cust:custscorelog:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = custScoreLogService.queryPage(params);

        return R.ok().put("page", page);
    }

    /**
     * 后台列表
     */
    @RequestMapping("/b_list")
    public R b_list(@RequestParam Map<String, Object> params) {
        params.put("orgId", getOrgId());
        String teamList = Convert.toStr(params.get("team"));
        if (StrUtil.isNotBlank(teamList)) {
            String[] checkList = teamList.split(",");
            params.put("team", checkList);
        } else {
            return R.error("请选择人员");
        }
        params.put("key", StrUtil.cleanBlank(Convert.toStr(params.get("key"))));
        PageUtils page = custScoreLogService.queryPageByBack(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("cust:custscorelog:info")
    public R info(@PathVariable("id") Long id) {
        CustScoreLogEntity custScoreLog = custScoreLogService.getById(id);

        return R.ok().put("custScoreLog", custScoreLog);
    }


    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("cust:custscorelog:update")
    public R update(@RequestBody CustScoreLogEntity custScoreLog) {
        custScoreLogService.updateById(custScoreLog);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("cust:custscorelog:delete")
    public R delete(@RequestBody Long[] ids) {
        custScoreLogService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    /**
     * 充值
     */
    @SysLog("系统赠送、扣减新增")
    @RequestMapping("/save")
    public R save(@RequestBody JSONObject params) {
        Long custId = params.getLong("custId");
        BigDecimal money = params.getBigDecimal("money");
        Integer type = params.getInt("type");
        String remark = "";
        int direct = 1;
        if (type == 1) {
            remark = "系统赠送充值金额为：" + money;
            direct = 1;
        } else if (type == 2) {
            remark = "系统扣减充值金额为：" + money;
            direct = 2;
        } else if (type == 12) {
            remark = "基金分红金额：" + money;
            direct = 1;
        }else if (type == 19) {
            remark = "基金扣减金额：" + money;
            direct = 2;
        }
        //客户
        custScoreLogService.scoreChange(custId, direct, money, type, remark);
        return R.ok();
    }
}
