package com.stardew.craft.quest;

import com.stardew.craft.item.IStardewItem;
import com.stardew.craft.mining.MiningDataManager;
import com.stardew.craft.mining.MiningPlayerData;
import com.stardew.craft.time.StardewTimeManager;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.Random;

/**
 * 每日任务随机生成器 — 复刻 SDV Utility.getRandomItemFromSeason / Quest generation
 * 每天从 4 类任务中随机选 1 种，使用 gameDay + worldSeed 确保确定性
 */
@SuppressWarnings("null")
public final class DailyQuestGenerator {

    private DailyQuestGenerator() {}

    // ─── NPC 池 (可接收交付任务的 NPC) ───
    private static final String[] DELIVERY_NPCS = {
        "sam", "sebastian", "abigail", "shane", "emily", "leah",
        "maru", "elliott", "harvey", "caroline", "demetrius",
        "pierre", "robin", "gus", "clint", "willy", "marnie",
        "pam", "george", "jodi", "linus", "sandy"
    };

    // ─── 交付物品池 (按季节) ───
    private static final String[][] DELIVERY_ITEMS_BY_SEASON = {
        // 春
        {"stardewcraft:parsnip", "stardewcraft:potato", "stardewcraft:cauliflower",
         "stardewcraft:copper_bar", "stardewcraft:wood_normal", "stardewcraft:stone"},
        // 夏
        {"stardewcraft:melon", "stardewcraft:tomato", "stardewcraft:corn",
         "stardewcraft:iron_bar", "stardewcraft:wood_normal", "stardewcraft:coal"},
        // 秋
        {"stardewcraft:pumpkin", "stardewcraft:corn",
         "stardewcraft:gold_bar", "stardewcraft:wood_normal", "stardewcraft:coal"},
        // 冬
        {"stardewcraft:copper_bar", "stardewcraft:iron_bar", "stardewcraft:gold_bar",
         "stardewcraft:wood_normal", "stardewcraft:stone", "stardewcraft:coal"}
    };

    // ─── 钓鱼物品池 (按季节) ───
    private static final String[][] FISH_BY_SEASON = {
        // 春
        {"stardewcraft:catfish", "stardewcraft:sunfish", "stardewcraft:largemouth_bass",
         "stardewcraft:carp", "stardewcraft:bullhead", "stardewcraft:chub"},
        // 夏
        {"stardewcraft:pufferfish", "stardewcraft:sunfish", "stardewcraft:largemouth_bass",
         "stardewcraft:carp"},
        // 秋
        {"stardewcraft:salmon", "stardewcraft:catfish", "stardewcraft:smallmouth_bass",
         "stardewcraft:carp", "stardewcraft:bullhead"},
        // 冬
        {"stardewcraft:carp", "stardewcraft:bullhead", "stardewcraft:ghostfish",
         "stardewcraft:chub"}
    };

    // ─── 资源收集池 ───
    private static final String[] RESOURCE_ITEMS = {
        "stardewcraft:copper_ore", "stardewcraft:iron_ore", "stardewcraft:gold_ore",
        "stardewcraft:wood_normal", "stardewcraft:stone", "stardewcraft:coal"
    };
    private static final int[] RESOURCE_AMOUNTS = {20, 15, 10, 50, 40, 20};
    @SuppressWarnings("unused")
    private static final int[] RESOURCE_REWARDS = {150, 250, 500, 100, 100, 200};
    private static final String RESOURCE_NPC = "clint"; // SDV: usually Clint or Robin

    // ─── 怪物讨伐池 ───
    private static final String[] MONSTER_TYPES = {
        "sd_mob_slime", "sd_mob_bat", "sd_mob_skeleton", "sd_mob_dust_sprite",
        "sd_mob_ghost", "sd_mob_crab", "sd_mob_shadow", "sd_mob_fly"
    };
    private static final int[] MONSTER_KILL_COUNTS = {10, 10, 8, 15, 5, 5, 5, 12};
    private static final int[] MONSTER_REWARDS = {100, 100, 150, 100, 200, 150, 150, 100};

