package com.PlayerCoder1;

import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.ui.overlay.*;

import javax.inject.Inject;
import java.awt.*;

public class NpcHighlighterOverlay extends Overlay
{
    private final Client client;
    private final NpcHighlighterPlugin plugin;

    @Inject
    NpcHighlighterOverlay(Client client, NpcHighlighterPlugin plugin)
    {
        this.client = client;
        this.plugin = plugin;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        for (NPC npc : plugin.getHighlightedNpcs())
        {
            LocalPoint lp = npc.getLocalLocation();
            Point point = Perspective.localToCanvas(client, lp, client.getPlane(), npc.getLogicalHeight() / 2);

            if (point != null)
            {
                int size = 20;
                Point npcPoint = new Point(point.getX() - size / 2, point.getY() - size / 2 - 10);
                graphics.setColor(Color.ORANGE);
                graphics.fillRect(npcPoint.getX(), npcPoint.getY(), size, size);
            }
        }

        return null;
    }
}
