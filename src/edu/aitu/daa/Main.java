package edu.aitu.daa;

import edu.aitu.daa.bench.BenchmarkRunner;

import java.nio.file.Path;

/**
 * Entry point.
 *
 * Usage:
 *   mvn -q exec:java                 # demo output
 *   mvn -q exec:java -Dexec.args=bench
 */
public final class Main {
    public static void main(String[] args) throws Exception {
        if (args.length > 0 && "bench".equalsIgnoreCase(args[0])) {
            Path csv = Path.of("results", "results.csv");
            BenchmarkRunner.run(csv);
            return;
        }
        BenchmarkRunner.demo();
    }
}
