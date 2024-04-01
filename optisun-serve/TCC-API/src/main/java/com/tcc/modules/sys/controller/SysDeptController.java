package com.tcc.modules.sys.controller;

import cn.hutool.core.convert.Convert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tcc.common.utils.DateUtils;
import com.tcc.common.utils.R;
import com.tcc.common.utils.TreeUtil;
import com.tcc.modules.base.AbstractBackController;
import com.tcc.common.base.dto.DeptTree;
import com.tcc.modules.sys.entity.SysDeptEntity;
import com.tcc.modules.sys.service.SysDeptService;
import com.tcc.modules.sys.service.SysUserService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 系统部门表
 *
 * @author 
 * @email 
 * @date 2021-01-06 14:52:45
 */
@RestController
@RequestMapping("sys/sysdept")
public class SysDeptController extends AbstractBackController {

    @Autowired
    private SysDeptService sysDeptService;

    @Autowired
    private SysUserService sysUserService;

    /**
     * 返回树形集合
     *
     * @return 树形
     */
    @GetMapping(value = "/tree")
    public R getTree() {
        SysDeptEntity condition = new SysDeptEntity();
        condition.setDelFlag("0");
        condition.setOrgId(getOrgId());
        return R.ok().put("data", this.getDeptTree(sysDeptService.list(new QueryWrapper<>(condition).orderByDesc("sort")), "0"));
    }

