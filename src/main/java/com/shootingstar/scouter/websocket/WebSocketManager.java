package com.shootingstar.scouter.websocket;

import com.shootingstar.scouter.ShootingStarPanel;
import com.shootingstar.scouter.services.StarDataService;
import com.shootingstar.scouter.websocket.handlers.DashboardUpdateHandler;
import com.shootingstar.scouter.websocket.handlers.StarSyncHandler;

import lombok.extern.slf4j.Slf4j;

import com.shootingstar.scouter.websocket.handlers.SpawnTimesHandler;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.framing.CloseFrame;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Manages WebSocket connection state and message dispatching.
 * Central coordinator between UI and WebSocket communication.
 */
@Slf4j
public class WebSocketManager
{    
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
        messageDispatcher.registerHandler(MSG_TYPE_SPAWN_TIMES, new SpawnTimesHandler(panel.getWaveTimersView()));
        messageDispatcher.registerHandler(MSG_TYPE_STAR_SYNC, new StarSyncHandler(panel));
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
     * Initiate WebSocket connection asynchronously
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
                    
                    if (code == CloseFrame.NORMAL) {
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

    /**
     * Send a message to the WebSocket server
     * @param message The JSON message string to send
     * @return true if sent successfully, false otherwise
     */
    public boolean sendMessage(String message)
    {
        if (!isConnected())
        {
            log.warn("Cannot send message - WebSocket is not connected");
            return false;
        }

        try
        {
            webSocketClient.send(message);
            log.debug("Sent message: {}", message);
            return true;
        }
        catch (Exception ex)
        {
            log.error("Failed to send message - closing connection", ex);
            // Force close the connection since it's likely broken
            if (webSocketClient != null)
            {
                try
                {
                    webSocketClient.close(CloseFrame.ABNORMAL_CLOSE, "Closing broken connection");
                }
                catch (Exception closeEx)
                {
                    log.error("Error closing broken connection", closeEx);
                }
                webSocketClient = null;
            }
            notifyStateChange(ConnectionState.ERROR);
            return false;
        }
    }


    private void notifyStateChange(ConnectionState state)
    {
        for (Consumer<ConnectionState> callback : stateChangeCallbacks) {
            callback.accept(state);
        }
    }
}
