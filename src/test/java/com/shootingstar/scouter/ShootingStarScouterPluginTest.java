package com.shootingstar.scouter;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class ShootingStarScouterPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(ShootingStarScouterPlugin.class);
		RuneLite.main(args);
	}
}