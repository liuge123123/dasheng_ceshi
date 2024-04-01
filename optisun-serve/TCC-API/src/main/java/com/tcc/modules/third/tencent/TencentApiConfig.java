package com.tcc.modules.third.tencent;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "tencentapi")
public class TencentApiConfig {

    private  String secretId;

    private String secretKey;

}
