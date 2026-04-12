package com.stardew.craft.combat.skill;

import com.stardew.craft.combat.network.CrystalDaggerLayerPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class CrystalDaggerLayerTracker {

    public static final int MAX_STACKS = 4;
    public static final int DURATION_TICKS = 120;

    private static final Map<UUID, State> STATES = new HashMap<>();

    private record State(int stacks, long endTick, long readyTick) {}

    private CrystalDaggerLayerTracker() {}

    public static int getStacks(ServerPlayer player, long nowTick) {
        if (player == null) {
            return 0;
        }
        State state = STATES.get(player.getUUID());
        if (state == null) {
            return 0;
        }
        if (nowTick > state.endTick) {
            clearAndSync(player);
            return 0;
        }
        return state.stacks;
    }

    public static void addStack(ServerPlayer player, long nowTick) {
        if (player == null) {
            return;
        }
        State state = STATES.get(player.getUUID());
        int stacks = 0;
        long endTick = 0L;
        long readyTick = 0L;

        if (state != null && nowTick <= state.endTick) {
            stacks = state.stacks;
            endTick = state.endTick;
            readyTick = state.readyTick;
        }

        int nextStacks = Mth.clamp(stacks + 1, 0, MAX_STACKS);
        boolean gained = nextStacks > stacks;
        endTick = nowTick + DURATION_TICKS;
        if (nextStacks >= MAX_STACKS && stacks < MAX_STACKS) {
            readyTick = nowTick + 1;
        }

        STATES.put(player.getUUID(), new State(nextStacks, endTick, readyTick));
        sync(player, nextStacks, (int) Math.max(0, endTick - nowTick), gained);
    }

    public static boolean shouldBurst(ServerPlayer player, long nowTick) {
        if (player == null) {
            return false;
        }
        State state = STATES.get(player.getUUID());
        if (state == null) {
            return false;
        }
        if (nowTick > state.endTick) {
            clearAndSync(player);
            return false;
        }
        return state.stacks >= MAX_STACKS && nowTick >= state.readyTick;
    }

    public static void consumeBurst(ServerPlayer player) {
        if (player == null) {
            return;
        }
        clearAndSync(player);
    }

    public static void tick(ServerPlayer player, long nowTick) {
        if (player == null) {
            return;
        }
        State state = STATES.get(player.getUUID());
        if (state == null) {
            return;
        }
        if (nowTick > state.endTick) {
            clearAndSync(player);
        }
    }

    private static void clearAndSync(ServerPlayer player) {
        STATES.remove(player.getUUID());
        sync(player, 0, 0, false);
    }

    private static void sync(ServerPlayer player, int stacks, int durationTicks, boolean playChime) {
        ServerPlayer target = Objects.requireNonNull(player, "player");
        PacketDistributor.sendToPlayer(target, new CrystalDaggerLayerPayload(stacks, durationTicks, playChime));
    }

    /** Clean up state when a player logs out to prevent memory leaks. */
    public static void removePlayer(UUID playerId) {
        STATES.remove(playerId);
    }
}
