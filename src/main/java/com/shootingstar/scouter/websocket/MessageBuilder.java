package com.shootingstar.scouter.websocket;

import com.google.gson.JsonObject;
import com.shootingstar.scouter.models.StarData;

/**
 * Builds JSON messages for WebSocket communication.
 * Centralizes message format and structure.
 *
 * The message structure assumes a common format used by the starhunt-ws-server:
 * {@url https://github.com/luisr96/starhunt-ws-server/}
 */
public class MessageBuilder
{
    /**
     * Build a star update message from a StarData object
     * @param starData The StarData object
     * @return The JSON string message
     */
    public static String buildStarUpdate(StarData starData)
    {
       JsonObject message = new JsonObject();
        message.addProperty("type", WebSocketManager.MSG_TYPE_STAR_UPDATE);
        JsonObject data = starData.toJsonObject();
        message.add("data", data);
        return message.toString();
    }

    /**
     * Build a star remove message
     * @param world The world number of the star to remove
     * @return The JSON string message
     */
    public static String buildStarRemove(String world)
    {
        JsonObject message = new JsonObject();
        message.addProperty("type", WebSocketManager.MSG_TYPE_STAR_REMOVE);
        
        JsonObject data = new JsonObject();
        data.addProperty("world", world);
        
        message.add("data", data);
        return message.toString();
    }

}
