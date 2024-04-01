package com.tcc.modules.t.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tcc.common.utils.PageUtils;
import com.tcc.modules.t.entity.TGoodsEntity;

import java.util.Map;

/**
 * 退单商品表
 *
 * @author
 * @email
 * @date 2022-07-19 09:17:41
 */
public interface TGoodsService extends IService<TGoodsEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

