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
        for (var player : event.getServer().getPlayerList().getPlayers()) {
            if (ModDimensions.STARDEW_VALLEY.equals(player.level().dimension())
                || ModMiningDimensions.STARDEW_MINING.equals(player.level().dimension())) {
                anyPlayerInStardew = true;
                break;
            }
        }

        if (!anyPlayerInStardew) {
            if (previouslyHadPlayers) {
                // Player just left — release all forced chunks and snap NPCs to schedule targets.
                NpcSpawnManager.onAllPlayersLeft(level);
                NpcChunkForceManager.releaseAllForcedChunks(level);
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
    }

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof StardewNpcEntity npc)) {
            return;
        }
        if (event.getLevel().isClientSide()) {
            return;
        }
        
        // Wait until entity is fully synchronized with clients and id is assigned
        // cleanup of invalid NPCs is handled by their delayed validation in tick().

        if (event.getLevel() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            NpcSpawnManager.onNpcJoin(serverLevel, npc);
        }
    }
}
