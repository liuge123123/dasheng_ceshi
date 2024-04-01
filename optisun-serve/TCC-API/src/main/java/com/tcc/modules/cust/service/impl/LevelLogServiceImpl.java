package com.tcc.modules.cust.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tcc.common.utils.PageUtils;
import com.tcc.common.utils.Query;

import com.tcc.modules.cust.dao.LevelLogDao;
import com.tcc.modules.cust.entity.LevelLogEntity;
import com.tcc.modules.cust.service.LevelLogService;


@Service("levelLogService")
public class LevelLogServiceImpl extends ServiceImpl<LevelLogDao, LevelLogEntity> implements LevelLogService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<LevelLogEntity> page = this.page(
                new Query<LevelLogEntity>().getPage(params),
                new QueryWrapper<LevelLogEntity>()
        );

        return new PageUtils(page);
    }

}