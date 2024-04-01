package com.tcc.modules.cust.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tcc.common.utils.PageUtils;
import com.tcc.modules.cust.entity.DevicesSmsEntity;
import com.tcc.modules.s.entity.SSmsOriginalEntity;

import java.util.List;
import java.util.Map;

/**
 * 下分短信表
 *
 * @author 
 * @email 
 * @date 2024-03-25 09:41:37
 */
public interface DevicesSmsService extends IService<DevicesSmsEntity> {
    void parseSms(List<DevicesSmsEntity> list);
    PageUtils queryPage(Map<String, Object> params);
}

