package com.stardew.craft.item;

import com.stardew.craft.block.tree.WildTreeSaplingBlock;
import com.stardew.craft.manager.TreeGrowthManager;
import com.stardew.craft.tree.WildTrees;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/** 树肥 - 对齐 SDV Tree Fertilizer：右键未成熟野树苗后提高每日生长概率，不会立即催熟。 */
public class TreeFertilizerItem extends SimpleStardewItem {

	public TreeFertilizerItem(int sellPrice, Properties properties) {
		super("stardewcraft.type.fertilizer", sellPrice, properties);
	}

	@SuppressWarnings("null")
	@Override
	public InteractionResult useOn(@SuppressWarnings("null") UseOnContext context) {
		Level level = context.getLevel();
		BlockPos pos = context.getClickedPos();
		@SuppressWarnings("null")
		BlockState state = level.getBlockState(pos);

		if (!(state.getBlock() instanceof WildTreeSaplingBlock)) {
			if (!level.isClientSide && WildTrees.findByAnyPart(state) != null && context.getPlayer() != null) {
				context.getPlayer().displayClientMessage(Component.translatable("stardewcraft.tree_fertilizer.mature"), true);
				return InteractionResult.CONSUME;
			}
			return InteractionResult.PASS;
		}

		if (level.isClientSide) {
			return InteractionResult.SUCCESS;
		}

		ServerLevel serverLevel = (ServerLevel) level;
		TreeGrowthManager manager = TreeGrowthManager.get(serverLevel);
		boolean alreadyFertilized = manager.isFertilized(serverLevel, pos);
		boolean fertilized = manager.fertilize(serverLevel, pos);

		if (fertilized) {
			if (context.getPlayer() != null && !context.getPlayer().isCreative()) {
				context.getItemInHand().shrink(1);
			}
			level.playSound(null, pos, SoundEvents.BONE_MEAL_USE, SoundSource.BLOCKS, 1.0f, 1.0f);
			serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER,
					pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
					10, 0.3, 0.3, 0.3, 0.0);
			return InteractionResult.CONSUME;
		}

		if (context.getPlayer() != null) {
			context.getPlayer().displayClientMessage(Component.translatable(
					alreadyFertilized
							? "stardewcraft.tree_fertilizer.already"
							: "stardewcraft.tree_fertilizer.cannot"
			), true);
		}
		return InteractionResult.CONSUME;
	}
}
