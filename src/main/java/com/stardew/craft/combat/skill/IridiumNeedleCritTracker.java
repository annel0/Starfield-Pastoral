package com.stardew.craft.combat.skill;

import com.stardew.craft.combat.network.IridiumNeedleCritPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Objects;

public final class IridiumNeedleCritTracker {

    private static final int MAX_STACKS = 3;
    private static final Map<UUID, Integer> STACKS = new HashMap<>();

    private IridiumNeedleCritTracker() {}

    public static int getStacks(ServerPlayer player) {
        if (player == null) {
            return 0;
        }
        return STACKS.getOrDefault(player.getUUID(), 0);
    }

    public static boolean shouldGuaranteeCrit(ServerPlayer player) {
        return getStacks(player) >= (MAX_STACKS - 1);
    }

    public static void recordHit(ServerPlayer player) {
        if (player == null) {
            return;
        }
        int current = getStacks(player);
        int next = (current + 1) % MAX_STACKS;
        if (next == 0) {
            STACKS.remove(player.getUUID());
        } else {
            STACKS.put(player.getUUID(), next);
        }
        sync(player, next);
    }

    public static void clear(ServerPlayer player) {
        if (player == null) {
            return;
        }
        STACKS.remove(player.getUUID());
        sync(player, 0);
    }

    private static void sync(ServerPlayer player, int stacks) {
        ServerPlayer target = Objects.requireNonNull(player, "player");
        PacketDistributor.sendToPlayer(target, new IridiumNeedleCritPayload(stacks));
    }
}
