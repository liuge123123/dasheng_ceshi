package com.tcc.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import lombok.Data;

/**
 * 用户数据权限
 * 
 * @author 
 * @email 
 * @date 2021-03-22 12:01:13
 */
@Data
@TableName("sys_data_permission")
public class SysDataPermissionEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	@TableId
	private Integer id;
	/**
	 * 用户ID
	 */
	private Integer userId;
	/**
	 * 授权类型
	 */
	private Integer type;
	/**
	 * 被授权者类型，1:用户，2:部门
	 */
	private Integer relationType;
	/**
	 * 被授权者ID
	 */
	private String relationId;
	/**
	 * 创建者
	 */
	private Integer createBy;
	/**
	 * 创建时间
	 */
	private Integer createTime;
	/**
	 * 更新者
	 */
	private Integer updateBy;
	/**
	 * 更新时间
	 */
	private Integer updateTime;
	/**
	 * 乐观锁
	 */
	private Integer version;

}
