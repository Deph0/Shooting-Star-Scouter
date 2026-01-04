package com.shootingstar.scouter.websocket.handlers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.shootingstar.scouter.ShootingStarPanel;
import com.shootingstar.scouter.models.StarData;
import com.shootingstar.scouter.services.StarDataService;

import javax.swing.SwingUtilities;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StarSyncHandler implements IMessageHandler
{
    private final ShootingStarPanel panel;
    private final StarDataService starDataService;

    public StarSyncHandler(ShootingStarPanel panel)
    {
        this.panel = panel;
        this.starDataService = panel.getInjector().getInstance(StarDataService.class);
    }

    @Override
    public void onData(JsonObject data)
    {
        // Extract the array from the wrapper if it exists
        JsonArray stars = data.has("data") ? data.getAsJsonArray("data") : data.getAsJsonArray();
        
        List<StarData> starList = new ArrayList<>();
        
        for (int i = 0; i < stars.size(); i++) {
            JsonObject star = stars.get(i).getAsJsonObject();
            StarData starData = StarData.fromJson(star);            
            starList.add(starData);
        }
        
        // Only update UI if there are changes
        if (hasChanges(starList)) {
            starDataService.updateAll(starList);
            SwingUtilities.invokeLater(() -> panel.refreshStars());
        }
    }
    
    /**
     * Check if the new star list differs from the current data
     */
    private boolean hasChanges(List<StarData> newStars)
    {
        Map<String, StarData> currentStars = starDataService.getAllStars();
        
        // Different size means changes
        if (newStars.size() != currentStars.size()) {
            return true;
        }
        
        // Check each star for differences
        for (StarData newStar : newStars) {
            StarData cached = currentStars.get(newStar.getWorld());
            
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
