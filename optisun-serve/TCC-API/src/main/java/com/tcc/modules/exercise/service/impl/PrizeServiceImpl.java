package com.tcc.modules.exercise.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tcc.common.utils.PageUtils;
import com.tcc.common.utils.Query;
import com.tcc.modules.exercise.dao.PrizeDao;
import com.tcc.modules.exercise.entity.PrizeEntity;
import com.tcc.modules.exercise.service.PrizeService;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service("PrizeService")
public class PrizeServiceImpl extends ServiceImpl<PrizeDao, PrizeEntity> implements PrizeService {
    @Override
    public PageUtils selectByCondition(Map<String, Object> params) {
        Long id = Convert.toLong(params.get("id"));
        String key = Convert.toStr(params.get("key"));
        IPage<PrizeEntity> page = new Query<PrizeEntity>().getPage(params);
        this.page(page, new LambdaQueryWrapper<PrizeEntity>()
                .eq(id != null, PrizeEntity::getId, id)
                .like(StrUtil.isNotBlank(key), PrizeEntity::getPrizeName, key)
        );
        return new PageUtils(page);
    }
}
