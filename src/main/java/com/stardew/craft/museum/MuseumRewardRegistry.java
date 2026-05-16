package com.stardew.craft.museum;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.item.IStardewItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.*;

/**
 * Registry of museum donation milestone rewards.
 * SDV parity: rewards given by Gunther when donation count thresholds are met.
 */
@SuppressWarnings("null")
public final class MuseumRewardRegistry {
    public static final String RUSTY_KEY_REWARD_ID = "museum60";

    /**
     * @param id          unique reward id (used as claimed-flag)
     * @param type        condition type
     * @param threshold   required count (-1 = all)
     * @param requiredIds specific item ids required (for SPECIFIC_ITEMS type)
     * @param rewardItems list of (itemId, count) pairs to give
     * @param grantRecipe if non-null, recipe id to unlock on claim
     */
    public record MuseumReward(
        String id,
        ConditionType type,
        int threshold,
        List<String> requiredIds,
        List<RewardItem> rewardItems,
        String grantRecipe
    ) {}

    public record RewardItem(String itemId, int count) {}

    public enum ConditionType {
        /** Total donated items (any type) */
        TOTAL_COUNT,
        /** Only minerals (stardewcraft.type.mineral) */
        MINERAL_COUNT,
        /** Only artifacts (stardewcraft.type.artifact) */
        ARTIFACT_COUNT,
        /** Specific items must all be donated */
        SPECIFIC_ITEMS
    }

    private static final List<MuseumReward> REWARDS = new ArrayList<>();

    static {
        // ── Total donation milestones ──
        total("museum5",  5,  "stardewcraft:cauliflower_seeds", 9);
        total("museum10", 10, "stardewcraft:melon_seeds", 9);
        total("museum15", 15, "stardewcraft:starfruit_seeds", 1);
        total("museum20", 20, "stardewcraft:quality_sprinkler", 1);
        total("museum25", 25, "stardewcraft:omni_geode", 5);
        total("museum30", 30, "stardewcraft:gold_bar", 5);
        total("museum35", 35, "stardewcraft:pumpkin_seeds", 9);
        total("museum40", 40, "stardewcraft:iridium_sprinkler", 1);
        total("museum50", 50, "stardewcraft:diamond", 3);
        totalNoItem(RUSTY_KEY_REWARD_ID, 60);
        total("museum70", 70, "stardewcraft:triple_shot_espresso", 3);
        total("museum80", 80, "stardewcraft:warp_totem_farm", 5);
        total("museum90", 90, "stardewcraft:magic_rock_candy", 1);

        // ── Mineral milestones ──
        mineral("mineral11", 11, "stardewcraft:omni_geode", 3);
        mineral("mineral21", 21, "stardewcraft:gold_bar", 3);
        mineral("mineral31", 31, "stardewcraft:iridium_bar", 2);
        mineral("mineral40", 40, "stardewcraft:diamond", 1);
        mineral("mineral50", 50, "stardewcraft:crystalarium", 1);

        // ── Artifact milestones ──
        artifact("arch15", 15, "stardewcraft:bone_fragment", 50);
        artifact("arch20", 20, "stardewcraft:mega_bomb", 3);

        // ── Specific items: Dwarf Scrolls → Dwarvish Translation Guide ──
        REWARDS.add(new MuseumReward(
            "dwarf_scrolls",
            ConditionType.SPECIFIC_ITEMS,
            4,
            List.of("stardewcraft:dwarf_scroll_i", "stardewcraft:dwarf_scroll_ii",
                     "stardewcraft:dwarf_scroll_iii", "stardewcraft:dwarf_scroll_iv"),
            List.of(new RewardItem("stardewcraft:dwarvish_translation_guide", 1)),
            null
        ));

        // ── Specific items: Ancient Seed → plantable Ancient Fruit Seeds + recipe ──
        REWARDS.add(new MuseumReward(
            "ancient_seed_reward",
            ConditionType.SPECIFIC_ITEMS,
            1,
            List.of("stardewcraft:ancient_seed"),
            List.of(new RewardItem("stardewcraft:ancient_fruit_seeds", 1)),
            "ancient_fruit_seeds"
        ));
    }

    private static void total(String id, int threshold, String itemId, int count) {
        REWARDS.add(new MuseumReward(id, ConditionType.TOTAL_COUNT, threshold,
            List.of(), List.of(new RewardItem(itemId, count)), null));
    }

    private static void totalNoItem(String id, int threshold) {
        REWARDS.add(new MuseumReward(id, ConditionType.TOTAL_COUNT, threshold,
            List.of(), List.of(), null));
    }

    private static void mineral(String id, int threshold, String itemId, int count) {
        REWARDS.add(new MuseumReward(id, ConditionType.MINERAL_COUNT, threshold,
            List.of(), List.of(new RewardItem(itemId, count)), null));
    }

    private static void artifact(String id, int threshold, String itemId, int count) {
        REWARDS.add(new MuseumReward(id, ConditionType.ARTIFACT_COUNT, threshold,
            List.of(), List.of(new RewardItem(itemId, count)), null));
    }

    public static List<MuseumReward> getAllRewards() {
        return Collections.unmodifiableList(REWARDS);
    }

    /**
     * Get all unclaimed rewards that the player now qualifies for.
     */
    public static List<MuseumReward> getClaimableRewards(MuseumDonationData data, java.util.UUID playerId, Set<String> claimedIds) {
        Set<String> donated = data.getDonatedItems(playerId);
        int totalCount = donated.size();

        // Count by type
        int mineralCount = 0;
        int artifactCount = 0;
        for (String itemId : donated) {
            Item item = resolveItem(itemId);
            if (item instanceof IStardewItem si) {
                String typeKey = si.getItemTypeKey();
                if ("stardewcraft.type.mineral".equals(typeKey)) mineralCount++;
                else if ("stardewcraft.type.artifact".equals(typeKey)) artifactCount++;
            }
        }

        List<MuseumReward> result = new ArrayList<>();
        for (MuseumReward reward : REWARDS) {
            if (claimedIds.contains(reward.id())) continue;
            if (meetsCondition(reward, totalCount, mineralCount, artifactCount, donated)) {
                result.add(reward);
            }
        }
        return result;
    }

    private static boolean meetsCondition(MuseumReward reward, int total, int minerals, int artifacts, Set<String> donated) {
        return switch (reward.type()) {
            case TOTAL_COUNT -> total >= reward.threshold();
            case MINERAL_COUNT -> minerals >= reward.threshold();
            case ARTIFACT_COUNT -> artifacts >= reward.threshold();
            case SPECIFIC_ITEMS -> donated.containsAll(reward.requiredIds());
        };
    }

    public static Item resolveItem(String itemId) {
        ResourceLocation rl = ResourceLocation.parse(itemId);
        Item item = BuiltInRegistries.ITEM.get(rl);
        return item == Items.AIR ? null : item;
    }

    /**
     * Create ItemStacks for a reward's items.
     */
    public static List<ItemStack> createRewardStacks(MuseumReward reward) {
        List<ItemStack> stacks = new ArrayList<>();
        for (RewardItem ri : reward.rewardItems()) {
            Item item = resolveItem(ri.itemId());
            if (item != null) {
                stacks.add(new ItemStack(item, ri.count()));
            } else {
                StardewCraft.LOGGER.warn("[MuseumReward] Item not found: {}", ri.itemId());
            }
        }
        return stacks;
    }

    private MuseumRewardRegistry() {}
}
