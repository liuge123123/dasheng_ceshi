package com.tcc.modules.l.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 理财房间
 * 
 * @author 
 * @email 
 * @date 2022-12-21 10:37:42
 */
@Data
@TableName("l_room")
public class LRoomEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	@TableId
	private Long id;
	/**
	 * 名称
	 */
	private String name;
	/**
	 * 图片
	 */
	private String img;
	/**
	 * 说明
	 */
	private String remark;
	/**
	 * 持有天数
	 */
	private Integer days;
	/**
	 * 受益率
	 */
	private BigDecimal rate;
	/**
	 * 可申购数量
	 */
	private Integer maxNum;
	/**
	 * 排序
	 */
	private Integer sort;

	/**
	 * 是否体验房 0-否，1-是
	 */
	private Integer isFund;

	/**
	 * 领取周期(时)
	 */
	private Integer receiveCircle;

	/**
	 * 金额限制
	 */
	private BigDecimal buyLimit;

}
