package com.tcc.modules.s.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tcc.common.utils.PageUtils;
import com.tcc.modules.s.entity.SBankCateEntity;

import java.util.Map;

/**
 * 银行种类
 *
 * @author 
 * @email 
 * @date 2022-07-19 13:56:50
 */
public interface SBankCateService extends IService<SBankCateEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

