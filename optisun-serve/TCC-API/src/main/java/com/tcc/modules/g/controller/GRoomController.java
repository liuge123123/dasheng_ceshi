package com.tcc.modules.g.controller;

import java.util.Arrays;
import java.util.Map;

import com.tcc.common.utils.DateUtils;
import com.tcc.common.utils.core.text.Convert;
import com.tcc.modules.base.AbstractBackController;
import com.tcc.modules.t.entity.TOrderEntity;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tcc.modules.g.entity.GRoomEntity;
import com.tcc.modules.g.service.GRoomService;
import com.tcc.common.utils.PageUtils;
import com.tcc.common.utils.R;



/**
 * 房间表
 *
 * @author
 * @email
 * @date 2022-07-21 17:55:20
 */
@RestController
@RequestMapping("g/groom")
public class GRoomController  extends AbstractBackController {
    @Autowired
    private GRoomService gRoomService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("g:groom:list")
    public R list(@RequestParam Map<String, Object> params){
        params.put("orgId",getOrgId());
        PageUtils page = gRoomService.selectByCondition(params);
        return R.ok().put("page", page);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("g:groom:info")
    public R info(@PathVariable("id") Long id){
		GRoomEntity gRoom = gRoomService.getById(id);
        return R.ok().put("gRoom", gRoom);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("g:groom:save")
    public R save(@RequestBody GRoomEntity gRoom){
        gRoom.setCreateBy(getUserId());
        gRoom.setCreateTime(DateUtils.getCurrentTime());
        gRoom.setDelFlag("0");
        gRoom.setOrgId(Convert.toLong(getOrgId()));
		gRoomService.save(gRoom);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("g:groom:update")
    public R update(@RequestBody GRoomEntity gRoom){
        gRoom.setUpdateBy(getUserId());
        gRoom.setUpdateTime(DateUtils.getCurrentTime());
		gRoomService.updateById(gRoom);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("g:groom:delete")
    public R delete(@RequestBody Long[] ids){
        for(Long item : ids){
            GRoomEntity gRoomEntity = new GRoomEntity();
            gRoomEntity.setDelFlag("1");
            gRoomEntity.setDeleteTime(DateUtils.getCurrentTime());
            gRoomEntity.setId(item);
            gRoomService.updateById(gRoomEntity);
        }
        return R.ok();
    }

}