    /**
     * 返回树形集合(新增、修改)
     *
     * @return 树形
     */
    @GetMapping(value = "/selectTree")
    public R selectTree() {
        Long  orgId = getOrgId();
        SysDeptEntity condition = new SysDeptEntity();
        condition.setDelFlag("0");
        condition.setOrgId(orgId);
        List<SysDeptEntity> list = sysDeptService.list(new QueryWrapper<>(condition).orderByDesc("sort"));
        if (list.size() == 0) {
            // 当不存在部门，添加顶级菜单
            SysDeptEntity root = new SysDeptEntity();
            root.setDeptId(-1);
            root.setName("顶级部门");
            root.setParentId(0);
            list.add(0, root);
        }
        return R.ok().put("data", this.getDeptTree(list, "0"));
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{deptId}")
    @RequiresPermissions("sys:sysdept:info")
    public R info(@PathVariable("deptId") String deptId) {
        SysDeptEntity sysDept = sysDeptService.getById(deptId);
        return R.ok().put("sysDept", sysDept);
    }

    /**
     * 保存
     */
    @Transactional(rollbackFor = Exception.class)
    @RequestMapping("/save")
    @RequiresPermissions("sys:sysdept:save")
    public R save(@RequestBody SysDeptEntity sysDept) {
        if(sysDept.getParentId() == -1){
            sysDept.setParentId(0);
            sysDept.setParentIds("/0/");
        }else{
            sysDept.setParentId(sysDept.getParentId());
            SysDeptEntity parentDept = sysDeptService.getById(sysDept.getParentId());
            sysDept.setParentIds(parentDept.getParentIds() + parentDept.getDeptId() + "/");
        }
        sysDept.setCreateTime(DateUtils.getCurrentTime());
        sysDept.setCreateUserId(getUserId());
        sysDept.setDelFlag("0");
        sysDept.setOrgId(getOrgId());
        sysDeptService.saveDept(sysDept, true);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("sys:sysdept:update")
    public R update(@RequestBody SysDeptEntity sysDept) {
        sysDept.setOrgId(getOrgId());
        sysDeptService.updateDept(sysDept, true);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("sys:sysdept:delete")
    public R delete(@RequestBody String[] deptIds) {
        for (String deptId : deptIds) {
            sysDeptService.delDept(deptId, true);
        }
        return R.ok();
    }

    /**
     * 构建部门树
     *
     * @param depts 部门
     * @param root  根节点
     * @return
     */
    private List<DeptTree> getDeptTree(List<SysDeptEntity> depts, String root) {
        List<DeptTree> trees = new ArrayList<>();
        for (SysDeptEntity dept : depts) {
            DeptTree node = new DeptTree();
            node.setId(Convert.toStr(dept.getDeptId()));
            node.setParentId(Convert.toStr(dept.getParentId()));
            node.setName(dept.getName());
            node.setType(0);
            trees.add(node);
        }
        return TreeUtil.bulid(trees, root);
    }

    /***
     * 获取所有部门及部门下的人员
     * @return
     */
    @GetMapping(value = "/getAllDeptPersonTree")
    public R getAllDeptPersonTree(Boolean isContainMy) {
        //查询部门信息
        SysDeptEntity condition = new SysDeptEntity();
        condition.setDelFlag("0");
        condition.setOrgId(getOrgId());
        List<SysDeptEntity> depts = sysDeptService.list(new QueryWrapper<>(condition).orderByDesc("sort"));
        //查询我下面的部门的人员信息
        Map<String, Object> params = new HashMap<>();
        params.put("deptIdList", null);
        params.put("orgId", getOrgId());
        //是否包含自己
        if (!isContainMy) {
            params.put("noContainMy", getUserId());
        }
        List<Map<String, Object>> myPersonList = sysUserService.getUsersByCondition(params).stream().map(obj -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", obj.getUserId());
            map.put("userId", obj.getUserId());
            map.put("name", obj.getName());
            map.put("deptId", obj.getDeptId());
            map.put("type", 1);
            return map;
        }).collect(Collectors.toList());
        List<DeptTree> list = this.getDeptPersonTree(depts, myPersonList, "0", 0);
        return R.ok().put("data", list);
    }

    /***
     * 获取部门下的人员
     * @return
     */
    @GetMapping(value = "/getDeptPersonTree")
    public R getDeptPerson(Integer enableNoneDept, Boolean isContainMy) {
        if (enableNoneDept == null) {
            enableNoneDept = 0;
        }
        //获取我所在的部门
        Integer deptId = getUser().getDeptId();
        if(deptId == null || deptId == 0){
            return R.ok().put("data", new ArrayList<>());
        }
        SysDeptEntity dept = sysDeptService.getById(deptId);
        //获取我所在的部门及下级部门id
        List<Map<String, Object>> deptList = sysDeptService.listMaps(new QueryWrapper<SysDeptEntity>()
                .likeRight("parent_ids", dept.getParentIds())
                .eq("org_id", getOrgId())
                .select("dept_id as deptId"));
        List<String> deptArray = new ArrayList<>();
        for (Map<String, Object> item : deptList) {
            deptArray.add(Convert.toStr(item.get("deptId")));
        }
        //查询部门信息
        SysDeptEntity condition = new SysDeptEntity();
        condition.setDelFlag("0");
        condition.setOrgId(getOrgId());
        List<SysDeptEntity> depts = sysDeptService.list(new QueryWrapper<>(condition).in(deptArray.size() > 0, "dept_id", deptArray).orderByDesc("sort"));
        //查询我下面的部门的人员信息
        Map<String, Object> params = new HashMap<>();
        if (deptList.size() > 0) {
            params.put("deptIdList", deptArray);
        } else {
            params.put("deptIdList", null);
        }
        params.put("orgId", getOrgId());
        //是否包含自己
        if (isContainMy == null || !isContainMy) {
            params.put("noContainMy", getUserId());
        }
        List<Map<String, Object>> myPersonList = sysUserService.getUsersByCondition(params).stream().map(obj -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", obj.getUserId());
            map.put("userId", obj.getUserId());
            map.put("name", obj.getName());
            map.put("deptId", obj.getDeptId());
            map.put("type", 1);
            return map;
        }).collect(Collectors.toList());
        //获取我所在部门的父节点id
        SysDeptEntity deptEntity = sysDeptService.getById(deptId);
        int deptParentId = 0;
        if (deptEntity != null) {
            deptParentId = deptEntity.getParentId();
        }
        List<DeptTree> list = this.getDeptPersonTree(depts, myPersonList, Convert.toStr(deptParentId), enableNoneDept);
        return R.ok().put("data", list);

    }

    /**
     * 构建部门人员树
     *
     * @param depts        部门
     * @param myPersonList 人员列表
     * @param root         根节点
     * @return
     * @enableNoneDept 为0时，当部门无员工时禁止选择，为1时，部门无员工可以选择
     */
    private List<DeptTree> getDeptPersonTree(List<SysDeptEntity> depts, List<Map<String, Object>> myPersonList, String root, int enableNoneDept) {
        List<DeptTree> trees = new ArrayList<>();
        DeptTree node;
        for (SysDeptEntity dept : depts) {
            if (dept.getParentId().equals(dept.getDeptId())) {
                continue;
            }
            node = new DeptTree();
            node.setId(Convert.toStr(dept.getDeptId()));
            node.setParentId(Convert.toStr(dept.getParentId()));
            node.setName(dept.getName());
            node.setType(0);
            node.setDisabled(enableNoneDept == 0);
            //部门下面的人
            List<Map<String, Object>> seachList = new ArrayList<>();
            for (Map<String, Object> item : myPersonList) {
                Integer deptId = Convert.toInt(item.get("deptId"));
                if (deptId != null && deptId.equals(dept.getDeptId())) {
                    seachList.add(item);
                    node.setDisabled(false);
                }
            }
            trees.add(node);
            for (Map<String, Object> item : seachList) {
                node = new DeptTree();
                node.setId(Convert.toStr(item.get("userId")));
                node.setParentId(Convert.toStr(dept.getDeptId()));
                node.setName(Convert.toStr(item.get("name")));
                node.setType(1);
                trees.add(node);
            }
        }
        return TreeUtil.bulid(trees, root);
    }
}
