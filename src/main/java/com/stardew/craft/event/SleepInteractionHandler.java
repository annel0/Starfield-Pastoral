package com.stardew.craft.event;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.block.ModBlocks;
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
        var state = player.level().getBlockState(event.getPos());
        boolean isVanillaBed = state.getBlock() instanceof BedBlock;
        boolean isDecorBed = state.is(ModBlocks.BED_1.get()) || state.is(ModBlocks.BED_2.get());
        if (!isVanillaBed && !isDecorBed) {
            return;
        }

        // 只能在农场区域睡觉
        if (!com.stardew.craft.core.FarmAreaHelper.isInFarmArea(player.level(), event.getPos())) {
            player.displayClientMessage(
                    net.minecraft.network.chat.Component.translatable("stardewcraft.farm.sleep_farm_only"), true);
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);
            return;
        }

        int currentMinute = StardewTimeManager.get().getCurrentTime();
        PacketDistributor.sendToPlayer(player, new OpenSleepConfirmScreenPayload(currentMinute));
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
    }
}
