package com.tcc.modules.cust.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tcc.common.utils.PageUtils;
import com.tcc.modules.cust.entity.BankEntity;

import java.util.Map;

/**
 * 客户银行卡
 *
 * @author 
 * @email 
 * @date 2022-07-19 13:53:03
 */
public interface BankService extends IService<BankEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

