package com.shootingstar.scouter.chatcommands;

import com.google.inject.Injector;
import com.shootingstar.scouter.ShootingStarPanel;
import com.shootingstar.scouter.models.StarData;
import com.shootingstar.scouter.services.StarDataService;
import com.shootingstar.scouter.websocket.MessageBuilder;
import com.shootingstar.scouter.websocket.WebSocketManager;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;

import javax.swing.SwingUtilities;
import java.time.Instant;

/**
 * Command to report a shooting star
 */
public class StarCommand implements IChatCommand
{
    private final Client client;
    private final StarDataService starDataService;
    private final ShootingStarPanel panel;
    private final WebSocketManager webSocketManager;

    public StarCommand(Injector injector, ShootingStarPanel panel, WebSocketManager webSocketManager)
    {
        this.client = injector.getInstance(Client.class);
        this.starDataService = panel.getInjector().getInstance(StarDataService.class);
        this.panel = panel;
        this.webSocketManager = webSocketManager;
    }

    @Override
    public void execute(String[] args)
    {
        if (webSocketManager == null || !webSocketManager.isConnected())
        {
            client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", 
                "Star Scouter: Not connected to server. Use the plugin panel to connect.", null);
            return;
        }

        if (args.length < 3)
        {
            client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", getUsage(), null);
            client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", 
                "Example: ::star 302 9 Lumbridge Swamp", null);
            return;
        }

        try
        {
            String world = args[0];
            int tier = Integer.parseInt(args[1]);
            
            // Check if last argument is the backup flag
            boolean backup = args.length > 3 && "true".equalsIgnoreCase(args[args.length - 1]);
            
            // Join all arguments from index 2 to build location (excluding backup flag if present)
            int locationEndIndex = backup ? args.length - 1 : args.length;
            StringBuilder locationBuilder = new StringBuilder();
            for (int i = 2; i < locationEndIndex; i++)
            {
                if (i > 2)
                {
                    locationBuilder.append(" ");
                }
                locationBuilder.append(args[i]);
            }
            String location = locationBuilder.toString();

            // Validate tier range
            if (tier < 1 || tier > 9)
            {
                client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", 
                    "Star Scouter: Tier must be between 1 and 9", null);
                return;
            }

            // Get current username and timestamp
            String username = client.getLocalPlayer() != null ? client.getLocalPlayer().getName() : "Unknown";
            String timestamp = Instant.now().toString();

            // Generate location alias by approximation (e.g., "Lumbridge Swamp" -> "ls")
            // Ideally, the command input should write "VSE" for "Varrock South East", but for now we use the reverse approach
            String locationalias = "";
            String[] words = location.split("\\s+");
            for (String word : words) {
                if (!word.isEmpty()) {
                    locationalias += word.substring(0, 1).toLowerCase();
                }
            }

            StarData starData = new StarData(world, location, locationalias, tier, backup, timestamp, username);
            String message = MessageBuilder.buildStarUpdate(starData);
            boolean success = webSocketManager.sendMessage(message);
            
            if (success)
            {
                starDataService.addOrUpdate(starData);
                SwingUtilities.invokeLater(() -> panel.refreshStars());
                client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", 
                    String.format("Star Scouter: Reported T%d star on W%s at %s%s", 
                        tier, world, location, backup ? " (backup)" : ""), null);
            }
            else
            {
                client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", 
                    "Star Scouter: Failed to send star update", null);
            }
        }
        catch (NumberFormatException ex)
        {
            client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", 
                "Star Scouter: Tier must be a number (1-9)", null);
        }
    }

    @Override
    public String getCommandName()
    {
        return "star";
    }

    @Override
    public String getDescription()
    {
        return "Report a shooting star location";
    }

    @Override
    public String getUsage()
    {
        return "::star <world> <tier> <location> [backup]";
    }
}
