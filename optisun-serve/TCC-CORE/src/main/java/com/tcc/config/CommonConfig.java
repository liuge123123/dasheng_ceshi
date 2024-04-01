package com.tcc.config;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.tcc.common.base.service.ConfigService;
import com.tcc.common.exception.WTDPException;
import com.tcc.common.utils.SpringContextUtils;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "common")
public class CommonConfig {

    private String filePath;

    private String baseUrl;

    private String serverIp;

    private String logoPath;

    /**
     * 获取公共配置
     * @param orgId
     * @return
     */
    public static CommonConfig getConfig(Long  orgId){
        ConfigService sysConfigService = SpringContextUtils.getBean("sysConfigService", ConfigService.class);
        String configStr = sysConfigService.getValue(orgId, "COMMON.CONFIG");
        if(StrUtil.isNotBlank(configStr)){
            CommonConfig config = JSONUtil.toBean(configStr, CommonConfig.class);
            return config;
        }else{
            throw new WTDPException("未找到相关的配置");
        }
    }

    /**
     * 获取公共参数值
     * @param orgId
     * @param key
     * @return
     */
    public static String getConfigVal(Long  orgId, String key){
        ConfigService sysConfigService = SpringContextUtils.getBean("sysConfigService", ConfigService.class);
        String configStr = sysConfigService.getValue(orgId, "COMMON.CONFIG");
        if(StrUtil.isNotBlank(configStr)){
            JSONObject config = JSONUtil.parseObj(configStr);
            return config.getStr(key);
        }else{
            throw new WTDPException("未找到相关的配置");
        }
    }

}
