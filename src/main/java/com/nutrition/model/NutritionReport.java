package com.nutrition.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Model class representing daily and weekly aggregated nutrition summaries,
 * statistical metrics, and report export formatters (CSV, TXT).
 */
public class NutritionReport implements Serializable {
    private static final long serialVersionUID = 1L;

    private String reportType; // "Daily" or "Weekly"
    private LocalDate startDate;
    private LocalDate endDate;

    private double totalCalories;
    private double totalProtein;
    private double totalCarbs;
    private double totalFat;
    private int totalMeals;

    private double targetCalorieGoal;
    private double targetProteinGoal;
    private double targetCarbGoal;
    private double targetFatGoal;

    // Weekly metrics
    private double averageDailyCalories;
    private double averageProtein;
    private LocalDate highestCalorieDate;
    private double highestCalorieValue;
    private LocalDate lowestCalorieDate;
    private double lowestCalorieValue;

    // Daily breakdown: Date -> calories consumed
    private Map<LocalDate, Double> dailyCalorieMap = new LinkedHashMap<>();
    private List<MealEntry> mealEntries;

    public NutritionReport() {
    }

    // --- Analytics Getters ---

    public double getCalorieCompletionPercentage() {
        if (targetCalorieGoal <= 0) return 0.0;
        return (totalCalories / targetCalorieGoal) * 100.0;
    }

    public double getProteinCompletionPercentage() {
        if (targetProteinGoal <= 0) return 0.0;
        return (totalProtein / targetProteinGoal) * 100.0;
    }

    public double getCarbCompletionPercentage() {
        if (targetCarbGoal <= 0) return 0.0;
        return (totalCarbs / targetCarbGoal) * 100.0;
    }

    public double getFatCompletionPercentage() {
        if (targetFatGoal <= 0) return 0.0;
        return (totalFat / targetFatGoal) * 100.0;
    }

    // --- Export Generators ---

    /**
     * Generates a clean comma-separated values (CSV) string for spreadsheet export.
     */
    public String toCsv() {
        StringBuilder sb = new StringBuilder();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        sb.append("# DIETARY CALORIE & NUTRITION REPORT\n");
        sb.append("Report Type,").append(reportType).append("\n");
        sb.append("Period,").append(startDate.format(dtf)).append(" to ").append(endDate.format(dtf)).append("\n");
        sb.append("Total Meals Logged,").append(totalMeals).append("\n");
        sb.append(String.format("Total Calories (kcal),%.1f,Target,%.1f,Completion,%.1f%%\n",
                totalCalories, targetCalorieGoal, getCalorieCompletionPercentage()));
        sb.append(String.format("Total Protein (g),%.1f,Target,%.1f,Completion,%.1f%%\n",
                totalProtein, targetProteinGoal, getProteinCompletionPercentage()));
        sb.append(String.format("Total Carbohydrates (g),%.1f,Target,%.1f,Completion,%.1f%%\n",
                totalCarbs, targetCarbGoal, getCarbCompletionPercentage()));
        sb.append(String.format("Total Fat (g),%.1f,Target,%.1f,Completion,%.1f%%\n",
                totalFat, targetFatGoal, getFatCompletionPercentage()));

        if ("Weekly".equalsIgnoreCase(reportType)) {
            sb.append("\n# WEEKLY STATISTICAL ANALYSIS\n");
            sb.append(String.format("Average Daily Calories,%.1f kcal\n", averageDailyCalories));
            sb.append(String.format("Average Daily Protein,%.1f g\n", averageProtein));
            sb.append(String.format("Highest Calorie Day,%s,%.1f kcal\n",
                    highestCalorieDate != null ? highestCalorieDate.format(dtf) : "N/A", highestCalorieValue));
            sb.append(String.format("Lowest Calorie Day,%s,%.1f kcal\n",
                    lowestCalorieDate != null ? lowestCalorieDate.format(dtf) : "N/A", lowestCalorieValue));

            sb.append("\n# DAILY CALORIE TREND\n");
            sb.append("Date,Calories (kcal)\n");
            for (Map.Entry<LocalDate, Double> entry : dailyCalorieMap.entrySet()) {
                sb.append(entry.getKey().format(dtf)).append(",").append(String.format("%.1f", entry.getValue())).append("\n");
            }
        }

        if (mealEntries != null && !mealEntries.isEmpty()) {
            sb.append("\n# DETAILED MEAL LOGS\n");
            sb.append("Date,Meal Type,Food Name,Quantity (g),Calories (kcal),Protein (g),Carbs (g),Fat (g)\n");
            for (MealEntry m : mealEntries) {
                sb.append(m.getDate().format(dtf)).append(",")
                  .append("\"").append(m.getMealType()).append("\",")
                  .append("\"").append(m.getFoodName().replace("\"", "\"\"")).append("\",")
                  .append(String.format("%.1f", m.getQuantityGrams())).append(",")
                  .append(String.format("%.1f", m.getCalories())).append(",")
                  .append(String.format("%.1f", m.getProtein())).append(",")
                  .append(String.format("%.1f", m.getCarbs())).append(",")
                  .append(String.format("%.1f", m.getFat())).append("\n");
            }
        }

        return sb.toString();
    }

