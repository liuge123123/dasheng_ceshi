package com.tcc.modules.sys.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tcc.common.utils.PageUtils;
import com.tcc.common.utils.Query;

import com.tcc.modules.sys.dao.SysRegionDao;
import com.tcc.modules.sys.entity.SysRegionEntity;
import com.tcc.modules.sys.service.SysRegionService;


@Service("sysRegionService")
public class SysRegionServiceImpl extends ServiceImpl<SysRegionDao, SysRegionEntity> implements SysRegionService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SysRegionEntity> page = this.page(
                new Query<SysRegionEntity>().getPage(params),
                new QueryWrapper<SysRegionEntity>()
        );

        return new PageUtils(page);
    }

}