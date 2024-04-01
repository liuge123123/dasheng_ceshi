package com.tcc.modules.exercise.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tcc.common.utils.PageUtils;
import com.tcc.modules.exercise.entity.TreasureEntity;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 宝箱派送
 *
 * @author 
 * @email 
 * @date 2023-10-06 11:15:29
 */
public interface TreasureService extends IService<TreasureEntity> {

    PageUtils queryPage(Map<String, Object> params);

    BigDecimal recive(Long custId, String code);
}

