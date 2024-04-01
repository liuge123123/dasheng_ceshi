

package com.tcc.modules.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tcc.common.base.service.ConfigService;
import com.tcc.modules.sys.entity.SysConfigEntity;

import java.util.List;

/**
 * 系统配置信息
 *
 */
public interface SysConfigService extends IService<SysConfigEntity>, ConfigService {

	List<SysConfigEntity> list(long orgId);
	
	/**
	 * 保存配置信息
	 */
	public void saveConfig(long orgId, SysConfigEntity config);

	/**
	 * 保存配置信息
	 * @param key
	 * @param value
	 */
	public void saveConfig(long orgId, String key, String value);
	
	/**
	 * 更新配置信息
	 */
	public void update(long orgId, SysConfigEntity config);
	
	/**
	 * 根据key，更新value
	 */
	public void updateValueByKey(long orgId, String key, String value);
	
	/**
	 * 删除配置信息
	 */
	public void deleteBatch(long orgId, Long[] ids);
	
	/**
	 * 根据key，获取配置的value值
	 * 
	 * @param key           key
	 */
	public String getValue(long orgId, String key);
	
	/**
	 * 根据key，获取value的Object对象
	 * @param key    key
	 * @param clazz  Object对象
	 */
	public <T> T getConfigObject(long orgId, String key, Class<T> clazz);
	
}
