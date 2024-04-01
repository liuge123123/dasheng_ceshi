package com.tcc.modules.shop.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tcc.common.utils.PageUtils;
import com.tcc.modules.shop.entity.ShopCustCouponEntity;

import java.util.Map;

/**
 * 客户折扣券表
 *
 * @author 
 * @email 
 * @date 2023-02-21 08:58:33
 */
public interface ShopCustCouponService extends IService<ShopCustCouponEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

