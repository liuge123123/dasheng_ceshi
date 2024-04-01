package com.tcc.modules.exercise.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 用户兑换产品进度表
 * 
 * @author 
 * @email 
 * @date 2022-10-05 11:40:10
 */
@Data
@TableName("exercise_sign_progress")
public class SignProgressEntity implements Serializable {
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
	 * 客户名称
	 */
	private String custName;
	/**
	 * 签到产品ID
	 */
	private Long productId;
	/**
	 * 签到产品名称
	 */
	private String productName;
	/**
	 * 签到产品图片
	 */
	private String productImg;
	/**
	 * 每次夺宝消耗的积分
	 */
	private Integer productPrice;
	/**
	 * 已兑换次数
	 */
	private Integer signNum;
	/**
	 * 已兑换进度,总进度为1000000
	 */
	private Integer signProgress;
	/**
	 * 最后一次兑换时间
	 */
	private Integer lastTime;
	/**
	 * 下一次兑换是可以完成
	 */
	private Integer nextComplete;

	/**
	 *  账号类型(1内部0外部)
	 */
	private Integer isNb;
	/**
	 * 业务员Id（员工id）
	 */
	private Long salesmanId;

}
