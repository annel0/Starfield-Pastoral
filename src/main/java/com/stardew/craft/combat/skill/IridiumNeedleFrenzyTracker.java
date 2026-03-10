package com.stardew.craft.combat.skill;

import com.stardew.craft.combat.network.IridiumNeedleFrenzyPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class IridiumNeedleFrenzyTracker {

    private static final Map<UUID, Long> ACTIVE = new HashMap<>();

    private IridiumNeedleFrenzyTracker() {}

    public static void start(ServerPlayer player, long nowTick, int durationTicks) {
        if (player == null || durationTicks <= 0) {
            return;
        }
        ACTIVE.put(player.getUUID(), nowTick + durationTicks);
        PacketDistributor.sendToPlayer(player, new IridiumNeedleFrenzyPayload(true, durationTicks));
    }

    public static boolean isActive(ServerPlayer player, long nowTick) {
        if (player == null) {
            return false;
        }
        Long endTick = ACTIVE.get(player.getUUID());
        if (endTick == null) {
            return false;
        }
        if (nowTick > endTick) {
            clear(player);
            return false;
        }
        return true;
    }

    public static void tick(ServerPlayer player, long nowTick) {
        if (player == null) {
            return;
        }
        Long endTick = ACTIVE.get(player.getUUID());
        if (endTick == null) {
            return;
        }
        if (nowTick > endTick) {
            clear(player);
        }
    }

    public static void clear(ServerPlayer player) {
        if (player == null) {
            return;
        }
        ACTIVE.remove(player.getUUID());
        PacketDistributor.sendToPlayer(player, new IridiumNeedleFrenzyPayload(false, 0));
    }
}
