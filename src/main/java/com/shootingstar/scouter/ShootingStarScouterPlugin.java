package com.shootingstar.scouter;

import com.google.inject.Injector;
import com.google.inject.Provides;
import javax.inject.Inject;
import javax.swing.SwingUtilities;

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

import com.shootingstar.scouter.chatcommands.*;
import com.shootingstar.scouter.services.StarDataService;
import com.shootingstar.scouter.websocket.WebSocketManager;

import java.awt.image.BufferedImage;

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

	@Inject
	private StarDataService starDataService;

	@Inject
	private Injector injector;

	@Inject
	private CommandDispatcher commandDispatcher;

	private NavigationButton navButton;
	private ShootingStarPanel panel;
	private WebSocketManager webSocketManager;

	@Override
	protected void startUp() throws Exception
	{
		log.info("Shooting Star Scouter started!");

		try {
			panel = new ShootingStarPanel(injector);
			webSocketManager = new WebSocketManager(config.websocketUrl(), panel);		
			panel.setWebSocketManager(webSocketManager); // maybe websocket manager can be injected into panel ?
			// do webSocketManager.messageDispatcher.registerHandler() here - to remove panel dependency later

			// Register command handlers
			commandDispatcher.registerCommand(new StarCommand(injector, panel, webSocketManager));
			commandDispatcher.registerCommand(new StarDebugCommand(injector, panel));
			commandDispatcher.registerCommand(new StarClearCommand(injector, panel));

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

		// Try to dispatch the command
		if (commandDispatcher != null && !commandDispatcher.dispatch(command, args))
		{
			// Command not found - could show help here if desired
			log.debug("Unknown command: {}", command);
		}
	}


	@Provides
	ShootingStarScouterConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ShootingStarScouterConfig.class);
	}
}
