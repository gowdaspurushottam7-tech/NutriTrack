package com.nutrition.ui;

import com.nutrition.service.INutritionService;
import com.nutrition.ui.theme.UITheme;

import javax.swing.*;
import java.awt.*;

/**
 * Abstract Base Panel providing common services, navigation capabilities,
 * design consistency, and lifecycle hooks.
 * Demonstrates Object-Oriented Principles: Inheritance, Abstraction, and Polymorphism.
 */
public abstract class BasePanel extends JPanel {
    protected final INutritionService service;
    protected final DashboardFrame mainFrame;

    public BasePanel(INutritionService service, DashboardFrame mainFrame) {
        this.service = service;
        this.mainFrame = mainFrame;
        setBackground(UITheme.BG_MAIN);
        setLayout(new BorderLayout(16, 16));
        setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));
    }

    /**
     * Hook called whenever the panel is navigated to or when underlying data changes.
     * Must be implemented by all subclasses (Polymorphism).
     */
    public abstract void refreshData();

    /**
     * Helper to navigate between sidebar screens.
     */
    protected void navigateTo(String panelName) {
        if (mainFrame != null) {
            mainFrame.showPanel(panelName);
        }
    }

    /**
     * Creates a standardized modern header banner.
     */
    protected JPanel createHeader(String title, String subtitle) {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(UITheme.FONT_TITLE);
        titleLabel.setForeground(UITheme.TEXT_PRIMARY);

        JLabel subLabel = new JLabel(subtitle);
        subLabel.setFont(UITheme.FONT_REGULAR);
        subLabel.setForeground(UITheme.TEXT_SECONDARY);

        textPanel.add(titleLabel);
        textPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        textPanel.add(subLabel);

        headerPanel.add(textPanel, BorderLayout.WEST);
        return headerPanel;
    }

    /**
     * Shows a standardized user-friendly information message dialog.
     */
    public void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Shows a standardized user-friendly error message dialog.
     */
    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Validation Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Shows a confirmation dialog.
     */
    public boolean showConfirm(String message, String title) {
        int res = JOptionPane.showConfirmDialog(this, message, title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        return res == JOptionPane.YES_OPTION;
    }
}
