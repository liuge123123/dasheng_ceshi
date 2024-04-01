package com.tcc.modules.sys.service;

import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNode;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tcc.modules.sys.entity.SysDataPermissionEntity;
import com.tcc.modules.sys.entity.UserTree;

import java.util.List;
import java.util.Map;

/**
 * 用户数据权限
 *
 * @author 
 * @email 
 * @date 2021-03-22 12:01:13
 */
public interface SysDataPermissionService extends IService<SysDataPermissionEntity> {

    List<Map<String, Object>> getByUserId(long orgId, long userId);

    List<Map<String, Object>> getTreeByUserId(long orgId, long userId);

    List<Long> getManageUserIds(long orgId, long userId);

    /***
     * 获取部门数据权限
     * @param orgId
     * @param userId
     * @return
     */
    List<Map<String,Object>> getTreeDeptByUserId(long orgId, long userId);

    /**
     * 获取树形列表
     * @param orgId
     * @param userId
     * @return
     */
    List<Tree<String>>  getTreeUserByUserId(long orgId, long userId,long parentId);
}

