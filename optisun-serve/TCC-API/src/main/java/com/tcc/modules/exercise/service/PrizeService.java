package com.tcc.modules.exercise.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tcc.common.utils.PageUtils;
import com.tcc.modules.exercise.entity.PrizeEntity;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * 奖品信息
 *
 * @author
 * @email
 * @date 2022-08-05 08:55:20
 */
public interface PrizeService extends IService<PrizeEntity> {

    PageUtils selectByCondition(@Param("condition") Map<String, Object> params);


}

