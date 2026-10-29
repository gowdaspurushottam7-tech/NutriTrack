package com.nutrition.storage;

import com.nutrition.model.Food;
import com.nutrition.model.MealEntry;
import com.nutrition.model.User;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Storage manager handling persistence of application data via Java File I/O.
 * Demonstrates Exception Handling, Stream Management (try-with-resources), and Data Protection.
 */
public class FileManager {
    private static final String DATA_DIR = "data";
    private static final String USER_FILE = DATA_DIR + File.separator + "users.dat";
    private static final String FOODS_FILE = DATA_DIR + File.separator + "foods.dat";
    private static final String MEALS_FILE = DATA_DIR + File.separator + "meals.dat";
    private static final String SETTINGS_FILE = DATA_DIR + File.separator + "settings.dat";

    public FileManager() {
        ensureDataDirectoryExists();
    }

    /**
     * Ensures that the local data directory exists.
     */
    private void ensureDataDirectoryExists() {
        try {
            Path path = Paths.get(DATA_DIR);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
        } catch (IOException e) {
            System.err.println("Warning: Could not create data directory: " + e.getMessage());
        }
    }

    // --- User Persistence ---

    public synchronized void saveUser(User user) {
        if (user == null) return;
        ensureDataDirectoryExists();
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USER_FILE))) {
            oos.writeObject(user);
        } catch (IOException e) {
            System.err.println("Error saving user data: " + e.getMessage());
        }
    }

    public synchronized User loadUser() {
        File file = new File(USER_FILE);
        if (!file.exists() || file.length() == 0) {
            return null; // Return null so application can prompt profile creation or default
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = ois.readObject();
            if (obj instanceof User) {
                return (User) obj;
            }
        } catch (FileNotFoundException e) {
            // Expected on first run
        } catch (Exception e) {
            System.err.println("Notice: Could not load user profile, using safe fallback. Reason: " + e.getMessage());
        }
        return null;
    }

    // --- Custom Foods Persistence ---

    public synchronized void saveCustomFoods(ArrayList<Food> foods) {
        ensureDataDirectoryExists();
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FOODS_FILE))) {
            oos.writeObject(foods != null ? foods : new ArrayList<Food>());
        } catch (IOException e) {
            System.err.println("Error saving custom foods: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public synchronized ArrayList<Food> loadCustomFoods() {
        File file = new File(FOODS_FILE);
        if (!file.exists() || file.length() == 0) {
            return new ArrayList<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = ois.readObject();
            if (obj instanceof ArrayList<?>) {
                return (ArrayList<Food>) obj;
            }
        } catch (FileNotFoundException e) {
            // Normal on first run
        } catch (Exception e) {
            System.err.println("Notice: Could not load custom foods, fallback to empty. Reason: " + e.getMessage());
        }
        return new ArrayList<>();
    }

    // --- Meal Entries Persistence ---

    public synchronized void saveMealEntries(ArrayList<MealEntry> meals) {
        ensureDataDirectoryExists();
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(MEALS_FILE))) {
            oos.writeObject(meals != null ? meals : new ArrayList<MealEntry>());
        } catch (IOException e) {
            System.err.println("Error saving meal entries: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public synchronized ArrayList<MealEntry> loadMealEntries() {
        File file = new File(MEALS_FILE);
        if (!file.exists() || file.length() == 0) {
            return new ArrayList<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = ois.readObject();
            if (obj instanceof ArrayList<?>) {
                return (ArrayList<MealEntry>) obj;
            }
        } catch (FileNotFoundException e) {
            // Normal on first run
        } catch (Exception e) {
            System.err.println("Notice: Could not load meal entries, fallback to empty. Reason: " + e.getMessage());
        }
        return new ArrayList<>();
    }

    // --- Settings Persistence ---

    public synchronized void saveSettings(Map<String, String> settings) {
        ensureDataDirectoryExists();
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SETTINGS_FILE))) {
            oos.writeObject(settings != null ? settings : new HashMap<String, String>());
        } catch (IOException e) {
            System.err.println("Error saving settings: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public synchronized Map<String, String> loadSettings() {
        File file = new File(SETTINGS_FILE);
        if (!file.exists() || file.length() == 0) {
            return new HashMap<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = ois.readObject();
            if (obj instanceof Map<?, ?>) {
                return (Map<String, String>) obj;
            }
        } catch (Exception e) {
            System.err.println("Notice: Settings file not accessible, using default settings.");
        }
        return new HashMap<>();
    }

    // --- Report Exporter Utility ---

    /**
     * Exports text or CSV content to the specified target file.
     * @param content Plain text or CSV data
     * @param targetFile Target destination
     * @return true if successful, false otherwise
     */
    public static boolean exportContentToFile(String content, File targetFile) {
        if (targetFile == null || content == null) return false;
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(targetFile), StandardCharsets.UTF_8))) {
            writer.write(content);
            writer.flush();
            return true;
        } catch (IOException e) {
            System.err.println("Export error: " + e.getMessage());
            return false;
        }
    }
}
