package com.tcc.modules.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tcc.common.exception.WTDPException;
import com.tcc.common.utils.PageUtils;
import com.tcc.common.utils.Query;
import com.tcc.modules.sys.dao.SysDeptDao;
import com.tcc.modules.sys.entity.SysDeptEntity;
import com.tcc.modules.sys.entity.SysUserEntity;
import com.tcc.modules.sys.service.SysDeptService;
import com.tcc.modules.sys.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;


@Service("sysDeptService")
public class SysDeptServiceImpl extends ServiceImpl<SysDeptDao, SysDeptEntity> implements SysDeptService {

    @Autowired
    private SysDeptService sysDeptService;

    @Autowired
    private SysUserService sysUserService;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SysDeptEntity> page = this.page(
                new Query<SysDeptEntity>().getPage(params),
                new QueryWrapper<SysDeptEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveDept(SysDeptEntity dept, boolean snycQywx) {
        // 添加部门信息
        save(dept);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateDept(SysDeptEntity sysDept, boolean snycQywx) {
        sysDept.setParentId(sysDept.getParentId());
        // 更新部门信息
        updateById(sysDept);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delDept(String deptId, boolean snycQywx) {
        SysDeptEntity deptEntity =  getById(deptId);
        Long  orgId = deptEntity.getOrgId();
        if((deptEntity.getOrgId() + "__0").equals(deptEntity.getParentId())){
            throw new WTDPException("顶级部门不能删除");
        }
        int childrenCount = count(new QueryWrapper<SysDeptEntity>().eq("org_id", orgId).eq("parent_id", deptId));
        if(childrenCount > 0){
            throw new WTDPException("存在子部门不能删除");
        }
        int userCount = sysUserService.count(new QueryWrapper<SysUserEntity>().eq("org_id", orgId).eq("dept_id", deptId));
        if(userCount > 0){
            throw new WTDPException("存在员工不能删除");
        }
         deptEntity.setDelFlag("1");
         // 逻辑删除
         updateById(deptEntity);

    }

}