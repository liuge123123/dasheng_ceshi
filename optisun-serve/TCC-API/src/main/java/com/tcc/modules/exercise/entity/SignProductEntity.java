package com.tcc.modules.exercise.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 积分兑换产品信息表
 * 
 * @author 
 * @email 
 * @date 2022-10-05 11:40:10
 */
@Data
@TableName("exercise_sign_product")
public class SignProductEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 产品ID
	 */
	@TableId
	private Long id;
	/**
	 * 产品名称
	 */
	private String name;
	/**
	 * 产品图片
	 */
	private String img;
	/**
	 * 每次夺宝消耗的积分
	 */
	private Integer price;

}
