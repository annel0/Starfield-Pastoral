package com.stardew.craft.client.renderer.entity.indicator;

import com.stardew.craft.entity.npc.StardewNpcEntity;
import net.minecraft.client.player.LocalPlayer;

/**
 * Provides a world-space overhead indicator above NPCs.
 */
@FunctionalInterface
public interface NpcOverheadIndicatorProvider {
    NpcOverheadIndicator resolve(StardewNpcEntity npc, LocalPlayer localPlayer);
}
