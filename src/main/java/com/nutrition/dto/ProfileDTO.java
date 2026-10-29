package com.nutrition.dto;

public class ProfileDTO {
    private String name;
    private int age;
    private String gender;
    private double heightCm;
    private double weightKg;
    private double dailyCalorieGoal;
    private double dailyProteinGoal;
    private double dailyCarbGoal;
    private double dailyFatGoal;

    // Derived
    private double bmi;
    private String bmiCategory;
    private double bmr;

    public ProfileDTO() {
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public double getHeightCm() { return heightCm; }
    public void setHeightCm(double heightCm) { this.heightCm = heightCm; }

    public double getWeightKg() { return weightKg; }
    public void setWeightKg(double weightKg) { this.weightKg = weightKg; }

    public double getDailyCalorieGoal() { return dailyCalorieGoal; }
    public void setDailyCalorieGoal(double dailyCalorieGoal) { this.dailyCalorieGoal = dailyCalorieGoal; }

    public double getDailyProteinGoal() { return dailyProteinGoal; }
    public void setDailyProteinGoal(double dailyProteinGoal) { this.dailyProteinGoal = dailyProteinGoal; }

    public double getDailyCarbGoal() { return dailyCarbGoal; }
    public void setDailyCarbGoal(double dailyCarbGoal) { this.dailyCarbGoal = dailyCarbGoal; }

    public double getDailyFatGoal() { return dailyFatGoal; }
    public void setDailyFatGoal(double dailyFatGoal) { this.dailyFatGoal = dailyFatGoal; }

    public double getBmi() { return bmi; }
    public void setBmi(double bmi) { this.bmi = bmi; }

    public String getBmiCategory() { return bmiCategory; }
    public void setBmiCategory(String bmiCategory) { this.bmiCategory = bmiCategory; }

    public double getBmr() { return bmr; }
    public void setBmr(double bmr) { this.bmr = bmr; }
}
