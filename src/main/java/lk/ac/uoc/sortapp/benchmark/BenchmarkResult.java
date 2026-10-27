package lk.ac.uoc.sortapp.benchmark;

/**
 * MEMBER 3 - Stores the result of running ONE sorting algorithm.
 * (Shared between BenchmarkRunner and the GUI.)
 */
public class BenchmarkResult {

    private final String algorithmName;
    private final long nanos;          // execution time in nanoseconds
    private final double[] sorted;     // the sorted data

    public BenchmarkResult(String algorithmName, long nanos, double[] sorted) {
        this.algorithmName = algorithmName;
        this.nanos = nanos;
        this.sorted = sorted;
    }

    public String getAlgorithmName() {
        return algorithmName;
    }

    public long getNanos() {
        return nanos;
    }

    /** Execution time in milliseconds (1 ms = 1,000,000 ns). */
    public double getMillis() {
        return nanos / 1_000_000.0;
    }

    public double[] getSorted() {
        return sorted;
    }
}
