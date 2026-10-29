package com.nutrition.dto;

import java.time.LocalDate;

/**
 * DTO for Meal Creation and Update requests.
 */
public class MealRequestDTO {
    private String id;
    private LocalDate date;
    private String mealType; // "Breakfast", "Lunch", "Dinner", "Snacks"
    private String foodId;
    private String foodName;
    private double quantityGrams;

    public MealRequestDTO() {
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getMealType() { return mealType; }
    public void setMealType(String mealType) { this.mealType = mealType; }

    public String getFoodId() { return foodId; }
    public void setFoodId(String foodId) { this.foodId = foodId; }

    public String getFoodName() { return foodName; }
    public void setFoodName(String foodName) { this.foodName = foodName; }

    public double getQuantityGrams() { return quantityGrams; }
    public void setQuantityGrams(double quantityGrams) { this.quantityGrams = quantityGrams; }
}
