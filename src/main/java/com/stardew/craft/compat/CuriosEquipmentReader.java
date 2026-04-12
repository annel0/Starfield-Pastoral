package com.stardew.craft.compat;

import com.stardew.craft.combat.equipment.EquipmentStats;
import com.stardew.craft.item.equipment.StardewBootsItem;
import com.stardew.craft.item.equipment.StardewRingItem;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;

/**
 * 从 Curios 槽位读取装备属性（仅 Curios 存在时加载）。
 */
public final class CuriosEquipmentReader {

    private CuriosEquipmentReader() {}

    /**
     * 合并 Curios 槽位中所有 SDV 戒指/靴子的属性到 builder。
     */
    public static void mergeFromCurios(ServerPlayer player, EquipmentStats.Builder builder) {
        CuriosApi.getCuriosInventory(player).ifPresent(handler -> {
            handler.findCurios(stack ->
                    stack.getItem() instanceof StardewRingItem || stack.getItem() instanceof StardewBootsItem
            ).forEach(slotResult -> {
                ItemStack stack = slotResult.stack();
                if (stack.getItem() instanceof StardewRingItem ring) {
                    builder.merge(ring.getEquipmentStats());
                } else if (stack.getItem() instanceof StardewBootsItem boots) {
                    builder.merge(boots.getEquipmentStats());
                }
            });
        });
    }
}
