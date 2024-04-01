

package com.tcc.modules.sys.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tcc.common.annotation.SysLog;
import com.tcc.common.base.service.ShiroService;
import com.tcc.common.exception.WTDPException;
import com.tcc.common.utils.Constant;
import com.tcc.common.utils.R;
import com.tcc.modules.base.AbstractBackController;
import com.tcc.modules.sys.entity.SysMenuEntity;
import com.tcc.modules.sys.service.SaasService;
import com.tcc.modules.sys.service.SysMenuService;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * 系统菜单
 *
 */
@RestController
@RequestMapping("/sys/menu")
public class SysMenuController extends AbstractBackController {
	@Autowired
	private SysMenuService sysMenuService;
	@Autowired
	private ShiroService shiroService;
	@Autowired private SaasService saasService;

	/**
	 * 导航菜单
	 */
	@GetMapping("/nav")
	public R nav(){
		List<SysMenuEntity> menuList = sysMenuService.getUserMenuList(getUserId());
		Set<String> permissions = shiroService.getUserPermissions(getUserId());
		return R.ok().put("menuList", menuList).put("permissions", permissions);
	}
	
	/**
	 * 所有菜单列表
	 */
	@GetMapping("/list")
	@RequiresPermissions("sys:menu:list")
	public List<SysMenuEntity> list(){
		if(getUser().getType() != 0) {
			if (saasService.isOpen()) {
				return sysMenuService.queryUserAllMenu(getUserId());
			} else if (getUser().getType() == 1) {
				return sysMenuService.list(new QueryWrapper<SysMenuEntity>().orderByAsc("order_num"));
			} else {
				return sysMenuService.queryUserAllMenu(getUserId());
			}
		}else{
			return sysMenuService.list(new QueryWrapper<SysMenuEntity>().orderByAsc("order_num"));
		}
	}
	
	/**
	 * 选择菜单(添加、修改菜单)
	 */
	@GetMapping("/select")
	@RequiresPermissions("sys:menu:select")
	public R select(){
		//查询列表数据
		List<SysMenuEntity> menuList = sysMenuService.queryNotButtonList();
		
		//添加顶级菜单
		SysMenuEntity root = new SysMenuEntity();
		root.setMenuId(0L);
		root.setName("一级菜单");
		root.setParentId(-1L);
		root.setOpen(true);
		menuList.add(root);
		
		return R.ok().put("menuList", menuList);
	}
	
	/**
	 * 菜单信息
	 */
	@GetMapping("/info/{menuId}")
	@RequiresPermissions("sys:menu:info")
	public R info(@PathVariable("menuId") Long menuId){
		SysMenuEntity menu = sysMenuService.getById(menuId);
		return R.ok().put("menu", menu);
	}
	
	/**
	 * 保存
	 */
	@SysLog("保存菜单")
	@PostMapping("/save")
	@RequiresPermissions("sys:menu:save")
	public R save(@RequestBody SysMenuEntity menu){
		//数据校验
		verifyForm(menu);
		
		sysMenuService.save(menu);
		
		return R.ok();
	}
	
	/**
	 * 修改
	 */
	@SysLog("修改菜单")
	@PostMapping("/update")
	@RequiresPermissions("sys:menu:update")
	public R update(@RequestBody SysMenuEntity menu){
		//数据校验
		verifyForm(menu);
				
		sysMenuService.updateById(menu);
		
		return R.ok();
	}
	
	/**
	 * 删除
	 */
	@SysLog("删除菜单")
	@PostMapping("/delete/{menuId}")
	@RequiresPermissions("sys:menu:delete")
	public R delete(@PathVariable("menuId") long menuId){
		//判断是否有子菜单或按钮
		int childrenNum = sysMenuService.count(new QueryWrapper<SysMenuEntity>().eq("parent_id", menuId));
		if(childrenNum > 0){
			return R.error("请先删除子菜单或按钮");
		}
		sysMenuService.delete(menuId);
		return R.ok();
	}
	
	/**
	 * 验证参数是否正确
	 */
	private void verifyForm(SysMenuEntity menu){
		if(StringUtils.isBlank(menu.getName())){
			throw new WTDPException("菜单名称不能为空");
		}
		
		if(menu.getParentId() == null){
			throw new WTDPException("上级菜单不能为空");
		}
		
		//菜单
		if(menu.getType() == Constant.MenuType.MENU.getValue()){
			if(StringUtils.isBlank(menu.getUrl())){
				throw new WTDPException("菜单URL不能为空");
			}
		}
		
		//上级菜单类型
		int parentType = Constant.MenuType.CATALOG.getValue();
		if(menu.getParentId() != 0){
			SysMenuEntity parentMenu = sysMenuService.getById(menu.getParentId());
			parentType = parentMenu.getType();
		}
		
		//目录、菜单
		if(menu.getType() == Constant.MenuType.CATALOG.getValue() ||
				menu.getType() == Constant.MenuType.MENU.getValue()){
			if(parentType != Constant.MenuType.CATALOG.getValue()){
				throw new WTDPException("上级菜单只能为目录类型");
			}
			return ;
		}
		
		//按钮
		if(menu.getType() == Constant.MenuType.BUTTON.getValue()){
			if(parentType != Constant.MenuType.MENU.getValue()){
				throw new WTDPException("上级菜单只能为菜单类型");
			}
			return ;
		}
	}
}
