package lk.ac.uoc.sortapp.sorting;

public class QuickSort implements SortingAlgorithm {

    @Override
    public String getName() {
        return "Quick Sort";
    }

    @Override
    public void sort(double[] data) {
        if (data == null || data.length < 2) {
            return;
        }
        quickSort(data, 0, data.length - 1);
    }

    private void quickSort(double[] data, int low, int high) {
        while (low < high) {
            int split = partition(data, low, high);

            // [low..split] holds the smaller values, [split+1..high] the larger ones.
            if (split - low < high - split) {
                quickSort(data, low, split);      // recurse into the smaller side
                low = split + 1;                  // loop on the larger side
            } else {
                quickSort(data, split + 1, high);
                high = split;
            }
        }
    }

    /** Hoare partition. Returns the index that splits the range into two parts. */
    private int partition(double[] data, int low, int high) {
        int mid = low + (high - low) / 2;

        // Median-of-three: after these swaps data[low] <= data[mid] <= data[high]
        if (data[mid] < data[low]) {
            swap(data, mid, low);
        }
        if (data[high] < data[low]) {
            swap(data, high, low);
        }
        if (data[high] < data[mid]) {
            swap(data, high, mid);
        }

        double pivot = data[mid];                 // the median value
        int i = low - 1;
        int j = high + 1;

        while (true) {
            do {
                i++;
            } while (data[i] < pivot);            // find a value on the left that is too big

            do {
                j--;
            } while (data[j] > pivot);            // find a value on the right that is too small

            if (i >= j) {
                return j;                         // pointers crossed: partition finished
            }
            swap(data, i, j);
        }
    }

    private void swap(double[] data, int a, int b) {
        double temp = data[a];
        data[a] = data[b];
        data[b] = temp;
    }
}