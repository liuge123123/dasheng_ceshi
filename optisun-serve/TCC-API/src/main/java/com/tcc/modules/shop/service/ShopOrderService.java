package com.tcc.modules.shop.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tcc.common.utils.PageUtils;
import com.tcc.modules.shop.entity.ShopOrderEntity;

import java.util.Map;

/**
 * 商城订单表
 *
 * @author 
 * @email 
 * @date 2023-02-21 08:58:33
 */
public interface ShopOrderService extends IService<ShopOrderEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void quitOrder(Long orderId, Long userId);
}

