package com.shootingstar.scouter.chatcommands;

import com.google.inject.Injector;
import com.shootingstar.scouter.ShootingStarPanel;
import com.shootingstar.scouter.services.StarDataService;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;

import javax.swing.SwingUtilities;

/**
 * Command to clear all stars from the UI
 */
public class StarClearCommand implements IChatCommand
{
    private final Client client;
    private final StarDataService starDataService;
    private final ShootingStarPanel panel;

    public StarClearCommand(Injector injector, ShootingStarPanel panel)
    {
        this.client = injector.getInstance(Client.class);
        this.starDataService = panel.getInjector().getInstance(StarDataService.class);
        this.panel = panel;
    }

    @Override
    public void execute(String[] args)
    {
        SwingUtilities.invokeLater(() -> {
            starDataService.clearAll();
            panel.refreshStars();
        });
        
        client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", 
            "Star Scouter: Cleared all stars", null);
    }

    @Override
    public String getCommandName()
    {
        return "starclear";
    }

    @Override
    public String getDescription()
    {
        return "Clear all stars from the UI";
    }

    @Override
    public String getUsage()
    {
        return "::starclear";
    }
}
