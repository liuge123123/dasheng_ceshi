package com.tcc.modules.g.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 房间表
 *
 * @author
 * @email
 * @date 2022-07-21 17:55:20
 */
@Data
@TableName("g_room")
public class GRoomEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@TableId
	private Long id;
	/**
	 * 中文名称
	 */
	private String roomName;
	/**
	 * 简介
	 */
	private String roomDesc;
	/**
	 * 封面图
	 */
	private String image;
	/**
	 * 价格开始
	 */
	private BigDecimal priceStart;
	/**
	 * 价格结束
	 */
	private BigDecimal priceEnd;
	/**
	 * 佣金比例
	 */
	private BigDecimal comRate;
	/**
	 * 共计抢单
	 */
	private Integer allNum;
	/**
	 * 关联等级ID
	 */
	private Integer gradeId;
	/**
	 * 开启:1=是,0=否
	 */
	private Integer isOpen;
	/**
	 * 建立人
	 */
	private Long createBy;
	/**
	 * 修改人
	 */
	private Long updateBy;

	/**
	 * 机构Id
	 */
	private Long orgId;

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
