package com.shootingstar.scouter.listeners;

import com.shootingstar.scouter.websocket.WebSocketManager;
import com.shootingstar.scouter.websocket.WebSocketManager.ConnectionState;

import javax.swing.JButton;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Handles connect/disconnect button actions.
 * Delegates to WebSocketManager for actual connection management.
 */
public class ConnectButtonListener implements ActionListener
{
    private static final Logger log = LoggerFactory.getLogger(ConnectButtonListener.class);
    private final JButton connectButton;
    private final WebSocketManager webSocketManager;

    public ConnectButtonListener(JButton connectButton, WebSocketManager webSocketManager)
    {
        this.connectButton = connectButton;
        this.webSocketManager = webSocketManager;

        // Register callback to update button state
        webSocketManager.addStateChangeCallback(this::onConnectionStateChanged);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if (connectButton.getText().equals("Connect"))
        {
            webSocketManager.connect();
        }
        else
        {
            webSocketManager.disconnect();
        }
    }

    private void onConnectionStateChanged(ConnectionState state)
    {
        log.debug("Connection state changed to: {}", state.name());
        SwingUtilities.invokeLater(() -> {
            switch (state)
            {
                case CONNECTING:
                    connectButton.setText("Connecting...");
                    connectButton.setForeground(Color.YELLOW);
                    connectButton.setEnabled(false);
                    break;

                case CONNECTED:
                    connectButton.setText("Disconnect");
                    connectButton.setForeground(Color.ORANGE);
                    connectButton.setEnabled(true);
                    break;

                case DISCONNECTED:
                    connectButton.setText("Connect");
                    connectButton.setForeground(Color.GREEN);
                    connectButton.setEnabled(true);
                    break;

                case ERROR:
                    connectButton.setText("Connection Error");
                    connectButton.setForeground(Color.RED);
                    connectButton.setEnabled(true);
                    break;
            }
        });
    }
}
