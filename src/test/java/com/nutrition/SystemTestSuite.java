package com.nutrition;

import com.nutrition.model.Food;
import com.nutrition.model.MealEntry;
import com.nutrition.model.NutritionReport;
import com.nutrition.model.User;
import com.nutrition.service.FoodDatabase;
import com.nutrition.service.NutritionCalculator;
import com.nutrition.service.NutritionService;
import com.nutrition.storage.FileManager;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Automated Verification & Testing Suite for the Dietary Calorie System.
 * Tests Models, Services, Calculations, Validations, and File Handling.
 */
public class SystemTestSuite {

    private static int testsPassed = 0;
    private static int testsTotal = 0;

    public static void main(String[] args) {
        System.out.println("===============================================================");
        System.out.println("  RUNNING SYSTEM TEST SUITE: NutriTrack Dietary System");
        System.out.println("===============================================================\n");

        testUserValidationAndBiometrics();
        testFoodDatabaseAndIntegrity();
        testNutritionCalculatorLogic();
        testMealEntryCalculations();
        testFilePersistence();
        testReportGenerationAndExport();
        testNutritionServiceIntegration();

        System.out.println("\n===============================================================");
        System.out.printf("  TEST RESULTS SUMMARY: %d / %d PASSED (%.1f%%)\n",
                testsPassed, testsTotal, (testsPassed * 100.0 / testsTotal));
        System.out.println("===============================================================");

        if (testsPassed != testsTotal) {
            System.err.println("Some tests failed!");
            System.exit(1);
        } else {
            System.out.println("ALL SYSTEM TESTS PASSED SUCCESSFULLY! Ready for academic viva.");
        }
    }

    private static void assertTrue(String testName, boolean condition) {
        testsTotal++;
        if (condition) {
            testsPassed++;
            System.out.println("  [PASS] " + testName);
        } else {
            System.err.println("  [FAIL] " + testName);
        }
    }

    private static void testUserValidationAndBiometrics() {
        System.out.println(">> Testing User Model & Biometrics...");

        User user = new User("Purushottam", 20, "Male", 175.0, 70.0, 2200, 120, 250, 65);
        assertTrue("Valid user profile returns no error", user.validate() == null);

        // Validation boundaries
        User invalidAge = new User("John", 150, "Male", 175, 70, 2000, 100, 200, 50);
        assertTrue("Age > 120 is rejected", invalidAge.validate() != null);

        User negativeHeight = new User("Jane", 25, "Female", -10, 60, 1800, 90, 180, 45);
        assertTrue("Negative height is rejected", negativeHeight.validate() != null);

        User negativeGoal = new User("Jane", 25, "Female", 160, 60, -500, 90, 180, 45);
        assertTrue("Negative calorie goal is rejected", negativeGoal.validate() != null);

        // BMI Check: 70 / (1.75 * 1.75) = 22.86 (Normal weight)
        double bmi = user.calculateBMI();
        assertTrue("BMI is correctly calculated near 22.86", Math.abs(bmi - 22.86) < 0.1);
        assertTrue("BMI Category is Normal weight", "Normal weight".equals(user.getBMICategory()));

        // BMR Check: (10 * 70) + (6.25 * 175) - (5 * 20) + 5 = 700 + 1093.75 - 100 + 5 = 1698.75
        double bmr = user.calculateBMR();
        assertTrue("BMR is correctly calculated", Math.abs(bmr - 1698.75) < 0.1);
    }

    private static void testFoodDatabaseAndIntegrity() {
        System.out.println("\n>> Testing Food Database & Catalog Operations...");

        FoodDatabase db = new FoodDatabase();
        ArrayList<Food> builtIns = db.getBuiltInFoods();
        assertTrue("Database contains at least 30 built-in foods", builtIns.size() >= 30);

        // Search check
        Food rice = db.findByName("Rice (Cooked White)");
        assertTrue("Found 'Rice (Cooked White)' in database", rice != null && rice.getCaloriesPer100g() == 130.0);

        ArrayList<Food> chickenMatches = db.searchFoods("Chicken");
        assertTrue("Search for 'Chicken' finds matches", chickenMatches.size() >= 2);

        // Custom Food Addition
        Food tofu = new Food(null, "Tofu (Firm Organic)", "Protein", 76.0, 8.0, 1.9, 4.8, true);
        String addRes = db.addCustomFood(tofu);
        assertTrue("Custom food added successfully", addRes == null);
        assertTrue("Tofu is marked as custom", db.findByName("Tofu (Firm Organic)").isCustom());

        // Duplicate rejection
        Food duplicateTofu = new Food(null, "Tofu (Firm Organic)", "Protein", 76.0, 8.0, 1.9, 4.8, true);
        String dupRes = db.addCustomFood(duplicateTofu);
        assertTrue("Duplicate food addition is rejected", dupRes != null);

        // Protection: Cannot delete built-in food
        String delBuiltIn = db.deleteCustomFood("F001");
        assertTrue("Deleting built-in food 'F001' is blocked", delBuiltIn != null);

        // Can delete custom food
        Food customItem = db.findByName("Tofu (Firm Organic)");
        String delCustom = db.deleteCustomFood(customItem.getId());
        assertTrue("Deleting custom food succeeds", delCustom == null);
    }

