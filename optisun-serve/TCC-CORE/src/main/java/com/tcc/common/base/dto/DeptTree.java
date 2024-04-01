package com.tcc.common.base.dto;

import lombok.Data;

@Data
public class DeptTree extends TreeNode {
    private String name;
    private Integer type; //0部门 1员工
    private Boolean disabled; //是否禁用
}
