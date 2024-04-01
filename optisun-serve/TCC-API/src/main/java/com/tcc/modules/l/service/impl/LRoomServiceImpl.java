package com.tcc.modules.l.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tcc.common.utils.PageUtils;
import com.tcc.common.utils.Query;

import com.tcc.modules.l.dao.LRoomDao;
import com.tcc.modules.l.entity.LRoomEntity;
import com.tcc.modules.l.service.LRoomService;


@Service("lRoomService")
public class LRoomServiceImpl extends ServiceImpl<LRoomDao, LRoomEntity> implements LRoomService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<LRoomEntity> page = this.page(
                new Query<LRoomEntity>().getPage(params),
                new QueryWrapper<LRoomEntity>()
        );

        return new PageUtils(page);
    }

}