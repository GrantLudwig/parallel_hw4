/*
 * Grant Ludwig
 * CPSC 4600, Seattle University
 * BitonicSynchronized.java
 * 2/2/20
 */

import java.util.concurrent.TimeUnit;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.BrokenBarrierException;
import java.lang.Math;

public class BitonicSynchronized {
    //public static final int N = 1 << 22;  // size of the final sorted array (power of two)
    public static final int N = 16; // needs to be a multiple of 2
    public static final int P = 8; // number of threads
    //public static final int TIME_ALLOWED = 10;  // seconds
    public static final int TIME_ALLOWED = 10;
    public static double[] data;

    /**
     *
     * @param args not used
     */
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        int work = 0;
        Thread[] sortThreads = new Thread[P];
        CyclicBarrier smallBarrier1 = new CyclicBarrier(P/2);
        CyclicBarrier smallBarrier2 = new CyclicBarrier(P/2);
        CyclicBarrier largeBarrier = new CyclicBarrier(P);
        CyclicBarrier newSortbarrier = new CyclicBarrier(P + 1);
        int sectionSize = (int) Math.ceil(N/P);
        data = new double[N];

        data = RandomArrayGenerator.getArray(N);
        int startIndex = 0;
        // setup threads
        for (int i = 0; i < P; i++) {
            int     endIndex,
                    calcIndex = startIndex + sectionSize - 1;

            // setup endIndex
            if (calcIndex >= N)
                endIndex = N - 1;
            else
                endIndex = calcIndex;

            if (i < P/2)
                sortThreads[i] = new Thread(new BitonicThreadLoop(smallBarrier1, largeBarrier, newSortbarrier, startIndex, endIndex, "thread " + i));
            else
                sortThreads[i] = new Thread(new BitonicThreadLoop(smallBarrier2, largeBarrier, newSortbarrier, startIndex, endIndex, "thread " + i));
            sortThreads[i].start();
            startIndex = endIndex + 1; // set start index for next sorter
        }


        while (System.currentTimeMillis() < start + TIME_ALLOWED * 1000) {
            try {
                double[] newArray = RandomArrayGenerator.getArray(N);
                newSortbarrier.await(); // wait for sort to complete

                if (!RandomArrayGenerator.isSorted(data) || N != data.length)
                    System.out.println("failed");
                data = newArray;
                newSortbarrier.await(); // new data array created
                work++;
            } catch (InterruptedException ex) {
                return;
            } catch (BrokenBarrierException ex) {
                return;
            }
        }

        System.out.println("sorted " + work + " arrays (each: " + N + " doubles) in "
                + TIME_ALLOWED + " seconds");

        for (int i = 0; i < P; i++)
            sortThreads[i].interrupt();
    }
}
