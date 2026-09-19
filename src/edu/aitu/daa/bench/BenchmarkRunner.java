package edu.aitu.daa.bench;

import edu.aitu.daa.algorithms.ClosestPair;
import edu.aitu.daa.algorithms.DeterministicSelect;
import edu.aitu.daa.algorithms.MergeSort;
import edu.aitu.daa.algorithms.QuickSort;
import edu.aitu.daa.metrics.Metrics;
import edu.aitu.daa.util.ArrayUtils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;

/**
 * Runs timing / depth experiments and writes results/results.csv.
 */
public final class BenchmarkRunner {
    private static final int[] SIZES = {100, 500, 1_000, 5_000, 10_000, 50_000, 100_000};
    private static final int WARMUP = 2;
    private static final int TRIALS = 5;

    private BenchmarkRunner() {
    }

    public static void run(Path csvPath) throws IOException {
        Files.createDirectories(csvPath.getParent());
        try (BufferedWriter w = Files.newBufferedWriter(csvPath, StandardCharsets.UTF_8)) {
            w.write("algorithm,input_type,n,time_ms,max_depth,comparisons,swaps,extra\n");

            for (int n : SIZES) {
                benchmarkSort("MergeSort", "random", n, ArrayUtils.randomArray(n, n * 10), w, true);
                benchmarkSort("MergeSort", "sorted", n, ArrayUtils.sortedArray(n), w, true);
                benchmarkSort("MergeSort", "reverse", n, ArrayUtils.reverseSortedArray(n), w, true);
                benchmarkSort("MergeSort", "nearly_sorted", n, ArrayUtils.nearlySortedArray(n, Math.max(1, n / 100)), w, true);

                benchmarkSort("QuickSort", "random", n, ArrayUtils.randomArray(n, n * 10), w, false);
                benchmarkSort("QuickSort", "sorted", n, ArrayUtils.sortedArray(n), w, false);
                benchmarkSort("QuickSort", "reverse", n, ArrayUtils.reverseSortedArray(n), w, false);
                benchmarkSort("QuickSort", "duplicates", n, ArrayUtils.duplicatesHeavyArray(n, Math.max(1, n / 1000)), w, false);

                benchmarkSelect(n, w);
                if (n <= 50_000) {
                    benchmarkClosest(n, w);
                }
            }
        }
        System.out.println("Wrote " + csvPath.toAbsolutePath());
    }

    private static void benchmarkSort(String name, String inputType, int n, int[] base,
                                      BufferedWriter w, boolean merge) throws IOException {
        Metrics agg = new Metrics();
        double timeSum = 0;
        int depthSum = 0;
        long cmpSum = 0;
        long swapSum = 0;

        for (int t = 0; t < WARMUP + TRIALS; t++) {
            int[] a = ArrayUtils.copyOf(base);
            Metrics m = new Metrics();
            m.startTimer();
            if (merge) {
                MergeSort.sort(a, m);
            } else {
                QuickSort.sort(a, m);
            }
            m.stopTimer();
            if (!ArrayUtils.isSorted(a)) {
                throw new IllegalStateException(name + " failed to sort " + inputType + " n=" + n);
            }
            if (t >= WARMUP) {
                timeSum += m.getElapsedMillis();
                depthSum += m.getMaxRecursionDepth();
                cmpSum += m.getComparisons();
                swapSum += m.getSwaps();
            }
        }

        writeRow(w, name, inputType, n, timeSum / TRIALS, depthSum / TRIALS, cmpSum / TRIALS, swapSum / TRIALS, "");
        System.out.printf(Locale.US, "%s %-14s n=%-7d time=%.3f ms depth=%d%n",
                name, inputType, n, timeSum / TRIALS, depthSum / TRIALS);
    }

    private static void benchmarkSelect(int n, BufferedWriter w) throws IOException {
        int[] base = ArrayUtils.randomArray(n, n * 10);
        int k = n / 2;
        double timeSum = 0;
        int depthSum = 0;
        long cmpSum = 0;

        for (int t = 0; t < WARMUP + TRIALS; t++) {
            int[] a = ArrayUtils.copyOf(base);
            Metrics m = new Metrics();
            m.startTimer();
            int got = DeterministicSelect.select(a, k, m);
            m.stopTimer();
            int[] sorted = ArrayUtils.copyOf(base);
            Arrays.sort(sorted);
            if (got != sorted[k]) {
                throw new IllegalStateException("Select mismatch at n=" + n);
            }
            if (t >= WARMUP) {
                timeSum += m.getElapsedMillis();
                depthSum += m.getMaxRecursionDepth();
                cmpSum += m.getComparisons();
            }
        }
        writeRow(w, "DeterministicSelect", "random_median", n, timeSum / TRIALS, depthSum / TRIALS,
                cmpSum / TRIALS, 0, "k=" + k);
        System.out.printf(Locale.US, "Select median     n=%-7d time=%.3f ms depth=%d%n",
                n, timeSum / TRIALS, depthSum / TRIALS);
    }

