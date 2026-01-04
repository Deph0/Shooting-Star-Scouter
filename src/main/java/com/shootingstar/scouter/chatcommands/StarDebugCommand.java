package com.shootingstar.scouter.chatcommands;

import com.google.inject.Injector;
import com.shootingstar.scouter.ShootingStarPanel;
import com.shootingstar.scouter.models.StarData;
import com.shootingstar.scouter.services.StarDataService;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;

import javax.swing.SwingUtilities;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Command to generate mock star data for testing
 */
public class StarDebugCommand implements IChatCommand
{
    private final Client client;
    private final StarDataService starDataService;
    private final ShootingStarPanel panel;

    public StarDebugCommand(Injector injector, ShootingStarPanel panel)
    {
        this.client = injector.getInstance(Client.class);
        this.starDataService = panel.getInjector().getInstance(StarDataService.class);
        this.panel = panel;
    }

    @Override
    public void execute(String[] args)
    {
        int count = 3; // Default count
        
        if (args.length > 0)
        {
            try
            {
                count = Integer.parseInt(args[0]);
                if (count < 1 || count > 50)
                {
                    client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", 
                        "Star Scouter: Count must be between 1 and 50", null);
                    return;
                }
            }
            catch (NumberFormatException ex)
            {
                client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", getUsage(), null);
                return;
            }
        }

        // Generate mock star data
        Random random = new Random();
        String[] locations = {
            "Lumbridge Swamp", "Varrock East", "Falador", "Crafting Guild",
            "Al Kharid Mine", "Wilderness", "Mining Guild", "Dwarven Mine",
            "Ardougne", "Yanille", "Piscatoris", "Fossil Island"
        };
        
        List<StarData> mockStars = new ArrayList<>();
        
        for (int i = 0; i < count; i++)
        {
            String world = String.valueOf(300 + random.nextInt(100));
            String location = locations[random.nextInt(locations.length)];
            int tier = 1 + random.nextInt(9);
            boolean backup = random.nextBoolean();
            String foundBy = "TestUser" + (i + 1);
            
            // Generate timestamp within last 2 hours
            long nowMillis = System.currentTimeMillis();
            long randomOffset = random.nextInt(7200000); // 0-2 hours in milliseconds
            String firstFound = Instant.ofEpochMilli(nowMillis - randomOffset).toString();
            
            mockStars.add(new StarData(
                world, location, "dummy", tier, backup, firstFound, foundBy
            ));
        }
        
        // Update the UI with mock data
        starDataService.updateAll(mockStars);
        SwingUtilities.invokeLater(() -> panel.refreshStars());
        
        client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", 
            String.format("Star Scouter: Generated %d mock stars", count), null);
    }

    @Override
    public String getCommandName()
    {
        return "stardebug";
    }

    @Override
    public String getDescription()
    {
        return "Generate mock star data for testing";
    }

    @Override
    public String getUsage()
    {
        return "::stardebug [count]";
    }
}
