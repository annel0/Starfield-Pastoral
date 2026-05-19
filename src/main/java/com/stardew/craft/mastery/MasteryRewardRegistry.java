package com.stardew.craft.mastery;

import com.stardew.craft.item.ModItems;
import com.stardew.craft.player.SkillType;
import net.minecraft.world.item.ItemStack;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 5 个技能各自的精通奖励物品定义（SDV parity，按 MasteryTrackerMenu.cs:36-151 对齐）。
 *
 * 每条 RewardEntry 含：
 *  - itemStack 工厂（Supplier，避免静态初始化期 ModItems 未就绪）
 *  - name / desc 翻译键
 *  - statBonus：非物品奖励（例 trinket 槽）
 *
 * 非物品条目使用原版 MasteryTrackerMenu 的 plaque 图标展示，不发放物品。
 */
public final class MasteryRewardRegistry {
    private MasteryRewardRegistry() {}

    public enum StatBonus {
        NONE,
        TRINKET_SLOT
    }

    public enum RewardKind {
        ITEM,
        RECIPE,
        STAT
    }

    public record RewardEntry(
        Supplier<ItemStack> stack,
        String recipeId,
        String nameKey,
        String descKey,
        StatBonus statBonus,
        RewardKind kind
    ) {
        public static RewardEntry item(Supplier<ItemStack> stack, String nameKey, String descKey) {
            return new RewardEntry(stack, "", nameKey, descKey, StatBonus.NONE, RewardKind.ITEM);
        }
        public static RewardEntry recipe(String recipeId, Supplier<ItemStack> displayStack, String nameKey, String descKey) {
            return new RewardEntry(displayStack, recipeId, nameKey, descKey, StatBonus.NONE, RewardKind.RECIPE);
        }
        public static RewardEntry stat(StatBonus bonus, String nameKey, String descKey) {
            return new RewardEntry(() -> ItemStack.EMPTY, "", nameKey, descKey, bonus, RewardKind.STAT);
        }
        public static RewardEntry placeholder(String nameKey, String descKey) {
            return new RewardEntry(() -> ItemStack.EMPTY, "", nameKey, descKey, StatBonus.NONE, RewardKind.STAT);
        }
        public boolean isItem() { return kind == RewardKind.ITEM && !stack.get().isEmpty(); }
        public boolean isRecipe() { return kind == RewardKind.RECIPE && recipeId != null && !recipeId.isBlank(); }
    }

    private static final Map<SkillType, List<RewardEntry>> REWARDS;

    static {
        REWARDS = new EnumMap<>(SkillType.class);

        // Farming (0) — Iridium Scythe + Statue of Blessings + skill-description plaque
        REWARDS.put(SkillType.FARMING, List.of(
            RewardEntry.item(
                () -> new ItemStack(ModItems.IRIDIUM_SCYTHE.get()),
                "stardewcraft.mastery.reward.iridium_scythe.name",
                "stardewcraft.mastery.reward.iridium_scythe.desc"),
            RewardEntry.recipe("statue_of_blessings",
                () -> new ItemStack(ModItems.STATUE_OF_BLESSINGS.get()),
                "stardewcraft.mastery.reward.statue_of_blessings.name",
                "stardewcraft.mastery.reward.statue_of_blessings.desc"),
            RewardEntry.placeholder(
                "stardewcraft.mastery.menu.farming",
                "stardewcraft.mastery.reward.farming_plaque.desc")
        ));

        // Fishing (1) — Advanced Iridium Rod + Challenge Bait + plaque
        REWARDS.put(SkillType.FISHING, List.of(
            RewardEntry.item(
                () -> new ItemStack(ModItems.ADVANCED_IRIDIUM_ROD.get()),
                "stardewcraft.mastery.reward.advanced_iridium_rod.name",
                "stardewcraft.mastery.reward.advanced_iridium_rod.desc"),
            RewardEntry.recipe("challenge_bait",
                () -> new ItemStack(ModItems.CHALLENGE_BAIT.get()),
                "stardewcraft.mastery.reward.challenge_bait.name",
                "stardewcraft.mastery.reward.challenge_bait.desc"),
            RewardEntry.placeholder(
                "stardewcraft.mastery.menu.fishing",
                "stardewcraft.mastery.reward.fishing_plaque.desc")
        ));

        // Foraging (2) — Mystic Tree Seed + Treasure Totem + plaque
        REWARDS.put(SkillType.FORAGING, List.of(
            RewardEntry.recipe("mystic_tree_seed",
                () -> new ItemStack(ModItems.MYSTIC_TREE_SEED.get()),
                "stardewcraft.mastery.reward.mystic_tree_seed.name",
                "stardewcraft.mastery.reward.mystic_tree_seed.desc"),
            RewardEntry.recipe("treasure_totem",
                () -> new ItemStack(ModItems.TREASURE_TOTEM.get()),
                "stardewcraft.mastery.reward.treasure_totem.name",
                "stardewcraft.mastery.reward.treasure_totem.desc"),
            RewardEntry.placeholder(
                "stardewcraft.mastery.menu.foraging",
                "stardewcraft.mastery.reward.foraging_plaque.desc")
        ));

        // Mining (3) — Statue of the Dwarf King + Heavy Furnace + plaque
        REWARDS.put(SkillType.MINING, List.of(
            RewardEntry.recipe("statue_of_dwarf_king",
                () -> new ItemStack(ModItems.STATUE_OF_DWARF_KING.get()),
                "stardewcraft.mastery.reward.statue_of_dwarf_king.name",
                "stardewcraft.mastery.reward.statue_of_dwarf_king.desc"),
            RewardEntry.recipe("heavy_furnace",
                () -> new ItemStack(ModItems.HEAVY_FURNACE.get()),
                "stardewcraft.mastery.reward.heavy_furnace.name",
                "stardewcraft.mastery.reward.heavy_furnace.desc"),
            RewardEntry.placeholder(
                "stardewcraft.mastery.menu.mining",
                "stardewcraft.mastery.reward.mining_plaque.desc")
        ));

        // Combat (4) — Anvil + Mini-Forge + Trinket slot unlock
        REWARDS.put(SkillType.COMBAT, List.of(
            RewardEntry.recipe("anvil",
                () -> new ItemStack(ModItems.ANVIL_MASTERY.get()),
                "stardewcraft.mastery.reward.anvil.name",
                "stardewcraft.mastery.reward.anvil.desc"),
            RewardEntry.recipe("mini_forge",
                () -> new ItemStack(ModItems.MINI_FORGE.get()),
                "stardewcraft.mastery.reward.mini_forge.name",
                "stardewcraft.mastery.reward.mini_forge.desc"),
            RewardEntry.stat(StatBonus.TRINKET_SLOT,
                "stardewcraft.mastery.reward.trinket_slot.name",
                "stardewcraft.mastery.reward.trinket_slot.desc")
        ));
    }

    public static List<RewardEntry> rewardsFor(SkillType skill) {
        return REWARDS.getOrDefault(skill, List.of());
    }
}
