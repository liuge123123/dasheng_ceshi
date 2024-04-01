package com.tcc.modules.pay;

import lombok.Data;

@Data
public class PayResult {
    private Integer status;//0 失败 1成功
    private String payUrl;
    private String msg;
}