    /**
     * SDV parity Utility.getQuestOfTheDay (Utility.cs:3195).
     *
     * 概率表（按顺序，首个命中即返回）：
     * <ul>
     *   <li>gameDay ≤ 1 → null（首日无任务）</li>
     *   <li>d < 0.08 → ResourceCollection</li>
     *   <li>d < 0.20 AND 玩家已到达矿井任一层 AND daysPlayed > 5 → SlayMonster</li>
     *   <li>d < 0.50 → null（30% 天数不给任务）</li>
     *   <li>d < 0.60 → Fishing</li>
     *   <li>d < 0.66 AND 今天周一 AND 此玩家本存档没做过 SocializeQuest → Socialize</li>
     *   <li>else → ItemDelivery</li>
     * </ul>
     *
     * @param gameDay   绝对天数（从 1 开始）
     * @param worldSeed 世界种子
     * @param player    要为其生成的玩家（用于读取矿井进度 / 社交任务历史）；可为 null，此时按"最宽松"对待
     * @return 生成的每日任务；可能为 null（玩家今天没任务）
     */
    @Nullable
    public static StardewQuest generate(int gameDay, long worldSeed, @Nullable ServerPlayer player) {
        // 首日无任务（SDV: DaysPlayed <= 1 → return null）
        if (gameDay <= 1) {
            return null;
        }

        // SDV: CreateDaySaveRandom(100.0, DaysPlayed * 777).NextDouble()
        // 确定性：同一 gameDay + 同一 worldSeed → 同一随机数。多玩家独立生成可以用玩家 UUID 加入种子。
        long baseSeed = worldSeed + gameDay * 777L;
        if (player != null) {
            baseSeed ^= player.getUUID().getLeastSignificantBits();
        }
        Random rng = new Random(baseSeed);
        double d = rng.nextDouble();

        int season = getCurrentSeason();
        String questId = "daily_" + gameDay;

        // 是否已进过矿井（等价 SDV MineShaft.lowestLevelReached > 0）
        boolean everEnteredMine = false;
        if (player != null) {
            MiningPlayerData md = MiningDataManager.getPlayerData(player);
            everEnteredMine = md != null && md.getMaxFloorReached() > 0;
        }

        StardewQuest quest = null;

        if (d < 0.08) {
            quest = generateResourceQuest(rng, questId);
        } else if (d < 0.20 && everEnteredMine && gameDay > 5) {
            quest = generateMonsterQuest(rng, questId);
        } else if (d < 0.50) {
            // 30% 天数没任务
            return null;
        } else if (d < 0.60) {
            quest = generateFishingQuest(rng, questId, season);
        } else if (d < 0.66 && isMonday(gameDay) && !playerHasDoneSocializeQuest(player)) {
            // 周一 + 本存档还没做过 → 社交任务
            quest = generateSocializeQuest(rng, questId, player);
        } else {
            quest = generateDeliveryQuest(rng, questId, season);
        }

        if (quest == null) return null;

        quest.setDailyQuest(true);
        quest.setCanBeCancelled(true);
        quest.setDaysLeft(2);
        return quest;
    }

    /** 兼容老代码的无 player 重载 — 等价于 SDV 的"宽松"模式。 */
    @Nullable
    public static StardewQuest generate(int gameDay, long worldSeed) {
        return generate(gameDay, worldSeed, null);
    }

    /** gameDay 对应的"周一"判定（SDV 周一起，7 天一周）。
     *  约定 gameDay=1 是春1 = Monday（与 SDV Game1.shortDayNameFromDayOfSeason 同步）。 */
    private static boolean isMonday(int gameDay) {
        return ((gameDay - 1) % 7) == 0;
    }

    /** 该玩家本存档是否做过任何社交任务。用 QuestManager 的 completed 集合查询。 */
    private static boolean playerHasDoneSocializeQuest(@Nullable ServerPlayer player) {
        if (player == null) return false;
        QuestManager qm = QuestManager.of(player);
        if (qm == null) return false;
        // 已完成
        for (String id : qm.getCompletedQuestIds()) {
            if (id != null && id.startsWith("socialize_")) return true;
        }
        // 或已在任务栏里（避免一周重复接）
        for (StardewQuest q : qm.getQuestLog()) {
            if (q instanceof SocializeQuest) return true;
        }
        return false;
    }

    /** SDV parity: SocializeQuest（和所有 NPC 打招呼）— 没数据驱动，只是新建实例。 */
    private static SocializeQuest generateSocializeQuest(Random rng, String id, @Nullable ServerPlayer player) {
        SocializeQuest q = new SocializeQuest();
        q.setId(id);
        q.setTitle("Socialization");
        q.setDescription("Introduce yourself to everyone in the valley.");
        q.setObjectiveText("Meet every villager");
        q.setMoneyReward(500);
        return q;
    }

    private static ItemDeliveryQuest generateDeliveryQuest(Random rng, String id, int season) {
        ItemDeliveryQuest q = new ItemDeliveryQuest();
        q.setId(id);

        String npc = DELIVERY_NPCS[rng.nextInt(DELIVERY_NPCS.length)];
        String[] items = DELIVERY_ITEMS_BY_SEASON[Math.min(season, 3)];
        String item = items[rng.nextInt(items.length)];

        q.setTargetNpc(npc);
        q.setItemId(item);
        q.setNumber(1);
        // 本地化：NPC 名走 entity.stardewcraft.npc.<id>，物品名走 item.<namespace>.<path>
        String npcKey = "entity.stardewcraft.npc." + npc;
        String itemKey = itemDescriptionId(item);
        q.setLocalizedTitle("stardewcraft.quest.delivery.title", npcKey);
        q.setLocalizedDescription("stardewcraft.quest.delivery.desc", npcKey, itemKey);
        q.setLocalizedObjective("stardewcraft.quest.delivery.objective", itemKey, npcKey);
        q.setMoneyReward(Math.max(75, getItemPrice(item) * 3));
        return q;
    }

