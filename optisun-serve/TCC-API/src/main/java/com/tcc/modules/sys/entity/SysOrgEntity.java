package com.tcc.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import lombok.Data;

/**
 * 
 * 
 * @author 
 * @email 
 * @date 2021-04-19 11:25:56
 */
@Data
@TableName("sys_org")
public class SysOrgEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 编号
	 */
	@TableId
	private Integer id;
	/**
	 * 机构名称
	 */
	private String name;
	/**
	 * 机构介绍
	 */
	private String contents;
	/**
	 * 机构状态（1启用 0 停用）
	 */
	private Integer status;
	/**
	 * 机构logo
	 */
	private String logo;
	/**
	 * 小程序AppID
	 */
	private String appId;
	/**
	 * 小程序AppSecret
	 */
	private String appSecret;
	/**
	 * 微信商户号id
	 */
	private String mchid;
	/**
	 * 微信支付密钥
	 */
	private String apikey;
	/**
	 * 证书文件cert
	 */
	private String certPem;
	/**
	 * 证书文件key
	 */
	private String keyPem;
	/**
	 * 系统标题
	 */
	private String sysTitle;
	/**
	 * 系统logo
	 */
	private String sysLogo;

}
