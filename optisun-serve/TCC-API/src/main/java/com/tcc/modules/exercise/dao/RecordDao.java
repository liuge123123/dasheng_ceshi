package com.tcc.modules.exercise.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tcc.modules.exercise.entity.RecordEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * 奖品信息
 *
 * @author
 * @email
 * @date 2022-08-05 08:55:20
 */
@Mapper
public interface RecordDao extends BaseMapper<RecordEntity> {

    Map<String,Object> getDetailById(@Param("condition") Map<String, Object> params);

}
