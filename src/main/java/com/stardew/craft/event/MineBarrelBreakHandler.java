package com.stardew.craft.event;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.block.ModBlocks;
import com.stardew.craft.core.ModMiningDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 矿井木桶武器打碎处理器
 *
 * SDV 原版中，武器挥击可以直接打碎木桶。
 * 实现方式：监听玩家攻击事件，攻击时扫描周围的木桶方块并打碎。
 * 触发条件：矿井维度 + 手持武器（SwordItem 或 IStardewWeapon）
 */
@EventBusSubscriber(modid = StardewCraft.MODID)
@SuppressWarnings("null")
public class MineBarrelBreakHandler {

    // 武器打碎木桶的范围（玩家前方）
    private static final double SWEEP_RANGE = 2.5;
    private static final double SWEEP_RANGE_SQR = SWEEP_RANGE * SWEEP_RANGE;
    private static final double SWEEP_CONE_DOT = 0.15;

    /**
     * 玩家攻击实体时，顺带检查附近的木桶
     */
    @SubscribeEvent
    public static void onAttackEntity(AttackEntityEvent event) {
        if (event.getEntity().level().isClientSide()) return;
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        ServerLevel level = (ServerLevel) player.level();
        if (level.dimension() != ModMiningDimensions.STARDEW_MINING) return;

        if (!isWeapon(player.getMainHandItem())) return;

        breakNearbyBarrels(level, player, null);
    }

    /**
     * 直接左键命中木桶时，武器应当立刻打碎木桶，而不是走普通挖掘计时。
     */
    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        if (event.getEntity().level().isClientSide()) return;
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        ServerLevel level = (ServerLevel) player.level();
        if (level.dimension() != ModMiningDimensions.STARDEW_MINING) return;
        if (!isWeapon(player.getMainHandItem())) return;

        BlockPos pos = event.getPos();
        if (!level.getBlockState(pos).is(ModBlocks.MINE_BARREL.get())) return;

        breakNearbyBarrels(level, player, pos.immutable());
        event.setCanceled(true);
    }

    /**
     * 也可通过左键空挥触发 — 使用 PlayerInteractEvent.LeftClickEmpty 在客户端
     * 但更简单的方式：在 attack(Entity) mixin 中也扫描木桶。
     * 这里只处理攻击实体时的连带效果。
     *
     * 对于主动打碎（对着木桶左键），Minecraft 会走正常的方块破坏流程，
     * MineBarrelBlock 的 strength(0.6F) 保证很快就能挖掉。
     */

    /**
     * 扫描玩家前方的木桶并打碎
     */
    public static void breakNearbyBarrels(ServerLevel level, ServerPlayer player, BlockPos forcedPos) {
        Vec3 look = player.getLookAngle();
        Vec3 eye = player.getEyePosition();

        // 扫描玩家前方 SWEEP_RANGE 范围
        AABB sweepBox = player.getBoundingBox().inflate(SWEEP_RANGE);

        List<BlockPos> targets = new ArrayList<>();

        BlockPos.MutableBlockPos mPos = new BlockPos.MutableBlockPos();
        int minX = (int) Math.floor(sweepBox.minX);
        int maxX = (int) Math.ceil(sweepBox.maxX);
        int minY = (int) Math.floor(sweepBox.minY);
        int maxY = (int) Math.ceil(sweepBox.maxY);
        int minZ = (int) Math.floor(sweepBox.minZ);
        int maxZ = (int) Math.ceil(sweepBox.maxZ);

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    mPos.set(x, y, z);
                    if (level.getBlockState(mPos).is(ModBlocks.MINE_BARREL.get())) {
                        BlockPos candidate = mPos.immutable();
                        if (shouldBreakBarrel(level, player, eye, look, candidate, forcedPos)) {
                            targets.add(candidate);
                        }
                    }
                }
            }
        }

        targets.sort(Comparator.comparingDouble(pos -> pos.distSqr(player.blockPosition())));
        for (BlockPos target : targets) {
            level.destroyBlock(target, false);
        }
    }

    private static boolean shouldBreakBarrel(ServerLevel level, ServerPlayer player, Vec3 eye, Vec3 look, BlockPos candidate, BlockPos forcedPos) {
        if (candidate == null) {
            return false;
        }
        if (forcedPos != null && candidate.equals(forcedPos)) {
            return true;
        }

        Vec3 center = Vec3.atCenterOf(candidate);
        Vec3 toBlock = center.subtract(eye);
        double distanceSqr = toBlock.lengthSqr();
        if (distanceSqr > SWEEP_RANGE_SQR) {
            return false;
        }
        if (distanceSqr < 1.0E-6) {
            return true;
        }

        double dot = toBlock.normalize().dot(look.normalize());
        if (dot < SWEEP_CONE_DOT) {
            return false;
        }

        return hasDirectSwingPath(level, eye, center, candidate, player);
    }

    private static boolean hasDirectSwingPath(ServerLevel level, Vec3 eye, Vec3 targetCenter, BlockPos targetPos,
                                               net.minecraft.world.entity.Entity entity) {
        if (entity == null) return true; // defensive: skip ray-check if entity gone
        BlockHitResult hit = level.clip(new ClipContext(
            eye,
            targetCenter,
            ClipContext.Block.COLLIDER,
            ClipContext.Fluid.NONE,
            entity
        ));
        if (hit.getType() == HitResult.Type.MISS) {
            return true;
        }
        return hit.getBlockPos().equals(targetPos);
    }

    private static boolean isWeapon(ItemStack stack) {
        if (stack.isEmpty()) return false;
        // 原版剑
        if (stack.getItem() instanceof SwordItem) return true;
        // 自定义 Stardew 武器
        if (stack.getItem() instanceof com.stardew.craft.item.weapon.IStardewWeapon) return true;
        return false;
    }
}
