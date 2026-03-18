package com.stardew.craft.network.overnight;

import com.stardew.craft.item.IStardewItem;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Collects per-player overnight settlement data during daytime and
 * turns it into one payload at next-morning transition.
 */
public final class OvernightSettlementTracker {
    private static final Map<UUID, PlayerLedger> LEDGER = new ConcurrentHashMap<>();

    private OvernightSettlementTracker() {
    }

    public static void recordShipping(ServerPlayer player, ItemStack stack, int pricePerItem) {
        if (player == null || stack.isEmpty() || pricePerItem <= 0) {
            return;
        }

        ItemStack copied = stack.copy();
        PlayerLedger ledger = LEDGER.computeIfAbsent(player.getUUID(), key -> new PlayerLedger());
        ledger.shippedItems.add(new OvernightSettlementPayload.ShippedItem(
            copied,
            classifyCategory(copied),
            pricePerItem
        ));
    }

    public static OvernightSettlementPayload consumePayload(ServerPlayer player) {
        if (player == null) {
            return new OvernightSettlementPayload(List.of(), List.of());
        }

        PlayerLedger ledger = LEDGER.remove(player.getUUID());
        if (ledger == null) {
            return new OvernightSettlementPayload(List.of(), List.of());
        }

        return new OvernightSettlementPayload(
            List.copyOf(ledger.shippedItems),
            List.of()
        );
    }

    private static int classifyCategory(ItemStack stack) {
        if (!(stack.getItem() instanceof IStardewItem stardewItem)) {
            return 4;
        }

        String typeKey = stardewItem.getItemTypeKey();
        if (typeKey == null || typeKey.isBlank()) {
            return 4;
        }

        String key = typeKey.toLowerCase(Locale.ROOT);
        if (key.contains("fish")) {
            return 2;
        }
        if (key.contains("mining") || key.contains("ore") || key.contains("gem") || key.contains("bar") || key.contains("mineral")) {
            return 3;
        }
        if (key.contains("forage") || key.contains("foraging")) {
            return 1;
        }
        if (key.contains("crop") || key.contains("animal_product") || key.contains("artisan") || key.contains("syrup")) {
            return 0;
        }
        return 4;
    }

    private static final class PlayerLedger {
        private final List<OvernightSettlementPayload.ShippedItem> shippedItems = new ArrayList<>();
    }
}