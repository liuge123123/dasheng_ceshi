package com.tcc.modules.g.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tcc.common.utils.PageUtils;
import com.tcc.modules.g.entity.GOrderEntity;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * 订单表
 *
 * @author
 * @email
 * @date 2022-07-21 17:55:20
 */
public interface GOrderService extends IService<GOrderEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils selectByCondition(@Param("condition") Map<String, Object> params);

    GOrderEntity buy(Long userId, Long goodsId, int num, String orderNo);

}

