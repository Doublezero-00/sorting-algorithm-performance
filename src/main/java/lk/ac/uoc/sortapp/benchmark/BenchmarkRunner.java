package lk.ac.uoc.sortapp.benchmark;

import lk.ac.uoc.sortapp.sorting.SortingAlgorithm;

import java.util.ArrayList;
import java.util.List;

public class BenchmarkRunner {

    private static final int WARM_UP_SIZE = 1000;

    /** Runs all algorithms and returns one result per algorithm (same order as the list). */
    public List<BenchmarkResult> run(double[] data, List<SortingAlgorithm> algorithms) {
        List<BenchmarkResult> results = new ArrayList<>();

        if (data == null || algorithms == null) {
            return results;
        }

        for (SortingAlgorithm algorithm : algorithms) {
            if (algorithm == null) {
                continue;
            }

            warmUp(algorithm, data);

            double[] copy = data.clone();
            long start = System.nanoTime();
            algorithm.sort(copy);
            long end = System.nanoTime();

            results.add(new BenchmarkResult(algorithm.getName(), end - start, copy));
        }
        return results;
    }

    /** @return the result with the shortest execution time (null if the list is empty). */
    public BenchmarkResult findBest(List<BenchmarkResult> results) {
        if (results == null || results.isEmpty()) {
            return null;
        }

        BenchmarkResult best = null;
        for (BenchmarkResult result : results) {
            if (result == null) {
                continue;
            }
            if (best == null || result.getNanos() < best.getNanos()) {
                best = result;
            }
        }
        return best;
    }

    /** Runs the algorithm once on a small copy so the timed run is not penalised by start-up cost. */
    private void warmUp(SortingAlgorithm algorithm, double[] data) {
        if (data == null || data.length == 0) {
            return;
        }

        int size = Math.min(data.length, WARM_UP_SIZE);
        double[] small = new double[size];
        System.arraycopy(data, 0, small, 0, size);
        algorithm.sort(small);
    }
}
