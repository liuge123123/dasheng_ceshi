package com.tcc.modules.s.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 短信表
 * 
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName("s_sms")
public class SSmsEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键
	 */
	@TableId
	private Long id;
	/**
	 * 交易ID
	 */
	private String transId;
	/**
	 * 交易时间
	 */
	private String transDate;
	/**
	 * 充值金额
	 */
	private BigDecimal money;

	/**
	 * 来自
	 */
	private String comeTo;

	/**
	 * 全文
	 */
	private String allContent;
	/**
	 * 是否处理
	 */
	private Integer isDone;
	/**
	 * 接收时间
	 */
	private Long receiveTime;
	/**
	 * 处理时间
	 */
	private Long doneTime;

	/**
	 * 关联短信id
	 */
	private Long rel_id ;

	/**
	 * 添加备注
	 */
	private String remark;

	/**
	 * 钱包名称
	 */
	private String wallet;

	public SSmsEntity(String transId,String transDate, BigDecimal money, String comeTo, String allContent, Integer isDone, Long receiveTime ,Long rel_id,String wallet) {
		this.transId = transId;
		this.transDate = transDate;
		this.money = money;
		this.comeTo = comeTo;
		this.allContent = allContent;
		this.isDone = isDone;
		this.receiveTime = receiveTime;
		this.rel_id = rel_id;
		this.wallet = wallet;
	}
}
