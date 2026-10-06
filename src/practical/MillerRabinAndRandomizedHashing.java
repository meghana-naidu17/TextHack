package practical;

import java.io.File;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.*;

/**
 * CO6: Implementation of Miller-Rabin Primality Testing and Randomized Hashing Techniques.
 *
 * Demonstrates:
 * 1. Miller-Rabin Probabilistic Primality Test:
 *    - Decomposes n - 1 = 2^s * d (d odd).
 *    - Witness loop checking a^d mod n and repeated squarings.
 *    - Step-by-step mathematical trace for educational inspection.
 *    - Benchmark vs Naive Trial Division.
 * 2. Randomized Hashing Techniques:
 *    - 2-Universal Hashing (Carter-Wegman family: h_{a,b}(x) = ((a*x + b) mod p) mod m).
 *    - Defending against worst-case hash-collision attacks in text processing.
 *    - Hash collision and bucket uniformity simulation using dataset tokens.
 */
public class MillerRabinAndRandomizedHashing {

    private static final SecureRandom RANDOM = new SecureRandom();

    // -------------------------------------------------------------
    // PART 1: MILLER-RABIN PRIMALITY TESTING
    // -------------------------------------------------------------

    /**
     * Miller-Rabin test for BigInteger with k independent random rounds.
     * Probability of a composite number being falsely declared prime is <= (1/4)^k.
     */
    public static boolean isProbablePrime(BigInteger n, int k, boolean trace) {
        // Base edge cases
        if (n.compareTo(BigInteger.ONE) <= 0) return false;
        if (n.equals(BigInteger.TWO) || n.equals(BigInteger.valueOf(3))) return true;
        if (!n.testBit(0)) return false; // Even number > 2 is composite

        // Write n - 1 = 2^s * d, where d is odd
        BigInteger nMinusOne = n.subtract(BigInteger.ONE);
        BigInteger d = nMinusOne;
        int s = 0;
        while (!d.testBit(0)) {
            d = d.shiftRight(1);
            s++;
        }

        if (trace) {
            System.out.println("\n[Miller-Rabin Decomposition]");
            System.out.printf("  n         = %s%n", n);
            System.out.printf("  n - 1     = 2^%d * %s (s = %d, d = %s)%n", s, d, s, d);
            System.out.printf("  Testing %d independent witness rounds...%n", k);
        }

        for (int round = 1; round <= k; round++) {
            // Pick random witness a in [2, n - 2]
            BigInteger a = getRandomBase(n);

            // Compute x = a^d mod n
            BigInteger x = a.modPow(d, n);

            if (trace) {
                System.out.printf("\nRound %d/%d with witness a = %s:%n", round, k, a);
                System.out.printf("  x = a^d mod n = %s%n", x);
            }

            if (x.equals(BigInteger.ONE) || x.equals(nMinusOne)) {
                if (trace) System.out.println("  -> Immediate witness pass (x == 1 or x == n-1)");
                continue;
            }

            boolean compositeWitnessFound = true;
            for (int r = 1; r < s; r++) {
                x = x.modPow(BigInteger.TWO, n);
                if (trace) {
                    System.out.printf("  Squaring step %d/%d: x^2 mod n = %s%n", r, s - 1, x);
                }

                if (x.equals(nMinusOne)) {
                    if (trace) System.out.println("  -> Witness pass (reached n - 1 during squaring)");
                    compositeWitnessFound = false;
                    break;
                }
                if (x.equals(BigInteger.ONE)) {
                    // Non-trivial square root of 1 found modulo n -> definitively composite!
                    if (trace) System.out.println("  -> Non-trivial square root of 1 found! Definitively composite.");
                    return false;
                }
            }

            if (compositeWitnessFound) {
                if (trace) {
                    System.out.printf("  -> Round %d failed: %s is a confirmed COMPOSITE (witness a = %s)%n",
                            round, n, a);
                }
                return false;
            }
        }

        if (trace) {
            System.out.printf("\n[RESULT] %s passed all %d rounds! Confirmed PROBABLE PRIME (error prob <= %.2e)%n",
                    n, k, Math.pow(0.25, k));
        }
        return true;
    }

