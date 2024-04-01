package com.tcc.modules.cust.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 充值记录表
 * 
 * @author 
 * @email 
 * @date 2022-07-19 10:14:26
 */
@Data
@TableName("c_recharge")
public class RechargeEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 充值Id
	 */
	@TableId
	private Long rechargeId;
	/**
	 * 客户Id
	 */
	private Long custId;
	/**
	 * 订单号
	 */
	private String orderCode;

	/**
	 * 是否首充(1-是;0-否)
	 */
	private Integer isFirst;
	/**
	 * 状态值:0=待付,1=已付,2=驳回
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
	 * 1:充值2赠送3:签到
	 */
	private String type;
	/**
	 * 平台银行卡id
	 */
	private long platformBankId;
	/**
	 * 单位
	 */
	private String unit;
	/**
	 * 换算前金额
	 */
	private BigDecimal moneyFront;
	/**
	 * 充值金额
	 */
	private BigDecimal amount;

	/**
	 * 充值类型名称
	 */
	private String moneytypename;
	/**
	 * APP传入的充值短信标识（交易id）
	 */
	private String transid;
	/**
	 * 费率
	 */
	private String fee;
	/**
	 * 审核备注
	 */
	private String checkRemark;
	/**
	 * 修改时间
	 */
	private Integer updateTime;
	/**
	 * 机构id
	 */
	private Long orgId;

	/**
	 * 账号类型(1内部0外部)
	 */
	private Integer isNb;
	/**
	 * 业务员Id
	 */
	private Long salesmanId;

	/**
	 * 审核信息
	 */
	private Integer checkTime;

	/**
	 * 审核信息
	 */
	private String transactionNo;
}
