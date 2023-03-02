package bstmap;

import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {
    private class Node {
        public K key;
        private V value;
        private Node right;
        private Node left;

        public Node(K key, V value) {
            this.key = key;
            this.value = value;
            this.right = null;
            this.left = null;
        }
    }

    private Node root;
    private int size;

    public BSTMap() {
        root = null;
        size = 0;
    }

    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    @Override
    public void put(K key, V value) {
        if (root == null) {
            root = new Node(key, value);
        }
        else if (!containsKey(key)) {
            put(key, value, root);
        }
        size += 1;
    }

    private Node put(K key, V value, Node t) {
        if (t == null) {
            return new Node(key, value);
        }
        if (key.compareTo(t.key) < 0) {
            t.left = put(key, value, t.left);
        }
        else if (key.compareTo(t.key) > 0) {
            t.right = put(key, value, t.right);
        }
        return t;
    }

    @Override
    public boolean containsKey(K key) {
        return containsKey(key, root);
    }

    private boolean containsKey(K key, Node t) {
        if (t == null) {
            return false;
        }
        if (key.equals(t.key)) {
            return true;
        }
        else if (key.compareTo(t.key) < 0) {
            return containsKey(key, t.left);
        }
        else {
            return containsKey(key, t.right);
        }
    }

    @Override
    public V get(K key) {
        return get(key, root);
    }

    private V get(K key, Node t) {
        if (t == null) {
            return null;
        }
        if (key.equals(t.key)) {
            return t.value;
        }
        else if (key.compareTo(t.key) < 0) {
            return get(key, t.left);
        }
        else {
            return get(key, t.right);
        }
    }

    @Override
    public int size() {
        return size;
    }

    public void printInOrder() {
        printInOrder(root);
    }

    private void printInOrder(Node t) {
        if (t == null) {
            return;
        }
        printInOrder(t.left);
        System.out.println("{" + t.key + " : " + t.value + "}");
        printInOrder(t.right);
    }

    @Override
    public Set<K> keySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<K> iterator() {
        throw new UnsupportedOperationException();
    }

}
