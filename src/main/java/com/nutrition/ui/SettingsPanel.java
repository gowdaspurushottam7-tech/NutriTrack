package com.nutrition.ui;

import com.nutrition.service.INutritionService;
import com.nutrition.ui.components.CardPanel;
import com.nutrition.ui.components.ModernButton;
import com.nutrition.ui.theme.UITheme;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * System Settings & Application Administration Screen:
 * Displays runtime details, data backup triggers, and academic system metadata.
 */
public class SettingsPanel extends BasePanel {

    public SettingsPanel(INutritionService service, DashboardFrame mainFrame) {
        super(service, mainFrame);
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 16));

        JPanel header = createHeader("Settings & System Information",
                "Application diagnostics, persistent data management, and project credits.");
        add(header, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        centerPanel.setOpaque(false);

        // Left Card: Application Diagnostics & Environment
        CardPanel diagCard = new CardPanel();
        diagCard.setLayout(new BoxLayout(diagCard, BoxLayout.Y_AXIS));

        JLabel titleDiag = new JLabel("⚙️ System Diagnostics");
        titleDiag.setFont(UITheme.FONT_SUBHEADER);
        titleDiag.setForeground(UITheme.TEXT_PRIMARY);
        diagCard.add(titleDiag);
        diagCard.add(Box.createRigidArea(new Dimension(0, 16)));

        diagCard.add(createDiagRow("Application Name", "Dietary Calorie Intake & Nutrition Log System"));
        diagCard.add(createDiagRow("Version", "1.0.0 (Release Build)"));
        diagCard.add(createDiagRow("Java Runtime", System.getProperty("java.version") + " (" + System.getProperty("java.vendor") + ")"));
        diagCard.add(createDiagRow("Operating System", System.getProperty("os.name") + " (" + System.getProperty("os.arch") + ")"));
        diagCard.add(createDiagRow("Data Directory", new File("data").getAbsolutePath()));
        diagCard.add(createDiagRow("Storage Format", "Java Binary Serialization (.dat files)"));
        diagCard.add(createDiagRow("Architecture", "Model-View-Service Layered Pattern"));

        centerPanel.add(diagCard);

        // Right Card: Storage Controls & Project Info
        CardPanel actionsCard = new CardPanel();
        actionsCard.setLayout(new BoxLayout(actionsCard, BoxLayout.Y_AXIS));

        JLabel titleActions = new JLabel("💾 Data Management");
        titleActions.setFont(UITheme.FONT_SUBHEADER);
        titleActions.setForeground(UITheme.TEXT_PRIMARY);
        actionsCard.add(titleActions);
        actionsCard.add(Box.createRigidArea(new Dimension(0, 16)));

        ModernButton saveNowBtn = new ModernButton("💾 Synchronize / Save Data Now", ModernButton.Style.PRIMARY);
        saveNowBtn.setMaximumSize(new Dimension(400, 38));
        saveNowBtn.addActionListener(e -> {
            service.saveAllData();
            showSuccess("All user profiles, foods, and meal logs synchronized to disk successfully.");
        });

        ModernButton reloadBtn = new ModernButton("🔄 Reload Data From Local Disk", ModernButton.Style.SECONDARY);
        reloadBtn.setMaximumSize(new Dimension(400, 38));
        reloadBtn.addActionListener(e -> {
            service.loadAllData();
            mainFrame.refreshAllPanels();
            showSuccess("Local records successfully reloaded.");
        });

        ModernButton clearBtn = new ModernButton("⚠️ Clear All Meal History Logs", ModernButton.Style.DANGER);
        clearBtn.setMaximumSize(new Dimension(400, 38));
        clearBtn.addActionListener(e -> {
            if (showConfirm("Are you sure you want to clear all meal history records?\nThis cannot be undone.", "Clear All Records")) {
                service.getAllMeals().clear();
                service.saveAllData();
                mainFrame.refreshAllPanels();
                showSuccess("Meal records cleared.");
            }
        });

        actionsCard.add(saveNowBtn);
        actionsCard.add(Box.createRigidArea(new Dimension(0, 12)));
        actionsCard.add(reloadBtn);
        actionsCard.add(Box.createRigidArea(new Dimension(0, 12)));
        actionsCard.add(clearBtn);

        actionsCard.add(Box.createRigidArea(new Dimension(0, 24)));
        JLabel titleProject = new JLabel("🎓 Academic Project Details");
        titleProject.setFont(UITheme.FONT_SUBHEADER);
        titleProject.setForeground(UITheme.TEXT_PRIMARY);
        actionsCard.add(titleProject);
        actionsCard.add(Box.createRigidArea(new Dimension(0, 10)));

        actionsCard.add(createDiagRow("Developer", "Purushottam"));
        actionsCard.add(createDiagRow("Program", "B.Tech Computer Science Engineering (IoT)"));
        actionsCard.add(createDiagRow("Semester", "3rd Semester"));
        actionsCard.add(createDiagRow("Core Concepts", "OOP, Swing GUI, File I/O, Collections, Exceptions"));

        centerPanel.add(actionsCard);

        add(centerPanel, BorderLayout.CENTER);
    }

    private JPanel createDiagRow(String label, String value) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setOpaque(false);
        row.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));

        JLabel lbl = new JLabel(label + ":");
        lbl.setFont(UITheme.FONT_MEDIUM);
        lbl.setForeground(UITheme.TEXT_SECONDARY);

        JLabel val = new JLabel(value);
        val.setFont(UITheme.FONT_REGULAR);
        val.setForeground(UITheme.TEXT_PRIMARY);

        row.add(lbl, BorderLayout.WEST);
        row.add(val, BorderLayout.CENTER);
        return row;
    }

    @Override
    public void refreshData() {
        // Static diagnostics
    }
}
