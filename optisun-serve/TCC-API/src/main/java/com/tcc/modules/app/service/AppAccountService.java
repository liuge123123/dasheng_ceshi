package com.tcc.modules.app.service;

import cn.hutool.json.JSONObject;
import com.tcc.common.utils.PageUtils;
import com.tcc.modules.g.entity.GOrderEntity;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

public interface AppAccountService {
    /**
     * 获取团队成员列表
     * @param params
     * @return
     */
     PageUtils getAppCustList(@Param("condition") Map<String, Object> params);
    /**
     * 获取充值团队成员列表
     * @param params
     * @return
     */
     PageUtils getAppCustListRecharge(@Param("condition") Map<String, Object> params);
    /**
     * 获取团队汇总信息
     * @param custId
     * @param orgId
     */
    JSONObject getTeamTotal(Long custId, Long orgId);

    /**
     * 做任务
     * @param custId
     * @param orgId
     */
    GOrderEntity taskDeal(Long custId, Long orgId, Long level,Long productId);

    /***
     * 解锁vip
     * @param orgId
     * @param custId
     * @param level 解锁等级id
     */
     void unlockVip(Long orgId,Long custId,Long level);

    /**
     * 获取任务的redis
     * @param custId
     * @return
     */
    String getTaskRedis(Long custId);


}
