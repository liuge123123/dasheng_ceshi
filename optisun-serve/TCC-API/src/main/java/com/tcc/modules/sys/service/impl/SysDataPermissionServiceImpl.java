package com.tcc.modules.sys.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNode;
import cn.hutool.core.lang.tree.TreeUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tcc.modules.sys.dao.SysDataPermissionDao;
import com.tcc.modules.sys.entity.SysDataPermissionEntity;
import com.tcc.modules.sys.entity.SysDeptEntity;
import com.tcc.modules.sys.entity.SysUserEntity;
import com.tcc.modules.sys.entity.UserTree;
import com.tcc.modules.sys.service.SysDataPermissionService;
import com.tcc.modules.sys.service.SysDeptService;
import com.tcc.modules.sys.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
//import com.tcc.common.utils.TreeUtil;

import java.util.*;
import java.util.stream.Collectors;


@Service("sysDataPermissionService")
public class SysDataPermissionServiceImpl extends ServiceImpl<SysDataPermissionDao, SysDataPermissionEntity> implements SysDataPermissionService {

    @Autowired
    private SysDeptService sysDeptService;

    @Autowired
    private SysUserService sysUserService;

    @Override
    public List<Map<String, Object>> getByUserId(long  orgId, long userId) {
        List<Map<String, Object>> list = baseMapper.getByUserId(userId);
        return list;
    }

    /***
     * 获取部门数据权限
     * @param orgId
     * @param userId
     * @return
     */
    @Override
    public  List<Map<String,Object>> getTreeDeptByUserId(long  orgId, long userId){
        // 直接管辖部门
        List<Map<String, Object>> deptList = baseMapper.getDeptPermission(userId);
        if(deptList.size() == 0){
           return  null;
        }
        // 所有部门
        List<Map<String, Object>> allDeptList = sysDeptService.list(new QueryWrapper<SysDeptEntity>()
                .eq("org_id", orgId)
                .eq("del_flag", 0))
                .stream().map(item -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", item.getDeptId());
                    map.put("name", item.getName());
                    map.put("parentId", item.getParentId());
                    map.put("type", 2);
                    return map;
                }).collect(Collectors.toList());
        // 管辖的部门及下级部门
        deptList.forEach(item -> {
            item.put("children", getChildrenDept(item, allDeptList));
        });

