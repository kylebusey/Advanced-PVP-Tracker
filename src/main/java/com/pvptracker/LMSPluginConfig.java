package com.pvptracker;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("LMStracker")
public interface LMSPluginConfig extends Config
{
	@ConfigItem(
		keyName = "Enabled Checkbox",
		name = "Enabled",
		description = "Enables/disables the plugin"
	)
	default boolean isEnabled()
	{
		return true;
	}
}
