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
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;

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
	private java.awt.image.BufferedImage navIcon;

	@Override
	protected void startUp() throws Exception
	{
		log.info("Shooting Star Scouter started!");

		// Create the side panel UI and navigation button
		panel = new ShootingStarPanel();

		// Create a small empty icon so the button has an icon in the toolbar.
		navIcon = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = navIcon.createGraphics();
		g.dispose();
		int priority = config.navButtonPriority();
		navButton = NavigationButton.builder()
			.tooltip("Shooting Star Scouter")
			.icon(navIcon)
			.panel(panel)
			.priority(priority)
			.build();
		clientToolbar.addNavigation(navButton);
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("Shooting Star Scouter stopped!");

		if (clientToolbar != null && navButton != null)
		{
			clientToolbar.removeNavigation(navButton);
		}
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() == GameState.LOGGED_IN)
		{
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "++ Shooting Star Scouter ++", null);
		}
	}

	@Provides
	ShootingStarScouterConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ShootingStarScouterConfig.class);
	}
}
