package com.tcc.modules.cust.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * 
 * @author 
 * @email 
 * @date 2022-09-20 20:45:17
 */
@Data
@TableName("c_vip_commmission")
public class VipCommmissionEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
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
	 * 奖励业务员Id（员工id）
	 */
	private Long salesmanId;
	/**
	 *  奖励客户账号类型(1内部0外部)
	 */
	private Integer isNb;

}
