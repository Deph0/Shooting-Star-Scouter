package com.shootingstar.scouter;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;

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

	@Provides
	ShootingStarScouterConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ShootingStarScouterConfig.class);
	}
}
