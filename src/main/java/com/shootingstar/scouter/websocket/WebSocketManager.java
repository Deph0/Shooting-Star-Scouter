package com.shootingstar.scouter.websocket;

import com.shootingstar.scouter.ShootingStarPanel;
import com.shootingstar.scouter.websocket.handlers.DashboardUpdateHandler;
import com.shootingstar.scouter.websocket.handlers.StarSyncHandler;
import com.shootingstar.scouter.websocket.handlers.SpawnTimesHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

/**
 * Manages WebSocket connection state and message dispatching.
 * Central coordinator between UI and WebSocket communication.
 */
public class WebSocketManager
{
    private static final Logger log = LoggerFactory.getLogger(WebSocketManager.class);
    
    private final StarWebSocketHandler webSocketHandler;
    private final MessageDispatcher messageDispatcher;
    private Consumer<ConnectionState> stateChangeCallback;

    public static final String MSG_TYPE_STAR_UPDATE = "STAR_UPDATE";
    public static final String MSG_TYPE_STAR_SYNC = "STAR_SYNC";
    public static final String MSG_TYPE_STAR_REMOVE = "STAR_REMOVE";
    public static final String MSG_TYPE_SPAWN_TIMES = "SPAWN_TIMES";
    public static final String MSG_TYPE_DASHBOARD_UPDATE = "DASHBOARD_UPDATE";

    public enum ConnectionState
    {
        DISCONNECTED,
        CONNECTING,
        CONNECTED,
        ERROR
    }

    public WebSocketManager(String serverUrl, ShootingStarPanel panel)
    {
        // Create WebSocket handler with callbacks
        this.webSocketHandler = new StarWebSocketHandler(serverUrl)
        {
            @Override
            protected void onConnectionOpened()
            {
                log.info("WebSocket connection established");
                notifyStateChange(ConnectionState.CONNECTED);
            }

            @Override
            protected void handleMessage(String message)
            {
                messageDispatcher.dispatch(message);
            }

            @Override
            protected void onConnectionClosed(int code, String reason, boolean remote)
            {
                log.info("WebSocket closed - Code: {}, Reason: {}, Remote: {}", code, reason, remote);
                
                if (code == 1000) { // Normal closure
                    notifyStateChange(ConnectionState.DISCONNECTED);
                } else {
                    notifyStateChange(ConnectionState.ERROR);
                }
            }

            @Override
            protected void handleError(Exception ex)
            {
                log.error("WebSocket error occurred", ex);
                notifyStateChange(ConnectionState.ERROR);
            }
        };

        // Initialize message dispatcher with handlers
        this.messageDispatcher = new MessageDispatcher();
        messageDispatcher.registerHandler(MSG_TYPE_DASHBOARD_UPDATE, new DashboardUpdateHandler(panel));
        messageDispatcher.registerHandler(MSG_TYPE_STAR_SYNC, new StarSyncHandler(panel.getCurrentStarsView()));
        messageDispatcher.registerHandler(MSG_TYPE_SPAWN_TIMES, new SpawnTimesHandler(panel.getWaveTimersView()));
        // messageDispatcher.registerHandler(MSG_TYPE_STAR_UPDATE, null);
        // messageDispatcher.registerHandler(MSG_TYPE_STAR_REMOVE, null);
    }

    /**
     * Set callback to be notified of connection state changes
     */
    public void setStateChangeCallback(Consumer<ConnectionState> callback)
    {
        this.stateChangeCallback = callback;
    }

    /**
     * Initiate WebSocket connection
     */
    public void connect()
    {
        log.info("Initiating WebSocket connection...");
        notifyStateChange(ConnectionState.CONNECTING);
        webSocketHandler.connect();
    }

    /**
     * Close WebSocket connection
     */
    public void disconnect()
    {
        log.info("Closing WebSocket connection...");
        webSocketHandler.disconnect();
        notifyStateChange(ConnectionState.DISCONNECTED);
    }

    /**
     * Check if currently connected
     */
    public boolean isConnected()
    {
        return webSocketHandler.isConnected();
    }

    private void notifyStateChange(ConnectionState state)
    {
        if (stateChangeCallback != null) {
            stateChangeCallback.accept(state);
        }
    }
}
