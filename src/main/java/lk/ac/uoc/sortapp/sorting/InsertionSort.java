package lk.ac.uoc.sortapp.sorting;

public class InsertionSort implements SortingAlgorithm {

    @Override
    public String getName() {
        return "Insertion Sort";
    }

    @Override
    public void sort(double[] data) {
        if (data == null || data.length < 2) {
            return;                              // nothing to sort
        }

        for (int i = 1; i < data.length; i++) {
            double key = data[i];                // the "card" we are inserting
            int j = i - 1;

            // Shift bigger values one step to the right
            while (j >= 0 && data[j] > key) {
                data[j + 1] = data[j];
                j--;
            }
            data[j + 1] = key;                   // drop the key into the gap
        }
    }
}