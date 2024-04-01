package com.tcc.modules.sys.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.tcc.modules.sys.dao.HomeDao;
import com.tcc.modules.sys.service.HomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HomeServiceImpl implements HomeService {

    @Autowired
    private HomeDao homeDao;

    /***
     * 获取汇总统计
     * @param orgId
     * @return
     */
    @Override
    public Map getHomeTotalInfo(long orgId, String[] team, String startTime, String endTime, String key) {
        Map<String, Object> params = new HashMap<>();
        params.put("team", team);
        if (StrUtil.isNotBlank(startTime)) {
            params.put("startTime", DateUtil.parse(startTime, DatePattern.NORM_DATETIME_FORMAT).getTime() / 1000);
        }
        if (StrUtil.isNotBlank(endTime)) {
            params.put("endTime", DateUtil.parse(endTime, DatePattern.NORM_DATETIME_FORMAT).getTime() / 1000);
        }

        Map<String, Object> result = new HashMap();
        switch (key) {
            case "totalRechargeMoney":
                //总充值金额
                BigDecimal totalRechargeMoney = homeDao.getRechargeMoney(params);
                result.put("totalRechargeMoney", totalRechargeMoney);
                break;
            case "totalGiveMoney":
                //总提现金额
                BigDecimal totalGiveMoney = homeDao.getWithdrawMoney(params);
                result.put("totalGiveMoney", totalGiveMoney);
                break;
            case "totalFreeMoney":
                //总赠送金额 1:系统赠送 ;3签到赠送；4首冲赠送
                BigDecimal totalFreeMoney = homeDao.getGiftMoney(params);
                result.put("totalFreeMoney", totalFreeMoney);
                break;
            case "totalDiffMoney":
                //总存取差
                BigDecimal totalRechargeMoney1 = homeDao.getRechargeMoney(params);
                BigDecimal totalGiveMoney1 = homeDao.getWithdrawMoney(params);
                BigDecimal totalDiffMoney = totalRechargeMoney1.subtract(totalGiveMoney1);
                result.put("totalDiffMoney", totalDiffMoney);
                break;
            case "totalAuditWithdraw":
                //待审核提现金额
                BigDecimal totalAuditWithdraw = homeDao.getAuditWithdraw(params);
                result.put("totalAuditWithdraw", totalAuditWithdraw);
                break;
            case "currentBB":
                //待审核提现金额
                BigDecimal totalRechargeMoney2 = homeDao.getRechargeMoney(params);
                BigDecimal totalGiveMoney2 = homeDao.getWithdrawMoney(params);
                BigDecimal currentBB = totalGiveMoney2.divide(totalRechargeMoney2, 2, BigDecimal.ROUND_HALF_UP);
                result.put("currentBB", currentBB.movePointRight(2));
                break;
            case "totalRechargeNum":
                //总充值人数
                int totalRechargeNum = homeDao.getRechargeCustNum(params);
                result.put("totalRechargeNum", totalRechargeNum);
                break;
            case "totalCommissionNum":
                //总提现人数
                int totalCommissionNum = homeDao.getWithDrawCustNum(params);
                result.put("totalCommissionNum", totalCommissionNum);
                break;
            case "totalRegisterNum":
                //总注册人数
                int totalRegisterNum = homeDao.getRegisterCustNum(params);
                result.put("totalRegisterNum", totalRegisterNum);
                break;
            case "totalRechargeOrderNum":
                //总充值笔数
                int totalRechargeOrderNum = homeDao.getRechargeNum(params);
                result.put("totalRechargeOrderNum", totalRechargeOrderNum);
                break;
        }
        return result;
    }

    /**
     * 获取今日汇总统计
     *
     * @return
     */
    @Override
    public Map getHomeToday(long orgId, String[] team, String key) {
        Date now = new Date();
        Map<String, Object> params = new HashMap<>();
        params.put("team", team);
        params.put("startTime", DateUtil.beginOfDay(now).getTime() / 1000);
        params.put("endTime", DateUtil.endOfDay(now).getTime() / 1000);

        Map result = new HashMap();

        switch (key) {
            case "todayRegisterNum":
                //今日新增注册人数
                int todayRegisterNum = homeDao.getRegisterCustNum(params);
                result.put("todayRegisterNum", todayRegisterNum);
                break;
            case "todayLoginNum":
                //今日登陆人数
                int todayLoginNum = homeDao.getLoginCustNum(params);
                result.put("todayLoginNum", todayLoginNum);
                break;
            case "todayProfitNum":
                //今日领取收益人数
                int todayProfitNum = homeDao.getProfitNum(params);
                result.put("todayProfitNum", todayProfitNum);
                break;
            case "todayFreeMoney":
                //今日赠送金额 1:系统赠送 ;3签到赠送；4首冲赠送
                BigDecimal todayFreeMoney = homeDao.getGiftMoney(params);
                result.put("todayFreeMoney", todayFreeMoney);
                break;
            case "todayRechargeMoney":
                //今日充值金额
                BigDecimal todayRechargeMoney = homeDao.getRechargeMoney(params);
                result.put("todayRechargeMoney", todayRechargeMoney);
                break;
            case "todayRechargeNum":
                //今日充值人数
                int todayRechargeNum = homeDao.getRechargeCustNum(params);
                result.put("todayRechargeNum", todayRechargeNum);
                break;
            case "todayFirstRechargeNum":
                //今日首充人数
                int todayFirstRechargeNum = homeDao.getRechargeFisrtCustNum(params);
                result.put("todayFirstRechargeNum", todayFirstRechargeNum);
                break;
            case "todayFirstRechargeMoney":
                //今日首充金额
                BigDecimal todayFirstRechargeMoney = homeDao.getRechargeFisrtMoney(params);
                result.put("todayFirstRechargeMoney", todayFirstRechargeMoney);
                break;
            case "todayOrderNum":
                //今日任务做单人数
                int todayOrderNum = homeDao.getOrderCustNum(params);
                result.put("todayOrderNum", todayOrderNum);
                break;
            case "todayTaskMoney":
                //今日任务返佣金额
                BigDecimal todayTaskMoney = homeDao.getOrderCommissionMoney(params);
                result.put("todayTaskMoney", todayTaskMoney);
                break;
            case "todayGiveMoney":
                //今日提线金额
                BigDecimal todayGiveMoney = homeDao.getWithdrawMoney(params);
                result.put("todayGiveMoney", todayGiveMoney);
                break;
            case "todayCommissionNum":
                //今日提现人数
                int todayCommissionNum = homeDao.getWithDrawCustNum(params);
                result.put("todayCommissionNum", todayCommissionNum);
                break;
            case "todayRechargeOrderNum":
                //今日充值笔数
                int todayRechargeOrderNum = homeDao.getRechargeNum(params);
                result.put("todayRechargeOrderNum", todayRechargeOrderNum);
                break;
        }
        return result;
    }

    /**
     * 获取昨日汇总统计
     *
     * @param orgId
     * @return
     */
    @Override
    public Map getHomeYesterday(long orgId, String[] team, String key) {
        Date now = DateUtil.offsetDay(new Date(), -1);
        Map<String, Object> params = new HashMap<>();
        params.put("team", team);
        params.put("startTime", DateUtil.beginOfDay(now).getTime() / 1000);
        params.put("endTime", DateUtil.endOfDay(now).getTime() / 1000);
        Map result = new HashMap();

        switch (key) {
            case "yesterdayRegisterNum":
                //昨日新增注册人数
                int yesterdayRegisterNum = homeDao.getRegisterCustNum(params);
                result.put("yesterdayRegisterNum", yesterdayRegisterNum);
                break;
            case "yesterdayRechargeMoney":
                //昨日充值金额
                BigDecimal yesterdayRechargeMoney = homeDao.getRechargeMoney(params);
                result.put("yesterdayRechargeMoney", yesterdayRechargeMoney);
                break;
            case "yesterdayOrderNum":
                //昨日任务做单人数
                int yesterdayOrderNum = homeDao.getOrderCustNum(params);
                result.put("yesterdayOrderNum", yesterdayOrderNum);
                break;
            case "yesterdayTaskMoney":
                //昨日任务返佣金额
                BigDecimal yesterdayTaskMoney = homeDao.getOrderCommissionMoney(params);
                result.put("yesterdayTaskMoney", yesterdayTaskMoney);
                //   int yesterdayVipOrderNum = homeDao.getHomeYesterdayVipOrderNum(orgId,team);
                break;
            case "yesterdayProfitNum":
                //昨日任务做单人数
                int yesterdayProfitNum = homeDao.getProfitNum(params);
                result.put("yesterdayProfitNum", yesterdayProfitNum);
                break;
            case "yesterdayGiveMoney":
                //昨日提现金额
                BigDecimal yesterdayGiveMoney = homeDao.getWithdrawMoney(params);
                result.put("yesterdayGiveMoney", yesterdayGiveMoney);
                break;
            case "yesterdayCommissionNum":
                //昨日提现人数
                int yesterdayCommissionNum = homeDao.getWithDrawCustNum(params);
                result.put("yesterdayCommissionNum", yesterdayCommissionNum);
                break;
            case "yesterdayRechargeOrderNum":
                //昨日充值笔数
                int yesterdayRechargeOrderNum = homeDao.getRechargeNum(params);
                result.put("yesterdayRechargeOrderNum", yesterdayRechargeOrderNum);
                break;
            case "yesterdayRechagreNum":
                // 昨日充值人数
                int yesterdayRechagreNum = homeDao.getRechargeCustNum(params);
                result.put("yesterdayRechagreNum", yesterdayRechagreNum);
                break;
            case "yesterdayRechargeFisrtCustNum":
                // 昨日首充人数
                int yesterdayRechargeFisrtCustNum = homeDao.getRechargeFisrtCustNum(params);
                result.put("yesterdayRechargeFisrtCustNum", yesterdayRechargeFisrtCustNum);
                break;
        }
        return result;
    }

    /**
     * 近7天注册情况统计
     *
     * @param orgId
     * @return
     */
    public List getRegisterList(long orgId, String[] team) {
        Date now = new Date();
        Map<String, Object> params = new HashMap<>();
        params.put("team", team);
        params.put("startTime", DateUtil.beginOfDay(DateUtil.offsetDay(now, -7)).getTime() / 1000);
        params.put("endTime", DateUtil.endOfDay(now).getTime() / 1000);
        return homeDao.getRegisterList(params);
    }
}
