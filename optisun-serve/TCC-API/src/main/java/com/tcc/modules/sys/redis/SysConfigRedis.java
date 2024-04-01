

package com.tcc.modules.sys.redis;


import com.tcc.common.utils.RedisUtils;
import com.tcc.modules.sys.entity.SysConfigEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 系统配置Redis
 *
 */
@Component
public class SysConfigRedis {
    @Autowired
    private RedisUtils redisUtils;

    public void saveOrUpdate(long orgId, SysConfigEntity config) {
        if(config == null){
            return ;
        }
        String key = RedisUtils.getRedisKey(orgId,config.getParamKey());
        redisUtils.set(key, config);
    }

    public void delete(long orgId, String configKey) {
        String key = RedisUtils.getRedisKey(orgId, configKey);
        redisUtils.delete(key);
    }

    public SysConfigEntity get(long orgId, String configKey){
        String key = RedisUtils.getRedisKey(orgId, configKey);
        return redisUtils.get(key, SysConfigEntity.class);
    }
}
