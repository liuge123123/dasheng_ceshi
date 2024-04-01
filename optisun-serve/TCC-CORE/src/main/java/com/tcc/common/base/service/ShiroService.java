package com.tcc.common.base.service;

import com.tcc.common.base.entity.BaseUserEntity;
import com.tcc.common.base.entity.BaseUserTokenEntity;

import java.util.Set;

public interface ShiroService {
    /**
     * 获取用户权限列表
     */
    Set<String> getUserPermissions(long userId);

    BaseUserTokenEntity queryByToken(String token);

    /**
     * 根据用户ID，查询用户
     * @param userId
     */
    BaseUserEntity queryUser(Long userId);
}
