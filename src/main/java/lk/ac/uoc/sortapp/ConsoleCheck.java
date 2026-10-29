package lk.ac.uoc.sortapp;

import lk.ac.uoc.sortapp.benchmark.BenchmarkResult;
import lk.ac.uoc.sortapp.benchmark.BenchmarkRunner;
import lk.ac.uoc.sortapp.data.ColumnValidator;
import lk.ac.uoc.sortapp.data.CsvLoader;
import lk.ac.uoc.sortapp.data.Dataset;
import lk.ac.uoc.sortapp.sorting.HeapSort;
import lk.ac.uoc.sortapp.sorting.InsertionSort;
import lk.ac.uoc.sortapp.sorting.MergeSort;
import lk.ac.uoc.sortapp.sorting.QuickSort;
import lk.ac.uoc.sortapp.sorting.ShellSort;
import lk.ac.uoc.sortapp.sorting.SortingAlgorithm;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;


public class ConsoleCheck {

    public static void main(String[] args) {
        String path = (args.length > 0) ? args[0] : "sample-data/titanic.csv";

        CsvLoader loader = new CsvLoader();
        ColumnValidator validator = new ColumnValidator();
        BenchmarkRunner runner = new BenchmarkRunner();
        List<SortingAlgorithm> algorithms = Arrays.asList(
                new InsertionSort(), new ShellSort(), new MergeSort(), new QuickSort(), new HeapSort());

        Dataset dataset;
        try {
            dataset = loader.load(new File(path));
        } catch (IOException e) {
            System.out.println("LOAD FAILED (friendly message): " + e.getMessage());
            return;
        }

        System.out.println("Loaded " + path + ": " + dataset.getRowCount() + " rows, "
                + dataset.getColumnCount() + " columns");

        for (int c = 0; c < dataset.getColumnCount(); c++) {
            String name = dataset.getHeaders().get(c);
            System.out.println();

            if (!validator.isNumeric(dataset, c)) {
                System.out.println("Column '" + name + "': NOT numeric (skipped)");
                try {
                    validator.toDoubleArray(dataset, c);
                } catch (IllegalArgumentException e) {
                    System.out.println("   message shown to user: " + e.getMessage());
                }
                continue;
            }

            double[] values = validator.toDoubleArray(dataset, c);
            System.out.println("Column '" + name + "': numeric, " + values.length + " values");

            List<BenchmarkResult> results = runner.run(values, algorithms);
            BenchmarkResult best = runner.findBest(results);

            for (BenchmarkResult r : results) {
                System.out.printf("   %-15s %10.3f ms%s%n", r.getAlgorithmName(), r.getMillis(),
                        (r == best) ? "   <-- BEST" : "");
            }
        }
    }
}