package com.tcc.modules.exercise.service.impl;

import cn.hutool.core.util.StrUtil;
import com.tcc.common.utils.core.text.Convert;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tcc.common.utils.PageUtils;
import com.tcc.common.utils.Query;

import com.tcc.modules.exercise.dao.SignProductDao;
import com.tcc.modules.exercise.entity.SignProductEntity;
import com.tcc.modules.exercise.service.SignProductService;


@Service("signProductService")
public class SignProductServiceImpl extends ServiceImpl<SignProductDao, SignProductEntity> implements SignProductService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String key = Convert.toStr(params.get("key"),"");
        Integer id =Convert.toInt(params.get("id"),0);
        QueryWrapper queryWrapper =new QueryWrapper<SignProductEntity>();
        queryWrapper.eq(id>0,"id",id);
        queryWrapper.like(StrUtil.isNotEmpty(key),"name",key);

        IPage<SignProductEntity> page = this.page(
                new Query<SignProductEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

}