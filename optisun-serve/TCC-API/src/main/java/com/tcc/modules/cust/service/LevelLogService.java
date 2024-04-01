package com.tcc.modules.cust.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tcc.common.utils.PageUtils;
import com.tcc.modules.cust.entity.LevelLogEntity;

import java.util.Map;

/**
 * 用户升降级表
 *
 * @author 
 * @email 
 * @date 2022-09-20 20:45:17
 */
public interface LevelLogService extends IService<LevelLogEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

