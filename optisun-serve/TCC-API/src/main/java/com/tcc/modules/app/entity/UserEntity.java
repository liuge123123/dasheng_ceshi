package com.tcc.modules.app.entity;

import cn.hutool.core.convert.Convert;
import com.tcc.modules.cust.entity.CustEntity;

public class UserEntity extends CustEntity {

    private String userId;

    private String name;

    public String getUserId() {
        return Convert.toStr(getCustId());
    }

    public String getName() {
        return getCustName();
    }
}
