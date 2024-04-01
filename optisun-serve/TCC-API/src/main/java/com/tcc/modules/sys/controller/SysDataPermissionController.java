package com.tcc.modules.sys.controller;

import com.tcc.common.utils.R;
import com.tcc.modules.base.AbstractBackController;
import com.tcc.modules.sys.service.SysDataPermissionService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;


/**
 * 用户数据权限
 *
 * @author 
 * @email 
 * @date 2021-03-22 12:01:13
 */
@RestController
@RequestMapping("sys/sysdatapermission")
public class SysDataPermissionController extends AbstractBackController {

    @Autowired
    private SysDataPermissionService sysDataPermissionService;

    @GetMapping("/tree")
    @ApiOperation("获取用户数据权限数据")
    public R tree(){
        Long orgId = getOrgId();
        long userId = getUserId();
//        Long  orgId = 1;
//        long userId = 1;
        List<Map<String, Object>> list = sysDataPermissionService.getTreeByUserId(orgId, userId);
        return R.ok().put("list", list);
    }

    @GetMapping("/userListTree")
    @ApiOperation("获取用户数据权限数据")
    public R userListTree(){
         long userId = (getUser().getType()==0||getUser().getType()==1)?0:getUserId();
         return R.ok().put("list",sysDataPermissionService.getTreeUserByUserId(getOrgId(),userId,getUser().getRelationId()));
    }

    /***
     * 获取我的权限部门
     * @return
     */
    @GetMapping("/treeDept")
    @ApiOperation("获取用权限部门数据")
    public R getTreeDept(){
        long orgId = getOrgId();
        long userId = getUserId();
        List<Map<String, Object>> list = sysDataPermissionService.getTreeDeptByUserId(orgId, userId);
        return R.ok().put("list", list);
    }

}
