package com.tcc.modules.l.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tcc.common.utils.PageUtils;
import com.tcc.common.utils.Query;

import com.tcc.modules.l.dao.LOrderCountDao;
import com.tcc.modules.l.entity.LOrderCountEntity;
import com.tcc.modules.l.service.LOrderCountService;


@Service("lOrderCountService")
public class LOrderCountServiceImpl extends ServiceImpl<LOrderCountDao, LOrderCountEntity> implements LOrderCountService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<LOrderCountEntity> page = this.page(
                new Query<LOrderCountEntity>().getPage(params),
                new QueryWrapper<LOrderCountEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public Integer getCountByLevel(Long custId, Long goodsLevel) {
        return baseMapper.getCountByLevel(custId,goodsLevel);
    }


}