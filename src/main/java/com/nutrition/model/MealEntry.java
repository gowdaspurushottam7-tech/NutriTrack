package com.nutrition.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Model class representing an individual meal record logged by the user.
 * Demonstrates Object-Oriented Principles: Encapsulation, State calculation.
 */
public class MealEntry implements Serializable, Comparable<MealEntry> {
    private static final long serialVersionUID = 1L;

    public static final String MEAL_BREAKFAST = "Breakfast";
    public static final String MEAL_LUNCH = "Lunch";
    public static final String MEAL_DINNER = "Dinner";
    public static final String MEAL_SNACKS = "Snacks";

    private String id;
    private LocalDate date;
    private String mealType;
    private String foodId;
    private String foodName;
    private double quantityGrams;
    private double calories;
    private double protein;
    private double carbs;
    private double fat;
    private LocalDateTime timestamp;

    public MealEntry() {
        this.id = UUID.randomUUID().toString();
        this.timestamp = LocalDateTime.now();
    }

    public MealEntry(LocalDate date, String mealType, Food food, double quantityGrams) {
        this.id = UUID.randomUUID().toString();
        this.date = date != null ? date : LocalDate.now();
        this.mealType = mealType;
        this.timestamp = LocalDateTime.now();
        this.quantityGrams = quantityGrams;

        if (food != null) {
            this.foodId = food.getId();
            this.foodName = food.getName();
            recalculateNutrition(food);
        }
    }

    public MealEntry(String id, LocalDate date, String mealType, String foodId, String foodName,
                     double quantityGrams, double calories, double protein, double carbs, double fat) {
        this.id = id != null ? id : UUID.randomUUID().toString();
        this.date = date != null ? date : LocalDate.now();
        this.mealType = mealType;
        this.foodId = foodId;
        this.foodName = foodName;
        this.quantityGrams = quantityGrams;
        this.calories = calories;
        this.protein = protein;
        this.carbs = carbs;
        this.fat = fat;
        this.timestamp = LocalDateTime.now();
    }

    /**
     * Recalculates nutritional totals based on food values per 100g and quantity in grams.
     * Formula: Nutrition Value = (Nutrition per 100 grams * Quantity in grams) / 100
     */
    public void recalculateNutrition(Food food) {
        if (food == null || quantityGrams <= 0) {
            this.calories = 0.0;
            this.protein = 0.0;
            this.carbs = 0.0;
            this.fat = 0.0;
            return;
        }
        this.calories = (food.getCaloriesPer100g() * quantityGrams) / 100.0;
        this.protein = (food.getProteinPer100g() * quantityGrams) / 100.0;
        this.carbs = (food.getCarbsPer100g() * quantityGrams) / 100.0;
        this.fat = (food.getFatPer100g() * quantityGrams) / 100.0;
    }

    public String validate() {
        if (date == null) {
            return "Meal date cannot be empty.";
        }
        if (mealType == null || mealType.trim().isEmpty()) {
            return "Meal type must be specified.";
        }
        if (foodName == null || foodName.trim().isEmpty()) {
            return "Food item must be selected.";
        }
        if (quantityGrams <= 0) {
            return "Quantity must be greater than zero grams.";
        }
        if (quantityGrams > 5000) {
            return "Quantity cannot exceed 5000 grams in a single entry.";
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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getMealType() {
        return mealType;
    }

    public void setMealType(String mealType) {
        this.mealType = mealType;
    }

    public String getFoodId() {
        return foodId;
    }

    public void setFoodId(String foodId) {
        this.foodId = foodId;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public double getQuantityGrams() {
        return quantityGrams;
    }

    public void setQuantityGrams(double quantityGrams) {
        this.quantityGrams = quantityGrams;
    }

    public double getCalories() {
        return calories;
    }

    public void setCalories(double calories) {
        this.calories = calories;
    }

    public double getProtein() {
        return protein;
    }

    public void setProtein(double protein) {
        this.protein = protein;
    }

    public double getCarbs() {
        return carbs;
    }

    public void setCarbs(double carbs) {
        this.carbs = carbs;
    }

    public double getFat() {
        return fat;
    }

    public void setFat(double fat) {
        this.fat = fat;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public int compareTo(MealEntry other) {
        // Sort descending by date, then meal type
        int dateCmp = other.date.compareTo(this.date);
        if (dateCmp != 0) return dateCmp;
        return this.mealType.compareTo(other.mealType);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MealEntry that = (MealEntry) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "MealEntry{" +
                "date=" + date +
                ", mealType='" + mealType + '\'' +
                ", foodName='" + foodName + '\'' +
                ", quantityGrams=" + quantityGrams +
                ", calories=" + calories +
                '}';
    }
}
