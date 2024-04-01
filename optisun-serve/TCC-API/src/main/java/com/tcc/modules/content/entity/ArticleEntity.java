package com.tcc.modules.content.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 文章
 * 
 * @author 
 * @email 
 * @date 2022-05-30 17:05:32
 */
@Data
@TableName("o_article")
public class ArticleEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 文章Id
	 */
	@TableId
	private Long articleId;

	/**
	 * 标题
	 */
	private String title;
	/**
	 * 文章类容
	 */
	private String content;
	/**
	 * 状态(0-下架;1-上架)
	 */
	private Integer status;
	/**
	 * 建立人
	 */
	private Long createBy;
	/**
	 * 建立时间
	 */
	private Integer createTime;
	/**
	 * 修改人
	 */
	private Long updateBy;
	/**
	 * 修改时间
	 */
	private Integer updateTime;
	/**
	 * 删除标识(0-正常;1-已删除)
	 */
	private String delFlag;


	/***
	 * 机构id
	 */
	private Long orgId;

	/**
	 * 排序（越大越靠前）
	 */
	private Integer sort;

	/***
	 * 位置编码
	 */
	private String positionCode;

}
