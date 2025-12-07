package com.shootingstar.scouter.listeners;

import com.shootingstar.scouter.ShootingStarScouterConfig;
import com.shootingstar.scouter.ShootingStarPanel;
import com.shootingstar.scouter.websocket.StarWebSocketHandler;
import com.shootingstar.scouter.websocket.MessageDispatcher;
import com.shootingstar.scouter.websocket.handlers.DashboardUpdateHandler;
import com.shootingstar.scouter.websocket.handlers.StarSyncHandler;
import com.shootingstar.scouter.websocket.handlers.SpawnTimesHandler;

import javax.swing.JButton;
import javax.swing.SwingUtilities;

import org.java_websocket.framing.CloseFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ConnectButtonListener implements ActionListener
{
    private static final Logger log = LoggerFactory.getLogger(ConnectButtonListener.class);
    
    private final JButton connectButton;
    private final ShootingStarScouterConfig config;
    private final ShootingStarPanel panel;
    private StarWebSocketHandler webSocketHandler;
    private MessageDispatcher messageDispatcher;

    public ConnectButtonListener(JButton connectButton, ShootingStarScouterConfig config, ShootingStarPanel panel)
    {
        this.connectButton = connectButton;
        this.config = config;
        this.panel = panel;

        initialize();
    }

    public void initialize()
    {
        try {
            log.info("Initializing ConnectButtonListener...");
            initializeMessageDispatcher();
            log.info("Message dispatcher initialized");
            initializeWebSocketHandler();
            log.info("WebSocket handler initialized");
        } catch (Exception ex) {
            log.error("Error initializing ConnectButtonListener", ex);
        }
    }

    private void initializeMessageDispatcher()
    {
        messageDispatcher = new MessageDispatcher();
        
        // Register handlers for each message type
        messageDispatcher.registerHandler("DASHBOARD_UPDATE", 
            new DashboardUpdateHandler(panel));
        messageDispatcher.registerHandler("STAR_SYNC", 
            new StarSyncHandler(panel.getCurrentStarsView()));
        messageDispatcher.registerHandler("SPAWN_TIMES", 
            new SpawnTimesHandler(panel.getWaveTimersView()));
    }

    private void initializeWebSocketHandler()
    {
        this.webSocketHandler = new StarWebSocketHandler(config.websocketUrl())
        {
            @Override
            protected void onConnectionOpened()
            {
                SwingUtilities.invokeLater(() ->  {
                    connectButton.setText("Disconnect");
                    connectButton.setForeground(Color.ORANGE);
                });
            }

            @Override
            protected void handleMessage(String message)
            {
                messageDispatcher.dispatch(message);
            }

            @Override
            protected void onConnectionClosed(int code, String reason, boolean remote)
            {
                String closeMessage = "WebSocket closed - Code: " + code;
                if (reason != null && !reason.isEmpty()) {
                    closeMessage += ", Reason: " + reason;
                }
                closeMessage += ", Remote: " + remote;
                
                log.error(closeMessage);
                
                SwingUtilities.invokeLater(() -> {
                    connectButton.setText("Connect");
                    connectButton.setForeground(Color.GREEN);

                    if (code != CloseFrame.NORMAL) {
                        connectButton.setText("Connection Error");
                        connectButton.setForeground(Color.RED);
                    }
                });
            }

            @Override
            protected void handleError(Exception ex)
            {
                log.error("WebSocket error", ex);
                
                SwingUtilities.invokeLater(() -> {
                    connectButton.setText("Connection Error");
                    connectButton.setForeground(Color.RED);
                });
            }
        };
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if (webSocketHandler == null) {
            log.error("WebSocket handler not initialized. Attempting to initialize...");
            try {
                initialize();
            } catch (Exception ex) {
                log.error("Failed to initialize WebSocket handler", ex);
                return;
            }
        }
        
        if (connectButton.getText().equals("Connect"))
        {
            webSocketHandler.connect();
        }
        else
        {
            webSocketHandler.disconnect();
            connectButton.setText("Connect");
            connectButton.setForeground(Color.GREEN);
        }
    }
}
