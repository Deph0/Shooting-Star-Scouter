package com.shootingstar.scouter;

import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;

import com.shootingstar.scouter.listeners.ConnectButtonListener;
import com.shootingstar.scouter.views.CurrentStarsCard;
import com.shootingstar.scouter.views.HeaderView;
import com.shootingstar.scouter.views.SecondaryViewPanel;
import com.shootingstar.scouter.views.WaveTimersCard;
import com.shootingstar.scouter.websocket.WebSocketManager;

import lombok.Getter;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;

public class ShootingStarPanel extends PluginPanel
{
    @Getter private final HeaderView headerView;
    private final SecondaryViewPanel secondaryViewPanel;
    private final JButton connectButton;

    public ShootingStarPanel(ShootingStarScouterConfig config)
    {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Title bar with connect button on left and title on right
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        
        connectButton = new JButton("Connect");
        connectButton.setForeground(Color.GREEN);
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

    /**
     * Initialize WebSocket manager and connect button listener.
     * Must be called after panel construction.
     */
    public void setWebSocketManager(WebSocketManager webSocketManager)
    {
        ConnectButtonListener listener = new ConnectButtonListener(connectButton, webSocketManager);
        connectButton.addActionListener(listener);
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
