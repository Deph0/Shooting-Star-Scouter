package com.shootingstar.scouter.views;

import net.runelite.client.ui.FontManager;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;

public class CurrentStarsCard extends JPanel
{
    private final JLabel messageLabel;

    public CurrentStarsCard()
    {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        messageLabel = new JLabel("No current stars.");
        messageLabel.setFont(FontManager.getRunescapeFont());
        add(messageLabel);
    }

    public void setMessage(String msg)
    {
        messageLabel.setText(msg);
    }
}
