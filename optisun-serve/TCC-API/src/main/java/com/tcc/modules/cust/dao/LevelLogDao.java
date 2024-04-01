package com.tcc.modules.cust.dao;

import com.tcc.modules.cust.entity.LevelLogEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户升降级表
 * 
 * @author 
 * @email 
 * @date 2022-09-20 20:45:17
 */
@Mapper
public interface LevelLogDao extends BaseMapper<LevelLogEntity> {
	
}
