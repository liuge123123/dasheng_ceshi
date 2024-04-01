package com.tcc.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import lombok.Data;

/**
 * 系统部门关系表
 * 
 * @author 
 * @email 
 * @date 2021-01-06 14:52:44
 */
@Data
@TableName("sys_dept_relation")
public class SysDeptRelationEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主节点
	 */
	@TableId(value = "ancestor", type = IdType.INPUT)
	private String ancestor;
	/**
	 * 子节点
	 */
	private String descendant;
	/**
	 * 机构id
	 */
	private Long  orgId;

}
