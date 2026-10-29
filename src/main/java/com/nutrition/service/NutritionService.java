package com.nutrition.service;

import com.nutrition.model.Food;
import com.nutrition.model.MealEntry;
import com.nutrition.model.NutritionReport;
import com.nutrition.model.User;
import com.nutrition.storage.FileManager;

import java.time.LocalDate;
import java.util.*;

/**
 * Core Service coordinating business logic, calculations, and data persistence.
 * Implements the INutritionService interface demonstrating Abstraction and Polymorphism.
 */
public class NutritionService implements INutritionService {
    private final FileManager fileManager;
    private final FoodDatabase foodDatabase;
    private final ArrayList<MealEntry> mealEntries;
    private User currentUser;

    public NutritionService() {
        this.fileManager = new FileManager();
        this.foodDatabase = new FoodDatabase();
        this.mealEntries = new ArrayList<>();
        loadAllData();
    }

    @Override
    public User getUser() {
        return currentUser;
    }

    @Override
    public void updateUser(User user) {
        this.currentUser = user;
        fileManager.saveUser(user);
    }

    @Override
    public FoodDatabase getFoodDatabase() {
        return foodDatabase;
    }

    @Override
    public synchronized String addMealEntry(MealEntry entry) {
        if (entry == null) {
            return "Meal entry cannot be null.";
        }
        String validation = entry.validate();
        if (validation != null) {
            return validation;
        }
        mealEntries.add(entry);
        Collections.sort(mealEntries);
        fileManager.saveMealEntries(mealEntries);
        return null;
    }

    @Override
    public synchronized String updateMealEntry(MealEntry entry) {
        if (entry == null || entry.getId() == null) {
            return "Invalid meal entry.";
        }
        String validation = entry.validate();
        if (validation != null) {
            return validation;
        }
        for (int i = 0; i < mealEntries.size(); i++) {
            if (mealEntries.get(i).getId().equals(entry.getId())) {
                mealEntries.set(i, entry);
                Collections.sort(mealEntries);
                fileManager.saveMealEntries(mealEntries);
                return null;
            }
        }
        return "Meal entry not found.";
    }

    @Override
    public synchronized boolean deleteMealEntry(String entryId) {
        if (entryId == null) return false;
        boolean removed = mealEntries.removeIf(e -> e.getId().equals(entryId));
        if (removed) {
            fileManager.saveMealEntries(mealEntries);
        }
        return removed;
    }

    @Override
    public MealEntry getMealEntryById(String entryId) {
        if (entryId == null) return null;
        for (MealEntry e : mealEntries) {
            if (e.getId().equals(entryId)) {
                return e;
            }
        }
        return null;
    }

    @Override
    public ArrayList<MealEntry> getAllMeals() {
        return new ArrayList<>(mealEntries);
    }

    @Override
    public ArrayList<MealEntry> getMealsForDate(LocalDate date) {
        ArrayList<MealEntry> result = new ArrayList<>();
        if (date == null) return result;
        for (MealEntry e : mealEntries) {
            if (date.equals(e.getDate())) {
                result.add(e);
            }
        }
        Collections.sort(result);
        return result;
    }

    @Override
    public ArrayList<MealEntry> getMealsForDateAndType(LocalDate date, String mealType) {
        ArrayList<MealEntry> result = new ArrayList<>();
        if (date == null) return result;
        for (MealEntry e : mealEntries) {
            if (date.equals(e.getDate())) {
                if (mealType == null || mealType.equalsIgnoreCase("All") || mealType.equalsIgnoreCase(e.getMealType())) {
                    result.add(e);
                }
            }
        }
        Collections.sort(result);
        return result;
    }

    @Override
    public int getMealCountForToday(String mealType) {
        LocalDate today = LocalDate.now();
        int count = 0;
        for (MealEntry e : mealEntries) {
            if (today.equals(e.getDate()) && e.getMealType().equalsIgnoreCase(mealType)) {
                count++;
            }
        }
        return count;
    }

    @Override
    public int getTotalMealsToday() {
        return getMealsForDate(LocalDate.now()).size();
    }

    @Override
    public double getCaloriesConsumedToday() {
        return NutritionCalculator.sumCalories(getMealsForDate(LocalDate.now()));
    }

    @Override
    public double getProteinConsumedToday() {
        return NutritionCalculator.sumProtein(getMealsForDate(LocalDate.now()));
    }

    @Override
    public double getCarbsConsumedToday() {
        return NutritionCalculator.sumCarbs(getMealsForDate(LocalDate.now()));
    }

