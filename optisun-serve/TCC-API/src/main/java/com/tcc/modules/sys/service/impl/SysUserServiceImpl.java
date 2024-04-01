

package com.tcc.modules.sys.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tcc.common.exception.WTDPException;
import com.tcc.common.utils.*;
import com.tcc.common.utils.core.text.Convert;
import com.tcc.modules.sys.dao.SysUserDao;
import com.tcc.modules.sys.entity.SysDataPermissionEntity;
import com.tcc.modules.sys.entity.SysRoleEntity;
import com.tcc.modules.sys.entity.SysUserEntity;
import com.tcc.modules.sys.service.SysDataPermissionService;
import com.tcc.modules.sys.service.SysRoleService;
import com.tcc.modules.sys.service.SysUserRoleService;
import com.tcc.modules.sys.service.SysUserService;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


/**
 * 系统用户
 */
@Service("sysUserService")
public class SysUserServiceImpl extends ServiceImpl<SysUserDao, SysUserEntity> implements SysUserService {
	@Autowired
	private SysUserRoleService sysUserRoleService;
	@Autowired
	private SysRoleService sysRoleService;
	@Autowired
	private SysDataPermissionService sysDataPermissionService;

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		String key = (String)params.get("key");
		Long createUserId = (Long)params.get("createUserId");
		int deptId = Convert.toInt(params.get("deptId"), 0);
		Long  orgId = Convert.toLong(params.get("orgId"));
		String teamStr =Convert.toStr(params.get("team"),"");
		String[] teamArr=new String[]{};;
		if(StrUtil.isNotBlank(teamStr)){
			teamArr=teamStr.split(",");
		}
		else{
			throw new WTDPException("请选择人员");
		}
		IPage<SysUserEntity> page = this.page(
			new Query<SysUserEntity>().getPage(params),
			new QueryWrapper<SysUserEntity>()
					.and(StringUtils.isNotBlank(key),a->a.like("username",key).or().like("name",key).or().like("mobile",key).or().like("user_id",key))
				.eq(createUserId != null,"create_user_id", createUserId)
				.eq(deptId != 0, "dept_id", deptId)
				.eq("org_id", orgId).in(teamArr.length>0,"user_id",teamArr).orderByDesc("create_time")
		);

