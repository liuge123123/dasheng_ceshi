package com.tcc.modules.t.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tcc.common.utils.PageUtils;
import com.tcc.modules.t.entity.TOrderEntity;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * 订单表
 *
 * @author
 * @email
 * @date 2022-07-19 09:17:41
 */
public interface TOrderService extends IService<TOrderEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils selectByCondition(@Param("condition") Map<String, Object> params);

    void sale(Long orderId);

    void sale1(Long orderId);

    TOrderEntity buy(Long userId, Long goodsId);

}

