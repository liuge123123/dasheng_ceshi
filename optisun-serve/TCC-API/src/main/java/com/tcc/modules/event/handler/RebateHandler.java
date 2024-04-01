package com.tcc.modules.event.handler;

import cn.hutool.json.JSONUtil;
import com.tcc.modules.cust.entity.CustEntity;
import com.tcc.modules.cust.service.CustScoreLogService;
import com.tcc.modules.cust.service.CustService;
import com.tcc.modules.event.*;
import com.tcc.modules.event.data.BalanceChangeData;
import com.tcc.modules.sys.service.SysConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Component
public class RebateHandler implements EventHandler {

    @Autowired
    private CustService custService;

    @Autowired
    private SysConfigService sysConfigService;

    @Autowired
    private EventPublisher eventPublisher;

    @Autowired
    private CustScoreLogService custScoreLogService;

    @Transactional
    @Override
    public <T> void handler(Event<T> event) {
        log.debug(JSONUtil.toJsonStr(event));
        // 三级分销计算上级返佣
//        RebateData data = (RebateData) event.getData();
//        CustEntity cust = custService.getById(data.getCustId());
//        if(StrUtil.isNotBlank(cust.getParentId()) && !"0".equals(cust.getParentId())) {
//            // 返佣比例
//            String configStr = sysConfigService.getValue(cust.getOrgId(), REBATE_CONFIG_KEY);
//            if (StrUtil.isNotBlank(configStr)) {
//                JSONObject config = JSONUtil.parseObj(configStr);
//                boolean open = config.getBool("open", false);
//                if (open) {
//                    BigDecimal first = config.getBigDecimal("first");
//                    BigDecimal second = config.getBigDecimal("second");
//                    BigDecimal third = config.getBigDecimal("third");
//                    // 一级返佣
//                    CustEntity fistCust = custService.getById(cust.getParentId());
//                    rebate(fistCust, first, data.getMoney(), EventConstant.BalanceChangeType.FIRST_REEBATE,data.getOrderId());
//                    if(StrUtil.isNotBlank(fistCust.getParentId()) && !"0".equals(fistCust.getParentId())){
//                        // 二级返佣
//                        CustEntity secondCust = custService.getById(fistCust.getParentId());
//                        rebate(secondCust, second, data.getMoney(), EventConstant.BalanceChangeType.SECOND_REEBATE,data.getOrderId());
//                        if(StrUtil.isNotBlank(secondCust.getParentId()) && !"0".equals(secondCust.getParentId())){
//                            // 三级级返佣
//                            CustEntity thirdCust = custService.getById(secondCust.getParentId());
//                            rebate(thirdCust, third, data.getMoney(), EventConstant.BalanceChangeType.THIRD_REEBATE,data.getOrderId());
//                        }
//                    }
//                }
//            }
//        }
    }


    private void rebate(CustEntity cust, BigDecimal rate, BigDecimal money, EventConstant.BalanceChangeType balanceChangeType, Long orderId) {
        BigDecimal profit = money.multiply(rate).divide(new BigDecimal(100));
        if (profit.compareTo(new BigDecimal(0)) > 0) {
            // 用固话账户余额变动
            BalanceChangeData balanceChangeData = new BalanceChangeData();
            balanceChangeData.setCustId(cust.getCustId());
            balanceChangeData.setType(balanceChangeType);
            balanceChangeData.setDirect(EventConstant.Direct.ADD);
            balanceChangeData.setChangeMoney(profit);
            balanceChangeData.setFrozenMoney(new BigDecimal(0));
            balanceChangeData.setSourceId(orderId);
            eventPublisher.publish(new Event("rebate", EventType.balanceChange, balanceChangeData));
            // custScoreLogService.change(cust, new Event("rebate", EventType.balanceChange, balanceChangeData));
        }
    }

}
