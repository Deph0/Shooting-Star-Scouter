package com.shootingstar.scouter.views;

import net.runelite.client.ui.FontManager;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.BoxLayout;

public class WaveTimersView extends JPanel
{
    private final JLabel messageLabel;

    public WaveTimersView()
    {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        messageLabel = new JLabel("No wave world timers.");
        messageLabel.setFont(FontManager.getRunescapeFont());
        add(messageLabel);
    }

    public void setMessage(String msg)
    {
        messageLabel.setText(msg);
    }
}
