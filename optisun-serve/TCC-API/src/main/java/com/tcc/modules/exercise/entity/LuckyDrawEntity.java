package com.tcc.modules.exercise.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 抽奖信息表
 *
 * @author
 * @email
 * @date 2022-08-04 17:55:20
 */
@Data
@TableName("exercise_lucky_draw")
public class LuckyDrawEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@TableId
	private Long id;
	/**
	 * 活动名称
	 */
	private String name;
	/**
	 * 每人每天最大参数次数
	 */
	private Integer maxNum;
	/**
	 * 开始时间
	 */
	private Date startTime;
	/**
	 * 结束时间
	 */
	private Date endTime;
	/**
	 * 描述
	 */
	private String description;

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
	 * 奖品信息
	 */
	@TableField(exist = false)
	private List<PrizeEntity> prizeList;

	/**
	 * 今日参与次数
	 */
	@TableField(exist = false)
	private Integer todayNum;

}
