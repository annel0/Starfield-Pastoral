package com.stardew.craft.npc;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.entity.npc.StardewNpcEntity;
import com.stardew.craft.npc.data.NpcDataManager;
import com.stardew.craft.npc.runtime.NpcCentralMovementService;
import com.stardew.craft.npc.runtime.NpcRuntimeManager;
import com.stardew.craft.npc.runtime.NpcScheduleRuntimeService;
import com.stardew.craft.npc.runtime.NpcSpawnManager;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

@EventBusSubscriber(modid = StardewCraft.MODID)
@SuppressWarnings("null")
public final class NpcSystem {
    private NpcSystem() {
    }

    @SubscribeEvent
    public static void onAddReloadListeners(AddReloadListenerEvent event) {     
        event.addListener(new NpcDataManager.ReloadListener());
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        NpcRuntimeManager.tickServer(event.getServer());
        if (event.getServer().getLevel(com.stardew.craft.core.ModDimensions.STARDEW_VALLEY) != null) {
            var level = event.getServer().getLevel(com.stardew.craft.core.ModDimensions.STARDEW_VALLEY);
            NpcScheduleRuntimeService.tick(level);
            NpcSpawnManager.tick(level);
            NpcCentralMovementService.tick(level);
        }
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
