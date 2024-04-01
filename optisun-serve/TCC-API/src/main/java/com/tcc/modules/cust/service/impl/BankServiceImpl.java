package com.tcc.modules.cust.service.impl;

import cn.hutool.core.util.StrUtil;
import com.tcc.common.utils.core.text.Convert;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tcc.common.utils.PageUtils;
import com.tcc.common.utils.Query;

import com.tcc.modules.cust.dao.BankDao;
import com.tcc.modules.cust.entity.BankEntity;
import com.tcc.modules.cust.service.BankService;


@Service("bankService")
public class BankServiceImpl extends ServiceImpl<BankDao, BankEntity> implements BankService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage page = new Query<>().getPage(params);
        List<Map<String,Object>> custList=this.baseMapper.getBankList(page, params);
        page.setRecords(custList);
        return new PageUtils(page);
    }

}