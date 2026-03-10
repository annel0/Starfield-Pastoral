package com.stardew.craft.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface IStardewItem {
    /**
     * Get the item type key (e.g. "stardewcraft.type.seed")
     */
    String getItemTypeKey();

    /**
     * Get the sell price based on the stack (quality aware)
     * If returns -1, it means not sellable.
     */
    default int getSellPrice(ItemStack stack) {
        return -1;
    }

    /**
     * Is this item food?
     */
    default boolean isFood() {
        return false;
    }

    /**
     * Energy restoration amount
     */
    default int getEnergy(ItemStack stack) {
        return 0;
    }

    /**
     * Health restoration amount
     */
    default int getHealth(ItemStack stack) {
        return 0;
    }

    /**
     * 额外的“吃完后”Buff 提示行。
     * 返回空列表表示无额外 Buff（仅显示能量/生命恢复）。
     */
    default List<Component> getAfterEatTooltipLines(ItemStack stack) {
        return List.of();
    }
}