    @Override
    public double getFatConsumedToday() {
        return NutritionCalculator.sumFat(getMealsForDate(LocalDate.now()));
    }

    @Override
    public List<MealEntry> getRecentMeals(int limit) {
        ArrayList<MealEntry> sorted = new ArrayList<>(mealEntries);
        Collections.sort(sorted);
        int take = Math.min(limit, sorted.size());
        return sorted.subList(0, take);
    }

    @Override
    public NutritionReport generateDailyReport(LocalDate date) {
        if (date == null) date = LocalDate.now();
        List<MealEntry> dayMeals = getMealsForDate(date);

        NutritionReport report = new NutritionReport();
        report.setReportType("Daily");
        report.setStartDate(date);
        report.setEndDate(date);
        report.setMealEntries(dayMeals);
        report.setTotalMeals(dayMeals.size());

        report.setTotalCalories(NutritionCalculator.sumCalories(dayMeals));
        report.setTotalProtein(NutritionCalculator.sumProtein(dayMeals));
        report.setTotalCarbs(NutritionCalculator.sumCarbs(dayMeals));
        report.setTotalFat(NutritionCalculator.sumFat(dayMeals));

        if (currentUser != null) {
            report.setTargetCalorieGoal(currentUser.getDailyCalorieGoal());
            report.setTargetProteinGoal(currentUser.getDailyProteinGoal());
            report.setTargetCarbGoal(currentUser.getDailyCarbGoal());
            report.setTargetFatGoal(currentUser.getDailyFatGoal());
        }

        Map<LocalDate, Double> map = new LinkedHashMap<>();
        map.put(date, report.getTotalCalories());
        report.setDailyCalorieMap(map);

        return report;
    }

    @Override
    public NutritionReport generateWeeklyReport(LocalDate endDate) {
        if (endDate == null) endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(6);

        ArrayList<MealEntry> periodMeals = new ArrayList<>();
        Map<LocalDate, Double> dailyCalories = new LinkedHashMap<>();
        Map<LocalDate, Double> dailyProtein = new LinkedHashMap<>();

        // Initialize 7 days in order
        for (int i = 6; i >= 0; i--) {
            LocalDate d = endDate.minusDays(i);
            dailyCalories.put(d, 0.0);
            dailyProtein.put(d, 0.0);
        }

        for (MealEntry m : mealEntries) {
            LocalDate mDate = m.getDate();
            if (!mDate.isBefore(startDate) && !mDate.isAfter(endDate)) {
                periodMeals.add(m);
                dailyCalories.put(mDate, dailyCalories.getOrDefault(mDate, 0.0) + m.getCalories());
                dailyProtein.put(mDate, dailyProtein.getOrDefault(mDate, 0.0) + m.getProtein());
            }
        }

        NutritionReport report = new NutritionReport();
        report.setReportType("Weekly");
        report.setStartDate(startDate);
        report.setEndDate(endDate);
        report.setMealEntries(periodMeals);
        report.setTotalMeals(periodMeals.size());
        report.setDailyCalorieMap(dailyCalories);

        double totalCalories = 0.0;
        double totalProtein = 0.0;
        double totalCarbs = NutritionCalculator.sumCarbs(periodMeals);
        double totalFat = NutritionCalculator.sumFat(periodMeals);

        LocalDate highestDay = null;
        double highestVal = -1.0;
        LocalDate lowestDay = null;
        double lowestVal = Double.MAX_VALUE;

        for (Map.Entry<LocalDate, Double> entry : dailyCalories.entrySet()) {
            double c = entry.getValue();
            totalCalories += c;
            if (c > highestVal) {
                highestVal = c;
                highestDay = entry.getKey();
            }
            if (c < lowestVal) {
                lowestVal = c;
                lowestDay = entry.getKey();
            }
        }

        for (double p : dailyProtein.values()) {
            totalProtein += p;
        }

        report.setTotalCalories(totalCalories);
        report.setTotalProtein(totalProtein);
        report.setTotalCarbs(totalCarbs);
        report.setTotalFat(totalFat);

        report.setAverageDailyCalories(totalCalories / 7.0);
        report.setAverageProtein(totalProtein / 7.0);
        report.setHighestCalorieDate(highestDay);
        report.setHighestCalorieValue(Math.max(0.0, highestVal));
        report.setLowestCalorieDate(lowestDay);
        report.setLowestCalorieValue(lowestVal == Double.MAX_VALUE ? 0.0 : lowestVal);

        if (currentUser != null) {
            report.setTargetCalorieGoal(currentUser.getDailyCalorieGoal() * 7.0);
            report.setTargetProteinGoal(currentUser.getDailyProteinGoal() * 7.0);
            report.setTargetCarbGoal(currentUser.getDailyCarbGoal() * 7.0);
            report.setTargetFatGoal(currentUser.getDailyFatGoal() * 7.0);
        }

        return report;
    }

