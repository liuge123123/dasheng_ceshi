

package com.tcc.modules.sys.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tcc.common.utils.MapUtils;
import com.tcc.modules.sys.dao.SysMenuDao;
import com.tcc.modules.sys.entity.SysMenuEntity;
import com.tcc.modules.sys.entity.SysUserEntity;
import com.tcc.modules.sys.service.SysMenuService;
import com.tcc.modules.sys.service.SysRoleMenuService;
import com.tcc.modules.sys.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service("sysMenuService")
public class SysMenuServiceImpl extends ServiceImpl<SysMenuDao, SysMenuEntity> implements SysMenuService {
	@Autowired
	private SysUserService sysUserService;
	@Autowired
	private SysRoleMenuService sysRoleMenuService;

	@Override
	public List<SysMenuEntity> queryNotButtonList() {
		return baseMapper.queryNotButtonList();
	}

	@Override
	public List<SysMenuEntity> getUserMenuList(Long userId) {
		List<SysMenuEntity> menuList = null;
		SysUserEntity user = sysUserService.getById(userId);
		//系统管理员，拥有最高权限，获取全部菜单
		if(user.getType() == 0){
			menuList = baseMapper.queryNotButtonList();
		}else{
			//用户菜单列表
			menuList = baseMapper.queryUserMenus(userId);
		}
		return getMenuTree(menuList, 0L);
	}

	@Override
	public void delete(Long menuId){
		//删除菜单
		this.removeById(menuId);
		//删除菜单与角色关联
		sysRoleMenuService.removeByMap(new MapUtils().put("menu_id", menuId));
	}


	private List<SysMenuEntity> getMenuTree(List<SysMenuEntity> menuList, long parentId){
		List<SysMenuEntity> subMenuList = new ArrayList<SysMenuEntity>();
		for(SysMenuEntity menu : menuList){
			if(menu.getParentId().equals(parentId)){
				menu.setList(getMenuTree(menuList, menu.getMenuId()));
				subMenuList.add(menu);
			}
		}
		return subMenuList;
	}

	@Override
	public List<SysMenuEntity> queryUserAllMenu(Long userId) {
		return baseMapper.queryUserAllMenu(userId);
	}

}
