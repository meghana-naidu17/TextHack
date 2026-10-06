package practical;

import java.util.*;
import java.util.concurrent.*;

/**
 * CO6: Implementation of Parallel Prefix-Sum and Performance Benchmarking of Sequential and Parallel Execution.
 *
 * Demonstrates:
 * 1. Work-efficient Parallel Prefix-Sum (Scan) Algorithm:
 *    - Phase 1: Parallel local scan & block reduction (Up-sweep).
 *    - Phase 2: Sequential prefix scan on block aggregates.
 *    - Phase 3: Parallel distribution of block offsets (Down-sweep).
 * 2. Sequential linear-time Prefix-Sum O(n).
 * 3. Java standard library Arrays.parallelPrefix reference.
 * 4. Step-by-step educational trace on small arrays.
 * 5. High-throughput performance benchmarking across 500,000 to 10,000,000 elements
 *    measuring execution time (ms), throughput (million elements/sec), and multi-core speedup factor.
 */
public class ParallelPrefixSumBenchmark {

    private static final int DEFAULT_THRESHOLD = 50_000;

    // -------------------------------------------------------------
    // ALGORITHM IMPLEMENTATIONS
    // -------------------------------------------------------------

    /**
     * Sequential Prefix-Sum in O(n) time.
     */
    public static long[] sequentialPrefixSum(long[] input) {
        long[] result = new long[input.length];
        if (input.length == 0) return result;

        result[0] = input[0];
        for (int i = 1; i < input.length; i++) {
            result[i] = result[i - 1] + input[i];
        }
        return result;
    }

    /**
     * Custom Multi-Threaded Work-Efficient Block Parallel Prefix-Sum.
     * Uses ForkJoinPool to parallelize Phase 1 (local scans) and Phase 3 (offset distribution).
     */
    public static long[] parallelPrefixSum(long[] input, ForkJoinPool pool, int numThreads) {
        int n = input.length;
        long[] output = new long[n];
        if (n == 0) return output;

        // Determine number of blocks based on available threads
        int blockSize = Math.max(DEFAULT_THRESHOLD, (n + numThreads - 1) / numThreads);
        int blockCount = (n + blockSize - 1) / blockSize;

        long[] blockSums = new long[blockCount];

        // PHASE 1: Parallel local prefix scan on each block
        List<Callable<Void>> phase1Tasks = new ArrayList<>(blockCount);
        for (int b = 0; b < blockCount; b++) {
            final int blockIdx = b;
            final int start = b * blockSize;
            final int end = Math.min(n, start + blockSize);

            phase1Tasks.add(() -> {
                long running = 0;
                for (int i = start; i < end; i++) {
                    running += input[i];
                    output[i] = running;
                }
                blockSums[blockIdx] = running;
                return null;
            });
        }
        pool.invokeAll(phase1Tasks);

        // PHASE 2: Sequential prefix scan of the block aggregates
        long[] blockOffsets = new long[blockCount];
        long accum = 0;
        for (int b = 0; b < blockCount; b++) {
            blockOffsets[b] = accum;
            accum += blockSums[b];
        }

        // PHASE 3: Parallel distribution of block offsets
        List<Callable<Void>> phase3Tasks = new ArrayList<>(blockCount);
        for (int b = 1; b < blockCount; b++) { // Block 0 needs no offset
            final int start = b * blockSize;
            final int end = Math.min(n, start + blockSize);
            final long offset = blockOffsets[b];

            phase3Tasks.add(() -> {
                for (int i = start; i < end; i++) {
                    output[i] += offset;
                }
                return null;
            });
        }
        pool.invokeAll(phase3Tasks);

        return output;
    }

    /**
     * Standard library reference using Arrays.parallelPrefix.
     */
    public static long[] libraryParallelPrefix(long[] input) {
        long[] copy = Arrays.copyOf(input, input.length);
        Arrays.parallelPrefix(copy, Long::sum);
        return copy;
    }

    // -------------------------------------------------------------
    // EDUCATIONAL TRACE DEMO
    // -------------------------------------------------------------

