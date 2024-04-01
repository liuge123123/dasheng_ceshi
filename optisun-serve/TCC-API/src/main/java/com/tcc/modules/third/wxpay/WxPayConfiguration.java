package com.tcc.modules.third.wxpay;

import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @author 
 */
@Configuration
@ConditionalOnClass(WxPayService.class)
public class WxPayConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public WxPayService wxPayService() {
        WxPayService wxPayService = new WxPayServiceImpl();
        return wxPayService;
    }



}
