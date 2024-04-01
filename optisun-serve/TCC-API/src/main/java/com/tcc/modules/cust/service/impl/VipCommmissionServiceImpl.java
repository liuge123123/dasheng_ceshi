package com.tcc.modules.cust.service.impl;

import cn.hutool.core.convert.Convert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tcc.common.utils.PageUtils;
import com.tcc.common.utils.Query;

import com.tcc.modules.cust.dao.VipCommmissionDao;
import com.tcc.modules.cust.entity.VipCommmissionEntity;
import com.tcc.modules.cust.service.VipCommmissionService;


@Service("vipCommmissionService")
public class VipCommmissionServiceImpl extends ServiceImpl<VipCommmissionDao, VipCommmissionEntity> implements VipCommmissionService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        Long custId = Convert.toLong(params.get("custId"));
        IPage<VipCommmissionEntity> page = this.page(
                new Query<VipCommmissionEntity>().getPage(params),
                new LambdaQueryWrapper<VipCommmissionEntity>()
                        .eq(VipCommmissionEntity::getRewardCust, custId)
                        .orderByDesc(VipCommmissionEntity::getOrderId)
        );

        return new PageUtils(page);
    }

}