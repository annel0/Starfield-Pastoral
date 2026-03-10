package com.stardew.craft.tooltip;

import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

/**
 * Client-only tooltip payload: shows fishing rod bait/tackle slots.
 */
public record FishingRodSlotsTooltipComponent(
		boolean showBait,
		int tackleSlots,
		ItemStack bait,
		ItemStack tackle1,
		ItemStack tackle2
) implements TooltipComponent {
	public FishingRodSlotsTooltipComponent {
		tackleSlots = Math.max(0, Math.min(2, tackleSlots));
		bait = bait == null ? ItemStack.EMPTY : bait;
		tackle1 = tackle1 == null ? ItemStack.EMPTY : tackle1;
		tackle2 = tackle2 == null ? ItemStack.EMPTY : tackle2;
	}
}
