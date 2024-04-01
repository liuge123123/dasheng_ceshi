package com.tcc.modules.cust.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * 客户积分变动表
 * 
 * @author 
 * @email 
 * @date 2022-07-19 15:57:58
 */
@Data
@TableName("c_cust_score_log")
public class CustScoreLogEntity implements Serializable {
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
	 * 变更积分
	 */
	private BigDecimal score;
	/**
	 * 变更前积分
	 */
	private BigDecimal beforeScore;
	/**
	 * 变更后积分
	 */
	private BigDecimal afterScore;
	/**
	 * 备注
	 */
	private String remark;
	/**
	 * 创建时间
	 */
	private Integer createTime;
	/**
	 * 机构id
	 */
	private Long orgId;
	/**
	 * 变动方向，1：增加，2：减少
	 */
	private Integer direct;
	/**
	 * 变动类型：1:系统赠送；2系统扣减;3签到赠送；4首冲赠送 5vip0 过期扣减注册金 6个人任务返佣7团队佣金8基金佣金9抽奖奖金10充值 11提现12基金分红13基金本金14商城下单
	 */
	private Integer type;

	/**
	 * 账号类型(1内部0外部)
	 */
	private Integer isNb;
	/**
	 * 业务员Id
	 */
	private Long salesmanId;


	/**
	 * 来源单据id
	 */
	private Long sourceId;

}
