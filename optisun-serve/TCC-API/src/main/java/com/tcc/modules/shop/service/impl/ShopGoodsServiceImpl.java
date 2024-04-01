package com.tcc.modules.shop.service.impl;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HtmlUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tcc.common.utils.PageUtils;
import com.tcc.common.utils.Query;
import com.tcc.modules.shop.dao.ShopGoodsDao;
import com.tcc.modules.shop.entity.ShopGoodsEntity;
import com.tcc.modules.shop.service.ShopGoodsService;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("shopGoodsService")
public class ShopGoodsServiceImpl extends ServiceImpl<ShopGoodsDao, ShopGoodsEntity> implements ShopGoodsService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String name = MapUtil.getStr(params, "name");
        String category1 = MapUtil.getStr(params, "category1");
        Double priceStart = MapUtil.getDouble(params, "priceStart");
        Double priceEnd = MapUtil.getDouble(params, "priceEnd");
        String sortField = MapUtil.getStr(params, "sortField");
        Integer status = MapUtil.getInt(params, "status");
        String source = MapUtil.getStr(params, "source");
        LambdaQueryWrapper<ShopGoodsEntity> query = new LambdaQueryWrapper<ShopGoodsEntity>()
                .select(ShopGoodsEntity::getId, ShopGoodsEntity::getName, ShopGoodsEntity::getImg, ShopGoodsEntity::getPrice, ShopGoodsEntity::getOutLink, ShopGoodsEntity::getSortNum, ShopGoodsEntity::getSortNum, ShopGoodsEntity::getStatus, ShopGoodsEntity::getCategory1, ShopGoodsEntity::getType)
                .like(StrUtil.isNotBlank(name), ShopGoodsEntity::getName, HtmlUtil.unescape(name))
                .like(StrUtil.isNotBlank(category1), ShopGoodsEntity::getCategory1, HtmlUtil.unescape(category1))
                .ge(priceStart != null, ShopGoodsEntity::getPrice, priceStart)
                .le(priceEnd != null, ShopGoodsEntity::getPrice, priceEnd)
                .eq(status != null, ShopGoodsEntity::getStatus, status)
                .eq(StrUtil.isNotBlank(source), ShopGoodsEntity::getSource, source);
        if (StrUtil.isNotBlank(sortField)) {
            if ("sortNum".equals(sortField)) {
                query.orderByDesc(ShopGoodsEntity::getSortNum, ShopGoodsEntity::getId);
            } else if ("price_desc".equals(sortField)) {
                query.orderByDesc(ShopGoodsEntity::getPrice, ShopGoodsEntity::getSortNum, ShopGoodsEntity::getId);
            } else if ("price_asc".equals(sortField)) {
                query.orderByAsc(ShopGoodsEntity::getPrice).orderByDesc(ShopGoodsEntity::getSortNum, ShopGoodsEntity::getId);
            }
        }
        IPage<ShopGoodsEntity> page = this.page(
                new Query<ShopGoodsEntity>().getPage(params),
                query
        );

        return new PageUtils(page);
    }

}