package edu.aitu.daa.metrics;

/**
 * Collects algorithmic counters for experimental analysis.
 */
public final class Metrics {
    private long comparisons;
    private long swaps;
    private long allocations;
    private int recursionDepth;
    private int maxRecursionDepth;
    private long startNanos;
    private long elapsedNanos;

    public void reset() {
        comparisons = 0;
        swaps = 0;
        allocations = 0;
        recursionDepth = 0;
        maxRecursionDepth = 0;
        startNanos = 0;
        elapsedNanos = 0;
    }

    public void startTimer() {
        startNanos = System.nanoTime();
    }

    public void stopTimer() {
        elapsedNanos = System.nanoTime() - startNanos;
    }

    public void enter() {
        recursionDepth++;
        if (recursionDepth > maxRecursionDepth) {
            maxRecursionDepth = recursionDepth;
        }
    }

    public void leave() {
        recursionDepth--;
    }

    public void compare() {
        comparisons++;
    }

    public void compare(long count) {
        comparisons += count;
    }

    public void swap() {
        swaps++;
    }

    public void allocate(long count) {
        allocations += count;
    }

    public long getComparisons() {
        return comparisons;
    }

    public long getSwaps() {
        return swaps;
    }

    public long getAllocations() {
        return allocations;
    }

    public int getMaxRecursionDepth() {
        return maxRecursionDepth;
    }

    public long getElapsedNanos() {
        return elapsedNanos;
    }

    public double getElapsedMillis() {
        return elapsedNanos / 1_000_000.0;
    }
}
