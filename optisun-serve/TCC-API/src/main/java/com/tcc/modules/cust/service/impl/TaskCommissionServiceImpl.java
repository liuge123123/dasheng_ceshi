package com.tcc.modules.cust.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tcc.common.utils.PageUtils;
import com.tcc.common.utils.Query;

import com.tcc.modules.cust.dao.TaskCommissionDao;
import com.tcc.modules.cust.entity.TaskCommissionEntity;
import com.tcc.modules.cust.service.TaskCommissionService;


@Service("taskCommissionService")
public class TaskCommissionServiceImpl extends ServiceImpl<TaskCommissionDao, TaskCommissionEntity> implements TaskCommissionService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<TaskCommissionEntity> page = this.page(
                new Query<TaskCommissionEntity>().getPage(params),
                new QueryWrapper<TaskCommissionEntity>()
        );

        return new PageUtils(page);
    }

}