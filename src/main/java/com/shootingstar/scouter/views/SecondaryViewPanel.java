package com.shootingstar.scouter.views;

import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.CardLayout;

public class SecondaryViewPanel extends JPanel
{
    private final CardLayout cardLayout;
    private final JPanel cardPanel;
    private final CurrentStarsView starsView;
    private final WaveTimersView timersView;
    private final JButton toggleButton;
    private boolean showingStars = true;

    public SecondaryViewPanel()
    {
        setLayout(new BorderLayout());

        toggleButton = new JButton("Show: Current Stars");

        toggleButton.addActionListener(e -> toggleView());
        add(toggleButton, BorderLayout.NORTH);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        starsView = new CurrentStarsView();
        timersView = new WaveTimersView();

        cardPanel.add(starsView, "STARS");
        cardPanel.add(timersView, "TIMERS");
        cardLayout.show(cardPanel, "STARS");

        add(cardPanel, BorderLayout.CENTER);
    }

    private void toggleView()
    {
        showingStars = !showingStars;
        if (showingStars)
        {
            cardLayout.show(cardPanel, "STARS");
            toggleButton.setText("Show: Current Stars");
        }
        else
        {
            cardLayout.show(cardPanel, "TIMERS");
            toggleButton.setText("Show: Wave Timers");
        }
    }

    public CurrentStarsView getStarsView()
    {
        return starsView;
    }

    public WaveTimersView getTimersView()
    {
        return timersView;
    }
}
