package com.nutrition.service;

import com.nutrition.model.Food;
import com.nutrition.model.MealEntry;
import com.nutrition.model.NutritionReport;
import com.nutrition.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Service Interface defining the contract for nutrition and meal management.
 * Demonstrates the Object-Oriented principle of Abstraction.
 */
public interface INutritionService {

    User getUser();

    void updateUser(User user);

    FoodDatabase getFoodDatabase();

    String addMealEntry(MealEntry entry);

    String updateMealEntry(MealEntry entry);

    boolean deleteMealEntry(String entryId);

    MealEntry getMealEntryById(String entryId);

    ArrayList<MealEntry> getAllMeals();

    ArrayList<MealEntry> getMealsForDate(LocalDate date);

    ArrayList<MealEntry> getMealsForDateAndType(LocalDate date, String mealType);

    int getMealCountForToday(String mealType);

    int getTotalMealsToday();

    double getCaloriesConsumedToday();

    double getProteinConsumedToday();

    double getCarbsConsumedToday();

    double getFatConsumedToday();

    List<MealEntry> getRecentMeals(int limit);

    NutritionReport generateDailyReport(LocalDate date);

    NutritionReport generateWeeklyReport(LocalDate endDate);

    void saveAllData();

    void loadAllData();
}
