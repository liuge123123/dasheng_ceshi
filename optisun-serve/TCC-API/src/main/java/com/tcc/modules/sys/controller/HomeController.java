package com.tcc.modules.sys.controller;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.tcc.common.utils.R;
import com.tcc.modules.base.AbstractBackController;
import com.tcc.modules.cust.service.CustScoreLogService;
import com.tcc.modules.cust.service.CustService;
import com.tcc.modules.cust.service.RechargeService;
import com.tcc.modules.sys.service.HomeService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/sys/home")
@Api("首页接口")
public class HomeController extends AbstractBackController {

    @Autowired
    private CustService custService;
    @Autowired
    private CustScoreLogService custScoreLogService;
    @Autowired
    private RechargeService rechargeService;
    @Autowired
    private HomeService homeService;


    /**
     * 获取总汇总统计
     *
     * @return
     */
    @GetMapping("/getHomeTotalInfo")
    public R getHomeTotalInfo(@RequestParam Map<String, Object> params) {
        params.put("orgId", getOrgId());
        String teamList = Convert.toStr(params.get("team"));
        String[] checkList = new String[]{};
        if (StrUtil.isNotBlank(teamList)) {
            checkList = teamList.split(",");
            params.put("team", checkList);
        } else {
            return R.error("请选择人员");
        }
        String startTime = Convert.toStr(params.get("startTime"), "");
        String endtime = Convert.toStr(params.get("endTime"), "");
        String key = MapUtil.getStr(params, "key");
        return R.ok().put("data", homeService.getHomeTotalInfo(getOrgId(), checkList, startTime, endtime, key));
    }

    /**
     * 获取今日汇总统计
     *
     * @return
     */
    @GetMapping("/getHomeToday")
    public R getHomeToday(@RequestParam Map<String, Object> params) {
        String teamList = Convert.toStr(params.get("team"));
        String[] checkList = new String[]{};
        if (StrUtil.isNotBlank(teamList)) {
            checkList = teamList.split(",");
            params.put("team", checkList);
        } else {
            return R.error("请选择人员");
        }
        String key = MapUtil.getStr(params, "key");
        return R.ok().put("data", homeService.getHomeToday(getOrgId(), checkList, key));
    }

    /**
     * 获取昨日汇总统计
     *
     * @return
     */
    @GetMapping("/getHomeYesterday")
    public R getHomeYesterday(@RequestParam Map<String, Object> params) {
        String teamList = Convert.toStr(params.get("team"));
        String[] checkList = new String[]{};
        if (StrUtil.isNotBlank(teamList)) {
            checkList = teamList.split(",");
            params.put("team", checkList);
        } else {
            return R.error("请选择人员");
        }
        String key = MapUtil.getStr(params, "key");
        return R.ok().put("data", homeService.getHomeYesterday(getOrgId(), checkList, key));
    }

    /**
     * 近7天注册情况统计
     *
     * @return
     */
    @GetMapping("/getRegisterList")
    public R getRegisterList(@RequestParam Map<String, Object> params) {
        String teamList = Convert.toStr(params.get("team"));
        String[] checkList = new String[]{};
        if (StrUtil.isNotBlank(teamList)) {
            checkList = teamList.split(",");
            params.put("team", checkList);
        } else {
            return R.error("请选择人员");
        }
        return R.ok().put("data", homeService.getRegisterList(getOrgId(), checkList));
    }
}
