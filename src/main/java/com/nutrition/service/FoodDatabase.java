package com.nutrition.service;

import com.nutrition.model.Food;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Manages the catalog of food items, including standard built-in foods
 * and user-created custom foods.
 * Demonstrates Collections (ArrayList), Encapsulation, and Error Validation.
 */
public class FoodDatabase implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String NUTRITION_DISCLAIMER =
            "Note: Nutritional values are standard approximate estimates per 100g and may vary based on variety and cooking methods.";

    private final ArrayList<Food> builtInFoods;
    private final ArrayList<Food> customFoods;

    public FoodDatabase() {
        this.builtInFoods = new ArrayList<>();
        this.customFoods = new ArrayList<>();
        initializeBuiltInFoods();
    }

    /**
     * Initializes the built-in food catalog with over 30 authentic food items.
     */
    private void initializeBuiltInFoods() {
        // Grains & Breads
        builtInFoods.add(new Food("F001", "Rice (Cooked White)", "Grains", 130.0, 2.7, 28.2, 0.3));
        builtInFoods.add(new Food("F002", "Brown Rice (Cooked)", "Grains", 112.0, 2.6, 23.5, 0.9));
        builtInFoods.add(new Food("F003", "Chapati (Whole Wheat)", "Breads", 297.0, 9.6, 56.0, 3.8));
        builtInFoods.add(new Food("F004", "Roti (Tandoori/Plain)", "Breads", 260.0, 8.5, 51.0, 2.5));
        builtInFoods.add(new Food("F005", "Idli (Steamed)", "South Indian", 132.0, 4.0, 28.0, 0.4));
        builtInFoods.add(new Food("F006", "Dosa (Plain)", "South Indian", 168.0, 3.9, 29.0, 4.2));
        builtInFoods.add(new Food("F007", "Oats (Rolled)", "Grains", 389.0, 16.9, 66.3, 6.9));
        builtInFoods.add(new Food("F008", "Bread (Whole Wheat)", "Breads", 247.0, 13.0, 41.0, 3.4));

        // Protein & Meat / Poultry / Seafood
        builtInFoods.add(new Food("F009", "Chicken Breast (Cooked)", "Meat/Poultry", 165.0, 31.0, 0.0, 3.6));
        builtInFoods.add(new Food("F010", "Chicken Curry (Indian Style)", "Meat/Poultry", 180.0, 15.2, 4.5, 11.0));
        builtInFoods.add(new Food("F011", "Egg (Raw/Poached)", "Eggs", 143.0, 12.6, 0.7, 9.5));
        builtInFoods.add(new Food("F012", "Boiled Egg", "Eggs", 155.0, 13.0, 1.1, 10.6));
        builtInFoods.add(new Food("F013", "Fish (Rohu/Salmon Grilled)", "Seafood", 175.0, 22.0, 0.0, 9.5));
        builtInFoods.add(new Food("F014", "Prawns (Cooked)", "Seafood", 99.0, 24.0, 0.2, 0.3));
        builtInFoods.add(new Food("F015", "Soybeans (Boiled)", "Legumes", 173.0, 16.6, 9.9, 9.0));
        builtInFoods.add(new Food("F016", "Dal (Tadka/Yellow Lentils)", "Legumes", 116.0, 7.3, 16.5, 2.7));

        // Dairy
        builtInFoods.add(new Food("F017", "Milk (Cow's Whole)", "Dairy", 61.0, 3.2, 4.8, 3.3));
        builtInFoods.add(new Food("F018", "Curd / Plain Yogurt", "Dairy", 61.0, 3.5, 4.7, 3.3));
        builtInFoods.add(new Food("F019", "Paneer (Cottage Cheese)", "Dairy", 265.0, 18.3, 1.2, 20.8));

        // Vegetables & Tubers
        builtInFoods.add(new Food("F020", "Potato (Boiled)", "Vegetables", 87.0, 1.9, 20.1, 0.1));
        builtInFoods.add(new Food("F021", "Sweet Potato (Baked)", "Vegetables", 90.0, 2.0, 20.7, 0.1));
        builtInFoods.add(new Food("F022", "Spinach (Fresh/Cooked)", "Vegetables", 23.0, 2.9, 3.6, 0.4));
        builtInFoods.add(new Food("F023", "Tomato (Raw)", "Vegetables", 18.0, 0.9, 3.9, 0.2));
        builtInFoods.add(new Food("F024", "Cucumber (Raw)", "Vegetables", 15.0, 0.7, 3.6, 0.1));
        builtInFoods.add(new Food("F025", "Broccoli (Steamed)", "Vegetables", 35.0, 2.4, 7.2, 0.4));

        // Fruits
        builtInFoods.add(new Food("F026", "Banana (Fresh)", "Fruits", 89.0, 1.1, 22.8, 0.3));
        builtInFoods.add(new Food("F027", "Apple (With Skin)", "Fruits", 52.0, 0.3, 13.8, 0.2));
        builtInFoods.add(new Food("F028", "Orange (Fresh)", "Fruits", 47.0, 0.9, 11.8, 0.1));

        // Nuts & Seeds
        builtInFoods.add(new Food("F029", "Peanut (Roasted)", "Nuts/Seeds", 567.0, 25.8, 16.1, 49.2));
        builtInFoods.add(new Food("F030", "Almonds (Raw)", "Nuts/Seeds", 579.0, 21.2, 21.6, 49.9));
    }

    /**
     * Returns an unmodifiable list of all foods (built-in + custom), sorted alphabetically.
     */
    public ArrayList<Food> getAllFoods() {
        ArrayList<Food> combined = new ArrayList<>(builtInFoods);
        combined.addAll(customFoods);
        Collections.sort(combined);
        return combined;
    }

    public ArrayList<Food> getBuiltInFoods() {
        return new ArrayList<>(builtInFoods);
    }

    public ArrayList<Food> getCustomFoods() {
        return new ArrayList<>(customFoods);
    }

    /**
     * Adds custom foods loaded from storage without generating new IDs.
     */
    public void setCustomFoods(List<Food> foods) {
        this.customFoods.clear();
        if (foods != null) {
            for (Food f : foods) {
                f.setCustom(true);
                this.customFoods.add(f);
            }
        }
    }

    /**
     * Finds a food item by ID.
     */
    public Food findById(String id) {
        if (id == null) return null;
        for (Food f : builtInFoods) {
            if (f.getId().equalsIgnoreCase(id)) return f;
        }
        for (Food f : customFoods) {
            if (f.getId().equalsIgnoreCase(id)) return f;
        }
        return null;
    }

    /**
     * Finds a food item by exact name (case-insensitive).
     */
    public Food findByName(String name) {
        if (name == null) return null;
        for (Food f : builtInFoods) {
            if (f.getName().equalsIgnoreCase(name.trim())) return f;
        }
        for (Food f : customFoods) {
            if (f.getName().equalsIgnoreCase(name.trim())) return f;
        }
        return null;
    }

    /**
     * Searches foods by keyword matching name or category.
     */
    public ArrayList<Food> searchFoods(String keyword) {
        ArrayList<Food> results = new ArrayList<>();
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllFoods();
        }
        String lower = keyword.trim().toLowerCase();
        for (Food f : getAllFoods()) {
            if (f.getName().toLowerCase().contains(lower) ||
                (f.getCategory() != null && f.getCategory().toLowerCase().contains(lower))) {
                results.add(f);
            }
        }
        return results;
    }

    /**
     * Adds a new custom food item.
     * @param food Food item to add
     * @return null if successful, or error message string if duplicate/invalid.
     */
    public String addCustomFood(Food food) {
        if (food == null) {
            return "Food object cannot be null.";
        }
        String validationError = food.validate();
        if (validationError != null) {
            return validationError;
        }
        if (findByName(food.getName()) != null) {
            return "A food item with name '" + food.getName() + "' already exists.";
        }
        if (food.getId() == null || food.getId().trim().isEmpty()) {
            food.setId("C_" + UUID.randomUUID().toString().substring(0, 8));
        }
        food.setCustom(true);
        customFoods.add(food);
        return null;
    }

    /**
     * Updates an existing custom food.
     * Built-in foods cannot be updated.
     */
    public String updateCustomFood(Food food) {
        if (food == null || food.getId() == null) {
            return "Invalid food data.";
        }
        // Protect built-in foods
        for (Food builtIn : builtInFoods) {
            if (builtIn.getId().equalsIgnoreCase(food.getId())) {
                return "Built-in food items cannot be modified.";
            }
        }
        String validationError = food.validate();
        if (validationError != null) {
            return validationError;
        }
        for (int i = 0; i < customFoods.size(); i++) {
            if (customFoods.get(i).getId().equalsIgnoreCase(food.getId())) {
                food.setCustom(true);
                customFoods.set(i, food);
                return null;
            }
        }
        return "Custom food item not found.";
    }

    /**
     * Deletes a custom food item.
     * Prevents deletion of built-in system foods.
     */
    public String deleteCustomFood(String foodId) {
        if (foodId == null) {
            return "Invalid food ID.";
        }
        // Check if built-in
        for (Food builtIn : builtInFoods) {
            if (builtIn.getId().equalsIgnoreCase(foodId)) {
                return "Built-in food items cannot be deleted.";
            }
        }
        boolean removed = customFoods.removeIf(f -> f.getId().equalsIgnoreCase(foodId));
        if (removed) {
            return null;
        }
        return "Food item not found or already deleted.";
    }
}
