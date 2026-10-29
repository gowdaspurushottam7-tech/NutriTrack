package com.nutrition.controller;

import com.nutrition.dto.ApiResponse;
import com.nutrition.model.NutritionReport;
import com.nutrition.service.INutritionService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final INutritionService service;

    public ReportController(INutritionService service) {
        this.service = service;
    }

    @GetMapping("/daily")
    public ResponseEntity<ApiResponse<NutritionReport>> getDailyReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        LocalDate target = (date != null) ? date : LocalDate.now();
        NutritionReport report = service.generateDailyReport(target);
        return ResponseEntity.ok(ApiResponse.success(report));
    }

    @GetMapping("/weekly")
    public ResponseEntity<ApiResponse<NutritionReport>> getWeeklyReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        LocalDate target = (endDate != null) ? endDate : LocalDate.now();
        NutritionReport report = service.generateWeeklyReport(target);
        return ResponseEntity.ok(ApiResponse.success(report));
    }

    @GetMapping("/export/csv")
    public ResponseEntity<byte[]> exportCsv(
            @RequestParam(defaultValue = "weekly") String type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        LocalDate target = (date != null) ? date : LocalDate.now();
        NutritionReport report = "daily".equalsIgnoreCase(type)
                ? service.generateDailyReport(target)
                : service.generateWeeklyReport(target);

        byte[] csvBytes = report.toCsv().getBytes(StandardCharsets.UTF_8);
        String filename = "Nutrition_Report_" + type + "_" + target + ".csv";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(csvBytes);
    }

    @GetMapping("/export/txt")
    public ResponseEntity<byte[]> exportTxt(
            @RequestParam(defaultValue = "weekly") String type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        LocalDate target = (date != null) ? date : LocalDate.now();
        NutritionReport report = "daily".equalsIgnoreCase(type)
                ? service.generateDailyReport(target)
                : service.generateWeeklyReport(target);

        byte[] txtBytes = report.toTxt().getBytes(StandardCharsets.UTF_8);
        String filename = "Nutrition_Report_" + type + "_" + target + ".txt";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("text/plain; charset=UTF-8"))
                .body(txtBytes);
    }
}
