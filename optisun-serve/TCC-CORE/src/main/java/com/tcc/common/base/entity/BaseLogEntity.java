package com.tcc.common.base.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class BaseLogEntity implements Serializable {
    private Long id;
    //用户名
    private String username;
    //用户操作
    private String operation;
    //请求方法
    private String method;
    //请求参数
    private String params;
    //执行时长(毫秒)
    private Long time;
    //IP地址
    private String ip;
    //创建时间
    private Integer createDate;
}
