package com.tcc.modules.sys.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Mapper
public interface HomeDao {

    /**
     * 近7天注册情况
     * @param params
     * @return
     */
    List<Map> getRegisterList(Map params);



    /*=======================================================================================*/

    /**
     * 获取充值金额
     * @param params
     * @return
     */
    BigDecimal getRechargeMoney(Map params);

    /**
     * 获取充值笔数
     * @param params
     * @return
     */
    int getRechargeNum(Map params);

    /**
     * 获取充值客户数
     * @param params
     * @return
     */
    int getRechargeCustNum(Map params);

    /**
     * 获取首充客户数
     * @param params
     * @return
     */
    int getRechargeFisrtCustNum(Map params);

    /**
     * 获取首充金额
     * @param params
     * @return
     */
    BigDecimal getRechargeFisrtMoney(Map params);

    /**
     * 获取做单任务佣金
     * @param params
     * @return
     */
    BigDecimal getOrderCommissionMoney(Map params);

    /**
     * 获取做单客户数
     * @param params
     * @return
     */
    int getOrderCustNum(Map params);

    /**
     * 获取提现金额
     * @param params
     * @return
     */
    BigDecimal getWithdrawMoney(Map params);

    /**
     * 获取待审核提现金额
     * @param params
     * @return
     */
    BigDecimal getAuditWithdraw(Map params);

    /**
     * 获取提现客户数
     * @param params
     * @return
     */
    int getWithDrawCustNum(Map params);

    /**
     * 获取赠送金额
     * @param params
     * @return
     */
    BigDecimal getGiftMoney(Map params);

    /**
     * 获取注册人数
     * @param params
     * @return
     */
    int getRegisterCustNum(Map params);

    /**
     * 获取登录客户数
     * @param params
     * @return
     */
    int getLoginCustNum(Map params);

    /**
     * 领取收益人数
     * @param params
     * @return
     */
    int getProfitNum(Map params);

}
