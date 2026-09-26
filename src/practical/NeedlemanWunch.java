package practical;

import java.util.Scanner;

public class NeedlemanWunch {

    static final int MATCH = 1;
    static final int MISMATCH = -1;
    static final int GAP = -2;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        run(sc);
        sc.close();
    }

    public static void run(Scanner sc) {
        System.out.println("==============================================");
        System.out.println("       NEEDLEMAN-WUNSCH SEQUENCE ALIGNMENT");
        System.out.println("   (Global Alignment with Dynamic Programming)");
        System.out.println("==============================================");

        while (true) {
            System.out.println("\nOptions:");
            System.out.println("1. Align Custom Sequences");
            System.out.println("2. Run Sample DNA Sequence Demo");
            System.out.println("3. Return to Main Menu");
            System.out.print("Enter choice: ");

            String input = sc.nextLine().trim();
            if (input.equals("1")) {
                System.out.print("\nEnter Sequence 1: ");
                String seq1 = sc.nextLine().trim().toUpperCase();
                System.out.print("Enter Sequence 2: ");
                String seq2 = sc.nextLine().trim().toUpperCase();

                if (seq1.isEmpty() || seq2.isEmpty()) {
                    System.out.println("Sequences cannot be empty!");
                    continue;
                }
                needlemanWunsch(seq1, seq2);
            } else if (input.equals("2")) {
                String dna1 = "ATGCCCCAACTAAATAC";
                String dna2 = "ATGAACGAAAATCTGTTC";
                System.out.println("\nRunning Demo Alignment with DNA fragments:");
                System.out.println("Seq 1: " + dna1);
                System.out.println("Seq 2: " + dna2);
                needlemanWunsch(dna1, dna2);
            } else if (input.equals("3") || input.equalsIgnoreCase("exit")) {
                System.out.println("\nReturning to Main Menu...");
                break;
            } else {
                System.out.println("Invalid choice. Enter 1, 2, or 3.");
            }
        }
    }

    public static void needlemanWunsch(String seq1, String seq2) {
        int m = seq1.length();
        int n = seq2.length();

        int[][] dp = new int[m + 1][n + 1];

        // Initialize first row
        for (int j = 0; j <= n; j++) {
            dp[0][j] = j * GAP;
        }

        // Initialize first column
        for (int i = 0; i <= m; i++) {
            dp[i][0] = i * GAP;
        }

        // Fill the matrix
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                int score = (seq1.charAt(i - 1) == seq2.charAt(j - 1)) ? MATCH : MISMATCH;
                int diagonal = dp[i - 1][j - 1] + score;
                int up = dp[i - 1][j] + GAP;
                int left = dp[i][j - 1] + GAP;

                dp[i][j] = Math.max(diagonal, Math.max(up, left));
            }
        }

        // Traceback
        StringBuilder aligned1 = new StringBuilder();
        StringBuilder aligned2 = new StringBuilder();

        int i = m;
        int j = n;

        while (i > 0 || j > 0) {
            // Diagonal
            if (i > 0 && j > 0) {
                int score = (seq1.charAt(i - 1) == seq2.charAt(j - 1)) ? MATCH : MISMATCH;
                if (dp[i][j] == dp[i - 1][j - 1] + score) {
                    aligned1.append(seq1.charAt(i - 1));
                    aligned2.append(seq2.charAt(j - 1));
                    i--;
                    j--;
                    continue;
                }
            }

            // Up
            if (i > 0 && dp[i][j] == dp[i - 1][j] + GAP) {
                aligned1.append(seq1.charAt(i - 1));
                aligned2.append('-');
                i--;
            }
            // Left
            else if (j > 0) {
                aligned1.append('-');
                aligned2.append(seq2.charAt(j - 1));
                j--;
            }
        }

        aligned1.reverse();
        aligned2.reverse();

        // Display result
        System.out.println("\n========== NEEDLEMAN-WUNSCH RESULT ==========");
        System.out.println("Original Seq 1 : " + seq1);
        System.out.println("Original Seq 2 : " + seq2);
        System.out.println("\nAligned Seq 1  : " + aligned1);
        System.out.println("Aligned Seq 2  : " + aligned2);
        System.out.println("\nAlignment Score: " + dp[m][n]);

        // Display DP Matrix (if reasonably sized)
        if (m <= 30 && n <= 30) {
            System.out.println("\n========== DP SCORING MATRIX ==========");
            System.out.print("       - ");
            for (int k = 0; k < n; k++) {
                System.out.printf("%4c", seq2.charAt(k));
            }
            System.out.println();

            for (int x = 0; x <= m; x++) {
                if (x == 0) {
                    System.out.print("  - ");
                } else {
                    System.out.printf("%3c ", seq1.charAt(x - 1));
                }

                for (int y = 0; y <= n; y++) {
                    System.out.printf("%4d", dp[x][y]);
                }
                System.out.println();
            }
        } else {
            System.out.println("(DP Matrix omitted because sequences are long > 30 chars)");
        }
    }
}