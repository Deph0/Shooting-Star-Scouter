package com.shootingstar.scouter.views;

import lombok.extern.slf4j.Slf4j;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;

import com.shootingstar.scouter.models.StarActionType;
import com.shootingstar.scouter.models.StarData;

import java.awt.*;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.function.BiConsumer;
import java.util.List;

@Slf4j
public class CurrentStarsCard extends JPanel
{
    private final JTable table;
    private final DefaultTableModel tableModel;
    private BiConsumer<String, StarActionType> actionCallback;

    public CurrentStarsCard()
    {
        // setLayout(new BorderLayout());
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        setBackground(ColorScheme.DARK_GRAY_COLOR);

        // Create table model with columns: World, Location, Tier, Actions
        String[] columnNames = {"W", "Location", "T", ""}; // Empty string for actions header
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == columnNames.length - 1; // Only actions column is editable (for buttons)
            }

            @Override
            public Class<?> getColumnClass(int column) {
                return column == columnNames.length - 1 ? JPanel.class : String.class;
            }
        };

        // Create table
        table = new JTable(tableModel);
        table.setFont(FontManager.getRunescapeFont());
        table.setBackground(ColorScheme.DARK_GRAY_COLOR);
        table.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
        table.setGridColor(ColorScheme.DARK_GRAY_COLOR.brighter());
        table.setShowGrid(true);
        // starsTable.setRowHeight(24);
        table.getTableHeader().setFont(FontManager.getRunescapeBoldFont());
        table.getTableHeader().setForeground(ColorScheme.LIGHT_GRAY_COLOR);
        table.getTableHeader().setBackground(ColorScheme.DARK_GRAY_COLOR);

        // Set column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(44);  // World
        table.getColumnModel().getColumn(0).setMinWidth(44);
        table.getColumnModel().getColumn(0).setMaxWidth(44);

        table.getColumnModel().getColumn(1).setPreferredWidth(93); // Location

        table.getColumnModel().getColumn(2).setPreferredWidth(34);  // Tier
        table.getColumnModel().getColumn(2).setMinWidth(34);
        table.getColumnModel().getColumn(2).setMaxWidth(34);
        
        table.getColumnModel().getColumn(3).setPreferredWidth(34); // Info button
        table.getColumnModel().getColumn(3).setMinWidth(34);
        table.getColumnModel().getColumn(3).setMaxWidth(34);
   

        // Custom renderer for text columns with tooltips
        DefaultTableCellRenderer tooltipRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                // Get the star data from the actions column
                int modelRow = table.convertRowIndexToModel(row);
                Object starObj = tableModel.getValueAt(modelRow, 3);
                
                if (starObj instanceof StarData) {
                    StarData star = (StarData) starObj;
                    String formattedTime = star.getFirstFound() != null && !star.getFirstFound().isEmpty() 
                        ? formatIsoDate(star.getFirstFound()) : "Unknown";
                    String foundBy = star.getFoundBy() != null && !star.getFoundBy().isEmpty() 
                        ? star.getFoundBy() : "Unknown";
                    
                    String tooltip = String.format(
                        "<html>World %s - Tier %d<br>%s<br>Found: %s<br>By: %s%s</html>",
                        star.getWorld(),
                        star.getTier(),
                        star.getLocation(),
                        formattedTime,
                        foundBy,
                        star.isBackup() ? "<br><b style='color:rgb(255, 215, 0);'>BACKUP</b>" : ""
                    ); // Html color code for gold rgb(255, 215, 0)
                    setToolTipText(tooltip);
                }
                
                setBackground(ColorScheme.DARK_GRAY_COLOR);
                setForeground(ColorScheme.LIGHT_GRAY_COLOR);
                return c;
            }
        };
        
        // Apply tooltip renderer to text columns
        table.getColumnModel().getColumn(0).setCellRenderer(tooltipRenderer);
        table.getColumnModel().getColumn(1).setCellRenderer(tooltipRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(tooltipRenderer);

        // Custom renderer for actions column
        table.getColumnModel().getColumn(3).setCellRenderer(new ButtonPanelRenderer());
        table.getColumnModel().getColumn(3).setCellEditor(new ButtonPanelEditor());

        // Create custom row sorter
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        sorter.setComparator(3, (a, b) -> {
            StarData starA = (StarData) a;
            StarData starB = (StarData) b;
            return Boolean.compare(starA.isBackup(), starB.isBackup());
        });
        table.setRowSorter(sorter);

        // Set initial sorting by World column (index 0) in ascending order
        sorter.setSortKeys(Collections.singletonList(
            new RowSorter.SortKey(0, SortOrder.ASCENDING)));

        add(table.getTableHeader());
        add(table);
    }

    /**
     * Set callback for when action buttons are clicked
     */
    public void setActionCallback(BiConsumer<String, StarActionType> callback)
    {
        this.actionCallback = callback;
    }

    /**
     * Update the stars table with new data
     */
    public void updateStars(List<StarData> stars)
    {
        tableModel.setRowCount(0);
        
        for (StarData star : stars) {
            Object[] row = {
                star.getWorld(),
                star.getLocation(),
                String.valueOf(star.getTier()), // Just the number, no "T" prefix
                star // Store star data in actions column for button handlers
            };
            tableModel.addRow(row);
        }
    }

    /**
     * Renderer for info button in actions column
     */
    private class ButtonPanelRenderer extends JPanel implements TableCellRenderer
    {
        private final JButton infoButton;

        public ButtonPanelRenderer()
        {
            setLayout(new BorderLayout());
            setBackground(ColorScheme.DARK_GRAY_COLOR);

            infoButton = createIconButton("ℹ️", "Star actions");

            add(infoButton, BorderLayout.CENTER);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column)
        {
            if (value instanceof StarData) {
                StarData star = (StarData) value;
                // Show orange info icon for backup stars, white for regular
                infoButton.setForeground(star.isBackup() ? Color.ORANGE : Color.WHITE);
            }
            return this;
        }
    }

    /**
     * Editor for info button in actions column
     */
    private class ButtonPanelEditor extends AbstractCellEditor implements TableCellEditor
    {
        private final JPanel panel;
        private final JButton infoButton;
        private StarData currentStar;

        public ButtonPanelEditor()
        {
            panel = new JPanel(new BorderLayout());
            panel.setBackground(ColorScheme.DARK_GRAY_COLOR);

            infoButton = createIconButton("ℹ️", "Star actions");

            infoButton.addActionListener(e -> {
                if (currentStar != null) {
                    showActionsMenu(infoButton, currentStar);
                }
                fireEditingStopped();
            });

            panel.add(infoButton, BorderLayout.CENTER);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column)
        {
            if (value instanceof StarData) {
                currentStar = (StarData) value;
                infoButton.setForeground(currentStar.isBackup() ? Color.ORANGE : Color.WHITE);
            }
            return panel;
        }

        @Override
        public Object getCellEditorValue()
        {
            return currentStar;
        }
        
        /**
         * Show popup menu with star actions
         */
        private void showActionsMenu(JButton button, StarData star)
        {
            JPopupMenu menu = new JPopupMenu();
            menu.setBackground(ColorScheme.DARKER_GRAY_COLOR);
            menu.setBorder(BorderFactory.createLineBorder(ColorScheme.MEDIUM_GRAY_COLOR));

            // Star info header
            JMenuItem infoItem = new JMenuItem("World " + star.getWorld() + " - Tier " + star.getTier());
            infoItem.setFont(FontManager.getRunescapeFont().deriveFont(Font.BOLD));
            infoItem.setEnabled(false);
            infoItem.setBackground(ColorScheme.DARKER_GRAY_COLOR);
            infoItem.setForeground(Color.YELLOW);
            menu.add(infoItem);
            
            JMenuItem locationItem = new JMenuItem("📍 " + star.getLocation());
            locationItem.setFont(FontManager.getRunescapeFont());
            locationItem.setEnabled(false);
            locationItem.setBackground(ColorScheme.DARKER_GRAY_COLOR);
            locationItem.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
            menu.add(locationItem);
            
            // First found timestamp
            if (star.getFirstFound() != null && !star.getFirstFound().isEmpty()) {
                String formattedTime = formatIsoDate(star.getFirstFound());
                JMenuItem timeItem = new JMenuItem("🕒 Found: " + formattedTime);
                timeItem.setFont(FontManager.getRunescapeFont());
                timeItem.setEnabled(false);
                timeItem.setBackground(ColorScheme.DARKER_GRAY_COLOR);
                timeItem.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
                menu.add(timeItem);
            }
            
            // Found by user
            if (star.getFoundBy() != null && !star.getFoundBy().isEmpty()) {
                JMenuItem userItem = new JMenuItem("👤 By: " + star.getFoundBy());
                userItem.setFont(FontManager.getRunescapeFont());
                userItem.setEnabled(false);
                userItem.setBackground(ColorScheme.DARKER_GRAY_COLOR);
                userItem.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
                menu.add(userItem);
            }
            
            menu.addSeparator();

            // Toggle backup
            JMenuItem backupItem = new JMenuItem((star.isBackup() ? "⭐ Mark as Regular" : "⭐ Mark as Backup"));
            backupItem.setFont(FontManager.getRunescapeFont());
            backupItem.setBackground(ColorScheme.DARKER_GRAY_COLOR);
            backupItem.setForeground(star.isBackup() ? Color.ORANGE : Color.WHITE);
            backupItem.addActionListener(ev -> {
                if (actionCallback != null) {
                    actionCallback.accept(star.getWorld(), StarActionType.TOGGLE_BACKUP);
                }
            });
            menu.add(backupItem);

            // Edit
            JMenuItem editItem = new JMenuItem("✏️ Edit Star");
            editItem.setFont(FontManager.getRunescapeFont());
            editItem.setBackground(ColorScheme.DARKER_GRAY_COLOR);
            editItem.setForeground(Color.WHITE);
            editItem.addActionListener(ev -> {
                if (actionCallback != null) {
                    actionCallback.accept(star.getWorld(), StarActionType.EDIT);
                }
            });
            menu.add(editItem);

            menu.addSeparator();

            // Remove
            JMenuItem removeItem = new JMenuItem("🗑️ Remove Star");
            removeItem.setFont(FontManager.getRunescapeBoldFont());
            removeItem.setBackground(ColorScheme.DARKER_GRAY_COLOR);
            removeItem.setForeground(Color.RED);
            removeItem.addActionListener(ev -> {
                if (actionCallback != null) {
                    actionCallback.accept(star.getWorld(), StarActionType.REMOVE);
                }
            });
            menu.add(removeItem);

            menu.show(button, 0, button.getHeight());
        }
    }

    /**
     * Create an icon button
     */
    private JButton createIconButton(String icon, String tooltip)
    {
        JButton button = new JButton(icon);
        button.setToolTipText(tooltip);
        button.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 11));
        button.setPreferredSize(new Dimension(26, 20));
        button.setMargin(new Insets(1, 1, 1, 1));
        button.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(true);
        return button;
    }

    /**
     * Format ISO 8601 date string to readable format
     */
    private String formatIsoDate(String isoDate)
    {
        try {
            Instant instant = Instant.parse(isoDate);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm")
                .withZone(ZoneId.systemDefault());
            return formatter.format(instant);
        } catch (Exception e) {
            // If parsing fails, return the original string
            return isoDate;
        }
    }
}
