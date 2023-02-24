package deque;

import java.util.Iterator;

public class LinkedListDeque<T> implements Iterable<T> {

    private final Node sentinel;
    private int size;

    public class Node {
        public T item;
        public Node next;
        public Node prev;

        public Node(T item, Node prev, Node next) {
            this.item = item;
            this.next = next;
            this.prev = prev;
        }
    }

    public LinkedListDeque() {
        this.sentinel = new Node(null, null, null);
        sentinel.prev = sentinel;
        sentinel.next = sentinel;
        this.size = 0;
    }

    public void addFirst(T item) {
        Node first = sentinel.next;
        Node node = new Node(item, sentinel, first);
        sentinel.next = node;
        first.prev = node;
        size++;
    }

    public void addLast(T item) {
        Node last = sentinel.prev;
        Node node = new Node(item, last, sentinel);
        sentinel.prev = node;
        last.next = node;
        size++;
    }

    public boolean isEmpty() {
        return sentinel.next == sentinel;
    }

    public int size() {
        return size;
    }

    public void printDeque() {
        Node p = sentinel.next;
        while (p != sentinel) {
            System.out.print(p.item + " ");
            p = p.next;
        }
        System.out.println();
    }

    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        Node first = sentinel.next;
        sentinel.next = first.next;
        sentinel.next.prev = sentinel;
        size--;
        return first.item;
    }

    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        Node last = sentinel.prev;
        sentinel.prev = last.prev;
        sentinel.prev.next = sentinel;
        size--;
        return last.item;
    }

    public T get(int index) {
        Node p = sentinel.next;
        int i = 0;
        while (p != sentinel) {
            if (i == index) {
                return p.item;
            }
            p = p.next;
        }
        return null;
    }

    public T getRecursive(int index) {
        if (isEmpty()) {
            return null;
        }
        return getRecursiveHelper(sentinel.next, index);
    }

    public T getRecursiveHelper(Node p, int index) {
        if (index == 0) {
            return p.item;
        }
        return getRecursiveHelper(p.next, index - 1);
    }

    private class LinkedListDequeIterator implements Iterator<T> {
        Node p;

        public LinkedListDequeIterator() {
            p = sentinel.next;
        }

        @Override
        public boolean hasNext() {
            return p != sentinel;
        }

        public T next() {
            T item = p.item;
            p = p.next;
            return item;
        }
    }

    public Iterator<T> iterator() {
        return new LinkedListDequeIterator();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null) {
            return false;
        }

        if (o instanceof LinkedListDeque) {
            LinkedListDeque<T> other = (LinkedListDeque<T>) o;
            if (other.size() != size) {
                return false;
            }
            for (int i = 0; i < size; i++) {
                if (other.get(i) != this.get(i)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

}
