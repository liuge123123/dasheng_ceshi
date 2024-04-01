package com.tcc.common.base.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;

@Data
public class BaseUserEntity implements Serializable {

    private Long userId;

    private String username;

    private String name;

    private Integer status;

    @TableField(exist = false)
    private String token;

}
