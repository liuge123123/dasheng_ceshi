package com.tcc.modules.exercise.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 抽奖记录
 *
 * @author
 * @email
 * @date 2022-08-04 17:55:20
 */
@Data
@TableName("exercise_record")
public class RecordEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@TableId
	private Long id;
	/**
	 * 抽奖信息ID
	 */
	private Long luckyDrawId;
	/**
	 * 奖品ID
	 */
	private Long prizeId;
	/**
	 * 用户ID
	 */
	private Long userId;
	/**
	 * 用户名称
	 */
	private String userName;
	/**
	 * 抽奖时间
	 */
	private Date drawTime;
	/**
	 * 是否中奖
	 */
	private Integer isHit;

	/**
	 * 中奖奖品
	 */
	private String hitPrize;
	/**
	 * 奖品图片
	 */
	private String hitPrizeImg;
	/**
	 * 奖品价值
	 */
	private Integer hitPrizeVal;

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
