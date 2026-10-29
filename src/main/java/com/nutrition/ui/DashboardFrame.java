package com.nutrition.ui;

import com.nutrition.service.INutritionService;
import com.nutrition.ui.theme.UITheme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * Main Application Window containing the modern sidebar navigation
 * and CardLayout content switcher.
 */
public class DashboardFrame extends JFrame {
    public static final String PANEL_DASHBOARD = "DASHBOARD";
    public static final String PANEL_ADD_MEAL = "ADD_MEAL";
    public static final String PANEL_FOOD_DB = "FOOD_DB";
    public static final String PANEL_HISTORY = "HISTORY";
    public static final String PANEL_REPORTS = "REPORTS";
    public static final String PANEL_PROFILE = "PROFILE";
    public static final String PANEL_SETTINGS = "SETTINGS";

    private final INutritionService service;

    private CardLayout cardLayout;
    private JPanel contentContainer;

    // Sidebar navigation buttons
    private final Map<String, JButton> navButtons = new HashMap<>();
    private String currentPanelName = PANEL_DASHBOARD;

    // Specific panel references
    private DashboardPanel dashboardPanel;
    private AddMealPanel addMealPanel;
    private FoodDatabasePanel foodDatabasePanel;
    private HistoryPanel historyPanel;
    private ReportsPanel reportsPanel;
    private ProfilePanel profilePanel;
    private SettingsPanel settingsPanel;

    // Sidebar bottom user card
    private JLabel userCardNameLabel;
    private JLabel userCardGoalLabel;

    public DashboardFrame(INutritionService service) {
        super("NutriTrack - Dietary Calorie Intake & Nutrition Log System");
        this.service = service;

        initWindow();
        initSidebarAndContent();
        showPanel(PANEL_DASHBOARD);
    }

