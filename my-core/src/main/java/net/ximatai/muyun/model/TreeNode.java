package net.ximatai.muyun.model;

import java.util.List;

public class TreeNode {
    private String id;
    private String label;
    private Object data;
    private List<TreeNode> children;

    public TreeNode() {
    }

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public Object getData() {
        return data;
    }

    public List<TreeNode> getChildren() {
        return children;
    }

    public TreeNode setId(String id) {
        this.id = id;
        return this;
    }

    public TreeNode setLabel(String label) {
        this.label = label;
        return this;
    }

    public TreeNode setData(Object data) {
        this.data = data;
        return this;
    }

    public TreeNode setChildren(List<TreeNode> children) {
        this.children = children;
        return this;
    }
}