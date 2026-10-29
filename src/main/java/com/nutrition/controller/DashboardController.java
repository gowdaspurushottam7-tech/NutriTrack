package com.nutrition.controller;

import com.nutrition.dto.ApiResponse;
import com.nutrition.dto.DashboardDTO;
import com.nutrition.model.MealEntry;
import com.nutrition.model.NutritionReport;
import com.nutrition.model.User;
import com.nutrition.service.INutritionService;
import com.nutrition.service.NutritionCalculator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final INutritionService service;

    public DashboardController(INutritionService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<DashboardDTO>> getDashboard() {
        User user = service.getUser();
        if (user == null) {
            user = new User();
        }

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        String greetingText;
        if (now.getHour() < 12) {
            greetingText = "Good Morning";
        } else if (now.getHour() < 17) {
            greetingText = "Good Afternoon";
        } else {
            greetingText = "Good Evening";
        }

        double calConsumed = service.getCaloriesConsumedToday();
        double calGoal = user.getDailyCalorieGoal();
        double calRemaining = NutritionCalculator.calculateRemaining(calConsumed, calGoal);
        double calPct = NutritionCalculator.calculateCompletionPercentage(calConsumed, calGoal);

        double proConsumed = service.getProteinConsumedToday();
        double proGoal = user.getDailyProteinGoal();
        double proPct = NutritionCalculator.calculateCompletionPercentage(proConsumed, proGoal);

        double carbConsumed = service.getCarbsConsumedToday();
        double carbGoal = user.getDailyCarbGoal();
        double carbPct = NutritionCalculator.calculateCompletionPercentage(carbConsumed, carbGoal);

        double fatConsumed = service.getFatConsumedToday();
        double fatGoal = user.getDailyFatGoal();
        double fatPct = NutritionCalculator.calculateCompletionPercentage(fatConsumed, fatGoal);

        DashboardDTO dto = new DashboardDTO();
        dto.setGreeting(greetingText);
        dto.setUserName(user.getName());
        dto.setCurrentDate(today);

        dto.setCaloriesConsumed(calConsumed);
        dto.setCalorieGoal(calGoal);
        dto.setRemainingCalories(calRemaining);
        dto.setCalorieCompletionPercentage(calPct);

        dto.setProteinConsumed(proConsumed);
        dto.setProteinGoal(proGoal);
        dto.setProteinCompletionPercentage(proPct);

        dto.setCarbsConsumed(carbConsumed);
        dto.setCarbGoal(carbGoal);
        dto.setCarbCompletionPercentage(carbPct);

        dto.setFatConsumed(fatConsumed);
        dto.setFatGoal(fatGoal);
        dto.setFatCompletionPercentage(fatPct);

        dto.setBreakfastCount(service.getMealCountForToday(MealEntry.MEAL_BREAKFAST));
        dto.setLunchCount(service.getMealCountForToday(MealEntry.MEAL_LUNCH));
        dto.setDinnerCount(service.getMealCountForToday(MealEntry.MEAL_DINNER));
        dto.setSnacksCount(service.getMealCountForToday(MealEntry.MEAL_SNACKS));
        dto.setTotalMealsToday(service.getTotalMealsToday());

        dto.setBmi(user.calculateBMI());
        dto.setBmiCategory(user.getBMICategory());
        dto.setBmr(user.calculateBMR());

        dto.setRecentMeals(service.getRecentMeals(5));

        // 7-day calorie trend for the chart
        NutritionReport weekly = service.generateWeeklyReport(today);
        Map<String, Double> formattedTrend = new LinkedHashMap<>();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("EEE dd");
        for (Map.Entry<LocalDate, Double> entry : weekly.getDailyCalorieMap().entrySet()) {
            formattedTrend.put(entry.getKey().format(dtf), entry.getValue());
        }
        dto.setWeeklyCalories(formattedTrend);

        return ResponseEntity.ok(ApiResponse.success(dto));
    }
}
