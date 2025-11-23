package com.shootingstar.scouter;

import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;

public class ShootingStarPanel extends PluginPanel
{
    public ShootingStarPanel()
    {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("Shooting Star Scouter");
        title.setFont(FontManager.getRunescapeBoldFont());
        add(title, BorderLayout.NORTH);

        // Placeholder content - extend as needed
        JLabel content = new JLabel("No stars tracked yet.");
        content.setFont(FontManager.getRunescapeFont());
        add(content, BorderLayout.CENTER);
    }
}
