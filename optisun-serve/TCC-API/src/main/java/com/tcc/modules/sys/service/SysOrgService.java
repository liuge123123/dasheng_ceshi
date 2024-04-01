package com.tcc.modules.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tcc.common.utils.PageUtils;
import com.tcc.modules.sys.entity.SysOrgEntity;

import java.util.Map;

/**
 * 
 *
 * @author 
 * @email 
 * @date 2021-04-19 11:25:56
 */
public interface SysOrgService extends IService<SysOrgEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

