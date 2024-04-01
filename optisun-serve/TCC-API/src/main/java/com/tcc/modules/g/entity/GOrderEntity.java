package com.tcc.modules.g.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 订单表
 * 
 * @author 
 * @email 
 * @date 2022-09-20 20:30:21
 */
@Data
@TableName("g_order")
public class GOrderEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@TableId
	private Long id;
	/**
	 * 订单号
	 */
	private String orderNo;
	/**
	 * 订单金额
	 */
	private BigDecimal orderMoney;
	/**
	 * 用户Id
	 */
	private Long custId;
	/**
	 * 关联商品ID
	 */
	private Long goodsId;
	/**
	 * 商品名称
	 */
	private String goodsName;
	/**
	 * 商品图片
	 */
	private String goodsImg;
	/**
	 * 佣金
	 */
	private BigDecimal commission;
	/**
	 * 机构Id
	 */
	private Long orgId;
	/**
	 * 状态:1=待处理,2=已完成
	 */
	private Integer status;
	/**
	 * 建立人
	 */
	private Long createBy;
	/**
	 * 修改人
	 */
	private Long updateBy;
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
	/**
	 *  账号类型(1内部0外部)
	 */
	private Integer isNb;
	/**
	 * 业务员Id（员工id）
	 */
	private Long salesmanId;

	/**
	 * 客户名称
	 */
	private String custName;

	/***
	 * 商品关联的等级
	 */
	private Long level;

}
