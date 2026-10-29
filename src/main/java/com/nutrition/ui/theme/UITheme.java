package com.nutrition.ui.theme;

import java.awt.*;
import javax.swing.*;

/**
 * Modern design system constants, colors, fonts, and rendering utilities.
 */
public class UITheme {
    // Primary Color Palette
    public static final Color PRIMARY = new Color(22, 163, 74);       // Emerald Green #16A34A
    public static final Color PRIMARY_DARK = new Color(21, 128, 61);  // Darker Green #15803D
    public static final Color PRIMARY_LIGHT = new Color(220, 252, 231);// Light Mint Green #DCFCE7

    // Secondary Macro Colors
    public static final Color COLOR_CALORIES = new Color(16, 185, 129); // Emerald
    public static final Color COLOR_PROTEIN = new Color(37, 99, 235);   // Royal Blue
    public static final Color COLOR_CARBS = new Color(245, 158, 11);    // Amber
    public static final Color COLOR_FAT = new Color(239, 68, 68);       // Crimson

    // Background & Surface Colors
    public static final Color BG_MAIN = new Color(248, 250, 252);     // Light Slate #F8FAFC
    public static final Color BG_SIDEBAR = new Color(255, 255, 255);  // White Sidebar
    public static final Color CARD_BG = new Color(255, 255, 255);     // Pure White Card
    public static final Color CARD_BORDER = new Color(226, 232, 240); // Soft Slate Border

    // Typography Colors
    public static final Color TEXT_PRIMARY = new Color(15, 23, 42);   // Near Black #0F172A
    public static final Color TEXT_SECONDARY = new Color(100, 116, 139);// Muted Slate #64748B
    public static final Color TEXT_MUTED = new Color(148, 163, 184);  // Light Slate #94A3B8

    // Interactive States
    public static final Color HOVER_COLOR = new Color(241, 245, 249);
    public static final Color ACTIVE_MENU_BG = new Color(236, 253, 245); // Mint active
    public static final Color DANGER = new Color(220, 38, 38);
    public static final Color WARNING = new Color(217, 119, 6);

    // Modern Fonts
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 17);
    public static final Font FONT_SUBHEADER = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_MEDIUM = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_BIG_NUMBER = new Font("Segoe UI", Font.BOLD, 26);

    /**
     * Applies high-quality rendering hints (anti-aliasing) to graphics context.
     */
    public static void applyAntiAliasing(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    }
}
