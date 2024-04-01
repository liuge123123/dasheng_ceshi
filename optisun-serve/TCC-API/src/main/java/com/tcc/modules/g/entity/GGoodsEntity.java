package com.tcc.modules.g.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 商品表
 *
 * @author
 * @email
 * @date 2022-07-21 17:55:20
 */
@Data
@TableName("g_goods")
public class GGoodsEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@TableId
	private Long id;
	/**
	 * 商品名称
	 */
	private String goodsName;
	/**
	 * 图片
	 */
	private String image;
	/**
	 * 价格
	 */
	private BigDecimal price;
	/**
	 * 关联会员等级
	 */
	private Long level;

	/**
	 * 机构Id
	 */
	private Long orgId;

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

}
