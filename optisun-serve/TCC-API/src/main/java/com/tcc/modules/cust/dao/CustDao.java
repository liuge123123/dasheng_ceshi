package com.tcc.modules.cust.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tcc.modules.cust.entity.CustEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 会员表
 * 
 * @author 
 * @email 
 * @date 2022-07-15 17:39:38
 */
@Mapper
public interface CustDao extends BaseMapper<CustEntity> {

    /***
     * 获取客户列表
     * @param page
     * @param params
     * @return
     */
    List getCustList(IPage<Object> page, @Param("condition") Map<String, Object> params);


    void updateMoney(@Param("params") Map<String, Object> params);

    BigDecimal getMoneyNow(Long custId);
}
