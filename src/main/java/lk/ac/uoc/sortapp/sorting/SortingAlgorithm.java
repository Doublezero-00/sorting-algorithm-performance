package lk.ac.uoc.sortapp.sorting;

/**
 * Common contract for every sorting algorithm (Strategy pattern).
 * Owner of implementations: Member 2. Used by: Member 3 (BenchmarkRunner).
 *
 * Implementations MUST sort the given array in place, in ascending order.
 */
public interface SortingAlgorithm {

    /** Human-readable name shown in the results table, e.g. "Merge Sort". */
    String getName();

    /** Sorts the array in place, ascending. Must handle null-safe empty/1-element arrays. */
    void sort(double[] data);
}
