package com.tcc.modules.shop.controller;

import java.util.Arrays;
import java.util.Map;

import cn.hutool.core.date.DateUtil;
import com.tcc.common.exception.WTDPException;
import com.tcc.common.utils.DateUtils;
import com.tcc.modules.cust.entity.CustEntity;
import com.tcc.modules.cust.service.CustService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tcc.modules.shop.entity.ShopCustCouponEntity;
import com.tcc.modules.shop.service.ShopCustCouponService;
import com.tcc.common.utils.PageUtils;
import com.tcc.common.utils.R;



/**
 * 客户折扣券表
 *
 * @author 
 * @email 
 * @date 2023-02-21 08:58:33
 */
@RestController
@RequestMapping("shop/shopcustcoupon")
public class ShopCustCouponController {
    @Autowired
    private ShopCustCouponService shopCustCouponService;

    @Autowired
    private CustService custService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("shop:shopcustcoupon:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = shopCustCouponService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("shop:shopcustcoupon:info")
    public R info(@PathVariable("id") Long id){
		ShopCustCouponEntity shopCustCoupon = shopCustCouponService.getById(id);

        return R.ok().put("shopCustCoupon", shopCustCoupon);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("shop:shopcustcoupon:save")
    public R save(@RequestBody ShopCustCouponEntity shopCustCoupon){
        CustEntity cust = custService.getById(shopCustCoupon.getCustId());
        if(cust == null){
            throw new WTDPException("客户ID输入错误，该客户不存在");
        }else{
            shopCustCoupon.setSalesmanId(cust.getSalesmanId());
            shopCustCoupon.setIsNb(cust.getIsNb());
            shopCustCoupon.setCreateTime(DateUtils.getCurrentTime());
            shopCustCouponService.save(shopCustCoupon);
        }
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("shop:shopcustcoupon:update")
    public R update(@RequestBody ShopCustCouponEntity shopCustCoupon){
		shopCustCouponService.updateById(shopCustCoupon);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("shop:shopcustcoupon:delete")
    public R delete(@RequestBody Long[] ids){
		shopCustCouponService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
