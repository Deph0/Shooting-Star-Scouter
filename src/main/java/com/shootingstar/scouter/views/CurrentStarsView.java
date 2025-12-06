package com.shootingstar.scouter.views;

import net.runelite.client.ui.FontManager;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.BoxLayout;

public class CurrentStarsView extends JPanel
{
    private final JLabel messageLabel;

    public CurrentStarsView()
    {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        messageLabel = new JLabel("No current stars.");
        messageLabel.setFont(FontManager.getRunescapeFont());
        add(messageLabel);
    }

    public void setMessage(String msg)
    {
        messageLabel.setText(msg);
    }
}
