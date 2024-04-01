package com.tcc.common.base.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class BaseUserTokenEntity implements Serializable {

    private Long userId;
    //token
    private String token;
    //过期时间
    private Date expireTime;
    //更新时间
    private Date updateTime;

}
