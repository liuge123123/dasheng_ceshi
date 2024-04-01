package com.tcc.modules.exercise.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tcc.common.utils.PageUtils;
import com.tcc.common.utils.Query;
import com.tcc.modules.exercise.dao.RecordDao;
import com.tcc.modules.exercise.entity.RecordEntity;
import com.tcc.modules.exercise.service.RecordService;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("RecordService")
public class RecordServiceImpl extends ServiceImpl<RecordDao, RecordEntity> implements RecordService {
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        Long userId = Convert.toLong(params.get("userId"));
        String hitPrize = Convert.toStr(params.get("hitPrize"));
        IPage<RecordEntity> page = this.page(new Query<RecordEntity>().getPage(params),
                new LambdaQueryWrapper<RecordEntity>()
                        .eq(userId != null, RecordEntity::getUserId, userId)
                        .like(StrUtil.isNotBlank(hitPrize), RecordEntity::getHitPrize, hitPrize)
                        .eq(RecordEntity::getDelFlag, 0)
                        .orderByDesc(RecordEntity::getCreateTime)
        );
        return new PageUtils(page);
    }

    @Override
    public Map<String, Object> getDetailById(@Param("condition") Map<String, Object> params) {
        Map<String, Object> map = baseMapper.getDetailById(params);
        return map;
    }
}
