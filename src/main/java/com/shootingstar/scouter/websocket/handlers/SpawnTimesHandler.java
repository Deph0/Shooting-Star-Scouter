package com.shootingstar.scouter.websocket.handlers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.shootingstar.scouter.views.WaveTimersCard;

import javax.swing.SwingUtilities;
import java.util.ArrayList;
import java.util.List;

public class SpawnTimesHandler implements IMessageHandler
{
    private final WaveTimersCard timersView;

    public SpawnTimesHandler(WaveTimersCard timersView)
    {
        this.timersView = timersView;
    }

    @Override
    public void handle(JsonObject data)
    {
        // Extract the array from the wrapper if it exists
        JsonArray timers = data.has("data") ? data.getAsJsonArray("data") : data.getAsJsonArray();
        
        List<WaveTimersCard.WorldTimer> timerList = new ArrayList<>();
        
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
            
            timerList.add(new WaveTimersCard.WorldTimer(world,  avgSpawn));
        }

        timerList.sort((a, b) -> a.world.compareToIgnoreCase(b.world));
        
        SwingUtilities.invokeLater(() -> timersView.updateTimers(timerList));
    }
}
