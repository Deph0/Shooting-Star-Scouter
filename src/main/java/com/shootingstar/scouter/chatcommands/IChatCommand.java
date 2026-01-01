package com.shootingstar.scouter.chatcommands;

/**
 * Interface for chat command handlers.
 */
public interface IChatCommand
{
    /**
     * Execute the command with the given arguments
     * @param args Command arguments (excluding the command name itself)
     */
    void execute(String[] args);
    
    /**
     * Get the command name (without the :: prefix)
     * @return The command name
     */
    String getCommandName();
    
    /**
     * Get a description of what this command does
     * @return Command description
     */
    String getDescription();
    
    /**
     * Get usage instructions for this command
     * @return Usage string (e.g., "::star <world> <tier> <location> [backup]")
     */
    String getUsage();
}
