package com.tcc.modules.l.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tcc.common.utils.PageUtils;
import com.tcc.modules.l.entity.LOrderCommmissionEntity;

import java.util.Map;

/**
 * 理财佣金记录
 *
 * @author 
 * @email 
 * @date 2022-09-23 15:09:39
 */
public interface LOrderCommmissionService extends IService<LOrderCommmissionEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

