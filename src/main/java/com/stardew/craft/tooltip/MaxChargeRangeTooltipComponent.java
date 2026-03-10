package com.stardew.craft.tooltip;

import net.minecraft.world.inventory.tooltip.TooltipComponent;

public record MaxChargeRangeTooltipComponent(int rows, int cols) implements TooltipComponent {
    public MaxChargeRangeTooltipComponent {
        rows = Math.max(1, rows);
        cols = Math.max(1, cols);
    }
}
