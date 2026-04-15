package com.stardew.craft.event;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.core.FarmAreaHelper;
import com.stardew.craft.core.ModDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;

/**
 * 农场区域保护：生存模式下只能在农场区域内放置/破坏方块。
 * 创造模式不受限。
 */
@EventBusSubscriber(modid = StardewCraft.MODID)
public class FarmAreaProtectionEvents {

    /**
     * 破坏方块：BreakEvent 取消安全（方块不会被破坏，无物品丢失）。
     */
    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }
        if (level.dimension() != ModDimensions.STARDEW_VALLEY) {
            return;
        }
        if (!(event.getPlayer() instanceof ServerPlayer player)) {
            return;
        }
        if (player.isCreative()) {
            return;
        }
        if (FarmAreaHelper.isInFarmArea(level, event.getPos())) {
            return;
        }
        event.setCanceled(true);
        player.displayClientMessage(
                Component.translatable("stardewcraft.farm.build_farm_only"), true);
    }

    /**
     * 放置方块：EntityPlaceEvent 在方块已放置后触发。
     * 不取消事件（取消会丢物品），而是立即 destroyBlock 使其掉落回去。
     * 注意：斧头去皮/除锈、锄头锄地等工具交互也会触发此事件（方块替换），
     * 此时 replacedBlock 不是空气，应放行。
     */
    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }
        if (level.dimension() != ModDimensions.STARDEW_VALLEY) {
            return;
        }
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        if (player.isCreative()) {
            return;
        }
        // 方块替换（去皮、除锈、锄地等工具交互）→ 放行
        // 如果被替换的方块不是空气，说明是工具交互而非手动放置
        if (!event.getBlockSnapshot().getState().isAir()) {
            return;
        }
        BlockPos pos = event.getPos();
        if (FarmAreaHelper.isInFarmArea(level, pos)) {
            return;
        }
        // 立即破坏刚放置的方块，使其掉落为物品
        level.destroyBlock(pos, true, player);
        player.displayClientMessage(
                Component.translatable("stardewcraft.farm.build_farm_only"), true);
    }
}
