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

import com.tcc.modules.g.dao.GRoomDao;
import com.tcc.modules.g.entity.GRoomEntity;
import com.tcc.modules.g.service.GRoomService;


@Service("gRoomService")
public class GRoomServiceImpl extends ServiceImpl<GRoomDao, GRoomEntity> implements GRoomService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<GRoomEntity> page = this.page(
                new Query<GRoomEntity>().getPage(params),
                new QueryWrapper<GRoomEntity>().eq("org_Id",params.get("orgId")).eq("del_flag",0)
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
