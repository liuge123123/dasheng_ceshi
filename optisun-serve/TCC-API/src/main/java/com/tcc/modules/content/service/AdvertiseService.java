package com.tcc.modules.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tcc.common.utils.PageUtils;
import com.tcc.modules.content.entity.AdvertiseEntity;

import java.util.Map;

/**
 * 轮播图片
 *
 * @author 
 * @email 
 * @date 2022-05-30 17:05:32
 */
public interface AdvertiseService extends IService<AdvertiseEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

