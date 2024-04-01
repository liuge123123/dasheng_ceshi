package com.tcc.modules.l.controller;

import java.util.Arrays;
import java.util.Map;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.tcc.common.annotation.SysLog;
import com.tcc.common.utils.DateUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tcc.modules.l.entity.LOrderEntity;
import com.tcc.modules.l.service.LOrderService;
import com.tcc.common.utils.PageUtils;
import com.tcc.common.utils.R;



/**
 * 
 *
 * @author 
 * @email 
 * @date 2022-08-30 13:52:53
 */
@RestController
@RequestMapping("l/lorder")
public class LOrderController {
    @Autowired
    private LOrderService lOrderService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("l:lorder:list")
    public R list(@RequestParam Map<String, Object> params){
        String teamList = cn.hutool.core.convert.Convert.toStr(params.get("team"));
        if(StrUtil.isNotBlank(teamList)){
            String[] checkList =  teamList.split(",");
            params.put("team",checkList);
        }
        else{
            return R.error("请选择人员");
        }
        PageUtils page = lOrderService.queryPage(params);
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("l:lorder:info")
    public R info(@PathVariable("id") Long id){
		LOrderEntity lOrder = lOrderService.getById(id);

        return R.ok().put("lOrder", lOrder);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("l:lorder:save")
    public R save(@RequestBody LOrderEntity lOrder){
		lOrderService.save(lOrder);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("l:lorder:update")
    public R update(@RequestBody LOrderEntity lOrder){
		lOrderService.updateById(lOrder);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("l:lorder:delete")
    public R delete(@RequestBody Long[] ids){
		lOrderService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    /**
     * 后台锁单
     * @param lOrder
     * @return
     */
    /*@SysLog("基金后台锁单")
    @RequestMapping("/buy")
    @RequiresPermissions("l:lorder:buy")
    public R buy(@RequestBody LOrderEntity lOrder){
        lOrderService.buy(lOrder.getCustId(), lOrder.getGoodsId());
        return R.ok();
    }*/

    @SysLog("基金后台锁单")
    @RequestMapping("/buyGive")
    @RequiresPermissions("l:lorder:buy")
    public R buyGive(@RequestBody LOrderEntity lOrder){
        lOrderService.buyGive(lOrder.getCustId(), lOrder.getGoodsId());
        return R.ok();
    }

    @SysLog("基金后台退单")
    @RequestMapping("/quit")
    @RequiresPermissions("l:lorder:quit")
    public R quit(@RequestBody Map<String, Object> params){
//        Long orderId = MapUtil.getLong(params, "orderId");
//        String cancelRemark = MapUtil.getStr(params, "cancelRemark");
//        int lockDay = MapUtil.getInt(params, "lockDay");
//        lOrderService.quit(orderId, cancelRemark, lockDay);
        return R.ok();
    }

    @SysLog("基金后台退单")
    @RequestMapping("/quitnew")
    @RequiresPermissions("l:lorder:quit")
    public R quitnew(@RequestBody Map<String, Object> params){
        Long orderId = MapUtil.getLong(params, "orderId");
        String cancelRemark = MapUtil.getStr(params, "cancelRemark");
        int lockDay = MapUtil.getInt(params, "lockDay");
        int quitType = MapUtil.getInt(params, "quitType"); //0 退还本金 1 退还本金和领取收益
        lOrderService.quitnew(orderId, cancelRemark, quitType);
        return R.ok();
    }

    /**
     * 后台锁单
     * @param orderId
     * @return
     */
    @SysLog("基金后台暂停收益")
    @RequestMapping("/pasue")
    @RequiresPermissions("l:lorder:pasue")
    public R pasue(@RequestBody Integer orderId){
        lOrderService.update(new LambdaUpdateWrapper<LOrderEntity>()
                .eq(LOrderEntity::getId, orderId)
                .eq(LOrderEntity::getOrderStatus, 1)
                .set(LOrderEntity::getOrderStatus, 6)
        );
        return R.ok();
    }

    /**
     * 后台锁单
     * @param orderId
     * @return
     */
    @SysLog("基金后台开启收益")
    @RequestMapping("/open")
    @RequiresPermissions("l:lorder:open")
    public R open(@RequestBody Integer orderId){
        lOrderService.update(new LambdaUpdateWrapper<LOrderEntity>()
                .eq(LOrderEntity::getId, orderId)
                .eq(LOrderEntity::getOrderStatus, 6)
                .set(LOrderEntity::getOrderStatus, 1)
                .set(LOrderEntity::getReceiveTime, DateUtils.getCurrentTime())
        );
        return R.ok();
    }

}