    public static void runEducationalTrace() {
        System.out.println("==================================================================");
        System.out.println("       PARALLEL PREFIX-SUM (SCAN) STEP-BY-STEP TRACE DEMO         ");
        System.out.println("==================================================================");

        long[] sample = {3, 1, 7, 0, 4, 1, 6, 3, 2, 5, 8, 4};
        int n = sample.length;
        int numThreads = 3;
        int blockSize = 4;
        int blockCount = 3;

        System.out.printf("Input Array (size %d): %s%n", n, Arrays.toString(sample));
        System.out.printf("Simulated Architecture: %d threads, Block size = %d elements%n", numThreads, blockSize);

        System.out.println("\n[PHASE 1: Parallel Local Block Scans]");
        long[] localScan = new long[n];
        long[] blockSums = new long[blockCount];

        for (int b = 0; b < blockCount; b++) {
            int start = b * blockSize;
            int end = Math.min(n, start + blockSize);
            long running = 0;
            System.out.printf("  Thread %d scanning Block %d [indices %d..%d]: [", b, b, start, end - 1);
            for (int i = start; i < end; i++) {
                running += sample[i];
                localScan[i] = running;
                System.out.printf("%d%s", running, (i < end - 1) ? ", " : "");
            }
            blockSums[b] = running;
            System.out.printf("] -> Block Sum = %d%n", running);
        }

        System.out.println("\n[PHASE 2: Sequential Block Aggregates Scan]");
        System.out.printf("  Block Sums Array: %s%n", Arrays.toString(blockSums));
        long[] blockOffsets = new long[blockCount];
        long accum = 0;
        for (int b = 0; b < blockCount; b++) {
            blockOffsets[b] = accum;
            accum += blockSums[b];
        }
        System.out.printf("  Calculated Block Offsets: %s%n", Arrays.toString(blockOffsets));

        System.out.println("\n[PHASE 3: Parallel Distribution of Block Offsets]");
        long[] finalResult = Arrays.copyOf(localScan, n);
        for (int b = 1; b < blockCount; b++) {
            int start = b * blockSize;
            int end = Math.min(n, start + blockSize);
            long offset = blockOffsets[b];
            System.out.printf("  Thread %d adding offset (+%d) to Block %d [indices %d..%d]%n",
                    b, offset, b, start, end - 1);
            for (int i = start; i < end; i++) {
                finalResult[i] += offset;
            }
        }

        System.out.println("\n[FINAL VERIFICATION]");
        long[] expected = sequentialPrefixSum(sample);
        System.out.printf("  Sequential Result: %s%n", Arrays.toString(expected));
        System.out.printf("  Parallel Result  : %s%n", Arrays.toString(finalResult));
        System.out.printf("  Verification     : %s%n", Arrays.equals(expected, finalResult) ? "EXACT MATCH [PASS]" : "MISMATCH [FAIL]");
    }

    // -------------------------------------------------------------
    // BENCHMARKING SUITE
    // -------------------------------------------------------------

