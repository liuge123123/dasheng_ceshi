package com.tcc.modules.t.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * 订单表
 *
 * @author
 * @email
 * @date 2022-07-19 09:17:41
 */
@Data
@TableName("t_order")
public class TOrderEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@TableId
	private Long id;
	/**
	 * 用户ID
	 */
	private Long custId;
	/**
	 * 商品ID
	 */
	private Long goodsId;
	/**
	 * 付款
	 */
	private BigDecimal money;
	/**
	 * 下单时候商品价格
	 */
	private BigDecimal goodsMoney;
	/**
	 * 佣金比例（整数）
	 */
	private BigDecimal parameter1;
	/**
	 * 佣金比例（具体值)
	 */
	private BigDecimal parameter2;
	/**
	 * 佣金
	 */
	private BigDecimal parameter3;
	/**
	 * 状态值:0=待审,1=同意,2=驳回
	 */
	private Integer type;
	/**
	 * 机构Id
	 */
	private  Long orgId;
	/**
	 * 建立人
	 */
	private Long createBy;

	/**
	 * 修改人
	 */
	private Long updateBy;
	/**
	 * 创建时间
	 */
	private Integer createTime;
	/**
	 * 更新时间
	 */
	private Integer updateTime;
	/**
	 * 删除时间
	 */
	private Integer deleteTime;

	/**
	 * 删除标识
	 */
	private String delFlag;
	/**
	 * 状态值:0=刚买入,1=出售中,2=已出售
	 */
	private Integer status;
	/**
	 * 状态值:0=当前订单,1=出售商品,2=定居
	 */
	private Integer bigKind;
	/**
	 * 关联等级
	 */
	private Long gradeId;
	/**
	 *
	 */
	private Integer sellTime;

	/**
	 * 订单号
	 */
	private String orderNo;
	/**
	 * 真实个人佣金
	 */
	private BigDecimal commission;

	/**
	 * 账号类型(1内部0外部)
	 */
	private Integer isNb;
	/**
	 * 业务员Id
	 */
	private Long salesmanId;

}
