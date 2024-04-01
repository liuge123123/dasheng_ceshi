package com.tcc.modules.l.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tcc.common.utils.PageUtils;
import com.tcc.modules.cust.entity.CustEntity;
import com.tcc.modules.l.entity.LOrderEntity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * 
 *
 * @author 
 * @email 
 * @date 2022-08-30 13:52:53
 */
public interface LOrderService extends IService<LOrderEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void buy(Long custId, Long goodsId);

    void buyGive(Long custId, Long goodsId);

    void sale(Long orderId);

    BigDecimal getTotalProfit(Date now, LOrderEntity item);

    BigDecimal getTodayProfit(Date now, LOrderEntity item);

    void quit(Long orderId, String cancelRemark, int lockDay);

    void quitDone(Long orderId);

    void orderReceive(Long custId, Long goodsId);

    void quitnew(Long orderId, String cancelRemark, int quitType);
}

