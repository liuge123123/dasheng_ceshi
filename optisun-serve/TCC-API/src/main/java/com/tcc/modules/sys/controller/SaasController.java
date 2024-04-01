package com.tcc.modules.sys.controller;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tcc.common.utils.R;
import com.tcc.modules.sys.entity.SysMenuEntity;
import com.tcc.modules.sys.service.SaasService;
import com.tcc.modules.sys.service.SysMenuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @description: sass平台相关接口
 * @author 
 * @create: 2020-05-26 12:02
 **/
@Api("sass服务接口")
@RequestMapping("/sass")
@RestController
public class SaasController {

    @Autowired
    private SaasService saasService;

    @Autowired
    private SysMenuService sysMenuService;

    @ApiOperation("初始化数据")
    @PostMapping("/init")
    public R initData(@RequestBody JSONObject data) {
        saasService.validParmas(data);
        saasService.initData(data);
        return R.ok();
    }

    /**
     * 所有菜单列表
     */
    @ApiOperation("获取所有功能资源")
    @GetMapping("/res/all")
    public R list(@RequestParam Map<String, Object> params) {
        saasService.validParmas(JSONUtil.parseObj(params));
        List<SysMenuEntity> menuList = sysMenuService.list(new QueryWrapper<SysMenuEntity>()
                .select("menu_id", "name", "parent_id"));
        return R.ok().put("data", menuList);
    }
}
