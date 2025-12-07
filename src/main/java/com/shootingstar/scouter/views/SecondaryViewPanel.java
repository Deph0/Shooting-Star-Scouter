package com.shootingstar.scouter.views;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.CardLayout;

public class SecondaryViewPanel extends JPanel
{
    private final CardLayout cardLayout;
    private final JPanel cardPanel;
    private final CurrentStarsCard starsCard;
    private final WaveTimersCard timersCard;
    private final JButton toggleButton;
    private boolean showingStars = true;

    public SecondaryViewPanel()
    {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        toggleButton = new JButton("Show Spawn Timers");

        toggleButton.addActionListener(e -> toggleView());
        add(toggleButton, BorderLayout.NORTH);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        starsCard = new CurrentStarsCard();
        timersCard = new WaveTimersCard();

        cardPanel.add(starsCard, "STARS");
        cardPanel.add(timersCard, "TIMERS");
        cardLayout.show(cardPanel, "STARS");

        add(cardPanel, BorderLayout.CENTER);
    }

    private void toggleView()
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

    public CurrentStarsCard getStarsCard()
    {
        return starsCard;
    }

    public WaveTimersCard getTimersCard()
    {
        return timersCard;
    }
}
