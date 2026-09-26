package practical;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class RabinKarpSearchDoc {

    static final int MAX_ARTICLES = 100;
    static final int BASE = 256;
    static final int PRIME = 101;

    static String[] articleTitle = new String[MAX_ARTICLES];
    static String[] articleContent = new String[MAX_ARTICLES];
    static String[] articleFile = new String[MAX_ARTICLES];

    static int articleCount = 0;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        run(scanner);
        scanner.close();
    }

    public static void run(Scanner scanner) {
        System.out.println("==============================================");
        System.out.println("              TEXTHACK");
        System.out.println("==============================================");
        System.out.println(" RABIN-KARP SEARCH & DOCUMENT SIMILARITY");
        System.out.println("==============================================");

        if (articleCount == 0) {
            loadDataset();
        }

        if (articleCount == 0) {
            System.out.println("\nNo articles were loaded.");
            return;
        }

        System.out.println("\nArticles Loaded Successfully : " + articleCount);

        while (true) {
            System.out.println("\n==============================================");
            System.out.println("        RABIN-KARP & SIMILARITY MENU");
            System.out.println("==============================================");
            System.out.println("1. Rabin-Karp Keyword Search");
            System.out.println("2. Document Similarity Analysis (Jaccard)");
            System.out.println("3. Return to Main Menu");
            System.out.print("\nEnter choice: ");

            String input = scanner.nextLine();
            int choice;

            try {
                choice = Integer.parseInt(input.trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid choice. Enter 1, 2 or 3.");
                continue;
            }

            if (choice == 1) {
                rabinKarpMenu(scanner);
            } else if (choice == 2) {
                similarityMenu(scanner);
            } else if (choice == 3) {
                System.out.println("\nReturning to TextHack Main Menu...");
                break;
            } else {
                System.out.println("Invalid choice.");
            }
        }
    }

    public static void loadDataset() {
        File[] files = DatasetHelper.getTextFiles();

        System.out.println("\nDataset Location: " + DatasetHelper.getDatasetFolder().getAbsolutePath());

        if (files == null || files.length == 0) {
            System.out.println("ERROR: No .txt files found in dataset directory.");
            return;
        }

        System.out.println("Total Articles Found : " + files.length);

        articleCount = 0;
        for (File file : files) {
            if (articleCount >= MAX_ARTICLES) {
                break;
            }

            try {
                String content = Files.readString(file.toPath());
                articleFile[articleCount] = file.getName();

                String[] lines = content.split("\\R");
                String firstLine = (lines.length > 0) ? lines[0] : "";
                articleTitle[articleCount] = DatasetHelper.getCleanTitle(file, firstLine);

                articleContent[articleCount] = content;
                articleCount++;
            } catch (IOException e) {
                System.out.println("Could not read: " + file.getName());
            }
        }
    }

    public static void rabinKarpMenu(Scanner scanner) {
        System.out.println("\n==============================================");
        System.out.println("             RABIN-KARP SEARCH");
        System.out.println("==============================================");
        System.out.print("Enter keyword: ");

        String keyword = scanner.nextLine().trim();

        if (keyword.isEmpty()) {
            System.out.println("Keyword cannot be empty.");
            return;
        }

        boolean found = false;

        for (int i = 0; i < articleCount; i++) {
            int position = rabinKarpSearch(articleContent[i], keyword);

            if (position != -1) {
                found = true;
                System.out.println("\n----------------------------------------------");
                System.out.println("Article : " + articleTitle[i]);
                System.out.println("File    : " + articleFile[i]);
                System.out.println("Position: " + position);
            }
        }

        if (!found) {
            System.out.println("\nNo article contains the keyword: \"" + keyword + "\"");
        }
    }

    public static int rabinKarpSearch(String text, String pattern) {
        if (text == null || pattern == null) {
            return -1;
        }

        text = text.toLowerCase();
        pattern = pattern.toLowerCase();

        int n = text.length();
        int m = pattern.length();

        if (m == 0) {
            return 0;
        }

        if (m > n) {
            return -1;
        }

        int patternHash = 0;
        int textHash = 0;
        int highestPower = 1;

        for (int i = 0; i < m - 1; i++) {
            highestPower = (highestPower * BASE) % PRIME;
        }

        for (int i = 0; i < m; i++) {
            patternHash = (BASE * patternHash + pattern.charAt(i)) % PRIME;
            textHash = (BASE * textHash + text.charAt(i)) % PRIME;
        }

        for (int i = 0; i <= n - m; i++) {
            if (patternHash == textHash) {
                boolean match = true;
                for (int j = 0; j < m; j++) {
                    if (text.charAt(i + j) != pattern.charAt(j)) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    return i;
                }
            }

            if (i < n - m) {
                textHash = (BASE * (textHash - text.charAt(i) * highestPower) + text.charAt(i + m)) % PRIME;
                if (textHash < 0) {
                    textHash = (textHash % PRIME + PRIME) % PRIME;
                }
            }
        }

        return -1;
    }

    public static void similarityMenu(Scanner scanner) {
        System.out.println("\n==============================================");
        System.out.println("          DOCUMENT SIMILARITY ANALYSIS");
        System.out.println("==============================================");
        System.out.println("\nAvailable Articles:");

        for (int i = 0; i < articleCount; i++) {
            System.out.println((i + 1) + ". " + articleTitle[i] + " [" + articleFile[i] + "]");
        }

        int first = getArticleNumber(scanner, "Enter first article number: ");
        if (first == -1) return;

        int second = getArticleNumber(scanner, "Enter second article number: ");
        if (second == -1) return;

        if (first == second) {
            System.out.println("Please select two different articles.");
            return;
        }

        int index1 = first - 1;
        int index2 = second - 1;

        double similarity = calculateJaccardSimilarity(articleContent[index1], articleContent[index2]);

        System.out.println("\n==============================================");
        System.out.println("             SIMILARITY RESULT");
        System.out.println("==============================================");
        System.out.println("Article 1 : " + articleTitle[index1] + " (" + articleFile[index1] + ")");
        System.out.println("Article 2 : " + articleTitle[index2] + " (" + articleFile[index2] + ")");
        System.out.printf("Jaccard Similarity: %.2f%%%n", similarity);
        System.out.println("==============================================");
    }

    public static int getArticleNumber(Scanner scanner, String message) {
        System.out.print(message);
        String input = scanner.nextLine();

        try {
            int number = Integer.parseInt(input.trim());
            if (number < 1 || number > articleCount) {
                System.out.println("Invalid article number.");
                return -1;
            }
            return number;
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
            return -1;
        }
    }

    public static double calculateJaccardSimilarity(String text1, String text2) {
        Set<String> words1 = getUniqueWords(text1);
        Set<String> words2 = getUniqueWords(text2);

        Set<String> commonWords = new HashSet<>(words1);
        commonWords.retainAll(words2);

        Set<String> allWords = new HashSet<>(words1);
        allWords.addAll(words2);

        if (allWords.isEmpty()) {
            return 0.0;
        }

        return ((double) commonWords.size() / allWords.size()) * 100.0;
    }

    public static Set<String> getUniqueWords(String text) {
        Set<String> words = new HashSet<>();
        if (text == null || text.isEmpty()) {
            return words;
        }

        text = text.toLowerCase().replaceAll("[^a-z0-9\\s]", " ");
        String[] tokens = text.split("\\s+");

        for (String word : tokens) {
            if (!word.isEmpty()) {
                words.add(word);
            }
        }

        return words;
    }
}