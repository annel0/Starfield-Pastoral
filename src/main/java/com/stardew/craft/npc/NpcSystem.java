package com.stardew.craft.npc;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.core.ModDimensions;
import com.stardew.craft.core.ModMiningDimensions;
import com.stardew.craft.entity.npc.StardewNpcEntity;
import com.stardew.craft.npc.data.NpcDataManager;
import com.stardew.craft.npc.runtime.NpcCentralMovementService;
import com.stardew.craft.npc.runtime.NpcChunkForceManager;
import com.stardew.craft.npc.runtime.NpcRuntimeManager;
import com.stardew.craft.npc.runtime.NpcScheduleRuntimeService;
import com.stardew.craft.npc.runtime.NpcSpawnManager;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

@EventBusSubscriber(modid = StardewCraft.MODID)
@SuppressWarnings("null")
public final class NpcSystem {
    /** Tracks whether the last tick had players in the dimension. */
    private static boolean previouslyHadPlayers = false;

    private NpcSystem() {
    }

    @SubscribeEvent
    public static void onAddReloadListeners(AddReloadListenerEvent event) {     
        event.addListener(new NpcDataManager.ReloadListener());
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        NpcRuntimeManager.tickServer(event.getServer());
        ServerLevel level = event.getServer().getLevel(ModDimensions.STARDEW_VALLEY);
        if (level == null) {
            return;
        }

        boolean anyPlayerInStardew = false;
        boolean anyPlayerInMining = false;
        for (var player : event.getServer().getPlayerList().getPlayers()) {
            if (ModDimensions.STARDEW_VALLEY.equals(player.level().dimension())) {
                anyPlayerInStardew = true;
            }
            if (ModMiningDimensions.STARDEW_MINING.equals(player.level().dimension())) {
                anyPlayerInStardew = true;
                anyPlayerInMining = true;
            }
        }

        if (!anyPlayerInStardew) {
            if (previouslyHadPlayers) {
                // Player just left — release all forced chunks and snap NPCs to schedule targets.
                NpcSpawnManager.onAllPlayersLeft(level);
                NpcChunkForceManager.releaseAllForcedChunks(level);
                // Also release mining dimension forced chunks
                ServerLevel mineLevel = event.getServer().getLevel(ModMiningDimensions.STARDEW_MINING);
                if (mineLevel != null) {
                    NpcChunkForceManager.releaseAllForcedChunks(mineLevel);
                }
                NpcScheduleRuntimeService.invalidateCache();
                previouslyHadPlayers = false;
            }
            // No player in dimension — skip all NPC ticking.
            return;
        }

        if (!previouslyHadPlayers) {
            // Player just entered — invalidate caches & snap NPCs into position.
            NpcScheduleRuntimeService.invalidateCache();
            NpcSpawnManager.onPlayerEntered(level);
            previouslyHadPlayers = true;
        }

        NpcScheduleRuntimeService.tick(level);
        NpcSpawnManager.tick(level);
        NpcCentralMovementService.tick(level);
        com.stardew.craft.festival.EggFestivalService.tickNpcActors(level);

        // Tick mining-dimension NPCs (e.g. Dwarf) when any player is in the mine
        if (anyPlayerInMining) {
            ServerLevel mineLevel = event.getServer().getLevel(ModMiningDimensions.STARDEW_MINING);
            if (mineLevel != null) {
                NpcSpawnManager.tickMiningDimension(mineLevel);
            } else {
            }
        }
    }

    /**
     * 强制立刻执行一次 NPC 系统 tick，用于跨维度传送后确保 NPC 立刻刷新。
     */
    public static void forceTickNow(ServerLevel level) {
        if (!ModDimensions.STARDEW_VALLEY.equals(level.dimension())) return;
        if (!previouslyHadPlayers) {
            NpcScheduleRuntimeService.invalidateCache();
            NpcSpawnManager.onPlayerEntered(level);
            previouslyHadPlayers = true;
        }
        NpcScheduleRuntimeService.tick(level);
        // Only force-spawn wizard if not already tracked, to avoid
        // creating duplicates when the tower chunk loads serialised entities.
        if (NpcSpawnManager.getTrackedNpc(level, "wizard") == null) {
            NpcSpawnManager.forceSpawnNpc("wizard");
        }
        NpcSpawnManager.tick(level);
    }

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof StardewNpcEntity npc)) {
            return;
        }
        if (event.getLevel().isClientSide()) {
            return;
        }

        if (event.getLevel() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            if (NpcSpawnManager.onNpcJoin(serverLevel, npc)) {
                event.setCanceled(true);
            }
        }
    }
}
