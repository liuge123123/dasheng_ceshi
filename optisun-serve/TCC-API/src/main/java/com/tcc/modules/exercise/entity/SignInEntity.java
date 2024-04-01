package com.tcc.modules.exercise.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 签到记录表
 *
 * @author
 * @email
 * @date 2022-08-06 10:55:20
 */
@Data
@TableName("exercise_sign_in")
public class SignInEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@TableId
	private Long id;
	/**
	 * 签到时间
	 */
	private Long custId;
	/**
	 * 签到时间
	 */
	private Date signInTime;
	/**
	 * 奖励积分
	 */
	private Integer pointNum;
	/**
	 * 赠送金额
	 */
	private BigDecimal giftAmount;
	/**
	 * 累计天数
	 */
	private Integer cumulativeDays;
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
	 * 删除标识(0-正常;1-删除)
	 */
	private String delFlag;

}
