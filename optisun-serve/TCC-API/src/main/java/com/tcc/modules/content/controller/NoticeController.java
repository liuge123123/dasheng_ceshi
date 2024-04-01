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

import com.tcc.modules.content.entity.NoticeEntity;
import com.tcc.modules.content.service.NoticeService;
import com.tcc.common.utils.PageUtils;
import com.tcc.common.utils.R;


/**
 * 公告
 *
 * @author 
 * @email 
 * @date 2022-05-30 17:05:32
 */
@RestController
@RequestMapping("content/notice")
public class NoticeController extends AbstractBackController {
    @Autowired
    private NoticeService noticeService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("content:notice:list")
    public R list(@RequestParam Map<String, Object> params){
        params.put("orgId",getOrgId());
        params.put("key", StrUtil.cleanBlank(Convert.toStr(params.get("key"))));
        PageUtils page = noticeService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{noticeId}")
    @RequiresPermissions("content:notice:info")
    public R info(@PathVariable("noticeId") Long noticeId){
		NoticeEntity notice = noticeService.getById(noticeId);

        return R.ok().put("notice", notice);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("content:notice:save")
    public R save(@RequestBody NoticeEntity notice){
        notice.setCreateBy(getUserId());
        notice.setCreateTime(DateUtils.getCurrentTime());
        notice.setOrgId(getOrgId());
		noticeService.save(notice);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("content:notice:update")
    public R update(@RequestBody NoticeEntity notice){
		noticeService.updateById(notice);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("content:notice:delete")
    public R delete(@RequestBody Long[] noticeIds){
		noticeService.removeByIds(Arrays.asList(noticeIds));

        return R.ok();
    }

}
