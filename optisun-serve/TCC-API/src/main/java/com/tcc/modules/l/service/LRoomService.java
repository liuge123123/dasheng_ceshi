package com.tcc.modules.l.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tcc.common.utils.PageUtils;
import com.tcc.modules.l.entity.LRoomEntity;

import java.util.Map;

/**
 * 理财房间
 *
 * @author 
 * @email 
 * @date 2022-12-21 10:37:42
 */
public interface LRoomService extends IService<LRoomEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

