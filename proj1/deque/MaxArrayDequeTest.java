package deque;

import java.util.Comparator;

import org.junit.Test;
import static org.junit.Assert.*;
public class MaxArrayDequeTest {
    private class IntComparator implements Comparator<Integer> {
        public int compare(Integer a, Integer b) {
            return Integer.compare(a, b);
        }
    }

    @Test
    public void defCmpTest() {
        MaxArrayDeque<Integer> mad1 = new MaxArrayDeque<>(new IntComparator());
        for (int i = 0; i < 10; i++) {
            mad1.addLast(i);
        }
        mad1.removeLast();
        assertEquals(8, (int) mad1.max());
    }

    @Test
    public void cmpTest() {
        MaxArrayDeque<Integer> mad1 = new MaxArrayDeque<>(new IntComparator());
        for (int i = 0; i < 10; i++) {
            mad1.addLast(i);
        }
        assertEquals(9, (int) mad1.max(new IntComparator()));
    }

}
