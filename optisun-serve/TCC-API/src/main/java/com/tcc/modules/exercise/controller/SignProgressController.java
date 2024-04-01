package com.tcc.modules.exercise.controller;

import java.util.Arrays;
import java.util.Map;

import cn.hutool.core.util.StrUtil;
import com.tcc.common.utils.core.text.Convert;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.tcc.modules.exercise.entity.SignProgressEntity;
import com.tcc.modules.exercise.service.SignProgressService;
import com.tcc.common.utils.PageUtils;
import com.tcc.common.utils.R;



/**
 * 用户兑换产品进度表
 *
 * @author 
 * @email 
 * @date 2022-10-05 11:40:10
 */
@RestController
@RequestMapping("exercise/signprogress")
public class SignProgressController {
    @Autowired
    private SignProgressService signProgressService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("exercise:signprogress:list")
    public R list(@RequestParam Map<String, Object> params){
        String teamList = cn.hutool.core.convert.Convert.toStr(params.get("team"));
        if(StrUtil.isNotBlank(teamList)){
            String[] checkList =  teamList.split(",");
            params.put("team",checkList);
        }
        else{
            return R.error("请选择人员");
        }
        PageUtils page = signProgressService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("exercise:signprogress:info")
    public R info(@PathVariable("id") Long id){
		SignProgressEntity signProgress = signProgressService.getById(id);

        return R.ok().put("signProgress", signProgress);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("exercise:signprogress:save")
    public R save(@RequestBody SignProgressEntity signProgress){
		signProgressService.save(signProgress);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("exercise:signprogress:update")
    public R update(@RequestBody SignProgressEntity signProgress){
		signProgressService.updateById(signProgress);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("exercise:signprogress:delete")
    public R delete(@RequestBody Long[] ids){
		signProgressService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    /**
     * 设置下次完成
     * @param params
     * @return
     */
    @PostMapping("/nextComplateChange")
    public R nextComplateChange(@RequestBody Map<String,Object> params){
         Long id  = Convert.toLong(params.get("id"),0L);
         Integer nextComplate = Convert.toInt(params.get("nextComplete"),0);
         SignProgressEntity signProgress = new SignProgressEntity();
         signProgress.setId(id);
         signProgress.setNextComplete(nextComplate);
        signProgressService.updateById(signProgress);
        return R.ok();
    }
}
