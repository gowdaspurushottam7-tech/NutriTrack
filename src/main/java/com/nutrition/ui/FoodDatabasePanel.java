package com.nutrition.ui;

import com.nutrition.model.Food;
import com.nutrition.service.FoodDatabase;
import com.nutrition.service.INutritionService;
import com.nutrition.ui.components.CardPanel;
import com.nutrition.ui.components.ModernButton;
import com.nutrition.ui.components.ModernTable;
import com.nutrition.ui.theme.UITheme;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

/**
 * Food Database Catalog Screen: Browse 30+ built-in foods, search by keyword,
 * add custom foods, and edit/delete custom foods safely.
 */
public class FoodDatabasePanel extends BasePanel {

    private JTextField searchField;
    private DefaultTableModel tableModel;
    private ModernTable foodTable;
    private ArrayList<Food> currentDisplayedFoods = new ArrayList<>();

    public FoodDatabasePanel(INutritionService service, DashboardFrame mainFrame) {
        super(service, mainFrame);
        initComponents();
        refreshData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 16));

        // Header
        JPanel header = createHeader("Food Database Catalog",
                "Explore comprehensive nutritional metrics per 100g or register custom food items.");
        add(header, BorderLayout.NORTH);

        // Main Center Panel
        CardPanel mainCard = new CardPanel();
        mainCard.setLayout(new BorderLayout(0, 14));

        // Search & Controls Bar
        JPanel topBar = new JPanel(new BorderLayout(16, 0));
        topBar.setOpaque(false);

        JPanel searchGroup = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        searchGroup.setOpaque(false);
        JLabel searchIcon = new JLabel("🔍 Search:");
        searchIcon.setFont(UITheme.FONT_MEDIUM);
        searchIcon.setForeground(UITheme.TEXT_SECONDARY);

        searchField = new JTextField(20);
        searchField.setFont(UITheme.FONT_REGULAR);
        searchField.setPreferredSize(new Dimension(240, 36));
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { filterTable(); }
            @Override
            public void removeUpdate(DocumentEvent e) { filterTable(); }
            @Override
            public void changedUpdate(DocumentEvent e) { filterTable(); }
        });

        searchGroup.add(searchIcon);
        searchGroup.add(searchField);
        topBar.add(searchGroup, BorderLayout.WEST);

        // Buttons Group
        JPanel btnGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnGroup.setOpaque(false);

        ModernButton logMealBtn = new ModernButton("🍽️ Log to Meal", ModernButton.Style.OUTLINE);
        logMealBtn.addActionListener(e -> handleLogSelectedFood());

        ModernButton addCustomBtn = new ModernButton("+ Add Custom Food", ModernButton.Style.PRIMARY);
        addCustomBtn.addActionListener(e -> showAddCustomFoodDialog());

        ModernButton editBtn = new ModernButton("✏️ Edit", ModernButton.Style.SECONDARY);
        editBtn.addActionListener(e -> handleEditCustomFood());

        ModernButton deleteBtn = new ModernButton("🗑️ Delete", ModernButton.Style.DANGER);
        deleteBtn.addActionListener(e -> handleDeleteCustomFood());

        btnGroup.add(logMealBtn);
        btnGroup.add(addCustomBtn);
        btnGroup.add(editBtn);
        btnGroup.add(deleteBtn);
        topBar.add(btnGroup, BorderLayout.EAST);

        mainCard.add(topBar, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Food Name", "Category", "Calories (kcal)", "Protein (g)", "Carbs (g)", "Fat (g)", "Origin"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        foodTable = new ModernTable(tableModel);
        foodTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(foodTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.CARD_BORDER));
        mainCard.add(scrollPane, BorderLayout.CENTER);

        // Disclaimer at bottom
        JLabel disclaimerLabel = new JLabel("ℹ️ " + FoodDatabase.NUTRITION_DISCLAIMER);
        disclaimerLabel.setFont(UITheme.FONT_SMALL);
        disclaimerLabel.setForeground(UITheme.TEXT_MUTED);
        disclaimerLabel.setBorder(BorderFactory.createEmptyBorder(6, 4, 2, 4));
        mainCard.add(disclaimerLabel, BorderLayout.SOUTH);

        add(mainCard, BorderLayout.CENTER);
    }

    private void filterTable() {
        String query = searchField.getText().trim();
        currentDisplayedFoods = service.getFoodDatabase().searchFoods(query);
        populateTable(currentDisplayedFoods);
    }

    private void populateTable(ArrayList<Food> foods) {
        tableModel.setRowCount(0);
        for (Food f : foods) {
            tableModel.addRow(new Object[]{
                    f.getId(),
                    f.getName(),
                    f.getCategory(),
                    String.format("%.1f", f.getCaloriesPer100g()),
                    String.format("%.1f", f.getProteinPer100g()),
                    String.format("%.1f", f.getCarbsPer100g()),
                    String.format("%.1f", f.getFatPer100g()),
                    f.isCustom() ? "Custom" : "Built-in"
            });
        }
    }

    private Food getSelectedFood() {
        int selectedRow = foodTable.getSelectedRow();
        if (selectedRow < 0 || selectedRow >= currentDisplayedFoods.size()) {
            return null;
        }
        return currentDisplayedFoods.get(selectedRow);
    }

    private void handleLogSelectedFood() {
        Food selected = getSelectedFood();
        if (selected == null) {
            showError("Please select a food item from the table first.");
            return;
        }
        mainFrame.getAddMealPanel().setPreselectedFood(selected);
        navigateTo(DashboardFrame.PANEL_ADD_MEAL);
    }

    private void showAddCustomFoodDialog() {
        JTextField nameField = new JTextField();
        JTextField categoryField = new JTextField("Custom");
        JTextField calField = new JTextField();
        JTextField proField = new JTextField();
        JTextField carbField = new JTextField();
        JTextField fatField = new JTextField();

        JPanel form = new JPanel(new GridLayout(6, 2, 10, 10));
        form.add(new JLabel("Food Name:"));
        form.add(nameField);
        form.add(new JLabel("Category:"));
        form.add(categoryField);
        form.add(new JLabel("Calories per 100g:"));
        form.add(calField);
        form.add(new JLabel("Protein per 100g (g):"));
        form.add(proField);
        form.add(new JLabel("Carbs per 100g (g):"));
        form.add(carbField);
        form.add(new JLabel("Fat per 100g (g):"));
        form.add(fatField);

        int result = JOptionPane.showConfirmDialog(this, form, "Add New Custom Food",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String name = nameField.getText().trim();
                String category = categoryField.getText().trim();
                double cal = Double.parseDouble(calField.getText().trim());
                double pro = Double.parseDouble(proField.getText().trim());
                double carb = Double.parseDouble(carbField.getText().trim());
                double fat = Double.parseDouble(fatField.getText().trim());

                Food newFood = new Food(null, name, category, cal, pro, carb, fat, true);
                String err = service.getFoodDatabase().addCustomFood(newFood);
                if (err != null) {
                    showError(err);
                } else {
                    service.saveAllData();
                    refreshData();
                    showSuccess("Custom food '" + name + "' added successfully!");
                }
            } catch (NumberFormatException e) {
                showError("Please enter valid numeric values for calories, protein, carbs, and fat.");
            }
        }
    }

    private void handleEditCustomFood() {
        Food selected = getSelectedFood();
        if (selected == null) {
            showError("Please select a food item to edit.");
            return;
        }

        if (!selected.isCustom()) {
            showError("Built-in food items are protected and cannot be edited.");
            return;
        }

        JTextField nameField = new JTextField(selected.getName());
        JTextField categoryField = new JTextField(selected.getCategory());
        JTextField calField = new JTextField(String.valueOf(selected.getCaloriesPer100g()));
        JTextField proField = new JTextField(String.valueOf(selected.getProteinPer100g()));
        JTextField carbField = new JTextField(String.valueOf(selected.getCarbsPer100g()));
        JTextField fatField = new JTextField(String.valueOf(selected.getFatPer100g()));

        JPanel form = new JPanel(new GridLayout(6, 2, 10, 10));
        form.add(new JLabel("Food Name:"));
        form.add(nameField);
        form.add(new JLabel("Category:"));
        form.add(categoryField);
        form.add(new JLabel("Calories per 100g:"));
        form.add(calField);
        form.add(new JLabel("Protein per 100g (g):"));
        form.add(proField);
        form.add(new JLabel("Carbs per 100g (g):"));
        form.add(carbField);
        form.add(new JLabel("Fat per 100g (g):"));
        form.add(fatField);

        int result = JOptionPane.showConfirmDialog(this, form, "Edit Custom Food",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                selected.setName(nameField.getText().trim());
                selected.setCategory(categoryField.getText().trim());
                selected.setCaloriesPer100g(Double.parseDouble(calField.getText().trim()));
                selected.setProteinPer100g(Double.parseDouble(proField.getText().trim()));
                selected.setCarbsPer100g(Double.parseDouble(carbField.getText().trim()));
                selected.setFatPer100g(Double.parseDouble(fatField.getText().trim()));

                String err = service.getFoodDatabase().updateCustomFood(selected);
                if (err != null) {
                    showError(err);
                } else {
                    service.saveAllData();
                    refreshData();
                    showSuccess("Custom food updated successfully!");
                }
            } catch (NumberFormatException e) {
                showError("Please enter valid numeric values.");
            }
        }
    }

    private void handleDeleteCustomFood() {
        Food selected = getSelectedFood();
        if (selected == null) {
            showError("Please select a food item to delete.");
            return;
        }

        if (!selected.isCustom()) {
            showError("Built-in food items cannot be deleted to maintain system integrity.");
            return;
        }

        if (showConfirm("Are you sure you want to delete '" + selected.getName() + "'?", "Confirm Delete")) {
            String err = service.getFoodDatabase().deleteCustomFood(selected.getId());
            if (err != null) {
                showError(err);
            } else {
                service.saveAllData();
                refreshData();
                showSuccess("Custom food deleted successfully.");
            }
        }
    }

    @Override
    public void refreshData() {
        filterTable();
    }
}
