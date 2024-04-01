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
@TableName("c_task_commission")
public class TaskCommissionEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 订单ID
	 */
	@TableId(type=IdType.INPUT)
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
	 * 一级客户ID
	 */
	private Long firstCustId;
	/**
	 * 一级客户所属业务员
	 */
	private Long firstSalesmanId;
	/**
	 * 一级客户是否为内部
	 */
	private Integer firstIsNb;
	/**
	 * 一级客户佣金
	 */
	private BigDecimal fisrtMoney;
	/**
	 * 二级客户ID
	 */
	private Long secondCustId;
	/**
	 * 二级客户所属业务员
	 */
	private Long secondSalesmanId;
	/**
	 * 二级客户是否为内部
	 */
	private Integer secondIsNb;
	/**
	 * 二级客户佣金
	 */
	private BigDecimal secondMoney;
	/**
	 * 3级客户ID
	 */
	private Long thirdCustId;
	/**
	 * 3级客户所属业务员
	 */
	private Long thirdSalesmanId;
	/**
	 * 3级客户是否为内部
	 */
	private Integer thirdIsNb;
	/**
	 * 3级客户佣金
	 */
	private BigDecimal thirdMoney;
	/**
	 * 4级客户ID
	 */
	private Long fourCustId;
	/**
	 * 4级客户所属业务员
	 */
	private Long fourSalesmanId;
	/**
	 * 4级客户是否为内部
	 */
	private Integer fourIsNb;
	/**
	 * 4级客户佣金
	 */
	private BigDecimal fourMoney;
	/**
	 * 5级客户ID
	 */
	private Long fiveCustId;
	/**
	 * 5级客户所属业务员
	 */
	private Long fiveSalesmanId;
	/**
	 * 5级客户是否为内部
	 */
	private Integer fiveIsNb;
	/**
	 * 5级客户佣金
	 */
	private BigDecimal fiveMoney;
	/**
	 * 6级客户ID
	 */
	private Long sixCustId;
	/**
	 * 6级客户所属业务员
	 */
	private Long sixSalesmanId;
	/**
	 * 6级客户是否为内部
	 */
	private Integer sixIsNb;
	/**
	 * 6级客户佣金
	 */
	private BigDecimal sixMoney;
	/**
	 * 7级客户ID
	 */
	private Long sevenCustId;
	/**
	 * 7级客户所属业务员
	 */
	private Long sevenSalesmanId;
	/**
	 * 7级客户是否为内部
	 */
	private Integer sevenIsNb;
	/**
	 * 7级客户佣金
	 */
	private BigDecimal sevenMoney;

}