    private void initWindow() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1240, 820);
        setMinimumSize(new Dimension(1020, 680));
        setLocationRelativeTo(null);

        // Auto-save on window close
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (service != null) {
                    service.saveAllData();
                }
            }
        });
    }

    private void initSidebarAndContent() {
        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBackground(UITheme.BG_MAIN);

        // West: Sidebar
        JPanel sidebar = createSidebar();
        rootPanel.add(sidebar, BorderLayout.WEST);

        // Center: CardLayout content area
        cardLayout = new CardLayout();
        contentContainer = new JPanel(cardLayout);
        contentContainer.setBackground(UITheme.BG_MAIN);

        dashboardPanel = new DashboardPanel(service, this);
        addMealPanel = new AddMealPanel(service, this);
        foodDatabasePanel = new FoodDatabasePanel(service, this);
        historyPanel = new HistoryPanel(service, this);
        reportsPanel = new ReportsPanel(service, this);
        profilePanel = new ProfilePanel(service, this);
        settingsPanel = new SettingsPanel(service, this);

        contentContainer.add(dashboardPanel, PANEL_DASHBOARD);
        contentContainer.add(addMealPanel, PANEL_ADD_MEAL);
        contentContainer.add(foodDatabasePanel, PANEL_FOOD_DB);
        contentContainer.add(historyPanel, PANEL_HISTORY);
        contentContainer.add(reportsPanel, PANEL_REPORTS);
        contentContainer.add(profilePanel, PANEL_PROFILE);
        contentContainer.add(settingsPanel, PANEL_SETTINGS);

        rootPanel.add(contentContainer, BorderLayout.CENTER);
        setContentPane(rootPanel);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(245, 0));
        sidebar.setBackground(UITheme.BG_SIDEBAR);
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, UITheme.CARD_BORDER));

        // 1. Sidebar Brand Header
        JPanel brandPanel = new JPanel();
        brandPanel.setOpaque(false);
        brandPanel.setLayout(new BoxLayout(brandPanel, BoxLayout.Y_AXIS));
        brandPanel.setBorder(BorderFactory.createEmptyBorder(24, 20, 24, 20));

        JLabel brandTitle = new JLabel("🥗 NutriTrack");
        brandTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        brandTitle.setForeground(UITheme.PRIMARY_DARK);

        JLabel brandSub = new JLabel("Calorie & Nutrition Log");
        brandSub.setFont(UITheme.FONT_SMALL);
        brandSub.setForeground(UITheme.TEXT_MUTED);

        brandPanel.add(brandTitle);
        brandPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        brandPanel.add(brandSub);
        sidebar.add(brandPanel, BorderLayout.NORTH);

        // 2. Navigation Menu List
        JPanel menuPanel = new JPanel();
        menuPanel.setOpaque(false);
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));

        menuPanel.add(createNavButton("📊  Dashboard", PANEL_DASHBOARD));
        menuPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        menuPanel.add(createNavButton("➕  Add Meal", PANEL_ADD_MEAL));
        menuPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        menuPanel.add(createNavButton("🍎  Food Database", PANEL_FOOD_DB));
        menuPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        menuPanel.add(createNavButton("🕒  Nutrition History", PANEL_HISTORY));
        menuPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        menuPanel.add(createNavButton("📈  Reports", PANEL_REPORTS));
        menuPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        menuPanel.add(createNavButton("👤  Profile", PANEL_PROFILE));
        menuPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        menuPanel.add(createNavButton("⚙️  Settings", PANEL_SETTINGS));

        sidebar.add(menuPanel, BorderLayout.CENTER);

        // 3. User Quick Card at Bottom
        JPanel userBottomCard = createUserBottomCard();
        sidebar.add(userBottomCard, BorderLayout.SOUTH);

        return sidebar;
    }

    private JButton createNavButton(String text, String panelKey) {
        JButton btn = new JButton(text);
        btn.setFont(UITheme.FONT_MEDIUM);
        btn.setForeground(UITheme.TEXT_SECONDARY);
        btn.setBackground(Color.WHITE);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(220, 42));
        btn.setPreferredSize(new Dimension(220, 42));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));

        btn.addActionListener(e -> showPanel(panelKey));
        navButtons.put(panelKey, btn);
        return btn;
    }

    private JPanel createUserBottomCard() {
        JPanel card = new JPanel(new BorderLayout(10, 0));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.CARD_BORDER),
                BorderFactory.createEmptyBorder(16, 16, 18, 16)
        ));

        // Avatar circle with initial
        JPanel avatar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                UITheme.applyAntiAliasing(g2);
                g2.setColor(UITheme.PRIMARY_LIGHT);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.setColor(UITheme.PRIMARY_DARK);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
                FontMetrics fm = g2.getFontMetrics();
                String initial = service.getUser() != null && !service.getUser().getName().isEmpty()
                        ? service.getUser().getName().substring(0, 1).toUpperCase()
                        : "U";
                int tx = (getWidth() - fm.stringWidth(initial)) / 2;
                int ty = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(initial, tx, ty);
                g2.dispose();
            }
        };
        avatar.setPreferredSize(new Dimension(38, 38));
        avatar.setOpaque(false);
        card.add(avatar, BorderLayout.WEST);

        JPanel textGroup = new JPanel();
        textGroup.setOpaque(false);
        textGroup.setLayout(new BoxLayout(textGroup, BoxLayout.Y_AXIS));

        String userName = service.getUser() != null ? service.getUser().getName() : "User";
        double calGoal = service.getUser() != null ? service.getUser().getDailyCalorieGoal() : 2200;

        userCardNameLabel = new JLabel(userName);
        userCardNameLabel.setFont(UITheme.FONT_MEDIUM);
        userCardNameLabel.setForeground(UITheme.TEXT_PRIMARY);

        userCardGoalLabel = new JLabel(String.format("Goal: %.0f kcal", calGoal));
        userCardGoalLabel.setFont(UITheme.FONT_SMALL);
        userCardGoalLabel.setForeground(UITheme.TEXT_MUTED);

        textGroup.add(userCardNameLabel);
        textGroup.add(userCardGoalLabel);
        card.add(textGroup, BorderLayout.CENTER);

        return card;
    }

    public void showPanel(String panelKey) {
        this.currentPanelName = panelKey;
        cardLayout.show(contentContainer, panelKey);

        // Highlight selected nav button
        for (Map.Entry<String, JButton> entry : navButtons.entrySet()) {
            JButton btn = entry.getValue();
            if (entry.getKey().equals(panelKey)) {
                btn.setBackground(UITheme.ACTIVE_MENU_BG);
                btn.setForeground(UITheme.PRIMARY_DARK);
            } else {
                btn.setBackground(Color.WHITE);
                btn.setForeground(UITheme.TEXT_SECONDARY);
            }
        }

        // Refresh panel data
        switch (panelKey) {
            case PANEL_DASHBOARD:
                dashboardPanel.refreshData();
                break;
            case PANEL_ADD_MEAL:
                addMealPanel.refreshData();
                break;
            case PANEL_FOOD_DB:
                foodDatabasePanel.refreshData();
                break;
            case PANEL_HISTORY:
                historyPanel.refreshData();
                break;
            case PANEL_REPORTS:
                reportsPanel.refreshData();
                break;
            case PANEL_PROFILE:
                profilePanel.refreshData();
                break;
            case PANEL_SETTINGS:
                settingsPanel.refreshData();
                break;
        }

        updateSidebarUserCard();
    }

    public void refreshAllPanels() {
        if (dashboardPanel != null) dashboardPanel.refreshData();
        if (addMealPanel != null) addMealPanel.refreshData();
        if (foodDatabasePanel != null) foodDatabasePanel.refreshData();
        if (historyPanel != null) historyPanel.refreshData();
        if (reportsPanel != null) reportsPanel.refreshData();
        if (profilePanel != null) profilePanel.refreshData();
        if (settingsPanel != null) settingsPanel.refreshData();
        updateSidebarUserCard();
    }

    private void updateSidebarUserCard() {
        if (service.getUser() != null) {
            userCardNameLabel.setText(service.getUser().getName());
            userCardGoalLabel.setText(String.format("Goal: %.0f kcal", service.getUser().getDailyCalorieGoal()));
            repaint();
        }
    }

    public AddMealPanel getAddMealPanel() {
        return addMealPanel;
    }
}
