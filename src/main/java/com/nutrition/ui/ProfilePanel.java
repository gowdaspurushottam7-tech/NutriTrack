package com.nutrition.ui;

import com.nutrition.model.User;
import com.nutrition.service.INutritionService;
import com.nutrition.ui.components.CardPanel;
import com.nutrition.ui.components.ModernButton;
import com.nutrition.ui.theme.UITheme;

import javax.swing.*;
import java.awt.*;

/**
 * User Profile & Targets Screen: Manage demographic information, biometrics,
 * and daily calorie/macronutrient target goals.
 */
public class ProfilePanel extends BasePanel {

    private JTextField nameField;
    private JTextField ageField;
    private JComboBox<String> genderCombo;
    private JTextField heightField;
    private JTextField weightField;

    private JTextField calorieGoalField;
    private JTextField proteinGoalField;
    private JTextField carbGoalField;
    private JTextField fatGoalField;

    // Computed biometric display labels
    private JLabel bmiDisplayLabel;
    private JLabel bmiCategoryLabel;
    private JLabel bmrDisplayLabel;

    public ProfilePanel(INutritionService service, DashboardFrame mainFrame) {
        super(service, mainFrame);
        initComponents();
        refreshData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 16));

        JPanel header = createHeader("User Profile & Health Targets",
                "Configure your biometrics, demographic profile, and customized nutritional targets.");
        add(header, BorderLayout.NORTH);

        JPanel centerContainer = new JPanel(new GridLayout(1, 2, 20, 0));
        centerContainer.setOpaque(false);

        // Left Card: Personal & Biometric Info
        CardPanel personalCard = new CardPanel();
        personalCard.setLayout(new BoxLayout(personalCard, BoxLayout.Y_AXIS));

        JLabel titlePersonal = new JLabel("👤 Personal Demographics");
        titlePersonal.setFont(UITheme.FONT_SUBHEADER);
        titlePersonal.setForeground(UITheme.TEXT_PRIMARY);
        personalCard.add(titlePersonal);
        personalCard.add(Box.createRigidArea(new Dimension(0, 16)));

        nameField = new JTextField();
        ageField = new JTextField();
        genderCombo = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        heightField = new JTextField();
        weightField = new JTextField();

        personalCard.add(createLabeledField("Full Name:", nameField));
        personalCard.add(Box.createRigidArea(new Dimension(0, 10)));
        personalCard.add(createLabeledField("Age (1-120 years):", ageField));
        personalCard.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel genderGroup = new JPanel();
        genderGroup.setOpaque(false);
        genderGroup.setLayout(new BoxLayout(genderGroup, BoxLayout.Y_AXIS));
        JLabel lblGen = new JLabel("Gender:");
        lblGen.setFont(UITheme.FONT_MEDIUM);
        genderGroup.add(lblGen);
        genderGroup.add(Box.createRigidArea(new Dimension(0, 4)));
        genderGroup.add(genderCombo);
        personalCard.add(genderGroup);

        personalCard.add(Box.createRigidArea(new Dimension(0, 10)));
        personalCard.add(createLabeledField("Height (cm):", heightField));
        personalCard.add(Box.createRigidArea(new Dimension(0, 10)));
        personalCard.add(createLabeledField("Weight (kg):", weightField));

        personalCard.add(Box.createRigidArea(new Dimension(0, 16)));
        JPanel biometricsBox = createBiometricsSummaryBox();
        personalCard.add(biometricsBox);

        centerContainer.add(personalCard);

        // Right Card: Daily Nutritional Goals
        CardPanel goalsCard = new CardPanel();
        goalsCard.setLayout(new BoxLayout(goalsCard, BoxLayout.Y_AXIS));

        JLabel titleGoals = new JLabel("🎯 Daily Nutrition Targets");
        titleGoals.setFont(UITheme.FONT_SUBHEADER);
        titleGoals.setForeground(UITheme.TEXT_PRIMARY);
        goalsCard.add(titleGoals);
        goalsCard.add(Box.createRigidArea(new Dimension(0, 16)));

        calorieGoalField = new JTextField();
        proteinGoalField = new JTextField();
        carbGoalField = new JTextField();
        fatGoalField = new JTextField();

        goalsCard.add(createLabeledField("Daily Calorie Target (kcal):", calorieGoalField));
        goalsCard.add(Box.createRigidArea(new Dimension(0, 12)));
        goalsCard.add(createLabeledField("Daily Protein Target (grams):", proteinGoalField));
        goalsCard.add(Box.createRigidArea(new Dimension(0, 12)));
        goalsCard.add(createLabeledField("Daily Carbohydrate Target (grams):", carbGoalField));
        goalsCard.add(Box.createRigidArea(new Dimension(0, 12)));
        goalsCard.add(createLabeledField("Daily Fat Target (grams):", fatGoalField));

        goalsCard.add(Box.createRigidArea(new Dimension(0, 24)));

        // Action Buttons
        JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionRow.setOpaque(false);

        ModernButton autoCalcBtn = new ModernButton("⚡ Auto-Recommend Goals", ModernButton.Style.OUTLINE);
        autoCalcBtn.setToolTipText("Estimates targets based on Harris-Benedict / Mifflin BMR");
        autoCalcBtn.addActionListener(e -> autoCalculateRecommendedGoals());

        ModernButton saveBtn = new ModernButton("💾 Save Profile & Goals", ModernButton.Style.PRIMARY);
        saveBtn.addActionListener(e -> handleSaveProfile());

        actionRow.add(autoCalcBtn);
        actionRow.add(saveBtn);
        goalsCard.add(actionRow);

        centerContainer.add(goalsCard);

        JScrollPane scroll = new JScrollPane(centerContainer);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        add(scroll, BorderLayout.CENTER);
    }

    private JPanel createLabeledField(String labelText, JTextField field) {
        JPanel group = new JPanel();
        group.setOpaque(false);
        group.setLayout(new BoxLayout(group, BoxLayout.Y_AXIS));

        JLabel label = new JLabel(labelText);
        label.setFont(UITheme.FONT_MEDIUM);
        label.setForeground(UITheme.TEXT_PRIMARY);

        field.setFont(UITheme.FONT_REGULAR);
        field.setPreferredSize(new Dimension(300, 34));

        group.add(label);
        group.add(Box.createRigidArea(new Dimension(0, 4)));
        group.add(field);
        return group;
    }

    private JPanel createBiometricsSummaryBox() {
        JPanel box = new JPanel(new GridLayout(3, 1, 6, 6));
        box.setBackground(new Color(241, 245, 249));
        box.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.CARD_BORDER),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));

        bmiDisplayLabel = new JLabel("Body Mass Index (BMI): -");
        bmiDisplayLabel.setFont(UITheme.FONT_MEDIUM);

        bmiCategoryLabel = new JLabel("Category: -");
        bmiCategoryLabel.setFont(UITheme.FONT_REGULAR);
        bmiCategoryLabel.setForeground(UITheme.TEXT_SECONDARY);

        bmrDisplayLabel = new JLabel("Basal Metabolic Rate (BMR): - kcal/day");
        bmrDisplayLabel.setFont(UITheme.FONT_REGULAR);
        bmrDisplayLabel.setForeground(UITheme.TEXT_SECONDARY);

        box.add(bmiDisplayLabel);
        box.add(bmiCategoryLabel);
        box.add(bmrDisplayLabel);
        return box;
    }

    private void autoCalculateRecommendedGoals() {
        try {
            int age = Integer.parseInt(ageField.getText().trim());
            double height = Double.parseDouble(heightField.getText().trim());
            double weight = Double.parseDouble(weightField.getText().trim());
            String gender = (String) genderCombo.getSelectedItem();

            if (age <= 0 || height <= 0 || weight <= 0) {
                showError("Please enter valid age, height, and weight first.");
                return;
            }

            User temp = new User("Temp", age, gender, height, weight, 2000, 100, 200, 60);
            double bmr = temp.calculateBMR();
            // Moderate activity multiplier 1.35
            double tdee = bmr * 1.35;

            // Macro split: 50% carbs, 25% protein, 25% fat
            double proteinGrams = (tdee * 0.25) / 4.0;
            double carbGrams = (tdee * 0.50) / 4.0;
            double fatGrams = (tdee * 0.25) / 9.0;

            calorieGoalField.setText(String.format("%.0f", tdee));
            proteinGoalField.setText(String.format("%.0f", proteinGrams));
            carbGoalField.setText(String.format("%.0f", carbGrams));
            fatGoalField.setText(String.format("%.0f", fatGrams));

            showSuccess(String.format("Calculated estimated daily maintenance goals:\nCalories: %.0f kcal\nProtein: %.0f g\nCarbs: %.0f g\nFat: %.0f g",
                    tdee, proteinGrams, carbGrams, fatGrams));
        } catch (NumberFormatException e) {
            showError("Please ensure age, height, and weight are valid numbers before auto-calculating.");
        }
    }

    private void handleSaveProfile() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            showError("Name cannot be empty.");
            nameField.requestFocus();
            return;
        }

        int age;
        try {
            age = Integer.parseInt(ageField.getText().trim());
            if (age < 1 || age > 120) {
                showError("Age must be between 1 and 120 years.");
                ageField.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            showError("Age must be a valid whole number.");
            ageField.requestFocus();
            return;
        }

        double height;
        try {
            height = Double.parseDouble(heightField.getText().trim());
            if (height <= 0 || height > 300) {
                showError("Height must be a positive number up to 300 cm.");
                heightField.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            showError("Height must be a valid decimal number.");
            heightField.requestFocus();
            return;
        }

        double weight;
        try {
            weight = Double.parseDouble(weightField.getText().trim());
            if (weight <= 0 || weight > 500) {
                showError("Weight must be a positive number up to 500 kg.");
                weightField.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            showError("Weight must be a valid decimal number.");
            weightField.requestFocus();
            return;
        }

        double calGoal;
        double proGoal;
        double carbGoal;
        double fatGoal;

        try {
            calGoal = Double.parseDouble(calorieGoalField.getText().trim());
            proGoal = Double.parseDouble(proteinGoalField.getText().trim());
            carbGoal = Double.parseDouble(carbGoalField.getText().trim());
            fatGoal = Double.parseDouble(fatGoalField.getText().trim());

            if (calGoal <= 0 || proGoal < 0 || carbGoal < 0 || fatGoal < 0) {
                showError("Calorie target must be positive and macronutrient targets cannot be negative.");
                return;
            }
        } catch (NumberFormatException e) {
            showError("Nutritional goals must be valid numbers.");
            return;
        }

        String gender = (String) genderCombo.getSelectedItem();
        User updatedUser = new User(name, age, gender, height, weight, calGoal, proGoal, carbGoal, fatGoal);

        String validationError = updatedUser.validate();
        if (validationError != null) {
            showError(validationError);
            return;
        }

        service.updateUser(updatedUser);
        mainFrame.refreshAllPanels();
        refreshData();
        showSuccess("Profile and nutrition goals saved successfully!");
    }

    @Override
    public void refreshData() {
        User user = service.getUser();
        if (user != null) {
            nameField.setText(user.getName());
            ageField.setText(String.valueOf(user.getAge()));
            genderCombo.setSelectedItem(user.getGender());
            heightField.setText(String.format("%.1f", user.getHeightCm()));
            weightField.setText(String.format("%.1f", user.getWeightKg()));

            calorieGoalField.setText(String.format("%.0f", user.getDailyCalorieGoal()));
            proteinGoalField.setText(String.format("%.1f", user.getDailyProteinGoal()));
            carbGoalField.setText(String.format("%.1f", user.getDailyCarbGoal()));
            fatGoalField.setText(String.format("%.1f", user.getDailyFatGoal()));

            double bmi = user.calculateBMI();
            bmiDisplayLabel.setText(String.format("Body Mass Index (BMI): %.1f", bmi));
            bmiCategoryLabel.setText("Category: " + user.getBMICategory());
            bmrDisplayLabel.setText(String.format("Basal Metabolic Rate (BMR): %.0f kcal/day", user.calculateBMR()));
        }
    }
}
