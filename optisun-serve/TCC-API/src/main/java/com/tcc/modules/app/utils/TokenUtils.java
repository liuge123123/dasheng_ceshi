package com.tcc.modules.app.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONObject;
import com.tcc.common.exception.WTDPException;
import com.tcc.common.utils.ConfigConstant;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class TokenUtils {

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private JwtUtils jwtUtils;

    private int tokenExpire = 7 * 24 * 3600;

    private int refreshTokenExpire = 15 * 24 * 3600;

    /**
     * 删除用户所有的token
     * @param userId
     */
    public void delToken(String userId){
        String tokenKey = ConfigConstant.REDISKEY.TOKEN.getVal(userId, "*");
        String refreshTokenKey = ConfigConstant.REDISKEY.REFRESH_TOKEN.getVal(userId, "*");
        redisTemplate.delete(redisTemplate.keys(tokenKey));
        redisTemplate.delete(redisTemplate.keys(refreshTokenKey));

    }
    /**
     * 生成token
     * @param userId
     * @return
     */
    public JSONObject genToken(String userId){
        String token = jwtUtils.generateTokenStr(userId);
        long expireTime = DateUtil.currentSeconds() + tokenExpire;
        String key = ConfigConstant.REDISKEY.TOKEN.getVal(userId, token);
        redisTemplate.opsForValue().set(key, userId, tokenExpire, TimeUnit.SECONDS);
        JSONObject result = new JSONObject();
        result.put("token", token);
        result.put("tokenExpireTime", expireTime);
        return result;
    }

    /**
     * 生成refresh_token
     * @param userId
     * @return
     */
    public JSONObject genRefreshToken(String userId){
        String token = jwtUtils.generateTokenStr(userId);
        long expireTime = DateUtil.currentSeconds() + refreshTokenExpire;
        String key = ConfigConstant.REDISKEY.REFRESH_TOKEN.getVal(userId, token);
        redisTemplate.opsForValue().set(key, userId, refreshTokenExpire, TimeUnit.SECONDS);
        JSONObject result = new JSONObject();
        result.put("refreshToken", token);
        result.put("refreshTokenExpireTime", expireTime);
        return result;
    }


    /**
     * 生成token和refreshToken
     * @param userId
     * @return
     */
    public JSONObject genTokenAndRefreshToken(String userId){
        JSONObject tokenObj = genToken(userId);
        JSONObject refreshTokenObj = genRefreshToken(userId);
        tokenObj.putAll(refreshTokenObj);
        return tokenObj;
    }

    /**
     * 通过refreshToken获取token
     * @param refreshToken
     * @return
     */
    public JSONObject genTokenAndRefreshTokenWithRefreshToken(String refreshToken){
        String userId = getUserId(refreshToken);
        String key = ConfigConstant.REDISKEY.REFRESH_TOKEN.getVal(userId, refreshToken);
        if(redisTemplate.hasKey(key)){
            delToken(userId);
            return genTokenAndRefreshToken(userId);
        }else{
            throw new WTDPException("refreshToken不存在或已过期", 401);
        }
    }

    /**
     * token是否过期
     * @param token
     * @return
     */
    public boolean isTokenExpire(String token){
        String userId = getUserId(token);
        String key = ConfigConstant.REDISKEY.TOKEN.getVal(userId, token);
        if(redisTemplate.hasKey(key)){
            return false;
        }
        return true;
    }

    /**
     * 生成h5token，有效期为30秒
     * @param userId
     * @return
     */
    public JSONObject genH5Token(String userId){
        int tokenExpire = 30 * 60;
        String token = jwtUtils.generateTokenStr(userId);
        long expireTime = DateUtil.currentSeconds() + tokenExpire;
        String key = ConfigConstant.REDISKEY.H5_TOKEN.getVal(userId, token);
        redisTemplate.opsForValue().set(key, userId, tokenExpire, TimeUnit.SECONDS);
        JSONObject result = new JSONObject();
        result.put("token", token);
        result.put("tokenExpireTime", expireTime);
        return result;
    }

    /**
     * h5token是否过期
     * @param token
     * @return
     */
    public boolean isH5TokenExpire(String token){
        String userId = getUserId(token);
        String key = ConfigConstant.REDISKEY.H5_TOKEN.getVal(userId, token);
        if(redisTemplate.hasKey(key)){
            return false;
        }
        return true;
    }

    /**
     * token是否过期
     * @param token
     * @return
     */
    public boolean isRefreshTokenExpire(String token){
        String userId = getUserId(token);
        String key = ConfigConstant.REDISKEY.REFRESH_TOKEN.getVal(userId, token);
        if(redisTemplate.hasKey(key)){
            return false;
        }
        return true;
    }

    /**
     * 从token获取用户ID
     * @param token
     * @return
     */
    public String getUserId(String token){
        Claims claims = jwtUtils.getClaimByToken(token);
        if(claims == null){
            throw new WTDPException("token不存在或已过期", 401);
        }
        return claims.getSubject();
    }

    /**
     * 获取token参数名
     * @return
     */
    public String getHeader(){
        return jwtUtils.getHeader();
    }

    /**
     * 获取加密密钥
     * @return
     */
    public String getSecret(){
        return jwtUtils.getSecret();
    }

}
