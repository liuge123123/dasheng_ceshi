package com.tcc.modules.base;

import com.tcc.common.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.servlet.http.HttpServletRequest;

/**
 * App端接口基础类
 */
public abstract class AbstractAppController {
    @Autowired
    private HttpServletRequest request;
    protected org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());
    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 获取用户id
     *
     * @return
     */
    protected Long getUserId() {
        return UserUtils.getUserId(request);
    }
    //获取机构id
    protected  Long getOrgId(){
        return  UserUtils.getOrgId(request);
    }

    /**
     * 获取当前时间 转10位时间戳
     * @return
     */
    public Integer getCreateTime(){
        Long s = System.currentTimeMillis() / 1000;
        return s.intValue();
    }
}
