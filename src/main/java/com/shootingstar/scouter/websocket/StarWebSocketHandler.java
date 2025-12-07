package com.shootingstar.scouter.websocket;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.framing.CloseFrame;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

public class StarWebSocketHandler
{
    private static final Logger log = LoggerFactory.getLogger(StarWebSocketHandler.class);
    private WebSocketClient webSocketClient;
    private final String serverUrl;

    public StarWebSocketHandler(String serverUrl)
    {
        this.serverUrl = serverUrl;
    }

    public void connect()
    {
        try
        {
            URI serverUri = new URI(serverUrl);
            
            webSocketClient = new WebSocketClient(serverUri)
            {
                @Override
                public void onOpen(ServerHandshake handshakedata)
                {
                    log.info("WebSocket connected");
                    onConnectionOpened();
                }

                @Override
                public void onMessage(String message)
                {
                    if (log.isDebugEnabled())
                    {
                        log.debug("webSocket messageSize={}, message={}", message.length(), message);
                    }
                    handleMessage(message);
                }

                @Override
                public void onClose(int code, String reason, boolean remote)
                {
                    log.info("WebSocket closed: {}", reason);
                    onConnectionClosed(code, reason, remote);
                }

                @Override
                public void onError(Exception ex)
                {
                    log.error("WebSocket error", ex);
                    handleError(ex);
                }
            };
            
            webSocketClient.connect();
        }
        catch (Exception ex)
        {
            log.error("Failed to connect", ex);
            handleError(ex);
        }
    }

    public void disconnect()
    {
        if (webSocketClient != null && webSocketClient.isOpen())
        {
            webSocketClient.close(CloseFrame.NORMAL, "Client disconnecting");
            webSocketClient = null;
        }
    }

    public boolean isConnected()
    {
        return webSocketClient != null && webSocketClient.isOpen();
    }

    protected void onConnectionOpened()
    {
        // Override in subclass or set callback
    }

    protected void handleMessage(String message)
    {
        // Override in subclass or set callback
    }

    protected void onConnectionClosed(int code, String reason, boolean remote)
    {
        // Override in subclass or set callback
    }

    protected void handleError(Exception ex)
    {
        // Override in subclass or set callback
    }
}
