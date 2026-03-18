package com.stardew.craft.event;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.core.ModDimensions;
import com.stardew.craft.network.payload.OpenSleepConfirmScreenPayload;
import com.stardew.craft.time.StardewTimeManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.BedBlock;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@SuppressWarnings("removal")
@EventBusSubscriber(modid = StardewCraft.MODID, bus = EventBusSubscriber.Bus.GAME)
public final class SleepInteractionHandler {
    private SleepInteractionHandler() {
    }

    @SuppressWarnings("null")
    @SubscribeEvent
    public static void onRightClickBed(PlayerInteractEvent.RightClickBlock event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        if (event.getHand() != InteractionHand.MAIN_HAND) {
            return;
        }
        if (player.level().dimension() != ModDimensions.STARDEW_VALLEY) {
            return;
        }
        if (!(player.level().getBlockState(event.getPos()).getBlock() instanceof BedBlock)) {
            return;
        }

        int currentMinute = StardewTimeManager.get().getCurrentTime();
        PacketDistributor.sendToPlayer(player, new OpenSleepConfirmScreenPayload(currentMinute));
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
    }
}
