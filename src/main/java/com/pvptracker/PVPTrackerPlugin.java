package com.pvptracker;

import com.google.inject.Provides;
import javax.inject.Inject;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.api.events.InteractingChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayManager;

@Slf4j
@PluginDescriptor(
	name = "PVP Tracker 1.0",
	description = "Shows statistics about PVP related activities"
)
public class PVPTrackerPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private PVPTrackerConfig config;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private StatisticsOverlay statisticsOverlay;

	@Getter
	private int playerDmg = 0;

	@Getter
	private int targetDmg = 0;

	private boolean currentlyAttacking = false;

	@Getter(AccessLevel.PACKAGE)
	private Actor currentOpponent;

	@Override
	protected void startUp() throws Exception {
		log.info("PVP Tracker has started.");
		overlayManager.add(statisticsOverlay);
	}

	@Override
	protected void shutDown() throws Exception {
		log.info("PVP Tracker stopped!");
		currentOpponent = null;
		overlayManager.remove(statisticsOverlay);
		currentlyAttacking = false;
	}

	@Provides
	PVPTrackerConfig provideConfig(ConfigManager configManager) {
		return configManager.getConfig(PVPTrackerConfig.class);
	}


	@Subscribe
	public void onHitsplatApplied(HitsplatApplied event) {

		if(currentOpponent == null) {
			return;
		}

		int damage = event.getHitsplat().getAmount();

		if(event.getActor() == client.getLocalPlayer()) {
			targetDmg += damage;
			log.info("Logging "+damage+ " for target.");


		} else if(event.getActor() == currentOpponent) {
			playerDmg += damage;
			log.info("Logging "+damage+ " for player.");
		}


	}

	@Subscribe
	public void onInteractingChanged(InteractingChanged interactEvent) {

		if(interactEvent.getSource() != client.getLocalPlayer()) {
			return;
		}

		Actor opponent = interactEvent.getTarget();

		if(opponent == null) {
			log.info("Opponent is null, returning");
			return;
		}

		currentOpponent = opponent;
	}

	public boolean compareDamage() {
		return playerDmg > targetDmg;
	}


}
