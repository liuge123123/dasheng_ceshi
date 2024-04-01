package com.tcc.modules.s.service.impl;

import cn.hutool.core.util.StrUtil;
import com.tcc.common.utils.core.text.Convert;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tcc.common.utils.PageUtils;
import com.tcc.common.utils.Query;

import com.tcc.modules.s.dao.SBankCateDao;
import com.tcc.modules.s.entity.SBankCateEntity;
import com.tcc.modules.s.service.SBankCateService;


@Service("sBankCateService")
public class SBankCateServiceImpl extends ServiceImpl<SBankCateDao, SBankCateEntity> implements SBankCateService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String key = Convert.toStr(params.get("key"),"");
        QueryWrapper<SBankCateEntity> query=  new QueryWrapper<SBankCateEntity>();
        IPage<SBankCateEntity> page = this.page(
                new Query<SBankCateEntity>().getPage(params),
                query.eq("org_id",params.get("orgId")).eq("del_flag","0")
                        .like(StrUtil.isNotBlank(key),"name",key)
        );
        return new PageUtils(page);
    }
}