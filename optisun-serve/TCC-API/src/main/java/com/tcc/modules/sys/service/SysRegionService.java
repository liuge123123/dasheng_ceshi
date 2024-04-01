package com.tcc.modules.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tcc.common.utils.PageUtils;
import com.tcc.modules.sys.entity.SysRegionEntity;

import java.util.Map;

/**
 * 
 *
 * @author 
 * @email 
 * @date 2020-12-15 12:54:50
 */
public interface SysRegionService extends IService<SysRegionEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

