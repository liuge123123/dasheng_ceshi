package com.tcc.modules.app.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.tcc.common.utils.Constant;
import com.tcc.modules.sys.service.SysConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class LuckUtils {

    @Autowired
    private SysConfigService sysConfigService;

    /**
     * 充值是否赠送
     */
    public static final String RECHARGEABLE = "rechargeAble";

    /**
     * 邀请下级是否赠送
     */
    public static final String SHAREABLE = "shareAble";

    /**
     * 邀请下级首充是否赠送
     */
    public static final String SHARERECHARGEFIRSTABLE = "shareRechargeFirstAble";

    public boolean checkAble(String type, Long levelId, BigDecimal money){
        String configStr = sysConfigService.getValue(Constant.SUPER_ORG, "luck.rule.config");
        if(StrUtil.isNotBlank(configStr)){
            JSONObject config = JSONUtil.parseObj(configStr);
            Long minLevel = config.getLong("minLevel", 0L);
            if(levelId >= minLevel){
                boolean able = config.getBool(type, false);
                if(able && RECHARGEABLE.equals(type)){
                    BigDecimal minMoney = config.getBigDecimal("minRechargeMoney", new BigDecimal(0));
                    return money.compareTo(minMoney) >= 0;
                }else{
                    return able;
                }
            }
        }
        return false;
    }


}
