package com.shootingstar.scouter.websocket.handlers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.shootingstar.scouter.views.CurrentStarsCard;

import javax.swing.SwingUtilities;

public class StarSyncHandler implements IMessageHandler
{
    private final CurrentStarsCard starsView;

    public StarSyncHandler(CurrentStarsCard starsView)
    {
        this.starsView = starsView;
    }

    @Override
    public void handle(JsonObject data)
    {
        // Extract the array from the wrapper if it exists
        JsonArray stars = data.has("data") ? data.getAsJsonArray("data") : data.getAsJsonArray();
        
        StringBuilder starsList = new StringBuilder("<html>");
        if (stars.size() == 0) {
            starsList.append("No current stars.");
        } else {
            for (int i = 0; i < stars.size(); i++) {
                JsonObject star = stars.get(i).getAsJsonObject();
                String world = star.has("world") ? star.get("world").getAsString() : "?";
                String location = star.has("location") ? star.get("location").getAsString() : "Unknown";
                String tier = star.has("tier") ? star.get("tier").getAsString() : "?";
                starsList.append(String.format("W%s - %s (T%s)<br/>", world, location, tier));
            }
        }
        starsList.append("</html>");
        String finalText = starsList.toString();
        SwingUtilities.invokeLater(() -> starsView.setMessage(finalText));
    }
}
