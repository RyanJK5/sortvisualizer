package sortvisualizer;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

public class Sorter implements Runnable {
        
    public SortingType sortingType;
    public int[] array;
    public int highlightLow = -1;
    public int highlightHigh = -1;

    public long comparisons;
    public long arrayAccesses;

    private boolean isRunning = true;
    private Runnable sleep;
    private Runnable onCompletion;

    public Sorter(int[] array, SortingType sort, Runnable sleepMethod, Runnable completeMethod, Consumer<Integer> playSoundMethod) {
        this.array = array;
        this.sortingType = sort;
        sleep = sleepMethod;
        onCompletion = completeMethod;
    }

    @Override
    public void run() {
        switch (sortingType) {
            case BOGO_SORT:
                bogoSort(array);
                break;
            case BUBBLE_SORT:
                bubbleSort(array);
                break;
            case COCKTAIL_SHAKER_SORT:
                cocktailSort(array);
                break;
            case GNOME_SORT:
                gnomeSort(array);
                break;
            case HEAP_SORT:
                heapSort(array);
                break;
            case INSERTION_SORT:
                insertionSort(array);
                break;
            case MERGE_SORT:
                mergeSort(array, 0, array.length - 1);
                break;
            case PANCAKE_SORT:
                pancakeSort(array, array.length);
                break;
            case PIGEONHOLE_SORT:
                pigeonholeSort(array);
                break;
            case QUICK_SORT:
                quickSort(array);
                break;
            case RADIX_SORT:
                radixSort(array, array.length);
                break;
            case SELECTION_SORT:
                selectionSort(array);
                break;
            case SHELL_SORT:
                shellSort(array);
                break;
        }
        onCompletion.run();
        kill();
    }
    
    private void incrComparisons() {
        if (comparisons == Long.MAX_VALUE) {
            return;
        }
        comparisons++;
    }

    public void kill() {
        isRunning = false;
        highlightLow = -1;
        highlightHigh = -1;
    }

    public void pigeonholeSort(int[] arr) {
        int min = arr[0];
        int max = arr[0];
        int range, i, j, index;
  
        for (int a = 0; a < arr.length; a++) {
            if (!isRunning) {
                return;
            }
            if (arr[a] > max) {
                max = arr[a];
                arrayAccesses++;
            }
            if (arr[a] < min) {
                min = arr[a];
                arrayAccesses++;
            }
            arrayAccesses += 2;
            highlightLow = a - 1;
            highlightHigh = a + 1;
            sleep.run();
        }
  
        range = max - min + 1;
        int[] phole = new int[range];
        Arrays.fill(phole, 0);
  
        for (i = 0; i < arr.length; i++) {
            if (!isRunning) {
                return;
            }
            phole[arr[i] - min]++;
            arrayAccesses++;
            highlightLow = i - 1;
            highlightHigh = i + 1;
            sleep.run();
        }
        index = 0;
  
        for (j = 0; j < range; j++) {
            while (phole[j]-- > 0) {
                if (!isRunning) {
                    return;
                }
                arr[index++] = j + min;
                arrayAccesses++;
                highlightLow = j;
                highlightHigh = j;
                sleep.run();
            }
        }
    }

    public void pancakeSort(int[] arr, int n) {
        for (int curr_size = n; curr_size > 1; --curr_size) {
            if (!isRunning) {
                return;
            }
            int mi = findMax(arr, curr_size);
            if (mi != curr_size-1) {
                flip(arr, mi);
                flip(arr, curr_size-1);
            }
        }
    }

    public void gnomeSort(int[] arr) {
        int index = 0;
  
        while (index < arr.length) {
            if (!isRunning) {
                return;
            }
            if (index == 0) {
                index++;
            }
            comparisons++;
            if (arr[index] >= arr[index - 1]) {
                index++;
            }
            else {
                int temp = 0;
                temp = arr[index];
                arr[index] = arr[index - 1];
                arr[index - 1] = temp;
                index--;
                arrayAccesses += 2;
            }
            highlightLow = index - 1;
            highlightHigh = index + 1;
            sleep.run();
        }
    }

