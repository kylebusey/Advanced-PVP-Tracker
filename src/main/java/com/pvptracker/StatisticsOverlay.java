package com.pvptracker;

import net.runelite.api.Actor;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;

public class StatisticsOverlay extends Overlay {

    private final PVPTrackerPlugin plugin;
    private final PVPTrackerConfig config;
    private PanelComponent panelComponent = new PanelComponent();
    private final String HEADER = "PVP Tracker 1.0";
    private final String FIRST_COLUMN_LABEL = "Opponent Name: ";
    private final String SECOND_COLUMN_LABEL = "Damage Dealt: ";

    @Inject
    private StatisticsOverlay(PVPTrackerPlugin plugin, PVPTrackerConfig config) {
        super(plugin);
        setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
        this.plugin = plugin;
        this.config = config;
    }

    @Override
    public Dimension render(Graphics2D graphics) {

        if(!config.isEnabled()) {
            return null;
        }

        final Actor opponent = plugin.getCurrentOpponent();

        if(opponent != null) {
            panelComponent.getChildren().clear();
            displayPanel(panelComponent);
        }

        return panelComponent.render(graphics);
    }

    private void displayPanel(PanelComponent panelComponent) {
        panelComponent.setPreferredSize(new Dimension(200, 400));
        panelComponent.getChildren().add(TitleComponent.builder().text(HEADER).build());
        panelComponent.getChildren().add(LineComponent.builder().left(FIRST_COLUMN_LABEL + " " + plugin.getCurrentOpponent().getName()).build());
        panelComponent.getChildren().add(LineComponent.builder().left(SECOND_COLUMN_LABEL + " " + plugin.getPlayerDmg()).build());
    }
}
