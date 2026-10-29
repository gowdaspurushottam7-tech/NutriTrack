package com.nutrition.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Model class representing a Food item with nutrition metrics per 100 grams.
 * Demonstrates Object-Oriented Principles: Encapsulation, Immutability/Validation.
 */
public class Food implements Serializable, Comparable<Food> {
    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String category;
    private double caloriesPer100g;
    private double proteinPer100g;
    private double carbsPer100g;
    private double fatPer100g;
    private boolean isCustom;

    public Food() {
    }

    public Food(String id, String name, String category, double caloriesPer100g,
                double proteinPer100g, double carbsPer100g, double fatPer100g, boolean isCustom) {
        this.id = id;
        this.name = name;
        this.category = (category == null || category.trim().isEmpty()) ? "General" : category;
        this.caloriesPer100g = caloriesPer100g;
        this.proteinPer100g = proteinPer100g;
        this.carbsPer100g = carbsPer100g;
        this.fatPer100g = fatPer100g;
        this.isCustom = isCustom;
    }

    // Convenience constructor for built-in items
    public Food(String id, String name, String category, double caloriesPer100g,
                double proteinPer100g, double carbsPer100g, double fatPer100g) {
        this(id, name, category, caloriesPer100g, proteinPer100g, carbsPer100g, fatPer100g, false);
    }

    /**
     * Validates food metrics.
     * @return null if valid, error message otherwise.
     */
    public String validate() {
        if (name == null || name.trim().isEmpty()) {
            return "Food name cannot be empty.";
        }
        if (caloriesPer100g < 0) {
            return "Calories cannot be negative.";
        }
        if (proteinPer100g < 0 || carbsPer100g < 0 || fatPer100g < 0) {
            return "Macro-nutrients (protein, carbs, fat) cannot be negative.";
        }
        if ((proteinPer100g + carbsPer100g + fatPer100g) > 100.0) {
            return "Sum of protein, carbs, and fat cannot exceed 100g per 100g of food.";
        }
        return null;
    }

    // --- Getters and Setters ---

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getCaloriesPer100g() {
        return caloriesPer100g;
    }

    public void setCaloriesPer100g(double caloriesPer100g) {
        this.caloriesPer100g = caloriesPer100g;
    }

    public double getProteinPer100g() {
        return proteinPer100g;
    }

    public void setProteinPer100g(double proteinPer100g) {
        this.proteinPer100g = proteinPer100g;
    }

    public double getCarbsPer100g() {
        return carbsPer100g;
    }

    public void setCarbsPer100g(double carbsPer100g) {
        this.carbsPer100g = carbsPer100g;
    }

    public double getFatPer100g() {
        return fatPer100g;
    }

    public void setFatPer100g(double fatPer100g) {
        this.fatPer100g = fatPer100g;
    }

    public boolean isCustom() {
        return isCustom;
    }

    public void setCustom(boolean custom) {
        isCustom = custom;
    }

    @Override
    public int compareTo(Food other) {
        return this.name.compareToIgnoreCase(other.name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Food food = (Food) o;
        return Objects.equals(id, food.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return name + " (" + String.format("%.0f", caloriesPer100g) + " kcal/100g)";
    }
}
