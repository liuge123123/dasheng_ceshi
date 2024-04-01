package com.tcc.modules.shop.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 商城商品表
 * 
 * @author 
 * @email 
 * @date 2023-02-21 08:58:33
 */
@Data
@TableName("shop_goods")
public class ShopGoodsEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	@TableId
	private Long id;
	/**
	 * 名称
	 */
	private String name;
	/**
	 * 图片
	 */
	private String img;
	/**
	 * 价格
	 */
	private BigDecimal price;
	/**
	 * 大分类
	 */
	private String category1;
	/**
	 * 小分类
	 */
	private String category2;
	/**
	 * 规则
	 */
	private String specs;
	/**
	 * 描述
	 */
	private String description;
	/**
	 * 状态
	 */
	private Integer status;
	/**
	 * 外部链接
	 */
	private String outLink;
	/**
	 * 排序，越大越前
	 */
	private Integer sortNum;
	/**
	 * 类型，1：现售，2：预售
	 */
	private Integer type;

	/**
	 * 来源
	 */
	private String source;

}
