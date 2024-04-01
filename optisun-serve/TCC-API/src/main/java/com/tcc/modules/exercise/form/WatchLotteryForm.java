package com.tcc.modules.exercise.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class WatchLotteryForm {
    @ApiModelProperty(value = "客户Id")
    private String custId;

    @ApiModelProperty(value = "Id")
    private Integer id;

    @ApiModelProperty(value = "中奖金额")
    private String prize;

    @ApiModelProperty(value = "中奖率")
    private Integer v;

    public WatchLotteryForm(int i, String s, int i1) {
    }

}
