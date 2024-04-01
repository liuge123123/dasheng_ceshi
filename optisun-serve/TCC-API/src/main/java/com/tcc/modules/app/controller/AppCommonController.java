package com.tcc.modules.app.controller;

import com.tcc.common.utils.R;
import com.tcc.modules.base.AbstractAppController;
import com.tcc.modules.sys.service.SysConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/app/common")
@Api("APP共通接口")
public class AppCommonController extends AbstractAppController {

    @Autowired
    private SysConfigService sysConfigService;

    @ApiOperation("获取配置")
    @GetMapping("/config")
    public R getConfig(String key){
        return R.ok().putData(sysConfigService.getValue(getOrgId(), key));
    }

}
