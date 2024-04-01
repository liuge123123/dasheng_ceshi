

package com.tcc.modules.sys.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tcc.common.base.entity.BaseUserEntity;
import com.tcc.common.shiro.TokenGenerator;
import com.tcc.common.utils.R;
import com.tcc.modules.sys.dao.SysUserTokenDao;
import com.tcc.modules.sys.entity.SysUserEntity;
import com.tcc.modules.sys.entity.SysUserTokenEntity;
import com.tcc.modules.sys.service.SysUserTokenService;
import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


@Service("sysUserTokenService")
public class SysUserTokenServiceImpl extends ServiceImpl<SysUserTokenDao, SysUserTokenEntity> implements SysUserTokenService {
    //12小时后过期
    private final static int EXPIRE = 3600 * 12;


    @Override
    public R createToken(SysUserEntity user) {
        Long userId = user.getUserId();
        //生成一个token
        String token = TokenGenerator.generateValue();
        //当前时间
        Date now = new Date();
        //过期时间
        Date expireTime = new Date(now.getTime() + EXPIRE * 1000);
        //判断是否生成过token
        List<SysUserTokenEntity> tokenList = queryByUserId(userId);
        if (user.getLoginLimit() > 0 &&  tokenList.size() > user.getLoginLimit()) {
            this.removeById(tokenList.get(0).getId());
        }
        SysUserTokenEntity tokenEntity = new SysUserTokenEntity();
        tokenEntity.setUserId(userId);
        tokenEntity.setToken(token);
        tokenEntity.setUpdateTime(now);
        tokenEntity.setExpireTime(expireTime);
        this.save(tokenEntity);
        R r = R.ok().put("token", token).put("expire", EXPIRE);
        return r;
    }

    @Override
    public void logout() {
        BaseUserEntity user = (BaseUserEntity) SecurityUtils.getSubject().getPrincipal();
        SysUserTokenEntity tokenEn = getBaseMapper().queryByToken(user.getToken());
        if (tokenEn != null) {
            this.removeById(tokenEn.getId());
        }
    }

    @Override
    public List<SysUserTokenEntity> queryByUserId(Long userId) {
        return getBaseMapper().queryByUserId(userId);
    }
}
