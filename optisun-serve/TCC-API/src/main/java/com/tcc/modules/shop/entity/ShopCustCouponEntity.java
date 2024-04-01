package com.tcc.modules.shop.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 客户折扣券表
 * 
 * @author 
 * @email 
 * @date 2023-02-21 08:58:33
 */
@Data
@TableName("shop_cust_coupon")
public class ShopCustCouponEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	@TableId
	private Long id;
	/**
	 * 客户ID
	 */
	private Long custId;
	/**
	 * 折扣
	 */
	private BigDecimal discount;
	/**
	 * 过期时间
	 */
	private Integer expireTime;
	/**
	 * 使用状态
	 */
	private Integer useStatus;
	/**
	 * 业务员ID
	 */
	private Long salesmanId;
	/**
	 * 是否内部
	 */
	private Integer isNb;
	/**
	 * 创建时间
	 */
	private Integer createTime;

}
