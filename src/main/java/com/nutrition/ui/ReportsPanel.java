package com.nutrition.ui;

import com.nutrition.model.NutritionReport;
import com.nutrition.service.INutritionService;
import com.nutrition.storage.FileManager;
import com.nutrition.ui.components.CalorieBarChart;
import com.nutrition.ui.components.CardPanel;
import com.nutrition.ui.components.ModernButton;
import com.nutrition.ui.theme.UITheme;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Analytics and Reports Screen: Aggregates daily summaries, weekly statistical trends,
 * renders the 7-day custom calorie bar chart, and provides CSV / TXT data exports.
 */
public class ReportsPanel extends BasePanel {

    private JTabbedPane tabbedPane;

    // Daily Report Elements
    private JLabel dailyDateLabel;
    private JLabel dailyCaloriesLabel;
    private JLabel dailyProteinLabel;
    private JLabel dailyCarbsLabel;
    private JLabel dailyFatLabel;
    private JLabel dailyMealsLabel;
    private JLabel dailyGoalPctLabel;

    // Weekly Report Elements
    private JLabel weeklyRangeLabel;
    private JLabel weeklyAvgCalLabel;
    private JLabel weeklyAvgProteinLabel;
    private JLabel weeklyHighCalLabel;
    private JLabel weeklyLowCalLabel;
    private CalorieBarChart calorieChart;

    // Cached current reports
    private NutritionReport currentDailyReport;
    private NutritionReport currentWeeklyReport;

