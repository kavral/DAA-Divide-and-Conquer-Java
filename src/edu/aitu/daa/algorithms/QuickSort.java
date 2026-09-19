package edu.aitu.daa.algorithms;

import edu.aitu.daa.metrics.Metrics;
import edu.aitu.daa.util.ArrayUtils;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Randomized QuickSort with in-place partitioning.
 * Recurses on the smaller partition and iterates over the larger one
 * to keep typical stack depth O(log n).
 */
public final class QuickSort {
    public static final int DEFAULT_CUTOFF = 16;

    private QuickSort() {
    }

    public static void sort(int[] a) {
        sort(a, DEFAULT_CUTOFF, ThreadLocalRandom.current(), null);
    }

    public static void sort(int[] a, Metrics metrics) {
        sort(a, DEFAULT_CUTOFF, ThreadLocalRandom.current(), metrics);
    }

    public static void sort(int[] a, int cutoff, Random rng, Metrics metrics) {
        if (a == null || a.length <= 1) {
            return;
        }
        sortRange(a, 0, a.length - 1, cutoff, rng, metrics);
    }

    private static void sortRange(int[] a, int left, int right, int cutoff, Random rng, Metrics metrics) {
        while (left < right) {
            int size = right - left + 1;
            if (size <= cutoff) {
                ArrayUtils.insertionSort(a, left, right);
                if (metrics != null) {
                    metrics.compare(size * (size - 1L) / 2);
                }
                return;
            }

            if (metrics != null) {
                metrics.enter();
            }
            try {
                int pivotIndex = left + rng.nextInt(size);
                ArrayUtils.swap(a, pivotIndex, right);
                if (metrics != null) {
                    metrics.swap();
                }
                int p = partition(a, left, right, metrics);

                int leftSize = p - left;
                int rightSize = right - p;
                // Recurse into the smaller side; loop over the larger side.
                if (leftSize < rightSize) {
                    if (left < p - 1) {
                        sortRange(a, left, p - 1, cutoff, rng, metrics);
                    }
                    left = p + 1;
                } else {
                    if (p + 1 < right) {
                        sortRange(a, p + 1, right, cutoff, rng, metrics);
                    }
                    right = p - 1;
                }
            } finally {
                if (metrics != null) {
                    metrics.leave();
                }
            }
        }
    }

    private static int partition(int[] a, int left, int right, Metrics metrics) {
        int pivot = a[right];
        int i = left;
        for (int j = left; j < right; j++) {
            if (metrics != null) {
                metrics.compare();
            }
            if (a[j] <= pivot) {
                ArrayUtils.swap(a, i, j);
                if (metrics != null) {
                    metrics.swap();
                }
                i++;
            }
        }
        ArrayUtils.swap(a, i, right);
        if (metrics != null) {
            metrics.swap();
        }
        return i;
    }
}
