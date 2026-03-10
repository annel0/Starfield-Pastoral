package com.stardew.craft.event;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.block.crop.StardewCropBlock;
import com.stardew.craft.block.nature.PastureGrassBlock;
import com.stardew.craft.block.nature.WildWeedsBlock;
import com.stardew.craft.item.tool.ScytheItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@SuppressWarnings("removal")
@EventBusSubscriber(modid = StardewCraft.MODID, bus = EventBusSubscriber.Bus.GAME)
public final class ScytheHarvestEvents {
	// 收割几何：前方扇形（按玩家视线），避免“必须指着作物”以及“扫到身后”。
	// 这里用 XZ 平面计算（高度用 3 个平面兜底，适配台阶/坡地）。
	private static final double SWING_RADIUS_BLOCKS = 2.35;
	private static final double COS_HALF_ANGLE = Math.cos(Math.toRadians(70.0));

	@SubscribeEvent
	public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
		var player = event.getEntity();
		if (!player.level().isClientSide) {
			return;
		}
		if (!(player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof ScytheItem)) {
			return;
		}

		// 关键：在客户端直接拦截挖掘开始（尤其是创造模式的瞬间破坏）。
		event.setCanceled(true);
	}

	@SubscribeEvent
	public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
		if (!(event.getEntity() instanceof ServerPlayer player)) {
			return;
		}
		if (!(player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof ScytheItem)) {
			return;
		}
		// 进一步确保不会进入挖掘进度。
		event.setNewSpeed(0.0F);
	}

	@SubscribeEvent
	public static void onBlockBreak(BlockEvent.BreakEvent event) {
		if (!(event.getPlayer() instanceof ServerPlayer player)) {
			return;
		}
		if (player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof ScytheItem) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void onAttackEntity(AttackEntityEvent event) {
		if (!(event.getEntity() instanceof ServerPlayer player)) {
			return;
		}
		// 镰刀不是战斗武器：禁用左键攻击实体。
		if (player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof ScytheItem) {
			event.setCanceled(true);
		}
	}

	public static boolean harvestSwing(ServerLevel level, ServerPlayer player, ScytheItem scythe) {
		boolean didSomething = false;
		BlockPos origin = player.blockPosition();
		Vec3 look = player.getLookAngle();
		double lookX = look.x;
		double lookZ = look.z;
		double lookLen = Math.sqrt(lookX * lookX + lookZ * lookZ);
		if (lookLen < 1.0e-6) {
			Direction dir = player.getDirection();
			lookX = dir.getStepX();
			lookZ = dir.getStepZ();
			lookLen = Math.sqrt(lookX * lookX + lookZ * lookZ);
		}
		lookX /= lookLen;
		lookZ /= lookLen;

		int r = (int) Math.ceil(SWING_RADIUS_BLOCKS);
		for (int dy = -1; dy <= 1; dy++) {
			int y = origin.getY() + dy;
			for (int dx = -r; dx <= r; dx++) {
				for (int dz = -r; dz <= r; dz++) {
					BlockPos pos = new BlockPos(origin.getX() + dx, y, origin.getZ() + dz);
					if (pos.equals(origin)) {
						continue;
					}

					double vx = (pos.getX() + 0.5) - player.getX();
					double vz = (pos.getZ() + 0.5) - player.getZ();
					double dist = Math.sqrt(vx * vx + vz * vz);
					if (dist > SWING_RADIUS_BLOCKS || dist < 1.0e-6) {
						continue;
					}

					double dot = (vx / dist) * lookX + (vz / dist) * lookZ;
					if (dot <= 0.0) {
						continue;
					}
					if (dot < COS_HALF_ANGLE) {
						continue;
					}

					if (tryHarvestAt(level, player, scythe, pos)) {
						didSomething = true;
					}
				}
			}
		}

		return didSomething;
	}

	@SuppressWarnings("null")
	private static boolean tryHarvestAt(ServerLevel level, ServerPlayer player, ScytheItem scythe, BlockPos pos) {
		@SuppressWarnings("null")
		BlockState state = level.getBlockState(pos);
		if (state.isAir()) {
			return false;
		}

		if (state.getBlock() instanceof StardewCropBlock crop) {
			return crop.tryHarvestByTool(level, pos, state, player);
		}

		if (state.getBlock() instanceof WildWeedsBlock) {
			return WildWeedsBlock.cutWithScythe(level, pos, player);
		}

		if (state.getBlock() instanceof PastureGrassBlock) {
			return PastureGrassBlock.cutWithScythe(level, pos, player, scythe);
		}

		// 原版差异：铱镰刀（66）能更广泛地“用镰刀收割作物”。
		if (scythe.getTier() != ScytheItem.Tier.IRIDIUM) {
			return false;
		}

		// 兼容原版/其他模组作物：优先用 vanilla 的 crops tag，其次兼容继承 CropBlock 的实现。
		if (!(state.is(BlockTags.CROPS) || state.getBlock() instanceof CropBlock)) {
			return false;
		}

		// 只破坏作物类方块；不会破坏普通方块。
		return level.destroyBlock(pos, true, player);
	}
}
