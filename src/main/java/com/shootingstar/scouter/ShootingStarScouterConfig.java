package com.shootingstar.scouter;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("shootingstarscouter")
public interface ShootingStarScouterConfig extends Config
{
	// @ConfigItem(
	// 	keyName = "greeting",
	// 	name = "Welcome Greeting",
	// 	description = "The message to show to the user when they login"
	// )
	// default String greeting()
	// {
	// 	return "Hello";
	// }

	@ConfigItem(
		keyName = "navPriority",
		name = "Priority",
		description = "Priority (ordering) of the plugin navigation button in the sidebar (higher = earlier)\nTurn plugin off and on to apply changes."
	)
	default int navButtonPriority()
	{
		return 6;
	}

	@ConfigItem(
		keyName = "websocketUrl",
		name = "WebSocket URL",
		description = "The WebSocket server URL for star data updates"
	)
	default String websocketUrl()
	{
		return "wss://starhunt.texasmonstermap.com";
		// return "wss://starhunt-ws-server.fly.dev";
		// return "ws://localhost:8080/stars";
	}
}
