

package com.tcc.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tcc.common.base.entity.BaseLogEntity;
import lombok.Data;


/**
 * 系统日志
 *
 */
@Data
@TableName("sys_log")
public class SysLogEntity extends BaseLogEntity {
	private static final long serialVersionUID = 1L;

	@TableId
	private Long id;


}
