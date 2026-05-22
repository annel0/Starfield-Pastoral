package com.stardew.craft.tooltip;

import com.stardew.craft.book.BookDefinition;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

public record BookTooltipComponent(ItemStack stack, BookDefinition definition) implements TooltipComponent {
    public BookTooltipComponent {
        stack = stack.copy();
        stack.setCount(1);
    }
}