package com.tcc.modules.content.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 公告
 * 
 * @author 
 * @email 
 * @date 2022-05-30 17:05:32
 */
@Data
@TableName("o_notice")
public class NoticeEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 公告Id
	 */
	@TableId
	private Long noticeId;
	/**
	 * 公告内容
	 */
	private String content;
	/**
	 * 状态(0-隐藏;1-正常)
	 */
	private Integer status;
	/**
	 * 接收人(*-所有人；指定接收人Id)
	 */
	private Long receiveId;
	/**
	 * 建立人
	 */
	private Long createBy;
	/**
	 * 建立时间
	 */
	private Integer createTime;

	/**
	 * 机构id
	 */
	private Long orgId;

}
