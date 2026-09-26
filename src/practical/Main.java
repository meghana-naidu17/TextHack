package practical;

import java.io.File;
import java.util.Scanner;

/**
 * Main Entry Point for TextHack.
 * Features an interactive switch-case menu to select and execute any text processing,
 * pattern matching, sequence alignment, or graph optimization algorithm.
 */
public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            printBanner();
            printMenu();

            System.out.print("\nEnter your choice [0-10]: ");
            String input = scanner.nextLine().trim();

            int choice;
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("\n[ERROR] Invalid input! Please enter a valid number between 0 and 10.");
                promptEnterToContinue(scanner);
                continue;
            }

            System.out.println();

            switch (choice) {
                case 1:
                    // Knuth-Morris-Pratt (KMP) Pattern Search & Text Metrics
                    KMPapplied.run(scanner);
                    break;

                case 2:
                    // Rabin-Karp Rolling Hash Search & Jaccard Document Similarity
                    RabinKarpSearchDoc.run(scanner);
                    break;

                case 3:
                    // Fuzzy Search & Spell Suggestion using Levenshtein Distance DP
                    FuzzySearch.run(scanner);
                    break;

                case 4:
                    // Needleman-Wunsch Global Sequence & DNA Alignment
                    NeedlemanWunch.run(scanner);
                    break;

                case 5:
                    // Article Storage, Title/Keyword Indexing & Retrieval
                    ArticleStorageQuer.run(scanner);
                    break;

                case 6:
                    // Corpus File Loading & Lexical Frequency Analysis
                    CorpusLoadingArticel.run(scanner);
                    break;

                case 7:
                    // Ford-Fulkerson Citation Network Flow Analysis
                    FordFulkersonCitationFlow.run(scanner);
                    break;

                case 8:
                    // Edmonds-Karp Max Flow & Bipartite Matching (Student-Project Allocation)
                    EdmondsKarpBipartiteMatching.run(scanner);
                    break;

                case 9:
                    // Exact Set Cover Document Selection (Backtracking)
                    ExactSetCover.run(scanner);
                    break;

                case 10:
                    // System Diagnostics and Dataset Health Check
                    runDiagnostics();
                    promptEnterToContinue(scanner);
                    break;

                case 0:
                    // Exit Application
                    running = false;
                    System.out.println("==================================================================");
                    System.out.println("   Thank you for using TextHack! Happy hacking & analyzing.       ");
                    System.out.println("==================================================================");
                    break;

                default:
                    System.out.println("[ERROR] Choice out of range! Please choose an option between 0 and 10.");
                    promptEnterToContinue(scanner);
                    break;
            }
        }

        scanner.close();
    }

    private static void printBanner() {
        System.out.println("\n+==================================================================+");
        System.out.println("|                 TEXTHACK - ALGORITHMS & NLP SUITE                |");
        System.out.println("|       Advanced Text Processing, Matching & Graph Analytics       |");
        System.out.println("+==================================================================+");
    }

    private static void printMenu() {
        System.out.println("\n                     --- MAIN MENU ---");
        System.out.println(" [1]  KMP Pattern Matching & Corpus Frequency Analysis");
        System.out.println(" [2]  Rabin-Karp Substring Search & Jaccard Document Similarity");
        System.out.println(" [3]  Fuzzy Search & Spell Suggestion (Levenshtein Distance)");
        System.out.println(" [4]  Needleman-Wunsch Sequence Alignment (Dynamic Programming)");
        System.out.println(" [5]  Article Storage, Indexing & Query Engine");
        System.out.println(" [6]  Corpus Loading & Vocabulary Statistics");
        System.out.println(" [7]  Citation Flow Analysis (Ford-Fulkerson Max Flow)");
        System.out.println(" [8]  Bipartite Matching & Resource Allocation (Edmonds-Karp)");
        System.out.println(" [9]  Exact Set Cover Document Optimization (Backtracking)");
        System.out.println(" [10] System Diagnostics & Dataset Verification");
        System.out.println(" [0]  Exit TextHack");
    }

    private static void runDiagnostics() {
        System.out.println("==================================================================");
        System.out.println("             TEXTHACK SYSTEM DIAGNOSTICS & DATASET CHECK          ");
        System.out.println("==================================================================");
        File datasetFolder = DatasetHelper.getDatasetFolder();
        System.out.println("Working Directory  : " + new File(".").getAbsolutePath());
        System.out.println("Dataset Location   : " + datasetFolder.getAbsolutePath());
        System.out.println("Dataset Exists     : " + (datasetFolder.exists() && datasetFolder.isDirectory() ? "YES [OK]" : "NO [MISSING]"));

        File[] files = DatasetHelper.getTextFiles();
        System.out.println("Text Files Count   : " + files.length);

        if (files.length > 0) {
            System.out.println("\nDiscovered Dataset Files:");
            long totalBytes = 0;
            for (int i = 0; i < files.length; i++) {
                long size = files[i].length();
                totalBytes += size;
                System.out.printf("  [%d] %-22s Size: %,10d bytes (%.2f KB)%n",
                        (i + 1), files[i].getName(), size, size / 1024.0);
            }
            System.out.printf("\nTotal Dataset Size : %,d bytes (%.2f MB)%n", totalBytes, totalBytes / (1024.0 * 1024.0));
            System.out.println("Status: ALL SYSTEMS OPERATIONAL");
        } else {
            System.out.println("[WARNING] No dataset .txt files detected! Please verify dataset folder.");
        }
        System.out.println("==================================================================");
    }

    private static void promptEnterToContinue(Scanner scanner) {
        System.out.print("\nPress Enter to return to menu...");
        scanner.nextLine();
    }
}
