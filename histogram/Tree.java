package histogram;

// Import statements

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Tree<T> implements Iterable<Tree<T>> {
    private T data;
    private Tree<T> left, right;

    public Tree(T data, Tree<T> l, Tree<T> r) {
        this.data = data;
        left = l; right = r;
    }

    public Tree(T data) { this(data, null, null); }

    public void reset() {
        data = null;
        left = right = null;
    }

    public T getValue() { return data; }
    public void setValue(T data) { this.data = data; }

    public boolean hasLeft() { return left != null; }
    public Tree<T> getLeft() { return left; }
    public void setLeft(Tree<T> l) { left = l; }

    public boolean hasRight() { return right != null; }
    public Tree<T> getRight() { return right; }
    public void setRight(Tree<T> r) { right = r; }

    public boolean isInner() { return left != null || right != null; }
    public boolean isLeaf() { return left == null && right == null; }

    public List<Tree<T>> fringe() {
        List<Tree<T>> f = new ArrayList<Tree<T>>();
        addToFringe(f);

        return f;
    }

    private void addToFringe(List<Tree<T>> f) {
        if (isLeaf())
            f.add(this);
        else {
            if (hasLeft()) left.addToFringe(f);
            if (hasRight()) right.addToFringe(f);
        }
    }

    @Override
    public Iterator<Tree<T>> iterator() {
        return fringe().iterator();
    }
}
