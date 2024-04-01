

package com.tcc.modules.app.controller;


import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.tcc.common.utils.Constant;
import com.tcc.common.utils.IPUtils;
import com.tcc.common.utils.R;
import com.tcc.common.utils.RabbitMqUtils;
import com.tcc.modules.app.annotation.Login;
import com.tcc.modules.app.annotation.LoginUser;
import com.tcc.modules.app.entity.UserEntity;
import com.tcc.modules.sys.service.SysConfigService;
import com.tcc.modules.sys.service.SysDataPermissionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.Map;

/**
 * APP测试接口
 *
 */
@Slf4j
@RestController
@RequestMapping("/app")
@Api("APP测试接口")
public class AppTestController {

    @Autowired
    private RabbitMqUtils rabbitMqUtils;

    @Autowired
    private SysDataPermissionService sysDataPermissionService;

    @Autowired
    private SysConfigService sysConfigService;



    @Login
    @GetMapping("userInfo")
    @ApiOperation("获取用户信息")
    public R userInfo(@ApiIgnore @LoginUser UserEntity user){
        return R.ok().put("user", user);
    }

    @Login
    @GetMapping("userId")
    @ApiOperation("获取用户ID")
    public R userInfo(@ApiIgnore @RequestAttribute("userId") Integer userId){
        return R.ok().put("userId", userId);
    }

    @GetMapping("notToken")
    @ApiOperation("忽略Token验证测试")
    public R notToken(){
        log.debug("debug级别日志");
        return R.ok().put("msg", "无需token也能访问。。。");
    }

    @GetMapping("sendMsg")
    @ApiOperation("发送测试信息")
    public R sendMsg(String queue, String msg){
        rabbitMqUtils.sendMsg(queue, msg);
        return R.ok();
    }


    @GetMapping("premission")
    @ApiOperation("获取后台用户权限")
    public R premission(Long  orgId, int userId){
        List<Map<String, Object>> list = sysDataPermissionService.getTreeByUserId(orgId, userId);
        return R.ok().put("list", list);
    }


    @PostMapping("/test")
    public String test(@RequestBody JSONObject test){
        System.out.println("test = " + test);
        JSONObject r = new JSONObject();
        r.putOpt("id", "5531492193");
        r.putOpt("is_bot", true);
        r.putOpt("first_name", "lzz123");


        JSONArray btns = new JSONArray();
        JSONObject btn1 = new JSONObject();
        btn1.putOpt("text", "11");
        btns.put(btn1);

        r.putOpt("inline_keyboard", btn1);
        return r.toString();
    }

    @ApiOperation("检测IP是否在黑名单")
    @GetMapping("/testIp")
    public R testIp(String ip){
        String[] ipBlackArr = getConfigArr("ip.black.config");
        boolean ipBlackFlag = IPUtils.isInRanges(ip, ipBlackArr);
        return R.ok().put("ipBlackFlag", ipBlackFlag);
    }

    private String[] getConfigArr(String key) {
        String configStr = sysConfigService.getValue(Constant.SUPER_ORG, key);
        if (StrUtil.isNotBlank(configStr)) {
            configStr = configStr.replaceAll(" ", "");
            configStr = configStr.replaceAll(",\\n", ",");
            configStr = configStr.replaceAll("\\n", ",");
            return configStr.split("[,，]");
        }
        return new String[]{};
    }
}
