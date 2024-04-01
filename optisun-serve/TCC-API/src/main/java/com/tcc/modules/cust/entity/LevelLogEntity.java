package com.tcc.modules.cust.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 用户升降级表
 * 
 * @author 
 * @email 
 * @date 2022-09-20 20:45:17
 */
@Data
@TableName("c_level_log")
public class LevelLogEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	@TableId
	private Long id;
	/**
	 * 客户ID
	 */
	private Long custId;
	/**
	 * 升级之前等级
	 */
	private Long beforLevel;
	/**
	 * 升级后等级
	 */
	private Long afterLevel;
	/**
	 * 升级时间
	 */
	private Integer createTime;
	/**
	 * 是否为内部客户
	 */
	private Integer isNb;
	/**
	 * 业务员ID
	 */
	private Long salesmanId;
	/**
	 * 消耗金额
	 */
	private BigDecimal useMoney;

}
