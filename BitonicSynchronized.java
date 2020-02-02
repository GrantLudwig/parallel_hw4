/*
 * Grant Ludwig
 * CPSC 4600, Seattle University
 * BitonicSynchronized.java
 * 2/2/20
 */

import java.util.concurrent.TimeUnit;
import java.util.concurrent.CyclicBarrier;

public class BitonicSynchronized {
    public static final int N = 1 << 22;  // size of the final sorted array (power of two)
    //public static final int N = 16;
    public static final int P = 2; // number of threads
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

        // single test
        double[] data = new double[N];
        data = RandomArrayGenerator.getArray(N);
//        System.out.println(" ");
//        for (int i = 0; i < data.length; i++) {
//            System.out.print(data[i] + " ");
//        }
//        System.out.println(" ");
        BitonicThreadLoop test = new BitonicThreadLoop(data, 0, N - 1);
        test.sort();
//        System.out.println(" ");
//        for (int i = 0; i < data.length; i++) {
//            System.out.print(data[i] + " ");
//        }
//        System.out.println(" ");
        if (!RandomArrayGenerator.isSorted(data) || N != data.length)
                System.out.println("failed");
    }
}
