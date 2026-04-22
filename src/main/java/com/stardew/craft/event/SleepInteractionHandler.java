package com.stardew.craft.event;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.block.ModBlocks;
import com.stardew.craft.core.ModDimensions;
import com.stardew.craft.core.ModMiningDimensions;
import com.stardew.craft.network.payload.OpenSleepConfirmScreenPayload;
import com.stardew.craft.time.StardewTimeManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.BedBlock;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.CanContinueSleepingEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("removal")
@EventBusSubscriber(modid = StardewCraft.MODID, bus = EventBusSubscriber.Bus.GAME)
public final class SleepInteractionHandler {
    private SleepInteractionHandler() {
    }

    /** 玩家右键床后暂存的床位置，确认睡觉时取出使用 */
    private static final Map<UUID, BlockPos> pendingBedPositions = new ConcurrentHashMap<>();

    /** 保存玩家待确认的床位置 */
    public static void storePendingBedPos(ServerPlayer player, BlockPos bedPos) {
        pendingBedPositions.put(player.getUUID(), bedPos.immutable());
    }

    /** 取出并移除玩家待确认的床位置 */
    public static BlockPos consumePendingBedPos(ServerPlayer player) {
        return pendingBedPositions.remove(player.getUUID());
    }

    /** 清除所有暂存的床位置 */
    public static void clearAllPendingBedPositions() {
        pendingBedPositions.clear();
    }

    /**
     * 允许星露谷维度的玩家在任何时间（包括白天）继续睡觉。
     * 原版会在白天踢出睡觉的玩家（BedSleepingProblem.NOT_POSSIBLE_NOW），这里覆盖该行为。
     */
    @SubscribeEvent
    public static void onCanContinueSleeping(CanContinueSleepingEvent event) {
        var dim = event.getEntity().level().dimension();
        if (dim == ModDimensions.STARDEW_VALLEY || dim == ModMiningDimensions.STARDEW_MINING) {
            event.setContinueSleeping(true);
        }
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
        if (!com.stardew.craft.core.FarmAreaResolver.isInFarmArea(player.level(), event.getPos())) {
            player.displayClientMessage(
                    net.minecraft.network.chat.Component.translatable("stardewcraft.farm.sleep_farm_only"), true);
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);
            return;
        }

        // 不能在别人农场睡觉（需要 PERM_FULL 权限）
        if (com.stardew.craft.event.FarmAreaProtectionEvents.isOnProtectedFarm(player, event.getPos())) {
            player.displayClientMessage(
                    net.minecraft.network.chat.Component.translatable("stardewcraft.farm.sleep_no_permission"), true);
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);
            return;
        }

        int currentMinute = StardewTimeManager.get().getCurrentTime();
        storePendingBedPos(player, event.getPos());
        PacketDistributor.sendToPlayer(player, new OpenSleepConfirmScreenPayload(currentMinute));
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
    }
}
