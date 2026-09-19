package edu.aitu.daa.algorithms;

import edu.aitu.daa.metrics.Metrics;
import edu.aitu.daa.util.ArrayUtils;

/**
 * Deterministic Select (Median-of-Medians) with groups of 5.
 * Guarantees worst-case Θ(n) time by choosing a sufficiently good pivot.
 */
public final class DeterministicSelect {
    private DeterministicSelect() {
    }

    public static int select(int[] a, int k) {
        return select(a, k, null);
    }

    /**
     * Returns the element that would be at index {@code k} in sorted order (0-based).
     */
    public static int select(int[] a, int k, Metrics metrics) {
        if (a == null) {
            throw new IllegalArgumentException("array must not be null");
        }
        if (a.length == 0) {
            throw new IllegalArgumentException("array must be non-empty");
        }
        if (k < 0 || k >= a.length) {
            throw new IllegalArgumentException("k out of range: " + k);
        }
        return select(a, 0, a.length - 1, k, metrics);
    }

    private static int select(int[] a, int left, int right, int k, Metrics metrics) {
        while (true) {
            if (metrics != null) {
                metrics.enter();
            }
            try {
                if (left == right) {
                    return a[left];
                }

                int pivotIndex = medianOfMedians(a, left, right, metrics);
                pivotIndex = partition(a, left, right, pivotIndex, metrics);

                if (k == pivotIndex) {
                    return a[k];
                } else if (k < pivotIndex) {
                    right = pivotIndex - 1;
                } else {
                    left = pivotIndex + 1;
                }
            } finally {
                if (metrics != null) {
                    metrics.leave();
                }
            }
        }
    }

    private static int medianOfMedians(int[] a, int left, int right, Metrics metrics) {
        int n = right - left + 1;
        if (n <= 5) {
            ArrayUtils.insertionSort(a, left, right);
            return left + n / 2;
        }

        int numGroups = (n + 4) / 5;
        for (int i = 0; i < numGroups; i++) {
            int groupLeft = left + i * 5;
            int groupRight = Math.min(groupLeft + 4, right);
            ArrayUtils.insertionSort(a, groupLeft, groupRight);
            int medianIndex = groupLeft + (groupRight - groupLeft) / 2;
            ArrayUtils.swap(a, left + i, medianIndex);
            if (metrics != null) {
                metrics.swap();
            }
        }

        // Recursively find the median of the group medians.
        int mid = left + (numGroups - 1) / 2;
        select(a, left, left + numGroups - 1, mid, metrics);
        return mid;
    }

    private static int partition(int[] a, int left, int right, int pivotIndex, Metrics metrics) {
        int pivot = a[pivotIndex];
        ArrayUtils.swap(a, pivotIndex, right);
        if (metrics != null) {
            metrics.swap();
        }
        int store = left;
        for (int i = left; i < right; i++) {
            if (metrics != null) {
                metrics.compare();
            }
            if (a[i] < pivot) {
                ArrayUtils.swap(a, store, i);
                if (metrics != null) {
                    metrics.swap();
                }
                store++;
            }
        }
        ArrayUtils.swap(a, store, right);
        if (metrics != null) {
            metrics.swap();
        }
        return store;
    }
}
