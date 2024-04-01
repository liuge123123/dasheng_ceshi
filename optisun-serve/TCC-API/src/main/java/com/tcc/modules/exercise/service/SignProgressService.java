package com.tcc.modules.exercise.service;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tcc.common.utils.PageUtils;
import com.tcc.modules.exercise.entity.SignProgressEntity;

import java.util.List;
import java.util.Map;

/**
 * 用户兑换产品进度表
 *
 * @author 
 * @email 
 * @date 2022-10-05 11:40:10
 */
public interface SignProgressService extends IService<SignProgressEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<JSONObject> getProductProgress(Long custId);

    void pointExchange(Long custId, Long productId);
}

