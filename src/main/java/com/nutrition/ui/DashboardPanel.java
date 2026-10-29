package com.nutrition.ui;

import com.nutrition.model.MealEntry;
import com.nutrition.model.User;
import com.nutrition.service.INutritionService;
import com.nutrition.service.NutritionCalculator;
import com.nutrition.ui.components.CardPanel;
import com.nutrition.ui.components.ModernButton;
import com.nutrition.ui.components.ModernProgressBar;
import com.nutrition.ui.components.ModernTable;
import com.nutrition.ui.theme.UITheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Dashboard Panel: The central overview hub of the application.
 * Dynamically displays daily nutrition targets, macro progress bars,
 * meal category summaries, and recent activity.
 */
public class DashboardPanel extends BasePanel {

    // Header labels
    private JLabel greetingLabel;
    private JLabel dateLabel;

    // Macro Cards UI elements
    private JLabel caloriesValueLabel;
    private ModernProgressBar caloriesProgressBar;
    private JLabel caloriesSubLabel;

    private JLabel proteinValueLabel;
    private ModernProgressBar proteinProgressBar;
    private JLabel proteinSubLabel;

    private JLabel carbsValueLabel;
    private ModernProgressBar carbsProgressBar;
    private JLabel carbsSubLabel;

    private JLabel fatValueLabel;
    private ModernProgressBar fatProgressBar;
    private JLabel fatSubLabel;

    // Meal Summary count labels
    private JLabel breakfastCountLabel;
    private JLabel lunchCountLabel;
    private JLabel dinnerCountLabel;
    private JLabel snacksCountLabel;

    // Additional Stats
    private JLabel totalMealsLabel;
    private JLabel remainingCaloriesLabel;
    private JLabel goalPercentLabel;
    private JLabel bmiValueLabel;

    // Recent Entries Table
    private DefaultTableModel recentTableModel;

