package com.tcc.modules.l.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tcc.modules.l.service.LRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tcc.common.utils.PageUtils;
import com.tcc.common.utils.Query;

import com.tcc.modules.l.dao.LGoodsDao;
import com.tcc.modules.l.entity.LGoodsEntity;
import com.tcc.modules.l.service.LGoodsService;


@Service("lGoodsService")
public class LGoodsServiceImpl extends ServiceImpl<LGoodsDao, LGoodsEntity> implements LGoodsService {

    @Autowired
    private LRoomService lRoomService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String key = Convert.toStr(params.get("key"));
        String roomId = Convert.toStr(params.get("roomId"));
        IPage<LGoodsEntity> page = this.page(
                new Query<LGoodsEntity>().getPage(params),
                new LambdaQueryWrapper<LGoodsEntity>()
                        .like(StrUtil.isNotBlank(key), LGoodsEntity::getName, key)
                        .eq(StrUtil.isNotBlank(roomId), LGoodsEntity::getRoomId, roomId)
        );
        page.getRecords().forEach(item -> {
            item.setRoom(lRoomService.getById(item.getRoomId()));
        });
        return new PageUtils(page);
    }

}