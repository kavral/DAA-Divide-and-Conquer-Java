package edu.aitu.daa.algorithms;

import edu.aitu.daa.metrics.Metrics;
import edu.aitu.daa.util.ArrayUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QuickSortTest {

    @Test
    void emptyAndSingleton() {
        int[] empty = {};
        QuickSort.sort(empty);
        assertArrayEquals(new int[]{}, empty);

        int[] one = {7};
        QuickSort.sort(one);
        assertArrayEquals(new int[]{7}, one);
    }

    @ParameterizedTest
    @ValueSource(ints = {10, 100, 1_000, 10_000})
    void matchesArraysSort(int n) {
        int[] a = ArrayUtils.randomArray(n, n * 4);
        int[] expected = ArrayUtils.copyOf(a);
        Arrays.sort(expected);
        Metrics m = new Metrics();
        QuickSort.sort(a, QuickSort.DEFAULT_CUTOFF, new Random(123), m);
        assertArrayEquals(expected, a);
    }

    @Test
    void sortsWithMetricsAndRandom() {
        int[] a = ArrayUtils.randomArray(5_000, 50_000);
        int[] expected = ArrayUtils.copyOf(a);
        Arrays.sort(expected);
        Metrics m = new Metrics();
        QuickSort.sort(a, QuickSort.DEFAULT_CUTOFF, new Random(99), m);
        assertArrayEquals(expected, a);
        // Smaller-first + random pivot → depth should be well below n.
        assertTrue(m.getMaxRecursionDepth() < 200, "depth=" + m.getMaxRecursionDepth());
    }

    @Test
    void adversarialSortedStillCorrect() {
        int[] a = ArrayUtils.sortedArray(2_000);
        QuickSort.sort(a, QuickSort.DEFAULT_CUTOFF, new Random(1), null);
        assertTrue(ArrayUtils.isSorted(a));
    }

    @Test
    void duplicates() {
        int[] a = ArrayUtils.duplicatesHeavyArray(3_000, 3);
        QuickSort.sort(a);
        assertTrue(ArrayUtils.isSorted(a));
    }
}
