package com.tcc.modules.l.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tcc.common.utils.PageUtils;
import com.tcc.common.utils.R;
import com.tcc.modules.l.entity.LRoomEntity;
import com.tcc.modules.l.service.LRoomService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * 理财房间
 *
 * @author
 * @email
 * @date 2022-12-21 10:37:42
 */
@RestController
@RequestMapping("l/lroom")
public class LRoomController {
    @Autowired
    private LRoomService lRoomService;

    @RequestMapping("/all")
    public R all() {
        List<LRoomEntity> list = lRoomService.list(new LambdaQueryWrapper<LRoomEntity>().orderByAsc(LRoomEntity::getSort, LRoomEntity::getId));
        return R.ok().put("data", list);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("l:lroom:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = lRoomService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("l:lroom:info")
    public R info(@PathVariable("id") Integer id) {
        LRoomEntity lRoom = lRoomService.getById(id);

        return R.ok().put("lRoom", lRoom);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("l:lroom:save")
    public R save(@RequestBody LRoomEntity lRoom) {
        lRoomService.save(lRoom);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("l:lroom:update")
    public R update(@RequestBody LRoomEntity lRoom) {
        lRoomService.updateById(lRoom);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("l:lroom:delete")
    public R delete(@RequestBody Integer[] ids) {
        lRoomService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
