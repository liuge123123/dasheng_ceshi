package com.tcc.modules.exercise.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class LuckyDrawForm {
    @ApiModelProperty(value = "客户Id")
    private Long custId;

}
