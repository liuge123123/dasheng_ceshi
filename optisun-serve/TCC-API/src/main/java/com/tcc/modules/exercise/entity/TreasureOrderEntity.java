package com.tcc.modules.exercise.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 宝箱记录
 * 
 * @author 
 * @email 
 * @date 2023-10-06 20:10:42
 */
@Data
@TableName("exercise_treasure_order")
public class TreasureOrderEntity implements Serializable {
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
	 * 宝箱ID
	 */
	private Long treasureId;
	/**
	 * 金额
	 */
	private BigDecimal amount;
	/**
	 * 状态：0-待领取，1-已领取
	 */
	private Integer status;
	/**
	 * 更新时间
	 */
	private Integer updateTime;
	/**
	 * 创建时间
	 */
	private Integer createTime;

}
