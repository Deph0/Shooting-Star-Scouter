package com.shootingstar.scouter.views;

import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import lombok.Getter;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;

public class HeaderView extends JPanel
{
    @Getter private final JLabel waveBeganLabel;
    @Getter private final JLabel waveEndsLabel;
    @Getter private final JLabel timeToScoutLabel;
    @Getter private final JLabel spawnPhaseEndsLabel;

    public HeaderView()
    {
        // Stack labels vertically: 4 rows, 1 column, small horizontal and vertical gap
        setLayout(new GridLayout(4, 1, 1, 4));

        // Add a border box with 5px inner offset
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ColorScheme.DARK_GRAY_COLOR.brighter(), 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        
        // Apply dark background color from ColorScheme
        setBackground(ColorScheme.DARK_GRAY_COLOR);
        setOpaque(true);
        
        waveBeganLabel = new JLabel("Wave began: N/A");
        waveBeganLabel.setFont(FontManager.getRunescapeSmallFont());
        waveBeganLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        waveBeganLabel.setAlignmentY(Component.BOTTOM_ALIGNMENT);

        waveEndsLabel = new JLabel("Wave ends in: N/A");
        waveEndsLabel.setFont(FontManager.getRunescapeSmallFont());
        waveEndsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        waveEndsLabel.setAlignmentY(Component.BOTTOM_ALIGNMENT);

        timeToScoutLabel = new JLabel("Start Scouting in: N/A");
        timeToScoutLabel.setFont(FontManager.getRunescapeSmallFont());
        timeToScoutLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        timeToScoutLabel.setAlignmentY(Component.BOTTOM_ALIGNMENT);

        spawnPhaseEndsLabel = new JLabel("Spawn phase ends: N/A");
        spawnPhaseEndsLabel.setFont(FontManager.getRunescapeSmallFont());
        spawnPhaseEndsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        spawnPhaseEndsLabel.setAlignmentY(Component.BOTTOM_ALIGNMENT);

        add(wrapLabelPanel(waveBeganLabel));
        add(wrapLabelPanel(waveEndsLabel));
        add(wrapLabelPanel(timeToScoutLabel));
        add(wrapLabelPanel(spawnPhaseEndsLabel));
    }

    private JPanel wrapLabelPanel(JLabel label)
    {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.add(label, BorderLayout.WEST);
        return p;
    }

}
