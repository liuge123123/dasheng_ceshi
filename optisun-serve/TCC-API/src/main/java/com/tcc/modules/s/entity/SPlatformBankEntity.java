package com.tcc.modules.s.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * 平台银行
 * 
 * @author 
 * @email 
 * @date 2022-07-19 16:34:54
 */
@Data
@TableName("s_platform_bank")
public class SPlatformBankEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@TableId
	private Long id;

	/**
	 * 姓名
	 */
	private String name;
	/**
	 * 卡号
	 */
	private String cardNo;
	/**
	 * 卡图片
	 */
	private String image;
	/**
	 * 创建时间
	 */
	private Integer createTime;
	/**
	 * 机构id
	 */
	private Long orgId;
	/**
	 * 创建人
	 */
	private Long createBy;
	/**
	 * 修改时间
	 */
	private Integer updateTime;
	/**
	 * 修改人
	 */
	private Long updateBy;
	/**
	 * 删除标示(0正常 1删除)
	 */
	private String delFlag;

	/**
	 * 字母标示
	 */
	private String letter;
	/**
	 * 费率
	 */
	private String fee;
	/**
	 * 最小接收金额
	 */
	private BigDecimal minMoney;
	/**
	 * 最大接收金额
	 */
	private BigDecimal maxMoney;


	/**
	 * 支付地址
	 */
	private String payUrl;

	/**
	 * 商户
	 */
	private String merchant;

	/**
	 * key
	 */
	private String merchantKey;

	/**
	 * 代付URl
	 */
	private String transferUrl;

	/**
	 * 支付编码
	 */
	private String payChannel;

	/**
	 * 代付编码
	 */
	private String transferChannel;

	/**
	 * 代付Key
	 */
	private String transferKey;

}
