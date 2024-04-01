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

import com.tcc.modules.s.dao.SPlatformBankDao;
import com.tcc.modules.s.entity.SPlatformBankEntity;
import com.tcc.modules.s.service.SPlatformBankService;


@Service("sPlatformBankService")
public class SPlatformBankServiceImpl extends ServiceImpl<SPlatformBankDao, SPlatformBankEntity> implements SPlatformBankService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String key = Convert.toStr(params.get("key"),"");
        QueryWrapper<SPlatformBankEntity> query=  new QueryWrapper<SPlatformBankEntity>();
        IPage<SPlatformBankEntity> page = this.page(
                new Query<SPlatformBankEntity>().getPage(params),
                query.eq("org_id",params.get("orgId")).eq("del_flag","0")
                        .like(StrUtil.isNotBlank(key),"name",key)
        );

        return new PageUtils(page);
    }

}