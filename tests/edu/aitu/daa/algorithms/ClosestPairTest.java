package edu.aitu.daa.algorithms;

import edu.aitu.daa.metrics.Metrics;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ClosestPairTest {

    @Test
    void simpleKnownPair() {
        ClosestPair.Point[] pts = {
                new ClosestPair.Point(0, 0),
                new ClosestPair.Point(5, 5),
                new ClosestPair.Point(1, 1),
                new ClosestPair.Point(10, 0)
        };
        ClosestPair.Result r = ClosestPair.closest(pts);
        assertEquals(Math.sqrt(2.0), r.distance, 1e-9);
    }

    @Test
    void matchesBruteForceSmall() {
        ClosestPair.Point[] pts = ClosestPair.randomPoints(80, 1_000, 7L);
        ClosestPair.Result fast = ClosestPair.closest(pts);
        ClosestPair.Result slow = ClosestPair.bruteForceAll(pts);
        assertEquals(slow.distance, fast.distance, 1e-9);
    }

    @ParameterizedTest
    @ValueSource(ints = {10, 50, 200, 500})
    void matchesBruteForce(int n) {
        ClosestPair.Point[] pts = ClosestPair.randomPoints(n, 10_000, n * 17L);
        Metrics m = new Metrics();
        ClosestPair.Result fast = ClosestPair.closest(pts, m);
        ClosestPair.Result slow = ClosestPair.bruteForceAll(pts);
        assertEquals(slow.distance, fast.distance, 1e-9);
        assertTrue(m.getMaxRecursionDepth() > 0);
        assertTrue(m.getMaxRecursionDepth() < n);
    }

    @Test
    void duplicateXCoordinates() {
        ClosestPair.Point[] pts = {
                new ClosestPair.Point(1, 0),
                new ClosestPair.Point(1, 5),
                new ClosestPair.Point(1, 0.5),
                new ClosestPair.Point(8, 8)
        };
        ClosestPair.Result fast = ClosestPair.closest(pts);
        ClosestPair.Result slow = ClosestPair.bruteForceAll(pts);
        assertEquals(slow.distance, fast.distance, 1e-9);
    }

    @Test
    void rejectsTooFewPoints() {
        assertThrows(IllegalArgumentException.class,
                () -> ClosestPair.closest(new ClosestPair.Point[]{new ClosestPair.Point(0, 0)}));
    }

    @Test
    void identicalPointsHaveZeroDistance() {
        ClosestPair.Point[] pts = {
                new ClosestPair.Point(2, 2),
                new ClosestPair.Point(2, 2),
                new ClosestPair.Point(9, 9)
        };
        ClosestPair.Result r = ClosestPair.closest(pts);
        assertEquals(0.0, r.distance, 1e-12);
    }
}
