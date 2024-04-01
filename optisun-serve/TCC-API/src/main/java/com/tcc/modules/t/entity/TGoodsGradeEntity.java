package com.tcc.modules.t.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * 商品等级表
 *
 * @author
 * @email
 * @date 2022-07-19 09:17:41
 */
@Data
@TableName("t_goods_grade")
public class TGoodsGradeEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@TableId
	private Long id;
	/**
	 * 等级名称
	 */
	private String gradeName;
	/**
	 * 金额
	 */
	private BigDecimal money;
	/**
	 *
	 */
	private BigDecimal parameter1;
	/**
	 *
	 */
	private String content;
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
	private  Long orgId;
	/**
	 * 删除标识(0-正常;1-已删除)
	 */
	private String delFlag;
	/**
	 *
	 */
	private String image;
	/**
	 * 购买次数
	 */
	private Integer gmNums;

	/**
	 * 会员等级
	 */
	private Integer gradeLevel;

	/**
	 * 状态(1打开 0关闭)
	 */
	private Integer status;

	/**
	 * 个人佣金比例
	 */
	private BigDecimal rate;

	/**
	 * 商城折扣
	 */
	private BigDecimal shopDiscount;

	/**
	 * Vipbanner
	 */
	private String vipBanner;

}
