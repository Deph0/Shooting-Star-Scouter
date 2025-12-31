package com.shootingstar.scouter.websocket;

import com.google.gson.JsonObject;
import com.shootingstar.scouter.models.StarData;

/**
 * Builds JSON messages for WebSocket communication.
 * Centralizes message format and structure.
 */
public class MessageBuilder
{
    /**
     * Build a star update message
     * @param world The world number where the star was found
     * @param tier The tier of the star (1-9)
     * @param location The location name where the star was found
     * @param backup Whether this is a backup star
     * @param foundBy The username of the player who found the star
     * @param firstFound ISO-8601 timestamp when the star was first found
     * @return The JSON string message
     */
    public static String buildStarUpdate(String world, int tier, String location, boolean backup, String foundBy, String firstFound)
    {
        JsonObject message = new JsonObject();
        message.addProperty("type", WebSocketManager.MSG_TYPE_STAR_UPDATE);
        
        JsonObject data = new JsonObject();
        data.addProperty("world", world);
        data.addProperty("tier", tier);
        data.addProperty("location", location);
        data.addProperty("backup", backup);
        data.addProperty("foundBy", foundBy);
        data.addProperty("firstFound", firstFound);
        
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

    /**
     * Build a star update message from a StarData object
     * @param starData The StarData object
     * @return The JSON string message
     */
    public static String buildStarUpdate(StarData starData)
    {
        return buildStarUpdate(
            starData.getWorld(), starData.getTier(), starData.getLocation(),
            starData.isBackup(), starData.getFoundBy(), starData.getFirstFound()
        );
    }
}
