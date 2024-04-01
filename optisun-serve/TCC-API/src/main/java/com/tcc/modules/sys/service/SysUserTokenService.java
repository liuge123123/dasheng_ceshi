

package com.tcc.modules.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tcc.common.utils.R;
import com.tcc.modules.sys.entity.SysUserEntity;
import com.tcc.modules.sys.entity.SysUserTokenEntity;

import java.util.List;

/**
 * 用户Token
 *
 */
public interface SysUserTokenService extends IService<SysUserTokenEntity> {

	/**
	 * 生成token
	 * @param user  用户ID
	 */
	R createToken(SysUserEntity user);

	/**
	 * 退出，修改token值
	 */
	void logout();

	List<SysUserTokenEntity> queryByUserId(Long userId);

}
