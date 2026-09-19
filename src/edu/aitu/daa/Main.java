package edu.aitu.daa;

import edu.aitu.daa.bench.BenchmarkRunner;
import java.nio.file.Path;

public final class Main {
    public static void main(String[] args) throws Exception {
        if (args.length > 0 && "bench".equalsIgnoreCase(args[0])) {
            BenchmarkRunner.run(Path.of("results", "results.csv"));
            return;
        }
        BenchmarkRunner.demo();
    }
}
