package deque;

public class ArrayDeque<T> {
    private T[] items;
    private int first;
    private int last;
    private int size;

    public ArrayDeque() {
        this.items = (T[]) new Object[4];
        this.first = -1;
        this.last = -1;
        this.size = 0;
    }

    private void resize(int capacity) {
        T[] arr = (T[]) new Object[capacity];
        int p = first, i = 0;

        while (p != last) {
            arr[i] = items[p];
            p = (p + 1) % items.length;
            i++;
        }

        arr[i] = items[p];
        first = 0;
        last = i;
        items = arr;
    }

    public void addFirst(T item) {
        if (size == items.length) {
            resize(items.length * 2);
        }

        if (isEmpty()) {
            first = 0;
            last = 0;
        } else {
            first = (first - 1) < 0 ? items.length - 1 : first - 1;
        }

        items[first] = item;
        size++;
    }

    public void addLast(T item) {
        if (size == items.length) {
            resize(items.length * 2);
        }

        if (isEmpty()) {
            first = 0;
            last = 0;
        } else {
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
        int p = first;
        while (p != last) {
            System.out.print(items[p] + " ");
            p = (p + 1) % items.length;
        }
        System.out.println(items[p]);
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
        int p = first;
        do {
            if (p == index) {
                return items[p];
            }
            p = (p + 1) % items.length;
        } while (p != last);

        return null;
    }
}
