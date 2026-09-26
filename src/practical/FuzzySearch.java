package practical;

import java.io.*;
import java.util.*;

public class FuzzySearch {

    private final List<String> dataset = new ArrayList<>();
    private static final int MAX_SUGGESTIONS = 5;

    public FuzzySearch() {
        loadDataset();
    }

    private void loadDataset() {
        File folder = DatasetHelper.getDatasetFolder();
        File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".txt"));

        if (files == null || files.length == 0) {
            System.out.println("Warning: No dataset files found in " + folder.getAbsolutePath());
            return;
        }

        System.out.println("\nLoading dataset from: " + folder.getAbsolutePath());

        for (File file : files) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                int count = 0;
                while ((line = br.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty() || isHeader(line)) {
                        continue;
                    }

                    String name = extractName(line);
                    if (!name.isEmpty()) {
                        dataset.add(name);
                        count++;
                    }
                }
                System.out.println("Loaded " + count + " entries from " + file.getName());
            } catch (IOException e) {
                System.out.println("Error reading file: " + file.getName() + " (" + e.getMessage() + ")");
            }
        }

        LinkedHashSet<String> unique = new LinkedHashSet<>();
        for (String item : dataset) {
            if (item != null && !item.trim().isEmpty()) {
                unique.add(item.trim());
            }
        }

        dataset.clear();
        dataset.addAll(unique);

        System.out.println("\n=================================");
        System.out.println("Total unique searchable entries: " + dataset.size());
        System.out.println("=================================");
    }

    private boolean isHeader(String line) {
        String value = line.trim().toLowerCase();
        return value.startsWith("name")
                || value.startsWith("title")
                || value.startsWith("character")
                || value.startsWith("movie")
                || value.startsWith("pokemon")
                || value.startsWith("anime")
                || value.startsWith("id")
                || value.startsWith("anime_id")
                || value.startsWith("character no.")
                || value.startsWith("sequence");
    }

    private String extractName(String line) {
        if (line == null) return "";
        line = line.trim();
        if (line.isEmpty()) return "";

        // Skip massive raw DNA sequences for named entity fuzzy search
        if (line.length() > 200 || line.matches("^[ATCGN\\s\\d]+$")) {
            return "";
        }

        String[] parts = line.split("[|,;\\t]", -1);
        if (parts == null || parts.length == 0) return "";

        for (int i = 0; i < parts.length; i++) {
            String part = parts[i] != null ? removeQuotes(parts[i].trim()) : "";
            if (part.isEmpty()) continue;

            // If the first column is numeric (like anime_id or character no.), prefer the next column
            if (i == 0 && part.matches("^\\d+$") && parts.length > 1) {
                continue;
            }
            return part;
        }
        return "";
    }

    private String removeQuotes(String text) {
        if (text == null) return "";
        text = text.trim();
        if (text.length() >= 2) {
            if ((text.startsWith("\"") && text.endsWith("\""))
                    || (text.startsWith("'") && text.endsWith("'"))) {
                text = text.substring(1, text.length() - 1);
            }
        }
        return text.trim();
    }

    private String normalize(String text) {
        if (text == null) return "";
        return text.toLowerCase().trim().replaceAll("\\s+", " ");
    }

    public int levenshteinDistance(String a, String b) {
        a = normalize(a);
        b = normalize(b);

        if (a.equals(b)) return 0;

        int[][] dp = new int[a.length() + 1][b.length() + 1];

        for (int i = 0; i <= a.length(); i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= b.length(); j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i <= a.length(); i++) {
            for (int j = 1; j <= b.length(); j++) {
                int cost = (a.charAt(i - 1) == b.charAt(j - 1)) ? 0 : 1;
                int insertion = dp[i][j - 1] + 1;
                int deletion = dp[i - 1][j] + 1;
                int replacement = dp[i - 1][j - 1] + cost;

                dp[i][j] = Math.min(Math.min(insertion, deletion), replacement);
            }
        }
        return dp[a.length()][b.length()];
    }

    private double similarity(String query, String item) {
        String normalizedQuery = normalize(query);
        String normalizedItem = normalize(item);

        int distance = levenshteinDistance(normalizedQuery, normalizedItem);
        int maxLength = Math.max(normalizedQuery.length(), normalizedItem.length());

        if (maxLength == 0) return 1.0;
        return 1.0 - ((double) distance / maxLength);
    }

    public List<String> search(String query) {
        List<Result> results = new ArrayList<>();
        if (query == null) return new ArrayList<>();

        query = query.trim();
        if (query.isEmpty()) return new ArrayList<>();

        String normalizedQuery = normalize(query);

        // Check for exact matches
        for (String item : dataset) {
            if (item != null && normalize(item).equals(normalizedQuery)) {
                List<String> exact = new ArrayList<>();
                exact.add(item);
                return exact;
            }
        }

        int maxDistance;
        if (normalizedQuery.length() <= 2) {
            maxDistance = 0;
        } else if (normalizedQuery.length() <= 4) {
            maxDistance = 1;
        } else if (normalizedQuery.length() <= 7) {
            maxDistance = 2;
        } else {
            maxDistance = 3;
        }

        for (String item : dataset) {
            if (item == null || item.trim().isEmpty()) continue;

            String normalizedItem = normalize(item);
            int distance = levenshteinDistance(normalizedQuery, normalizedItem);
            double score = similarity(normalizedQuery, normalizedItem);

            if (distance <= maxDistance) {
                results.add(new Result(item, distance, score));
            }
        }

        results.sort(
                Comparator.comparingInt((Result r) -> r.distance)
                        .thenComparing((Result r) -> -r.score)
        );

        List<String> suggestions = new ArrayList<>();
        for (int i = 0; i < Math.min(MAX_SUGGESTIONS, results.size()); i++) {
            suggestions.add(results.get(i).text);
        }

        return suggestions;
    }

    private static class Result {
        String text;
        int distance;
        double score;

        Result(String text, int distance, double score) {
            this.text = text;
            this.distance = distance;
            this.score = score;
        }
    }

    public void displayDatasetSample() {
        System.out.println("\nSample searchable entries:");
        System.out.println("---------------------------------");
        int count = Math.min(10, dataset.size());
        for (int i = 0; i < count; i++) {
            System.out.println((i + 1) + ". " + dataset.get(i));
        }
        System.out.println("---------------------------------");
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        run(scanner);
        scanner.close();
    }

    public static void run(Scanner scanner) {
        FuzzySearch fuzzySearch = new FuzzySearch();
        fuzzySearch.displayDatasetSample();

        System.out.println("=================================");
        System.out.println("     TEXTHACK FUZZY SEARCH");
        System.out.println(" (Levenshtein Distance & Suggest)");
        System.out.println("=================================");

        while (true) {
            System.out.print("\nEnter search query (or 'exit' to return): ");
            String query;

            try {
                query = scanner.nextLine();
            } catch (NoSuchElementException e) {
                break;
            }

            if (query == null || query.equalsIgnoreCase("exit")) {
                break;
            }

            if (query.trim().isEmpty()) {
                System.out.println("Please enter a non-empty search query.");
                continue;
            }

            List<String> suggestions = fuzzySearch.search(query);

            if (suggestions.size() == 1 && suggestions.get(0).equalsIgnoreCase(query.trim())) {
                System.out.println("\n[Exact match found]: -> " + suggestions.get(0));
                continue;
            }

            if (suggestions.isEmpty()) {
                System.out.println("\nNo similar entries found within edit distance threshold.");
            } else {
                System.out.println("\nDid you mean:");
                for (int i = 0; i < suggestions.size(); i++) {
                    System.out.println("  " + (i + 1) + ". " + suggestions.get(i));
                }
            }
        }

        System.out.println("\nReturning from Fuzzy Search to Main Menu...");
    }
}