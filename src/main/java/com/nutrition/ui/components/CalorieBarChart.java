package com.nutrition.ui.components;

import com.nutrition.ui.theme.UITheme;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Custom painted modern 7-day bar chart visualizing daily calorie consumption
 * against the target goal.
 */
public class CalorieBarChart extends JComponent {
    private Map<LocalDate, Double> dailyData = new LinkedHashMap<>();
    private double dailyGoal = 2200.0;
    private final DateTimeFormatter dayFmt = DateTimeFormatter.ofPattern("EEE dd");

    public CalorieBarChart() {
        setPreferredSize(new Dimension(650, 260));
        setOpaque(false);
    }

    public void setData(Map<LocalDate, Double> data, double targetGoal) {
        this.dailyData = data != null ? data : new LinkedHashMap<>();
        this.dailyGoal = targetGoal > 0 ? targetGoal : 2200.0;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        UITheme.applyAntiAliasing(g2);

        int width = getWidth();
        int height = getHeight();

        // Padding
        int padLeft = 60;
        int padRight = 30;
        int padTop = 30;
        int padBottom = 45;

        int chartWidth = width - padLeft - padRight;
        int chartHeight = height - padTop - padBottom;

        if (chartWidth <= 0 || chartHeight <= 0) {
            g2.dispose();
            return;
        }

        // Determine max scale
        double maxCal = dailyGoal * 1.25;
        for (double v : dailyData.values()) {
            if (v > maxCal) maxCal = v * 1.15;
        }
        if (maxCal <= 0) maxCal = 3000;

        // Draw horizontal grid lines and scale labels
        g2.setFont(UITheme.FONT_SMALL);
        FontMetrics fm = g2.getFontMetrics();
        int gridSteps = 4;
        for (int i = 0; i <= gridSteps; i++) {
            double val = (maxCal / gridSteps) * i;
            int y = padTop + chartHeight - (int) ((val / maxCal) * chartHeight);

            // Gridline
            g2.setColor(new Color(241, 245, 249));
            g2.drawLine(padLeft, y, width - padRight, y);

            // Label
            g2.setColor(UITheme.TEXT_MUTED);
            String label = String.format("%.0f", val);
            g2.drawString(label, padLeft - fm.stringWidth(label) - 8, y + 4);
        }

        // Draw Daily Goal Reference Line
        int goalY = padTop + chartHeight - (int) ((dailyGoal / maxCal) * chartHeight);
        if (goalY >= padTop && goalY <= padTop + chartHeight) {
            Stroke dashed = new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{6, 4}, 0);
            g2.setStroke(dashed);
            g2.setColor(new Color(239, 68, 68, 180)); // Red dashed goal line
            g2.drawLine(padLeft, goalY, width - padRight, goalY);

            // Goal label
            g2.setFont(UITheme.FONT_SMALL);
            g2.setColor(new Color(220, 38, 38));
            g2.drawString("Goal: " + String.format("%.0f", dailyGoal) + " kcal", width - padRight - 110, goalY - 6);
        }

        // Draw Bars
        int numBars = dailyData.size();
        if (numBars > 0) {
            int slotWidth = chartWidth / numBars;
            int barWidth = Math.min(38, (int) (slotWidth * 0.55));
            int index = 0;
            LocalDate today = LocalDate.now();

            for (Map.Entry<LocalDate, Double> entry : dailyData.entrySet()) {
                LocalDate date = entry.getKey();
                double calories = entry.getValue();

                int barHeight = (int) ((calories / maxCal) * chartHeight);
                int x = padLeft + (index * slotWidth) + (slotWidth - barWidth) / 2;
                int y = padTop + chartHeight - barHeight;

                // Bar Color (highlight today or over goal)
                boolean isToday = date.equals(today);
                Color barColor;
                if (calories > dailyGoal * 1.1) {
                    barColor = new Color(245, 158, 11); // Amber if high
                } else if (calories > 0) {
                    barColor = isToday ? UITheme.PRIMARY : new Color(52, 211, 153);
                } else {
                    barColor = new Color(226, 232, 240); // Empty day
                }

                // Draw Bar
                g2.setColor(barColor);
                if (barHeight > 0) {
                    g2.fillRoundRect(x, y, barWidth, barHeight, 8, 8);
                } else {
                    // Min dot for 0 calories
                    g2.fillRoundRect(x, padTop + chartHeight - 4, barWidth, 4, 2, 2);
                }

                // Draw Calorie value above bar
                if (calories > 0) {
                    g2.setFont(UITheme.FONT_SMALL);
                    g2.setColor(UITheme.TEXT_PRIMARY);
                    String valText = String.format("%.0f", calories);
                    int valW = fm.stringWidth(valText);
                    g2.drawString(valText, x + (barWidth - valW) / 2, Math.max(padTop - 4, y - 6));
                }

                // Draw X-axis day label
                g2.setFont(isToday ? UITheme.FONT_MEDIUM : UITheme.FONT_SMALL);
                g2.setColor(isToday ? UITheme.PRIMARY_DARK : UITheme.TEXT_SECONDARY);
                String dayText = isToday ? "Today" : date.format(dayFmt);
                int textW = fm.stringWidth(dayText);
                g2.drawString(dayText, x + (barWidth - textW) / 2, height - padBottom + 18);

                index++;
            }
        }

        g2.dispose();
    }
}
