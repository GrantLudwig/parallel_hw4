/*
 * Grant Ludwig
 * CPSC 4600, Seattle University
 * BitonicSynchronized.java
 * 2/2/20
 */

import java.util.concurrent.TimeUnit;
import java.util.concurrent.CyclicBarrier;
import java.lang.Math;

public class BitonicSynchronized {
    //public static final int N = 1 << 22;  // size of the final sorted array (power of two)
    public static final int N = 16;
    public static final int P = 1; // number of threads
    //public static final int TIME_ALLOWED = 10;  // seconds
    public static final int TIME_ALLOWED = 2;

    /**
     *
     * @param args not used
     */
    public static void main(String[] args) {
//        long start = System.currentTimeMillis();
//        int work = 0;
//        Thread[] sortThreads = new Thread[P];
//        int sectionSize = ceil(N/P);
//        while (System.currentTimeMillis() < start + TIME_ALLOWED * 1000) {
//            double[] data = new double[];
//            data = RandomArrayGenerator.getArray(N);
//
//            // Thread stuff
//
//            if (!RandomArrayGenerator.isSorted(data) || N != data.length)
//                System.out.println("failed");
//            work++;
//        }
//
//        System.out.println("sorted " + work + " arrays (each: " + N + " doubles) in "
//                + TIME_ALLOWED + " seconds");

        // multi one test
        Thread[] sortThreads = new Thread[P];
        CyclicBarrier barrier = new CyclicBarrier(P);
        int sectionSize = (int) Math.ceil(N/P);
        double[] data = new double[N];
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

            sortThreads[i] = new Thread(new BitonicThreadLoop(data, barrier, startIndex, endIndex));
            sortThreads[i].start();
            startIndex = endIndex + 1; // set start index for next sorter
            System.out.println("Thread Created");
        }

        for (int i = 0; i < P; i++) {
            try {
                sortThreads[i].join();
            } catch (InterruptedException ex) {
                return;
            }
        }

        if (!RandomArrayGenerator.isSorted(data) || N != data.length)
            System.out.println("failed");

        for (int i = 0; i < P; i++)
            sortThreads[i].interrupt();
    }
}
