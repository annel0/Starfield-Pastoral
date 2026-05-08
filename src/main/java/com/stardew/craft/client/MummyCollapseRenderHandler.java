package com.stardew.craft.client;

import com.stardew.craft.StardewCraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(modid = StardewCraft.MODID, value = Dist.CLIENT)
public final class MummyCollapseRenderHandler {

    private MummyCollapseRenderHandler() {}

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        MummyCollapseClientState.onClientTick(event);
    }
}