package com.tcc.modules.app.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tcc.modules.cust.entity.BankEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


@Mapper
public interface AppAccountDao {

    /***
     * 获取客户列表
     * @param page
     * @param params
     * @return
     */
    List getAppCustList(IPage<Object> page, @Param("condition") Map<String, Object> params);
    /***
     * 获取充值客户列表
     * @param page
     * @param params
     * @return
     */
    List getAppCustListRecharge(IPage<Object> page, @Param("condition") Map<String, Object> params);

    /**
     * 获取成员信息
     * @param params
     * @return
     */
    Integer getTotalPerson(@Param("condition") Map<String, Object> params);

    /**
     * 获取今日佣金
     * @param params
     * @return
     */
    String getTodayMoney(@Param("condition") Map<String, Object> params);

    Map<String, Object> getTodayGOrderTotal(@Param("condition") Map<String, Object> params);

     /**
     * 获取团队层级下面的人数
     * @param params
     * @return
     */
    Integer getAppCustCount(@Param("condition") Map<String, Object> params);

    Integer getAppCustCountUser(@Param("condition") Map<String, Object> params);

    Integer getAppCustCountRecharge(@Param("condition") Map<String, Object> params);

    BigDecimal getAppCommission(@Param("condition") Map<String, Object> params);
    BigDecimal getTodayAppCommission(@Param("condition") Map<String, Object> params);
    /**
     * 获取随机产品
     * @param params
     * @return
     */
    Map<String, Object> getOneGGoodsModel(@Param("condition") Map<String, Object> params);

    /**
     * 更新客户的任务佣金
     * @param params
     */
    void updateCustTaskMoney(@Param("condition") Map<String, Object> params);

    /**
     *  更新客户的团队佣金
     * @param params
     */
    void updateCustTeamMoney(@Param("condition") Map<String, Object> params);


    /**
     * 获取解锁需上级用户的数量
     * @param params
     * @return
     */
   Integer  getAppUnlockCustCount(@Param("condition") Map<String, Object> params);

    /**
     * 扣减我的充值余额 更新过期时间
     * @param params
     */
   void updateCustLeftRechargeMoney(@Param("condition") Map<String, Object> params);

    /**
     * 更新邀请vip奖励
     * @param params
     */
    void updateCustRewardMoney(@Param("condition") Map<String, Object> params);

}
