package com.tcc.modules.cust.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 下分设备列表
 * 
 * @author 
 * @email 
 * @date 2024-03-20 14:51:59
 */
@Data
@TableName("c_devices")
public class DevicesEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 设备ID
	 */
	@TableId
	private Long deviceId;
	/**
	 * 设备手机号码
	 */
	private String mobile;
	/**
	 * 钱包类型
	 */
	private String moneyType;
	/**
	 * 创建时间
	 */
	private Integer createTime;
	/**
	 * 更新时间
	 */
	private Integer updateTime;

	/**
	 * isOpen
	 */
	private Integer isOpen;

	/**
	 * 操作密码
	 */
	private String password;

}
