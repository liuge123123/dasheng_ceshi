package com.tcc.modules.app.interceptor;


import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tcc.common.exception.WTDPException;
import com.tcc.common.utils.Constant;
import com.tcc.common.utils.IPUtils;
import com.tcc.common.utils.ResultCode;
import com.tcc.modules.app.annotation.Login;
import com.tcc.modules.app.utils.TokenUtils;
import com.tcc.modules.cust.entity.CustEntity;
import com.tcc.modules.cust.service.CustService;
import com.tcc.modules.sys.service.SysConfigService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 权限(Token)验证
 */
@Component
public class AuthorizationInterceptor extends HandlerInterceptorAdapter {
    @Autowired
    private TokenUtils tokenUtils;

    @Autowired
    private SysConfigService sysConfigService;

    @Autowired
    private CustService custService;

    public static final String USER_KEY = "userId";

    public static final String CLINET_KEY = "client_id";


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (HttpMethod.OPTIONS.equals(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return true;
        }

        if(handler instanceof HandlerMethod) {
            String uri = request.getRequestURI();
            Login annotation = ((HandlerMethod) handler).getMethodAnnotation(Login.class);

            // 用户白名单
            String[] userIdArr = getConfigArr("user.white.config");
            boolean userWhiteFlag = false;

            // IP黑名单
            String ip = IPUtils.getIpAddr(request);
            String[] ipBlackArr = getConfigArr("ip.black.config");
            boolean ipBlackFlag = IPUtils.isInRanges(ip, ipBlackArr);

            if (!isIgnore(uri)) {
                //获取用户凭证
                String tokenKey = tokenUtils.getHeader();
                String token = getToken(request);
                if (StrUtil.isBlank(token)) {
                    throw new WTDPException(tokenKey + "不能为空", HttpStatus.UNAUTHORIZED.value());
                }
                String appSecret = tokenUtils.getSecret();
                if (annotation == null && token.startsWith(appSecret)) {
                    String clientId = token.replace(appSecret, "");
                    //设置clinetId到request里
                    request.setAttribute(CLINET_KEY, clientId);
                    String username = request.getParameter("username");
                    if (StrUtil.isNotBlank(username)) {
                        CustEntity cust = custService.getByUsername(username);
                        if (cust != null) {
                            userWhiteFlag = ArrayUtil.contains(userIdArr, Convert.toStr(cust.getCustId()));
                        }
                    }
                } else {
                    //凭证为空
                    if (tokenUtils.isTokenExpire(token)) {
                        throw new WTDPException(tokenKey + "失效，请重新登录", HttpStatus.UNAUTHORIZED.value());
                    } else {
                        //设置userId到request里，后续根据userId，获取用户信息
                        String userId = tokenUtils.getUserId(token);
                        request.setAttribute(USER_KEY, userId);
                        userWhiteFlag = ArrayUtil.contains(userIdArr, userId);
                    }
                }
            }
            if (ipBlackFlag && !userWhiteFlag) {
                throw new WTDPException(ResultCode.RESULT_100037);
            }
        }
        return true;
    }

    /**
     * 获取IP黑名单或用户白名单数组
     * @param key
     * @return
     */
    private String[] getConfigArr(String key) {
        String configStr = sysConfigService.getValue(Constant.SUPER_ORG, key);
        if (StrUtil.isNotBlank(configStr)) {
            configStr = configStr.replaceAll(" ", "");
            configStr = configStr.replaceAll(",\\n", ",");
            configStr = configStr.replaceAll("\\n", ",");
            return configStr.split("[,，]");
        }
        return new String[]{};
    }

    /**
     * 从请求获取token
     *
     * @param request
     * @return
     */
    private String getToken(HttpServletRequest request) {
        String tokenKey = tokenUtils.getHeader();
        String token = request.getHeader(tokenKey);
        if (StringUtils.isBlank(token)) {
            token = request.getParameter(tokenKey);
        }
        return token;
    }

    /**
     * 不需要token的uri
     *
     * @param uri
     * @return
     */
    private boolean isIgnore(String uri) {
        if (uri.indexOf("/app/wxNotify/payNotify") != -1
                || uri.indexOf("/app/alipayCallback/aliPayCallback") != -1
                || uri.indexOf("/app/memberUser/poster") != -1
                || uri.indexOf("/app/memberUser/classQrcode") != -1
                || uri.indexOf("/app/memberUser/classPoster") != -1
                || uri.indexOf("/app/iap/appstorynotify") != -1
                || uri.indexOf("/app/wx/") != -1
                || uri.indexOf("/app/qywx/") != -1
                || uri.indexOf("/app/test") != -1
                || uri.indexOf("/app/sms/commissionsms") != -1
                || uri.indexOf("/app/sms/transmitSms") != -1
                || uri.indexOf("/app/sms/auditorder") != -1
                || uri.indexOf("/app/sms/login") != -1
                || uri.indexOf("/app/account/online/service") != -1
                || uri.indexOf("/app/account/notify/") != -1) {
            return true;
        }
        return false;
    }
}
