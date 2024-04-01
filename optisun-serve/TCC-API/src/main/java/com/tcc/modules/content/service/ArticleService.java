package com.tcc.modules.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tcc.common.utils.PageUtils;
import com.tcc.modules.content.entity.ArticleEntity;

import java.util.Map;

/**
 * 文章
 *
 * @author 
 * @email 
 * @date 2022-05-30 17:05:32
 */
public interface ArticleService extends IService<ArticleEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

