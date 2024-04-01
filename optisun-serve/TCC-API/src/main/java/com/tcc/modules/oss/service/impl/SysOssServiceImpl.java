

package com.tcc.modules.oss.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tcc.common.utils.PageUtils;
import com.tcc.common.utils.Query;
import com.tcc.modules.oss.dao.SysOssDao;
import com.tcc.modules.oss.entity.SysOssEntity;
import com.tcc.modules.oss.service.SysOssService;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("sysOssService")
public class SysOssServiceImpl extends ServiceImpl<SysOssDao, SysOssEntity> implements SysOssService {

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		String key = Convert.toStr(params.get("key"));
		//类型1:图片，2:语音，3:视频，4:文件
		Integer type = Convert.toInt(params.get("type"),-1);
		IPage<SysOssEntity> page = this.page(
			new Query<SysOssEntity>().getPage(params),
			new QueryWrapper<SysOssEntity>().like(StrUtil.isNotBlank(key), "name", key)
					.eq(type>0,"type",type)
		);

		return new PageUtils(page);
	}
	
}
