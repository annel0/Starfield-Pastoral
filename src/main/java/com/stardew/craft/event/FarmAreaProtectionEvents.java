package com.stardew.craft.event;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.block.ModBlocks;
import com.stardew.craft.core.FarmAreaResolver;
import com.stardew.craft.core.ModDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

/**
 * 农场区域保护：
 * - 非农场区域（公共区域）不可放置/破坏方块
 * - 别人的农场：无权限(0)不可进入，仅访问权限(1)不可修改方块，完全权限(2)可操作
 * - 自己的农场：完全权限
 * - 创造模式不受限
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
        // 温室外观区域不可破坏
        if (com.stardew.craft.greenhouse.GreenhouseManager.isInGreenhouseExterior(level, event.getPos())) {
            event.setCanceled(true);
            return;
        }
        // 温室内部区域可自由操作
        if (com.stardew.craft.greenhouse.GreenhouseManager.isInGreenhouseInterior(level, event.getPos())) {
            return;
        }
        if (!canModifyAt(player, event.getPos())) {
            event.setCanceled(true);
            player.displayClientMessage(
                    Component.translatable("stardewcraft.farm.build_farm_only"), true);
        }
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
        // 方块替换（去皮、除锈、锄地等工具交互）→ 也需要权限检查
        // 但远古斑点允许在任何区域被锄（挖掘后变耕地，第二天复原）
        BlockPos pos = event.getPos();
        if (!event.getBlockSnapshot().getState().isAir()) {
            if (event.getBlockSnapshot().getState().is(ModBlocks.ARTIFACT_SPOT_DIRT.get())) {
                // 远古斑点：放行，不受区域保护
                return;
            }
            if (!canModifyAt(player, pos)) {
                // 还原为原来的方块
                event.getBlockSnapshot().restore();
                player.displayClientMessage(
                        Component.translatable("stardewcraft.farm.build_farm_only"), true);
            }
            return;
        }
        // 温室外观区域不可放置
        if (com.stardew.craft.greenhouse.GreenhouseManager.isInGreenhouseExterior(level, pos)) {
            level.destroyBlock(pos, true, player);
            return;
        }
        // 温室内部区域可自由操作
        if (com.stardew.craft.greenhouse.GreenhouseManager.isInGreenhouseInterior(level, pos)) {
            return;
        }
        if (!canModifyAt(player, pos)) {
            level.destroyBlock(pos, true, player);
            player.displayClientMessage(
                    Component.translatable("stardewcraft.farm.build_farm_only"), true);
        }
    }

    /**
     * 判断玩家是否可以在指定位置修改方块。
     * - 非农场区域 → 不可以
     * - 旧公共农场区域 → 可以（保留向后兼容）
     * - 自己的实例化农场 → 可以
     * - 别人的实例化农场 → 需要 PERM_FULL(2)
     *
     * 可被外部调用（镰刀、作物交互、工具等自定义逻辑需要统一权限检查）。
     */
    public static boolean canModifyAt(ServerPlayer player, BlockPos pos) {
        // 查找该位置属于哪个农场
        java.util.UUID ownerUUID = FarmAreaResolver.getOwnerAt(pos);
        if (ownerUUID == null) return false; // 不在任何农场内

        // 自己的农场
        if (ownerUUID.equals(player.getUUID())) return true;

        // 别人的农场：检查权限
        return com.stardew.craft.farm.FarmPermissionManager.get()
                .canModify(ownerUUID, player.getUUID());
    }

    /**
     * 判断玩家是否在别人的受保护农场上（没有 PERM_FULL 权限）。
     * 与 canModifyAt 的区别：非农场区域（城镇等）返回 false（允许交互），
     * 而 canModifyAt 对非农场区域返回 false（禁止修改方块）。
     */
    public static boolean isOnProtectedFarm(ServerPlayer player, BlockPos pos) {
        java.util.UUID ownerUUID = FarmAreaResolver.getOwnerAt(pos);
        if (ownerUUID == null) return false; // 不在任何农场内 → 非受保护区域
        if (ownerUUID.equals(player.getUUID())) return false; // 自己的农场

        return !com.stardew.craft.farm.FarmPermissionManager.get()
                .canModify(ownerUUID, player.getUUID());
    }

    // ═══════════════════════════════════════════════════════════
    // 右键交互保护：阻止在别人农场上使用机器、箱子、收割作物等
    // ═══════════════════════════════════════════════════════════

    /**
     * 右键方块：阻止在别人农场上进行任何方块交互。
     * 覆盖所有 useWithoutItem / useItemOn / item.useOn 的调用。
     */
    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        if (player.level().dimension() != ModDimensions.STARDEW_VALLEY) {
            return;
        }
        if (player.isCreative()) {
            return;
        }
        // 温室内部允许交互
        if (event.getLevel() instanceof ServerLevel sl
                && com.stardew.craft.greenhouse.GreenhouseManager.isInGreenhouseInterior(sl, event.getPos())) {
            return;
        }
        // 别人的农场禁止右键交互（公共区域允许：开门、献祭、NPC交互等）
        if (isOnProtectedFarm(player, event.getPos())) {
            event.setCanceled(true);
            event.setCancellationResult(net.minecraft.world.InteractionResult.CONSUME);
            player.displayClientMessage(
                    Component.translatable("stardewcraft.farm.build_farm_only"), true);
        }
    }
}