    @Override
    public void saveAllData() {
        if (currentUser != null) {
            fileManager.saveUser(currentUser);
        }
        fileManager.saveCustomFoods(foodDatabase.getCustomFoods());
        fileManager.saveMealEntries(mealEntries);
    }

    @Override
    public void loadAllData() {
        this.currentUser = fileManager.loadUser();
        if (this.currentUser == null) {
            this.currentUser = new User(); // sensible default profile
            fileManager.saveUser(this.currentUser);
        }

        ArrayList<Food> customList = fileManager.loadCustomFoods();
        this.foodDatabase.setCustomFoods(customList);

        ArrayList<MealEntry> loadedMeals = fileManager.loadMealEntries();
        this.mealEntries.clear();
        if (loadedMeals != null) {
            this.mealEntries.addAll(loadedMeals);
            Collections.sort(this.mealEntries);
        }

        // Seed some sample realistic data for today if brand new application to give a stunning out-of-the-box demo
        if (this.mealEntries.isEmpty()) {
            seedSampleDemoData();
        }
    }

    /**
     * Seeds realistic initial meal data across recent days so the student has an
     * immediately impressive, functioning dashboard and 7-day reports upon first launch.
     */
    private void seedSampleDemoData() {
        LocalDate today = LocalDate.now();

        // Today's meals
        Food oats = foodDatabase.findByName("Oats (Rolled)");
        Food milk = foodDatabase.findByName("Milk (Cow's Whole)");
        Food banana = foodDatabase.findByName("Banana (Fresh)");
        if (oats != null) mealEntries.add(new MealEntry(today, MealEntry.MEAL_BREAKFAST, oats, 60.0));
        if (milk != null) mealEntries.add(new MealEntry(today, MealEntry.MEAL_BREAKFAST, milk, 200.0));
        if (banana != null) mealEntries.add(new MealEntry(today, MealEntry.MEAL_BREAKFAST, banana, 120.0));

        Food rice = foodDatabase.findByName("Rice (Cooked White)");
        Food dal = foodDatabase.findByName("Dal (Tadka/Yellow Lentils)");
        Food chicken = foodDatabase.findByName("Chicken Breast (Cooked)");
        if (rice != null) mealEntries.add(new MealEntry(today, MealEntry.MEAL_LUNCH, rice, 180.0));
        if (dal != null) mealEntries.add(new MealEntry(today, MealEntry.MEAL_LUNCH, dal, 150.0));
        if (chicken != null) mealEntries.add(new MealEntry(today, MealEntry.MEAL_LUNCH, chicken, 150.0));

        Food almonds = foodDatabase.findByName("Almonds (Raw)");
        Food apple = foodDatabase.findByName("Apple (With Skin)");
        if (almonds != null) mealEntries.add(new MealEntry(today, MealEntry.MEAL_SNACKS, almonds, 30.0));
        if (apple != null) mealEntries.add(new MealEntry(today, MealEntry.MEAL_SNACKS, apple, 150.0));

        Food roti = foodDatabase.findByName("Roti (Tandoori/Plain)");
        Food paneer = foodDatabase.findByName("Paneer (Cottage Cheese)");
        if (roti != null) mealEntries.add(new MealEntry(today, MealEntry.MEAL_DINNER, roti, 120.0));
        if (paneer != null) mealEntries.add(new MealEntry(today, MealEntry.MEAL_DINNER, paneer, 100.0));

        // Previous 3 days sample data for weekly chart
        for (int d = 1; d <= 3; d++) {
            LocalDate pastDate = today.minusDays(d);
            if (rice != null) mealEntries.add(new MealEntry(pastDate, MealEntry.MEAL_LUNCH, rice, 200.0));
            if (chicken != null) mealEntries.add(new MealEntry(pastDate, MealEntry.MEAL_LUNCH, chicken, 120.0));
            if (roti != null) mealEntries.add(new MealEntry(pastDate, MealEntry.MEAL_DINNER, roti, 150.0));
            if (oats != null) mealEntries.add(new MealEntry(pastDate, MealEntry.MEAL_BREAKFAST, oats, 50.0));
        }

        Collections.sort(mealEntries);
        fileManager.saveMealEntries(mealEntries);
    }
}
