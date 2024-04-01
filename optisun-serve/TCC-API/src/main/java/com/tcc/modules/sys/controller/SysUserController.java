package com.tcc.modules.sys.controller;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tcc.common.annotation.SysLog;
import com.tcc.common.utils.MD5Util;
import com.tcc.common.utils.PageUtils;
import com.tcc.common.utils.R;
import com.tcc.common.validator.Assert;
import com.tcc.common.validator.ValidatorUtils;
import com.tcc.common.validator.group.AddGroup;
import com.tcc.common.validator.group.UpdateGroup;
import com.tcc.modules.base.AbstractBackController;
import com.tcc.modules.sys.entity.SysDataPermissionEntity;
import com.tcc.modules.sys.entity.SysOrgEntity;
import com.tcc.modules.sys.entity.SysUserEntity;
import com.tcc.modules.sys.form.PasswordForm;
import com.tcc.modules.sys.service.SysDataPermissionService;
import com.tcc.modules.sys.service.SysOrgService;
import com.tcc.modules.sys.service.SysUserRoleService;
import com.tcc.modules.sys.service.SysUserService;
import org.apache.commons.lang.ArrayUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 系统用户
 *
 */
@RestController
@RequestMapping("/sys/user")
public class SysUserController extends AbstractBackController {

	@Autowired
	private SysUserService sysUserService;
	@Autowired
	private SysUserRoleService sysUserRoleService;
	@Autowired
	private SysOrgService sysOrgService;
	@Autowired
	private SysDataPermissionService sysDataPermissionService;

	/**
	 * 所有用户列表
	 */
	@GetMapping("/all")
	public R all(){
		//只有超级管理员，才能查看所有管理员列表
		List<SysUserEntity> list = sysUserService.list(new QueryWrapper<SysUserEntity>().eq("org_id", getOrgId()));
		return R.ok().put("list", list);
	}

	/**
	 * 所有用户列表
	 */
	@GetMapping("/list")
	@RequiresPermissions("sys:user:list")
	public R list(@RequestParam Map<String, Object> params){
		params.put("orgId", getOrgId());
		PageUtils page = sysUserService.queryPage(params);

		return R.ok().put("page", page);
	}
	
	/**
	 * 获取登录的用户信息
	 */
	@GetMapping("/info")
	public R info(){
		SysUserEntity user = getUser();
		//获取用户所属机构信息
		Map<String,Object> orgInfo = new HashMap<>();
		SysOrgEntity orgEntity =  sysOrgService.getById(user.getOrgId());
		if(orgEntity!=null){
			orgInfo.put("sysLogo",orgEntity.getSysLogo());
			orgInfo.put("sysTitle",orgEntity.getSysTitle());
		}
		else{
			orgInfo.put("sysLogo","");
			orgInfo.put("sysTitle","后台管理系统");
		}
		List<Map<String, Object>> dataPermission = sysDataPermissionService
				.list(new QueryWrapper<SysDataPermissionEntity>()
						.eq("user_id", user.getUserId()))
				.stream().map(item -> {
					Map<String, Object> m = new HashMap<>();
					m.put("id", item.getRelationId());
					m.put("type", item.getRelationType() == 1 ? 1 : 0);
					return m;
				}).collect(Collectors.toList());
		user.setDataPermission(dataPermission);
		return R.ok().put("user", user)
				.put("orgInfo",orgInfo);
	}
	
	/**
	 * 修改登录用户密码
	 */
	@SysLog("修改密码")
	@PostMapping("/password")
	public R password(@RequestBody PasswordForm form){
		Assert.isBlank(form.getNewPassword(), "新密码不为能空");
		
		//sha256加密
		String password = MD5Util.md5(form.getPassword());
		//sha256加密
		String newPassword = MD5Util.md5(form.getNewPassword());
				
		//更新密码
		boolean flag = sysUserService.updatePassword(getUserId(), password, newPassword);
		if(!flag){
			return R.error("原密码不正确");
		}
		
		return R.ok();
	}

