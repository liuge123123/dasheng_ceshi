package com.tcc.modules.exercise.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tcc.modules.exercise.entity.LuckyDrawEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 抽奖信息
 *
 * @author
 * @email
 * @date 2022-08-04 17:55:20
 */
@Mapper
public interface LuckyDrawDao extends BaseMapper<LuckyDrawEntity> {

    Map<String,Object> selectByCondition(@Param("condition") Map<String, Object> params);

    List<Map<String,Object>> selectByConditions(IPage<Map<String,Object>> page, @Param("condition") Map<String, Object> params);

}
