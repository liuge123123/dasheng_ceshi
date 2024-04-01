package com.tcc.modules.app.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.tcc.modules.app.entity.UserEntity;
import com.tcc.modules.app.service.UserService;
import com.tcc.modules.cust.entity.CustEntity;
import com.tcc.modules.cust.service.CustService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service("userService")
public class UserServiceImpl implements UserService {

	@Autowired
	private CustService custService;

	@Override
	public UserEntity getUserById(String userId) {
		CustEntity cust = custService.getById(userId);
		return BeanUtil.toBean(cust, UserEntity.class);
	}



}
