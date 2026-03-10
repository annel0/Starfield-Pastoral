package com.stardew.craft.tooltip;

import net.minecraft.util.Mth;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

/**
 * Client-only tooltip payload: one row of fishing-rod attachment slots.
 *
 * <p>We intentionally split bait/tackle into separate tooltip components so the vanilla tooltip layout
 * (line spacing) matches other tools like the watering can.</p>
 */
public record FishingRodSlotRowTooltipComponent(
		RowType type,
		int sharedLabelWidth,
		int slotCount,
		ItemStack slot1,
		ItemStack slot2
) implements TooltipComponent {
	public enum RowType {
		BAIT,
		TACKLE
	}

	public FishingRodSlotRowTooltipComponent {
		type = type == null ? RowType.BAIT : type;
		sharedLabelWidth = Math.max(0, sharedLabelWidth);
		slotCount = Mth.clamp(slotCount, 0, 2);
		slot1 = slot1 == null ? ItemStack.EMPTY : slot1;
		slot2 = slot2 == null ? ItemStack.EMPTY : slot2;
	}
}
