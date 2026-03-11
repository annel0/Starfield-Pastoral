package com.stardew.craft.farming;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiPredicate;

/**
 * Stardew-like location rule entry for season checks.
 *
 * Vanilla uses GameLocation.SeedsIgnoreSeasonsHere() to bypass season limits
 * in places like greenhouse/island. This class provides an equivalent, with
 * pluggable predicates so new locations can opt-in without touching crop/seed code.
 */
public final class SeasonLocationRules {
    private static final List<BiPredicate<Level, BlockPos>> IGNORE_SEASON_RULES = new CopyOnWriteArrayList<>();

    private SeasonLocationRules() {
    }

    /**
     * Register a rule that returns true when seeds/crops should ignore seasons at a location.
     */
    public static void registerIgnoreSeasonsRule(BiPredicate<Level, BlockPos> rule) {
        if (rule != null) {
            IGNORE_SEASON_RULES.add(rule);
        }
    }

    /**
     * Equivalent concept to Stardew's SeedsIgnoreSeasonsHere().
     */
    public static boolean seedsIgnoreSeasonsHere(Level level, BlockPos pos) {
        if (level == null || pos == null) {
            return false;
        }

        for (BiPredicate<Level, BlockPos> rule : IGNORE_SEASON_RULES) {
            try {
                if (rule.test(level, pos)) {
                    return true;
                }
            } catch (Exception ignored) {
                // Keep evaluation robust even if one rule is faulty.
            }
        }

        return false;
    }

    /**
     * Unified seasonal gate for planting.
     */
    public static boolean isPlantingSeasonAllowed(Level level, BlockPos pos, int currentSeason, int... allowedSeasons) {
        if (seedsIgnoreSeasonsHere(level, pos)) {
            return true;
        }
        return containsSeason(currentSeason, allowedSeasons);
    }

    /**
     * Unified seasonal gate for crop growth/death checks.
     */
    public static boolean isCropSeasonAllowed(Level level, BlockPos pos, int currentSeason, int... allowedSeasons) {
        if (seedsIgnoreSeasonsHere(level, pos)) {
            return true;
        }
        return containsSeason(currentSeason, allowedSeasons);
    }

    private static boolean containsSeason(int currentSeason, int... allowedSeasons) {
        if (allowedSeasons == null || allowedSeasons.length == 0) {
            return false;
        }
        for (int season : allowedSeasons) {
            if (season == currentSeason) {
                return true;
            }
        }
        return false;
    }
}
