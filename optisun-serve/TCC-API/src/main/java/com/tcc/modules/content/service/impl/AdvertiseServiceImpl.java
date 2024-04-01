package com.tcc.modules.content.service.impl;

import cn.hutool.core.util.StrUtil;
import com.tcc.common.utils.core.text.Convert;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tcc.common.utils.PageUtils;
import com.tcc.common.utils.Query;

import com.tcc.modules.content.dao.AdvertiseDao;
import com.tcc.modules.content.entity.AdvertiseEntity;
import com.tcc.modules.content.service.AdvertiseService;


@Service("advertiseService")
public class AdvertiseServiceImpl extends ServiceImpl<AdvertiseDao, AdvertiseEntity> implements AdvertiseService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String key = Convert.toStr(params.get("key"),"");
        QueryWrapper<AdvertiseEntity> queryWrapper= new QueryWrapper<AdvertiseEntity>();
        queryWrapper.eq("org_id",params.get("orgId"));
        queryWrapper.like(StrUtil.isNotBlank(key),"advertise_name",key);
        IPage<AdvertiseEntity> page = this.page(
                new Query<AdvertiseEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

}