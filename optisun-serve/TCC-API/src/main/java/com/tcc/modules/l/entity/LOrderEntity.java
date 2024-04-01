package com.tcc.modules.l.entity;

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
 * @date 2022-08-30 13:52:53
 */
@Data
@TableName("l_order")
public class LOrderEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	@TableId
	private Long id;
	/**
	 * 会员ID
	 */
	private Long custId;
	/**
	 * 会员名称
	 */
	private String custName;
	/**
	 * 产品ID
	 */
	private Long goodsId;
	/**
	 * 产品所属房间
	 */
	private Long goodsRoom;
	/**
	 * 产品名称
	 */
	private String goodsName;
	/**
	 * 产品图片
	 */
	private String goodsImg;
	/**
	 * 产品价格
	 */
	private BigDecimal goodsPrice;
	/**
	 * 产品日利率
	 */
	private BigDecimal goodsRate;
	/**
	 * 产品周期
	 */
	private Integer goodsCycle;
	/**
	 * 订单状态,1：进行中，2：已完成，3：结算中，4：取消中，5：取消完成
	 */
	private Integer orderStatus;
	/**
	 * 取消备注
	 */
	private String cancelRemark;
	/**
	 * 取消时间
	 */
	private Integer cancelTime;
	/**
	 * 取消锁定到期时间
	 */
	private Integer lockTime;
	/**
	 * 下单时间
	 */
	private Integer createTime;
	/**
	 * 完成时间
	 */
	private Integer doneTime;
	/**
	 * 业务员ID
	 */
	private Long salesmanId;
	/**
	 * 到期时间
	 */
	private Integer expireTime;
	/**
	 * 是否为内部用户
	 */
	private Integer isNb;

	/**
	 * 是否为内部用户
	 */
	private Integer receiveTime;

	/**
	 * 是否为内部用户
	 */
	private BigDecimal receiveProfit;

	/**
	 * 产品当日收益
	 */
	private BigDecimal goodsIncomeDay;

	/**
	 * 产品是否日反
	 */
	private Integer goodsIsDay;

	/**
	 * 产品排序
	 */
	private Integer goodsSort;

	/**
	 * 产品是否赠送
	 */
	private Integer goodsIsGive;
	/**
	 * 产品是否到期反本
	 */
	private Integer goodsIsCapitalReturn;

	/**
	 * 产品级别A-E
	 */
	private Integer goodsLevel;

}
