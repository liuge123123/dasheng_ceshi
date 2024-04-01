package com.tcc.modules.l.dao;

import com.tcc.modules.l.entity.OrderCountEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 非福利区订单次数记录
 * 
 * @author 
 * @email 
 * @date 2023-12-19 16:33:35
 */
@Mapper
public interface OrderCountDao extends BaseMapper<OrderCountEntity> {
	
}
