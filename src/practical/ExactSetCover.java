package practical;

import java.util.*;

public class ExactSetCover {

    static List<Set<String>> documents;
    static Set<String> requiredTerms;

    static List<Integer> bestSolution;
    static int bestSize = Integer.MAX_VALUE;

    public static void initializeDocuments() {
        documents = new ArrayList<>();

        documents.add(new HashSet<>(Arrays.asList(
                "algorithm", "search", "text", "pattern"
        )));

        documents.add(new HashSet<>(Arrays.asList(
                "graph", "citation", "search", "document"
        )));

        documents.add(new HashSet<>(Arrays.asList(
                "text", "similarity", "pattern", "matching"
        )));

        documents.add(new HashSet<>(Arrays.asList(
                "graph", "flow", "citation", "matching"
        )));

        documents.add(new HashSet<>(Arrays.asList(
                "search", "document", "similarity"
        )));
    }

    public static void solveCover() {
        bestSolution = new ArrayList<>();
        bestSize = Integer.MAX_VALUE;

        findExactCover(0, new HashSet<>(), new ArrayList<>());

        System.out.println("\nRequired Terms:");
        System.out.println(requiredTerms);

        if (bestSize == Integer.MAX_VALUE) {
            System.out.println("\nNo combination of documents can cover all required terms!");
            return;
        }

        System.out.println("\nMinimum Number of Documents Needed: " + bestSize);
        System.out.println("\nSelected Documents:");
        for (int index : bestSolution) {
            System.out.println("  Document " + (index + 1) + " -> " + documents.get(index));
        }

        Set<String> covered = new HashSet<>();
        for (int index : bestSolution) {
            covered.addAll(documents.get(index));
        }
        covered.retainAll(requiredTerms);

        System.out.println("\nCovered Terms:");
        System.out.println(covered);
        System.out.println("\nAll required terms covered: " + covered.containsAll(requiredTerms));
    }

    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        run(s);
        s.close();
    }

    public static void run(Scanner s) {
        initializeDocuments();

        while (true) {
            System.out.println("\n==============================================");
            System.out.println("         EXACT SET COVER (DOCUMENT SELECTION)");
            System.out.println("==============================================");
            System.out.println("Available Documents in Corpus:");
            for (int i = 0; i < documents.size(); i++) {
                System.out.println("Doc " + (i + 1) + ": " + documents.get(i));
            }

            System.out.println("\nOptions:");
            System.out.println("1. Run Default Set Cover Demo (terms: text, search, citation, pattern)");
            System.out.println("2. Enter Custom Target Terms to Cover");
            System.out.println("3. Return to Main Menu");
            System.out.print("Enter choice: ");

            String input = s.nextLine().trim();

            if (input.equals("1")) {
                requiredTerms = new HashSet<>(Arrays.asList(
                        "text", "search", "citation", "pattern"
                ));
                solveCover();
            } else if (input.equals("2")) {
                System.out.print("\nEnter terms separated by space (e.g. text graph similarity): ");
                String termsStr = s.nextLine().trim().toLowerCase();
                if (termsStr.isEmpty()) {
                    System.out.println("No terms entered.");
                    continue;
                }
                String[] parts = termsStr.split("\\s+");
                requiredTerms = new HashSet<>(Arrays.asList(parts));
                solveCover();
            } else if (input.equals("3") || input.equalsIgnoreCase("exit")) {
                System.out.println("\nReturning to Main Menu...");
                break;
            } else {
                System.out.println("Invalid choice. Please enter 1, 2, or 3.");
            }
        }
    }

    static void findExactCover(
            int index,
            Set<String> coveredTerms,
            List<Integer> selectedDocuments) {

        if (coveredTerms.containsAll(requiredTerms)) {
            if (selectedDocuments.size() < bestSize) {
                bestSize = selectedDocuments.size();
                bestSolution = new ArrayList<>(selectedDocuments);
            }
            return;
        }

        if (index >= documents.size()) {
            return;
        }

        if (selectedDocuments.size() >= bestSize) {
            return;
        }

        // Branch 1: Include documents.get(index)
        Set<String> newCoveredTerms = new HashSet<>(coveredTerms);
        newCoveredTerms.addAll(documents.get(index));
        selectedDocuments.add(index);

        findExactCover(index + 1, newCoveredTerms, selectedDocuments);

        // Branch 2: Exclude documents.get(index) (backtrack)
        selectedDocuments.remove(selectedDocuments.size() - 1);

        findExactCover(index + 1, coveredTerms, selectedDocuments);
    }
}