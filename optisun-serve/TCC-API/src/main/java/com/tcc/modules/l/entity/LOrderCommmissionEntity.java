package com.tcc.modules.l.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 理财佣金记录
 * 
 * @author 
 * @email 
 * @date 2022-09-23 15:09:39
 */
@Data
@TableName("l_order_commmission")
public class LOrderCommmissionEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 取理财表ID
	 */
	@TableId(type = IdType.INPUT)
	private Long orderId;
	/**
	 * 订单时间
	 */
	private Integer orderTime;
	/**
	 * 订单所属客户
	 */
	private Long orderCust;
	/**
	 * 奖励客户
	 */
	private Long rewardCust;
	/**
	 * 奖励金额
	 */
	private BigDecimal rewardMoney;
	/**
	 *  奖励客户账号类型(1内部0外部)
	 */
	private Integer isNb;
	/**
	 * 奖励客户业务员Id（员工id）
	 */
	private Long salesmanId;

}
