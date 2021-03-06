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

/**
 * @class BitonicSynchronized runner class for testing threaded bitonic sort
 */
public class BitonicSynchronized {
    public static final int N = 1 << 22;  // size of the final sorted array (power of two)
    public static final int P = 16; // number of threads
    public static final int TIME_ALLOWED = 10;  // seconds

    public static double[] data; // public for BitonicThreadLoop to use

    /**
     * @param args not used
     */
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        int work = 0;
        Thread[] sortThreads = new Thread[P];
        CyclicBarrier smallBarrier = new CyclicBarrier(P);
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

            sortThreads[i] = new Thread(new BitonicThreadLoop(smallBarrier, largeBarrier, newSortbarrier, startIndex, endIndex));
            sortThreads[i].start();
            startIndex = endIndex + 1; // set start index for next sorter
        }

        // test
        while (System.currentTimeMillis() < start + TIME_ALLOWED * 1000) {
            try {
                double[] newArray = RandomArrayGenerator.getArray(N); // create new random array while threads are sorting
                newSortbarrier.await(); // wait for sort to complete

                double[] sortedArray = data;
                data = newArray;
                newSortbarrier.await(); // new data array created

                if (!RandomArrayGenerator.isSorted(sortedArray) || N != sortedArray.length)
                    System.out.println("failed");
                work++;
            } catch (InterruptedException ex) {
                return;
            } catch (BrokenBarrierException ex) {
                return;
            }
        }

        System.out.println("sorted " + work + " arrays (each: " + N + " doubles) in "
                + TIME_ALLOWED + " seconds");

        // stop threads
        for (int i = 0; i < P; i++)
            sortThreads[i].interrupt();
    }
}
