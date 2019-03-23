package util.tree;

import com.google.common.collect.ImmutableList;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class TreeNode<T> implements Iterable<TreeNode<T>>, Serializable {

    private T data;
    private transient TreeNode<T> parent;
    private List<TreeNode<T>> children;

    public ImmutableList<TreeNode<T>> getChildren(){
        return ImmutableList.copyOf(children);
    };

    public boolean isRoot() {
        return parent == null;
    }

    public boolean isLeaf() {
        return children.size() == 0;
    }

    public TreeNode(T data) {
        this.data = data;
        this.children = new LinkedList<>();
    }

    public TreeNode<T> addChild(T child) {
        TreeNode<T> childNode = new TreeNode<T>(child);
        childNode.parent = this;
        this.children.add(childNode);
        return childNode;
    }

    public int getLevel() {
        if (this.isRoot())
            return 0;
        else
            return parent.getLevel() + 1;
    }

    @Override
    public String toString() {
        return data != null ? data.toString() : "[data null]";
    }

    @Override
    public Iterator<TreeNode<T>> iterator() {
        TreeNodeIter<T> iter = new TreeNodeIter<T>(this);
        return iter;
    }

    public int height(){
        if(children.isEmpty()){
            return 0;
        }else {
            return 1 + children.stream().map(TreeNode::height).max(Comparator.naturalOrder()).get();
        }
    }

    public T getData() {
        return data;
    }
}
