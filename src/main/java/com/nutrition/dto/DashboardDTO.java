package com.nutrition.dto;

import com.nutrition.model.MealEntry;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Data Transfer Object containing full aggregated metrics for the Dashboard.
 */
public class DashboardDTO {
    private String greeting;
    private String userName;
    private LocalDate currentDate;

    // Macro Targets & Actuals
    private double caloriesConsumed;
    private double calorieGoal;
    private double remainingCalories;
    private double calorieCompletionPercentage;

    private double proteinConsumed;
    private double proteinGoal;
    private double proteinCompletionPercentage;

    private double carbsConsumed;
    private double carbGoal;
    private double carbCompletionPercentage;

    private double fatConsumed;
    private double fatGoal;
    private double fatCompletionPercentage;

    // Meal Category Counts
    private int breakfastCount;
    private int lunchCount;
    private int dinnerCount;
    private int snacksCount;
    private int totalMealsToday;

    // Biometrics
    private double bmi;
    private String bmiCategory;
    private double bmr;

    // Recent Meals & Trend
    private List<MealEntry> recentMeals;
    private Map<String, Double> weeklyCalories; // Formatted date string -> calories

    public DashboardDTO() {
    }

    // Getters and Setters
    public String getGreeting() { return greeting; }
    public void setGreeting(String greeting) { this.greeting = greeting; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public LocalDate getCurrentDate() { return currentDate; }
    public void setCurrentDate(LocalDate currentDate) { this.currentDate = currentDate; }

    public double getCaloriesConsumed() { return caloriesConsumed; }
    public void setCaloriesConsumed(double caloriesConsumed) { this.caloriesConsumed = caloriesConsumed; }

    public double getCalorieGoal() { return calorieGoal; }
    public void setCalorieGoal(double calorieGoal) { this.calorieGoal = calorieGoal; }

    public double getRemainingCalories() { return remainingCalories; }
    public void setRemainingCalories(double remainingCalories) { this.remainingCalories = remainingCalories; }

    public double getCalorieCompletionPercentage() { return calorieCompletionPercentage; }
    public void setCalorieCompletionPercentage(double calorieCompletionPercentage) { this.calorieCompletionPercentage = calorieCompletionPercentage; }

    public double getProteinConsumed() { return proteinConsumed; }
    public void setProteinConsumed(double proteinConsumed) { this.proteinConsumed = proteinConsumed; }

    public double getProteinGoal() { return proteinGoal; }
    public void setProteinGoal(double proteinGoal) { this.proteinGoal = proteinGoal; }

    public double getProteinCompletionPercentage() { return proteinCompletionPercentage; }
    public void setProteinCompletionPercentage(double proteinCompletionPercentage) { this.proteinCompletionPercentage = proteinCompletionPercentage; }

    public double getCarbsConsumed() { return carbsConsumed; }
    public void setCarbsConsumed(double carbsConsumed) { this.carbsConsumed = carbsConsumed; }

    public double getCarbGoal() { return carbGoal; }
    public void setCarbGoal(double carbGoal) { this.carbGoal = carbGoal; }

    public double getCarbCompletionPercentage() { return carbCompletionPercentage; }
    public void setCarbCompletionPercentage(double carbCompletionPercentage) { this.carbCompletionPercentage = carbCompletionPercentage; }

    public double getFatConsumed() { return fatConsumed; }
    public void setFatConsumed(double fatConsumed) { this.fatConsumed = fatConsumed; }

    public double getFatGoal() { return fatGoal; }
    public void setFatGoal(double fatGoal) { this.fatGoal = fatGoal; }

    public double getFatCompletionPercentage() { return fatCompletionPercentage; }
    public void setFatCompletionPercentage(double fatCompletionPercentage) { this.fatCompletionPercentage = fatCompletionPercentage; }

    public int getBreakfastCount() { return breakfastCount; }
    public void setBreakfastCount(int breakfastCount) { this.breakfastCount = breakfastCount; }

    public int getLunchCount() { return lunchCount; }
    public void setLunchCount(int lunchCount) { this.lunchCount = lunchCount; }

    public int getDinnerCount() { return dinnerCount; }
    public void setDinnerCount(int dinnerCount) { this.dinnerCount = dinnerCount; }

    public int getSnacksCount() { return snacksCount; }
    public void setSnacksCount(int snacksCount) { this.snacksCount = snacksCount; }

    public int getTotalMealsToday() { return totalMealsToday; }
    public void setTotalMealsToday(int totalMealsToday) { this.totalMealsToday = totalMealsToday; }

    public double getBmi() { return bmi; }
    public void setBmi(double bmi) { this.bmi = bmi; }

    public String getBmiCategory() { return bmiCategory; }
    public void setBmiCategory(String bmiCategory) { this.bmiCategory = bmiCategory; }

    public double getBmr() { return bmr; }
    public void setBmr(double bmr) { this.bmr = bmr; }

    public List<MealEntry> getRecentMeals() { return recentMeals; }
    public void setRecentMeals(List<MealEntry> recentMeals) { this.recentMeals = recentMeals; }

    public Map<String, Double> getWeeklyCalories() { return weeklyCalories; }
    public void setWeeklyCalories(Map<String, Double> weeklyCalories) { this.weeklyCalories = weeklyCalories; }
}
