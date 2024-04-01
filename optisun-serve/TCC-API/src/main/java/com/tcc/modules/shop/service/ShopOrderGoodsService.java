package com.tcc.modules.shop.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tcc.common.utils.PageUtils;
import com.tcc.modules.shop.entity.ShopOrderGoodsEntity;

import java.util.Map;

/**
 * 订单商品表
 *
 * @author 
 * @email 
 * @date 2023-02-21 08:58:33
 */
public interface ShopOrderGoodsService extends IService<ShopOrderGoodsEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

