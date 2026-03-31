package com.stardew.craft.event;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.block.tree.WildOakLeavesBlock;
import com.stardew.craft.manager.WildLeavesPlacedManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;

@EventBusSubscriber(modid = StardewCraft.MODID)
public final class WildLeavesPlacementEvents {
    private WildLeavesPlacementEvents() {
    }

    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }
        if (event.getEntity() == null) {
            return;
        }

        markIfWildLeaves(level, event.getPos());
    }

    @SubscribeEvent
    public static void onBlockMultiPlace(BlockEvent.EntityMultiPlaceEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }
        if (event.getEntity() == null) {
            return;
        }

        for (var snapshot : event.getReplacedBlockSnapshots()) {
            markIfWildLeaves(level, snapshot.getPos());
        }
    }

    @SuppressWarnings("null")
    private static void markIfWildLeaves(ServerLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (state.getBlock() instanceof WildOakLeavesBlock) {
            WildLeavesPlacedManager.get(level).markPlaced(pos);
        }
    }
}
