package com.tcc.modules.content.service.impl;

import cn.hutool.core.util.StrUtil;
import com.tcc.common.utils.StringUtils;
import com.tcc.common.utils.core.text.Convert;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tcc.common.utils.PageUtils;
import com.tcc.common.utils.Query;

import com.tcc.modules.content.dao.ArticleDao;
import com.tcc.modules.content.entity.ArticleEntity;
import com.tcc.modules.content.service.ArticleService;


@Service("articleService")
public class ArticleServiceImpl extends ServiceImpl<ArticleDao, ArticleEntity> implements ArticleService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String key = Convert.toStr(params.get("key"),"");
        QueryWrapper<ArticleEntity>  queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("org_id",params.get("orgId"));
        queryWrapper.like(StrUtil.isNotBlank(key),"title",key);
        queryWrapper.orderByDesc("sort");
        IPage<ArticleEntity> page = this.page(
                new Query<ArticleEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

}