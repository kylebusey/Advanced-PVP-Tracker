package com.pvptracker;

import com.google.inject.Provides;
import javax.inject.Inject;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ActorDeath;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.api.events.InteractingChanged;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.OverlayUtil;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

@Slf4j
@PluginDescriptor(
	name = "LMS Session Tracker 1.0",
	description = "Shows statistics about your current LMS session."
)
public class LMSPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private LMSPluginConfig config;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private StatisticsOverlay statisticsOverlay;

	private WorldArea lmsArea = new WorldArea(3139, 3632, 12, 12, 0);

	private final ArrayList<Integer> desertIslandCoords = new ArrayList<>(Arrays.asList(13659, 13915, 13658, 13914));
	private final ArrayList<Integer> wildVarrockCoords = new ArrayList<>(Arrays.asList(13918, 13919, 13920, 14174, 14175, 14176, 14430, 13431, 13432));

	@Getter
	private int gamesPlayed = 0;

	@Getter
	private int gamesWon = 0;

	@Getter
	private int totalKills = 0;

	@Getter
	private boolean inCombat = false;

	@Getter
	private boolean inGame = false;

	@Getter(AccessLevel.PACKAGE)
	private Actor currentOpponent;

	@Getter(AccessLevel.PACKAGE)
	private Actor player = client.getLocalPlayer();

	@Override
	protected void startUp() throws Exception {
		log.info("LMS Tracker has started.");
		overlayManager.add(statisticsOverlay);
	}

	@Override
	protected void shutDown() throws Exception {
		log.info("LMS Tracker stopped!");
		currentOpponent = null;
		overlayManager.remove(statisticsOverlay);
		inCombat = false;
		inGame = false;
		totalKills = 0;

	}

	@Provides
	LMSPluginConfig provideConfig(ConfigManager configManager) {
		return configManager.getConfig(LMSPluginConfig.class);
	}


	@Subscribe
	public void onInteractingChanged(InteractingChanged interactEvent) {

		if(interactEvent.getSource() != player) {
			return;
		}

		Actor opponent = interactEvent.getTarget();

		if(opponent == null) {
			log.info("Opponent is null, returning");
			return;
		}

		currentOpponent = opponent;
	}


	@Subscribe
	public void onHitsplatApplied(HitsplatApplied event) {
		if(!inCombat) {
			if(event.getHitsplat() == player || event.getHitsplat() == currentOpponent) {
				inCombat = true;
				log.info("You are entering combat in the LMS Game");
			}
		}
	}

	@Subscribe
	public void onActorDeath(ActorDeath event) {

		if(event.getActor() == currentOpponent) {
			totalKills++;
			inCombat = false;
			log.info("Your opponent has died in the LMS Game");
		}

		if(event.getActor() == client.getLocalPlayer()) {
			inGame = false;
			inCombat = false;
			log.info("You have died in the LMS Game");
		}


	}

	@Subscribe
	public void onGameTick(GameTick event) {

		WorldPoint playerLocation = WorldPoint.fromLocalInstance(client, player.getLocalLocation());
		int regionId = playerLocation.getRegionID();

		if(inGameArea(playerLocation)) {
			log.info("You have been found in the LMS Area");
			inGame = false;
			inCombat = false;
		} else if(inGameMap(playerLocation)) {
			if(!inGame) {
				inGame = true;
				gamesPlayed++;
			}
		}

		log.info("Region ID: " + regionId);

		//chat msg --> "Oh dear, you are dead!"

	}

	protected boolean inGameArea(WorldPoint point) {
		return point.isInArea(lmsArea);
	}

	protected boolean inGameMap(WorldPoint point) {
		return desertIslandCoords.contains(point.getRegionID()) || wildVarrockCoords.contains(point.getRegionID());
	}


}
