package com.tcc.modules.shop.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 订单商品表
 * 
 * @author 
 * @email 
 * @date 2023-02-21 08:58:33
 */
@Data
@TableName("shop_order_goods")
public class ShopOrderGoodsEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId
	private Long id;
	/**
	 * 订单ID
	 */
	private Long orderId;
	/**
	 * 产品ID
	 */
	private Long goodsId;
	/**
	 * 产品名称
	 */
	private String goodsName;
	/**
	 * 产品数量
	 */
	private Integer goodsNum;
	/**
	 * 产品价格
	 */
	private BigDecimal goodsPrice;
	/**
	 * 产品图片
	 */
	private String goodsImg;
	/**
	 * 产品描述
	 */
	private String goodsDesc;

}
