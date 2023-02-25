package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T>, Iterable<T> {
    private T[] items;
    private int first;
    private int last;
    private int size;
    private static final int DEFAULT_CAPACITY = 8;

    public ArrayDeque() {
        this.items = (T[]) new Object[DEFAULT_CAPACITY];
        this.first = DEFAULT_CAPACITY / 2;
        this.last = DEFAULT_CAPACITY / 2;
        this.size = 0;
    }

    private void resize(int capacity) {
        T[] arr = (T[]) new Object[capacity];
        for (int i = 0, p = first; i < size; i++) {
            arr[i] = items[p];
            p = (p + 1) % items.length;
        }

        first = 0;
        last = size - 1;
        items = arr;
    }

    public void addFirst(T item) {
        if (size == items.length) {
            resize(items.length * 2);
        }

        if (!isEmpty()) {
            first = (first - 1) < 0 ? items.length - 1 : first - 1;
        }

        items[first] = item;
        size++;
    }

    public void addLast(T item) {
        if (size == items.length) {
            resize(items.length * 2);
        }

        if (!isEmpty()) {
            last = (last + 1) % items.length;
        }

        items[last] = item;
        size++;

    }

    public int size() {
        return size;
    }

    public void printDeque() {
        for (int i = 0, p = first; i < size; i++) {
            System.out.print(items[p] + " ");
            p = (p + 1) % items.length;
        }
    }

    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }

        if (size < items.length / 4) {
            resize(items.length / 4);
        }

        T item = items[first];
        if (size > 1) {
            first = (first + 1) % items.length;
        }
        size--;
        return item;
    }

    public T removeLast() {
        if (isEmpty()) {
            return null;
        }

        if (size < items.length / 4 ) {
            resize(items.length / 4);
        }

        T item = items[last];
        if (size > 1) {
            last = (last - 1) < 0 ? items.length - 1 : last - 1;
        }
        size--;
        return item;
    }

    public T get(int index) {
        for (int i = 0, p = first; i < size; i++) {
            if (i == index) {
                return items[p];
            }
            p = (p + 1) % items.length;
        }
        return null;
    }

    private class ArrayDequeIterator implements Iterator<T> {
        int i;
        int p;

        public ArrayDequeIterator() {
            i = 0;
            p = first;
        }

        @Override
        public boolean hasNext() {
            return i < size;
        }

        public T next() {
            T next =  items[p];
            i++;
            p = (p + 1) % items.length;
            return next;
        }
    }

    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null) {
            return false;
        }

        if (o instanceof Deque) {
            Deque<T> other = (Deque<T>) o;
            if (other.size() != size) {
                return false;
            }
            for (int i = 0; i < size; i++) {
                if (!this.get(i).equals(other.get(i))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}
