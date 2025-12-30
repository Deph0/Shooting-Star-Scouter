package com.shootingstar.scouter.websocket.handlers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.shootingstar.scouter.ShootingStarPanel;
import com.shootingstar.scouter.views.CurrentStarsCard.StarData;

import javax.swing.SwingUtilities;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StarSyncHandler implements IMessageHandler
{
    private final ShootingStarPanel panel;

    public StarSyncHandler(ShootingStarPanel panel)
    {
        this.panel = panel;
    }

    @Override
    public void onData(JsonObject data)
    {
        // Extract the array from the wrapper if it exists
        JsonArray stars = data.has("data") ? data.getAsJsonArray("data") : data.getAsJsonArray();
        
        List<StarData> starList = new ArrayList<>();
        
        for (int i = 0; i < stars.size(); i++) {
            JsonObject star = stars.get(i).getAsJsonObject();
            
            String world = star.has("world") ? star.get("world").getAsString() : "?";
            String location = star.has("location") ? star.get("location").getAsString() : "Unknown";
            int tier = star.has("tier") ? star.get("tier").getAsInt() : 0;
            boolean backup = star.has("backup") && star.get("backup").getAsBoolean();
            String firstFound = star.has("firstFound") ? star.get("firstFound").getAsString() : "";
            String foundBy = star.has("foundBy") ? star.get("foundBy").getAsString() : "";
            
            starList.add(new StarData(world, location, tier, backup, firstFound, foundBy));
        }
        
        // Only update UI if there are changes
        if (hasChanges(starList)) {
            SwingUtilities.invokeLater(() -> {
                panel.updateStarDataCache(starList);
                panel.getCurrentStarsView().updateStars(starList);
            });
        }
    }
    
    /**
     * Check if the new star list differs from the current cache
     */
    private boolean hasChanges(List<StarData> newStars)
    {
        Map<String, StarData> currentCache = panel.getStarDataCache();
        
        // Different size means changes
        if (newStars.size() != currentCache.size()) {
            return true;
        }
        
        // Check each star for differences
        for (StarData newStar : newStars) {
            StarData cached = currentCache.get(newStar.getWorld());
            
            if (cached == null) {
                return true; // New star added
            }
            
            // Compare all fields
            if (!cached.getLocation().equals(newStar.getLocation()) ||
                cached.getTier() != newStar.getTier() ||
                cached.isBackup() != newStar.isBackup() ||
                !cached.getFirstFound().equals(newStar.getFirstFound()) ||
                !cached.getFoundBy().equals(newStar.getFoundBy())) {
                return true; // Star data changed
            }
        }
        
        return false; // No changes detected
    }
}
