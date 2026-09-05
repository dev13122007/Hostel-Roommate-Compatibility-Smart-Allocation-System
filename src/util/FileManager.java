package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Centralized utility class for local file-based data persistence.
 * Handles reading, writing, appending, and automated file initialization.
 */
public class FileManager {
    private static final String DATA_DIR = "data";

    /**
     * Ensures the data directory and the specified file exist on disk.
     */
    public static void ensureFileExists(String filename) {
        try {
            File dir = new File(DATA_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(dir, filename);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            System.err.println("Error initializing file '" + filename + "': " + e.getMessage());
        }
    }

    /**
     * Reads all non-empty lines from the given file under data directory.
     */
    public static List<String> readLines(String filename) {
        List<String> lines = new ArrayList<>();
        ensureFileExists(filename);
        File file = new File(DATA_DIR, filename);

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty() && !line.startsWith("#")) {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file '" + filename + "': " + e.getMessage());
        }
        return lines;
    }

    /**
     * Overwrites the file with the given list of string lines.
     */
    public static void writeLines(String filename, List<String> lines) {
        ensureFileExists(filename);
        File file = new File(DATA_DIR, filename);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, false))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing to file '" + filename + "': " + e.getMessage());
        }
    }

    /**
     * Appends a single line to the specified file.
     */
    public static void appendLine(String filename, String line) {
        ensureFileExists(filename);
        File file = new File(DATA_DIR, filename);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(line);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error appending to file '" + filename + "': " + e.getMessage());
        }
    }

    /**
     * Checks if the file contains any records.
     */
    public static boolean hasData(String filename) {
        List<String> lines = readLines(filename);
        return !lines.isEmpty();
    }
}