        //部门去重
        List<String> deptIds = new ArrayList<>();
        getDeptIds(deptList, deptIds);
        Map<String, Integer> idM = new HashMap<>();
        Iterator<String> it = deptIds.iterator();
        while (it.hasNext()){
            String id = it.next();
            if(idM.containsKey(id)){
                int count = idM.get(id) + 1;
                idM.put(id, count);
                it.remove();
            }else{
                idM.put(id, 0);
            }
        }
        Iterator<Map<String, Object>> deptIt = deptList.iterator();
        while (deptIt.hasNext()){
            Map<String, Object> dept = deptIt.next();
            if(idM.get(Convert.toStr(dept.get("id"))) > 0){
                deptIt.remove();
            }
        }
        return  deptList;
    }



    @Override
    public List<Map<String, Object>> getTreeByUserId(long orgId, long userId) {
        // 直接管辖人员
        List<Map<String, Object>> userList = baseMapper.getUserPermission(userId);
        // 直接管辖人员包含自己
        SysUserEntity self = sysUserService.getById(userId);
        Map<String, Object> selfFormat = new HashMap<>();
        selfFormat.put("id", self.getUserId());
        selfFormat.put("name", self.getName());
        selfFormat.put("deptId", self.getDeptId());
        selfFormat.put("type", 1);
        userList.add(selfFormat);

        // 直接管辖部门
        List<Map<String, Object>> deptList = baseMapper.getDeptPermission(userId);
        if(deptList.size() == 0){
            deptList.addAll(userList);
        }else {
            // 所有部门
            List<Map<String, Object>> allDeptList = sysDeptService.list(new QueryWrapper<SysDeptEntity>()
                    .eq("org_id", orgId)
                    .eq("del_flag", 0))
                    .stream().map(item -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("id", item.getDeptId());
                        map.put("name", item.getName());
                        map.put("parentId", item.getParentId());
                        map.put("type", 2);
                        return map;
                    }).collect(Collectors.toList());

            // 管辖的部门及下级部门
            deptList.forEach(item -> {
                item.put("children", getChildrenDept(item, allDeptList));
            });

            // 管辖部门ID
            List<String> deptIds = new ArrayList<>();
            getDeptIds(deptList, deptIds);

            //部门去重
            Map<String, Integer> idM = new HashMap<>();
            Iterator<String> it = deptIds.iterator();
            while (it.hasNext()){
                String id = it.next();
                if(idM.containsKey(id)){
                    int count = idM.get(id) + 1;
                    idM.put(id, count);
                    it.remove();
                }else{
                    idM.put(id, 0);
                }
            }
            Iterator<Map<String, Object>> deptIt = deptList.iterator();
            while (deptIt.hasNext()){
                Map<String, Object> dept = deptIt.next();
                if(idM.get(Convert.toStr(dept.get("id"))) > 0){
                    deptIt.remove();
                }
            }

            // 管辖部门员工
            Map<String, Object> params = new HashMap<>();
            params.put("orgId", orgId);
            params.put("deptIdList", deptIds);
            List<Map<String, Object>> users =  sysUserService.getUsersByCondition(params).stream().map(obj -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", obj.getUserId());
                map.put("name", obj.getName());
                map.put("deptId", obj.getDeptId());
                map.put("type", 1);
                return map;
            }).collect(Collectors.toList());

            // 管辖部门员工与直接管辖员工合并
            users.forEach(item1 -> {
                boolean isExist = false;
                for (int i = 0; i < userList.size(); i++) {
                    Map<String, Object> item2 = userList.get(i);
                    if (item1.get("id").equals(item2.get("id"))) {
                        isExist = true;
                        break;
                    }
                }
                if (!isExist) {
                    userList.add(item1);
                }
            });

            // 将员工设置到对应部门树
            setDeptUser(deptList, userList);

            // 将不存在管辖部门的员工添加到部门树
            userList.forEach(item -> {
                boolean hasDept = Convert.toBool(item.get("hasDept"), false);
                if (!hasDept) {
                    deptList.add(item);
                }
            });
        }
        return deptList;
    }

    @Override
    public List<Long> getManageUserIds(long orgId, long userId) {
        // 直接管辖人员
        List<Map<String, Object>> userList = baseMapper.getUserPermission(userId);
        // 直接管辖人员包含自己
        SysUserEntity self = sysUserService.getById(userId);
        Map<String, Object> selfFormat = new HashMap<>();
        selfFormat.put("id", self.getUserId());
        selfFormat.put("name", self.getName());
        selfFormat.put("deptId", self.getDeptId());
        selfFormat.put("type", 1);
        userList.add(selfFormat);

        // 直接管辖部门
        List<Map<String, Object>> deptList = baseMapper.getDeptPermission(userId);
        if(deptList.size() > 0){
            // 所有部门
            List<Map<String, Object>> allDeptList = sysDeptService.list(new QueryWrapper<SysDeptEntity>()
                    .eq("org_id", orgId)
                    .eq("del_flag", 0))
                    .stream().map(item -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("id", item.getDeptId());
                        map.put("name", item.getName());
                        map.put("parentId", item.getParentId());
                        map.put("type", 2);
                        return map;
                    }).collect(Collectors.toList());

            // 管辖的部门及下级部门
            deptList.forEach(item -> {
                item.put("children", getChildrenDept(item, allDeptList));
            });

            // 管辖部门ID
            List<String> deptIds = new ArrayList<>();
            getDeptIds(deptList, deptIds);

            //部门去重
            Map<String, Integer> idM = new HashMap<>();
            Iterator<String> it = deptIds.iterator();
            while (it.hasNext()){
                String id = it.next();
                if(idM.containsKey(id)){
                    int count = idM.get(id) + 1;
                    idM.put(id, count);
                    it.remove();
                }else{
                    idM.put(id, 0);
                }
            }
            Iterator<Map<String, Object>> deptIt = deptList.iterator();
            while (deptIt.hasNext()){
                Map<String, Object> dept = deptIt.next();
                if(idM.get(Convert.toStr(dept.get("id"))) > 0){
                    deptIt.remove();
                }
            }

            // 管辖部门员工
            Map<String, Object> params = new HashMap<>();
            params.put("orgId", orgId);
            params.put("deptIdList", deptIds);
            List<Map<String, Object>> users = sysUserService.getUsersByCondition(params).stream().map(obj -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", obj.getUserId());
                map.put("name", obj.getName());
                map.put("deptId", obj.getDeptId());
                map.put("type", 1);
                return map;
            }).collect(Collectors.toList());

            // 管辖部门员工与直接管辖员工合并
            users.forEach(item1 -> {
                boolean isExist = false;
                for (int i = 0; i < userList.size(); i++) {
                    Map<String, Object> item2 = userList.get(i);
                    if (item1.get("id").equals(item2.get("id"))) {
                        isExist = true;
                        break;
                    }
                }
                if (!isExist) {
                    userList.add(item1);
                }
            });
        }


        return userList.stream().map(item -> {
            return Convert.toLong(item.get("id"));
        }).collect(Collectors.toList());
    }

    /**
     * 获取部门的子部门
     * @param dept
     * @param allDept
     * @return
     */
    private List<Map<String, Object>> getChildrenDept(Map<String, Object> dept, List<Map<String, Object>> allDept) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i = 0; i < allDept.size(); i++) {
            Map<String, Object> item = allDept.get(i);
            if (dept.get("id").equals(item.get("parentId"))) {
                item.put("children", getChildrenDept(item, allDept));
                list.add(item);
            }
        }
        return list;
    }

    /**
     * 获取所有部门id
     * @param deptList
     * @param resultList
     */
    private void getDeptIds(List<Map<String, Object>> deptList, List<String> resultList) {
        deptList.forEach(item -> {
            resultList.add(Convert.toStr(item.get("id")));
            List<Map<String, Object>> children = (List<Map<String, Object>>) item.get("children");
            getDeptIds(children, resultList);
        });
    }

    /**
     * 将员工划分到部门
     * @param deptList
     * @param userList
     */
    private void setDeptUser(List<Map<String, Object>> deptList, List<Map<String, Object>> userList){
        deptList.forEach(item -> {
            List<Map<String, Object>> users = findByDeptId(item.get("id"), userList);
            List<Map<String, Object>> children = (List<Map<String, Object>>) item.get("children");
            if(children == null){
                children = new ArrayList<>();
            }else{
                setDeptUser(children, userList);
            }
            if(users!= null && users.size() > 0){
                children.addAll(users);
            }
        });
    }

    /**
     * 获取部门员工
     * @param deptId
     * @param userList
     * @return
     */
    private List<Map<String, Object>> findByDeptId(Object deptId, List<Map<String, Object>> userList){
        return userList.stream().filter(item -> {
            boolean hasDept = deptId.equals(item.get("deptId")) ;
            if(!Convert.toBool(item.get("hasDept"), false) && hasDept) {
                item.put("hasDept", true);
            }
            return hasDept;
        }).collect(Collectors.toList());
    }

    /**
     * 获取树形列表
     * @param orgId
     * @param userId
     * @return
     */
    @Override
    public List<Tree<String>> getTreeUserByUserId(long orgId, long userId,long parentId) {
        //用户类型，0:超级管理员，1:平台账号，2:总代理账号;3:分代理账号；4普通员工账号

        Map<String,Object> params = new HashMap<>();
        params.put("orgId",orgId);
        params.put("userId",userId);

        List<TreeNode<String>> trees = CollUtil.newArrayList();

        List<Map<String,Object>> list = this.baseMapper.getChildList(params);
        for (Map<String,Object> item:list) {
            TreeNode node = new TreeNode();
            node.setId(Convert.toStr(item.get("id")));
            node.setParentId(Convert.toStr(item.get("parentId")));
            int userType = Convert.toInt(item.get("type"));
            //扩展参数
            Map<String,Object> userTypeParams = new HashMap<>();
            userTypeParams.put("userType",userType);
            node.setExtra(userTypeParams);
            String userTypeStr="";
            if(userType==0){
                userTypeStr="超级管理员";
            }
            else if(userType==1){
                userTypeStr="平台";
            }
            else if(userType==2){
                userTypeStr="总代理";
            }
            else if(userType==3){
                userTypeStr="分代理";
            }
            else if(userType==4){
                userTypeStr="员工";
            }
            node.setName(Convert.toStr(item.get("username"))+"-"+item.get("id")+"-"+userTypeStr);
            trees.add(node);
        }
       return TreeUtil.build(trees,Convert.toStr(parentId));
    }
}