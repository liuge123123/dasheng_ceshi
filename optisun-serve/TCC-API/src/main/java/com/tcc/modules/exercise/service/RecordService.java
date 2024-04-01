package com.tcc.modules.exercise.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tcc.common.utils.PageUtils;
import com.tcc.modules.exercise.entity.RecordEntity;

import java.util.Map;

/**
 * 抽奖记录
 *
 * @author
 * @email
 * @date 2022-08-05 08:55:20
 */
public interface RecordService extends IService<RecordEntity> {

    PageUtils queryPage(Map<String, Object> params);

    Map<String,Object> getDetailById(Map<String, Object> params);
}

