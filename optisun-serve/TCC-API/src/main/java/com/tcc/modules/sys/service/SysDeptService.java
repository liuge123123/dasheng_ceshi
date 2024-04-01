package com.tcc.modules.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tcc.common.utils.PageUtils;
import com.tcc.modules.sys.entity.SysDeptEntity;

import java.util.Map;

/**
 * 系统部门表
 *
 * @author 
 * @email 
 * @date 2021-01-06 14:52:45
 */
public interface SysDeptService extends IService<SysDeptEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveDept(SysDeptEntity dept, boolean snycQywx);

    void updateDept(SysDeptEntity dept, boolean snycQywx);

    void delDept(String deptId, boolean snycQywx);

}

