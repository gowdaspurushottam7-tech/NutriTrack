package com.nutrition.service;

import com.nutrition.model.Food;
import com.nutrition.model.MealEntry;

import java.util.List;

/**
 * Service class performing pure nutritional and biometric calculations.
 * Ensures complete separation of business and mathematical logic from the GUI.
 */
public class NutritionCalculator {

    /**
     * Calculates nutrition for a given food and quantity in grams.
     * Formula: Nutrition Value = (Nutrition per 100 grams * Quantity in grams) / 100.0
     *
     * @param valuePer100g Nutritional metric per 100 grams
     * @param quantityGrams Quantity consumed in grams
     * @return Total nutrient value
     */
    public static double calculateNutrient(double valuePer100g, double quantityGrams) {
        if (quantityGrams <= 0 || valuePer100g <= 0) {
            return 0.0;
        }
        return (valuePer100g * quantityGrams) / 100.0;
    }

    public static double calculateCalories(Food food, double quantityGrams) {
        if (food == null) return 0.0;
        return calculateNutrient(food.getCaloriesPer100g(), quantityGrams);
    }

    public static double calculateProtein(Food food, double quantityGrams) {
        if (food == null) return 0.0;
        return calculateNutrient(food.getProteinPer100g(), quantityGrams);
    }

    public static double calculateCarbs(Food food, double quantityGrams) {
        if (food == null) return 0.0;
        return calculateNutrient(food.getCarbsPer100g(), quantityGrams);
    }

    public static double calculateFat(Food food, double quantityGrams) {
        if (food == null) return 0.0;
        return calculateNutrient(food.getFatPer100g(), quantityGrams);
    }

    /**
     * Calculates the sum of calories for a list of meal entries.
     */
    public static double sumCalories(List<MealEntry> entries) {
        if (entries == null || entries.isEmpty()) return 0.0;
        double total = 0.0;
        for (MealEntry e : entries) {
            total += e.getCalories();
        }
        return total;
    }

    /**
     * Calculates the sum of protein for a list of meal entries.
     */
    public static double sumProtein(List<MealEntry> entries) {
        if (entries == null || entries.isEmpty()) return 0.0;
        double total = 0.0;
        for (MealEntry e : entries) {
            total += e.getProtein();
        }
        return total;
    }

    /**
     * Calculates the sum of carbohydrates for a list of meal entries.
     */
    public static double sumCarbs(List<MealEntry> entries) {
        if (entries == null || entries.isEmpty()) return 0.0;
        double total = 0.0;
        for (MealEntry e : entries) {
            total += e.getCarbs();
        }
        return total;
    }

    /**
     * Calculates the sum of fat for a list of meal entries.
     */
    public static double sumFat(List<MealEntry> entries) {
        if (entries == null || entries.isEmpty()) return 0.0;
        double total = 0.0;
        for (MealEntry e : entries) {
            total += e.getFat();
        }
        return total;
    }

    /**
     * Computes goal completion percentage safely.
     */
    public static double calculateCompletionPercentage(double actual, double goal) {
        if (goal <= 0) return 0.0;
        return (actual / goal) * 100.0;
    }

    /**
     * Computes remaining calories to achieve target.
     */
    public static double calculateRemaining(double consumed, double target) {
        double diff = target - consumed;
        return Math.max(0.0, diff);
    }
}
