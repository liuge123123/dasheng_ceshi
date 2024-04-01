package com.tcc.modules.l.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tcc.common.utils.PageUtils;
import com.tcc.modules.l.entity.OrderCountEntity;

import java.util.Map;

/**
 * 非福利区订单次数记录
 *
 * @author 
 * @email 
 * @date 2023-12-19 16:33:35
 */
public interface OrderCountService extends IService<OrderCountEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