    private static BigInteger getRandomBase(BigInteger n) {
        BigInteger max = n.subtract(BigInteger.TWO); // n - 2
        BigInteger min = BigInteger.TWO;             // 2
        BigInteger range = max.subtract(min).add(BigInteger.ONE);

        int bits = range.bitLength();
        BigInteger result;
        do {
            result = new BigInteger(bits, RANDOM);
        } while (result.compareTo(range) >= 0);

        return result.add(min);
    }

    /**
     * Naive trial division for performance comparison.
     */
    public static boolean isPrimeTrialDivision(long n) {
        if (n <= 1) return false;
        if (n <= 3) return true;
        if (n % 2 == 0 || n % 3 == 0) return false;

        for (long i = 5; i * i <= n; i += 6) {
            if (n % i == 0 || n % (i + 2) == 0) {
                return false;
            }
        }
        return true;
    }

    public static void runPrimalityDemo(Scanner scanner) {
        System.out.println("==================================================================");
        System.out.println("        MILLER-RABIN PROBABILISTIC PRIMALITY TESTING              ");
        System.out.println("==================================================================");

        System.out.println("Options:");
        System.out.println("1. Test Custom Number with Step-by-Step Mathematical Trace");
        System.out.println("2. Run Cryptographic & Benchmark Suite (Miller-Rabin vs Trial Division)");
        System.out.print("Enter choice: ");

        String choice = scanner.nextLine().trim();
        if (choice.equals("1")) {
            System.out.print("\nEnter an integer to test for primality: ");
            String numStr = scanner.nextLine().trim();
            try {
                BigInteger num = new BigInteger(numStr);
                System.out.print("Enter number of witness rounds k (e.g. 5 to 20): ");
                int k = Integer.parseInt(scanner.nextLine().trim());
                if (k <= 0) k = 10;

                long start = System.nanoTime();
                boolean result = isProbablePrime(num, k, true);
                long elapsed = System.nanoTime() - start;

                System.out.println("\n--------------------------------------------------");
                System.out.printf("Verdict      : %s%n", result ? "PRIME" : "COMPOSITE");
                System.out.printf("Time Elapsed : %,d ns (%.3f ms)%n", elapsed, elapsed / 1_000_000.0);
                System.out.println("--------------------------------------------------");
            } catch (Exception e) {
                System.out.println("Invalid integer input: " + e.getMessage());
            }
        } else if (choice.equals("2")) {
            runPrimalityBenchmark();
        } else {
            System.out.println("Invalid choice.");
        }
    }

    private static void runPrimalityBenchmark() {
        System.out.println("\n--- Benchmarking Miller-Rabin vs Trial Division ---");

        long[] testCases = {
            1000000007L,            // 10^9 + 7 (Prime)
            1000000009L,            // 10^9 + 9 (Prime)
            1000000000039L,         // 10^12 + 39 (Prime)
            1000000000000037L,      // 10^15 + 37 (Prime)
            2305843009213693951L    // 2^61 - 1 Mersenne Prime M61
        };

        System.out.printf("%-24s | %-12s | %-16s | %-16s | %s%n",
                "Target Number", "Result", "Miller-Rabin (10)", "Trial Division", "Speedup");
        System.out.println("-----------------------------------------------------------------------------------------");

        for (long val : testCases) {
            BigInteger bVal = BigInteger.valueOf(val);

            // Miller-Rabin timing
            long t1 = System.nanoTime();
            boolean mrResult = isProbablePrime(bVal, 10, false);
            long t2 = System.nanoTime();
            long mrTime = t2 - t1;

            // Trial division timing
            long t3 = System.nanoTime();
            boolean tdResult = isPrimeTrialDivision(val);
            long t4 = System.nanoTime();
            long tdTime = t4 - t3;

            double speedup = (mrTime > 0) ? (double) tdTime / mrTime : 1.0;

            System.out.printf("%-24d | %-12s | %,10d ns    | %,10d ns    | %.1fx faster%n",
                    val, mrResult ? "PRIME" : "COMPOSITE", mrTime, tdTime, speedup);
        }

        // Test large cryptographic prime (100+ digits)
        System.out.println("\nTesting 256-bit Cryptographic Prime:");
        BigInteger cryptoPrime = new BigInteger("115792089237316195423570985008687907853269984665640564039457584007913129639747");
        long startCrypto = System.nanoTime();
        boolean cryptoResult = isProbablePrime(cryptoPrime, 25, false);
        long elapsedCrypto = System.nanoTime() - startCrypto;
        System.out.printf("Value  : %s%n", cryptoPrime);
        System.out.printf("Verdict: %s (Miller-Rabin 25 rounds verified in %.2f ms)%n",
                cryptoResult ? "PRIME" : "COMPOSITE", elapsedCrypto / 1_000_000.0);
    }

