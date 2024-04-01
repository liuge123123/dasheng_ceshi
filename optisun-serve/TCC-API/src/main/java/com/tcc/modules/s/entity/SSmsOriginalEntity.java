package com.tcc.modules.s.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 原始短信表
 * 
 * @author 
 * @email 
 * @date 2022-08-29 14:00:49
 */
@Data
@TableName("s_sms_original")
public class SSmsOriginalEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键
	 */
	@TableId(type = IdType.INPUT)
	private Long id;
	/**
	 * 来源
	 */
	private String comeTo;
	/**
	 * 短信内容
	 */
	private String allContent;
	/**
	 * 接收时间
	 */
	private Long receiveTime;
	/**
	 * 原始id
	 */
	private String originalId;
	/**
	 * 解析状态 0：未解析，1：解析失败，2：解析成功
	 */
	private Integer parseStatus = 0;

}
