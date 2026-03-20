package com.stardew.craft.event;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.interior.InteriorSubspaceManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.ExplosionEvent;

@EventBusSubscriber(modid = StardewCraft.MODID)
public class InteriorSubspaceProtectionEvents {

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }
        if (!InteriorSubspaceManager.isInteriorRegion(level, event.getPos())) {
            return;
        }
        if (!(event.getPlayer() instanceof ServerPlayer player)) {
            event.setCanceled(true);
            return;
        }
        if (player.isCreative()) {
            return;
        }

        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }
        if (!InteriorSubspaceManager.isInteriorRegion(level, event.getPos())) {
            return;
        }

        if (event.getEntity() instanceof Player player && player.isCreative()) {
            return;
        }

        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onBlockMultiPlace(BlockEvent.EntityMultiPlaceEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }
        if (!InteriorSubspaceManager.isInteriorRegion(level, event.getPos())) {
            return;
        }

        if (event.getEntity() instanceof Player player && player.isCreative()) {
            return;
        }

        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onFluidPlace(BlockEvent.FluidPlaceBlockEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }
        if (!InteriorSubspaceManager.isInteriorRegion(level, event.getPos())) {
            return;
        }
        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onExplosionDetonate(ExplosionEvent.Detonate event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }

        event.getAffectedBlocks().removeIf(pos -> InteriorSubspaceManager.isInteriorRegion(level, pos));
    }
}
