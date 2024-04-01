package com.tcc.modules.shop.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.Data;

/**
 * 商城订单表
 * 
 * @author 
 * @email 
 * @date 2023-02-21 08:58:33
 */
@Data
@TableName("shop_order")
public class ShopOrderEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId
	private Long id;
	/**
	 * 客户ID
	 */
	private Long custId;
	/**
	 * 订单编号
	 */
	private String orderNo;
	/**
	 * 订单产品数量
	 */
	private Integer orderNum;
	/**
	 * 订单金额
	 */
	private BigDecimal orderMoney;
	/**
	 * 订单折扣
	 */
	private BigDecimal orderDiscount;
	/**
	 * 订单实付金额
	 */
	private BigDecimal orderPayMoney;
	/**
	 * 订单时间
	 */
	private Integer orderTime;
	/**
	 * 购买者姓名
	 */
	private String buyerName;
	/**
	 * 购买者电话
	 */
	private String buyderPhone;
	/**
	 * 购买者地址
	 */
	private String buyerAddress;
	/**
	 * 折扣券ID
	 */
	private Long couponId;
	/**
	 * 业务员ID
	 */
	private Long salesmanId;
	/**
	 * 是否为内部客户
	 */
	private Integer isNb;

	/**
	 * 订单状态，1：已付款，2：已退单
	 */
	private Integer orderStatus;

	/**
	 * 退单时间
	 */
	private Integer quitTime;

	/**
	 * 退单操作人
	 */
	private Long quitUser;

	@TableField(exist = false)
	private List<ShopOrderGoodsEntity> goodsList;

}
