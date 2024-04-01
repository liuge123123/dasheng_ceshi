package com.tcc.modules.exercise.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 宝箱派送
 * 
 * @author 
 * @email 
 * @date 2023-10-06 11:15:29
 */
@Data
@TableName("exercise_treasure")
public class TreasureEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@TableId
	private Long id;
	/**
	 * 用户ID
	 */
	private Long uid;
	/**
	 * 类型：1-固定金额，0-随机金额
	 */
	private Integer type;
	/**
	 * 总金额
	 */
	private BigDecimal amount;
	/**
	 * 数量
	 */
	private Integer number;
	/**
	 * 红包码
	 */
	private String code;
	/**
	 * 状态：0-未抢完，1-已抢完
	 */
	private Integer status;
	/**
	 * 创建时间
	 */
	private Integer createTime;


}