    public static void runBenchmarkSuite(Scanner scanner) {
        System.out.println("==================================================================");
        System.out.println("   PARALLEL VS SEQUENTIAL PREFIX-SUM PERFORMANCE BENCHMARK       ");
        System.out.println("==================================================================");

        int availableCores = Runtime.getRuntime().availableProcessors();
        System.out.printf("Available Hardware CPU Cores: %d threads%n", availableCores);

        System.out.println("\nSelect Array Size for Benchmark:");
        System.out.println(" [1] 1,000,000 elements   (1 Million - Fast)");
        System.out.println(" [2] 5,000,000 elements   (5 Million - Recommended)");
        System.out.println(" [3] 10,000,000 elements  (10 Million - High Load)");
        System.out.println(" [4] Custom size");
        System.out.print("Enter choice: ");

        String choice = scanner.nextLine().trim();
        int size = 5_000_000;
        if (choice.equals("1")) size = 1_000_000;
        else if (choice.equals("2")) size = 5_000_000;
        else if (choice.equals("3")) size = 10_000_000;
        else if (choice.equals("4")) {
            System.out.print("Enter custom element count: ");
            try {
                size = Integer.parseInt(scanner.nextLine().trim());
                if (size <= 0) size = 5_000_000;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input, using default 5,000,000.");
            }
        }

        System.out.printf("\nGenerating test dataset with %,d long integers...%n", size);
        long[] data = new long[size];
        Random rand = new Random(42);
        for (int i = 0; i < size; i++) {
            data[i] = rand.nextInt(100) + 1;
        }

        ForkJoinPool pool = new ForkJoinPool(availableCores);

        // Warm-up to trigger JIT optimizations
        System.out.println("Warming up JIT compiler and memory pages (2 iterations)...");
        for (int w = 0; w < 2; w++) {
            sequentialPrefixSum(Arrays.copyOf(data, Math.min(size, 200_000)));
            parallelPrefixSum(Arrays.copyOf(data, Math.min(size, 200_000)), pool, availableCores);
        }

        System.out.println("Executing 3 benchmark trials for each algorithm...");

        // 1. Sequential Benchmark
        long[] seqResult = null;
        long seqTotalTime = 0;
        for (int t = 0; t < 3; t++) {
            long start = System.nanoTime();
            seqResult = sequentialPrefixSum(data);
            seqTotalTime += (System.nanoTime() - start);
        }
        double seqAvgMs = (seqTotalTime / 3.0) / 1_000_000.0;

        // 2. Custom Parallel Benchmark
        long[] parResult = null;
        long parTotalTime = 0;
        for (int t = 0; t < 3; t++) {
            long start = System.nanoTime();
            parResult = parallelPrefixSum(data, pool, availableCores);
            parTotalTime += (System.nanoTime() - start);
        }
        double parAvgMs = (parTotalTime / 3.0) / 1_000_000.0;

        // 3. Java Built-in Arrays.parallelPrefix Benchmark
        long[] libResult = null;
        long libTotalTime = 0;
        for (int t = 0; t < 3; t++) {
            long start = System.nanoTime();
            libResult = libraryParallelPrefix(data);
            libTotalTime += (System.nanoTime() - start);
        }
        double libAvgMs = (libTotalTime / 3.0) / 1_000_000.0;

        // Verification Check
        boolean parCorrect = Arrays.equals(seqResult, parResult);
        boolean libCorrect = Arrays.equals(seqResult, libResult);

        // Display Benchmark Report
        System.out.println("\n=========================== BENCHMARK RESULTS ===========================");
        System.out.printf("Dataset Size        : %,d elements (%.2f MB)%n", size, (size * 8.0) / (1024 * 1024));
        System.out.printf("CPU Worker Threads  : %d threads%n", availableCores);
        System.out.println("-------------------------------------------------------------------------");
        System.out.printf("%-30s | %-12s | %-18s | %s%n",
                "Algorithm", "Time (Avg)", "Throughput", "Speedup vs Seq");
        System.out.println("-------------------------------------------------------------------------");

        double seqThroughput = (size / (seqAvgMs / 1000.0)) / 1_000_000.0;
        System.out.printf("%-30s | %8.2f ms | %8.2f MOps/s | 1.00x (Baseline)%n",
                "1. Sequential Prefix-Sum", seqAvgMs, seqThroughput);

        double parThroughput = (size / (parAvgMs / 1000.0)) / 1_000_000.0;
        double parSpeedup = (parAvgMs > 0) ? (seqAvgMs / parAvgMs) : 1.0;
        System.out.printf("%-30s | %8.2f ms | %8.2f MOps/s | %.2fx faster%n",
                "2. Custom Multi-Threaded", parAvgMs, parThroughput, parSpeedup);

        double libThroughput = (size / (libAvgMs / 1000.0)) / 1_000_000.0;
        double libSpeedup = (libAvgMs > 0) ? (seqAvgMs / libAvgMs) : 1.0;
        System.out.printf("%-30s | %8.2f ms | %8.2f MOps/s | %.2fx faster%n",
                "3. Arrays.parallelPrefix", libAvgMs, libThroughput, libSpeedup);

        System.out.println("-------------------------------------------------------------------------");
        System.out.printf("Correctness Validation: Custom Parallel = %s | Library Parallel = %s%n",
                parCorrect ? "PASS [100%]" : "FAIL", libCorrect ? "PASS [100%]" : "FAIL");
        System.out.println("=========================================================================");

        pool.shutdown();
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        run(scanner);
        scanner.close();
    }

    public static void run(Scanner scanner) {
        while (true) {
            System.out.println("\n==================================================================");
            System.out.println("   CO6: PARALLEL PREFIX-SUM & PERFORMANCE BENCHMARKING           ");
            System.out.println("==================================================================");
            System.out.println(" [1] Educational Step-by-Step Parallel Scan Trace");
            System.out.println(" [2] Run Comprehensive Multi-Core Performance Benchmark");
            System.out.println(" [3] Return to Main Menu");
            System.out.print("Enter choice: ");

            String input = scanner.nextLine().trim();
            if (input.equals("1")) {
                runEducationalTrace();
            } else if (input.equals("2")) {
                runBenchmarkSuite(scanner);
            } else if (input.equals("3") || input.equalsIgnoreCase("exit")) {
                System.out.println("\nReturning to Main Menu...");
                break;
            } else {
                System.out.println("Invalid choice! Please select 1, 2, or 3.");
            }
        }
    }
}
