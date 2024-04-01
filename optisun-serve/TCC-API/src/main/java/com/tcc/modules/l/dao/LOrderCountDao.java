package com.tcc.modules.l.dao;

import com.tcc.modules.l.entity.LOrderCountEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

/**
 *
 *
 * @author
 * @email
 * @date 2022-08-30 13:52:53
 */
@Mapper
public interface LOrderCountDao extends BaseMapper<LOrderCountEntity> {

    Integer getCountByLevel(Long custId, Long goodsLevel);
}
