package sortvisualizer;

public enum SortingType {
    
    BOGO_SORT,
    BUBBLE_SORT,
    COCKTAIL_SHAKER_SORT,
    GNOME_SORT,
    HEAP_SORT,
    INSERTION_SORT,
    MERGE_SORT,
    PANCAKE_SORT,
    PIGEONHOLE_SORT,
    QUICK_SORT,
    RADIX_SORT,
    SELECTION_SORT,
    SHELL_SORT;

    @Override
    public String toString() {
        switch (this) {
            case BOGO_SORT:
                return "Bogo Sort";
            case BUBBLE_SORT:
                return "Bubble Sort";
            case COCKTAIL_SHAKER_SORT:
                return "Cocktail Shaker Sort";
            case GNOME_SORT:
                return "Gnome Sort";
            case HEAP_SORT:
                return "Heap Sort";
            case INSERTION_SORT:
                return "Insertion Sort";
            case MERGE_SORT:
                return "Merge Sort";
            case PANCAKE_SORT:
                return "Pancake Sort";
            case PIGEONHOLE_SORT:
                return "Pigeonhole Sort";
            case QUICK_SORT:
                return "Quick Sort";
            case RADIX_SORT:
                return "Radix Sort";
            case SELECTION_SORT:
                return "Selection Sort";
            case SHELL_SORT:
                return "Shell Sort";
        }
        throw new NullPointerException();
    }
}
