package com.shootingstar.scouter.websocket.handlers;

import com.google.gson.JsonObject;
import com.shootingstar.scouter.ShootingStarPanel;

import javax.swing.SwingUtilities;
import java.awt.Color;

public class DashboardUpdateHandler implements IMessageHandler
{
    private final ShootingStarPanel panel;

    public DashboardUpdateHandler(ShootingStarPanel panel)
    {
        this.panel = panel;
    }

    @Override
    public void handle(JsonObject data)
    {
        // Parse time values (in minutes)
        if (data.has("timeSinceWaveBegan")) {
            String minutes = data.get("timeSinceWaveBegan").getAsString();
            SwingUtilities.invokeLater(() -> 
                panel.setCurrentWaveTime("Wave began: " + minutes + " min ago"));
        }
        
        if (data.has("waveEndsIn")) {
            String minutes = data.get("waveEndsIn").getAsString();
            SwingUtilities.invokeLater(() -> 
                panel.setWaveEndTime("Wave ends in: " + minutes + " min"));
        }
        
        if (data.has("startScoutingIn")) {
            String minutes = data.get("startScoutingIn").getAsString();
            boolean isScoutNow = "Scout now".equalsIgnoreCase(minutes);
            
            SwingUtilities.invokeLater(() -> {
                String scoutText = (isScoutNow && minutes.toLowerCase().contains("scout")) ?
                 "Start Scouting in: - Scout now!" : "Start Scouting in z: " + minutes + " min";
                
                Color scoutColor = isScoutNow ? Color.ORANGE : Color.WHITE;
                
                panel.setTimeToScout(scoutText);
                panel.getHeaderView().setTimeToScoutColor(scoutColor);
            });
        }
        
        if (data.has("spawnPhaseStatus")) {
            String status = data.get("spawnPhaseStatus").getAsString();
            SwingUtilities.invokeLater(() -> {
                String displayText;
                Color phaseColor;
                if ("Fully spawned".equalsIgnoreCase(status)) {
                    displayText = "Spawn Phase: Fully spawned";
                    phaseColor = Color.GREEN;
                } else {
                    displayText = "Spawn Phase ends in: " + status + " min";
                    phaseColor = Color.WHITE;
                }
                panel.getHeaderView().setSpawnPhaseEndsText(displayText);
                panel.getHeaderView().setSpawnPhaseColor(phaseColor);
            });
        }
    }
}
