package com.shootingstar.scouter;

import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;

import com.shootingstar.scouter.views.CurrentStarsView;
import com.shootingstar.scouter.views.HeaderView;
import com.shootingstar.scouter.views.SecondaryViewPanel;
import com.shootingstar.scouter.views.WaveTimersView;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;

public class ShootingStarPanel extends PluginPanel
{
    private final HeaderView headerView;
    private final SecondaryViewPanel secondaryViewPanel;

    public ShootingStarPanel()
    {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Title
        JLabel title = new JLabel("Shooting Star Scouter");
        title.setForeground(Color.WHITE);
        title.setFont(FontManager.getRunescapeBoldFont());

        headerView = new HeaderView();

        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.add(title, BorderLayout.NORTH);
        topContainer.add(headerView, BorderLayout.SOUTH);

        add(topContainer, BorderLayout.NORTH);

        // Secondary view (encapsulates toggle + cards)
        secondaryViewPanel = new SecondaryViewPanel();
        add(secondaryViewPanel, BorderLayout.CENTER);
    }

    // Delegate setters
    public void setCurrentWaveTime(String text)
    {
        headerView.setCurrentWaveText(text);
    }

    public void setTimeToScout(String text)
    {
        headerView.setTimeToScoutText(text);
    }

    public void setWaveEndTime(String text)
    {
        headerView.setWaveEndText(text);
    }

    public CurrentStarsView getCurrentStarsView()
    {
        return secondaryViewPanel.getStarsView();
    }

    public WaveTimersView getWaveTimersView()
    {
        return secondaryViewPanel.getTimersView();
    }
}
