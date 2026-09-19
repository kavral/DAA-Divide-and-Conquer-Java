package edu.aitu.daa.algorithms;

import edu.aitu.daa.metrics.Metrics;
import edu.aitu.daa.util.ArrayUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MergeSortTest {

    @Test
    void emptyAndSingleton() {
        int[] empty = {};
        MergeSort.sort(empty);
        assertArrayEquals(new int[]{}, empty);

        int[] one = {42};
        MergeSort.sort(one);
        assertArrayEquals(new int[]{42}, one);
    }

    @Test
    void rejectsNull() {
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> MergeSort.sort(null));
    }

    @Test
    void sortsSmallArray() {
        int[] a = {5, 1, 4, 2, 8, 0, 3};
        MergeSort.sort(a);
        assertArrayEquals(new int[]{0, 1, 2, 3, 4, 5, 8}, a);
    }

    @ParameterizedTest
    @ValueSource(ints = {10, 100, 1_000, 10_000})
    void matchesArraysSort(int n) {
        int[] a = ArrayUtils.randomArray(n, n * 4);
        int[] expected = ArrayUtils.copyOf(a);
        Arrays.sort(expected);
        Metrics m = new Metrics();
        MergeSort.sort(a, m);
        assertArrayEquals(expected, a);
        assertTrue(m.getMaxRecursionDepth() > 0);
        assertTrue(m.getMaxRecursionDepth() <= n);
    }

    @Test
    void handlesAlreadySortedAndReverse() {
        int[] sorted = ArrayUtils.sortedArray(500);
        MergeSort.sort(sorted);
        assertTrue(ArrayUtils.isSorted(sorted));

        int[] reverse = ArrayUtils.reverseSortedArray(500);
        MergeSort.sort(reverse);
        assertTrue(ArrayUtils.isSorted(reverse));
    }

    @Test
    void handlesDuplicates() {
        int[] a = ArrayUtils.duplicatesHeavyArray(1_000, 5);
        MergeSort.sort(a);
        assertTrue(ArrayUtils.isSorted(a));
    }
}
