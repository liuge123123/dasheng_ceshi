package com.tcc.modules.cust.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class RechargeCheckForm {

    @ApiModelProperty(value = "状态(0=待付,1=已付,2=驳回)")
    private Integer status;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "充值id")
    private Long rechargeId;

}
