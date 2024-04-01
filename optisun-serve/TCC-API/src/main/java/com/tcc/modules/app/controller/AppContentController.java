package com.tcc.modules.app.controller;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tcc.common.utils.ConfigConstant;
import com.tcc.common.utils.R;
import com.tcc.modules.base.AbstractAppController;
import com.tcc.modules.content.entity.AdvertiseEntity;
import com.tcc.modules.content.entity.ArticleEntity;
import com.tcc.modules.content.entity.NoticeEntity;
import com.tcc.modules.content.service.AdvertiseService;
import com.tcc.modules.content.service.ArticleService;
import com.tcc.modules.content.service.NoticeService;
import com.tcc.modules.sys.service.SysConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/app/content")
@Api("APP共通接口")
public class AppContentController extends AbstractAppController {

    @Autowired
    private AdvertiseService advertiseService;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private NoticeService noticeService;

    @Autowired
    private SysConfigService sysConfigService;

    @ApiOperation("获取内容")
    @GetMapping("/info")
    public R info(String code){
        return R.ok().putData(articleService.getOne(new LambdaQueryWrapper<ArticleEntity>()
                .eq(ArticleEntity::getPositionCode, code)
                .eq(ArticleEntity::getDelFlag, "0")
                .eq(ArticleEntity::getStatus, 1)
                .eq(ArticleEntity::getOrgId, getOrgId())
                .last("limit 0, 1")
        ));
    }

    @ApiOperation("根据id获取内容")
    @GetMapping("/infoById")
    public R infoById(Integer articleId){
        return R.ok().putData(articleService.getOne(new LambdaQueryWrapper<ArticleEntity>()
                .eq(ArticleEntity::getArticleId, articleId)
                .eq(ArticleEntity::getDelFlag, "0")
                .eq(ArticleEntity::getStatus, 1)
                .eq(ArticleEntity::getOrgId, getOrgId())
                .last("limit 0, 1")
        ));
    }

    @ApiOperation("获取列表")
    @GetMapping("/list")
    public R list(String code){
        return R.ok().putData(articleService.list(new LambdaQueryWrapper<ArticleEntity>()
                .eq(ArticleEntity::getPositionCode, code)
                .eq(ArticleEntity::getDelFlag, "0")
                .eq(ArticleEntity::getStatus, 1)
                .eq(ArticleEntity::getOrgId, getOrgId()).orderByDesc(ArticleEntity::getSort)
        ));
    }

    @ApiOperation("获取配置")
    @GetMapping("/notice")
    public R notice(){
        String videoConfigStr = sysConfigService.getValue(getOrgId(), "luck.record.config");
        String lines[] = videoConfigStr.split("\\r?\\n");
        return R.ok().putData(lines);
//        return R.ok().putData(noticeService.list(new LambdaQueryWrapper<NoticeEntity>()
//                .eq(NoticeEntity::getStatus, 1)
//                .eq(NoticeEntity::getOrgId, getOrgId())
//                .orderByDesc(NoticeEntity::getCreateTime)
//        ));
    }

    @ApiOperation("获取配置")
    @GetMapping("/ad")
    public R ad(){
        return R.ok().putData(advertiseService.list(new LambdaQueryWrapper<AdvertiseEntity>()
                .eq(AdvertiseEntity::getStatus, 1)
                .eq(AdvertiseEntity::getOrgId, getOrgId())
        ));
    }

    @ApiOperation("获取配置")
    @GetMapping("/video")
    public R video(){
        String videoConfigStr = sysConfigService.getValue(getOrgId(), ConfigConstant.VIDEO_SETTING_CONFIG_KEY);

        JSONObject kfConfig = JSONUtil.parseObj(videoConfigStr);
        return R.ok().putData(kfConfig.get("video"));
    }

}
