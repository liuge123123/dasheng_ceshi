package com.tcc.modules.app.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 充值自动审核接口
 */
@Data
public class RechargeCheckForm implements Serializable {

    @NotBlank(message = "交易ID不能为空")
    private String transId;

    @NotNull(message = "充值金额不能为空")
    private BigDecimal money;

    private String bankType;

}
