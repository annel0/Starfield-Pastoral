package com.stardew.craft.compat;

import com.stardew.craft.item.equipment.RingType;
import com.stardew.craft.item.equipment.StardewRingItem;
import net.minecraft.server.level.ServerPlayer;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.List;

/**
 * 从 Curios 槽位读取已装备的 RingType（仅 Curios 存在时加载）。
 */
public final class CuriosRingReader {

    private CuriosRingReader() {}

    /**
     * 将 Curios 槽位中的 SDV 戒指类型添加到列表。
     */
    public static void addRingTypesFromCurios(ServerPlayer player, List<RingType> result) {
        CuriosApi.getCuriosInventory(player).ifPresent(handler -> {
            handler.findCurios(stack -> stack.getItem() instanceof StardewRingItem).forEach(slotResult -> {
                if (slotResult.stack().getItem() instanceof StardewRingItem ring) {
                    result.add(ring.getRingType());
                }
            });
        });
    }
}
