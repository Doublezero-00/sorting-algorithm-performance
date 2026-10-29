package lk.ac.uoc.sortapp.sorting;

public class HeapSort implements SortingAlgorithm {

    @Override
    public String getName() {
        return "Heap Sort";
    }

    @Override
    public void sort(double[] data) {
        if (data == null || data.length < 2) {
            return;
        }

        int n = data.length;

        // Phase 1: build the max heap (start from the last parent and go backwards)
        for (int i = n / 2 - 1; i >= 0; i--) {
            heapify(data, n, i);
        }

        // Phase 2: repeatedly move the largest value to the end
        for (int end = n - 1; end > 0; end--) {
            swap(data, 0, end);                   // biggest goes to its final place
            heapify(data, end, 0);                // repair the smaller heap
        }
    }

    /**
     * Pushes the value at 'root' down until the heap rule holds again.
     * 'size' is how many elements at the front of the array belong to the heap.
     */
    private void heapify(double[] data, int size, int root) {
        while (true) {
            int largest = root;
            int left = 2 * root + 1;
            int right = 2 * root + 2;

            if (left < size && data[left] > data[largest]) {
                largest = left;
            }
            if (right < size && data[right] > data[largest]) {
                largest = right;
            }

            if (largest == root) {
                return;                           // heap rule already holds
            }
            swap(data, root, largest);
            root = largest;                       // continue pushing down
        }
    }

    private void swap(double[] data, int a, int b) {
        double temp = data[a];
        data[a] = data[b];
        data[b] = temp;
    }
}