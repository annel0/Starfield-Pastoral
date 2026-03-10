package com.stardew.craft.tooltip;

import net.minecraft.world.inventory.tooltip.TooltipComponent;

public record WaterAmountTooltipComponent(int water, int max) implements TooltipComponent {
}
