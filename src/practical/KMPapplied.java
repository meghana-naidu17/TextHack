package practical;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

public class KMPapplied {

    public static int[] createLPS(String pattern) {
        int[] lps = new int[pattern.length()];
        int len = 0;
        int i = 1;

        while (i < pattern.length()) {
            if (pattern.charAt(i) == pattern.charAt(len)) {
                len++;
                lps[i] = len;
                i++;
            } else {
                if (len != 0) {
                    len = lps[len - 1];
                } else {
                    lps[i] = 0;
                    i++;
                }
            }
        }
        return lps;
    }

    public static void KMP(String text, String pattern) {
        int[] lps = createLPS(pattern);
        int i = 0;
        int j = 0;
        boolean found = false;

        System.out.println("\n========== SEARCH RESULTS (KMP) ==========");
        while (i < text.length()) {
            if (text.charAt(i) == pattern.charAt(j)) {
                i++;
                j++;
                if (j == pattern.length()) {
                    System.out.println("Keyword found at index: " + (i - j));
                    found = true;
                    j = lps[j - 1];
                }
            } else {
                if (j != 0) {
                    j = lps[j - 1];
                } else {
                    i++;
                }
            }
        }

        if (!found) {
            System.out.println("Keyword not found in text!");
        }
    }

    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        run(s);
        s.close();
    }

    public static void run(Scanner s) {
        File[] files = DatasetHelper.getTextFiles();

        if (files == null || files.length == 0) {
            System.out.println("No dataset files found!");
            return;
        }

        System.out.println("==============================================");
        System.out.println("   KMP PATTERN SEARCH & CORPUS ANALYSIS");
        System.out.println("==============================================");
        System.out.println("Total Files Loaded: " + files.length);
        System.out.println("\nAvailable Files:");

        for (int i = 0; i < files.length; i++) {
            System.out.println((i + 1) + ". " + files[i].getName());
        }

        System.out.print("\nEnter your choice (1-" + files.length + ", or 0 to return): ");
        String fileChoiceStr = s.nextLine().trim();
        int choice;
        try {
            choice = Integer.parseInt(fileChoiceStr);
        } catch (NumberFormatException e) {
            System.out.println("Invalid Choice!");
            return;
        }

        if (choice == 0) {
            return;
        }

        if (choice < 1 || choice > files.length) {
            System.out.println("Invalid Choice!");
            return;
        }

        File selectedFile = files[choice - 1];
        String content = "";

        try {
            content = Files.readString(selectedFile.toPath());
            System.out.println("\nFile Loaded Successfully: " + selectedFile.getName());
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
            return;
        }

        boolean exit = false;
        while (!exit) {
            System.out.println("\n================================");
            System.out.println("      CORPUS & KMP MENU");
            System.out.println("================================");
            System.out.println("1. Number of Characters");
            System.out.println("2. Number of Words");
            System.out.println("3. Number of Unique Words");
            System.out.println("4. Frequency of Each Word");
            System.out.println("5. Most Frequent Word");
            System.out.println("6. Least Frequent Word");
            System.out.println("7. Display File Content (Preview)");
            System.out.println("8. Search Keyword using KMP");
            System.out.println("9. Return to Main Menu");
            System.out.print("Enter your choice: ");

            String optionStr = s.nextLine().trim();
            int option;
            try {
                option = Integer.parseInt(optionStr);
            } catch (NumberFormatException e) {
                System.out.println("Invalid Choice! Please enter a number.");
                continue;
            }

            switch (option) {
                case 1:
                    System.out.println("\nTotal Characters: " + content.length());
                    break;

                case 2:
                    String[] words = content.trim().isEmpty() ? new String[0] : content.trim().split("\\s+");
                    System.out.println("\nTotal Words: " + words.length);
                    break;

                case 3:
                    HashSet<String> unique = new HashSet<>();
                    for (String word : content.toLowerCase().split("\\W+")) {
                        if (!word.isEmpty()) {
                            unique.add(word);
                        }
                    }
                    System.out.println("\nUnique Words: " + unique.size());
                    break;

                case 4:
                    HashMap<String, Integer> frequency = new HashMap<>();
                    for (String word : content.toLowerCase().split("\\W+")) {
                        if (word.isEmpty()) continue;
                        frequency.put(word, frequency.getOrDefault(word, 0) + 1);
                    }
                    System.out.println("\n--- Word Frequency Sample (Top 30) ---");
                    int count = 0;
                    for (String key : frequency.keySet()) {
                        System.out.println(key + " : " + frequency.get(key));
                        count++;
                        if (count >= 30) {
                            System.out.println("... (" + (frequency.size() - 30) + " more words omitted for brevity)");
                            break;
                        }
                    }
                    break;

                case 5:
                    HashMap<String, Integer> maxMap = new HashMap<>();
                    for (String word : content.toLowerCase().split("\\W+")) {
                        if (word.isEmpty()) continue;
                        maxMap.put(word, maxMap.getOrDefault(word, 0) + 1);
                    }
                    String maxWord = "";
                    int max = 0;
                    for (String key : maxMap.keySet()) {
                        if (maxMap.get(key) > max) {
                            max = maxMap.get(key);
                            maxWord = key;
                        }
                    }
                    System.out.println("\nMost Frequent Word: \"" + maxWord + "\" (Frequency: " + max + ")");
                    break;

                case 6:
                    HashMap<String, Integer> minMap = new HashMap<>();
                    for (String word : content.toLowerCase().split("\\W+")) {
                        if (word.isEmpty()) continue;
                        minMap.put(word, minMap.getOrDefault(word, 0) + 1);
                    }
                    String minWord = "";
                    int min = Integer.MAX_VALUE;
                    for (String key : minMap.keySet()) {
                        if (minMap.get(key) < min) {
                            min = minMap.get(key);
                            minWord = key;
                        }
                    }
                    System.out.println("\nLeast Frequent Word: \"" + minWord + "\" (Frequency: " + min + ")");
                    break;

                case 7:
                    System.out.println("\n========== FILE CONTENT (PREVIEW) ==========");
                    if (content.length() > 1000) {
                        System.out.println(content.substring(0, 1000) + "\n... [Preview truncated for display]");
                    } else {
                        System.out.println(content);
                    }
                    break;

                case 8:
                    System.out.print("\nEnter keyword to search with KMP: ");
                    String keyword = s.nextLine().trim();
                    if (keyword.isEmpty()) {
                        System.out.println("Keyword cannot be empty!");
                        break;
                    }
                    KMP(content.toLowerCase(), keyword.toLowerCase());
                    break;

                case 9:
                    exit = true;
                    System.out.println("\nReturning to Main Menu...");
                    break;

                default:
                    System.out.println("Invalid Choice!");
            }
        }
    }
}