    // -------------------------------------------------------------
    // PART 2: RANDOMIZED HASHING TECHNIQUES
    // -------------------------------------------------------------

    /**
     * Carter-Wegman 2-Universal Hash Family:
     * h_{a,b}(x) = ((a * x + b) mod p) mod m
     * where p is a large prime > maximum key value,
     * a in [1, p-1] is randomly chosen,
     * b in [0, p-1] is randomly chosen.
     */
    public static class UniversalStringHasher {
        private final long p; // Large prime
        private final long a; // Multiplier
        private final long b; // Additive salt
        private final int m;  // Table/Bucket size

        public UniversalStringHasher(int bucketCount) {
            this.m = bucketCount;
            // 2^31 - 1 (Mersenne prime M31)
            this.p = 2147483647L;
            this.a = 1 + (Math.abs(RANDOM.nextLong()) % (p - 1));
            this.b = Math.abs(RANDOM.nextLong()) % p;
        }

        public int hash(String str) {
            // First compress string to an integer key via polynomial rolling
            long key = 0;
            for (int i = 0; i < str.length(); i++) {
                key = (key * 31 + str.charAt(i)) % p;
            }
            if (key < 0) key += p;

            // Universal hash mapping: ((a * key + b) % p) % m
            long h = ((a * key + b) % p) % m;
            return (int) h;
        }

        public long getA() { return a; }
        public long getB() { return b; }
    }

    public static void runRandomizedHashingDemo(Scanner scanner) {
        System.out.println("==================================================================");
        System.out.println("     RANDOMIZED UNIVERSAL HASHING & COLLISION RESISTANCE DEMO     ");
        System.out.println("==================================================================");

        List<String> words = loadSampleWords();
        System.out.printf("Loaded %d vocabulary tokens from text dataset.%n", words.size());

        int bucketCount = 50;
        System.out.printf("Simulating hash distribution across %d hash buckets...%n", bucketCount);

        // 1. Standard Deterministic String Hash
        int[] standardBuckets = new int[bucketCount];
        for (String w : words) {
            int bucket = Math.abs(w.hashCode()) % bucketCount;
            standardBuckets[bucket]++;
        }

        // 2. Randomized Universal Hash Family Instance 1
        UniversalStringHasher hasher1 = new UniversalStringHasher(bucketCount);
        int[] universalBuckets1 = new int[bucketCount];
        for (String w : words) {
            universalBuckets1[hasher1.hash(w)]++;
        }

        // 3. Randomized Universal Hash Family Instance 2 (independent randomized parameters)
        UniversalStringHasher hasher2 = new UniversalStringHasher(bucketCount);
        int[] universalBuckets2 = new int[bucketCount];
        for (String w : words) {
            universalBuckets2[hasher2.hash(w)]++;
        }

        printHashStats("Standard Deterministic Java Hash (hashCode() % m)", standardBuckets, words.size(), bucketCount);
        printHashStats("Universal Randomized Hash #1 [a=" + hasher1.getA() + ", b=" + hasher1.getB() + "]",
                universalBuckets1, words.size(), bucketCount);
        printHashStats("Universal Randomized Hash #2 [a=" + hasher2.getA() + ", b=" + hasher2.getB() + "]",
                universalBuckets2, words.size(), bucketCount);

        System.out.println("\n[Theoretical Insight]");
        System.out.println("In deterministic hashing, an adversary can engineer inputs that all hash to the same bucket (DoS attack).");
        System.out.println("With 2-Universal Randomized Hashing, the hash parameters (a, b) are chosen uniformly at random at runtime.");
        System.out.println("For ANY pair of distinct keys x != y, P(h(x) == h(y)) <= 1/m, guaranteeing worst-case collision resistance.");
    }

