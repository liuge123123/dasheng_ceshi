package com.tcc.modules.cust.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tcc.common.utils.PageUtils;
import com.tcc.modules.cust.entity.DevicesEntity;

import java.util.Map;

/**
 * 下分设备列表
 *
 * @author 
 * @email 
 * @date 2024-03-20 14:51:59
 */
public interface DevicesService extends IService<DevicesEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

