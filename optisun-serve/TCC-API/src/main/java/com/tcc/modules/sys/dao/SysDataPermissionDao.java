package com.tcc.modules.sys.dao;

import com.tcc.modules.sys.entity.SysDataPermissionEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 用户数据权限
 * 
 * @author 
 * @email 
 * @date 2021-03-22 12:01:13
 */
@Mapper
public interface SysDataPermissionDao extends BaseMapper<SysDataPermissionEntity> {

    /**
     * 获取用户管辖的所有部门和员工
     * @param userId
     * @return
     */
    List<Map<String, Object>> getByUserId(long userId);

    /**
     * 获取用户管辖的部门
     * @param userId
     * @return
     */
    List<Map<String, Object>> getDeptPermission(long userId);

    /**
     * 获取用户管辖的员工
     * @param userId
     * @return
     */
    List<Map<String, Object>> getUserPermission(long userId);

    /***
     * 获取员工的下级人员
     * @param params
     * @return
     */
    List getChildList(@Param("condition") Map<String, Object> params);

}
