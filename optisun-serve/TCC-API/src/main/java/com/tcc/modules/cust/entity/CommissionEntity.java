package com.tcc.modules.cust.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 提现申请
 * 
 * @author 
 * @email 
 * @date 2022-07-19 20:16:56
 */
@Data
@TableName("c_commission")
public class CommissionEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@TableId
	private Long id;
	/**
	 * 客户ID
	 */
	private Long custId;
	/**
	 * 银行名称
	 */
	private String bankname;
	/**
	 * 银行代码
	 */
	private String code;
	/**
	 * 持卡人
	 */
	private String name;
	/**
	 * 账号
	 */
	private String account;
	/**
	 * IBAN
	 */
	private String iban;
	/**
	 * 出生
	 */
	private String chusheng;
	/**
	 * 提现金额
	 */
	private BigDecimal money;
	/**
	 * 状态值:0=待审,1=已打款,2=驳回，3=代付中
	 */
	private Integer status;
	/**
	 *
	 */
	private String cn;
	/**
	 * 备注
	 */
	private String remark;
	/**
	 * 到账金额
	 */
	private BigDecimal dzMoney;
	/**
	 * 0赠1减
	 */
	private Integer cut;
	/**
	 * 创建时间
	 */
	private Integer createTime;
	/**
	 * 审核人
	 */
	private Long checkBy;
	/**
	 * 审核人时间
	 */
	private Integer checkTime;
	/**
	 * 机构id
	 */
	private Long orgId;
	/**
	 * 删除标示(0正常1删除)
	 */
	private String delFlag;

	/**
	 * 账号类型(1内部0外部)
	 */
	private Integer isNb;
	/**
	 * 业务员Id
	 */
	private Long salesmanId;

	/**
	 * 代付通道Id
	 */
	private Long channelId;

	/**
	 * 代付通道名称
	 */
	private String channelName;

	/**
	 * 订单号
	 */
	private String orderId;

}
