package com.stardew.craft.communitycenter;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.communitycenter.data.BundleDataManager;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;

@EventBusSubscriber(modid = StardewCraft.MODID)
public final class CommunityCenterSystem {

    private CommunityCenterSystem() {}

    @SubscribeEvent
    public static void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(new BundleDataManager.ReloadListener());
    }
}
