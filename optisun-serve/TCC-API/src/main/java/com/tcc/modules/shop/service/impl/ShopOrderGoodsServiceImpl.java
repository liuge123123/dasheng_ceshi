package com.tcc.modules.shop.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tcc.common.utils.PageUtils;
import com.tcc.common.utils.Query;

import com.tcc.modules.shop.dao.ShopOrderGoodsDao;
import com.tcc.modules.shop.entity.ShopOrderGoodsEntity;
import com.tcc.modules.shop.service.ShopOrderGoodsService;


@Service("shopOrderGoodsService")
public class ShopOrderGoodsServiceImpl extends ServiceImpl<ShopOrderGoodsDao, ShopOrderGoodsEntity> implements ShopOrderGoodsService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<ShopOrderGoodsEntity> page = this.page(
                new Query<ShopOrderGoodsEntity>().getPage(params),
                new QueryWrapper<ShopOrderGoodsEntity>()
        );

        return new PageUtils(page);
    }

}