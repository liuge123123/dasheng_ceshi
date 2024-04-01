package com.tcc.modules.exercise.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 奖品信息表
 *
 * @author
 * @email
 * @date 2022-08-04 17:55:20
 */
@Data
@TableName("exercise_prize")
public class PrizeEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@TableId
	private Long id;
	/**
	 * 奖品类型
	 */
	private String prizeType;
	/**
	 * 奖品图片
	 */
	private String prizeImg;
	/**
	 * 奖品名称
	 */
	private String prizeName;
	/**
	 * 奖品数值
	 */
	private Integer prizeValue;
	/**
	 * 奖品数量
	 */
	private Integer prizeNum;
	/**
	 * 中奖几率
	 */
	private Integer ratio;

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
