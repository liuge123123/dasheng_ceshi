package com.tcc.modules.sys.service.impl;

import cn.hutool.core.date.DateUtil;
import com.tcc.common.utils.DateUtils;
import org.springframework.stereotype.Service;

import java.util.Date;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.tcc.modules.sys.dao.SysTicketNoDao;
import com.tcc.modules.sys.entity.SysTicketNoEntity;
import com.tcc.modules.sys.service.SysTicketNoService;


@Service("sysTicketNoService")
public class SysTicketNoServiceImpl extends ServiceImpl<SysTicketNoDao, SysTicketNoEntity> implements SysTicketNoService {


    @Override
    public int getTicketNo(Long  orgId, String type) {
        SysTicketNoEntity sysTicket = getOne(new QueryWrapper<SysTicketNoEntity>()
                .eq("org_id", orgId)
                .eq("type", type));
        return getTicketNo(orgId, type, sysTicket);
    }

    @Override
    public int getTicketNoFromDate(Long  orgId, String type) {
        String date = DateUtil.formatDate(new Date());
        SysTicketNoEntity sysTicket = getOne(new QueryWrapper<SysTicketNoEntity>()
                .eq("org_id", orgId)
                .eq("type", type)
                .eq("business_date", date));
        return getTicketNo(orgId, type, sysTicket);
    }

    @Override
    public void initTicketNo(Long  orgId, String type, int ticketNo) {
        SysTicketNoEntity sysTicket = getOne(new QueryWrapper<SysTicketNoEntity>()
                .eq("org_id", orgId)
                .eq("type", type));
        initTicketNo(orgId, type, ticketNo, sysTicket);
    }

    @Override
    public void initTicketNoFromDate(Long  orgId, String type, int ticketNo) {
        String date = DateUtil.formatDate(new Date());
        SysTicketNoEntity sysTicket = getOne(new QueryWrapper<SysTicketNoEntity>()
                .eq("org_id", orgId)
                .eq("type", type)
                .eq("business_date", date));
        initTicketNo(orgId, type, ticketNo, sysTicket);
    }

    private int getTicketNo(Long  orgId, String type, SysTicketNoEntity sysTicket){
        if(sysTicket == null){
            sysTicket = new SysTicketNoEntity();
            sysTicket.setOrgId(orgId);
            sysTicket.setTicketNo(1);
            sysTicket.setType(type);
            sysTicket.setBusinessDate(DateUtil.formatDate(new Date()));
            sysTicket.setCreateTime(DateUtils.getCurrentTime());
            save(sysTicket);
        }else{
            sysTicket.setTicketNo(sysTicket.getTicketNo() + 1);
            sysTicket.setBusinessDate(DateUtil.formatDate(new Date()));
            sysTicket.setUpdateBy(DateUtils.getCurrentTime());
            updateById(sysTicket);
        }
        return sysTicket.getTicketNo();
    }

    private int initTicketNo(Long  orgId, String type, int ticketNo, SysTicketNoEntity sysTicket){
        if(sysTicket == null){
            sysTicket = new SysTicketNoEntity();
            sysTicket.setOrgId(orgId);
            sysTicket.setTicketNo(ticketNo);
            sysTicket.setType(type);
            sysTicket.setBusinessDate(DateUtil.formatDate(new Date()));
            sysTicket.setCreateTime(DateUtils.getCurrentTime());
            save(sysTicket);
        }else{
            sysTicket.setTicketNo(ticketNo);
            sysTicket.setBusinessDate(DateUtil.formatDate(new Date()));
            sysTicket.setUpdateBy(DateUtils.getCurrentTime());
            updateById(sysTicket);
        }
        return sysTicket.getTicketNo();
    }
}