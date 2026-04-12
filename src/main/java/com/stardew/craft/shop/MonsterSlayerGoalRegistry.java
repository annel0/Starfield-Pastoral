package com.stardew.craft.shop;

import javax.annotation.Nullable;
import java.util.*;

/**
 * SDV MonsterSlayerQuests parity — 13 goals from Gil at the Adventurer's Guild.
 * Each goal tracks kills of specific monster types; upon reaching the required
 * count the player can claim a reward from Gil.
 *
 * Monster types are identified by entity tags (sd_mob_*) used by MineMonsterDropHandler.
 */
public final class MonsterSlayerGoalRegistry {

    public record SlayerGoal(
        String goalKey,           // e.g. "Slimes"
        String translationKey,    // e.g. "stardewcraft.slayer.goal.slimes"
        int requiredKills,
        @Nullable String rewardItemId, // null = no item reward (e.g. FlameSpirits → dialogue only)
        Set<String> monsterTags   // entity tags that count toward this goal
    ) {}

    private static final List<SlayerGoal> GOALS = new ArrayList<>();
    // monsterTag → goalKey lookup for fast kill routing
    private static final Map<String, String> TAG_TO_GOAL = new HashMap<>();

    static {
        // SDV source: Content/Data/MonsterSlayerQuests.json
        // Only goals for monsters that actually spawn in our mine (MineMonsterSpawnHandler).
        register("Slimes",       "stardewcraft.slayer.goal.slimes",        1000,
            "stardewcraft:slime_charmer_ring",  "sd_mob_slime");
        register("Shadows",      "stardewcraft.slayer.goal.shadows",        150,
            "stardewcraft:savage_ring",         "sd_mob_shadow");
        register("Bats",         "stardewcraft.slayer.goal.bats",           200,
            "stardewcraft:vampire_ring",        "sd_mob_bat");
        register("Skeletons",    "stardewcraft.slayer.goal.skeletons",       50,
            null,                               "sd_mob_skeleton"); // SDV: Skeleton Mask hat
        register("Insects",      "stardewcraft.slayer.goal.insects",         80,
            "stardewcraft:insect_head",         "sd_mob_fly", "sd_mob_grub");
        register("Duggy",        "stardewcraft.slayer.goal.duggies",         30,
            null,                               "sd_mob_duggy");    // SDV: Hard Hat
        register("DustSpirits",  "stardewcraft.slayer.goal.dust_sprites",   500,
            "stardewcraft:burglars_ring",       "sd_mob_dust_sprite");
        register("Crabs",        "stardewcraft.slayer.goal.crabs",           60,
            "stardewcraft:crabshell_ring",      "sd_mob_crab");
        register("Ghosts",       "stardewcraft.slayer.goal.ghosts",          30,
            null,                               "sd_mob_ghost");
        register("Golems",       "stardewcraft.slayer.goal.golems",          60,
            null,                               "sd_mob_golem");
        register("MetalHeads",   "stardewcraft.slayer.goal.metal_heads",     50,
            null,                               "sd_mob_metal_head");
        register("SquidKids",    "stardewcraft.slayer.goal.squid_kids",      30,
            "stardewcraft:napalm_ring",         "sd_mob_squid");
    }

    private static void register(String goalKey, String translationKey, int required,
                                 @Nullable String rewardItemId, String... tags) {
        Set<String> tagSet = Set.of(tags);
        GOALS.add(new SlayerGoal(goalKey, translationKey, required, rewardItemId, tagSet));
        for (String tag : tags) {
            TAG_TO_GOAL.put(tag, goalKey);
        }
    }

    /** Returns the goal key for a given monster entity tag, or null if no goal tracks it. */
    @Nullable
    public static String getGoalKeyForTag(String monsterTag) {
        return TAG_TO_GOAL.get(monsterTag);
    }

    /** Returns all registered goals (unmodifiable). */
    public static List<SlayerGoal> getAllGoals() {
        return Collections.unmodifiableList(GOALS);
    }

    /** Returns a specific goal by key, or null. */
    @Nullable
    public static SlayerGoal getGoal(String goalKey) {
        for (SlayerGoal g : GOALS) {
            if (g.goalKey().equals(goalKey)) return g;
        }
        return null;
    }

    private MonsterSlayerGoalRegistry() {}
}
