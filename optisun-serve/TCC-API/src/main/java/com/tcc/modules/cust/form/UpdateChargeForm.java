package com.tcc.modules.cust.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateChargeForm {
    @ApiModelProperty(value = "充值id")
    private Long rechargeId;

    @ApiModelProperty(value = "充值前金额")
    private BigDecimal moneyFront;

    @ApiModelProperty(value = "充值金额")
    private BigDecimal amount;

    @ApiModelProperty(value = "交易id")
    private String transid;
}
