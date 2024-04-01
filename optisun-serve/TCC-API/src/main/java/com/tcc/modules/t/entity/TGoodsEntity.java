package com.tcc.modules.t.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 退单商品表
 *
 * @author
 * @email
 * @date 2022-07-19 09:17:41
 */
@Data
@TableName("t_goods")
public class TGoodsEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@TableId
	private Integer id;
	/**
	 * 关联等级
	 */
	private Integer gradeId;
	/**
	 * 商品名称
	 */
	private String goodsName;
	/**
	 * 价格
	 */
	private BigDecimal price;
	/**
	 * 预期回报
	 */
	private BigDecimal estimateprice;
	/**
	 * 周期
	 */
	private String periodId;
	/**
	 * 图片
	 */
	private String avatar;
	/**
	 * 图片组
	 */
	private String images;
	/**
	 * 状态值:0=当前订单,1=出售商品,2=定居
	 */
	private Integer status;
	/**
	 * 完成
	 */
	private String completeNum;

	/**
	 * 建立人
	 */
	private Long createBy;

	/**
	 * 修改人
	 */
	private Long updateBy;

	/**
	 * 剩余
	 */
	private String lessNum;
	/**
	 * 是否超级商品
	 */
	private Integer isVip;
	/**
	 *
	 */
	private Integer weigh;
	/**
	 * 创建时间
	 */
	private Integer createTime;
	/**
	 * 更新时间
	 */
	private Integer updateTime;

	/**
	 * 机构Id
	 */
	private  Long orgId;

	/**
	 *
	 */
	private String delFlag;


	/**
	 * 库存数量
	 */
	private Integer stockNum;

}
