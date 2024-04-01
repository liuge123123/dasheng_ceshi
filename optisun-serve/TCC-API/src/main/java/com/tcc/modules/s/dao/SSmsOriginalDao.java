package com.tcc.modules.s.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tcc.modules.s.entity.SSmsOriginalEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 原始短信表
 * 
 * @author 
 * @email 
 * @date 2022-08-29 14:00:49
 */
@Mapper
public interface SSmsOriginalDao extends BaseMapper<SSmsOriginalEntity> {

    List<Map<String, Object>> getList(IPage page, @Param("condition") Map<String, Object> condition);

    BigDecimal getSuccessCount(@Param("condition") Map<String, Object> params);

    BigDecimal getAuditCount(@Param("condition") Map<String, Object> params);

    List<Map<String, Object>> getList(@Param("condition") Map<String, Object> condition);
}
