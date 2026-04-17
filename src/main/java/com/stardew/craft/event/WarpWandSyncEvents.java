package com.stardew.craft.event;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.network.payload.WarpWandSyncPayload;
import com.stardew.craft.warp.WarpWandSavedData;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * 传送魔杖数据同步事件 — 玩家登录时同步已解锁目的地。
 */
@EventBusSubscriber(modid = StardewCraft.MODID)
public class WarpWandSyncEvents {

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            WarpWandSavedData data = WarpWandSavedData.get();
            PacketDistributor.sendToPlayer(player,
                    new WarpWandSyncPayload(data.getUnlockedDestinations(player.getUUID())));
        }
    }
}
