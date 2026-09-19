package edu.aitu.daa.algorithms;

import edu.aitu.daa.metrics.Metrics;
import edu.aitu.daa.util.ArrayUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DeterministicSelectTest {

    @Test
    void selectsEachOrderStatistic() {
        int[] a = {9, 3, 7, 1, 8, 2, 6, 4, 5, 0};
        int[] sorted = ArrayUtils.copyOf(a);
        Arrays.sort(sorted);
        for (int k = 0; k < a.length; k++) {
            int[] copy = ArrayUtils.copyOf(a);
            assertEquals(sorted[k], DeterministicSelect.select(copy, k));
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 5, 31, 100, 1_001, 5_000})
    void matchesSortMedian(int n) {
        int[] a = ArrayUtils.randomArray(n, n * 10);
        int[] sorted = ArrayUtils.copyOf(a);
        Arrays.sort(sorted);
        int k = n / 2;
        Metrics m = new Metrics();
        int got = DeterministicSelect.select(a, k, m);
        assertEquals(sorted[k], got);
        assertTrue(m.getMaxRecursionDepth() > 0);
    }

    @Test
    void minAndMax() {
        int[] a = ArrayUtils.randomArray(200, 1_000);
        int[] sorted = ArrayUtils.copyOf(a);
        Arrays.sort(sorted);
        assertEquals(sorted[0], DeterministicSelect.select(ArrayUtils.copyOf(a), 0));
        assertEquals(sorted[a.length - 1], DeterministicSelect.select(ArrayUtils.copyOf(a), a.length - 1));
    }

    @Test
    void rejectsBadK() {
        int[] a = {1, 2, 3};
        assertThrows(IllegalArgumentException.class, () -> DeterministicSelect.select(a, -1));
        assertThrows(IllegalArgumentException.class, () -> DeterministicSelect.select(a, 3));
    }
}
