package com.tcc.modules.sys.entity;


import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 数据字典
 * 
 * @author 
 * @email 
 * @date 2018-07-30 20:13:21
 */
@Data
@TableName("sys_dictionary")
public class DictionaryEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 序号
	 */
	@TableId
	private Long id;
	/**
	 * 编码
	 */
	private String code;
	/**
	 * 值
	 */
	private String value;
	/**
	 * 上级编码
	 */
	private String parentCode;
	/**
	 * 备注
	 */
	private String comments;
	/**
	 * 状态  0：禁用   1：正常
	 */
	private Integer status;
	/**
	 * 创建者ID
	 */
	private Long createUserId;
	/**
	 * 创建时间
	 */
	private Integer createTime;

	/***
	 * 排序序号
	 */
	private Integer sort;

	/***
	 * 字典类型(10公共字典,11客户字典)
	 */
	private Integer dicType;

	/***
	 * 机构id
	 */
	private Long  orgId;

	/***
	 * 是否可编辑(0不可编辑 1可编辑)
	 */
	private Integer isEdit;

}
