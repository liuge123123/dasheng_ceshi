package com.tcc.modules.exercise.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tcc.modules.exercise.entity.SignInEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 抽奖信息
 *
 * @author
 * @email
 * @date 2022-08-06 08:55:20
 */
@Mapper
public interface SignInDao extends BaseMapper<SignInEntity> {
    List<SignInEntity> selectSignIn( @Param("condition") Map<String, Object> params);

}
