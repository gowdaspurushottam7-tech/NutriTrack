package com.nutrition.controller;

import com.nutrition.dto.ApiResponse;
import com.nutrition.dto.MealRequestDTO;
import com.nutrition.model.Food;
import com.nutrition.model.MealEntry;
import com.nutrition.service.INutritionService;
import com.nutrition.service.NutritionCalculator;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api")
public class MealController {

    private final INutritionService service;

    public MealController(INutritionService service) {
        this.service = service;
    }

    @GetMapping("/meals")
    public ResponseEntity<ApiResponse<ArrayList<MealEntry>>> getMeals(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) String type) {
        LocalDate targetDate = (date != null) ? date : LocalDate.now();
        ArrayList<MealEntry> list = service.getMealsForDateAndType(targetDate, type);
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @PostMapping("/meals")
    public ResponseEntity<ApiResponse<MealEntry>> addMeal(@RequestBody MealRequestDTO request) {
        if (request == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Meal request data is missing."));
        }

        Food food = null;
        if (request.getFoodId() != null && !request.getFoodId().trim().isEmpty()) {
            food = service.getFoodDatabase().findById(request.getFoodId());
        }
        if (food == null && request.getFoodName() != null) {
            food = service.getFoodDatabase().findByName(request.getFoodName());
        }

        if (food == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Selected food item not found in database."));
        }

        if (request.getQuantityGrams() <= 0) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Quantity must be greater than zero grams."));
        }

        LocalDate mealDate = (request.getDate() != null) ? request.getDate() : LocalDate.now();
        String mealType = (request.getMealType() != null) ? request.getMealType() : MealEntry.MEAL_BREAKFAST;

        MealEntry entry = new MealEntry(mealDate, mealType, food, request.getQuantityGrams());
        String error = service.addMealEntry(entry);
        if (error != null) {
            return ResponseEntity.badRequest().body(ApiResponse.error(error));
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Meal logged successfully.", entry));
    }

    @PutMapping("/meals/{id}")
    public ResponseEntity<ApiResponse<MealEntry>> updateMeal(
            @PathVariable String id,
            @RequestBody MealRequestDTO request) {
        MealEntry existing = service.getMealEntryById(id);
        if (existing == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Meal entry with ID '" + id + "' not found."));
        }

        if (request.getQuantityGrams() <= 0) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Quantity must be greater than zero grams."));
        }

        if (request.getDate() != null) {
            existing.setDate(request.getDate());
        }
        if (request.getMealType() != null) {
            existing.setMealType(request.getMealType());
        }

        existing.setQuantityGrams(request.getQuantityGrams());

        // Recalculate using food item
        Food food = service.getFoodDatabase().findByName(existing.getFoodName());
        if (food != null) {
            existing.recalculateNutrition(food);
        } else {
            double ratio = request.getQuantityGrams() / existing.getQuantityGrams();
            existing.setCalories(existing.getCalories() * ratio);
            existing.setProtein(existing.getProtein() * ratio);
            existing.setCarbs(existing.getCarbs() * ratio);
            existing.setFat(existing.getFat() * ratio);
        }

        String error = service.updateMealEntry(existing);
        if (error != null) {
            return ResponseEntity.badRequest().body(ApiResponse.error(error));
        }

        return ResponseEntity.ok(ApiResponse.success("Meal record updated successfully.", existing));
    }

    @DeleteMapping("/meals/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMeal(@PathVariable String id) {
        boolean removed = service.deleteMealEntry(id);
        if (!removed) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Meal entry not found or already deleted."));
        }
        return ResponseEntity.ok(ApiResponse.success("Meal record deleted successfully.", null));
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getHistory(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) String type) {
        LocalDate targetDate = (date != null) ? date : LocalDate.now();
        ArrayList<MealEntry> entries = service.getMealsForDateAndType(targetDate, type);

        double totalCalories = NutritionCalculator.sumCalories(entries);
        double totalProtein = NutritionCalculator.sumProtein(entries);
        double totalCarbs = NutritionCalculator.sumCarbs(entries);
        double totalFat = NutritionCalculator.sumFat(entries);

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("date", targetDate);
        responseData.put("selectedType", (type != null && !type.isEmpty()) ? type : "All");
        responseData.put("entries", entries);
        responseData.put("totalMeals", entries.size());
        responseData.put("totalCalories", totalCalories);
        responseData.put("totalProtein", totalProtein);
        responseData.put("totalCarbs", totalCarbs);
        responseData.put("totalFat", totalFat);

        return ResponseEntity.ok(ApiResponse.success(responseData));
    }
}
