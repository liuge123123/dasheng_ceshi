package com.tcc.modules.exercise.controller;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

import com.tcc.common.annotation.SysLog;
import com.tcc.common.utils.DateUtils;
import com.tcc.modules.exercise.service.TreasureOrderService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tcc.modules.exercise.entity.TreasureEntity;
import com.tcc.modules.exercise.service.TreasureService;
import com.tcc.common.utils.PageUtils;
import com.tcc.common.utils.R;
import com.tcc.modules.base.AbstractBackController;



/**
 * 宝箱派送
 *
 * @author 
 * @email 
 * @date 2023-10-06 11:15:29
 */
@RestController
@RequestMapping("exercise/treasure")
public class TreasureController extends AbstractBackController{
    @Autowired
    private TreasureService treasureService;

    @Autowired
    private TreasureOrderService treasureOrderService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("exercise:treasure:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = treasureService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("exercise:treasure:info")
    public R info(@PathVariable("id") Integer id){
		TreasureEntity treasure = treasureService.getById(id);

        return R.ok().put("treasure", treasure);
    }

    /**
     * 保存
     */
    @SysLog("宝箱派送")
    @RequestMapping("/save")
    @RequiresPermissions("exercise:treasure:save")
    @Transactional(rollbackFor = Exception.class)
    public R save(@RequestBody TreasureEntity treasure){
        if(treasure.getAmount().compareTo(BigDecimal.ZERO)==-1 ||treasure.getAmount().compareTo(new BigDecimal("100000")) == 1){
            return R.error("宝箱总金额有误,宝箱总金额1-100000");
        }
        String uuid = UUID.randomUUID().toString();
        treasure.setCode(uuid.substring(0,8));
        treasure.setUid(getUserId());
        treasure.setCreateTime(DateUtils.getCurrentTime());
        treasure.setStatus(0);
		treasureService.save(treasure);
        if(treasure.getType().equals(1)){//固定金额
            treasureOrderService.setFixedPacket(treasure.getId(),treasure.getAmount(),treasure.getNumber());
        }else{//随机金额
            treasureOrderService.setRandomPacket(treasure.getId(),treasure.getAmount(),treasure.getNumber());
        }
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("exercise:treasure:update")
    public R update(@RequestBody TreasureEntity treasure){
		treasureService.updateById(treasure);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("exercise:treasure:delete")
    public R delete(@RequestBody Integer[] ids){
		treasureService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
