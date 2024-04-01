package com.tcc.modules.t.service.impl;

import cn.hutool.core.util.StrUtil;
import com.tcc.common.utils.StringUtils;
import com.tcc.common.utils.core.text.Convert;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tcc.common.utils.PageUtils;
import com.tcc.common.utils.Query;

import com.tcc.modules.t.dao.TGoodsDao;
import com.tcc.modules.t.entity.TGoodsEntity;
import com.tcc.modules.t.service.TGoodsService;


@Service("tGoodsService")
public class TGoodsServiceImpl extends ServiceImpl<TGoodsDao, TGoodsEntity> implements TGoodsService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String key= params.containsKey("key") ? params.get("key").toString() : "";
        String id= params.containsKey("id") ? params.get("id").toString() : "";
        Integer gradeId = Convert.toInt(params.get("gradeId"));
        IPage<TGoodsEntity> page = this.page(
                new Query<TGoodsEntity>().getPage(params),
                new QueryWrapper<TGoodsEntity>()
                        .eq("del_flag","0")
                        .eq("org_id",params.get("orgId"))
                        .and(wrapper -> wrapper.like("goods_name", key))
                        .eq(StringUtils.isNotBlank(id),"id",id)
                        .eq(gradeId != null, "grade_id", params.get("gradeId")).orderByAsc("price")
        );
        return new PageUtils(page);
    }

}
