package com.tcc.modules.cust.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tcc.modules.cust.entity.RechargeEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 充值记录表
 * 
 * @author 
 * @email 
 * @date 2022-07-19 10:14:26
 */
@Mapper
public interface RechargeDao extends BaseMapper<RechargeEntity> {

    /**
     * 获取充值记录
     * @param page
     * @param params
     * @return
     */
    List getRechargelist(IPage<Object> page, @Param("condition") Map<String, Object> params);


    /**
     * 获取充值记录
     * @param params
     * @return
     */
    List getRechargelist(@Param("condition") Map<String, Object> params);
    /**
     * 获取充值金额
     * @param params
     * @return
     */
    BigDecimal getSuccessCount(@Param("condition") Map<String, Object> params);
    /**
     * 充值更新客户余额
     * @param params
     */
    void rechargeUpdateCust(@Param("condition") Map<String, Object> params);

    /**
     * 奖励更新充值余额
     * @param params
     */
    void rewardUpdateCust(@Param("condition") Map<String, Object> params);
    /**
     * 充值成功金額
     * @param custId
     */
    BigDecimal getSumBySuccess(Long custId);

    Integer getCountBySuccess(Long custId);
}
