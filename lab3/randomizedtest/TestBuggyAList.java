package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
  // YOUR TESTS HERE
    @Test
    public void testThreeAddThree() {
        AListNoResizing<Integer> a = new AListNoResizing<>();
        BuggyAList<Integer> b = new BuggyAList<>();

        a.addLast(4); a.addLast(5); a.addLast(6);
        b.addLast(4); b.addLast(5); b.addLast(6);

        Assert.assertEquals(a.removeLast(), b.removeLast());
        Assert.assertEquals(a.removeLast(), b.removeLast());
        Assert.assertEquals(a.removeLast(), b.removeLast());
    }

    @Test
    public void randomizedTest() {
        AListNoResizing<Integer> a = new AListNoResizing<>();
        BuggyAList<Integer> b = new BuggyAList<>();

        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                a.addLast(randVal);
                b.addLast(randVal);
                System.out.println("addLast(" + randVal + ")");
            } else if (operationNumber == 1) {
                // size
                int x = a.size();
                int y = b.size();
                System.out.println("size: " + x);
                Assert.assertEquals(x, y);
            } else if (a.size() > 0) {
                if (operationNumber == 2) {
                    // getLast
                    int x = a.getLast();
                    int y = b.getLast();
                    System.out.println("getLast() -> " + x);
                    Assert.assertEquals(x, y);
                } else {
                    // removeLast
                    int x = a.removeLast();
                    int y = b.removeLast();
                    System.out.println("removeLast() -> " + x);
                    Assert.assertEquals(x, y);
                }
            }
        }
    }
}
