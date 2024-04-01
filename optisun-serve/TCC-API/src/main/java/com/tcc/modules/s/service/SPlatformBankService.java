package com.tcc.modules.s.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tcc.common.utils.PageUtils;
import com.tcc.modules.s.entity.SPlatformBankEntity;

import java.util.Map;

/**
 * 平台银行
 *
 * @author 
 * @email 
 * @date 2022-07-19 16:34:54
 */
public interface SPlatformBankService extends IService<SPlatformBankEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

