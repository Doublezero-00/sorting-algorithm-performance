package lk.ac.uoc.sortapp.sorting;

import java.util.Arrays;
import java.util.Random;

public class SortingCheck {

    private static int failures = 0;

    public static void main(String[] args) {
        SortingAlgorithm[] algorithms = {
                new InsertionSort(), new ShellSort(), new MergeSort(), new QuickSort(), new HeapSort()
        };
        Random random = new Random(1);

        runCase(algorithms, "empty array", new double[]{});
        runCase(algorithms, "one element", new double[]{42});
        runCase(algorithms, "two elements (reversed)", new double[]{9, 3});
        runCase(algorithms, "already sorted", new double[]{1, 2, 3, 4, 5});
        runCase(algorithms, "reverse sorted", new double[]{5, 4, 3, 2, 1});
        runCase(algorithms, "all duplicates", new double[]{7, 7, 7, 7});
        runCase(algorithms, "negatives and decimals", new double[]{3.5, -2, 0, -7.25, 10, 3.5});

        int n = 20000;
        double[] randomData = new double[n];
        double[] sortedData = new double[n];
        double[] reverseData = new double[n];
        double[] sameData = new double[n];
        double[] fewDistinct = new double[n];
        for (int i = 0; i < n; i++) {
            randomData[i] = random.nextDouble() * 1000 - 500;
            sortedData[i] = i;
            reverseData[i] = n - i;
            sameData[i] = 5.0;
            fewDistinct[i] = random.nextInt(5);
        }
        runCase(algorithms, "20000 random numbers", randomData);
        runCase(algorithms, "20000 already sorted", sortedData);
        runCase(algorithms, "20000 reverse sorted", reverseData);
        runCase(algorithms, "20000 identical values", sameData);
        runCase(algorithms, "20000 values with only 5 distinct numbers", fewDistinct);

        System.out.println();
        if (failures == 0) {
            System.out.println("ALL TESTS PASSED");
        } else {
            System.out.println(failures + " TEST(S) FAILED");
        }
    }

    private static void runCase(SortingAlgorithm[] algorithms, String caseName, double[] input) {
        double[] expected = input.clone();
        Arrays.sort(expected);                   // the reference answer

        for (SortingAlgorithm algorithm : algorithms) {
            double[] actual = input.clone();
            boolean ok;
            try {
                algorithm.sort(actual);
                ok = Arrays.equals(expected, actual);
            } catch (Exception | StackOverflowError e) {
                ok = false;
                System.out.println("   " + algorithm.getName() + " crashed: " + e);
            }

            if (!ok) {
                failures++;
            }
            System.out.println((ok ? "PASS  " : "FAIL  ") + algorithm.getName() + "  -  " + caseName);
        }
    }
}