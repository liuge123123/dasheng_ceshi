package com.tcc.modules.shop.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tcc.common.utils.PageUtils;
import com.tcc.modules.shop.entity.ShopGoodsEntity;

import java.util.Map;

/**
 * 商城商品表
 *
 * @author 
 * @email 
 * @date 2023-02-21 08:58:33
 */
public interface ShopGoodsService extends IService<ShopGoodsEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

