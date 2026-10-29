package com.nutrition.ui;

import com.nutrition.model.Food;
import com.nutrition.model.MealEntry;
import com.nutrition.service.INutritionService;
import com.nutrition.service.NutritionCalculator;
import com.nutrition.ui.components.CardPanel;
import com.nutrition.ui.components.ModernButton;
import com.nutrition.ui.theme.UITheme;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Add Meal Screen: Allows users to log food entries for Breakfast, Lunch, Dinner, and Snacks.
 * Features live calculation preview, intelligent search filter, and strict input validation.
 */
public class AddMealPanel extends BasePanel {

    private JComboBox<String> mealTypeCombo;
    private JComboBox<Food> foodCombo;
    private JTextField searchFoodField;
    private JTextField quantityField;
    private JTextField dateField;

    // Live Nutrition Preview labels
    private JLabel previewCaloriesLabel;
    private JLabel previewProteinLabel;
    private JLabel previewCarbsLabel;
    private JLabel previewFatLabel;

    public AddMealPanel(INutritionService service, DashboardFrame mainFrame) {
        super(service, mainFrame);
        initComponents();
        refreshData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 20));

        // Header
        JPanel header = createHeader("Add Meal Entry", "Log your food consumption and monitor nutrition automatically.");
        add(header, BorderLayout.NORTH);

        // Center card container
        JPanel centerWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        centerWrapper.setOpaque(false);

        CardPanel formCard = new CardPanel();
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setPreferredSize(new Dimension(680, 520));
        formCard.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));

        // 1. Date & Meal Type Row
        JPanel row1 = new JPanel(new GridLayout(1, 2, 20, 0));
        row1.setOpaque(false);

        JPanel dateGroup = new JPanel();
        dateGroup.setOpaque(false);
        dateGroup.setLayout(new BoxLayout(dateGroup, BoxLayout.Y_AXIS));
        JLabel lblDate = new JLabel("Log Date (YYYY-MM-DD):");
        lblDate.setFont(UITheme.FONT_MEDIUM);
        dateField = new JTextField(LocalDate.now().toString());
        dateField.setFont(UITheme.FONT_REGULAR);
        dateField.setPreferredSize(new Dimension(280, 36));
        dateGroup.add(lblDate);
        dateGroup.add(Box.createRigidArea(new Dimension(0, 6)));
        dateGroup.add(dateField);

        JPanel mealGroup = new JPanel();
        mealGroup.setOpaque(false);
        mealGroup.setLayout(new BoxLayout(mealGroup, BoxLayout.Y_AXIS));
        JLabel lblMeal = new JLabel("Meal Category:");
        lblMeal.setFont(UITheme.FONT_MEDIUM);
        String[] mealTypes = {MealEntry.MEAL_BREAKFAST, MealEntry.MEAL_LUNCH, MealEntry.MEAL_DINNER, MealEntry.MEAL_SNACKS};
        mealTypeCombo = new JComboBox<>(mealTypes);
        mealTypeCombo.setFont(UITheme.FONT_REGULAR);
        mealTypeCombo.setPreferredSize(new Dimension(280, 36));
        mealGroup.add(lblMeal);
        mealGroup.add(Box.createRigidArea(new Dimension(0, 6)));
        mealGroup.add(mealTypeCombo);

        row1.add(dateGroup);
        row1.add(mealGroup);
        formCard.add(row1);
        formCard.add(Box.createRigidArea(new Dimension(0, 16)));

        // 2. Search & Select Food
        JPanel searchGroup = new JPanel();
        searchGroup.setOpaque(false);
        searchGroup.setLayout(new BoxLayout(searchGroup, BoxLayout.Y_AXIS));
        JLabel lblSearch = new JLabel("Quick Search Food Catalog:");
        lblSearch.setFont(UITheme.FONT_MEDIUM);
        searchFoodField = new JTextField();
        searchFoodField.setFont(UITheme.FONT_REGULAR);
        searchFoodField.setToolTipText("Type to filter food items...");
        searchFoodField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { filterFoodList(); }
            @Override
            public void removeUpdate(DocumentEvent e) { filterFoodList(); }
            @Override
            public void changedUpdate(DocumentEvent e) { filterFoodList(); }
        });

        searchGroup.add(lblSearch);
        searchGroup.add(Box.createRigidArea(new Dimension(0, 6)));
        searchGroup.add(searchFoodField);
        formCard.add(searchGroup);
        formCard.add(Box.createRigidArea(new Dimension(0, 14)));

        // 3. Food Dropdown
        JPanel foodGroup = new JPanel();
        foodGroup.setOpaque(false);
        foodGroup.setLayout(new BoxLayout(foodGroup, BoxLayout.Y_AXIS));
        JLabel lblFood = new JLabel("Selected Food Item:");
        lblFood.setFont(UITheme.FONT_MEDIUM);
        foodCombo = new JComboBox<>();
        foodCombo.setFont(UITheme.FONT_REGULAR);
        foodCombo.setPreferredSize(new Dimension(610, 36));
        foodCombo.addActionListener(e -> updateLivePreview());

        foodGroup.add(lblFood);
        foodGroup.add(Box.createRigidArea(new Dimension(0, 6)));
        foodGroup.add(foodCombo);
        formCard.add(foodGroup);
        formCard.add(Box.createRigidArea(new Dimension(0, 16)));

        // 4. Quantity Field
        JPanel qtyGroup = new JPanel();
        qtyGroup.setOpaque(false);
        qtyGroup.setLayout(new BoxLayout(qtyGroup, BoxLayout.Y_AXIS));
        JLabel lblQty = new JLabel("Quantity (in grams):");
        lblQty.setFont(UITheme.FONT_MEDIUM);
        quantityField = new JTextField("100");
        quantityField.setFont(UITheme.FONT_REGULAR);
        quantityField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { updateLivePreview(); }
            @Override
            public void removeUpdate(DocumentEvent e) { updateLivePreview(); }
            @Override
            public void changedUpdate(DocumentEvent e) { updateLivePreview(); }
        });

        qtyGroup.add(lblQty);
        qtyGroup.add(Box.createRigidArea(new Dimension(0, 6)));
        qtyGroup.add(quantityField);
        formCard.add(qtyGroup);
        formCard.add(Box.createRigidArea(new Dimension(0, 18)));

        // 5. Live Nutrition Preview Box
        JPanel previewCard = createPreviewCard();
        formCard.add(previewCard);
        formCard.add(Box.createRigidArea(new Dimension(0, 24)));

        // 6. Action Buttons (Add Meal, Clear, Cancel)
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        btnRow.setOpaque(false);

        ModernButton clearBtn = new ModernButton("Clear Form", ModernButton.Style.SECONDARY);
        clearBtn.addActionListener(e -> clearForm());

        ModernButton cancelBtn = new ModernButton("Cancel", ModernButton.Style.OUTLINE);
        cancelBtn.addActionListener(e -> navigateTo(DashboardFrame.PANEL_DASHBOARD));

        ModernButton addBtn = new ModernButton("✓ Add Meal", ModernButton.Style.PRIMARY);
        addBtn.addActionListener(e -> handleAddMeal());

        btnRow.add(clearBtn);
        btnRow.add(cancelBtn);
        btnRow.add(addBtn);
        formCard.add(btnRow);

        centerWrapper.add(formCard);

        JScrollPane scroll = new JScrollPane(centerWrapper);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        add(scroll, BorderLayout.CENTER);
    }

    private JPanel createPreviewCard() {
        JPanel box = new JPanel(new GridLayout(1, 4, 12, 0));
        box.setBackground(new Color(241, 245, 249));
        box.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.CARD_BORDER),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)
        ));

        previewCaloriesLabel = new JLabel("0.0 kcal");
        previewProteinLabel = new JLabel("0.0 g");
        previewCarbsLabel = new JLabel("0.0 g");
        previewFatLabel = new JLabel("0.0 g");

        box.add(createMiniPreviewSlot("🔥 Calories", previewCaloriesLabel, UITheme.COLOR_CALORIES));
        box.add(createMiniPreviewSlot("🥩 Protein", previewProteinLabel, UITheme.COLOR_PROTEIN));
        box.add(createMiniPreviewSlot("🌾 Carbs", previewCarbsLabel, UITheme.COLOR_CARBS));
        box.add(createMiniPreviewSlot("🥑 Fat", previewFatLabel, UITheme.COLOR_FAT));

        return box;
    }

    private JPanel createMiniPreviewSlot(String title, JLabel valLabel, Color c) {
        JPanel slot = new JPanel();
        slot.setOpaque(false);
        slot.setLayout(new BoxLayout(slot, BoxLayout.Y_AXIS));

        JLabel lbl = new JLabel(title);
        lbl.setFont(UITheme.FONT_SMALL);
        lbl.setForeground(UITheme.TEXT_SECONDARY);

        valLabel.setFont(UITheme.FONT_SUBHEADER);
        valLabel.setForeground(c);

        slot.add(lbl);
        slot.add(Box.createRigidArea(new Dimension(0, 4)));
        slot.add(valLabel);
        return slot;
    }

    private void filterFoodList() {
        String query = searchFoodField.getText().trim();
        Food selected = (Food) foodCombo.getSelectedItem();

        ArrayList<Food> matches = service.getFoodDatabase().searchFoods(query);
        foodCombo.removeAllItems();
        for (Food f : matches) {
            foodCombo.addItem(f);
        }

        if (selected != null && matches.contains(selected)) {
            foodCombo.setSelectedItem(selected);
        } else if (!matches.isEmpty()) {
            foodCombo.setSelectedIndex(0);
        }
        updateLivePreview();
    }

    private void updateLivePreview() {
        Food food = (Food) foodCombo.getSelectedItem();
        double qty = 0.0;
        try {
            qty = Double.parseDouble(quantityField.getText().trim());
        } catch (NumberFormatException ignored) {}

        if (food != null && qty > 0) {
            double cal = NutritionCalculator.calculateCalories(food, qty);
            double pro = NutritionCalculator.calculateProtein(food, qty);
            double carb = NutritionCalculator.calculateCarbs(food, qty);
            double fat = NutritionCalculator.calculateFat(food, qty);

            previewCaloriesLabel.setText(String.format("%.1f kcal", cal));
            previewProteinLabel.setText(String.format("%.1f g", pro));
            previewCarbsLabel.setText(String.format("%.1f g", carb));
            previewFatLabel.setText(String.format("%.1f g", fat));
        } else {
            previewCaloriesLabel.setText("0.0 kcal");
            previewProteinLabel.setText("0.0 g");
            previewCarbsLabel.setText("0.0 g");
            previewFatLabel.setText("0.0 g");
        }
    }

    private void handleAddMeal() {
        Food selectedFood = (Food) foodCombo.getSelectedItem();
        if (selectedFood == null) {
            showError("Please select a food item from the list.");
            return;
        }

        String qtyStr = quantityField.getText().trim();
        if (qtyStr.isEmpty()) {
            showError("Please enter the food quantity in grams.");
            quantityField.requestFocus();
            return;
        }

        double quantity;
        try {
            quantity = Double.parseDouble(qtyStr);
        } catch (NumberFormatException e) {
            showError("Invalid quantity: Please enter a valid positive number.");
            quantityField.requestFocus();
            return;
        }

        if (quantity <= 0) {
            showError("Quantity must be greater than zero grams.");
            quantityField.requestFocus();
            return;
        }

        if (quantity > 5000) {
            showError("Quantity is unrealistically high (exceeds 5,000 grams).");
            return;
        }

        LocalDate mealDate;
        try {
            mealDate = LocalDate.parse(dateField.getText().trim());
        } catch (Exception e) {
            showError("Invalid date format. Please use YYYY-MM-DD (e.g. " + LocalDate.now() + ").");
            dateField.requestFocus();
            return;
        }

        String mealType = (String) mealTypeCombo.getSelectedItem();

        // Create MealEntry and compute nutrition
        MealEntry entry = new MealEntry(mealDate, mealType, selectedFood, quantity);
        String result = service.addMealEntry(entry);

        if (result == null) {
            showSuccess(String.format("Successfully logged %s (%s)!\nCalories: %.1f kcal | Protein: %.1f g",
                    selectedFood.getName(), mealType, entry.getCalories(), entry.getProtein()));

            // Refresh all main frame views
            mainFrame.refreshAllPanels();

            // Clear quantity for next potential entry
            quantityField.setText("");
            quantityField.requestFocus();
        } else {
            showError("Could not add meal: " + result);
        }
    }

    public void setPreselectedMealType(String mealType) {
        if (mealType != null) {
            mealTypeCombo.setSelectedItem(mealType);
        }
    }

    public void setPreselectedFood(Food food) {
        if (food != null) {
            searchFoodField.setText("");
            foodCombo.setSelectedItem(food);
            updateLivePreview();
        }
    }

    private void clearForm() {
        searchFoodField.setText("");
        quantityField.setText("100");
        dateField.setText(LocalDate.now().toString());
        if (foodCombo.getItemCount() > 0) {
            foodCombo.setSelectedIndex(0);
        }
        updateLivePreview();
    }

    @Override
    public void refreshData() {
        Food current = (Food) foodCombo.getSelectedItem();
        foodCombo.removeAllItems();
        ArrayList<Food> allFoods = service.getFoodDatabase().getAllFoods();
        for (Food f : allFoods) {
            foodCombo.addItem(f);
        }
        if (current != null && allFoods.contains(current)) {
            foodCombo.setSelectedItem(current);
        }
        updateLivePreview();
    }
}
