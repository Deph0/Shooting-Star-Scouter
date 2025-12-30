package com.shootingstar.scouter.websocket.handlers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.shootingstar.scouter.models.WorldSpawnTime;
import com.shootingstar.scouter.views.WaveTimersCard;

import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SpawnTimesHandler implements IMessageHandler
{
    private static final Logger log = LoggerFactory.getLogger(SpawnTimesHandler.class);
    private final WaveTimersCard timersView;

    public SpawnTimesHandler(WaveTimersCard timersView)
    {
        this.timersView = timersView;
    }

    @Override
    public void onData(JsonObject data)
    {
        // Extract the array from the wrapper if it exists
        JsonArray timers = data.has("data") ? data.getAsJsonArray("data") : data.getAsJsonArray();
        
        List<WorldSpawnTime> timerList = new ArrayList<>();
        
        for (int i = 0; i < timers.size(); i++) {
            JsonObject timer = timers.get(i).getAsJsonObject();
            String world = timer.has("world") ? timer.get("world").getAsString() : "";
            String avgSpawn = timer.has("avgSpawn") ? timer.get("avgSpawn").getAsString() : "";
            
            // Skip entries with empty world
            if (world.isEmpty()) {
                continue;
            }
            // If world is present but avgSpawn is missing, set avgSpawn to "?"
            if (!world.isEmpty() && avgSpawn.isEmpty()) {
                avgSpawn = "?";
            }
            else
            {
                avgSpawn = avgSpawn + " min";
            }
            
            timerList.add(new WorldSpawnTime(world,  avgSpawn));
        }

        timerList.sort((a, b) -> a.world.compareToIgnoreCase(b.world));
        
        SwingUtilities.invokeLater(() -> this.updateTimers(timerList));
    }

    private void updateTimers(List<WorldSpawnTime> timers)
    {
        // Build a map of world -> spawn time for quick lookup
        Map<String, String> timerMap = new HashMap<>();
        for (WorldSpawnTime timer : timers) {
            timerMap.put(timer.getWorld(), timer.getSpawnTime());
        }
        
        // Track which worlds we've seen
        Set<String> processedWorlds = new HashSet<>();
        
        // Update existing rows and remove obsolete ones
        for (int i = timersView.getTableModel().getRowCount() - 1; i >= 0; i--) {
            String currentWorld = (String) timersView.getTableModel().getValueAt(i, 0);
            
            if (timerMap.containsKey(currentWorld)) {
                String newTime = timerMap.get(currentWorld);
                String currentTime = (String) timersView.getTableModel().getValueAt(i, 1);
                
                // Only update if the time changed
                if (!newTime.equals(currentTime)) {
                    log.debug("Updating spawn time for world {}: {} -> {}", currentWorld, currentTime, newTime);
                    timersView.getTableModel().setValueAt(newTime, i, 1);
                }
                processedWorlds.add(currentWorld);
            } else {
                // World no longer exists, remove row
                log.debug("Removing obsolete world from spawn times: {}", currentWorld);
                timersView.getTableModel().removeRow(i);
            }
        }
        
        // Add new worlds that weren't in the table
        for (WorldSpawnTime timer : timers) {
            if (!processedWorlds.contains(timer.getWorld())) {
                log.debug("Adding new world to spawn times: {} with time {}", timer.getWorld(), timer.getSpawnTime());
                timersView.getTableModel().addRow(new Object[]{timer.getWorld(), timer.getSpawnTime()});
            }
        }
    }
}
