package com.stardew.craft.compat;

import com.stardew.craft.item.equipment.StardewBootsItem;
import com.stardew.craft.item.equipment.StardewRingItem;
import net.minecraft.world.item.Item;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

/**
 * Curios mod 可选兼容层。
 * 仅在 Curios 安装时由 {@link CuriosCompatBridge} 调用，
 * 避免 Curios 不存在时触发 ClassNotFoundError。
 */
public final class CuriosCompat {

    private CuriosCompat() {}

    /**
     * 将指定的戒指/靴子物品注册为 Curios 兼容物品。
     * 戒指 → "ring" 槽位，靴子 → "feet" 槽位。
     */
    public static void registerCurioItem(Item item) {
        if (item instanceof StardewRingItem) {
            CuriosApi.registerCurio(item, new RingCurio());
        } else if (item instanceof StardewBootsItem) {
            CuriosApi.registerCurio(item, new BootsCurio());
        }
    }

    /**
     * 戒指 → 只能放入 "ring" 槽
     */
    private static class RingCurio implements ICurioItem {
        @Override
        public boolean canEquip(SlotContext slotContext, net.minecraft.world.item.ItemStack stack) {
            return "ring".equals(slotContext.identifier());
        }
    }

    /**
     * 靴子 → 只能放入 "feet" 槽
     */
    private static class BootsCurio implements ICurioItem {
        @Override
        public boolean canEquip(SlotContext slotContext, net.minecraft.world.item.ItemStack stack) {
            return "feet".equals(slotContext.identifier());
        }
    }
}
