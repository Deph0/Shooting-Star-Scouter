package com.shootingstar.scouter;

import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;

import com.shootingstar.scouter.listeners.ConnectButtonListener;
import com.shootingstar.scouter.views.CurrentStarsCard;
import com.shootingstar.scouter.views.HeaderView;
import com.shootingstar.scouter.views.SecondaryViewPanel;
import com.shootingstar.scouter.views.WaveTimersCard;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;

public class ShootingStarPanel extends PluginPanel
{
    private final HeaderView headerView;
    private final SecondaryViewPanel secondaryViewPanel;

    public ShootingStarPanel(ShootingStarScouterConfig config)
    {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Title bar with connect button on left and title on right
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        
        JButton connectButton = new JButton("Connect");
        connectButton.setForeground(Color.GREEN);
        ConnectButtonListener listener = new ConnectButtonListener(connectButton, config, this);
        listener.initialize();
        connectButton.addActionListener(listener);
        titleBar.add(connectButton, BorderLayout.EAST);
        
        JLabel title = new JLabel("<html>Shooting Star<br>Scouter</html>");
        title.setHorizontalAlignment(JLabel.LEFT);
        
        title.setForeground(Color.WHITE);
        title.setFont(FontManager.getRunescapeBoldFont());
        titleBar.add(title, BorderLayout.WEST);

        headerView = new HeaderView();

        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.add(titleBar, BorderLayout.NORTH);
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

    public HeaderView getHeaderView()
    {
        return headerView;
    }

    public CurrentStarsCard getCurrentStarsView()
    {
        return secondaryViewPanel.getStarsCard();
    }

    public WaveTimersCard getWaveTimersView()
    {
        return secondaryViewPanel.getTimersCard();
    }
}
