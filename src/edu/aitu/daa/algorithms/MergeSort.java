package edu.aitu.daa.algorithms;

import edu.aitu.daa.metrics.Metrics;
import edu.aitu.daa.util.ArrayUtils;

/**
 * MergeSort with reusable auxiliary buffer and small-input insertion-sort cutoff.
 * Complexity: Θ(n log n) time, Θ(n) extra space for the buffer.
 */
public final class MergeSort {
    public static final int DEFAULT_CUTOFF = 16;

    private MergeSort() {
    }

    public static void sort(int[] a) {
        sort(a, DEFAULT_CUTOFF, null);
    }

    public static void sort(int[] a, Metrics metrics) {
        sort(a, DEFAULT_CUTOFF, metrics);
    }

    public static void sort(int[] a, int cutoff, Metrics metrics) {
        if (a == null || a.length <= 1) {
            return;
        }
        int[] buffer = new int[a.length];
        if (metrics != null) {
            metrics.allocate(a.length);
        }
        sort(a, buffer, 0, a.length - 1, cutoff, metrics);
    }

    private static void sort(int[] a, int[] buffer, int left, int right, int cutoff, Metrics metrics) {
        if (metrics != null) {
            metrics.enter();
        }
        try {
            int size = right - left + 1;
            if (size <= 1) {
                return;
            }
            if (size <= cutoff) {
                ArrayUtils.insertionSort(a, left, right);
                if (metrics != null) {
                    // Rough operation accounting for insertion sort on this segment.
                    metrics.compare(size * (size - 1L) / 2);
                }
                return;
            }

            int mid = left + ((right - left) >>> 1);
            sort(a, buffer, left, mid, cutoff, metrics);
            sort(a, buffer, mid + 1, right, cutoff, metrics);
            // Skip merge when already ordered across the midpoint.
            if (metrics != null) {
                metrics.compare();
            }
            if (a[mid] <= a[mid + 1]) {
                return;
            }
            merge(a, buffer, left, mid, right, metrics);
        } finally {
            if (metrics != null) {
                metrics.leave();
            }
        }
    }

    private static void merge(int[] a, int[] buffer, int left, int mid, int right, Metrics metrics) {
        int n = right - left + 1;
        System.arraycopy(a, left, buffer, left, n);

        int i = left;
        int j = mid + 1;
        int k = left;
        while (i <= mid && j <= right) {
            if (metrics != null) {
                metrics.compare();
            }
            if (buffer[i] <= buffer[j]) {
                a[k++] = buffer[i++];
            } else {
                a[k++] = buffer[j++];
            }
        }
        while (i <= mid) {
            a[k++] = buffer[i++];
        }
        while (j <= right) {
            a[k++] = buffer[j++];
        }
    }
}
