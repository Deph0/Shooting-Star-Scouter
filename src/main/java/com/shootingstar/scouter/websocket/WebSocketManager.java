package com.shootingstar.scouter.websocket;

import com.shootingstar.scouter.ShootingStarPanel;
import com.shootingstar.scouter.websocket.handlers.DashboardUpdateHandler;
import com.shootingstar.scouter.websocket.handlers.StarSyncHandler;
import com.shootingstar.scouter.websocket.handlers.SpawnTimesHandler;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.framing.CloseFrame;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Manages WebSocket connection state and message dispatching.
 * Central coordinator between UI and WebSocket communication.
 */
public class WebSocketManager
{
    private static final Logger log = LoggerFactory.getLogger(WebSocketManager.class);
    
    private final String serverUrl;
    private WebSocketClient webSocketClient;
    private final MessageDispatcher messageDispatcher;
    private final List<Consumer<ConnectionState>> stateChangeCallbacks = new ArrayList<>();

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
        this.serverUrl = serverUrl;
        
        // Initialize message dispatcher with handlers
        this.messageDispatcher = new MessageDispatcher();
        messageDispatcher.registerHandler(MSG_TYPE_DASHBOARD_UPDATE, new DashboardUpdateHandler(panel));
        messageDispatcher.registerHandler(MSG_TYPE_STAR_SYNC, new StarSyncHandler(panel.getCurrentStarsView()));
        messageDispatcher.registerHandler(MSG_TYPE_SPAWN_TIMES, new SpawnTimesHandler(panel.getWaveTimersView()));
        // messageDispatcher.registerHandler(MSG_TYPE_STAR_UPDATE, null);
        // messageDispatcher.registerHandler(MSG_TYPE_STAR_REMOVE, null);
    }

    /**
     * Add callback to be notified of connection state changes
     */
    public void addStateChangeCallback(Consumer<ConnectionState> callback)
    {
        stateChangeCallbacks.add(callback);
    }

    /**
     * Remove a previously registered state change callback
     */
    public void removeStateChangeCallback(Consumer<ConnectionState> callback)
    {
        stateChangeCallbacks.remove(callback);
    }

    /**
     * Initiate WebSocket connection
     */
    public void connect()
    {
        try
        {
            log.info("Connecting to WebSocket server at {}", serverUrl);
            notifyStateChange(ConnectionState.CONNECTING);
            
            URI serverUri = new URI(serverUrl);
            
            webSocketClient = new WebSocketClient(serverUri)
            {
                @Override
                public void onOpen(ServerHandshake handshakedata)
                {
                    log.debug("WebSocket connection established");
                    notifyStateChange(ConnectionState.CONNECTED);
                }

                @Override
                public void onMessage(String message)
                {
                    log.debug("Received WebSocket message: {}", message);
                    messageDispatcher.dispatch(message);
                }

                @Override
                public void onClose(int code, String reason, boolean remote)
                {
                    log.info("WebSocket closed - Code: {}, Reason: {}, Remote: {}", code, reason, remote);
                    
                    if (code == 1000) { // Normal closure
                        notifyStateChange(ConnectionState.DISCONNECTED);
                    } else {
                        notifyStateChange(ConnectionState.ERROR);
                    }
                }

                @Override
                public void onError(Exception ex)
                {
                    log.error("WebSocket error occurred", ex);
                    notifyStateChange(ConnectionState.ERROR);
                }
            };
            
            webSocketClient.connect();
        }
        catch (Exception ex)
        {
            log.error("Failed to connect to WebSocket", ex);
            notifyStateChange(ConnectionState.ERROR);
        }
    }

    /**
     * Close WebSocket connection
     */
    public void disconnect()
    {
        if (webSocketClient != null && webSocketClient.isOpen())
        {
            log.info("Closing WebSocket connection...");
            webSocketClient.close(CloseFrame.NORMAL, "Client disconnecting");
            webSocketClient = null;
            notifyStateChange(ConnectionState.DISCONNECTED);
        }
    }

    /**
     * Check if currently connected
     */
    public boolean isConnected()
    {
        return webSocketClient != null && webSocketClient.isOpen();
    }

    private void notifyStateChange(ConnectionState state)
    {
        for (Consumer<ConnectionState> callback : stateChangeCallbacks) {
            callback.accept(state);
        }
    }
}
