package com.PlayerCoder1;

import net.runelite.api.*;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;

@PluginDescriptor(
        name = "NPC",
        description = "Testing a way to highlight NPCs"
)
public class NpcHighlighterPlugin extends Plugin
{
    @Inject
    private Client client;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private NpcHighlighterOverlay npcHighlighterOverlay;

    // Maintain a set of NPCs to highlight
    private Set<NPC> highlightedNpcs = new HashSet<>();

    @Override
    protected void startUp() throws Exception
    {
        overlayManager.add(npcHighlighterOverlay);
    }

    @Override
    protected void shutDown() throws Exception
    {
        overlayManager.remove(npcHighlighterOverlay);
    }

    @Subscribe
    public void onNpcSpawned(NpcSpawned npcSpawned)
    {
        NPC npc = npcSpawned.getNpc();
        if (npc.getId() == 5788) {
            highlightedNpcs.add(npc);
        }
    }

    @Subscribe
    public void onNpcDespawned(NpcDespawned npcDespawned)
    {
        NPC npc = npcDespawned.getNpc();
        highlightedNpcs.remove(npc);
    }

    public Set<NPC> getHighlightedNpcs() {
        return highlightedNpcs;
    }
}
