package com.tcc.modules.event.data;

import com.tcc.modules.event.EventConstant;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class BalanceChangeData implements Serializable {

    private Long custId;

    private BigDecimal changeMoney = BigDecimal.ZERO;

    private BigDecimal frozenMoney = BigDecimal.ZERO;

    private EventConstant.BalanceChangeType type;

    private EventConstant.Direct direct;


     /**
     * 来源单据id
     */
    private Long sourceId;

}
