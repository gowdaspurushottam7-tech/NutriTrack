package com.nutrition.ui.components;

import com.nutrition.ui.theme.UITheme;

import javax.swing.*;
import java.awt.*;

/**
 * Modern smooth-rendered progress bar with custom colors and rounded tracks.
 */
public class ModernProgressBar extends JComponent {
    private double progress = 0.0; // 0.0 to 1.0 (or greater than 1.0 for overflow)
    private Color fillColor = UITheme.PRIMARY;
    private Color trackColor = new Color(241, 245, 249);
    private int barHeight = 8;
    private int cornerRadius = 8;

    public ModernProgressBar() {
        setPreferredSize(new Dimension(100, 10));
    }

    public ModernProgressBar(Color fillColor) {
        this();
        this.fillColor = fillColor;
    }

    public void setProgress(double current, double max) {
        if (max <= 0) {
            this.progress = 0.0;
        } else {
            this.progress = current / max;
        }
        repaint();
    }

    public void setFillColor(Color fillColor) {
        this.fillColor = fillColor;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        UITheme.applyAntiAliasing(g2);

        int w = getWidth();
        int h = getHeight();
        int drawH = Math.min(barHeight, h);
        int y = (h - drawH) / 2;

        // Draw track
        g2.setColor(trackColor);
        g2.fillRoundRect(0, y, w, drawH, cornerRadius, cornerRadius);

        // Calculate fill width (capped at w for display, but distinct color if exceeded)
        double clamped = Math.min(1.0, Math.max(0.0, progress));
        int fillW = (int) Math.round(w * clamped);

        if (fillW > 0) {
            if (progress > 1.0) {
                // If over goal (e.g. over calories), tint or draw warm alert
                g2.setColor(progress > 1.15 ? UITheme.WARNING : fillColor);
            } else {
                g2.setColor(fillColor);
            }
            g2.fillRoundRect(0, y, fillW, drawH, cornerRadius, cornerRadius);
        }

        g2.dispose();
    }
}