	@SysLog("设置客服链接")
	@PostMapping("/setkefu")
	public R setkefu(@RequestBody SysUserEntity user){
		user.setUserId(getUserId());
		sysUserService.updateById(user);
		return R.ok();
	}
	
	/**
	 * 用户信息
	 */
	@GetMapping("/info/{userId}")
	public R info(@PathVariable("userId") Long userId){
		SysUserEntity user = sysUserService.getById(userId);
		
		//获取用户所属的角色列表
		List<Long> roleIdList = sysUserRoleService.queryRoleIdList(userId);
		user.setRoleIdList(roleIdList);
		List<Map<String, Object>> dataPermission = sysDataPermissionService
				.list(new QueryWrapper<SysDataPermissionEntity>()
						.eq("user_id", user.getUserId()))
				.stream().map(item -> {
					Map<String, Object> m = new HashMap<>();
					m.put("id", item.getRelationId());
					m.put("type", item.getRelationType() == 1 ? 1 : 0);
					return m;
				}).collect(Collectors.toList());
		user.setDataPermission(dataPermission);
		return R.ok().put("user", user);
	}
	
	/**
	 * 保存用户
	 */
	@SysLog("保存用户")
	@PostMapping("/save")
	@RequiresPermissions("sys:user:save")
	public R save(@RequestBody SysUserEntity user){
		ValidatorUtils.validateEntity(user, AddGroup.class);
		user.setCreateUserId(getUserId());
		if(user.getUserId() == null
				|| user.getUserId() == 0) {
			// 新增的用户
			user.setOrgId(getOrgId());
			if(user.getType()==0||user.getType()==1){
				user.setRelationId(0L);
			}
			else{
				user.setRelationId(getUserId());
			}
		}
		sysUserService.saveUser(user);
		
		return R.ok();
	}
	
	/**
	 * 修改用户
	 */
	@SysLog("修改用户")
	@PostMapping("/update")
	@RequiresPermissions("sys:user:update")
	public R update(@RequestBody SysUserEntity user){
		ValidatorUtils.validateEntity(user, UpdateGroup.class);
		user.setCreateUserId(getUserId());
		sysUserService.update(user);
		
		return R.ok();
	}
	
	/**
	 * 删除用户
	 */
	@SysLog("删除用户")
	@PostMapping("/delete")
	@RequiresPermissions("sys:user:delete")
	public R delete(@RequestBody Long[] userIds){
		if(ArrayUtils.contains(userIds, 1L)){
			return R.error("系统管理员不能删除");
		}
		
		if(ArrayUtils.contains(userIds, getUserId())){
			return R.error("当前用户不能删除");
		}
		
		sysUserService.deleteBatch(userIds);
		
		return R.ok();
	}

	@SysLog("重置密码")
	@PostMapping("/resetPassword")
	@RequiresPermissions("sys:user:reset")
	public R resetPassword(@RequestBody Long userId){
	   SysUserEntity sysUser = 	sysUserService.getById(userId);
		//sha256加密
		String newPassword = MD5Util.md5("123456");
		//更新密码
		boolean flag = sysUserService.resetPassword(userId, newPassword);
		if(!flag){
			return R.error("初始化密码错误");
		}
		return R.ok();
	}

	@SysLog("个人修改信息")
	@PostMapping("/updateInfo")
	public R updateInfo(@RequestBody JSONObject jsonObject){
		 Long userId = jsonObject.getLong("userId");
		 String name = jsonObject.getStr("name");
		 String avater = jsonObject.getStr("avatar");
		 String customerUrl= jsonObject.getStr("customerUrl");
	     SysUserEntity sysUser = 	sysUserService.getById(userId);
		 if(sysUser!=null){
			 sysUser.setName(name);
			 sysUser.setAvatar(avater);
			 sysUser.setCustomerUrl(customerUrl);
			 sysUserService.updateById(sysUser);
		 }
         return R.ok();
	}
}
