package com.pvptracker;

import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;

public class StatisticsOverlay extends Overlay {

    private final LMSPlugin plugin;
    private final LMSPluginConfig config;
    private final Client client;

    private PanelComponent panelComponent = new PanelComponent();
    private final String HEADER = "LMS Session Tracker 0.1";
    private final String GAMES_PLAYED_LABEL = "Games Played: ";
    private final String GAMES_WON_LABEL = "Games Won: ";
    private final String TOTAL_KILLS_LABEL = "Total Kills: ";

    @Inject
    private StatisticsOverlay(LMSPlugin plugin, LMSPluginConfig config, Client client) {
        super(plugin);
        setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
        this.plugin = plugin;
        this.config = config;
        this.client = client;
    }

    @Override
    public Dimension render(Graphics2D graphics) {

        if(!config.isEnabled()) {
            return null;
        }

        final Actor currentPlayer = plugin.getPlayer();
        WorldPoint playerLocation = WorldPoint.fromLocalInstance(client, currentPlayer.getLocalLocation());

        if(plugin.inGameArea(playerLocation) || plugin.inGameMap(playerLocation)) {
            panelComponent.getChildren().clear();
            displayPanel(panelComponent);
        } else {
            panelComponent.getChildren().clear();
        }

        return panelComponent.render(graphics);
    }

    private void displayPanel(PanelComponent panelComponent) {
        panelComponent.setPreferredSize(new Dimension(200, 400));
        panelComponent.getChildren().add(TitleComponent.builder().text(HEADER).build());
        panelComponent.getChildren().add(LineComponent.builder().left(GAMES_PLAYED_LABEL + " " + plugin.getGamesPlayed()).build());
        panelComponent.getChildren().add(LineComponent.builder().left(GAMES_WON_LABEL + " " + plugin.getGamesWon()).build());
        panelComponent.getChildren().add(LineComponent.builder().left(TOTAL_KILLS_LABEL + " " + plugin.getTotalKills()).build());

    }
}
