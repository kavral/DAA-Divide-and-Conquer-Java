package edu.aitu.daa.algorithms;

import edu.aitu.daa.metrics.Metrics;

import java.util.Arrays;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Closest pair of points in the plane via divide-and-conquer.
 * Maintains a y-sorted view with a merge step (like MergeSort) so the
 * strip check stays O(n). Complexity: Θ(n log n).
 */
public final class ClosestPair {
    private ClosestPair() {
    }

    public static final class Point {
        public final double x;
        public final double y;

        public Point(double x, double y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return "(" + x + ", " + y + ")";
        }
    }

    public static final class Result {
        public final Point a;
        public final Point b;
        public final double distance;

        public Result(Point a, Point b, double distance) {
            this.a = a;
            this.b = b;
            this.distance = distance;
        }
    }

    public static Result closest(Point[] points) {
        return closest(points, null);
    }

    public static Result closest(Point[] points, Metrics metrics) {
        if (points == null || points.length < 2) {
            throw new IllegalArgumentException("need at least two points");
        }

        Point[] byX = Arrays.copyOf(points, points.length);
        Arrays.sort(byX, Comparator.comparingDouble(p -> p.x));
        if (metrics != null) {
            metrics.allocate(points.length);
        }

        Point[] byY = Arrays.copyOf(byX, byX.length);
        Arrays.sort(byY, Comparator.comparingDouble(p -> p.y));
        if (metrics != null) {
            metrics.allocate(points.length);
        }

        Point[] aux = new Point[points.length];
        return closest(byX, byY, aux, 0, points.length - 1, metrics);
    }

    private static Result closest(Point[] byX, Point[] byY, Point[] aux, int left, int right, Metrics metrics) {
        if (metrics != null) {
            metrics.enter();
        }
        try {
            int n = right - left + 1;
            if (n <= 3) {
                // Keep byY sorted by y inside this tiny range.
                Arrays.sort(byY, left, right + 1, Comparator.comparingDouble(p -> p.y));
                return bruteForce(byX, left, right, metrics);
            }

            int mid = left + ((right - left) >>> 1);
            double midX = byX[mid].x;

            Map<Point, Boolean> inLeft = new IdentityHashMap<>();
            for (int i = left; i <= mid; i++) {
                inLeft.put(byX[i], Boolean.TRUE);
            }

            // Partition y-order into left / right halves without destroying relative y-order.
            int li = left;
            int ri = mid + 1;
            for (int i = left; i <= right; i++) {
                if (inLeft.containsKey(byY[i])) {
                    aux[li++] = byY[i];
                } else {
                    aux[ri++] = byY[i];
                }
            }
            System.arraycopy(aux, left, byY, left, n);

            Result leftRes = closest(byX, byY, aux, left, mid, metrics);
            Result rightRes = closest(byX, byY, aux, mid + 1, right, metrics);
            Result best = leftRes.distance <= rightRes.distance ? leftRes : rightRes;
            double delta = best.distance;

            // Merge y-sorted halves back (same as MergeSort merge on y-coordinate).
            mergeByY(byY, aux, left, mid, right);

            int stripSize = 0;
            for (int i = left; i <= right; i++) {
                if (metrics != null) {
                    metrics.compare();
                }
                if (Math.abs(byY[i].x - midX) < delta) {
                    aux[stripSize++] = byY[i];
                }
            }

            for (int i = 0; i < stripSize; i++) {
                for (int j = i + 1; j < stripSize && (aux[j].y - aux[i].y) < delta; j++) {
                    if (metrics != null) {
                        metrics.compare();
                    }
                    double d = distance(aux[i], aux[j]);
                    if (d < delta) {
                        delta = d;
                        best = new Result(aux[i], aux[j], d);
                    }
                }
            }
            return best;
        } finally {
            if (metrics != null) {
                metrics.leave();
            }
        }
    }

    private static void mergeByY(Point[] byY, Point[] aux, int left, int mid, int right) {
        int i = left;
        int j = mid + 1;
        int k = left;
        while (i <= mid && j <= right) {
            if (byY[i].y <= byY[j].y) {
                aux[k++] = byY[i++];
            } else {
                aux[k++] = byY[j++];
            }
        }
        while (i <= mid) {
            aux[k++] = byY[i++];
        }
        while (j <= right) {
            aux[k++] = byY[j++];
        }
        System.arraycopy(aux, left, byY, left, right - left + 1);
    }

    private static Result bruteForce(Point[] points, int left, int right, Metrics metrics) {
        Result best = null;
        for (int i = left; i <= right; i++) {
            for (int j = i + 1; j <= right; j++) {
                if (metrics != null) {
                    metrics.compare();
                }
                double d = distance(points[i], points[j]);
                if (best == null || d < best.distance) {
                    best = new Result(points[i], points[j], d);
                }
            }
        }
        return best;
    }

    public static double distance(Point a, Point b) {
        double dx = a.x - b.x;
        double dy = a.y - b.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    /** O(n²) reference used for testing. */
    public static Result bruteForceAll(Point[] points) {
        Result best = null;
        for (int i = 0; i < points.length; i++) {
            for (int j = i + 1; j < points.length; j++) {
                double d = distance(points[i], points[j]);
                if (best == null || d < best.distance) {
                    best = new Result(points[i], points[j], d);
                }
            }
        }
        return best;
    }

    public static Point[] randomPoints(int n, double bound, long seed) {
        java.util.Random rng = new java.util.Random(seed);
        Point[] points = new Point[n];
        for (int i = 0; i < n; i++) {
            points[i] = new Point(rng.nextDouble() * bound, rng.nextDouble() * bound);
        }
        return points;
    }
}
