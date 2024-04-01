package com.tcc.modules.t.controller;

import java.util.Arrays;
import java.util.Map;

import cn.hutool.core.util.StrUtil;
import com.tcc.common.utils.DateUtils;
import com.tcc.common.utils.core.text.Convert;
import com.tcc.modules.base.AbstractBackController;
import com.tcc.modules.t.entity.TGoodsGradeEntity;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tcc.modules.t.entity.TOrderEntity;
import com.tcc.modules.t.service.TOrderService;
import com.tcc.common.utils.PageUtils;
import com.tcc.common.utils.R;



/**
 * 订单表
 *
 * @author
 * @email
 * @date 2022-07-19 09:17:41
 */
@RestController
@RequestMapping("t/torder")
public class TOrderController extends AbstractBackController {
    @Autowired
    private TOrderService tOrderService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("t:torder:list")
    public R list(@RequestParam Map<String, Object> params){
        params.put("orgId",getOrgId());
        String teamList = cn.hutool.core.convert.Convert.toStr(params.get("team"));
        if(StrUtil.isNotBlank(teamList)){
            String[] checkList =  teamList.split(",");
            params.put("team",checkList);
        }
        else{
            return R.error("请选择人员");
        }
        params.put("key",StrUtil.cleanBlank(cn.hutool.core.convert.Convert.toStr(params.get("key"))));
        PageUtils page = tOrderService.selectByCondition(params);
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("t:torder:info")
    public R info(@PathVariable("id") Long id){
		TOrderEntity tOrder = tOrderService.getById(id);

        return R.ok().put("tOrder", tOrder);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("t:torder:save")
    public R save(@RequestBody TOrderEntity tOrder){
        tOrder.setOrgId(Convert.toLong(getOrgId()));
        tOrder.setDelFlag("0");
        tOrder.setCreateBy(getUserId());
        tOrder.setCreateTime(DateUtils.getCurrentTime());
		tOrderService.save(tOrder);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("t:torder:update")
    public R update(@RequestBody TOrderEntity tOrder){
		tOrderService.updateById(tOrder);

        return R.ok();
    }

    /**
     * 手动卖出
     */
    @RequestMapping("/manualSell")
    @RequiresPermissions("t:torder:update")
    public R manualSell(@RequestParam Map<String, Object> params){
        Long id = Convert.toLong(params.get("id"),-1L);
        tOrderService.sale1(id);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("t:torder:delete")
    public R delete(@RequestBody Long[] ids){
		tOrderService.removeByIds(Arrays.asList(ids));
        for(Long item : ids){
            TOrderEntity tOrder = new TOrderEntity();
            tOrder.setDelFlag("1");
            tOrder.setDeleteTime(DateUtils.getCurrentTime());
            tOrder.setId(item);
            tOrderService.updateById(tOrder);
        }
        return R.ok();
    }

}
