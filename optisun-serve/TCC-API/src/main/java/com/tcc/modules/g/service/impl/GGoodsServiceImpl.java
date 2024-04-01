package com.tcc.modules.g.service.impl;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tcc.common.utils.PageUtils;
import com.tcc.common.utils.Query;

import com.tcc.modules.g.dao.GGoodsDao;
import com.tcc.modules.g.entity.GGoodsEntity;
import com.tcc.modules.g.service.GGoodsService;


@Service("gGoodsService")
public class GGoodsServiceImpl extends ServiceImpl<GGoodsDao, GGoodsEntity> implements GGoodsService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<GGoodsEntity> page = this.page(
                new Query<GGoodsEntity>().getPage(params),
                new QueryWrapper<GGoodsEntity>().eq("org_id",params.get("orgId")).eq("del_flag","0")
        );

        return new PageUtils(page);
    }
    @Override
    public PageUtils selectByCondition(@Param("condition") Map<String, Object> params) {
        IPage<Map<String,Object>> page = new Query<Map<String,Object>>().getPage(params);
        List<Map<String,Object>> list = baseMapper.selectByCondition(page, params);
        page.setRecords(list);
        return new PageUtils(page);
    }
}
