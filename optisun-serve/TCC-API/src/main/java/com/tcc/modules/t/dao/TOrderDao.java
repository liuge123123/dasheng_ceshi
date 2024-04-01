package com.tcc.modules.t.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tcc.modules.t.entity.TOrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 订单表
 *
 * @author
 * @email
 * @date 2022-07-19 09:17:41
 */
@Mapper
public interface TOrderDao extends BaseMapper<TOrderEntity> {
    List<Map<String,Object>> selectByCondition(IPage<Map<String,Object>> page, @Param("condition") Map<String, Object> params);
}

