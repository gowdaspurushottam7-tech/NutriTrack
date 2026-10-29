package com.nutrition;

import com.nutrition.service.NutritionService;
import com.nutrition.ui.DashboardFrame;

import javax.swing.*;

/**
 * Main application launcher for Dietary Calorie Intake & Nutrition Log System.
 * 
 * B.Tech Computer Science Engineering (IoT) Academic Project
 * Author: Purushottam
 */
public class Main {
    public static final String APP_NAME = "NutriTrack - Dietary Calorie Intake & Nutrition Log System";
    public static final String APP_VERSION = "1.0.0";

    public static void main(String[] args) {
        // Configure high-DPI scaling and font smoothing for Windows
        System.setProperty("sun.java2d.uiScale.enabled", "true");
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        System.out.println("===============================================================");
        System.out.println("  " + APP_NAME);
        System.out.println("  Version: " + APP_VERSION);
        System.out.println("  Status: Initializing Core Services and Local Data...");
        System.out.println("===============================================================");

        // Set native system Look and Feel for clean Windows desktop integration
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Notice: Could not set System Look and Feel, using standard Swing theme.");
        }

        // Initialize Services & Launch GUI on the Swing Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            try {
                NutritionService service = new NutritionService();
                DashboardFrame frame = new DashboardFrame(service);
                frame.setVisible(true);
                System.out.println(">> Application GUI successfully launched.");
            } catch (Exception e) {
                System.err.println("Fatal Error during application startup: " + e.getMessage());
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                        "An unexpected error occurred while launching NutriTrack:\n" + e.getMessage(),
                        "Startup Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
