package com.tcc.modules.exercise.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tcc.common.utils.PageUtils;
import com.tcc.modules.exercise.entity.TreasureOrderEntity;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 宝箱记录
 *
 * @author 
 * @email 
 * @date 2023-10-06 20:10:42
 */
public interface TreasureOrderService extends IService<TreasureOrderEntity> {

    PageUtils queryPage(Map<String, Object> params);

    Boolean setFixedPacket(Long treasureId, BigDecimal amount,Integer number);

    Boolean setRandomPacket(Long treasureId, BigDecimal amount,Integer number);
}

