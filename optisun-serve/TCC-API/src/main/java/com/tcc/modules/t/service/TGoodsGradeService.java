package com.tcc.modules.t.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tcc.common.utils.PageUtils;
import com.tcc.modules.cust.entity.CustEntity;
import com.tcc.modules.t.entity.TGoodsGradeEntity;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 商品等级表
 *
 * @author
 * @email
 * @date 2022-07-19 09:17:41
 */
public interface TGoodsGradeService extends IService<TGoodsGradeEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void updateLevel(CustEntity cust, BigDecimal totalMoney);
}

