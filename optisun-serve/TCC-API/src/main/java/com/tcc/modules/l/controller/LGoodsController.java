package com.tcc.modules.l.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tcc.common.annotation.SysLog;
import com.tcc.common.utils.DateUtils;
import com.tcc.common.utils.PageUtils;
import com.tcc.common.utils.R;
import com.tcc.modules.base.AbstractBackController;
import com.tcc.modules.l.entity.LGoodsEntity;
import com.tcc.modules.l.service.LGoodsService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author
 * @email
 * @date 2022-08-30 13:52:53
 */
@RestController
@RequestMapping("l/lgoods")
public class LGoodsController extends AbstractBackController {
    @Autowired
    private LGoodsService lGoodsService;

    @GetMapping("/all")
    public R all(Long roomId) {
        List<LGoodsEntity> list = lGoodsService.list(new LambdaQueryWrapper<LGoodsEntity>()
                .eq(roomId != null, LGoodsEntity::getRoomId, roomId)
                .orderByAsc(LGoodsEntity::getSort)
        );
        return R.ok().put("data", list);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("l:lgoods:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = lGoodsService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("l:lgoods:info")
    public R info(@PathVariable("id") Long id) {
        LGoodsEntity lGoods = lGoodsService.getById(id);
        return R.ok().put("lGoods", lGoods);
    }

    /**
     * 保存
     */
    @SysLog("新增理财产品")
    @RequestMapping("/save")
    @RequiresPermissions("l:lgoods:save")
    public R save(@RequestBody LGoodsEntity lGoods) {
        lGoods.setCreateTime(DateUtils.getCurrentTime());
        lGoods.setCreateBy(getUserId());
        lGoodsService.save(lGoods);
        return R.ok();
    }

    /**
     * 修改
     */
    @SysLog("更新理财产品")
    @RequestMapping("/update")
    @RequiresPermissions("l:lgoods:update")
    public R update(@RequestBody LGoodsEntity lGoods) {
        lGoodsService.updateById(lGoods);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("l:lgoods:delete")
    public R delete(@RequestBody Long[] ids) {
        lGoodsService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
