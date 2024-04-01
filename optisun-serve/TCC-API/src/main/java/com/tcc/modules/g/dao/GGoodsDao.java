package com.tcc.modules.g.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tcc.modules.g.entity.GGoodsEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 商品表
 *
 * @author
 * @email
 * @date 2022-07-21 17:55:20
 */
@Mapper
public interface GGoodsDao extends BaseMapper<GGoodsEntity> {
    List<Map<String,Object>> selectByCondition(IPage<Map<String,Object>> page, @Param("condition") Map<String, Object> params);
}
