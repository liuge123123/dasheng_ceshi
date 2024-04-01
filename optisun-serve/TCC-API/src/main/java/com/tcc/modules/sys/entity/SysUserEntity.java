

package com.tcc.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tcc.common.base.entity.BaseUserEntity;
import com.tcc.common.validator.group.AddGroup;
import com.tcc.common.validator.group.UpdateGroup;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 系统用户
 *
 */
@Data
@TableName("sys_user")
public class SysUserEntity extends BaseUserEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
	 * 用户ID
	 */
	@TableId
	private Long userId;

	/**
	 * 用户名
	 */
	@NotBlank(message="用户名不能为空", groups = {AddGroup.class, UpdateGroup.class})
	private String username;

	/**
	 * 密码
	 */
	@NotBlank(message="密码不能为空", groups = AddGroup.class)
	private String password;

	/**
	 * 盐
	 */
	private String salt;

	/**
	 * 邮箱
	 */
	@Email(message="邮箱格式不正确", groups = {AddGroup.class, UpdateGroup.class})
	private String email;

	/**
	 * 手机号
	 */
	private String mobile;

	/**
	 * 状态  0：禁用   1：正常
	 */
	private Integer status;

	/**
	 * 角色ID列表
	 */
	@TableField(exist=false)
	private List<Long> roleIdList;

	/**
	 * 创建者ID
	 */
	private Long createUserId;

	/**
	 * 创建时间
	 */
	private Integer createTime;

	/**
	 * 姓名
	 */
	private String name;

	/**
	 * 用户头像
	 */
	private String avatar;

	/**
	 * 类型，用户类型，0:超级管理员，1:平台账号，2:总代理账号;3:分代理账号；4普通员工账号
	 */
	private Integer type;

	/**
	 * 关联ID，，0:超级管理员，1:平台账号 关联id为0
	 * 其他关联为当前操作的用户id
	 */
	private Long relationId;

	/**
	 * 机构ID
	 */
	private Long orgId;

	/**
	 * 部门ID
	 */
	private Integer deptId;

	/**
	 * 工号
	 */
	private String jobNumber;

	/**
	 * 数据权限
	 */
	@TableField(exist = false)
	private List<Map<String, Object>> dataPermission;


	/**
	 * 客服地址
	 */
	private String customerUrl;

	/**
	 * 同时在线人数
	 */
	private Integer loginLimit;

}
