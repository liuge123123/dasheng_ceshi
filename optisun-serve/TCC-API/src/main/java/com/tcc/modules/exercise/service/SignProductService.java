package com.tcc.modules.exercise.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tcc.common.utils.PageUtils;
import com.tcc.modules.exercise.entity.SignProductEntity;

import java.util.Map;

/**
 * 积分兑换产品信息表
 *
 * @author 
 * @email 
 * @date 2022-10-05 11:40:10
 */
public interface SignProductService extends IService<SignProductEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