    private static void printHashStats(String title, int[] buckets, int totalKeys, int m) {
        System.out.println("\n--------------------------------------------------");
        System.out.println(title);
        System.out.println("--------------------------------------------------");

        double expectedPerBucket = (double) totalKeys / m;
        int maxCollisions = 0;
        int emptyBuckets = 0;
        double varianceSum = 0;

        for (int count : buckets) {
            if (count > maxCollisions) maxCollisions = count;
            if (count == 0) emptyBuckets++;
            varianceSum += Math.pow(count - expectedPerBucket, 2);
        }

        double stdDev = Math.sqrt(varianceSum / m);

        System.out.printf("  Total Keys Hashed     : %d%n", totalKeys);
        System.out.printf("  Buckets Count (m)     : %d%n", m);
        System.out.printf("  Expected per Bucket   : %.2f%n", expectedPerBucket);
        System.out.printf("  Max Keys in a Bucket  : %d%n", maxCollisions);
        System.out.printf("  Empty Buckets         : %d%n", emptyBuckets);
        System.out.printf("  Standard Deviation    : %.2f%n", stdDev);
    }

    private static List<String> loadSampleWords() {
        List<String> words = new ArrayList<>();
        File[] files = DatasetHelper.getTextFiles();

        for (File f : files) {
            String content = DatasetHelper.readFileSafe(f);
            String[] tokens = content.split("\\W+");
            for (String t : tokens) {
                if (t.length() >= 3 && t.length() <= 20) {
                    words.add(t.toLowerCase());
                    if (words.size() >= 3000) return words;
                }
            }
        }

        if (words.isEmpty()) {
            for (int i = 0; i < 1000; i++) {
                words.add("token_" + i);
            }
        }
        return words;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        run(scanner);
        scanner.close();
    }

    public static void run(Scanner scanner) {
        while (true) {
            System.out.println("\n==================================================================");
            System.out.println(" CO6: MILLER-RABIN PRIMALITY & RANDOMIZED HASHING TECHNIQUES     ");
            System.out.println("==================================================================");
            System.out.println(" [1] Miller-Rabin Primality Testing & Benchmarking Suite");
            System.out.println(" [2] Randomized Universal Hashing & Collision Analysis");
            System.out.println(" [3] Run Both Demos");
            System.out.println(" [4] Return to Main Menu");
            System.out.print("Enter choice: ");

            String input = scanner.nextLine().trim();
            if (input.equals("1")) {
                runPrimalityDemo(scanner);
            } else if (input.equals("2")) {
                runRandomizedHashingDemo(scanner);
            } else if (input.equals("3")) {
                runPrimalityBenchmark();
                runRandomizedHashingDemo(scanner);
            } else if (input.equals("4") || input.equalsIgnoreCase("exit")) {
                System.out.println("\nReturning to Main Menu...");
                break;
            } else {
                System.out.println("Invalid choice! Please select 1, 2, 3, or 4.");
            }
        }
    }
}
