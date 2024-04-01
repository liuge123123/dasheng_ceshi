

package com.tcc.modules.sys.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.tcc.modules.sys.entity.SysMenuEntity;

import java.util.List;


/**
 * 菜单管理
 *
 */
public interface SysMenuService extends IService<SysMenuEntity> {
	
	/**
	 * 获取不包含按钮的菜单列表
	 */
	List<SysMenuEntity> queryNotButtonList();
	
	/**
	 * 获取用户菜单列表
	 */
	List<SysMenuEntity> getUserMenuList(Long userId);

	/**
	 * 删除
	 */
	void delete(Long menuId);

	/**
	 * 获取用户的所有菜单资源
	 * @param userId
	 * @return
	 */
	List<SysMenuEntity> queryUserAllMenu(Long userId);
}
