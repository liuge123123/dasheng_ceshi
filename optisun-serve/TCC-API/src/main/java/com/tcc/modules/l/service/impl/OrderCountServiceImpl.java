package com.tcc.modules.l.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tcc.modules.l.entity.LGoodsEntity;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tcc.common.utils.PageUtils;
import com.tcc.common.utils.Query;

import com.tcc.modules.l.dao.OrderCountDao;
import com.tcc.modules.l.entity.OrderCountEntity;
import com.tcc.modules.l.service.OrderCountService;


@Service("orderCountService")
public class OrderCountServiceImpl extends ServiceImpl<OrderCountDao, OrderCountEntity> implements OrderCountService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String key = Convert.toStr(params.get("key"));
        IPage<OrderCountEntity> page = this.page(
                new Query<OrderCountEntity>().getPage(params),
                new LambdaQueryWrapper<OrderCountEntity>()
                        .eq(StrUtil.isNotBlank(key), OrderCountEntity::getCustId, key)
        );

        return new PageUtils(page);
    }

}