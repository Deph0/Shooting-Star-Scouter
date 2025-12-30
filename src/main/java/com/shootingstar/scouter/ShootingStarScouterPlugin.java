package com.shootingstar.scouter;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.CommandExecuted;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;

import com.shootingstar.scouter.websocket.MessageBuilder;
import com.shootingstar.scouter.websocket.WebSocketManager;

import java.awt.image.BufferedImage;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@PluginDescriptor(
	name = "Shooting Star Scouter"
)
public class ShootingStarScouterPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private ShootingStarScouterConfig config;

	@Inject
	private ClientToolbar clientToolbar;

	private NavigationButton navButton;
	private ShootingStarPanel panel;
	private WebSocketManager webSocketManager;

	@Override
	protected void startUp() throws Exception
	{
		log.info("Shooting Star Scouter started!");

		try {
			panel = new ShootingStarPanel(config);
			webSocketManager = new WebSocketManager(config.websocketUrl(), panel);			
			panel.setWebSocketManager(webSocketManager);

			final BufferedImage navIcon = ImageUtil.loadImageResource(getClass(), "/shooting_star_icon.png");
			int priority = config.navButtonPriority();
			navButton = NavigationButton.builder()
				.tooltip("Shooting Star Scouter")
				.icon(navIcon)
				.panel(panel)
				.priority(priority)
				.build();
			clientToolbar.addNavigation(navButton);
			log.info("Shooting Star Scouter plugin loaded successfully!");
		} catch (Exception ex) {
			log.error("Failed to start Shooting Star Scouter plugin", ex);
			throw ex;
		}
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("Shooting Star Scouter stopped!");

		if (webSocketManager != null && webSocketManager.isConnected())
		{
			webSocketManager.disconnect();
		}

		if (clientToolbar != null && navButton != null)
		{
			clientToolbar.removeNavigation(navButton);
		}
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		// if (gameStateChanged.getGameState() == GameState.LOGGED_IN)
		// {
		// 	client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "++ Shooting Star Scouter ++", null);
		// }
	}

	@Subscribe
	public void onCommandExecuted(CommandExecuted commandExecuted)
	{
		String command = commandExecuted.getCommand();
		String[] args = commandExecuted.getArguments();

		if ("star".equals(command))
		{
			handleStarCommand(args);
		}
		else if ("stardebug".equals(command))
		{
			handleStarDebugCommand(args);
		}
		else if ("starclear".equals(command))
		{
			handleStarClearCommand();
		}
	}

	private void handleStarCommand(String[] args)
	{
		if (webSocketManager == null || !webSocketManager.isConnected())
		{
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", 
				"Star Scouter: Not connected to server. Use the plugin panel to connect.", null);
			return;
		}

		// Usage: ::star <world> <tier> <location> [backup]
		// Example: ::star 302 9 Falador
		// Example: ::star 302 9 Lumbridge Swamp true
		if (args.length < 3)
		{
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", 
				"Star Scouter: Usage: ::star <world> <tier> <location> [backup]", null);
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

			String message = MessageBuilder.buildStarUpdate(world, tier, location, backup, username, timestamp);
			boolean success = webSocketManager.sendMessage(message);
			
			if (success)
			{
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
		catch (Exception ex)
		{
			log.error("Error handling star command", ex);
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", 
				"Star Scouter: Error sending star update", null);
		}
	}

	private void handleStarDebugCommand(String[] args)
	{
		// Usage: ::stardebug <count>
		// Example: ::stardebug 5
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
				client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", 
					"Star Scouter: Invalid count. Usage: ::stardebug <count>", null);
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
		
		List<com.shootingstar.scouter.views.CurrentStarsCard.StarData> mockStars = new ArrayList<>();
		
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
			
			mockStars.add(new com.shootingstar.scouter.views.CurrentStarsCard.StarData(
				world, location, tier, backup, firstFound, foundBy
			));
		}
		
		// Update the UI with mock data
		javax.swing.SwingUtilities.invokeLater(() -> {
			panel.updateStarDataCache(mockStars);
			panel.getCurrentStarsView().updateStars(mockStars);
		});
		
		client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", 
			String.format("Star Scouter: Generated %d mock stars", count), null);
	}

	private void handleStarClearCommand()
	{
		// Clear all stars from the UI
		javax.swing.SwingUtilities.invokeLater(() -> {
			panel.updateStarDataCache(new ArrayList<>());
			panel.getCurrentStarsView().updateStars(new ArrayList<>());
		});
		
		client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", 
			"Star Scouter: Cleared all stars", null);
	}

	@Provides
	ShootingStarScouterConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ShootingStarScouterConfig.class);
	}
}
