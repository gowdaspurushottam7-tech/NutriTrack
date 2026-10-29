package com.nutrition.controller;

import com.nutrition.dto.ApiResponse;
import com.nutrition.dto.ProfileDTO;
import com.nutrition.model.User;
import com.nutrition.service.INutritionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final INutritionService service;

    public ProfileController(INutritionService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<ProfileDTO>> getProfile() {
        User user = service.getUser();
        if (user == null) {
            user = new User();
        }

        ProfileDTO dto = new ProfileDTO();
        dto.setName(user.getName());
        dto.setAge(user.getAge());
        dto.setGender(user.getGender());
        dto.setHeightCm(user.getHeightCm());
        dto.setWeightKg(user.getWeightKg());

        dto.setDailyCalorieGoal(user.getDailyCalorieGoal());
        dto.setDailyProteinGoal(user.getDailyProteinGoal());
        dto.setDailyCarbGoal(user.getDailyCarbGoal());
        dto.setDailyFatGoal(user.getDailyFatGoal());

        dto.setBmi(user.calculateBMI());
        dto.setBmiCategory(user.getBMICategory());
        dto.setBmr(user.calculateBMR());

        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<ProfileDTO>> updateProfile(@RequestBody ProfileDTO request) {
        if (request == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Profile data is required."));
        }

        User user = new User(
                request.getName(),
                request.getAge(),
                request.getGender(),
                request.getHeightCm(),
                request.getWeightKg(),
                request.getDailyCalorieGoal(),
                request.getDailyProteinGoal(),
                request.getDailyCarbGoal(),
                request.getDailyFatGoal()
        );

        String error = user.validate();
        if (error != null) {
            return ResponseEntity.badRequest().body(ApiResponse.error(error));
        }

        service.updateUser(user);

        request.setBmi(user.calculateBMI());
        request.setBmiCategory(user.getBMICategory());
        request.setBmr(user.calculateBMR());

        return ResponseEntity.ok(ApiResponse.success("Profile and goals updated successfully.", request));
    }
}
