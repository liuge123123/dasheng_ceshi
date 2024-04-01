package com.tcc.modules.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tcc.modules.sys.entity.SysTicketNoEntity;

/**
 * 取号记录表
 *
 * @author 
 * @email 
 * @date 2021-01-19 18:13:32
 */
public interface SysTicketNoService extends IService<SysTicketNoEntity> {

    /**
     * 获取业务流水号
     * @param orgId
     * @param type
     * @return
     */
    int getTicketNo(Long  orgId, String type);

    /**
     * 获取业务流水号（每日重置）
     * @param orgId
     * @param type
     * @return
     */
    int getTicketNoFromDate(Long  orgId, String type);

    /**
     * 初始化业务流水号的初始值
     * @param orgId
     * @param type
     * @param ticketNo
     */
    void initTicketNo(Long  orgId, String type, int ticketNo);

    /**
     * 分日期初始化业务流水号的初始值
     * @param orgId
     * @param type
     * @param ticketNo
     */
    void initTicketNoFromDate(Long  orgId, String type, int ticketNo);

}

