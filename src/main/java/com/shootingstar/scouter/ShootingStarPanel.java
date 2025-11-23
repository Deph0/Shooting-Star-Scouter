package com.shootingstar.scouter;

import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.GridLayout;

public class ShootingStarPanel extends PluginPanel
{
    private final JLabel currentWaveTimeValue;
    private final JLabel timeToScoutValue;
    private final JLabel waveEndTimeValue;

    private final CardLayout secondaryCardLayout;
    private final JPanel secondaryCardPanel;
    private boolean showingStars = true;

    public ShootingStarPanel()
    {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Title
        JLabel title = new JLabel("Shooting Star Scouter");
        title.setFont(FontManager.getRunescapeBoldFont());

        // Primary header box - three text elements
        JPanel headerPanel = new JPanel(new GridLayout(1, 3, 8, 0));

        currentWaveTimeValue = new JLabel("Current Wave: N/A");
        currentWaveTimeValue.setFont(FontManager.getRunescapeFont());
        currentWaveTimeValue.setAlignmentX(Component.LEFT_ALIGNMENT);

        timeToScoutValue = new JLabel("Time To Scout: N/A");
        timeToScoutValue.setFont(FontManager.getRunescapeFont());
        timeToScoutValue.setAlignmentX(Component.LEFT_ALIGNMENT);

        waveEndTimeValue = new JLabel("Wave End: N/A");
        waveEndTimeValue.setFont(FontManager.getRunescapeFont());
        waveEndTimeValue.setAlignmentX(Component.LEFT_ALIGNMENT);

        headerPanel.add(wrapLabelPanel(currentWaveTimeValue));
        headerPanel.add(wrapLabelPanel(timeToScoutValue));
        headerPanel.add(wrapLabelPanel(waveEndTimeValue));

        // Top container holds title and header
        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.add(title, BorderLayout.NORTH);
        topContainer.add(headerPanel, BorderLayout.SOUTH);

        add(topContainer, BorderLayout.NORTH);

        // Secondary view: togglable between "Current Stars" and "Wave World Timers"
        JPanel secondaryContainer = new JPanel(new BorderLayout());

        JButton toggleView = new JButton("Show: Current Stars");
        toggleView.addActionListener(e -> toggleSecondaryView(toggleView));
        secondaryContainer.add(toggleView, BorderLayout.NORTH);

        secondaryCardLayout = new CardLayout();
        secondaryCardPanel = new JPanel(secondaryCardLayout);

        // Current Stars view (placeholder)
        JPanel currentStarsPanel = new JPanel();
        currentStarsPanel.setLayout(new BoxLayout(currentStarsPanel, BoxLayout.Y_AXIS));
        JLabel noStars = new JLabel("No current stars.");
        noStars.setFont(FontManager.getRunescapeFont());
        currentStarsPanel.add(noStars);

        // Wave World Timers view (placeholder)
        JPanel waveTimersPanel = new JPanel();
        waveTimersPanel.setLayout(new BoxLayout(waveTimersPanel, BoxLayout.Y_AXIS));
        JLabel noTimers = new JLabel("No wave world timers.");
        noTimers.setFont(FontManager.getRunescapeFont());
        waveTimersPanel.add(noTimers);

        secondaryCardPanel.add(currentStarsPanel, "STARS");
        secondaryCardPanel.add(waveTimersPanel, "TIMERS");
        secondaryCardLayout.show(secondaryCardPanel, "STARS");

        secondaryContainer.add(secondaryCardPanel, BorderLayout.CENTER);
        add(secondaryContainer, BorderLayout.CENTER);
    }

    private JPanel wrapLabelPanel(JLabel label)
    {
        JPanel p = new JPanel(new BorderLayout());
        p.add(label, BorderLayout.WEST);
        return p;
    }

    private void toggleSecondaryView(JButton toggleButton)
    {
        showingStars = !showingStars;
        if (showingStars)
        {
            secondaryCardLayout.show(secondaryCardPanel, "STARS");
            toggleButton.setText("Show: Current Stars");
        }
        else
        {
            secondaryCardLayout.show(secondaryCardPanel, "TIMERS");
            toggleButton.setText("Show: Wave Timers");
        }
    }

    // Public setters to update header values from plugin logic
    public void setCurrentWaveTime(String text)
    {
        currentWaveTimeValue.setText(text);
    }

    public void setTimeToScout(String text)
    {
        timeToScoutValue.setText(text);
    }

    public void setWaveEndTime(String text)
    {
        waveEndTimeValue.setText(text);
    }
}
