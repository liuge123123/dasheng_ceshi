

package com.tcc.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.tcc.common.base.entity.BaseUserTokenEntity;
import lombok.Data;

import java.io.Serializable;


/**
 * 系统用户Token
 */
@Data
@TableName("sys_user_token")
public class SysUserTokenEntity extends BaseUserTokenEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

}
