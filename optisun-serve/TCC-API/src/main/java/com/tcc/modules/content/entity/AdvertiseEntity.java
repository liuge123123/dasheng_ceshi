package com.tcc.modules.content.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 轮播图片
 * 
 * @author 
 * @email 
 * @date 2022-05-30 17:05:32
 */
@Data
@TableName("o_advertise")
public class AdvertiseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 申请Id
	 */
	@TableId
	private Long id;
	/**
	 * 图片地址
	 */
	private String imgUrl;
	/**
	 * 图片链接
	 */
	private String linkUrl;
	/**
	 * 图片名称
	 */
	private String advertiseName;
	/**
	 * 启用-1；禁用-0
	 */
	private Integer status;
	/**
	 * 建立时间
	 */
	private Integer createTime;
	/**
	 * 建立人
	 */
	private Long createBy;
	/**
	 * 修改时间
	 */
	private Integer updateTime;
	/**
	 * 修改人
	 */
	private Long updateBy;

	/***
	 * 机构id
	 */
	private Long orgId;

}
