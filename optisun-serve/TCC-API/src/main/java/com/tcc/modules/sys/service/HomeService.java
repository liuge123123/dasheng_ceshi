package com.tcc.modules.sys.service;

import java.util.List;
import java.util.Map;

public interface HomeService {

     /***
     * 获取汇总统计
     * @param orgId
     * @return
     */
    Map getHomeTotalInfo(long orgId,String[] team,String startTime,String endTime, String key);

    /**
     * 获取今日汇总统计
     * @return
     */
    Map getHomeToday(long orgId,String[] team, String key);

    /**
     * 获取昨日汇总统计
     * @param orgId
     * @return
     */
    Map getHomeYesterday(long orgId,String[] team, String key);

    /**
     * 近7天注册情况统计
     * @param orgId
     * @return
     */
    List getRegisterList(long orgId,String[] team);

}
