package com.tcc.modules.cust.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tcc.common.utils.StringUtils;
import com.tcc.modules.cust.entity.CustScoreLogEntity;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tcc.common.utils.PageUtils;
import com.tcc.common.utils.Query;

import com.tcc.modules.cust.dao.DevicesDao;
import com.tcc.modules.cust.entity.DevicesEntity;
import com.tcc.modules.cust.service.DevicesService;


@Service("devicesService")
public class DevicesServiceImpl extends ServiceImpl<DevicesDao, DevicesEntity> implements DevicesService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String key = Convert.toStr(params.get("key"));
        String moneyType = Convert.toStr(params.get("moneyType"));
        Integer isOpen = Convert.toInt(params.get("isOpen"));
        IPage<DevicesEntity> page = this.page(
                new Query<DevicesEntity>().getPage(params),
                new LambdaQueryWrapper<DevicesEntity>()
                        .eq(StringUtils.isNotBlank(moneyType), DevicesEntity::getMoneyType, moneyType)
                        .eq(isOpen != null, DevicesEntity::getIsOpen, isOpen)
                        .like(StringUtils.isNotBlank(key), DevicesEntity::getMobile, key)
                        .orderByDesc(DevicesEntity::getDeviceId)
        );
        return new PageUtils(page);
    }

}