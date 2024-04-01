package com.tcc.modules.s.controller;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.tcc.common.annotation.SysLog;
import com.tcc.common.exception.WTDPException;
import com.tcc.common.utils.PageUtils;
import com.tcc.common.utils.R;
import com.tcc.modules.s.entity.SSmsOriginalEntity;
import com.tcc.modules.s.service.SSmsOriginalService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;



/**
 * 原始短信表
 *
 */
@RestController
@RequestMapping("s/ssmsoriginal")
public class SSmsOriginalController {
    @Autowired
    private SSmsOriginalService sSmsOriginalService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("s:ssmsoriginal:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = sSmsOriginalService.queryPage(params);

        return R.ok().put("page", page);
    }

    /**
     * 列表
     */
    @RequestMapping("/count")
    public R count(@RequestParam Map<String, Object> params){
        return R.ok().put("data", sSmsOriginalService.getCount(params));
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("s:ssmsoriginal:info")
    public R info(@PathVariable("id") Long id){
		SSmsOriginalEntity sSmsOriginal = sSmsOriginalService.getById(id);

        return R.ok().put("sSmsOriginal", sSmsOriginal);
    }

    /**
     * 保存
     */
    @SysLog("手动添加短信")
    @RequestMapping("/save")
    @RequiresPermissions("s:ssmsoriginal:save")
    public R save(@RequestBody SSmsOriginalEntity sSmsOriginal){
        sSmsOriginal.setReceiveTime(System.currentTimeMillis());
        List<SSmsOriginalEntity> list = new ArrayList<>();
        list.add(sSmsOriginal);
        sSmsOriginalService.parseSms(list);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("s:ssmsoriginal:update")
    public R update(@RequestBody SSmsOriginalEntity sSmsOriginal){
		sSmsOriginalService.updateById(sSmsOriginal);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("s:ssmsoriginal:delete")
    public R delete(@RequestBody Long[] ids){
		sSmsOriginalService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    /***
     * 导出到excel
     * @return
     */
    @RequestMapping("/downExcel")
    @RequiresPermissions("s:ssmsoriginal:excel")
    public void   downSmsExcel(HttpServletResponse response, @RequestParam Map<String,Object> params) throws IOException {
        params.put("key",StrUtil.cleanBlank(Convert.toStr(params.get("key"))));
        sSmsOriginalService.downExcel(response,params);
    }

}
