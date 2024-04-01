package com.tcc.modules.shop.dao;

import com.tcc.modules.shop.entity.ShopOrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商城订单表
 * 
 * @author 
 * @email 
 * @date 2023-02-21 08:58:33
 */
@Mapper
public interface ShopOrderDao extends BaseMapper<ShopOrderEntity> {
	
}
