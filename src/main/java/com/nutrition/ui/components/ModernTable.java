package com.nutrition.ui.components;

import com.nutrition.ui.theme.UITheme;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import java.awt.*;

/**
 * Modern styled JTable with alternating row colors, customized headers, and row padding.
 */
public class ModernTable extends JTable {

    public ModernTable() {
        super();
        initStyle();
    }

    public ModernTable(TableModel model) {
        super(model);
        initStyle();
    }

    private void initStyle() {
        setRowHeight(38);
        setShowVerticalLines(false);
        setShowHorizontalLines(true);
        setGridColor(new Color(241, 245, 249));
        setSelectionBackground(UITheme.PRIMARY_LIGHT);
        setSelectionForeground(UITheme.PRIMARY_DARK);
        setFont(UITheme.FONT_REGULAR);
        setBackground(Color.WHITE);
        setFillsViewportHeight(true);

        // Header Style
        JTableHeader header = getTableHeader();
        header.setFont(UITheme.FONT_SUBHEADER);
        header.setBackground(new Color(248, 250, 252));
        header.setForeground(UITheme.TEXT_SECONDARY);
        header.setPreferredSize(new Dimension(100, 40));
        header.setReorderingAllowed(false);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.CARD_BORDER));

        // Default cell renderer with left padding
        setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                    c.setForeground(UITheme.TEXT_PRIMARY);
                } else {
                    c.setBackground(UITheme.PRIMARY_LIGHT);
                    c.setForeground(UITheme.PRIMARY_DARK);
                }
                return c;
            }
        });
    }
}
