package com.tcc.modules.basic.dao;

import com.tcc.modules.basic.entity.ProductEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品表
 * 
 * @author 
 * @email 
 * @date 2022-05-30 16:54:43
 */
@Mapper
public interface ProductDao extends BaseMapper<ProductEntity> {
	
}
