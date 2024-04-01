package com.tcc.modules.l.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tcc.common.utils.PageUtils;
import com.tcc.modules.l.entity.LOrderCountEntity;

import java.util.Map;

/**
 * 非福利区订单次数记录
 *
 * @author
 * @email
 * @date 2022-09-23 15:09:39
 */
public interface LOrderCountService extends IService<LOrderCountEntity> {

    PageUtils queryPage(Map<String, Object> params);

    Integer  getCountByLevel( Long custId, Long goodsLevel);
}

