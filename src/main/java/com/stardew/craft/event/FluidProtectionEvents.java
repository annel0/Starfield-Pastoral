package com.stardew.craft.event;

import com.stardew.craft.StardewCraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;

@EventBusSubscriber(modid = StardewCraft.MODID)
public final class FluidProtectionEvents {
    private FluidProtectionEvents() {
    }

    @SubscribeEvent
    public static void onFluidPlaceBlock(BlockEvent.FluidPlaceBlockEvent event) {
        BlockState original = event.getOriginalState();
        if (original.isAir() || original.is(event.getNewState().getBlock())) {
            return;
        }
        if (isFluidProtected(original)) {
            event.setCanceled(true);
        }
    }

    private static boolean isFluidProtected(BlockState state) {
        ResourceLocation key = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        if (!StardewCraft.MODID.equals(key.getNamespace())) {
            return false;
        }

        String className = state.getBlock().getClass().getName();
        return state.getBlock() instanceof EntityBlock
                || className.startsWith("com.stardew.craft.block.decor.")
                || className.startsWith("com.stardew.craft.block.utility.")
                || className.startsWith("com.stardew.craft.block.mastery.")
                || className.startsWith("com.stardew.craft.block.cooking.")
                || className.startsWith("com.stardew.craft.block.tv.")
                || className.startsWith("com.stardew.craft.communitycenter.block.")
                || className.startsWith("com.stardew.craft.block.portal.")
                || className.startsWith("com.stardew.craft.block.mine.");
    }
}
