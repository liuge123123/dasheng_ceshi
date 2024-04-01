package com.tcc.modules.sys.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tcc.common.exception.WTDPException;
import com.tcc.common.utils.Constant;
import com.tcc.common.utils.DateUtils;
import com.tcc.common.utils.R;
import com.tcc.modules.base.AbstractBackController;
import com.tcc.modules.sys.entity.DictionaryEntity;
import com.tcc.modules.sys.service.DictionaryService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;


/**
 * 数据字典
 *
 * @author 
 * @email 
 * @date 2018-07-30 20:13:21
 */
@RestController
@RequestMapping("sys/dictionary")
public class DictionaryController extends AbstractBackController {
    @Autowired
    private DictionaryService dictionaryService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("sys:dictionary:list")
    public R list(@RequestParam Map<String, Object> params) {
        params.put("orgId",getOrgId());
        List<DictionaryEntity> list = dictionaryService.selAll(params);
        return R.ok().put("list", list);
    }
    /**
     * 列表
     */
    @RequestMapping("/firstList")
    public R firstList(@RequestParam Map<String, Object> params) {
        QueryWrapper queryWrapper = new QueryWrapper<DictionaryEntity>();
        queryWrapper.eq("org_id", Constant.SUPER_ORG);
        queryWrapper.eq("parent_code", "-1");
        queryWrapper.eq(params.containsKey("dicType"),"dic_type",params.get("dicType"));
        queryWrapper.orderByAsc("sort");
        Collection<DictionaryEntity> list = dictionaryService.list(queryWrapper);
        return R.ok().put("list", list);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("sys:dictionary:info")
    public R info(@PathVariable("id") Long id) {
        DictionaryEntity dictionary = dictionaryService.getById(id);
        return R.ok().put("dictionary", dictionary);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("sys:dictionary:save")
    public R save(@RequestBody DictionaryEntity dictionary) {
        dictionary.setOrgId(getOrgId());
        dictionary.setCreateTime(DateUtils.getCurrentTime());
        List list = dictionaryService.list(new QueryWrapper<DictionaryEntity>()
                .eq("code", dictionary.getCode())
                .eq("org_id", dictionary.getOrgId())
                .eq("parent_code", dictionary.getParentCode()));
        if (list.size() > 0) {
            return R.error("该编码已使用");
        }
        dictionaryService.saveDict(dictionary);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("sys:dictionary:update")
    public R update(@RequestBody DictionaryEntity dictionary) {
        dictionaryService.updateDict(getOrgId(), dictionary);
        return R.ok();
    }

    /**
     * 删除
     */
    @Transactional(rollbackFor = Exception.class)
    @RequestMapping("/delete")
    @RequiresPermissions("sys:dictionary:delete")
    public R delete(@RequestBody Long[] ids) {
        for (Long id : ids) {
            DictionaryEntity dictionary = dictionaryService.getById(id);
            if(dictionary.getIsEdit().equals(0)){
                //不可编辑
                throw new WTDPException("系统固定内容,不可删除");
            }
            dictionaryService.delDict(getOrgId(), dictionary);
        }
        return R.ok();
    }

    /**
     * 获取code值列表
     *
     * @return
     */
    @GetMapping("/codeList")
    public R codeList(String code) {
        List<DictionaryEntity> list = dictionaryService.getDictList(getOrgId(), code);
        return R.ok().put("list", list);
    }

    /**
     * 获取code值对应key的值
     *
     * @param code
     * @param key
     * @return
     */
    @GetMapping("/codeVal")
    public R codeVal(String code, String key) {
        String val = dictionaryService.getDictValue(getOrgId(), code, key);
        return R.ok().put("codeVal", val);
    }

}
