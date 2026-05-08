package com.stardew.craft.cutscene;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.cutscene.data.EventRegistry;
import com.stardew.craft.cutscene.network.ClientEventSeenCache;
import com.stardew.craft.cutscene.network.SyncEventRegistryPayload;
import com.stardew.craft.cutscene.network.SyncEventSeenPayload;
import com.stardew.craft.cutscene.runtime.EventTriggerChecker;
import com.stardew.craft.cutscene.server.EventSeenData;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;

/**
 * Server-side event bus hooks for the cutscene/event system.
 * - Registers JSON reload listener
 * - Syncs eventsSeen on player login
 */
@EventBusSubscriber(modid = StardewCraft.MODID)
public final class CutsceneSystem {

    private CutsceneSystem() {}

    @SubscribeEvent
    public static void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(new EventRegistry.ReloadListener());
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            // Sync cutscene event JSON data so client can play them on dedicated servers
            PacketDistributor.sendToPlayer(player,
                    new SyncEventRegistryPayload(new java.util.HashMap<>(EventRegistry.getRawJsonMap())));

            EventSeenData data = EventSeenData.get(player.serverLevel());
            var seen = data.getSeenEvents(player.getUUID());
            PacketDistributor.sendToPlayer(player, new SyncEventSeenPayload(new ArrayList<>(seen)));

            // Push friendship overview so that client-side "friendship" preconditions can be
            // evaluated without waiting for the player to open the social menu. Without this,
            // auto-triggered enter_area events gated on friendship never fire.
            com.stardew.craft.network.payload.RequestNpcFriendshipOverviewPayload.sendOverviewTo(player);

            // Offline players miss the overnight wake_up scan. Rebuild and dispatch their
            // personal queue after login sync so shared-farm storylines stay independent.
            com.stardew.craft.cutscene.server.WakeUpEventScheduler.syncOnLogin(player);
        }
    }

    /**
     * Client-side cleanup on disconnect. Without this, the seen-event set from a previous
     * world/server lingers in memory; reconnecting to a different save would incorrectly
     * treat same-id events as already seen until the new SyncEventSeenPayload arrives.
     */
    @EventBusSubscriber(modid = StardewCraft.MODID, value = Dist.CLIENT)
    public static final class ClientEvents {
        private ClientEvents() {}

        @SubscribeEvent
        public static void onClientDisconnect(PlayerEvent.PlayerLoggedOutEvent event) {
            ClientEventSeenCache.reset();
            EventTriggerChecker.reset();
        }
    }
}