    private static void benchmarkClosest(int n, BufferedWriter w) throws IOException {
        ClosestPair.Point[] points = ClosestPair.randomPoints(n, 1_000_000.0, 42L + n);
        double timeSum = 0;
        int depthSum = 0;
        long cmpSum = 0;
        double dist = 0;

        for (int t = 0; t < WARMUP + TRIALS; t++) {
            Metrics m = new Metrics();
            m.startTimer();
            ClosestPair.Result r = ClosestPair.closest(points, m);
            m.stopTimer();
            if (t >= WARMUP) {
                timeSum += m.getElapsedMillis();
                depthSum += m.getMaxRecursionDepth();
                cmpSum += m.getComparisons();
                dist = r.distance;
            }
        }
        writeRow(w, "ClosestPair", "uniform_random", n, timeSum / TRIALS, depthSum / TRIALS,
                cmpSum / TRIALS, 0, String.format(Locale.US, "dist=%.6f", dist));
        System.out.printf(Locale.US, "ClosestPair       n=%-7d time=%.3f ms depth=%d%n",
                n, timeSum / TRIALS, depthSum / TRIALS);
    }

    private static void writeRow(BufferedWriter w, String algo, String input, int n,
                                 double timeMs, int depth, long comparisons, long swaps,
                                 String extra) throws IOException {
        w.write(String.format(Locale.US, "%s,%s,%d,%.6f,%d,%d,%d,%s%n",
                algo, input, n, timeMs, depth, comparisons, swaps, extra));
    }

    /** Quick smoke demo for console output / screenshots. */
    public static void demo() {
        System.out.println("=== Divide-and-Conquer Demo ===");
        int[] sample = {9, 3, 7, 1, 8, 2, 6, 4, 5, 0};
        int[] m = ArrayUtils.copyOf(sample);
        int[] q = ArrayUtils.copyOf(sample);
        Metrics mm = new Metrics();
        Metrics mq = new Metrics();
        mm.startTimer();
        MergeSort.sort(m, mm);
        mm.stopTimer();
        mq.startTimer();
        QuickSort.sort(q, mq);
        mq.stopTimer();
        System.out.println("MergeSort: " + Arrays.toString(m) + "  time=" + mm.getElapsedMillis() + " ms depth=" + mm.getMaxRecursionDepth());
        System.out.println("QuickSort: " + Arrays.toString(q) + "  time=" + mq.getElapsedMillis() + " ms depth=" + mq.getMaxRecursionDepth());

        int[] sel = ArrayUtils.copyOf(sample);
        int median = DeterministicSelect.select(sel, sel.length / 2);
        System.out.println("DeterministicSelect median (k=5): " + median);

        ClosestPair.Point[] pts = {
                new ClosestPair.Point(0, 0),
                new ClosestPair.Point(3, 4),
                new ClosestPair.Point(1, 1),
                new ClosestPair.Point(10, 10),
                new ClosestPair.Point(1.5, 1.2)
        };
        ClosestPair.Result r = ClosestPair.closest(pts);
        System.out.printf(Locale.US, "Closest pair: %s - %s  dist=%.4f%n", r.a, r.b, r.distance);

        System.out.println();
        System.out.println("Small benchmark (n=10_000 random):");
        int[] big = ArrayUtils.randomArray(10_000, 100_000);
        Metrics bm = new Metrics();
        int[] b1 = ArrayUtils.copyOf(big);
        bm.startTimer();
        MergeSort.sort(b1, bm);
        bm.stopTimer();
        Metrics bq = new Metrics();
        int[] b2 = ArrayUtils.copyOf(big);
        bq.startTimer();
        QuickSort.sort(b2, bq);
        bq.stopTimer();
        System.out.printf(Locale.US, "  MergeSort  %.3f ms  depth=%d  comparisons=%d%n",
                bm.getElapsedMillis(), bm.getMaxRecursionDepth(), bm.getComparisons());
        System.out.printf(Locale.US, "  QuickSort  %.3f ms  depth=%d  comparisons=%d%n",
                bq.getElapsedMillis(), bq.getMaxRecursionDepth(), bq.getComparisons());

        Metrics bs = new Metrics();
        int[] b3 = ArrayUtils.copyOf(big);
        bs.startTimer();
        DeterministicSelect.select(b3, 5000, bs);
        bs.stopTimer();
        System.out.printf(Locale.US, "  Select     %.3f ms  depth=%d  comparisons=%d%n",
                bs.getElapsedMillis(), bs.getMaxRecursionDepth(), bs.getComparisons());

        ClosestPair.Point[] pts2 = ClosestPair.randomPoints(5_000, 10_000, new Random(1).nextLong());
        Metrics bc = new Metrics();
        bc.startTimer();
        ClosestPair.Result cr = ClosestPair.closest(pts2, bc);
        bc.stopTimer();
        System.out.printf(Locale.US, "  ClosestPair %.3f ms  depth=%d  dist=%.6f%n",
                bc.getElapsedMillis(), bc.getMaxRecursionDepth(), cr.distance);
    }
}
