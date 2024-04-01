package com.tcc.modules.l.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 非福利区订单次数记录
 * 
 * @author 
 * @email 
 * @date 2023-12-19 16:33:35
 */
@Data
@TableName("l_order_count")
public class OrderCountEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@TableId
	private Long id;
	/**
	 * 会员ID
	 */
	private Long custId;
	/**
	 * 产品级别 A-E对应1-5
	 */
	private Integer goodsLevel;
	/**
	 * 创建时间
	 */
	private Integer createTime;
	/**
	 * 更新时间
	 */
	private Integer updateTime;
	/**
	 * 产品订单次数
	 */
	private Integer goodsOrderCount;

}