    /**
     * Generates a professionally formatted plain text (.txt) report.
     */
    public String toTxt() {
        StringBuilder sb = new StringBuilder();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MMM-yyyy");

        sb.append("========================================================================\n");
        sb.append("         DIETARY CALORIE INTAKE & NUTRITION LOG SYSTEM REPORT          \n");
        sb.append("========================================================================\n\n");
        sb.append("Report Type : ").append(reportType).append("\n");
        sb.append("Date Range  : ").append(startDate.format(dtf)).append(" to ").append(endDate.format(dtf)).append("\n");
        sb.append("Total Meals : ").append(totalMeals).append("\n\n");

        sb.append("------------------------------------------------------------------------\n");
        sb.append("NUTRITIONAL INTAKE SUMMARY\n");
        sb.append("------------------------------------------------------------------------\n");
        sb.append(String.format("  Calories      : %8.1f / %8.1f kcal  (%5.1f%% of goal)\n",
                totalCalories, targetCalorieGoal, getCalorieCompletionPercentage()));
        sb.append(String.format("  Protein       : %8.1f / %8.1f g     (%5.1f%% of goal)\n",
                totalProtein, targetProteinGoal, getProteinCompletionPercentage()));
        sb.append(String.format("  Carbohydrates : %8.1f / %8.1f g     (%5.1f%% of goal)\n",
                totalCarbs, targetCarbGoal, getCarbCompletionPercentage()));
        sb.append(String.format("  Fat           : %8.1f / %8.1f g     (%5.1f%% of goal)\n",
                totalFat, targetFatGoal, getFatCompletionPercentage()));
        sb.append("------------------------------------------------------------------------\n\n");

        if ("Weekly".equalsIgnoreCase(reportType)) {
            sb.append("------------------------------------------------------------------------\n");
            sb.append("7-DAY STATISTICAL HIGHLIGHTS\n");
            sb.append("------------------------------------------------------------------------\n");
            sb.append(String.format("  Average Daily Calories : %8.1f kcal\n", averageDailyCalories));
            sb.append(String.format("  Average Daily Protein  : %8.1f g\n", averageProtein));
            sb.append(String.format("  Highest Calorie Day    : %s (%8.1f kcal)\n",
                    highestCalorieDate != null ? highestCalorieDate.format(dtf) : "N/A", highestCalorieValue));
            sb.append(String.format("  Lowest Calorie Day     : %s (%8.1f kcal)\n",
                    lowestCalorieDate != null ? lowestCalorieDate.format(dtf) : "N/A", lowestCalorieValue));
            sb.append("------------------------------------------------------------------------\n\n");

            sb.append("DAILY CALORIE BREAKDOWN:\n");
            for (Map.Entry<LocalDate, Double> entry : dailyCalorieMap.entrySet()) {
                sb.append(String.format("  * %-12s : %6.1f kcal\n", entry.getKey().format(dtf), entry.getValue()));
            }
            sb.append("\n");
        }

        if (mealEntries != null && !mealEntries.isEmpty()) {
            sb.append("------------------------------------------------------------------------\n");
            sb.append(String.format("%-12s | %-10s | %-20s | %6s | %8s | %6s\n",
                    "Date", "Meal", "Food Name", "Qty(g)", "Calories", "Prot(g)"));
            sb.append("------------------------------------------------------------------------\n");
            for (MealEntry m : mealEntries) {
                String foodTruncated = m.getFoodName().length() > 20
                        ? m.getFoodName().substring(0, 17) + "..."
                        : m.getFoodName();
                sb.append(String.format("%-12s | %-10s | %-20s | %6.0f | %8.1f | %6.1f\n",
                        m.getDate().format(dtf),
                        m.getMealType(),
                        foodTruncated,
                        m.getQuantityGrams(),
                        m.getCalories(),
                        m.getProtein()));
            }
            sb.append("------------------------------------------------------------------------\n");
        }

        sb.append("\nGenerated by NutriTrack System. All nutritional metrics are estimated.\n");
        return sb.toString();
    }

