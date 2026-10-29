package lk.ac.uoc.sortapp.benchmark;

import lk.ac.uoc.sortapp.sorting.SortingAlgorithm;

import java.util.ArrayList;
import java.util.List;

/**
 * MEMBER 3 - Runs every sorting algorithm on the same data, measures the
 * execution time of each one and finds the fastest.
 *
 * Rules for a FAIR comparison:
 *  - Every algorithm gets its OWN COPY of the original data. If we sorted the
 *    same array again and again, later algorithms would receive already-sorted
 *    data and the times would be meaningless.
 *  - We time ONLY the sort call (not copying, not printing).
 *  - Java compiles code "just in time", so the very first run of a method is
 *    slower. A short warm-up run on a small piece of data is done first.
 */
public class BenchmarkRunner {

    private static final int WARM_UP_SIZE = 1000;

    /** Runs all algorithms and returns one result per algorithm (same order as the list). */
    public List<BenchmarkResult> run(double[] data, List<SortingAlgorithm> algorithms) {
        List<BenchmarkResult> results = new ArrayList<>();

        for (SortingAlgorithm algorithm : algorithms) {

            warmUp(algorithm, data);

            double[] copy = data.clone();               // fresh copy for this algorithm

            long start = System.nanoTime();
            algorithm.sort(copy);
            long end = System.nanoTime();

            results.add(new BenchmarkResult(algorithm.getName(), end - start, copy));
        }
        return results;
    }

    /** @return the result with the shortest execution time (null if the list is empty). */
    public BenchmarkResult findBest(List<BenchmarkResult> results) {
        BenchmarkResult best = null;

        for (BenchmarkResult result : results) {
            if (best == null || result.getNanos() < best.getNanos()) {
                best = result;
            }
        }
        return best;
    }

    /** Runs the algorithm once on a small copy so the timed run is not penalised by start-up cost. */
    private void warmUp(SortingAlgorithm algorithm, double[] data) {
        int size = Math.min(data.length, WARM_UP_SIZE);
        double[] small = new double[size];
        System.arraycopy(data, 0, small, 0, size);
        algorithm.sort(small);
    }
}
