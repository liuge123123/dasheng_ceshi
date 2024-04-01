package com.tcc.modules.s.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tcc.common.utils.PageUtils;
import com.tcc.modules.s.entity.SSmsEntity;

import java.util.Map;

/**
 * 短信表
 *
 */
public interface SSmsService extends IService<SSmsEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void dealAudit(SSmsEntity entity);
}

