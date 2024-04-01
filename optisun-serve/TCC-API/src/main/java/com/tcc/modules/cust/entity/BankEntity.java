package com.tcc.modules.cust.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * 客户银行卡
 * 
 * @author 
 * @email 
 * @date 2022-07-19 13:53:03
 */
@Data
@TableName("c_bank")
public class BankEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@TableId
	private Long id;
	/**
	 * 客户ID
	 */
	private Long custId;
	/**
	 * 银行名称
	 */
	private String bankName;
	/**
	 * 银行代码
	 */
	private String code;
	/**
	 * 持卡人
	 */
	private String name;
	/**
	 * 账号
	 */
	private String account;
	/**
	 * IBAN
	 */
	private String iban;
	/**
	 * 出生
	 */
	private String chusheng;
	/**
	 * 参数1
	 */
	private String parameter1;
	/**
	 * 图片
	 */
	private String image;
	/**
	 * 状态值:0=待审,1=通过,2=驳回
	 */
	private Integer type;
	/**
	 * 创建时间
	 */
	private Integer createTime;
	/**
	 * 更新时间
	 */
	private Integer updateTime;

	/**
	 * 银行种类名称
	 */
	private Long cateId;

	/**
	 * 银行种类名称
	 */
	private String cateName;
	/**
	 * 机构id
	 */
	private long orgId;
	/**
	 * 删除(0正常 1删除)
	 */
	private String delFlag;
	/**
	 * 创建人id
	 */
	private Long createBy;
	/**
	 * 修改人id
	 */
	private Long updateBy;

	/**
	 * 备注
	 */
	private String remark;

}
