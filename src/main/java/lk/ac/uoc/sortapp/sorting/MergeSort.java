package lk.ac.uoc.sortapp.sorting;

public class MergeSort implements SortingAlgorithm {

    @Override
    public String getName() {
        return "Merge Sort";
    }

    @Override
    public void sort(double[] data) {
        if (data == null || data.length < 2) {
            return;
        }
        double[] aux = new double[data.length];   // helper array, created once
        mergeSort(data, aux, 0, data.length - 1);
    }

    /** Sorts data[low..high] (both ends included). */
    private void mergeSort(double[] data, double[] aux, int low, int high) {
        if (low >= high) {
            return;                               // 0 or 1 element: already sorted
        }
        int mid = low + (high - low) / 2;

        mergeSort(data, aux, low, mid);           // sort left half
        mergeSort(data, aux, mid + 1, high);      // sort right half
        merge(data, aux, low, mid, high);         // combine them
    }

    /** Merges the sorted ranges data[low..mid] and data[mid+1..high]. */
    private void merge(double[] data, double[] aux, int low, int mid, int high) {
        // Copy the range into the helper array
        for (int k = low; k <= high; k++) {
            aux[k] = data[k];
        }

        int left = low;          // pointer in the left half
        int right = mid + 1;     // pointer in the right half

        for (int k = low; k <= high; k++) {
            if (left > mid) {                     // left half used up
                data[k] = aux[right++];
            } else if (right > high) {            // right half used up
                data[k] = aux[left++];
            } else if (aux[right] < aux[left]) {  // right value is smaller
                data[k] = aux[right++];
            } else {                              // left value is smaller or equal
                data[k] = aux[left++];
            }
        }
    }
}