package com.shootingstar.scouter.chatcommands;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;

/**
 * Dispatches chat commands to their respective handlers.
 */
@Slf4j
@Singleton
public class CommandDispatcher
{
    private final Map<String, IChatCommand> commands = new HashMap<>();
    @Inject private Client client;

    @Inject
    public CommandDispatcher()
    {
    }

    /**
     * Register a command handler
     * @param command The command to register
     */
    public void registerCommand(IChatCommand command)
    {
        commands.put(command.getCommandName().toLowerCase(), command);
        log.debug("Registered command: {}", command.getCommandName());
    }

    /**
     * Dispatch a command to its handler
     * @param commandName The command name (without ::)
     * @param args The command arguments
     * @return true if command was found and executed, false otherwise
     */
    public boolean dispatch(String commandName, String[] args)
    {
        IChatCommand command = commands.get(commandName.toLowerCase());
        
        if (command == null)
        {
            return false;
        }

        try
        {
            command.execute(args);
            return true;
        }
        catch (Exception ex)
        {
            log.error("Error executing command: {}", commandName, ex);
            client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", 
                "Star Scouter: Error executing command: " + ex.getMessage(), null);
            return true; // Command was found, just failed to execute
        }
    }

    /**
     * Show help for all registered commands
     */
    public void showHelp()
    {
        client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", 
            "Star Scouter Commands:", null);
        
        for (IChatCommand command : commands.values())
        {
            client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", 
                "  " + command.getUsage(), null);
            client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", 
                "    " + command.getDescription(), null);
        }
    }

    /**
     * Show help for a specific command
     * @param commandName The command name
     */
    public void showHelp(String commandName)
    {
        IChatCommand command = commands.get(commandName.toLowerCase());
        
        if (command == null)
        {
            client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", 
                "Star Scouter: Unknown command: " + commandName, null);
            return;
        }

        client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", 
            command.getUsage(), null);
        client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", 
            "  " + command.getDescription(), null);
    }
}
