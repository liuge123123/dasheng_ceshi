package com.tcc.modules.l.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tcc.common.utils.PageUtils;
import com.tcc.modules.l.entity.LGoodsEntity;

import java.util.Map;

/**
 * 
 *
 * @author 
 * @email 
 * @date 2022-08-30 13:52:53
 */
public interface  LGoodsService extends IService<LGoodsEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

