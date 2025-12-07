package com.shootingstar.scouter.views;

import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SortOrder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;

public class WaveTimersCard extends JPanel
{
    private final JTable table;
    private final DefaultTableModel tableModel;

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
        table.getTableHeader().setFont(FontManager.getRunescapeBoldFont());
        table.getTableHeader().setForeground(ColorScheme.LIGHT_GRAY_COLOR);
        
        // Create custom row sorter with comparator for spawn time column (index 1)
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        sorter.setComparator(1, (o1, o2) -> {
            String s1 = (String) o1;
            String s2 = (String) o2;
            
            // Handle "?" case, meaning no data available for that world
            if (s1.equals("?") && s2.equals("?")) return 0;
            if (s1.equals("?")) return 1; // Put "?" at the end
            if (s2.equals("?")) return -1;
            
            // Parse time strings (e.g., "5 min" -> 5)
            int time1 = Integer.parseInt(s1.replaceAll("\\D+", ""));
            int time2 = Integer.parseInt(s2.replaceAll("\\D+", ""));
            return Integer.compare(time1, time2);
        });
        table.setRowSorter(sorter);

        // Set initial sorting by World column (index 0) in ascending order
        sorter.setSortKeys(java.util.Collections.singletonList(
            new javax.swing.RowSorter.SortKey(0, SortOrder.ASCENDING)));

        // Set column widths
        // Set column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(60);
        table.getColumnModel().getColumn(1).setPreferredWidth(90);

        add(table.getTableHeader());
        add(table);
    }

    public void setMessage(String msg)
    {
        // For backwards compatibility - not used with table
    }
    
    public void updateTimers(java.util.List<WorldTimer> timers)
    {
        // Optimize updates by only modifying changed rows
        if (timers.size() == tableModel.getRowCount()) {
            boolean needsUpdate = false;
            for (int i = 0; i < timers.size(); i++) {
            WorldTimer timer = timers.get(i);
            // Check if row data differs (need to check against sorted view)
            int modelRow = table.convertRowIndexToModel(i);
            String currentWorld = (String) tableModel.getValueAt(modelRow, 0);
            String currentTime = (String) tableModel.getValueAt(modelRow, 1);
            
            if (!timer.world.equals(currentWorld) || !timer.spawnTime.equals(currentTime)) {
                needsUpdate = true;
                break;
            }
            }
            
            if (!needsUpdate) {
            return; // No changes, skip update
            }
        }
        // Clear existing rows
        tableModel.setRowCount(0);
        
        // Add new rows
        for (WorldTimer timer : timers) {
            tableModel.addRow(new Object[]{timer.world, timer.spawnTime});
        }
    }
    
    public static class WorldTimer
    {
        public final String world;
        public final String spawnTime;
        
        public WorldTimer(String world, String spawnTime)
        {
            this.world = world;
            this.spawnTime = spawnTime;
        }
    }
}