    public ReportsPanel(INutritionService service, DashboardFrame mainFrame) {
        super(service, mainFrame);
        initComponents();
        refreshData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 16));

        // Header with Export buttons
        JPanel header = createHeaderWithExports();
        add(header, BorderLayout.NORTH);

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UITheme.FONT_SUBHEADER);
        tabbedPane.setBackground(UITheme.BG_MAIN);

        // Tab 1: Weekly Analytics & Chart
        JPanel weeklyTab = createWeeklyTab();
        tabbedPane.addTab("📊 7-Day Weekly Analysis & Chart", weeklyTab);

        // Tab 2: Daily Report
        JPanel dailyTab = createDailyTab();
        tabbedPane.addTab("📅 Daily Summary Report", dailyTab);

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createHeaderWithExports() {
        JPanel headerPanel = new JPanel(new BorderLayout(16, 0));
        headerPanel.setOpaque(false);

        JPanel textGroup = new JPanel();
        textGroup.setOpaque(false);
        textGroup.setLayout(new BoxLayout(textGroup, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("Nutrition Analytics & Reports");
        titleLabel.setFont(UITheme.FONT_TITLE);
        titleLabel.setForeground(UITheme.TEXT_PRIMARY);

        JLabel subLabel = new JLabel("Review nutritional trends, weekly averages, and export reports for medical/dietary records.");
        subLabel.setFont(UITheme.FONT_REGULAR);
        subLabel.setForeground(UITheme.TEXT_SECONDARY);

        textGroup.add(titleLabel);
        textGroup.add(Box.createRigidArea(new Dimension(0, 4)));
        textGroup.add(subLabel);
        headerPanel.add(textGroup, BorderLayout.WEST);

        // Export Buttons
        JPanel exportGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        exportGroup.setOpaque(false);

        ModernButton exportCsvBtn = new ModernButton("📥 Export to CSV", ModernButton.Style.PRIMARY);
        exportCsvBtn.addActionListener(e -> handleExport("CSV"));

        ModernButton exportTxtBtn = new ModernButton("📄 Export to TXT", ModernButton.Style.SECONDARY);
        exportTxtBtn.addActionListener(e -> handleExport("TXT"));

        exportGroup.add(exportCsvBtn);
        exportGroup.add(exportTxtBtn);
        headerPanel.add(exportGroup, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createWeeklyTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 4, 12, 4));

        // 1. Weekly Stat Cards
        CardPanel statsCard = new CardPanel();
        statsCard.setLayout(new GridLayout(1, 4, 14, 0));
        statsCard.setPreferredSize(new Dimension(800, 95));

        weeklyAvgCalLabel = new JLabel("0.0 kcal");
        weeklyAvgProteinLabel = new JLabel("0.0 g");
        weeklyHighCalLabel = new JLabel("0.0 kcal");
        weeklyLowCalLabel = new JLabel("0.0 kcal");

        statsCard.add(createReportStatSlot("Daily Calorie Avg", weeklyAvgCalLabel, UITheme.COLOR_CALORIES));
        statsCard.add(createReportStatSlot("Daily Protein Avg", weeklyAvgProteinLabel, UITheme.COLOR_PROTEIN));
        statsCard.add(createReportStatSlot("Peak Calorie Day", weeklyHighCalLabel, UITheme.WARNING));
        statsCard.add(createReportStatSlot("Lowest Calorie Day", weeklyLowCalLabel, UITheme.TEXT_SECONDARY));

        panel.add(statsCard, BorderLayout.NORTH);

        // 2. Center: Calorie Bar Chart Card
        CardPanel chartCard = new CardPanel();
        chartCard.setLayout(new BorderLayout(0, 10));

        JPanel chartHeader = new JPanel(new BorderLayout());
        chartHeader.setOpaque(false);

        JLabel chartTitle = new JLabel("📈 7-Day Calorie Consumption Trend");
        chartTitle.setFont(UITheme.FONT_SUBHEADER);
        chartTitle.setForeground(UITheme.TEXT_PRIMARY);

        weeklyRangeLabel = new JLabel("Period: -");
        weeklyRangeLabel.setFont(UITheme.FONT_SMALL);
        weeklyRangeLabel.setForeground(UITheme.TEXT_SECONDARY);

        chartHeader.add(chartTitle, BorderLayout.WEST);
        chartHeader.add(weeklyRangeLabel, BorderLayout.EAST);
        chartCard.add(chartHeader, BorderLayout.NORTH);

        calorieChart = new CalorieBarChart();
        chartCard.add(calorieChart, BorderLayout.CENTER);

        panel.add(chartCard, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createDailyTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 4, 12, 4));

        CardPanel card = new CardPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        dailyDateLabel = new JLabel("Daily Summary for: Today");
        dailyDateLabel.setFont(UITheme.FONT_SUBHEADER);
        dailyDateLabel.setForeground(UITheme.TEXT_PRIMARY);
        card.add(dailyDateLabel);
        card.add(Box.createRigidArea(new Dimension(0, 16)));

        JPanel grid = new JPanel(new GridLayout(3, 2, 16, 16));
        grid.setOpaque(false);

        dailyCaloriesLabel = new JLabel("0.0 kcal");
        dailyProteinLabel = new JLabel("0.0 g");
        dailyCarbsLabel = new JLabel("0.0 g");
        dailyFatLabel = new JLabel("0.0 g");
        dailyMealsLabel = new JLabel("0 meals");
        dailyGoalPctLabel = new JLabel("0.0%");

        grid.add(createReportStatSlot("Total Calories Consumed", dailyCaloriesLabel, UITheme.COLOR_CALORIES));
        grid.add(createReportStatSlot("Calorie Goal Completion", dailyGoalPctLabel, UITheme.PRIMARY));
        grid.add(createReportStatSlot("Total Protein", dailyProteinLabel, UITheme.COLOR_PROTEIN));
        grid.add(createReportStatSlot("Total Carbohydrates", dailyCarbsLabel, UITheme.COLOR_CARBS));
        grid.add(createReportStatSlot("Total Healthy Fat", dailyFatLabel, UITheme.COLOR_FAT));
        grid.add(createReportStatSlot("Meals Logged", dailyMealsLabel, UITheme.TEXT_PRIMARY));

        card.add(grid);
        panel.add(card, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createReportStatSlot(String title, JLabel valLabel, Color accent) {
        JPanel slot = new JPanel();
        slot.setOpaque(false);
        slot.setLayout(new BoxLayout(slot, BoxLayout.Y_AXIS));

        JLabel lbl = new JLabel(title);
        lbl.setFont(UITheme.FONT_SMALL);
        lbl.setForeground(UITheme.TEXT_SECONDARY);

        valLabel.setFont(UITheme.FONT_HEADER);
        valLabel.setForeground(accent);

        slot.add(lbl);
        slot.add(Box.createRigidArea(new Dimension(0, 4)));
        slot.add(valLabel);
        return slot;
    }

    private void handleExport(String format) {
        NutritionReport reportToExport;
        if (tabbedPane.getSelectedIndex() == 0) {
            reportToExport = currentWeeklyReport != null ? currentWeeklyReport : service.generateWeeklyReport(LocalDate.now());
        } else {
            reportToExport = currentDailyReport != null ? currentDailyReport : service.generateDailyReport(LocalDate.now());
        }

        String content;
        String defaultFilename;
        String extension;

        if ("CSV".equalsIgnoreCase(format)) {
            content = reportToExport.toCsv();
            defaultFilename = "Nutrition_Report_" + LocalDate.now() + ".csv";
            extension = ".csv";
        } else {
            content = reportToExport.toTxt();
            defaultFilename = "Nutrition_Report_" + LocalDate.now() + ".txt";
            extension = ".txt";
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Nutrition Report (" + format + ")");
        fileChooser.setSelectedFile(new File(defaultFilename));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            if (!fileToSave.getName().toLowerCase().endsWith(extension)) {
                fileToSave = new File(fileToSave.getParentFile(), fileToSave.getName() + extension);
            }

            boolean success = FileManager.exportContentToFile(content, fileToSave);
            if (success) {
                showSuccess("Report exported successfully to:\n" + fileToSave.getAbsolutePath());
            } else {
                showError("Failed to write report file. Please check folder permissions.");
            }
        }
    }

    @Override
    public void refreshData() {
        LocalDate today = LocalDate.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MMM-yyyy");

        // 1. Refresh Daily Report
        currentDailyReport = service.generateDailyReport(today);
        dailyDateLabel.setText("Daily Summary for: " + today.format(dtf));
        dailyCaloriesLabel.setText(String.format("%.1f / %.1f kcal",
                currentDailyReport.getTotalCalories(), currentDailyReport.getTargetCalorieGoal()));
        dailyGoalPctLabel.setText(String.format("%.1f%%", currentDailyReport.getCalorieCompletionPercentage()));
        dailyProteinLabel.setText(String.format("%.1f / %.1f g",
                currentDailyReport.getTotalProtein(), currentDailyReport.getTargetProteinGoal()));
        dailyCarbsLabel.setText(String.format("%.1f / %.1f g",
                currentDailyReport.getTotalCarbs(), currentDailyReport.getTargetCarbGoal()));
        dailyFatLabel.setText(String.format("%.1f / %.1f g",
                currentDailyReport.getTotalFat(), currentDailyReport.getTargetFatGoal()));
        dailyMealsLabel.setText(currentDailyReport.getTotalMeals() + " meal entries");

        // 2. Refresh Weekly Report
        currentWeeklyReport = service.generateWeeklyReport(today);
        weeklyRangeLabel.setText("Period: " + currentWeeklyReport.getStartDate().format(dtf) +
                " to " + currentWeeklyReport.getEndDate().format(dtf));

        weeklyAvgCalLabel.setText(String.format("%.0f kcal", currentWeeklyReport.getAverageDailyCalories()));
        weeklyAvgProteinLabel.setText(String.format("%.1f g", currentWeeklyReport.getAverageProtein()));

        String highDateStr = currentWeeklyReport.getHighestCalorieDate() != null
                ? currentWeeklyReport.getHighestCalorieDate().format(DateTimeFormatter.ofPattern("EEE dd"))
                : "N/A";
        weeklyHighCalLabel.setText(String.format("%.0f kcal (%s)",
                currentWeeklyReport.getHighestCalorieValue(), highDateStr));

        String lowDateStr = currentWeeklyReport.getLowestCalorieDate() != null
                ? currentWeeklyReport.getLowestCalorieDate().format(DateTimeFormatter.ofPattern("EEE dd"))
                : "N/A";
        weeklyLowCalLabel.setText(String.format("%.0f kcal (%s)",
                currentWeeklyReport.getLowestCalorieValue(), lowDateStr));

        // Update Chart
        double dailyGoal = service.getUser() != null ? service.getUser().getDailyCalorieGoal() : 2200.0;
        calorieChart.setData(currentWeeklyReport.getDailyCalorieMap(), dailyGoal);
    }
}