    public DashboardPanel(INutritionService service, DashboardFrame mainFrame) {
        super(service, mainFrame);
        initComponents();
        refreshData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 20));

        // Top Greeting Banner
        JPanel topBanner = createTopBanner();
        add(topBanner, BorderLayout.NORTH);

        // Center Content inside a smooth ScrollPane
        JPanel contentArea = new JPanel();
        contentArea.setOpaque(false);
        contentArea.setLayout(new BoxLayout(contentArea, BoxLayout.Y_AXIS));

        // 1. 4 Nutrition Metric Cards
        JPanel metricsGrid = createMetricsGrid();
        contentArea.add(metricsGrid);
        contentArea.add(Box.createRigidArea(new Dimension(0, 18)));

        // 2. Middle Section: Meal Summaries + Daily Health Highlights
        JPanel middleRow = createMiddleSection();
        contentArea.add(middleRow);
        contentArea.add(Box.createRigidArea(new Dimension(0, 18)));

        // 3. Recent Food Entries Card
        JPanel recentEntriesCard = createRecentEntriesCard();
        contentArea.add(recentEntriesCard);

        JScrollPane scrollPane = new JScrollPane(contentArea);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(14);

        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createTopBanner() {
        JPanel banner = new JPanel(new BorderLayout(16, 0));
        banner.setOpaque(false);

        JPanel textGroup = new JPanel();
        textGroup.setOpaque(false);
        textGroup.setLayout(new BoxLayout(textGroup, BoxLayout.Y_AXIS));

        greetingLabel = new JLabel("Good Morning, User!");
        greetingLabel.setFont(UITheme.FONT_TITLE);
        greetingLabel.setForeground(UITheme.TEXT_PRIMARY);

        dateLabel = new JLabel(LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy")));
        dateLabel.setFont(UITheme.FONT_REGULAR);
        dateLabel.setForeground(UITheme.TEXT_SECONDARY);

        textGroup.add(greetingLabel);
        textGroup.add(Box.createRigidArea(new Dimension(0, 4)));
        textGroup.add(dateLabel);

        banner.add(textGroup, BorderLayout.WEST);

        // Quick action: Add meal button
        ModernButton quickAddBtn = new ModernButton("+ Log Meal", ModernButton.Style.PRIMARY);
        quickAddBtn.setFont(UITheme.FONT_MEDIUM);
        quickAddBtn.addActionListener(e -> navigateTo(DashboardFrame.PANEL_ADD_MEAL));
        banner.add(quickAddBtn, BorderLayout.EAST);

        return banner;
    }

    private JPanel createMetricsGrid() {
        JPanel grid = new JPanel(new GridLayout(1, 4, 16, 0));
        grid.setOpaque(false);
        grid.setMaximumSize(new Dimension(2000, 140));
        grid.setPreferredSize(new Dimension(900, 140));

        // Calories Card
        caloriesValueLabel = new JLabel("0 / 0 kcal");
        caloriesValueLabel.setFont(UITheme.FONT_HEADER);
        caloriesValueLabel.setForeground(UITheme.TEXT_PRIMARY);
        caloriesProgressBar = new ModernProgressBar(UITheme.COLOR_CALORIES);
        caloriesSubLabel = new JLabel("0% of daily goal");
        caloriesSubLabel.setFont(UITheme.FONT_SMALL);
        caloriesSubLabel.setForeground(UITheme.TEXT_SECONDARY);
        grid.add(buildMetricCard("Calories Consumed", "🔥", caloriesValueLabel, caloriesProgressBar, caloriesSubLabel));

        // Protein Card
        proteinValueLabel = new JLabel("0 / 0 g");
        proteinValueLabel.setFont(UITheme.FONT_HEADER);
        proteinValueLabel.setForeground(UITheme.TEXT_PRIMARY);
        proteinProgressBar = new ModernProgressBar(UITheme.COLOR_PROTEIN);
        proteinSubLabel = new JLabel("0% of daily target");
        proteinSubLabel.setFont(UITheme.FONT_SMALL);
        proteinSubLabel.setForeground(UITheme.TEXT_SECONDARY);
        grid.add(buildMetricCard("Protein", "🥩", proteinValueLabel, proteinProgressBar, proteinSubLabel));

        // Carbs Card
        carbsValueLabel = new JLabel("0 / 0 g");
        carbsValueLabel.setFont(UITheme.FONT_HEADER);
        carbsValueLabel.setForeground(UITheme.TEXT_PRIMARY);
        carbsProgressBar = new ModernProgressBar(UITheme.COLOR_CARBS);
        carbsSubLabel = new JLabel("0% of daily target");
        carbsSubLabel.setFont(UITheme.FONT_SMALL);
        carbsSubLabel.setForeground(UITheme.TEXT_SECONDARY);
        grid.add(buildMetricCard("Carbohydrates", "🌾", carbsValueLabel, carbsProgressBar, carbsSubLabel));

        // Fat Card
        fatValueLabel = new JLabel("0 / 0 g");
        fatValueLabel.setFont(UITheme.FONT_HEADER);
        fatValueLabel.setForeground(UITheme.TEXT_PRIMARY);
        fatProgressBar = new ModernProgressBar(UITheme.COLOR_FAT);
        fatSubLabel = new JLabel("0% of daily target");
        fatSubLabel.setFont(UITheme.FONT_SMALL);
        fatSubLabel.setForeground(UITheme.TEXT_SECONDARY);
        grid.add(buildMetricCard("Fat", "🥑", fatValueLabel, fatProgressBar, fatSubLabel));

        return grid;
    }

    private CardPanel buildMetricCard(String title, String icon, JLabel valueLabel,
                                      ModernProgressBar bar, JLabel subLabel) {
        CardPanel card = new CardPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setOpaque(false);
        JLabel lblTitle = new JLabel(icon + " " + title);
        lblTitle.setFont(UITheme.FONT_MEDIUM);
        lblTitle.setForeground(UITheme.TEXT_SECONDARY);
        titleRow.add(lblTitle, BorderLayout.WEST);

        card.add(titleRow);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(valueLabel);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(bar);
        card.add(Box.createRigidArea(new Dimension(0, 8)));
        card.add(subLabel);

        return card;
    }

    private JPanel createMiddleSection() {
        JPanel middle = new JPanel(new GridLayout(1, 2, 16, 0));
        middle.setOpaque(false);
        middle.setMaximumSize(new Dimension(2000, 185));
        middle.setPreferredSize(new Dimension(900, 185));

        // 1. Meal Summary Card
        CardPanel mealSummaryCard = new CardPanel();
        mealSummaryCard.setLayout(new BorderLayout(0, 12));

        JLabel titleMeal = new JLabel("🍽️ Today's Meals Summary");
        titleMeal.setFont(UITheme.FONT_SUBHEADER);
        titleMeal.setForeground(UITheme.TEXT_PRIMARY);
        mealSummaryCard.add(titleMeal, BorderLayout.NORTH);

        JPanel mealGrid = new JPanel(new GridLayout(2, 2, 12, 12));
        mealGrid.setOpaque(false);

        breakfastCountLabel = new JLabel("0 items");
        lunchCountLabel = new JLabel("0 items");
        dinnerCountLabel = new JLabel("0 items");
        snacksCountLabel = new JLabel("0 items");

        mealGrid.add(createMealSlot("🌅 Breakfast", breakfastCountLabel, MealEntry.MEAL_BREAKFAST));
        mealGrid.add(createMealSlot("☀️ Lunch", lunchCountLabel, MealEntry.MEAL_LUNCH));
        mealGrid.add(createMealSlot("🌙 Dinner", dinnerCountLabel, MealEntry.MEAL_DINNER));
        mealGrid.add(createMealSlot("🍎 Snacks", snacksCountLabel, MealEntry.MEAL_SNACKS));

        mealSummaryCard.add(mealGrid, BorderLayout.CENTER);
        middle.add(mealSummaryCard);

        // 2. Health & Daily Goal Overview Card
        CardPanel statsCard = new CardPanel();
        statsCard.setLayout(new BorderLayout(0, 12));

        JLabel titleStats = new JLabel("🎯 Daily Goal & Health Metrics");
        titleStats.setFont(UITheme.FONT_SUBHEADER);
        titleStats.setForeground(UITheme.TEXT_PRIMARY);
        statsCard.add(titleStats, BorderLayout.NORTH);

        JPanel statsGrid = new JPanel(new GridLayout(2, 2, 12, 12));
        statsGrid.setOpaque(false);

        remainingCaloriesLabel = new JLabel("0 kcal");
        totalMealsLabel = new JLabel("0 meals");
        goalPercentLabel = new JLabel("0%");
        bmiValueLabel = new JLabel("0.0");

        statsGrid.add(createStatSlot("Remaining Calories", remainingCaloriesLabel, UITheme.COLOR_CALORIES));
        statsGrid.add(createStatSlot("Goal Completion", goalPercentLabel, UITheme.PRIMARY));
        statsGrid.add(createStatSlot("Total Meals Logged", totalMealsLabel, UITheme.TEXT_PRIMARY));
        statsGrid.add(createStatSlot("BMI (Body Mass Index)", bmiValueLabel, UITheme.COLOR_PROTEIN));

        statsCard.add(statsGrid, BorderLayout.CENTER);
        middle.add(statsCard);

        return middle;
    }

    private JPanel createMealSlot(String name, JLabel countLabel, String mealType) {
        JPanel slot = new JPanel(new BorderLayout(8, 0));
        slot.setBackground(new Color(248, 250, 252));
        slot.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.CARD_BORDER, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));

        JLabel lblName = new JLabel(name);
        lblName.setFont(UITheme.FONT_MEDIUM);
        lblName.setForeground(UITheme.TEXT_PRIMARY);

        countLabel.setFont(UITheme.FONT_SMALL);
        countLabel.setForeground(UITheme.TEXT_SECONDARY);

        text.add(lblName);
        text.add(countLabel);
        slot.add(text, BorderLayout.CENTER);

        JButton addBtn = new JButton("+");
        addBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        addBtn.setForeground(UITheme.PRIMARY);
        addBtn.setOpaque(false);
        addBtn.setContentAreaFilled(false);
        addBtn.setBorderPainted(false);
        addBtn.setFocusPainted(false);
        addBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addBtn.setToolTipText("Add food to " + mealType);
        addBtn.addActionListener(e -> {
            mainFrame.getAddMealPanel().setPreselectedMealType(mealType);
            navigateTo(DashboardFrame.PANEL_ADD_MEAL);
        });
        slot.add(addBtn, BorderLayout.EAST);

        return slot;
    }

    private JPanel createStatSlot(String title, JLabel valueLabel, Color accent) {
        JPanel slot = new JPanel(new BorderLayout());
        slot.setBackground(new Color(248, 250, 252));
        slot.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.CARD_BORDER, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(UITheme.FONT_SMALL);
        lblTitle.setForeground(UITheme.TEXT_SECONDARY);

        valueLabel.setFont(UITheme.FONT_HEADER);
        valueLabel.setForeground(accent);

        slot.add(lblTitle, BorderLayout.NORTH);
        slot.add(valueLabel, BorderLayout.CENTER);
        return slot;
    }

    private JPanel createRecentEntriesCard() {
        CardPanel card = new CardPanel();
        card.setLayout(new BorderLayout(0, 12));
        card.setPreferredSize(new Dimension(900, 240));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("🕒 Recent Food Logs");
        title.setFont(UITheme.FONT_SUBHEADER);
        title.setForeground(UITheme.TEXT_PRIMARY);
        header.add(title, BorderLayout.WEST);

        ModernButton viewAllBtn = new ModernButton("View Full History →", ModernButton.Style.OUTLINE);
        viewAllBtn.setFont(UITheme.FONT_SMALL);
        viewAllBtn.addActionListener(e -> navigateTo(DashboardFrame.PANEL_HISTORY));
        header.add(viewAllBtn, BorderLayout.EAST);

        card.add(header, BorderLayout.NORTH);

        String[] columns = {"Date", "Meal Type", "Food Name", "Quantity (g)", "Calories (kcal)", "Protein (g)"};
        recentTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        ModernTable table = new ModernTable(recentTableModel);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(UITheme.CARD_BORDER));
        scroll.getViewport().setBackground(Color.WHITE);

        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    @Override
    public void refreshData() {
        User user = service.getUser();
        if (user == null) {
            user = new User();
        }

        // 1. Dynamic Greeting
        LocalTime now = LocalTime.now();
        String timeGreeting;
        if (now.getHour() < 12) {
            timeGreeting = "Good Morning";
        } else if (now.getHour() < 17) {
            timeGreeting = "Good Afternoon";
        } else {
            timeGreeting = "Good Evening";
        }
        greetingLabel.setText(timeGreeting + ", " + user.getName() + "!");
        dateLabel.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy")));

        // 2. Real Calculations for Today
        double calConsumed = service.getCaloriesConsumedToday();
        double calGoal = user.getDailyCalorieGoal();
        double calPct = NutritionCalculator.calculateCompletionPercentage(calConsumed, calGoal);
        caloriesValueLabel.setText(String.format("%.0f / %.0f kcal", calConsumed, calGoal));
        caloriesProgressBar.setProgress(calConsumed, calGoal);
        caloriesSubLabel.setText(String.format("%.1f%% of daily target", calPct));

        double proteinConsumed = service.getProteinConsumedToday();
        double proteinGoal = user.getDailyProteinGoal();
        double proteinPct = NutritionCalculator.calculateCompletionPercentage(proteinConsumed, proteinGoal);
        proteinValueLabel.setText(String.format("%.1f / %.1f g", proteinConsumed, proteinGoal));
        proteinProgressBar.setProgress(proteinConsumed, proteinGoal);
        proteinSubLabel.setText(String.format("%.1f%% of daily target", proteinPct));

        double carbsConsumed = service.getCarbsConsumedToday();
        double carbsGoal = user.getDailyCarbGoal();
        double carbsPct = NutritionCalculator.calculateCompletionPercentage(carbsConsumed, carbsGoal);
        carbsValueLabel.setText(String.format("%.1f / %.1f g", carbsConsumed, carbsGoal));
        carbsProgressBar.setProgress(carbsConsumed, carbsGoal);
        carbsSubLabel.setText(String.format("%.1f%% of daily target", carbsPct));

        double fatConsumed = service.getFatConsumedToday();
        double fatGoal = user.getDailyFatGoal();
        double fatPct = NutritionCalculator.calculateCompletionPercentage(fatConsumed, fatGoal);
        fatValueLabel.setText(String.format("%.1f / %.1f g", fatConsumed, fatGoal));
        fatProgressBar.setProgress(fatConsumed, fatGoal);
        fatSubLabel.setText(String.format("%.1f%% of daily target", fatPct));

        // 3. Meal counts for today
        breakfastCountLabel.setText(service.getMealCountForToday(MealEntry.MEAL_BREAKFAST) + " items");
        lunchCountLabel.setText(service.getMealCountForToday(MealEntry.MEAL_LUNCH) + " items");
        dinnerCountLabel.setText(service.getMealCountForToday(MealEntry.MEAL_DINNER) + " items");
        snacksCountLabel.setText(service.getMealCountForToday(MealEntry.MEAL_SNACKS) + " items");

        // 4. Additional stats
        totalMealsLabel.setText(service.getTotalMealsToday() + " meals");
        double remaining = NutritionCalculator.calculateRemaining(calConsumed, calGoal);
        remainingCaloriesLabel.setText(String.format("%.0f kcal", remaining));
        goalPercentLabel.setText(String.format("%.1f%%", calPct));

        double bmi = user.calculateBMI();
        bmiValueLabel.setText(String.format("%.1f (%s)", bmi, user.getBMICategory()));

        // 5. Recent food entries
        recentTableModel.setRowCount(0);
        List<MealEntry> recents = service.getRecentMeals(5);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MMM");
        for (MealEntry m : recents) {
            recentTableModel.addRow(new Object[]{
                    m.getDate().format(dtf),
                    m.getMealType(),
                    m.getFoodName(),
                    String.format("%.0f g", m.getQuantityGrams()),
                    String.format("%.1f kcal", m.getCalories()),
                    String.format("%.1f g", m.getProtein())
            });
        }
    }
}
