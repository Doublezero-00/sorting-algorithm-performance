package lk.ac.uoc.sortapp.sorting;

public class ShellSort implements SortingAlgorithm {

    @Override
    public String getName() {
        return "Shell Sort";
    }

    @Override
    public void sort(double[] data) {
        if (data == null || data.length < 2) {
            return;
        }

        int n = data.length;

        // Find the biggest gap in the sequence that is still smaller than n/3
        int gap = 1;
        while (gap < n / 3) {
            gap = 3 * gap + 1;
        }

        // Do a gapped insertion sort, then shrink the gap (13 -> 4 -> 1 -> 0)
        while (gap >= 1) {
            for (int i = gap; i < n; i++) {
                double temp = data[i];
                int j = i;

                while (j >= gap && data[j - gap] > temp) {
                    data[j] = data[j - gap];
                    j -= gap;
                }
                data[j] = temp;
            }
            gap = gap / 3;
        }
    }
}