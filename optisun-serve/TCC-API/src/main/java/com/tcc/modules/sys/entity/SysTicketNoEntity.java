package com.tcc.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import lombok.Data;

/**
 * 取号记录表
 * 
 * @author 
 * @email 
 * @date 2021-01-19 18:13:32
 */
@Data
@TableName("sys_ticket_no")
public class SysTicketNoEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	@TableId
	private Integer id;
	/**
	 * 业务类型
	 */
	private String type;
	/**
	 * 业务日期，yyyy-MM-dd
	 */
	private String businessDate;
	/**
	 * 业务流水号
	 */
	private Integer ticketNo;
	/**
	 * 机构ID
	 */
	private Long  orgId;
	/**
	 * 状态
	 */
	private Integer status;
	/**
	 * 创建者
	 */
	private Integer createBy;
	/**
	 * 创建时间
	 */
	private Integer createTime;
	/**
	 * 更新者
	 */
	private Integer updateBy;
	/**
	 * 更新时间
	 */
	private Integer updateTime;

}
