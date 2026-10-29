package com.nutrition.model;

import java.io.Serializable;

/**
 * Model class representing the User Profile and their daily nutritional targets.
 * Demonstrates Object-Oriented Principles: Encapsulation and Data Validation.
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private int age;
    private String gender; // "Male", "Female", "Other"
    private double heightCm;
    private double weightKg;
    private double dailyCalorieGoal;
    private double dailyProteinGoal;
    private double dailyCarbGoal;
    private double dailyFatGoal;

    /**
     * Default constructor with sensible healthy defaults.
     */
    public User() {
        this.name = "Purushottam";
        this.age = 20;
        this.gender = "Male";
        this.heightCm = 175.0;
        this.weightKg = 70.0;
        this.dailyCalorieGoal = 2200.0;
        this.dailyProteinGoal = 120.0;
        this.dailyCarbGoal = 250.0;
        this.dailyFatGoal = 65.0;
    }

    /**
     * Parameterized constructor.
     */
    public User(String name, int age, String gender, double heightCm, double weightKg,
                double dailyCalorieGoal, double dailyProteinGoal, double dailyCarbGoal, double dailyFatGoal) {
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.heightCm = heightCm;
        this.weightKg = weightKg;
        this.dailyCalorieGoal = dailyCalorieGoal;
        this.dailyProteinGoal = dailyProteinGoal;
        this.dailyCarbGoal = dailyCarbGoal;
        this.dailyFatGoal = dailyFatGoal;
    }

    // --- Validation Methods ---

    /**
     * Validates whether all profile fields conform to the domain rules.
     * @return null if valid, or an error message string if invalid.
     */
    public String validate() {
        if (name == null || name.trim().isEmpty()) {
            return "Name cannot be empty.";
        }
        if (age < 1 || age > 120) {
            return "Age must be between 1 and 120 years.";
        }
        if (heightCm <= 0 || heightCm > 300) {
            return "Height must be a positive number up to 300 cm.";
        }
        if (weightKg <= 0 || weightKg > 500) {
            return "Weight must be a positive number up to 500 kg.";
        }
        if (dailyCalorieGoal <= 0) {
            return "Daily calorie goal must be greater than zero.";
        }
        if (dailyProteinGoal < 0 || dailyCarbGoal < 0 || dailyFatGoal < 0) {
            return "Nutrient goals cannot be negative.";
        }
        return null;
    }

    // --- Health Metrics Helpers ---

    /**
     * Calculates Body Mass Index (BMI).
     * BMI = weight (kg) / (height in meters)^2
     */
    public double calculateBMI() {
        if (heightCm <= 0) return 0.0;
        double heightM = heightCm / 100.0;
        return weightKg / (heightM * heightM);
    }

    /**
     * Returns standard BMI Category.
     */
    public String getBMICategory() {
        double bmi = calculateBMI();
        if (bmi < 18.5) return "Underweight";
        if (bmi < 24.9) return "Normal weight";
        if (bmi < 29.9) return "Overweight";
        return "Obese";
    }

    /**
     * Estimates Basal Metabolic Rate (BMR) using Mifflin-St Jeor Equation.
     */
    public double calculateBMR() {
        if ("Female".equalsIgnoreCase(gender)) {
            return (10 * weightKg) + (6.25 * heightCm) - (5 * age) - 161;
        } else {
            return (10 * weightKg) + (6.25 * heightCm) - (5 * age) + 5;
        }
    }

    // --- Getters and Setters (Encapsulation) ---

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public double getHeightCm() {
        return heightCm;
    }

    public void setHeightCm(double heightCm) {
        this.heightCm = heightCm;
    }

    public double getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(double weightKg) {
        this.weightKg = weightKg;
    }

    public double getDailyCalorieGoal() {
        return dailyCalorieGoal;
    }

    public void setDailyCalorieGoal(double dailyCalorieGoal) {
        this.dailyCalorieGoal = dailyCalorieGoal;
    }

    public double getDailyProteinGoal() {
        return dailyProteinGoal;
    }

    public void setDailyProteinGoal(double dailyProteinGoal) {
        this.dailyProteinGoal = dailyProteinGoal;
    }

    public double getDailyCarbGoal() {
        return dailyCarbGoal;
    }

    public void setDailyCarbGoal(double dailyCarbGoal) {
        this.dailyCarbGoal = dailyCarbGoal;
    }

    public double getDailyFatGoal() {
        return dailyFatGoal;
    }

    public void setDailyFatGoal(double dailyFatGoal) {
        this.dailyFatGoal = dailyFatGoal;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", gender='" + gender + '\'' +
                ", heightCm=" + heightCm +
                ", weightKg=" + weightKg +
                ", dailyCalorieGoal=" + dailyCalorieGoal +
                '}';
    }
}