    private static void testNutritionCalculatorLogic() {
        System.out.println("\n>> Testing Nutrition Calculator Math...");

        // Formula: Value = (Per100g * Quantity) / 100.0
        // Rice 130 kcal/100g, Quantity 200g -> 260 kcal
        Food rice = new Food("F001", "Rice", "Grains", 130.0, 2.7, 28.2, 0.3);
        double cal = NutritionCalculator.calculateCalories(rice, 200.0);
        assertTrue("200g of 130 kcal/100g yields 260 kcal", Math.abs(cal - 260.0) < 0.001);

        double pro = NutritionCalculator.calculateProtein(rice, 200.0);
        assertTrue("200g of 2.7g/100g yields 5.4g protein", Math.abs(pro - 5.4) < 0.001);

        // Completion percentage
        double pct = NutritionCalculator.calculateCompletionPercentage(1100, 2200);
        assertTrue("1100 / 2200 is exactly 50%", Math.abs(pct - 50.0) < 0.001);

        // Remaining calories
        double rem = NutritionCalculator.calculateRemaining(1800, 2200);
        assertTrue("2200 - 1800 is 400 remaining kcal", Math.abs(rem - 400.0) < 0.001);

        double remOver = NutritionCalculator.calculateRemaining(2500, 2200);
        assertTrue("Over target yields 0 remaining kcal", remOver == 0.0);
    }

    private static void testMealEntryCalculations() {
        System.out.println("\n>> Testing MealEntry Lifecycle & Auto-calculations...");

        Food chicken = new Food("F009", "Chicken Breast", "Poultry", 165.0, 31.0, 0.0, 3.6);
        MealEntry entry = new MealEntry(LocalDate.now(), MealEntry.MEAL_LUNCH, chicken, 200.0);

        assertTrue("Meal entry has 330 calories (165 * 2)", Math.abs(entry.getCalories() - 330.0) < 0.01);
        assertTrue("Meal entry has 62g protein (31 * 2)", Math.abs(entry.getProtein() - 62.0) < 0.01);
        assertTrue("Meal entry validates cleanly", entry.validate() == null);

        // Recalculate on quantity edit
        entry.setQuantityGrams(100.0);
        entry.recalculateNutrition(chicken);
        assertTrue("Recalculated to 165 calories on 100g edit", Math.abs(entry.getCalories() - 165.0) < 0.01);
    }

    private static void testFilePersistence() {
        System.out.println("\n>> Testing Local File Storage & Persistence...");

        FileManager fm = new FileManager();
        User testUser = new User("StorageTestUser", 22, "Other", 180, 75, 2400, 130, 260, 70);
        fm.saveUser(testUser);

        User loadedUser = fm.loadUser();
        assertTrue("Saved user successfully reloaded from file", loadedUser != null && "StorageTestUser".equals(loadedUser.getName()));

        // Meal persistence
        ArrayList<MealEntry> testMeals = new ArrayList<>();
        Food egg = new Food("F012", "Boiled Egg", "Eggs", 155.0, 13.0, 1.1, 10.6);
        testMeals.add(new MealEntry(LocalDate.now(), MealEntry.MEAL_BREAKFAST, egg, 100.0));
        fm.saveMealEntries(testMeals);

        ArrayList<MealEntry> loadedMeals = fm.loadMealEntries();
        assertTrue("Saved meal entries reloaded successfully", loadedMeals != null && !loadedMeals.isEmpty());
    }

    private static void testReportGenerationAndExport() {
        System.out.println("\n>> Testing Analytics, Reports, and CSV/TXT Exporters...");

        NutritionService service = new NutritionService();
        LocalDate today = LocalDate.now();

        NutritionReport dailyReport = service.generateDailyReport(today);
        assertTrue("Daily report generated", dailyReport != null);
        assertTrue("Daily report has positive or zero calories", dailyReport.getTotalCalories() >= 0);

        NutritionReport weeklyReport = service.generateWeeklyReport(today);
        assertTrue("Weekly report covers 7 days", weeklyReport.getDailyCalorieMap().size() == 7);
        assertTrue("Weekly report has average daily calories", weeklyReport.getAverageDailyCalories() >= 0);

        // CSV Export check
        String csv = weeklyReport.toCsv();
        assertTrue("CSV contains report header", csv.contains("# DIETARY CALORIE & NUTRITION REPORT"));
        assertTrue("CSV contains daily calorie trend", csv.contains("# DAILY CALORIE TREND"));

        // TXT Export check
        String txt = weeklyReport.toTxt();
        assertTrue("TXT contains report title banner", txt.contains("DIETARY CALORIE INTAKE & NUTRITION LOG SYSTEM REPORT"));
        assertTrue("TXT contains statistical highlights", txt.contains("7-DAY STATISTICAL HIGHLIGHTS"));

        // File writing test
        File tempExport = new File("data" + File.separator + "test_export.csv");
        boolean exported = FileManager.exportContentToFile(csv, tempExport);
        assertTrue("Report successfully exported to disk", exported && tempExport.exists());
        if (tempExport.exists()) {
            tempExport.delete();
        }
    }

    private static void testNutritionServiceIntegration() {
        System.out.println("\n>> Testing Nutrition Service Integration...");

        NutritionService service = new NutritionService();
        int initialCount = service.getAllMeals().size();

        Food paneer = service.getFoodDatabase().findByName("Paneer (Cottage Cheese)");
        MealEntry entry = new MealEntry(LocalDate.now(), MealEntry.MEAL_DINNER, paneer, 100.0);
        String addErr = service.addMealEntry(entry);
        assertTrue("Service added meal entry cleanly", addErr == null);
        assertTrue("Meal count increased by 1", service.getAllMeals().size() == initialCount + 1);

        // Delete meal entry
        boolean deleted = service.deleteMealEntry(entry.getId());
        assertTrue("Service deleted meal entry cleanly", deleted);
        assertTrue("Meal count returned to initial count", service.getAllMeals().size() == initialCount);
    }
}
