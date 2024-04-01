package com.tcc.modules.shop.service.impl;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tcc.common.utils.PageUtils;
import com.tcc.common.utils.Query;

import com.tcc.modules.shop.dao.ShopCustCouponDao;
import com.tcc.modules.shop.entity.ShopCustCouponEntity;
import com.tcc.modules.shop.service.ShopCustCouponService;


@Service("shopCustCouponService")
public class ShopCustCouponServiceImpl extends ServiceImpl<ShopCustCouponDao, ShopCustCouponEntity> implements ShopCustCouponService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        Long custId = MapUtil.getLong(params, "custId");
        Integer useStatus = MapUtil.getInt(params, "useStatus");
        String team =  MapUtil.getStr(params, "team");
        String[] teamArr = null;
        if(team != null){
            teamArr = team.split(",");
        }
        IPage<ShopCustCouponEntity> page = this.page(
                new Query<ShopCustCouponEntity>().getPage(params),
                new LambdaQueryWrapper<ShopCustCouponEntity>()
                        .eq(custId != null, ShopCustCouponEntity::getCustId, custId)
                        .eq(useStatus != null, ShopCustCouponEntity::getUseStatus, useStatus)
                        .in(teamArr != null, ShopCustCouponEntity::getSalesmanId, teamArr)
        );

        return new PageUtils(page);
    }

}