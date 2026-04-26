package com.stardew.craft.weather;

import com.stardew.craft.blockentity.LightningRodBlockEntity;
import com.stardew.craft.blockentity.registry.LightningRodRegistry;
import com.stardew.craft.core.ModDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * SDV parity: {@code Utility.performLightningUpdate(int time_of_day)}.
 * <p>Called once per 10 in-game minute tick on the server thread.
 * Iterates Stardew-weather dimensions; if thundering, rolls a 12.5%
 * chance and picks up to 2 lightning rods from the level's registry.
 * Empty rods are charged immediately if their chunk is loaded, or
 * queued in the registry's pending set so they charge the next time
 * their block entity ticks.</p>
 *
 * <p>Chunk-unload safe: charges are authoritative because they live
 * in persistent {@link LightningRodRegistry}; block entities only
 * drain the pending queue when they happen to be loaded, but the
 * battery itself is guaranteed to materialise once the chunk loads.</p>
 */
public final class LightningStrikeScheduler {

    /** SDV: 0.125 + luck. We drop the luck term (no per-player context at global tick). */
    private static final double STRIKE_CHANCE = 0.125;

    private LightningStrikeScheduler() {}

    public static void performTenMinuteUpdate(MinecraftServer server) {
        if (server == null) return;
        for (ServerLevel level : server.getAllLevels()) {
            if (!hasStardewWeather(level)) continue;
            if (!WeatherManager.isThundering(level)) continue;
            tick(level);
        }
    }

    private static boolean hasStardewWeather(ServerLevel level) {
        return level.dimension() == ModDimensions.STARDEW_VALLEY;
    }

    private static void tick(ServerLevel level) {
        LightningRodRegistry registry = LightningRodRegistry.get(level);
        if (registry.size() == 0) return;
        if (level.random.nextDouble() >= STRIKE_CHANCE) return;

        // SDV picks up to 2 rods per successful roll; the FIRST empty rod
        // gets the battery and the loop returns.
        List<BlockPos> snapshot = new ArrayList<>(registry.positions());
        int attempts = Math.min(2, snapshot.size());
        for (int i = 0; i < attempts; i++) {
            // Fisher-Yates partial pick using RandomSource.
            int swap = i + level.random.nextInt(snapshot.size() - i);
            BlockPos pos = snapshot.get(swap);
            snapshot.set(swap, snapshot.get(i));
            snapshot.set(i, pos);
            if (tryStrike(level, registry, pos)) return;
        }
    }

    /** @return true if this rod consumed the strike (busy rods do NOT consume it in SDV). */
    private static boolean tryStrike(ServerLevel level, LightningRodRegistry registry, BlockPos pos) {
        if (isChunkLoaded(level, pos)) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof LightningRodBlockEntity rod) {
                if (rod.isBusy()) return false;
                rod.startChargingFromStrike();
                return true;
            }
            // Block gone — drop stale registry entry.
            registry.remove(pos);
            return false;
        }
        // Chunk not loaded: we cannot inspect busy-state. Queue the charge; the
        // BE will pick it up when the chunk next ticks. If it's already busy we
        // silently ignore on drain. Count it as a consumed strike (SDV would
        // also only try two rods total per roll).
        registry.addPending(pos);
        return true;
    }

    private static boolean isChunkLoaded(ServerLevel level, BlockPos pos) {
        return level.getChunkSource().getChunkNow(pos.getX() >> 4, pos.getZ() >> 4) != null;
    }
}
