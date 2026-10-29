package com.nutrition.ui;

import com.nutrition.model.Food;
import com.nutrition.model.MealEntry;
import com.nutrition.service.INutritionService;
import com.nutrition.service.NutritionCalculator;
import com.nutrition.ui.components.CardPanel;
import com.nutrition.ui.components.ModernButton;
import com.nutrition.ui.components.ModernTable;
import com.nutrition.ui.theme.UITheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * Nutrition History Screen: Comprehensive log of all past dietary consumption.
 * Allows date navigation, meal type filtering, text search, editing, and deletion.
 */
public class HistoryPanel extends BasePanel {

    private LocalDate selectedDate = LocalDate.now();
    private JLabel currentDateLabel;
    private JComboBox<String> mealTypeFilter;
    private JTextField searchField;

    private DefaultTableModel historyTableModel;
    private ModernTable historyTable;
    private ArrayList<MealEntry> currentEntries = new ArrayList<>();

    // Day Summary Footer labels
    private JLabel dayCaloriesLabel;
    private JLabel dayProteinLabel;
    private JLabel dayCarbsLabel;
    private JLabel dayFatLabel;
    private JLabel dayCountLabel;

    public HistoryPanel(INutritionService service, DashboardFrame mainFrame) {
        super(service, mainFrame);
        initComponents();
        refreshData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 16));

        // Header
        JPanel header = createHeader("Dietary Nutrition History",
                "Review, audit, edit, and filter previous meal logs by date and meal category.");
        add(header, BorderLayout.NORTH);

        CardPanel mainCard = new CardPanel();
        mainCard.setLayout(new BorderLayout(0, 14));

        // Filter & Date Navigation Controls Bar
        JPanel controlsBar = createControlsBar();
        mainCard.add(controlsBar, BorderLayout.NORTH);

        // Center Table
        String[] columns = {"ID", "Time/Date", "Meal Type", "Food Name", "Qty (g)", "Calories", "Protein", "Carbs", "Fat"};
        historyTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        historyTable = new ModernTable(historyTableModel);
        historyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // Hide ID column from visual display but keep data
        historyTable.getColumnModel().getColumn(0).setMinWidth(0);
        historyTable.getColumnModel().getColumn(0).setMaxWidth(0);
        historyTable.getColumnModel().getColumn(0).setPreferredWidth(0);

        JScrollPane scroll = new JScrollPane(historyTable);
        scroll.setBorder(BorderFactory.createLineBorder(UITheme.CARD_BORDER));
        mainCard.add(scroll, BorderLayout.CENTER);

        // Footer Summary Bar
        JPanel footerBar = createFooterSummaryBar();
        mainCard.add(footerBar, BorderLayout.SOUTH);

        add(mainCard, BorderLayout.CENTER);
    }

    private JPanel createControlsBar() {
        JPanel bar = new JPanel(new BorderLayout(12, 10));
        bar.setOpaque(false);

        // Date navigation buttons (Left)
        JPanel dateNav = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        dateNav.setOpaque(false);

        ModernButton prevBtn = new ModernButton("◀ Prev Day", ModernButton.Style.SECONDARY);
        prevBtn.addActionListener(e -> {
            selectedDate = selectedDate.minusDays(1);
            applyFilter();
        });

        currentDateLabel = new JLabel(selectedDate.toString());
        currentDateLabel.setFont(UITheme.FONT_SUBHEADER);
        currentDateLabel.setForeground(UITheme.TEXT_PRIMARY);

        ModernButton nextBtn = new ModernButton("Next Day ▶", ModernButton.Style.SECONDARY);
        nextBtn.addActionListener(e -> {
            selectedDate = selectedDate.plusDays(1);
            applyFilter();
        });

        ModernButton todayBtn = new ModernButton("Today", ModernButton.Style.OUTLINE);
        todayBtn.addActionListener(e -> {
            selectedDate = LocalDate.now();
            applyFilter();
        });

        ModernButton pickDateBtn = new ModernButton("📅 Jump to Date", ModernButton.Style.SECONDARY);
        pickDateBtn.addActionListener(e -> handleJumpDate());

        dateNav.add(prevBtn);
        dateNav.add(currentDateLabel);
        dateNav.add(nextBtn);
        dateNav.add(todayBtn);
        dateNav.add(pickDateBtn);
        bar.add(dateNav, BorderLayout.WEST);

        // Filters and Action Buttons (Right)
        JPanel rightGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightGroup.setOpaque(false);

        JLabel lblMeal = new JLabel("Meal:");
        lblMeal.setFont(UITheme.FONT_SMALL);
        String[] filters = {"All", MealEntry.MEAL_BREAKFAST, MealEntry.MEAL_LUNCH, MealEntry.MEAL_DINNER, MealEntry.MEAL_SNACKS};
        mealTypeFilter = new JComboBox<>(filters);
        mealTypeFilter.setFont(UITheme.FONT_REGULAR);
        mealTypeFilter.addActionListener(e -> applyFilter());

        ModernButton editBtn = new ModernButton("✏️ Edit Entry", ModernButton.Style.SECONDARY);
        editBtn.addActionListener(e -> handleEditEntry());

        ModernButton deleteBtn = new ModernButton("🗑️ Delete", ModernButton.Style.DANGER);
        deleteBtn.addActionListener(e -> handleDeleteEntry());

        rightGroup.add(lblMeal);
        rightGroup.add(mealTypeFilter);
        rightGroup.add(editBtn);
        rightGroup.add(deleteBtn);
        bar.add(rightGroup, BorderLayout.EAST);

        return bar;
    }

    private JPanel createFooterSummaryBar() {
        JPanel footer = new JPanel(new GridLayout(1, 5, 12, 0));
        footer.setBackground(new Color(248, 250, 252));
        footer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.CARD_BORDER),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));

        dayCountLabel = new JLabel("Meals: 0");
        dayCountLabel.setFont(UITheme.FONT_MEDIUM);

        dayCaloriesLabel = new JLabel("Calories: 0.0 kcal");
        dayCaloriesLabel.setFont(UITheme.FONT_MEDIUM);
        dayCaloriesLabel.setForeground(UITheme.COLOR_CALORIES);

        dayProteinLabel = new JLabel("Protein: 0.0 g");
        dayProteinLabel.setFont(UITheme.FONT_MEDIUM);
        dayProteinLabel.setForeground(UITheme.COLOR_PROTEIN);

        dayCarbsLabel = new JLabel("Carbs: 0.0 g");
        dayCarbsLabel.setFont(UITheme.FONT_MEDIUM);
        dayCarbsLabel.setForeground(UITheme.COLOR_CARBS);

        dayFatLabel = new JLabel("Fat: 0.0 g");
        dayFatLabel.setFont(UITheme.FONT_MEDIUM);
        dayFatLabel.setForeground(UITheme.COLOR_FAT);

        footer.add(dayCountLabel);
        footer.add(dayCaloriesLabel);
        footer.add(dayProteinLabel);
        footer.add(dayCarbsLabel);
        footer.add(dayFatLabel);

        return footer;
    }

    private void handleJumpDate() {
        String input = JOptionPane.showInputDialog(this, "Enter Date (YYYY-MM-DD):", selectedDate.toString());
        if (input != null && !input.trim().isEmpty()) {
            try {
                selectedDate = LocalDate.parse(input.trim());
                applyFilter();
            } catch (Exception e) {
                showError("Invalid date format. Please use YYYY-MM-DD format (e.g. 2026-10-29).");
            }
        }
    }

    private void applyFilter() {
        currentDateLabel.setText(selectedDate.format(DateTimeFormatter.ofPattern("EEE, dd MMM yyyy")));
        String selectedType = (String) mealTypeFilter.getSelectedItem();

        ArrayList<MealEntry> dayMeals = service.getMealsForDateAndType(selectedDate, selectedType);
        currentEntries = dayMeals;

        historyTableModel.setRowCount(0);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
        for (MealEntry m : dayMeals) {
            historyTableModel.addRow(new Object[]{
                    m.getId(),
                    m.getDate().format(dtf),
                    m.getMealType(),
                    m.getFoodName(),
                    String.format("%.0f", m.getQuantityGrams()),
                    String.format("%.1f", m.getCalories()),
                    String.format("%.1f", m.getProtein()),
                    String.format("%.1f", m.getCarbs()),
                    String.format("%.1f", m.getFat())
            });
        }

        // Update footer totals
        double totalCal = NutritionCalculator.sumCalories(dayMeals);
        double totalPro = NutritionCalculator.sumProtein(dayMeals);
        double totalCarb = NutritionCalculator.sumCarbs(dayMeals);
        double totalFat = NutritionCalculator.sumFat(dayMeals);

        dayCountLabel.setText("Meals: " + dayMeals.size());
        dayCaloriesLabel.setText(String.format("Calories: %.1f kcal", totalCal));
        dayProteinLabel.setText(String.format("Protein: %.1f g", totalPro));
        dayCarbsLabel.setText(String.format("Carbs: %.1f g", totalCarb));
        dayFatLabel.setText(String.format("Fat: %.1f g", totalFat));
    }

    private MealEntry getSelectedMealEntry() {
        int row = historyTable.getSelectedRow();
        if (row < 0 || row >= currentEntries.size()) {
            return null;
        }
        return currentEntries.get(row);
    }

    private void handleEditEntry() {
        MealEntry selected = getSelectedMealEntry();
        if (selected == null) {
            showError("Please select a meal entry from the table to edit.");
            return;
        }

        JComboBox<String> typeCombo = new JComboBox<>(new String[]{
                MealEntry.MEAL_BREAKFAST, MealEntry.MEAL_LUNCH, MealEntry.MEAL_DINNER, MealEntry.MEAL_SNACKS
        });
        typeCombo.setSelectedItem(selected.getMealType());

        JTextField qtyField = new JTextField(String.valueOf(selected.getQuantityGrams()));

        JPanel form = new JPanel(new GridLayout(3, 2, 8, 8));
        form.add(new JLabel("Food Item:"));
        form.add(new JLabel("<html><b>" + selected.getFoodName() + "</b></html>"));
        form.add(new JLabel("Meal Category:"));
        form.add(typeCombo);
        form.add(new JLabel("Quantity (grams):"));
        form.add(qtyField);

        int res = JOptionPane.showConfirmDialog(this, form, "Edit Meal Record",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (res == JOptionPane.OK_OPTION) {
            try {
                double newQty = Double.parseDouble(qtyField.getText().trim());
                if (newQty <= 0) {
                    showError("Quantity must be greater than zero grams.");
                    return;
                }
                selected.setMealType((String) typeCombo.getSelectedItem());
                selected.setQuantityGrams(newQty);

                // Recalculate using FoodDatabase item if present
                Food food = service.getFoodDatabase().findByName(selected.getFoodName());
                if (food != null) {
                    selected.recalculateNutrition(food);
                } else {
                    // Proportionate scale
                    double ratio = newQty / selected.getQuantityGrams();
                    selected.setCalories(selected.getCalories() * ratio);
                    selected.setProtein(selected.getProtein() * ratio);
                    selected.setCarbs(selected.getCarbs() * ratio);
                    selected.setFat(selected.getFat() * ratio);
                }

                String err = service.updateMealEntry(selected);
                if (err != null) {
                    showError(err);
                } else {
                    mainFrame.refreshAllPanels();
                    showSuccess("Meal record updated successfully.");
                }
            } catch (NumberFormatException ex) {
                showError("Please enter a valid numeric quantity.");
            }
        }
    }

    private void handleDeleteEntry() {
        MealEntry selected = getSelectedMealEntry();
        if (selected == null) {
            showError("Please select a meal entry from the table to delete.");
            return;
        }

        if (showConfirm("Are you sure you want to delete this " + selected.getMealType() +
                " entry for '" + selected.getFoodName() + "'?", "Confirm Deletion")) {
            boolean removed = service.deleteMealEntry(selected.getId());
            if (removed) {
                mainFrame.refreshAllPanels();
                showSuccess("Meal entry removed successfully.");
            } else {
                showError("Could not delete the selected entry.");
            }
        }
    }

    @Override
    public void refreshData() {
        applyFilter();
    }
}
