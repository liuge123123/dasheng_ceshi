package com.tcc.common.base.dto;

import lombok.Data;

import java.util.List;

@Data
public class TreeNode {
    protected String id;
    protected String parentId;
    protected List<TreeNode> children = null;

    public void add(TreeNode node) {
        children.add(node);
    }
}