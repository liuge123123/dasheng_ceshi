package com.tcc.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import lombok.Data;

/**
 * 系统部门表
 * 
 * @author 
 * @email 
 * @date 2021-01-06 14:52:45
 */
@Data
@TableName("sys_dept")
public class SysDeptEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 部门id
	 */
	@TableId
	private Integer deptId;
	/**
	 * 部门名称
	 */
	private String name;
	/**
	 * 父节点id
	 */
	private Integer parentId;
	/**
	 * 父节点的所有ID
	 */
	private String parentIds;
	/**
	 * 删除标示（0正常 1删除）
	 */
	private String delFlag;
	/**
	 * 创建时间
	 */
	private Integer createTime;
	/**
	 * 创建用户id
	 */
	private Long createUserId;
	/**
	 * 部门id
	 */
	private Long  orgId;
	/**
	 * 排序
	 */
	private Integer sort;

}
