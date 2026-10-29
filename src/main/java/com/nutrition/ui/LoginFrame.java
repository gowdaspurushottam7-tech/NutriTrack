package com.nutrition.ui;

import com.nutrition.model.User;
import com.nutrition.service.INutritionService;
import com.nutrition.ui.components.CardPanel;
import com.nutrition.ui.components.ModernButton;
import com.nutrition.ui.theme.UITheme;

import javax.swing.*;
import java.awt.*;

/**
 * Entrance Screen for profile setup and user authentication/onboarding.
 */
public class LoginFrame extends JFrame {
    private final INutritionService service;

    private JTextField nameField;
    private JTextField ageField;
    private JComboBox<String> genderCombo;
    private JTextField heightField;
    private JTextField weightField;
    private JTextField calorieGoalField;

    public LoginFrame(INutritionService service) {
        super("NutriTrack - Setup & Profile Login");
        this.service = service;

        initWindow();
        initComponents();
    }

    private void initWindow() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(520, 680);
        setResizable(false);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BG_MAIN);
        root.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        CardPanel card = new CardPanel(20);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        // Header
        JLabel logo = new JLabel("🥗 NutriTrack");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        logo.setForeground(UITheme.PRIMARY_DARK);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Dietary Calorie Intake & Nutrition Log System");
        subtitle.setFont(UITheme.FONT_SMALL);
        subtitle.setForeground(UITheme.TEXT_SECONDARY);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(logo);
        card.add(Box.createRigidArea(new Dimension(0, 4)));
        card.add(subtitle);
        card.add(Box.createRigidArea(new Dimension(0, 20)));

        User existing = service.getUser();
        String initName = existing != null ? existing.getName() : "Purushottam";
        int initAge = existing != null ? existing.getAge() : 20;
        String initGender = existing != null ? existing.getGender() : "Male";
        double initHeight = existing != null ? existing.getHeightCm() : 175.0;
        double initWeight = existing != null ? existing.getWeightKg() : 70.0;
        double initGoal = existing != null ? existing.getDailyCalorieGoal() : 2200.0;

        nameField = new JTextField(initName);
        ageField = new JTextField(String.valueOf(initAge));
        genderCombo = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        genderCombo.setSelectedItem(initGender);
        heightField = new JTextField(String.valueOf(initHeight));
        weightField = new JTextField(String.valueOf(initWeight));
        calorieGoalField = new JTextField(String.format("%.0f", initGoal));

        card.add(createFieldRow("Your Full Name:", nameField));
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(createFieldRow("Age (Years):", ageField));
        card.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel gGroup = new JPanel();
        gGroup.setOpaque(false);
        gGroup.setLayout(new BoxLayout(gGroup, BoxLayout.Y_AXIS));
        JLabel gLbl = new JLabel("Gender:");
        gLbl.setFont(UITheme.FONT_MEDIUM);
        gGroup.add(gLbl);
        gGroup.add(Box.createRigidArea(new Dimension(0, 4)));
        genderCombo.setPreferredSize(new Dimension(420, 34));
        gGroup.add(genderCombo);
        card.add(gGroup);

        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(createFieldRow("Height (cm):", heightField));
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(createFieldRow("Weight (kg):", weightField));
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(createFieldRow("Daily Calorie Target (kcal):", calorieGoalField));

        card.add(Box.createRigidArea(new Dimension(0, 20)));

        // Action Buttons
        ModernButton loginBtn = new ModernButton("✓ Continue to Dashboard", ModernButton.Style.PRIMARY);
        loginBtn.setMaximumSize(new Dimension(440, 42));
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginBtn.addActionListener(e -> handleLogin());

        ModernButton quickBtn = new ModernButton("⚡ Quick Start with Defaults", ModernButton.Style.OUTLINE);
        quickBtn.setMaximumSize(new Dimension(440, 38));
        quickBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        quickBtn.addActionListener(e -> launchDashboard());

        card.add(loginBtn);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(quickBtn);

        root.add(card, BorderLayout.CENTER);
        setContentPane(root);
    }

    private JPanel createFieldRow(String labelText, JTextField field) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(UITheme.FONT_MEDIUM);
        lbl.setForeground(UITheme.TEXT_PRIMARY);

        field.setFont(UITheme.FONT_REGULAR);
        field.setPreferredSize(new Dimension(420, 34));
        field.setMaximumSize(new Dimension(420, 34));

        panel.add(lbl);
        panel.add(Box.createRigidArea(new Dimension(0, 4)));
        panel.add(field);
        return panel;
    }

    private void handleLogin() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name cannot be empty.", "Validation", JOptionPane.ERROR_MESSAGE);
            nameField.requestFocus();
            return;
        }

        try {
            int age = Integer.parseInt(ageField.getText().trim());
            double height = Double.parseDouble(heightField.getText().trim());
            double weight = Double.parseDouble(weightField.getText().trim());
            double goal = Double.parseDouble(calorieGoalField.getText().trim());

            if (age < 1 || age > 120) {
                JOptionPane.showMessageDialog(this, "Age must be between 1 and 120.", "Validation", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (height <= 0 || weight <= 0 || goal <= 0) {
                JOptionPane.showMessageDialog(this, "Height, weight, and calorie goal must be positive numbers.", "Validation", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Estimate macros based on goal (25% protein, 50% carbs, 25% fat)
            double protein = (goal * 0.25) / 4.0;
            double carbs = (goal * 0.50) / 4.0;
            double fat = (goal * 0.25) / 9.0;

            String gender = (String) genderCombo.getSelectedItem();
            User user = new User(name, age, gender, height, weight, goal, protein, carbs, fat);
            service.updateUser(user);

            launchDashboard();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid numeric values for age, height, weight, and goal.", "Validation", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void launchDashboard() {
        SwingUtilities.invokeLater(() -> {
            DashboardFrame frame = new DashboardFrame(service);
            frame.setVisible(true);
            dispose();
        });
    }
}
