

package com.tcc.modules.sys.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tcc.common.base.entity.BaseLogEntity;
import com.tcc.common.utils.PageUtils;
import com.tcc.common.utils.Query;
import com.tcc.modules.sys.dao.SysLogDao;
import com.tcc.modules.sys.entity.SysLogEntity;
import com.tcc.modules.sys.service.SysLogService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("sysLogService")
public class SysLogServiceImpl extends ServiceImpl<SysLogDao, SysLogEntity> implements SysLogService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String key = (String)params.get("key");

        IPage<SysLogEntity> page = this.page(
            new Query<SysLogEntity>().getPage(params),
            new LambdaQueryWrapper<SysLogEntity>()
                    .like(StringUtils.isNotBlank(key),SysLogEntity::getUsername, key)
                    .or()
                    .like(StringUtils.isNotBlank(key),SysLogEntity::getOperation, key)
                    .orderByDesc( SysLogEntity::getCreateDate)
        );

        return new PageUtils(page);
    }

    @Override
    public void saveLog(BaseLogEntity log) {
        SysLogEntity sysLog = JSONUtil.parseObj(log).toBean(SysLogEntity.class);
        this.save(sysLog);
    }
}
