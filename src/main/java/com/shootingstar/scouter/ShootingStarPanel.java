package com.shootingstar.scouter;

import com.google.inject.Injector;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;

import com.shootingstar.scouter.listeners.ConnectButtonListener;
import com.shootingstar.scouter.listeners.StarMenuActionCallback;
import com.shootingstar.scouter.models.StarData;
import com.shootingstar.scouter.views.CurrentStarsCard;
import com.shootingstar.scouter.services.StarDataService;
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
import java.util.ArrayList;
import java.util.List;



public class ShootingStarPanel extends PluginPanel
{
    @Getter private final HeaderView headerView;
    private final SecondaryViewPanel secondaryViewPanel;
    private final JButton connectButton;
    private ShootingStarScouterConfig config;
    private StarDataService starDataService;
    @Getter private final Injector injector;
	@Getter private WebSocketManager webSocketManager;

    public ShootingStarPanel(Injector injector)
    {
        super(true);
        this.injector = injector;
        this.starDataService = injector.getInstance(StarDataService.class);
        this.config = injector.getInstance(ShootingStarScouterConfig.class);
        
        // Title bar with connect button on left and title on right
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        titleBar.setBackground(ColorScheme.DARK_GRAY_COLOR);
        
        connectButton = new JButton("Connect");
        connectButton.setFocusPainted(false);
        connectButton.setForeground(Color.GREEN);
        titleBar.add(connectButton, BorderLayout.EAST);
        
        JLabel title = new JLabel("<html>Shooting Star<br>Scouter</html>");
        title.setHorizontalAlignment(JLabel.LEFT);
        
        title.setForeground(Color.WHITE);
        title.setFont(FontManager.getRunescapeBoldFont());
        titleBar.add(title, BorderLayout.WEST);

        headerView = new HeaderView();
        
        // Add components directly to the PluginPanel (uses DynamicGridLayout)
        add(titleBar);
        add(headerView);

        // Secondary view (encapsulates toggle + cards)
        secondaryViewPanel = new SecondaryViewPanel();
        add(secondaryViewPanel);
    }

    /**
     * Initialize WebSocket manager and button listener.
     * Must be called after panel construction.
     */
    public void setWebSocketManager(WebSocketManager webSocketManager)
    {
        this.webSocketManager = webSocketManager;
        ConnectButtonListener listener = new ConnectButtonListener(this, connectButton);
        connectButton.addActionListener(listener);
        
        // Set up action callback for current stars buttons
        StarMenuActionCallback starMenuListener = new StarMenuActionCallback(this);
        getCurrentStarsView().setActionCallback(starMenuListener::handleStarAction);
    }

    public CurrentStarsCard getCurrentStarsView()
    {
        return secondaryViewPanel.getStarsCard();
    }

    public WaveTimersCard getWaveTimersView()
    {
        return secondaryViewPanel.getTimersCard();
    }

    public void refreshStars()
    {
        List<StarData> stars = new ArrayList<>(starDataService.getAllStars().values());
        getCurrentStarsView().updateStars(stars);
    }
}