    private static FishingQuest generateFishingQuest(Random rng, String id, int season) {
        FishingQuest q = new FishingQuest();
        q.setId(id);

        String[] fishes = FISH_BY_SEASON[Math.min(season, 3)];
        String fish = fishes[rng.nextInt(fishes.length)];
        int count = 1 + rng.nextInt(3); // 1-3 fish

        q.setTargetNpc("willy");
        q.setItemId(fish);
        q.setNumberToFish(count);
        String fishKey = itemDescriptionId(fish);
        String willyKey = "entity.stardewcraft.npc.willy";
        q.setLocalizedTitle("stardewcraft.quest.fishing.title", willyKey);
        q.setLocalizedDescription("stardewcraft.quest.fishing.desc", willyKey, String.valueOf(count), fishKey);
        // objective: "钓 {count} 条 {fish}（{progress}/{count}）" — 进度在 FishingQuest.getObjectiveComponents 动态生成
        q.setLocalizedObjective("stardewcraft.quest.fishing.objective", String.valueOf(count), fishKey, "0");
        q.setReward(Math.max(100, count * (int)(getItemPrice(fish) * 1.5)));
        return q;
    }

    private static ResourceCollectionQuest generateResourceQuest(Random rng, String id) {
        ResourceCollectionQuest q = new ResourceCollectionQuest();
        q.setId(id);

        int idx = rng.nextInt(RESOURCE_ITEMS.length);
        String item = RESOURCE_ITEMS[idx];
        int amount = RESOURCE_AMOUNTS[idx];
        int reward = Math.max(100, getItemPrice(item) * amount);

        q.setTargetNpc(RESOURCE_NPC);
        q.setItemId(item);
        q.setNumber(amount);
        String itemKey = itemDescriptionId(item);
        String clintKey = "entity.stardewcraft.npc." + RESOURCE_NPC;
        q.setLocalizedTitle("stardewcraft.quest.resource.title");
        q.setLocalizedDescription("stardewcraft.quest.resource.desc", clintKey, String.valueOf(amount), itemKey);
        q.setLocalizedObjective("stardewcraft.quest.resource.objective", String.valueOf(amount), itemKey, "0");
        q.setReward(reward);
        return q;
    }

    private static SlayMonsterQuest generateMonsterQuest(Random rng, String id) {
        SlayMonsterQuest q = new SlayMonsterQuest();
        q.setId(id);

        int idx = rng.nextInt(MONSTER_TYPES.length);
        String monsterTag = MONSTER_TYPES[idx];
        int killCount = MONSTER_KILL_COUNTS[idx];
        int reward = MONSTER_REWARDS[idx];

        q.setMonsterName(monsterTag);
        q.setTargetNpc("lewis");
        q.setNumberToKill(killCount);
        // 怪物名：走翻译键 stardewcraft.monster.<id>（我们在 lang 里加上对应条目）
        String monsterKey = "stardewcraft.monster." + monsterTag;
        q.setLocalizedTitle("stardewcraft.quest.monster.title");
        q.setLocalizedDescription("stardewcraft.quest.monster.desc", String.valueOf(killCount), monsterKey);
        q.setLocalizedObjective("stardewcraft.quest.monster.objective", String.valueOf(killCount), monsterKey, "0");
        q.setReward(reward);
        return q;
    }

    private static int getCurrentSeason() {
        try {
            StardewTimeManager tm = StardewTimeManager.get();
            return tm != null ? tm.getCurrentSeason() : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 把 registry id（如 "stardewcraft:carp"）转成物品描述翻译键（"item.stardewcraft.carp"）。
     * 客户端的 lang 文件里登记这个键 → Component.translatable 会自动解析为对应语言的物品名。
     */
    private static String itemDescriptionId(String registryId) {
        if (registryId == null || registryId.isEmpty()) return "";
        int colon = registryId.indexOf(':');
        if (colon < 0) return "item." + registryId;
        return "item." + registryId.substring(0, colon) + "." + registryId.substring(colon + 1);
    }

    /** Look up the sell price of a stardew item by registry id. Falls back to 50 if not found. */
    private static int getItemPrice(String itemId) {
        try {
            ResourceLocation rl = ResourceLocation.parse(itemId);
            Item item = BuiltInRegistries.ITEM.get(rl);
            if (item instanceof IStardewItem sdItem) {
                int price = sdItem.getSellPrice(new ItemStack(item));
                if (price > 0) return price;
            }
        } catch (Exception ignored) {}
        return 50; // fallback
    }
}
