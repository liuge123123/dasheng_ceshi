package com.tcc.modules.content.controller;

import java.util.Arrays;
import java.util.Map;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.tcc.common.utils.DateUtils;
import com.tcc.modules.base.AbstractBackController;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tcc.modules.content.entity.ArticleEntity;
import com.tcc.modules.content.service.ArticleService;
import com.tcc.common.utils.PageUtils;
import com.tcc.common.utils.R;



/**
 * 文章
 *
 * @author 
 * @email 
 * @date 2022-05-30 17:05:32
 */
@RestController
@RequestMapping("content/article")
public class ArticleController extends AbstractBackController {
    @Autowired
    private ArticleService articleService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("content:article:list")
    public R list(@RequestParam Map<String, Object> params){
        params.put("orgId",getOrgId());
        params.put("key", StrUtil.cleanBlank(Convert.toStr(params.get("key"))));
        PageUtils page = articleService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{articleId}")
    @RequiresPermissions("content:article:info")
    public R info(@PathVariable("articleId") Long articleId){
		ArticleEntity article = articleService.getById(articleId);

        return R.ok().put("article", article);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("content:article:save")
    public R save(@RequestBody ArticleEntity article){
        article.setDelFlag("0");
        article.setCreateBy(getUserId());
        article.setCreateTime(DateUtils.getCurrentTime());
        article.setOrgId(getOrgId());
		articleService.save(article);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("content:article:update")
    public R update(@RequestBody ArticleEntity article){
        article.setUpdateBy(getUserId());
        article.setUpdateTime(DateUtils.getCurrentTime());
		articleService.updateById(article);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("content:article:delete")
    public R delete(@RequestBody Long[] articleIds){
		articleService.removeByIds(Arrays.asList(articleIds));

        return R.ok();
    }

}
