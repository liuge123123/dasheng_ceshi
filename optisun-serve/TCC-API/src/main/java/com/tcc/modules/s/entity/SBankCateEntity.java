package com.tcc.modules.s.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 银行种类
 * 
 * @author 
 * @email 
 * @date 2022-07-19 13:56:50
 */
@Data
@TableName("s_bank_cate")
public class SBankCateEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@TableId
	private Long id;
	/**
	 * 名称
	 */
	private String name;
	/**
	 * 图标
	 */
	private String logo;
	/**
	 * 字母标示
	 */
	private String letter;
	/**
	 * 费率
	 */
	private String fee;
	/**
	 * 是否开启充值
	 */
	private Integer rechargeOpen;

	/**
	 * 是否开启提现
	 */
	private Integer cashoutOpen;

	/**
	 * 创建人id
	 */
	private Long createBy;
	/**
	 * 修改人id
	 */
	private Long updateBy;
	/**
	 * 删除标示(0正常1删除)
	 */
	private String delFlag;
	/**
	 * 机构id
	 */
	private Long orgId;
	/**
	 * 更新时间
	 */
	private Integer updateTime;
	/**
	 * 创建时间
	 */
	private Integer createTime;

	/**
	 * 前端显示名称
	 */
	private String showName;

	/**
	 * 排序
	 */
	private Integer sort;

}
