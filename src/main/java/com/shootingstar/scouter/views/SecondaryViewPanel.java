package com.shootingstar.scouter.views;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;
import lombok.Getter;

import java.awt.BorderLayout;
import java.awt.CardLayout;

public class SecondaryViewPanel extends PluginPanel
{
    private final CardLayout cardLayout;
    private final JPanel cardPanel;
    @Getter private final CurrentStarsCard starsCard;
    @Getter private final WaveTimersCard timersCard;
    private final JButton toggleButton;
    private boolean showingStars = true;

    public SecondaryViewPanel()
    {
        super(false);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        setBackground(ColorScheme.DARK_GRAY_COLOR);

        toggleButton = new JButton("Show Spawn Timers");
        toggleButton.setFocusPainted(false);

        toggleButton.addActionListener(e -> toggleViewBtn());
        add(toggleButton, BorderLayout.NORTH);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(ColorScheme.DARK_GRAY_COLOR);

        starsCard = new CurrentStarsCard();
        timersCard = new WaveTimersCard();

        cardPanel.add(starsCard, "STARS");
        cardPanel.add(timersCard, "TIMERS");
        cardLayout.show(cardPanel, "STARS");

        add(cardPanel, BorderLayout.CENTER);
    }

    private void toggleViewBtn()
    {
        showingStars = !showingStars;
        if (showingStars)
        {
            cardLayout.show(cardPanel, "STARS");
            toggleButton.setText("Show Spawn Timers");
        }
        else
        {
            cardLayout.show(cardPanel, "TIMERS");
            toggleButton.setText("Show Current Stars");
        }
    }
}
