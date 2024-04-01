package com.tcc.modules.l.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tcc.common.utils.PageUtils;
import com.tcc.common.utils.Query;

import com.tcc.modules.l.dao.LOrderCommmissionDao;
import com.tcc.modules.l.entity.LOrderCommmissionEntity;
import com.tcc.modules.l.service.LOrderCommmissionService;


@Service("lOrderCommmissionService")
public class LOrderCommmissionServiceImpl extends ServiceImpl<LOrderCommmissionDao, LOrderCommmissionEntity> implements LOrderCommmissionService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<LOrderCommmissionEntity> page = this.page(
                new Query<LOrderCommmissionEntity>().getPage(params),
                new QueryWrapper<LOrderCommmissionEntity>()
        );

        return new PageUtils(page);
    }

}