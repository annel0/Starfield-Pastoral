package com.stardew.craft.combat.equipment;

import com.stardew.craft.item.equipment.StardewBootsItem;
import com.stardew.craft.item.equipment.CombinedRingData;
import com.stardew.craft.item.equipment.CombinedRingItem;
import com.stardew.craft.item.equipment.StardewRingItem;
import com.stardew.craft.player.PlayerDataManager;
import com.stardew.craft.player.PlayerStardewData;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Resolves the merged EquipmentStats from a player's currently equipped rings and boots.
 */
public final class EquipmentResolver {

    private EquipmentResolver() {}

    /**
     * Get merged EquipmentStats from a server player's equipped rings and boots.
     * Returns empty stats if nothing is equipped.
     */
    public static EquipmentStats getMergedStats(ServerPlayer player) {
        PlayerStardewData data = PlayerDataManager.getPlayerData(player);
        EquipmentStats.Builder builder = EquipmentStats.builder();

        resolveRing(data.getEquippedLeftRing(), builder);
        resolveRing(data.getEquippedRightRing(), builder);
        resolveBoots(data.getEquippedBoots(), builder);

        // Curios 可选兼容：合并 Curios 槽位中的装备属性
        if (com.stardew.craft.compat.CuriosCompatBridge.isCuriosLoaded()) {
            com.stardew.craft.compat.CuriosEquipmentReader.mergeFromCurios(player, builder);
        }

        return builder.build();
    }

    private static void resolveRing(String itemId, EquipmentStats.Builder builder) {
        if (itemId == null || itemId.isEmpty()) return;
        if (CombinedRingData.isEncodedEquipmentSlot(itemId)) {
            ItemStack stack = CombinedRingData.stackFromEquipmentSlot(itemId);
            if (stack.getItem() instanceof CombinedRingItem ring) {
                builder.merge(ring.getEquipmentStats(stack));
            }
            return;
        }
        ResourceLocation rl = ResourceLocation.tryParse(itemId);
        if (rl == null) return;
        Item item = BuiltInRegistries.ITEM.get(rl);
        if (item instanceof StardewRingItem ring) {
            builder.merge(ring.getEquipmentStats());
        }
    }

    private static void resolveBoots(String itemId, EquipmentStats.Builder builder) {
        if (itemId == null || itemId.isEmpty()) return;
        ResourceLocation rl = ResourceLocation.tryParse(itemId);
        if (rl == null) return;
        Item item = BuiltInRegistries.ITEM.get(rl);
        if (item instanceof StardewBootsItem boots) {
            builder.merge(boots.getEquipmentStats());
        }
    }
}
