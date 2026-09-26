package practical;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Scanner;

public class ArticleStorageQuer {

    static final int MAX_ARTICLES = 100;

    static String[] articleTitle = new String[MAX_ARTICLES];
    static String[] articleContent = new String[MAX_ARTICLES];
    static String[] articleFile = new String[MAX_ARTICLES];

    static int articleCount = 0;

    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        run(s);
        s.close();
    }

    public static void run(Scanner s) {
        File[] files = DatasetHelper.getTextFiles();

        if (files == null || files.length == 0) {
            System.out.println("No article files found in dataset!");
            return;
        }

        System.out.println("==============================================");
        System.out.println("       ARTICLE STORAGE AND QUERY SYSTEM");
        System.out.println("==============================================");
        System.out.println("Total Number of Articles Found : " + files.length);

        articleCount = 0;
        for (int i = 0; i < files.length && articleCount < MAX_ARTICLES; i++) {
            try {
                String content = Files.readString(files[i].toPath());
                articleFile[articleCount] = files[i].getName();

                String[] lines = content.split("\\R");
                String firstLine = (lines.length > 0) ? lines[0] : "";
                articleTitle[articleCount] = DatasetHelper.getCleanTitle(files[i], firstLine);

                articleContent[articleCount] = content;
                articleCount++;
            } catch (IOException e) {
                System.out.println("Error reading : " + files[i].getName());
            }
        }

        System.out.println("Articles Loaded Successfully : " + articleCount);

        boolean exit = false;
        while (!exit) {
            System.out.println("\n================================");
            System.out.println("          ARTICLE MENU");
            System.out.println("================================");
            System.out.println("1. Display All Articles");
            System.out.println("2. Search Article by Title");
            System.out.println("3. Search Article by Keyword");
            System.out.println("4. Display Article Content (Preview)");
            System.out.println("5. Show Number of Articles");
            System.out.println("6. Return to Main Menu");
            System.out.print("\nEnter your choice: ");

            String input = s.nextLine().trim();
            int choice;
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid Choice! Please enter a number.");
                continue;
            }

            switch (choice) {
                case 1:
                    System.out.println("\n========== AVAILABLE ARTICLES ==========");
                    for (int i = 0; i < articleCount; i++) {
                        System.out.println((i + 1) + ". " + articleTitle[i]);
                        System.out.println("   File : " + articleFile[i]);
                    }
                    break;

                case 2:
                    System.out.print("\nEnter title to search: ");
                    String titleQuery = s.nextLine().toLowerCase().trim();
                    if (titleQuery.isEmpty()) {
                        System.out.println("Title query cannot be empty.");
                        break;
                    }

                    boolean titleFound = false;
                    System.out.println("\n========== SEARCH RESULTS ==========");
                    for (int i = 0; i < articleCount; i++) {
                        if (articleTitle[i].toLowerCase().contains(titleQuery)) {
                            System.out.println("Article Found : " + articleTitle[i]);
                            System.out.println("File          : " + articleFile[i]);
                            titleFound = true;
                        }
                    }

                    if (!titleFound) {
                        System.out.println("No article found matching: \"" + titleQuery + "\"");
                    }
                    break;

                case 3:
                    System.out.print("\nEnter keyword: ");
                    String keyword = s.nextLine().toLowerCase().trim();
                    if (keyword.isEmpty()) {
                        System.out.println("Keyword cannot be empty.");
                        break;
                    }

                    boolean keywordFound = false;
                    System.out.println("\n========== ARTICLES CONTAINING KEYWORD ==========");
                    for (int i = 0; i < articleCount; i++) {
                        String content = articleContent[i].toLowerCase();
                        if (content.contains(keyword)) {
                            System.out.println("\nArticle : " + articleTitle[i]);
                            System.out.println("File    : " + articleFile[i]);
                            keywordFound = true;
                        }
                    }

                    if (!keywordFound) {
                        System.out.println("No articles contain this keyword: \"" + keyword + "\"");
                    }
                    break;

                case 4:
                    System.out.print("\nEnter article number (1-" + articleCount + "): ");
                    String numStr = s.nextLine().trim();
                    int number;
                    try {
                        number = Integer.parseInt(numStr);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid number format!");
                        break;
                    }

                    if (number >= 1 && number <= articleCount) {
                        int index = number - 1;
                        System.out.println("\n========== ARTICLE PREVIEW ==========");
                        System.out.println("Title : " + articleTitle[index]);
                        System.out.println("File  : " + articleFile[index]);
                        System.out.println("\n---------- CONTENT ----------");
                        String text = articleContent[index];
                        if (text.length() > 1000) {
                            System.out.println(text.substring(0, 1000) + "\n... [Preview truncated for display]");
                        } else {
                            System.out.println(text);
                        }
                    } else {
                        System.out.println("Invalid article number!");
                    }
                    break;

                case 5:
                    System.out.println("\nTotal Articles Stored : " + articleCount);
                    break;

                case 6:
                    exit = true;
                    System.out.println("\nReturning to Main Menu...");
                    break;

                default:
                    System.out.println("Invalid Choice!");
            }
        }
    }
}