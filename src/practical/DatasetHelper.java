package practical;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Utility helper to automatically detect and load datasets from multiple possible paths
 * (e.g., project root, src folder, or parent directory).
 */
public class DatasetHelper {

    private static final String[] CANDIDATE_PATHS = {
        "dataset",
        "src/dataset",
        "../dataset",
        "../../dataset"
    };

    /**
     * Resolves the dataset directory by checking candidate locations.
     * @return File object pointing to existing dataset directory, or a fallback File("dataset").
     */
    public static File getDatasetFolder() {
        for (String path : CANDIDATE_PATHS) {
            File folder = new File(path);
            if (folder.exists() && folder.isDirectory()) {
                return folder;
            }
        }
        return new File("dataset");
    }

    /**
     * Retrieves all .txt files from the resolved dataset directory.
     */
    public static File[] getTextFiles() {
        File folder = getDatasetFolder();
        if (!folder.exists() || !folder.isDirectory()) {
            return new File[0];
        }
        File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".txt"));
        return files != null ? files : new File[0];
    }

    /**
     * Resolves a specific file inside the dataset folder.
     */
    public static File resolveFile(String fileName) {
        File folder = getDatasetFolder();
        File file = new File(folder, fileName);
        if (file.exists()) {
            return file;
        }
        // Check without folder in case path is absolute or already relative
        File direct = new File(fileName);
        if (direct.exists()) {
            return direct;
        }
        return file;
    }

    /**
     * Reads entire content of a file as a String safely.
     */
    public static String readFileSafe(File file) {
        try {
            return Files.readString(file.toPath());
        } catch (IOException e) {
            System.err.println("Warning: Unable to read file " + file.getName() + ": " + e.getMessage());
            return "";
        }
    }

    /**
     * Returns a human-friendly title for a dataset file.
     */
    public static String getCleanTitle(File file, String firstLine) {
        String name = file.getName();
        if (firstLine != null && !firstLine.trim().isEmpty()) {
            String lower = firstLine.toLowerCase().trim();
            boolean isHeader = lower.contains("name") || lower.contains("title") || lower.contains("id")
                    || lower.contains("type") || lower.contains("class") || lower.contains("sequence");
            if (!isHeader && firstLine.trim().length() <= 80) {
                return firstLine.trim();
            }
        }

        String base = name.replaceAll("(?i)txt", "").replace(".", "").trim();
        if (base.equalsIgnoreCase("anime")) return "Anime Database (" + name + ")";
        if (base.equalsIgnoreCase("characters")) return "Demon Slayer Characters (" + name + ")";
        if (base.equalsIgnoreCase("human")) return "Human DNA Sequences (" + name + ")";
        if (base.equalsIgnoreCase("moviedata")) return "Indian Cinema Box Office (" + name + ")";
        if (base.equalsIgnoreCase("pokemon")) return "Pokemon Master Collection (" + name + ")";
        return (base.isEmpty() ? name : Character.toUpperCase(base.charAt(0)) + base.substring(1)) + " (" + name + ")";
    }
}
