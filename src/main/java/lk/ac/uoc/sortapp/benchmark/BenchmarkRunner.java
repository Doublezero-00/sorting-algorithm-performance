package lk.ac.uoc.sortapp.benchmark;

import lk.ac.uoc.sortapp.sorting.SortingAlgorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BenchmarkRunner {

    public List<BenchmarkResult> run(double[] values, List<SortingAlgorithm> algorithms) {
        Objects.requireNonNull(values, "values must not be null");
        Objects.requireNonNull(algorithms, "algorithms must not be null");

        List<BenchmarkResult> results = new ArrayList<>(algorithms.size());
        for (SortingAlgorithm algorithm : algorithms) {
            Objects.requireNonNull(algorithm, "algorithm must not be null");
            double[] sorted = values.clone();
            long start = System.nanoTime();
            algorithm.sort(sorted);
            long elapsed = System.nanoTime() - start;
            results.add(new BenchmarkResult(algorithm.getName(), elapsed, sorted));
        }
        return results;
    }

    public BenchmarkResult findBest(List<BenchmarkResult> results) {
        Objects.requireNonNull(results, "results must not be null");
        if (results.isEmpty()) {
            throw new IllegalArgumentException("At least one benchmark result is required.");
        }

        BenchmarkResult best = Objects.requireNonNull(results.get(0), "result must not be null");
        for (int i = 1; i < results.size(); i++) {
            BenchmarkResult result = Objects.requireNonNull(results.get(i), "result must not be null");
            if (result.getNanos() < best.getNanos()) {
                best = result;
            }
        }
        return best;
    }
}
