

package com.tcc.modules.app.service;


import com.tcc.modules.app.entity.UserEntity;

/**
 * 用户
 *
 */
public interface UserService {

	/***
	 * 获取用户信息
	 * @param userId
	 * @return
	 */
	UserEntity getUserById(String userId);
}
