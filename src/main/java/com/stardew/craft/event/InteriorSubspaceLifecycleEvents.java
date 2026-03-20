package com.stardew.craft.event;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.core.ModDimensions;
import com.stardew.craft.interior.InteriorSubspaceManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

@EventBusSubscriber(modid = StardewCraft.MODID)
@SuppressWarnings("null")
public class InteriorSubspaceLifecycleEvents {

    private static final String PLAYER_FIRST_INTERIOR_ENTERED = "stardewcraft_interior_region_first_entered";
    private static int tickCounter = 0;

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        ServerLevel stardew = event.getServer().getLevel(ModDimensions.STARDEW_VALLEY);
        if (stardew == null) {
            return;
        }

        InteriorSubspaceManager.ensureLoaded(stardew, "server_started");
    }

    @SubscribeEvent
    public static void onPlayerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        if (!ModDimensions.STARDEW_VALLEY.equals(player.serverLevel().dimension())) {
            return;
        }

        InteriorSubspaceManager.ensureLoaded(player.serverLevel(), "enter_stardew_dimension");
    }

    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }
        if (!ModDimensions.STARDEW_VALLEY.equals(level.dimension())) {
            return;
        }

        tickCounter++;
        if (tickCounter < 20) {
            return;
        }
        tickCounter = 0;

        // 兜底触发：老存档/特殊进服时序下可能错过 server_started 与 dimension_changed。
        // 这里只在星露谷维度存在玩家时每秒尝试一次，ensureLoaded 内部会快速短路。
        if (!level.players().isEmpty()) {
            InteriorSubspaceManager.ensureLoaded(level, "stardew_player_present");
        }

        for (ServerPlayer player : level.players()) {
            if (player.getPersistentData().getBoolean(PLAYER_FIRST_INTERIOR_ENTERED)) {
                continue;
            }

            if (InteriorSubspaceManager.isInteriorRegion(level, player.blockPosition())) {
                InteriorSubspaceManager.ensureLoaded(level, "first_enter_interior_region");
                player.getPersistentData().putBoolean(PLAYER_FIRST_INTERIOR_ENTERED, true);
            }
        }
    }
}
