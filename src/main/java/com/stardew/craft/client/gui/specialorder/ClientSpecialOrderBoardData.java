package com.stardew.craft.client.gui.specialorder;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class ClientSpecialOrderBoardData {
    private static CompoundTag snapshot = new CompoundTag();
    private static final Set<String> viewedOrderIds = new HashSet<>();

    private ClientSpecialOrderBoardData() {
    }

    public static void replace(CompoundTag data) {
        snapshot = data == null ? new CompoundTag() : data.copy();
    }

    public static CompoundTag snapshot() {
        return snapshot.copy();
    }

    public static boolean hasUnclaimedAvailableOrders() {
        if (!snapshot.getBoolean("Unlocked")) {
            return false;
        }
        if (snapshot.getBoolean("AcceptedThisRefresh")) {
            return false;
        }
        ListTag available = snapshot.getList("Available", 10);
        if (available.isEmpty()) {
            return false;
        }
        ListTag active = snapshot.getList("Active", 10);
        for (int i = 0; i < active.size(); i++) {
            CompoundTag order = active.getCompound(i);
            if (order.getBoolean("Accepted") && !order.getBoolean("Failed")) {
                return false;
            }
        }
        return true;
    }

    public static List<SpecialOrderQuestView> activeQuestLogEntries() {
        List<SpecialOrderQuestView> out = new ArrayList<>();
        ListTag active = snapshot.getList("Active", 10);
        for (int i = active.size() - 1; i >= 0; i--) {
            CompoundTag order = active.getCompound(i);
            if (!order.getBoolean("Accepted") || order.getBoolean("Failed")) {
                continue;
            }
            out.add(new SpecialOrderQuestView(order));
        }
        return out;
    }

    public static boolean isViewed(String orderId) {
        return viewedOrderIds.contains(orderId);
    }

    public static void markViewed(String orderId) {
        if (orderId != null && !orderId.isBlank()) {
            viewedOrderIds.add(orderId);
        }
    }
}
