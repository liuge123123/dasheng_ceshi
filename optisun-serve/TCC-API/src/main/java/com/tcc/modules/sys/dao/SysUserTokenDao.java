

package com.tcc.modules.sys.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tcc.modules.sys.entity.SysUserTokenEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 系统用户Token
 *
 */
@Mapper
public interface SysUserTokenDao extends BaseMapper<SysUserTokenEntity> {

    SysUserTokenEntity queryByToken(String token);

    List<SysUserTokenEntity> queryByUserId(Long userId);
	
}
