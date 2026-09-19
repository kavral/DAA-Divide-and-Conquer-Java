package edu.aitu.daa.util;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Shared array helpers used by sorting and selection algorithms.
 */
public final class ArrayUtils {
    private ArrayUtils() {
    }

    public static void swap(int[] a, int i, int j) {
        int tmp = a[i];
        a[i] = a[j];
        a[j] = tmp;
    }

    public static void insertionSort(int[] a, int left, int right) {
        for (int i = left + 1; i <= right; i++) {
            int key = a[i];
            int j = i - 1;
            while (j >= left && a[j] > key) {
                a[j + 1] = a[j];
                j--;
            }
            a[j + 1] = key;
        }
    }

    public static boolean isSorted(int[] a) {
        for (int i = 1; i < a.length; i++) {
            if (a[i - 1] > a[i]) {
                return false;
            }
        }
        return true;
    }

    public static int[] copyOf(int[] a) {
        int[] copy = new int[a.length];
        System.arraycopy(a, 0, copy, 0, a.length);
        return copy;
    }

    public static int[] randomArray(int n, int bound) {
        Random rng = ThreadLocalRandom.current();
        int[] a = new int[n];
        for (int i = 0; i < n; i++) {
            a[i] = rng.nextInt(bound);
        }
        return a;
    }

    public static int[] sortedArray(int n) {
        int[] a = new int[n];
        for (int i = 0; i < n; i++) {
            a[i] = i;
        }
        return a;
    }

    public static int[] reverseSortedArray(int n) {
        int[] a = new int[n];
        for (int i = 0; i < n; i++) {
            a[i] = n - i;
        }
        return a;
    }

    public static int[] nearlySortedArray(int n, int swaps) {
        int[] a = sortedArray(n);
        Random rng = ThreadLocalRandom.current();
        for (int i = 0; i < swaps; i++) {
            int x = rng.nextInt(n);
            int y = rng.nextInt(n);
            swap(a, x, y);
        }
        return a;
    }

    public static int[] duplicatesHeavyArray(int n, int distinct) {
        Random rng = ThreadLocalRandom.current();
        int[] a = new int[n];
        for (int i = 0; i < n; i++) {
            a[i] = rng.nextInt(Math.max(1, distinct));
        }
        return a;
    }
}
