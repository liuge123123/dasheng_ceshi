package com.tcc.modules.basic.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tcc.common.utils.PageUtils;
import com.tcc.modules.basic.entity.ProductEntity;

import java.util.Map;

/**
 * 商品表
 *
 * @author 
 * @email 
 * @date 2022-05-30 16:54:43
 */
public interface ProductService extends IService<ProductEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

