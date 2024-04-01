package com.tcc.modules.cust.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tcc.modules.cust.entity.CustScoreLogEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 客户积分变动表
 * 
 * @author 
 * @email 
 * @date 2022-07-19 15:57:58
 */
@Mapper
public interface CustScoreLogDao extends BaseMapper<CustScoreLogEntity> {
    List getScorelist(IPage<Object> page, @Param("condition") Map<String, Object> params);

    BigDecimal custRewardTotal(Long custId);

    BigDecimal custRewardToday(@Param("custId") Long custId,@Param("type") Integer type,@Param("startTime") Long start,@Param("endTime") Long end);
}
