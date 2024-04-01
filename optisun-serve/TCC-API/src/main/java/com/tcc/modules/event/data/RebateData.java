package com.tcc.modules.event.data;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class RebateData implements Serializable {

    private Long custId;

    private BigDecimal money = BigDecimal.ZERO;

    private Long orderId;

    private String type;

}
