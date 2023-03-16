package hashmap;

import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author YOUR NAME HERE
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    // You should probably define some more!
    int capacity = 16;
    double loadFactor = 0.75;
    int size = 0;

    /** Constructors */
    public MyHashMap() {
        buckets = createTable(capacity);
    }

    public MyHashMap(int initialSize) {
        capacity = initialSize;
        buckets = createTable(capacity);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        this(initialSize);
        loadFactor = maxLoad;
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new LinkedList<Node>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        Collection<Node>[] table = new Collection[tableSize];
        for (int i = 0; i < tableSize; i++) {
            table[i] = createBucket();
        }
        return table;
    }

    // TODO: Implement the methods of the Map61B Interface below
    // Your code won't compile until you do so!

    @Override
    public void clear() {
        buckets = createTable(capacity);
        size = 0;
    }

    @Override
    public boolean containsKey(K key) {
        return getNode(key) != null;
    }

    @Override
    public V get(K key) {
        Node item = getNode(key);
        if (item == null) {
            return null;
        }
        return item.value;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void put(K key, V value) {
        Node item = getNode(key);
        if (item != null) {
            item.value = value;
        }
        else {
            buckets[getIndex(key)].add(createNode(key, value));
            size += 1;
        }
    }

    @Override
    public Set<K> keySet() {
        Set<K> res = new TreeSet<>();
        for (Collection<Node> bucket : buckets) {
            for (Node item : bucket) {
                res.add(item.key);
            }
        }
        return res;
    }

    private int getIndex(K key) {
        return Math.floorMod(key.hashCode(), capacity);
    }

    private Node getNode(K key) {
        for (Node item : buckets[getIndex(key)]) {
            if (item.key.equals(key)) {
                return item;
            }
        }
        return null;
    }

    @Override
    public Iterator<K> iterator() {
        return new MyHashMapIter();
    }

    private class MyHashMapIter implements Iterator<K> {
        private Iterator<K> keyIter;

        MyHashMapIter() {
            keyIter = keySet().iterator();
        }

        @Override
        public boolean hasNext() {
           return keyIter.hasNext();
        }

        @Override
        public K next() {
           if (!hasNext()) {
               throw new NoSuchElementException();
           }
           return keyIter.next();
        }

    }

    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }

}
