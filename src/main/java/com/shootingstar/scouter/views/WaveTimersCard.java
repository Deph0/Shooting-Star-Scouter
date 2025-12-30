package com.shootingstar.scouter.views;

import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import lombok.Getter;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import java.util.Collections;

public class WaveTimersCard extends JPanel
{
    private final JTable table;
    @Getter private final DefaultTableModel tableModel;

    public WaveTimersCard()
    {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        setBackground(ColorScheme.DARK_GRAY_COLOR);
        
        // Create table model with columns
        String[] columnNames = {"World", "Spawn Time"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        
        // Create table
        table = new JTable(tableModel);
        table.setFont(FontManager.getRunescapeFont());
        table.setBackground(ColorScheme.DARK_GRAY_COLOR);
        table.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
        table.setGridColor(ColorScheme.DARK_GRAY_COLOR.brighter());
        table.setShowGrid(true);
        // table.setRowHeight(24);
        table.getTableHeader().setFont(FontManager.getRunescapeBoldFont());
        table.getTableHeader().setForeground(ColorScheme.LIGHT_GRAY_COLOR);
        table.getTableHeader().setBackground(ColorScheme.DARK_GRAY_COLOR);
        
        // Create custom row sorter with comparator for spawn time column (index 1)
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        sorter.setComparator(1, (a, b) -> compareSpawnTimes((String) a, (String) b));
        table.setRowSorter(sorter);

        // Set initial sorting by World column (index 0) in ascending order
        sorter.setSortKeys(Collections.singletonList(
            new RowSorter.SortKey(0, SortOrder.ASCENDING)));

        // Set column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(60);
        table.getColumnModel().getColumn(1).setPreferredWidth(90);

        // Wrap in scroll pane
        // JScrollPane scrollPane = new JScrollPane(table);
        // scrollPane.setBackground(ColorScheme.DARK_GRAY_COLOR);
        // scrollPane.getViewport().setBackground(ColorScheme.DARK_GRAY_COLOR);
        // add(scrollPane);

        add(table.getTableHeader());
        add(table);
    }
    
    // Custom sorter logic for spawn times column
    private int compareSpawnTimes(String a, String b)
    {
        // Handle "?" case, meaning no data available for that world
        if (a.equals("?") && b.equals("?")) return 0; // Equal in terms of sorting
        if (a.equals("?")) return 1; // a comes before b in the sorted order
        if (b.equals("?")) return -1; // b comes before a in the sorted order
        
        // Parse time strings (e.g., "5 min" -> 5)
        int time1 = Integer.parseInt(a.replaceAll("\\D+", ""));
        int time2 = Integer.parseInt(b.replaceAll("\\D+", ""));
        return Integer.compare(time1, time2);
    }
}
