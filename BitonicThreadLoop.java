/*
 * Grant Ludwig
 * CPSC 4600, Seattle University
 * BitonicThreadLoop.java
 * 2/2/20
 */

//import java.util.concurrent.TimeUnit;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.BrokenBarrierException;

public class BitonicThreadLoop implements Runnable {
    private static final int timeout = 10;  // in seconds

    private double[] data;
    private CyclicBarrier smallBarrier;
    private CyclicBarrier largeBarrier;
    private CyclicBarrier newSortbarrier;
    private int startIndex,
                endIndex;

    public BitonicThreadLoop(double[] data, CyclicBarrier smallBarrier, CyclicBarrier largeBarrier, CyclicBarrier newSortbarrier, int startIndex, int endIndex) {
        this.data = data;
        this.smallBarrier = smallBarrier;
        this.largeBarrier = largeBarrier;
        this.newSortbarrier = newSortbarrier;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    /**
     * Constructor for use in threaded version
     * @param barrier
     * @param newSortbarrier
     * @param startIndex
     * @param endIndex
     */
    public BitonicThreadLoop(CyclicBarrier smallBarrier, CyclicBarrier largeBarrier, CyclicBarrier newSortbarrier, int startIndex, int endIndex) {
        this.barrier = barrier;
        this.largeBarrier = largeBarrier;
        this.newSortbarrier = newSortbarrier;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    public void sort() {
        for (int k = 2; k <= data.length; k *= 2) { // k is one bit, marching to the left
            // j is the distance between the first and second halves of the merge
            // corresponds to 1<<p in textbook
            for (int j = k / 2; j > 0; j /= 2) {  // j is one bit, marching from k to the right
                // i is the merge element
                for (int i = startIndex; i <= endIndex; i++) {
                    int ixj = i ^ j;  // xor: all the bits that are on in one and off in the other
                    // only compare if ixj is to the right of i
                    if (ixj > i) {
                        if ((i & k) == 0 && data[i] > data[ixj])
                            swap(i, ixj);
                        if ((i & k) != 0 && data[i] < data[ixj])
                            swap(i, ixj);
                    }
                }
                try {
                    if (k == data.length) {
                        System.out.println("In large K: " + startIndex);
                        barrier[1].await();
                    }
                    else {
                        System.out.println("small: " + startIndex);
                        barrier[0].await();
                    }
                } catch (InterruptedException ex) {
                    return;
                } catch (BrokenBarrierException ex) {
                    return;
                }
            }
        }
    }

    private void swap(int firstIndex, int secondIndex) {
        double temp = data[firstIndex];
        data[firstIndex] = data[secondIndex];
        data[secondIndex] = temp;
    }

    /***
     * Run when used in a thread
     */
    @Override
    public void run() {
        while(true) {
            data = BitonicSynchronized.data; // get the data array to be sorted
            sort();
            System.out.println("Complete");
            try {
                newSortbarrier.await(); // sort completed
                newSortbarrier.await(); // new data array is created
            } catch (InterruptedException ex) {
                return;
            } catch (BrokenBarrierException ex) {
                return;
            }
        }

    }
}