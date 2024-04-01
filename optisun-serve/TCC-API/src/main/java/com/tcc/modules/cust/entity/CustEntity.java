package com.tcc.modules.cust.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;

import com.tcc.modules.t.entity.TGoodsGradeEntity;
import lombok.Data;

/**
 * 会员表
 * 
 * @author 
 * @email 
 * @date 2022-09-20 20:30:22
 */
@Data
@TableName("c_cust")
public class CustEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 会员Id
	 */
	@TableId
	private Long custId;
	/**
	 * 上级ID
	 */
	private Long parentId;
	/**
	 * 上级ID列表
	 */
	private String parentIdList;
	/**
	 * 会员名(登陆名)
	 */
	private String custName;
	/**
	 * 昵称
	 */
	private String nickName;
	/**
	 * 手机区号
	 */
	private String mobileArea;
	/**
	 * 手机号
	 */
	private String mobile;
	/**
	 * 头像
	 */
	private String headImg;
	/**
	 * 密码
	 */
	private String pwd;
	/**
	 * 随机盐
	 */
	private String salt;
	/**
	 * 提现密码
	 */
	private String pwd1;
	/**
	 * 提现随机盐
	 */
	private String salt1;
	/**
	 * 会员等级
	 */
	private Long level;
	/**
	 * 上一会员等级
	 */
	private Long lastLevel;
	/**
	 * 等级升级时间
	 */
	private Integer levelUpTime;
	/**
	 * 业务员Id（员工id）
	 */
	private Long salesmanId;
	/**
	 *  账号类型(1内部0外部)
	 */
	private Integer isNb;
	/**
	 * status(1-启用;0-停用)
	 */
	private Integer status;
	/**
	 * 建立时间
	 */
	private Integer createTime;
	/**
	 * 建立人
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
	 * 删除(0正常 1删除)
	 */
	private String delFlag;
	/**
	 * 最后一次登录时间
	 */
	private Integer loginTime;
	/**
	 * 最后一次登录IP
	 */
	private String loginIp;
	/**
	 * 第一次注册加入IP
	 */
	private String joinIp;
	/**
	 * 第一次注册加入时间
	 */
	private Integer joinTime;
	/**
	 * 机构id
	 */
	private Long orgId;
	/**
	 * 连续签到天数
	 */
	private Integer continuousSignIn;
	/**
	 * 最大连续签到天数
	 */
	private Integer maxContinuousSignIn;
	/**
	 * 累计签到天数
	 */
	private Integer totalSignIn;
	/**
	 * 参与理财的次数
	 */
	private Integer joinCftNum;
	/**
	 * 理财产品收益
	 */
	private BigDecimal personCftMoney;
	/**
	 * 注册赠送金额
	 */
	private BigDecimal registerMoney;
	/**
	 * 累计充值金额
	 */
	private BigDecimal totalRechargeMoney;
	/**
	 * 充值余额
	 */
	private BigDecimal leftRechargeMoney;
	/**
	 * 累计佣金
	 */
	private BigDecimal totalCommissionMoney;
	/**
	 * 可提现佣金
	 */
	private BigDecimal leftCommissionMoney;
	/**
	 * 任务佣金
	 */
	private BigDecimal taskCommissionMoney;
	/**
	 * 团队佣金
	 */
	private BigDecimal teamCommissionMoney;
	/**
	 * vip解锁奖金
	 */
	private BigDecimal vipCommissionMoney;
	/**
	 * 基金分红
	 */
	private BigDecimal fundCommissionMoney;
	/**
	 * 其他佣金
	 */
	private BigDecimal otherCommissionMoney;
	/**
	 * 是否完成首充(0未完成 1完成)
	 */
	private Integer complete;

	/*
	* 总抽奖次数
	 */
	private Integer luckTotalNum;

	/*
	 * 剩余抽奖次数
	 */
	private Integer luckLeftNum;


	/**
	 * vip过期时间
	 */
	private Integer expireTime;

	/**
	 * 累计积分
	 */
	private Integer pointTotal;
	/**
	 * 剩余积分
	 */
	private Integer pointLeft;

	/**
	 * 提现限制，1：不允许提现，0：允许提现
	 */
	private Integer withdrawLimit;

	/**
	 * 任务限制，1：不允许做任务，0允许做任务 暂时取消掉
	 * 这里用于理财限制 1 允许理财 0 不允许理财
	 */
	private Integer taskLimit;

	/**
	 * 特权账号，0：不是，1 是
	 */
	private Integer isPrivilege;
	/**
	 * 备注
	 */
	private String remark;

	@TableField(exist = false)
	private TGoodsGradeEntity grade;

}
