package com.nutrition.ui.components;

import com.nutrition.ui.theme.UITheme;

import javax.swing.*;
import java.awt.*;

/**
 * A modern white rounded card panel with soft border.
 */
public class CardPanel extends JPanel {
    private int cornerRadius = 16;
    private Color backgroundColor = UITheme.CARD_BG;
    private Color borderColor = UITheme.CARD_BORDER;

    public CardPanel() {
        this(16);
    }

    public CardPanel(int cornerRadius) {
        this.cornerRadius = cornerRadius;
        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));
    }

    public CardPanel(int cornerRadius, Color bg, Color border) {
        this.cornerRadius = cornerRadius;
        this.backgroundColor = bg;
        this.borderColor = border;
        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        UITheme.applyAntiAliasing(g2);

        int width = getWidth();
        int height = getHeight();

        // Draw background
        g2.setColor(backgroundColor);
        g2.fillRoundRect(0, 0, width - 1, height - 1, cornerRadius, cornerRadius);

        // Draw soft border
        g2.setColor(borderColor);
        g2.setStroke(new BasicStroke(1f));
        g2.drawRoundRect(0, 0, width - 1, height - 1, cornerRadius, cornerRadius);

        g2.dispose();
        super.paintComponent(g);
    }

    public void setBackgroundColor(Color color) {
        this.backgroundColor = color;
        repaint();
    }
}
