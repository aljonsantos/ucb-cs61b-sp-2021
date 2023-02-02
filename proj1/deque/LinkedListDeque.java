package deque;

public class LinkedListDeque<T> {

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
}
