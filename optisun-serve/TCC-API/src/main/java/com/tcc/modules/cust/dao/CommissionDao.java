package com.tcc.modules.cust.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tcc.modules.cust.entity.CommissionEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 提现申请
 * 
 * @author 
 * @email 
 * @date 2022-07-19 20:16:56
 */
@Mapper
public interface CommissionDao extends BaseMapper<CommissionEntity> {


    /***
     * 获取提现申请
     * @param page
     * @param params
     * @return
     */
    List<Map<String, Object>> getCommissionList(IPage<Object> page, @Param("condition") Map<String, Object> params);
    /**
     * 获取审核金额
     * @param params
     * @return
     */
    BigDecimal getSuccessCount(@Param("condition") Map<String, Object> params);

    /**
     * 获取待体现金额
     * @param params
     * @return
     */
    BigDecimal getAuditCount(@Param("condition") Map<String, Object> params);

    /***
     * 获取提现申请
     * @param params
     * @return
     */
    List<Map<String, Object>> getCommissionList(@Param("condition") Map<String, Object> params);

    /**
     * 获取使用同号的用户
     * @param params
     * @return
     */
    List<Map<String, Object>> getCustListByAccount(@Param("condition") Map<String, Object> params);


    /**
     * 扣减佣金余额
     * @param params
     */
    void reduceLeftCommissionUpdateCust(@Param("condition") Map<String, Object> params);


    /**
     * 退回到佣金余额
     * @param params
     */
    void backLeftCommissionUpdateCust(@Param("condition") Map<String, Object> params);

    BigDecimal getSum(Long custId);

    BigDecimal getSumBySuccess(Long custId);

}
