package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Iterable<T>{
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

    public boolean isEmpty() {
        return size == 0;
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
        first = (first + 1) % items.length;
        size--;
        return item;
    }

    public T removeLast() {
        if (isEmpty()) {
            return null;
        }

        if (size < items.length / 4) {
            resize(items.length / 4);
        }

        T item = items[last];
        last = (last - 1) < 0 ? items.length - 1 : last - 1;
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
        int c;

        public ArrayDequeIterator() {
            c = 0;
        }

        @Override
        public boolean hasNext() {
            return c < size;
        }

        public T next() {
            T next =  items[c];
            c++;
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

        if (o instanceof ArrayDeque) {
            ArrayDeque<T> other = (ArrayDeque<T>) o;
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
