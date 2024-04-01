package com.tcc.common.aspect;

import com.tcc.common.exception.WTDPException;
import com.tcc.common.utils.ResultCode;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;


/**
 * 系统日志，切面处理类
 */
@Aspect
@Component
public class AccessLimitAspect {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private StringRedisTemplate redisTemplate;


    @Pointcut("@annotation(com.tcc.common.annotation.AccessLimit)")
    public void needCheckMethod() {
    }

    @Before("needCheckMethod()")
    public void checkMethod() {
        String requestId = request.getServletPath() + request.getHeader("token");
        Boolean absent = redisTemplate.opsForValue()
                .setIfAbsent(requestId, "1", 10, TimeUnit.SECONDS);
        if (!absent) {
            throw new WTDPException(ResultCode.RESULT_100022);
        }
        request.setAttribute("__request_resubmit_need_release", "need");
    }

    @After("needCheckMethod()")
    public void release() {
        if ("need".equals(String.valueOf(request.getAttribute("__request_resubmit_need_release")))) {
            String requestId = request.getServletPath() + request.getHeader("token");
            redisTemplate.delete(requestId);
        }
    }

}
