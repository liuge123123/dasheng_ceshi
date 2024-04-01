package com.tcc.modules.sys.dao;

import com.tcc.modules.sys.entity.SysDeptEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 系统部门表
 * 
 * @author 
 * @email 
 * @date 2021-01-06 14:52:45
 */
@Mapper
public interface SysDeptDao extends BaseMapper<SysDeptEntity> {
    /**
     * 关联dept——relation
     *
     * @return 数据列表
     */
    List<SysDeptEntity> selectDeptDtoList(Long  orgId);

    /**
     * 删除部门关系表数据
     * @param id 部门ID
     */
    void deleteDeptRealtion(String id);
}
