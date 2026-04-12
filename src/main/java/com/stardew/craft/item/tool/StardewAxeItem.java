package com.stardew.craft.item.tool;

import com.stardew.craft.item.IStardewItem;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;

@SuppressWarnings("null")
public class StardewAxeItem extends AxeItem implements IStardewItem {

	// SDV 工具没有耐久
	@Override
	public boolean isDamageable(ItemStack stack) { return false; }
	@Override
	public int getMaxDamage(ItemStack stack) { return 0; }
	@Override
	public void setDamage(ItemStack stack, int damage) { /* no-op */ }

	public enum Tier {
		STARTER(0),
		COPPER(1),
		STEEL(2),
		GOLD(3),
		IRIDIUM(4);

		private final int level;

		Tier(int level) {
			this.level = level;
		}

		public int getLevel() {
			return level;
		}
	}

	private final Tier tier;

	public StardewAxeItem(Tier tier, Properties properties) {
		super(new IndestructibleTier(toVanillaTier(tier)), properties.stacksTo(1).setNoRepair());
		this.tier = tier;
	}

	private static net.minecraft.world.item.Tier toVanillaTier(Tier tier) {
		// Only affects vanilla/modded blocks that are mineable with an axe.
		// Our wild tree chopping speed is handled by WildTreeChopEvents.
		return switch (tier) {
			case STARTER -> Tiers.STONE;
			case COPPER, STEEL -> Tiers.IRON;
			case GOLD -> Tiers.DIAMOND;
			case IRIDIUM -> Tiers.NETHERITE;
		};
	}

	@Override
	public boolean mineBlock(@SuppressWarnings("null") ItemStack stack, @SuppressWarnings("null") Level level, @SuppressWarnings("null") BlockState state, @SuppressWarnings("null") BlockPos pos, @SuppressWarnings("null") LivingEntity entityLiving) {
		return true;
	}

	public Tier getStardewTier() {
		return tier;
	}

	public int getTierLevel() {
		return tier.getLevel();
	}

	@Override
	public boolean canPerformAction(@SuppressWarnings("null") ItemStack stack, @SuppressWarnings("null") ItemAbility ability) {
		// 关键：让方块/系统能识别这是"斧头能力"（包括砍/去皮/刮蜡等）。
		return ItemAbilities.DEFAULT_AXE_ACTIONS.contains(ability);
	}

	@Override
	public boolean hurtEnemy(@SuppressWarnings("null") ItemStack stack, @SuppressWarnings("null") LivingEntity target, @SuppressWarnings("null") LivingEntity attacker) {
		return true;
	}

	@Override
	public InteractionResult useOn(@SuppressWarnings("null") UseOnContext context) {
		// 保留斧头功能（去皮/刮蜡等）但不允许耗久
		ItemStack stack = context.getItemInHand();
		int before = stack.getDamageValue();
		InteractionResult result = super.useOn(context);
		if (!stack.isEmpty() && stack.getDamageValue() != before) {
			stack.setDamageValue(before);
		}
		return result;
	}

	@Override
	public String getItemTypeKey() {
		return "stardewcraft.tool.axe";
	}

	@Override
	public int getSellPrice(net.minecraft.world.item.ItemStack stack) {
		// 星露谷工具不可出售
		return -1;
	}
}
