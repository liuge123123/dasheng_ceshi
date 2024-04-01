package com.tcc.modules.content.dao;

import com.tcc.modules.content.entity.ArticleEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文章
 * 
 * @author 
 * @email 
 * @date 2022-05-30 17:05:32
 */
@Mapper
public interface ArticleDao extends BaseMapper<ArticleEntity> {
	
}
