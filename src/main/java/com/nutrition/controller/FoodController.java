package com.nutrition.controller;

import com.nutrition.dto.ApiResponse;
import com.nutrition.model.Food;
import com.nutrition.service.INutritionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/api/foods")
public class FoodController {

    private final INutritionService service;

    public FoodController(INutritionService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<ArrayList<Food>>> getFoods(
            @RequestParam(required = false) String query) {
        ArrayList<Food> list = service.getFoodDatabase().searchFoods(query);
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Food>> getFoodById(@PathVariable String id) {
        Food food = service.getFoodDatabase().findById(id);
        if (food == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Food item with ID '" + id + "' not found."));
        }
        return ResponseEntity.ok(ApiResponse.success(food));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Food>> createCustomFood(@RequestBody Food food) {
        if (food == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Food request body is required."));
        }
        String error = service.getFoodDatabase().addCustomFood(food);
        if (error != null) {
            return ResponseEntity.badRequest().body(ApiResponse.error(error));
        }
        service.saveAllData();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Custom food added successfully.", food));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Food>> updateCustomFood(
            @PathVariable String id,
            @RequestBody Food food) {
        if (food == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Food request body is required."));
        }
        food.setId(id);
        String error = service.getFoodDatabase().updateCustomFood(food);
        if (error != null) {
            return ResponseEntity.badRequest().body(ApiResponse.error(error));
        }
        service.saveAllData();
        return ResponseEntity.ok(ApiResponse.success("Custom food updated successfully.", food));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCustomFood(@PathVariable String id) {
        String error = service.getFoodDatabase().deleteCustomFood(id);
        if (error != null) {
            return ResponseEntity.badRequest().body(ApiResponse.error(error));
        }
        service.saveAllData();
        return ResponseEntity.ok(ApiResponse.success("Food item removed successfully.", null));
    }
}
