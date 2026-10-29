package lk.ac.uoc.sortapp.sorting;

public interface SortingAlgorithm {

    /** Human-readable name shown in the results table, e.g. "Merge Sort". */
    String getName();

    /** Sorts the array in place, ascending. Must handle null-safe empty/1-element arrays. */
    void sort(double[] data);
}
