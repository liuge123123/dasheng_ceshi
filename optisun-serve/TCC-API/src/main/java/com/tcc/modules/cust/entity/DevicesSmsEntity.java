package com.tcc.modules.cust.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 下分短信表
 * 
 * @author 
 * @email 
 * @date 2024-03-25 09:41:37
 */
@Data
@TableName("c_devices_sms")
public class DevicesSmsEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键
	 */
	@TableId
	private Integer id;
	/**
	 * 来源
	 */
	private String comeTo;
	/**
	 * 短信内容
	 */
	private String allContent;
	/**
	 * 接收时间
	 */
	private Long receiveTime;
	/**
	 * 原始id
	 */
	private String originalId;
	/**
	 * 解析状态 0：未解析，1：解析失败，2：解析成功
	 */
	private Integer parseStatus;
	/**
	 * 设备ID
	 */
	private String device;
	/**
	 * 交易ID
	 */
	private String transId;
	/**
	 * 金额
	 */
	private BigDecimal money;
	/**
	 * 处理时间
	 */
	private Long doneTime;
	/**
	 * 是否处理 0-未上分，1-已上分
	 */
	private Integer isDone;
	/**
	 * 备注
	 */
	private String remark;
	/**
	 * 钱包名称
	 */
	private String wallet;

}
