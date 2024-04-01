package com.tcc.modules.cust.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CommissionCheckForm {

    @ApiModelProperty(value = "编号")
    private Long  id;

    @ApiModelProperty(value = "状态(0=待付,1=已打款,2=驳回)")
    private Integer status;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "通道ID")
    private String channelId;

}
