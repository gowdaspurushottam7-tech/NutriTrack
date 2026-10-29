package com.nutrition.ui.components;

import com.nutrition.ui.theme.UITheme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * A modern flat button with rounded corners, smooth hover feedback, and custom color styles.
 */
public class ModernButton extends JButton {
    public enum Style {
        PRIMARY, SECONDARY, OUTLINE, DANGER, SUCCESS
    }

    private final Style style;
    private boolean isHovered = false;
    private int cornerRadius = 10;

    public ModernButton(String text) {
        this(text, Style.PRIMARY);
    }

    public ModernButton(String text, Style style) {
        super(text);
        this.style = style;
        setFont(UITheme.FONT_MEDIUM);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setMargin(new Insets(10, 18, 10, 18));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        UITheme.applyAntiAliasing(g2);

        int w = getWidth();
        int h = getHeight();

        Color bg;
        Color fg;
        Color border = null;

        switch (style) {
            case PRIMARY:
                bg = isHovered ? UITheme.PRIMARY_DARK : UITheme.PRIMARY;
                fg = Color.WHITE;
                break;
            case SECONDARY:
                bg = isHovered ? new Color(226, 232, 240) : new Color(241, 245, 249);
                fg = UITheme.TEXT_PRIMARY;
                border = UITheme.CARD_BORDER;
                break;
            case OUTLINE:
                bg = isHovered ? UITheme.PRIMARY_LIGHT : Color.WHITE;
                fg = UITheme.PRIMARY_DARK;
                border = UITheme.PRIMARY;
                break;
            case DANGER:
                bg = isHovered ? new Color(185, 28, 28) : UITheme.DANGER;
                fg = Color.WHITE;
                break;
            case SUCCESS:
            default:
                bg = isHovered ? new Color(16, 185, 129) : UITheme.PRIMARY;
                fg = Color.WHITE;
                break;
        }

        // Fill background
        g2.setColor(bg);
        g2.fillRoundRect(0, 0, w - 1, h - 1, cornerRadius, cornerRadius);

        // Draw border if needed
        if (border != null) {
            g2.setColor(border);
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawRoundRect(0, 0, w - 1, h - 1, cornerRadius, cornerRadius);
        }

        // Draw text
        g2.setColor(fg);
        g2.setFont(getFont());
        FontMetrics fm = g2.getFontMetrics();
        int textX = (w - fm.stringWidth(getText())) / 2;
        int textY = (h + fm.getAscent() - fm.getDescent()) / 2;
        g2.drawString(getText(), textX, textY);

        g2.dispose();
    }

    public void setCornerRadius(int radius) {
        this.cornerRadius = radius;
        repaint();
    }
}
