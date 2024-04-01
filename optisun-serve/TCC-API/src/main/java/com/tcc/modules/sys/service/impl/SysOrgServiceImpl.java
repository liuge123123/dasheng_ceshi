package com.tcc.modules.sys.service.impl;

import cn.hutool.core.convert.Convert;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tcc.common.utils.PageUtils;
import com.tcc.common.utils.Query;

import com.tcc.modules.sys.dao.SysOrgDao;
import com.tcc.modules.sys.entity.SysOrgEntity;
import com.tcc.modules.sys.service.SysOrgService;


@Service("sysOrgService")
public class SysOrgServiceImpl extends ServiceImpl<SysOrgDao, SysOrgEntity> implements SysOrgService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {

        String key = Convert.toStr(params.get("key"));
        IPage<SysOrgEntity> page = this.page(
                new Query<SysOrgEntity>().getPage(params),
                new QueryWrapper<SysOrgEntity>()
                        .like("name", key)
        );

        return new PageUtils(page);
    }

}