package com.tcc.modules.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tcc.common.exception.WTDPException;
import com.tcc.common.utils.Constant;
import com.google.gson.Gson;
import com.tcc.modules.sys.dao.SysConfigDao;
import com.tcc.modules.sys.entity.SysConfigEntity;
import com.tcc.modules.sys.redis.SysConfigRedis;
import com.tcc.modules.sys.service.SysConfigService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service("sysConfigService")
public class SysConfigServiceImpl extends ServiceImpl<SysConfigDao, SysConfigEntity> implements SysConfigService {
	@Autowired
	private SysConfigRedis sysConfigRedis;


	@Override
	public List<SysConfigEntity> list(long orgId) {
		if(orgId == Constant.SUPER_ORG){
			return list(new QueryWrapper<SysConfigEntity>().eq("org_id", orgId));
		}else{
			List<SysConfigEntity> superList = list(new QueryWrapper<SysConfigEntity>().eq("org_id", Constant.SUPER_ORG));
			List<SysConfigEntity> currentList = list(new QueryWrapper<SysConfigEntity>().eq("org_id", orgId));
			List<String> currentKeys = currentList.stream().map(item -> {
				return item.getParamKey();
			}).collect(Collectors.toList());
			superList = superList.stream().filter(item -> {
				return !currentKeys.contains(item.getParamKey());
			}).collect(Collectors.toList());
			currentList.addAll(superList);
			return currentList;
		}
	}

	@Override
	public void saveConfig(long orgId, SysConfigEntity config) {
		config.setOrgId(orgId);
		this.save(config);
		sysConfigRedis.saveOrUpdate(orgId, config);
	}

	@Override
	public void saveConfig(long orgId, String key, String value) {
		SysConfigEntity config = getOne(new QueryWrapper<SysConfigEntity>().eq("org_id", orgId).eq("param_key", key));
		if(config == null){
			config = new SysConfigEntity();
			config.setParamKey(key);
			config.setParamValue(value);
			config.setOrgId(orgId);
			save(config);
		}else{
			config.setOrgId(orgId);
			config.setParamValue(value);
			updateById(config);
		}
		sysConfigRedis.saveOrUpdate(orgId, config);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void update(long orgId, SysConfigEntity config) {
		config.setOrgId(orgId);
		if(orgId == Constant.SUPER_ORG){
			this.updateById(config);
		}else{
			SysConfigEntity old = getById(config.getId());
			if(old.getOrgId() != Constant.SUPER_ORG){
				this.updateById(config);
			}else{
				config.setOrgId(orgId);
				save(config);
			}
		}
		sysConfigRedis.saveOrUpdate(orgId, config);

	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateValueByKey(long orgId, String key, String value) {
		SysConfigEntity config = sysConfigRedis.get(orgId, key);
		if(config == null){
			config = getOne(new QueryWrapper<SysConfigEntity>().eq("org_id", orgId).eq("param_key", key));
			if(config == null){
				config = getOne(new QueryWrapper<SysConfigEntity>().eq("org_id", Constant.SUPER_ORG).eq("param_key", key));
			}
		}
		config.setOrgId(orgId);
		config.setParamKey(key);
		config.setParamValue(value);
		update(orgId, config);
		sysConfigRedis.saveOrUpdate(orgId, config);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteBatch(long orgId, Long[] ids) {
		for(Long id : ids){
			SysConfigEntity config = this.getById(id);
			sysConfigRedis.delete(orgId, config.getParamKey());
		}
		this.removeByIds(Arrays.asList(ids));
	}

	@Override
	public String getValue(long orgId, String key) {
		SysConfigEntity config = sysConfigRedis.get(orgId, key);
		if(config == null){
			config = getOne(new QueryWrapper<SysConfigEntity>().eq("org_id", orgId).eq("param_key", key));
			if(config == null){
				config = getOne(new QueryWrapper<SysConfigEntity>().eq("org_id", Constant.SUPER_ORG).eq("param_key", key));
			}
			sysConfigRedis.saveOrUpdate(orgId, config);
		}

		return config == null ? null : config.getParamValue();
	}
	
	@Override
	public <T> T getConfigObject(long orgId, String key, Class<T> clazz) {
		String value = getValue(orgId, key);
		if(StringUtils.isNotBlank(value)){
			return new Gson().fromJson(value, clazz);
		}
		try {
			return clazz.newInstance();
		} catch (Exception e) {
			throw new WTDPException("获取参数失败");
		}
	}
}
