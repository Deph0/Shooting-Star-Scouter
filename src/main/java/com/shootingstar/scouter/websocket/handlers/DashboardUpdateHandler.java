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
    public void onData(JsonObject data)
    {
        // Parse time values (in minutes)
        if (data.has("timeSinceWaveBegan")) {
            String minutes = data.get("timeSinceWaveBegan").getAsString();
            String waveBeganText = "Wave began: " + minutes + " min ago";

            SwingUtilities.invokeLater(() -> 
                panel.getHeaderView().getWaveBeganLabel().setText(waveBeganText));
        }
        
        if (data.has("waveEndsIn")) {
            String minutes = data.get("waveEndsIn").getAsString();
            String waveEndsText = "Wave ends in: " + minutes + " min";

            SwingUtilities.invokeLater(() -> 
                panel.getHeaderView().getWaveEndsLabel().setText(waveEndsText));
        }
        
        if (data.has("startScoutingIn")) {
            String minutes = data.get("startScoutingIn").getAsString(); // A Number or "Scout now"
            boolean isScoutNow = "Scout now".equalsIgnoreCase(minutes);

            Color scoutColor = isScoutNow ? Color.ORANGE : Color.WHITE;
            String scoutText = isScoutNow ? 
                "Start Scouting now!" : 
                "Start Scouting in: " + minutes + " min";
            
            SwingUtilities.invokeLater(() -> {
                panel.getHeaderView().getTimeToScoutLabel().setText(scoutText);
                panel.getHeaderView().getTimeToScoutLabel().setForeground(scoutColor);
            });
        }
        
        if (data.has("spawnPhaseStatus")) {
            String minutes = data.get("spawnPhaseStatus").getAsString(); // A Number or "Fully spawned"
            boolean isFullySpawned = "Fully spawned".equalsIgnoreCase(minutes);
            
            Color phaseColor = isFullySpawned ? Color.GREEN : Color.WHITE;
            String displayText = isFullySpawned ? 
                "Spawn Phase: Fully spawned" : 
                "Spawn Phase ends in: " + minutes + " min";
            
            SwingUtilities.invokeLater(() -> {
                panel.getHeaderView().getSpawnPhaseEndsLabel().setText(displayText);
                panel.getHeaderView().getSpawnPhaseEndsLabel().setForeground(phaseColor);
            });
        }
    }
}
