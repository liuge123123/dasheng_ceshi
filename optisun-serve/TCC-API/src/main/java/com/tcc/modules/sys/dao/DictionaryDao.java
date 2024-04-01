package com.tcc.modules.sys.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tcc.modules.sys.entity.DictionaryEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 数据字典
 * 
 * @author 
 * @email 
 * @date 2018-07-30 20:13:21
 */
@Mapper
public interface DictionaryDao extends BaseMapper<DictionaryEntity> {

    List<DictionaryEntity> selAll(Map<String, Object> params);

    /**
     * @param orgId
     * @param parentCode
     * @return
     */
    List<DictionaryEntity> selectByParentCode(Long  orgId, String parentCode);
}