    public void cocktailSort(int[] arr) {
        boolean swapped = true;
        int start = 0;
        int end = arr.length;
 
        while (swapped == true) {
            swapped = false;
            for (int i = start; i < end - 1; ++i) {
                if (!isRunning) {
                    return;
                }
                comparisons++;
                if (arr[i] > arr[i + 1]) {
                    cocktailSwap(arr, i, i + 1);
                    swapped = true;
                }
            }
 
            if (swapped == false) {
                break;
            }
            swapped = false;
            end--;
 
            for (int i = end; i > start; i--) {
                if (!isRunning) {
                    return;
                }
                comparisons++;
                if (arr[i - 1] > arr[i]) {
                    cocktailSwap(arr, i, i - 1);
                    swapped = true;
                }
            }
            start++;
        }
    }

    public void bogoSort(int[] arr) {
        while (!isSorted(arr)) {
            comparisons++;
            Random rand = ThreadLocalRandom.current();
            for (int i = arr.length - 1; i > 0; i--) {
                if (!isRunning) {
                    return;
                }
                int index = rand.nextInt(i + 1);
                swap(arr, i, index);
            }
        }
    }

    public void bubbleSort(int[] arr) {
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (!isRunning) {
                    return;
                }
                if (arr[j] > arr[j + 1]) {
                    swap(arr, j, j + 1);
                }
                incrComparisons();
            }
        }
    }

    public void shellSort(int[] arr) {
        int n = arr.length;
  
        for (int gap = n / 2; gap > 0; gap /= 2) {
            for (int i = gap; i < n; i += 1) {
                if (!isRunning) {
                    return;
                }

                int temp = arr[i];
                int j;
                incrComparisons();
                highlightLow = i - 1;
                highlightHigh = i + 1;
                for (j = i; j >= gap && arr[j - gap] > temp; j -= gap) {
                    arr[j] = arr[j - gap];
                    arrayAccesses++;
                    sleep.run();
                }
                arr[j] = temp;
            }
        }
    }

    public void heapSort(int[] arr) {
        int n = arr.length;
  
        for (int i = n / 2 - 1; i >= 0; i--) {
            if (!isRunning) {
                return;
            }
            heapify(arr, n, i);
        }
        for (int i = n - 1; i >= 0; i--) {
            if (!isRunning) {
                return;
            }
            swap(arr, 0, i);
            heapify(arr, i, 0);
        }
    }

    public void insertionSort(int[] arr) {
        int n = arr.length;
        for (int i = 1; i < n; ++i) {
            if (!isRunning) {
                return;
            }
            int key = arr[i];
            arrayAccesses++;
            int j = i - 1;
            
            while (j >= 0 && arr[j] > key) {
                incrComparisons();
                arr[j + 1] = arr[j];
                arrayAccesses++;
                j--;
                highlightLow = j - 1;
                highlightHigh = j + 1;
                sleep.run();
            }
            arr[j + 1] = key;
        }
    }

    public void mergeSort(int arr[], int l, int r) {
        if (l < r) {
            int m = l + (r - l) / 2;

            mergeSort(arr, l, m);
            mergeSort(arr, m + 1, r);
 
            merge(arr, l, m, r);
            if (!isRunning) {
                return;
            }
        }
    }

    public void quickSort(int[] arr) {
        quickSort(arr, 0, arr.length - 1);
    }

    public void radixSort(int arr[], int n) {
        int m = getMax(arr, n);
  
        for (int exp = 1; m / exp > 0; exp *= 10) {
            countSort(arr, n, exp);
        }
    }

    public void selectionSort(int arr[]) {
        for (int i = 0; i < arr.length - 1; i++) {
            int minIndex = i;
            for (int j = i+1; j < arr.length; j++) {
                if (!isRunning) {
                    return;
                }
                arrayAccesses += 2;
                if (arr[j] < arr[minIndex]) {
                    minIndex = j;
                }
                incrComparisons();
                highlightLow = j - 1;
                highlightHigh = j + 1;
                sleep.run();
            }
            int temp = arr[i];
            arr[i] = arr[minIndex];
            arr[minIndex] = temp;
            arrayAccesses += 2;
            sleep.run();
        }
    }

    // part of bogosort
    private boolean isSorted(int[] arr) {
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] < arr[i - 1]) {
                return false;
            }
        }
        return true;
    }

    // part of heap sort
    private void heapify(int arr[], int n, int i) {
        int largest = i;
        int l = 2 * i + 1;
        int r = 2 * i + 2;
  
        incrComparisons();
        incrComparisons();
        if (l < n && arr[l] > arr[largest]) {
            largest = l;
        }
        if (r < n && arr[r] > arr[largest]) {
            largest = r;
        }
        if (largest != i) {
            swap(arr, i, largest);
            heapify(arr, n, largest);
        }
    }

    // part of radix sort
    private void countSort(int arr[], int n, int exp) {
        int output[] = new int[n];
        int i;
        int count[] = new int[10];
        Arrays.fill(count, 0);
  
        for (i = 0; i < n; i++) {
            if (!isRunning) {
                return;
            }
            count[(arr[i] / exp) % 10]++;
            arrayAccesses++;
            highlightLow = i - 1;
            highlightHigh = i + 1;
            sleep.run();
        }
        for (i = 1; i < 10; i++) {
            if (!isRunning) {
                return;
            }
            count[i] += count[i - 1];
            arrayAccesses++;
            highlightLow = i - 1;
            highlightHigh = i + 1;
            sleep.run();
        }
        for (i = n - 1; i >= 0; i--) {
            if (!isRunning) {
                return;
            }
            output[count[(arr[i] / exp) % 10] - 1] = arr[i];
            count[(arr[i] / exp) % 10]--;
            arrayAccesses += 2;
            highlightLow = i - 1;
            highlightHigh = i + 1;
            sleep.run();
        }
        for (i = 0; i < n; i++) {
            if (!isRunning) {
                return;
            }
            arr[i] = output[i];
            arrayAccesses++;
            highlightLow = i - 1;
            highlightHigh = i + 1;
            sleep.run();
        }
    }
  
    // part of radix sort
    private int getMax(int arr[], int n) {
        int mx = arr[0];
        for (int i = 1; i < n; i++)
            if (arr[i] > mx)
                mx = arr[i];
        return mx;
    }

    // part of mergeSort
    private void merge(int arr[], int start, int mid, int end) {
        int start2 = mid + 1;
 
        if (arr[mid] <= arr[start2]) {
            return;
        }
        arrayAccesses += 2;
 
        while (start <= mid && start2 <= end) {
            if (arr[start] <= arr[start2]) {
                start++;
            }
            else {
                int value = arr[start2];
                arrayAccesses++;
                int index = start2;
 
                highlightHigh = index;
                highlightLow = start;
                while (index != start) {
                    if (!isRunning) {
                        return;
                    }
                    arr[index] = arr[index - 1];
                    arrayAccesses++;
                    index--;
                }
                sleep.run();
                incrComparisons();

                arr[start] = value;
 
                start++;
                mid++;
                start2++;
            }
        }
    }

    // part of quickSort
    private void quickSort(int[] arr, int low, int high) {
        if (low < high) {
            int pi = partition(arr, low, high);
            if (!isRunning) {
                return;
            }
            quickSort(arr, low, pi-1);
            quickSort(arr, pi+1, high);
        }
    }

    // part of quickSort
    private int partition(int[] arr, int low, int high) {
        int pivot = arr[high];
        arrayAccesses++;
        int i = (low - 1);
        for (int j = low; j < high; j++) {
            if (!isRunning) {
                return -1;
            }
            incrComparisons();
            arrayAccesses++;
            if (arr[j] <= pivot) {
                i++;
                swap(arr, i, j);
            }
        }
        swap(arr, i + 1, high);
        return i+1;
    }

    // part of cocktailSort
    private void cocktailSwap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
        arrayAccesses += 2;
        highlightLow = i;
        highlightHigh = i + 1;
        sleep.run();
    }
 
    // part of pancakeSort
    private void flip(int arr[], int i) {
        int start = 0;
        while (start < i) {
            comparisons++;
            if (!isRunning) {
                return;
            }
            swap(arr, i, start);
            start++;
            i--;
        }
    }

    // part of pancake sort
    private int findMax(int arr[], int n) {
        int mi, i;
        for (mi = 0, i = 0; i < n; ++i)
            if (arr[i] > arr[mi])
                mi = i;
        return mi;
    }

    private void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
        arrayAccesses += 2;
        highlightLow = Math.min(i, j);
        highlightHigh = Math.max(i, j);
        sleep.run();
    }
}
