package com.tcc.modules.exercise.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tcc.common.utils.PageUtils;
import com.tcc.modules.exercise.entity.LuckyDrawEntity;
import com.tcc.modules.exercise.entity.PrizeEntity;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * 抽奖信息
 *
 * @author
 * @email
 * @date 2022-08-04 17:55:20
 */
public interface LuckyDrawService extends IService<LuckyDrawEntity> {

    Map<String,Object> selectByCondition(@Param("condition") Map<String, Object> params);

    PageUtils selectByConditions(@Param("condition") Map<String, Object> params);

    PrizeEntity excute(Long actId, Long custId, String custName);
}