		return new PageUtils(page);
	}

	@Override
	public List<String> queryAllPerms(Long userId) {
		return baseMapper.queryAllPerms(userId);
	}

	@Override
	public List<Long> queryAllMenuId(Long userId) {
		return baseMapper.queryAllMenuId(userId);
	}

	@Override
	public SysUserEntity queryByUserName(String username) {
		return baseMapper.queryByUserName(username);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveUser(SysUserEntity user) {
		user.setCreateTime(DateUtils.getCurrentTime());
		//sha256加密
		String salt = RandomStringUtils.randomAlphanumeric(20);
		user.setPassword(MD5Util.md5(user.getPassword()));
		user.setSalt(salt);
		this.save(user);
		
		//检查角色是否越权
		checkRole(user);
		
		//保存用户与角色关系
		sysUserRoleService.saveOrUpdate(user.getUserId(), user.getRoleIdList());

		//保存员工的数据权限
		if(user.getDataPermission() != null && user.getDataPermission().size() > 0) {
			List<SysDataPermissionEntity> dataPermission = user.getDataPermission().stream().map(item -> {
				SysDataPermissionEntity en = new SysDataPermissionEntity();
				en.setUserId(Convert.toInt(user.getUserId()));
				en.setType(1);
				en.setRelationType(Convert.toInt(item.get("type")) == 0 ? 2 : 1);
				en.setRelationId(Convert.toStr(item.get("id")));
				en.setCreateBy(Convert.toInt(user.getCreateUserId()));
				en.setCreateTime(DateUtils.getCurrentTime());
				en.setUpdateBy(Convert.toInt(user.getCreateUserId()));
				en.setUpdateTime(DateUtils.getCurrentTime());
				en.setVersion(0);
				return en;
			}).collect(Collectors.toList());
			sysDataPermissionService.saveBatch(dataPermission);
		}
	}

	@Caching(evict = {
			@CacheEvict(value = Constant.REDIS_CACHE_KEY, key = "'adminuser:' + #user.userId")
	})
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void update(SysUserEntity user) {
		if(StringUtils.isBlank(user.getPassword())){
			user.setPassword(null);
		}else{
			user.setPassword(new Sha256Hash(user.getPassword(), user.getSalt()).toHex());
		}
		this.updateById(user);
		
		//检查角色是否越权
		checkRole(user);
		
		//保存用户与角色关系
		sysUserRoleService.saveOrUpdate(user.getUserId(), user.getRoleIdList());

		//保存员工的数据权限
		sysDataPermissionService.remove(new QueryWrapper<SysDataPermissionEntity>().eq("user_id", user.getUserId()));
		if(user.getDataPermission() != null && user.getDataPermission().size() > 0) {
			List<SysDataPermissionEntity> dataPermission = user.getDataPermission().stream().map(item -> {
				SysDataPermissionEntity en = new SysDataPermissionEntity();
				en.setUserId(Convert.toInt(user.getUserId()));
				en.setType(1);
				en.setRelationType(Convert.toInt(item.get("type")) == 0 ? 2 : 1);
				en.setRelationId(Convert.toStr(item.get("id")));
				en.setCreateBy(Convert.toInt(user.getCreateUserId()));
				en.setCreateTime(DateUtils.getCurrentTime());
				en.setUpdateBy(Convert.toInt(user.getCreateUserId()));
				en.setUpdateTime(DateUtils.getCurrentTime());
				en.setVersion(0);
				return en;
			}).collect(Collectors.toList());
			sysDataPermissionService.saveBatch(dataPermission);
		}
	}

	@Override
	public void deleteBatch(Long[] userId) {
		this.removeByIds(Arrays.asList(userId));
	}

	@Override
	public boolean updatePassword(Long userId, String password, String newPassword) {
		SysUserEntity userEntity = new SysUserEntity();
		userEntity.setPassword(newPassword);
		return this.update(userEntity,
				new QueryWrapper<SysUserEntity>().eq("user_id", userId).eq("password", password));
	}
	
	/**
	 * 检查角色是否越权
	 */
	private void checkRole(SysUserEntity user){
		if(user.getRoleIdList() == null || user.getRoleIdList().size() == 0){
			return;
		}
//		//如果不是超级管理员，则需要判断用户的角色是否自己创建
//		if(user.getType() == 0){
//			return ;
//		}
		List<SysRoleEntity> roleList = sysRoleService.list(new LambdaQueryWrapper<SysRoleEntity>().in(SysRoleEntity::getRoleId, user.getRoleIdList()));
		for (int i = 0; i < roleList.size(); i++) {
			if(user.getType() != roleList.get(i).getType()){
				throw new WTDPException("所选的角色与其用户类型不匹配");
			}
		}



//
//		//若是代理商或是门店，无需判断
//		if(user.getType() == 2 || user.getType() == 3){
//			return;
//		}
//
//		//查询用户创建的角色列表
//		List<Long> roleIdList = sysRoleService.queryRoleIdList(user.getCreateUserId());
//
//		//判断是否越权
//		if(!roleIdList.containsAll(user.getRoleIdList())){
//			throw new WTDPException("新增用户所选角色，不是本人创建");
//		}


	}

	@Override
	public boolean resetPassword(Long userId,String newPassword){
		SysUserEntity userEntity = new SysUserEntity();
		userEntity.setUserId(userId);
		userEntity.setPassword(newPassword);
		return this.updateById(userEntity);
	}

	@Override
	public List<SysUserEntity> getUsersByCondition(Map<String, Object> params) {
		List<String> deptIdList = (List<String>) params.get("deptIdList");
		return this.list(new QueryWrapper<SysUserEntity>()
				.eq(StrUtil.isNotBlank("orgId"), "org_id", params.get("orgId"))
				.in(CollectionUtil.isNotEmpty(deptIdList),"dept_id", deptIdList));
	}
}