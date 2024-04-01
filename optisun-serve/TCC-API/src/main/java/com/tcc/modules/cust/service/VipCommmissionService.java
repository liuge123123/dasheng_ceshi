package com.tcc.modules.cust.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tcc.common.utils.PageUtils;
import com.tcc.modules.cust.entity.VipCommmissionEntity;

import java.util.Map;

/**
 * 
 *
 * @author 
 * @email 
 * @date 2022-09-20 20:45:17
 */
public interface VipCommmissionService extends IService<VipCommmissionEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

