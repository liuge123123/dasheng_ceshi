package com.tcc.modules.pay;

import lombok.Data;

@Data
public class TransferResult {
    private Integer status;//0 失败 1成功
    private String msg;
}
