package com.tcc.modules.content.service.impl;

import cn.hutool.core.util.StrUtil;
import com.tcc.common.utils.core.text.Convert;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tcc.common.utils.PageUtils;
import com.tcc.common.utils.Query;

import com.tcc.modules.content.dao.NoticeDao;
import com.tcc.modules.content.entity.NoticeEntity;
import com.tcc.modules.content.service.NoticeService;


@Service("noticeService")
public class NoticeServiceImpl extends ServiceImpl<NoticeDao, NoticeEntity> implements NoticeService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String key = Convert.toStr(params.get("key"),"");
        QueryWrapper<NoticeEntity> queryWrapper = new QueryWrapper<NoticeEntity>();
        queryWrapper.eq("org_id",params.get("orgId"));
        queryWrapper.like(StrUtil.isNotBlank(key),"content",key);
        IPage<NoticeEntity> page = this.page(
                new Query<NoticeEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

}