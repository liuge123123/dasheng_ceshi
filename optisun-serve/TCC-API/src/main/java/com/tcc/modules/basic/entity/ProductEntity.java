package com.tcc.modules.basic.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 商品表
 * 
 * @author 
 * @email 
 * @date 2022-05-30 16:54:43
 */
@Data
@TableName("b_product")
public class ProductEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 配置Id
	 */
	@TableId
	private String productId;
	/**
	 * 商品名称
	 */
	private String productName;
	/**
	 * 产品图片
	 */
	private String imgUrl;
	/**
	 * 产品价格
	 */
	private BigDecimal price;
	/**
	 * 商品描述
	 */
	private String detail;
	/**
	 * 店面名称
	 */
	private BigDecimal shopName;
	/**
	 * 链接地址
	 */
	private String productUrl;
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
	/**
	 * 删除标识(0-正常；1-已删除)
	 */
	private Integer delFlag;

}