    // --- Getters & Setters ---

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public double getTotalCalories() {
        return totalCalories;
    }

    public void setTotalCalories(double totalCalories) {
        this.totalCalories = totalCalories;
    }

    public double getTotalProtein() {
        return totalProtein;
    }

    public void setTotalProtein(double totalProtein) {
        this.totalProtein = totalProtein;
    }

    public double getTotalCarbs() {
        return totalCarbs;
    }

    public void setTotalCarbs(double totalCarbs) {
        this.totalCarbs = totalCarbs;
    }

    public double getTotalFat() {
        return totalFat;
    }

    public void setTotalFat(double totalFat) {
        this.totalFat = totalFat;
    }

    public int getTotalMeals() {
        return totalMeals;
    }

    public void setTotalMeals(int totalMeals) {
        this.totalMeals = totalMeals;
    }

    public double getTargetCalorieGoal() {
        return targetCalorieGoal;
    }

    public void setTargetCalorieGoal(double targetCalorieGoal) {
        this.targetCalorieGoal = targetCalorieGoal;
    }

    public double getTargetProteinGoal() {
        return targetProteinGoal;
    }

    public void setTargetProteinGoal(double targetProteinGoal) {
        this.targetProteinGoal = targetProteinGoal;
    }

    public double getTargetCarbGoal() {
        return targetCarbGoal;
    }

    public void setTargetCarbGoal(double targetCarbGoal) {
        this.targetCarbGoal = targetCarbGoal;
    }

    public double getTargetFatGoal() {
        return targetFatGoal;
    }

    public void setTargetFatGoal(double targetFatGoal) {
        this.targetFatGoal = targetFatGoal;
    }

    public double getAverageDailyCalories() {
        return averageDailyCalories;
    }

    public void setAverageDailyCalories(double averageDailyCalories) {
        this.averageDailyCalories = averageDailyCalories;
    }

    public double getAverageProtein() {
        return averageProtein;
    }

    public void setAverageProtein(double averageProtein) {
        this.averageProtein = averageProtein;
    }

    public LocalDate getHighestCalorieDate() {
        return highestCalorieDate;
    }

    public void setHighestCalorieDate(LocalDate highestCalorieDate) {
        this.highestCalorieDate = highestCalorieDate;
    }

    public double getHighestCalorieValue() {
        return highestCalorieValue;
    }

    public void setHighestCalorieValue(double highestCalorieValue) {
        this.highestCalorieValue = highestCalorieValue;
    }

    public LocalDate getLowestCalorieDate() {
        return lowestCalorieDate;
    }

    public void setLowestCalorieDate(LocalDate lowestCalorieDate) {
        this.lowestCalorieDate = lowestCalorieDate;
    }

    public double getLowestCalorieValue() {
        return lowestCalorieValue;
    }

    public void setLowestCalorieValue(double lowestCalorieValue) {
        this.lowestCalorieValue = lowestCalorieValue;
    }

    public Map<LocalDate, Double> getDailyCalorieMap() {
        return dailyCalorieMap;
    }

    public void setDailyCalorieMap(Map<LocalDate, Double> dailyCalorieMap) {
        this.dailyCalorieMap = dailyCalorieMap;
    }

    public List<MealEntry> getMealEntries() {
        return mealEntries != null ? mealEntries : Collections.emptyList();
    }

    public void setMealEntries(List<MealEntry> mealEntries) {
        this.mealEntries = mealEntries;
    }
}
