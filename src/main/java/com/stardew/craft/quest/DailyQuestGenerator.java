package com.stardew.craft.quest;

import com.stardew.craft.item.IStardewItem;
import com.stardew.craft.time.StardewTimeManager;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

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
        "pam", "george", "jodi", "linus"
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
    private static final String[] MONSTER_DISPLAY_NAMES = {
        "Slime", "Bat", "Skeleton", "Dust Sprite",
        "Ghost", "Rock Crab", "Shadow Brute", "Fly"
    };
    private static final int[] MONSTER_KILL_COUNTS = {10, 10, 8, 15, 5, 5, 5, 12};
    private static final int[] MONSTER_REWARDS = {100, 100, 150, 100, 200, 150, 150, 100};

    /**
     * 根据游戏日和世界种子生成当日公告栏任务
     * @param gameDay 绝对天数 (从 StardewTimeManager 获取)
     * @param worldSeed 世界种子
     * @return 生成的每日任务
     */
    public static StardewQuest generate(int gameDay, long worldSeed) {
        Random rng = new Random(worldSeed + gameDay * 77L);
        int season = getCurrentSeason();
        int questType = rng.nextInt(4); // 0=delivery, 1=fishing, 2=resource, 3=monster

        String questId = "daily_" + gameDay;
        StardewQuest quest;

        switch (questType) {
            case 0 -> quest = generateDeliveryQuest(rng, questId, season);
            case 1 -> quest = generateFishingQuest(rng, questId, season);
            case 2 -> quest = generateResourceQuest(rng, questId);
            case 3 -> quest = generateMonsterQuest(rng, questId);
            default -> quest = generateDeliveryQuest(rng, questId, season);
        }

        quest.setDailyQuest(true);
        quest.setCanBeCancelled(true);
        quest.setDaysLeft(2);
        return quest;
    }

    private static ItemDeliveryQuest generateDeliveryQuest(Random rng, String id, int season) {
        ItemDeliveryQuest q = new ItemDeliveryQuest();
        q.setId(id);

        String npc = DELIVERY_NPCS[rng.nextInt(DELIVERY_NPCS.length)];
        String[] items = DELIVERY_ITEMS_BY_SEASON[Math.min(season, 3)];
        String item = items[rng.nextInt(items.length)];
        String itemName = item.substring(item.indexOf(':') + 1).replace('_', ' ');
        String npcName = npc.substring(0, 1).toUpperCase() + npc.substring(1);

        q.setTargetNpc(npc);
        q.setItemId(item);
        q.setNumber(1);
        q.setTitle(npcName + "'s Request");
        q.setDescription(npcName + " needs a " + itemName + ". Can you bring one?");
        q.setObjectiveText("Bring " + itemName + " to " + npcName);
        q.setMoneyReward(Math.max(75, getItemPrice(item) * 3));
        return q;
    }

    private static FishingQuest generateFishingQuest(Random rng, String id, int season) {
        FishingQuest q = new FishingQuest();
        q.setId(id);

        String[] fishes = FISH_BY_SEASON[Math.min(season, 3)];
        String fish = fishes[rng.nextInt(fishes.length)];
        String fishName = fish.substring(fish.indexOf(':') + 1).replace('_', ' ');
        int count = 1 + rng.nextInt(3); // 1-3 fish

        q.setTargetNpc("willy");
        q.setItemId(fish);
        q.setNumberToFish(count);
        q.setTitle("Willy's Fishing Request");
        q.setDescription("Willy needs " + count + " " + fishName + " for a customer order.");
        q.setObjectiveText("Catch " + count + " " + fishName);
        // SDV two-phase: reward stored in subclass field, transferred to moneyReward on NPC report
        q.setReward(Math.max(100, count * (int)(getItemPrice(fish) * 1.5)));
        return q;
    }

    private static ResourceCollectionQuest generateResourceQuest(Random rng, String id) {
        ResourceCollectionQuest q = new ResourceCollectionQuest();
        q.setId(id);

        int idx = rng.nextInt(RESOURCE_ITEMS.length);
        String item = RESOURCE_ITEMS[idx];
        String itemName = item.substring(item.indexOf(':') + 1).replace('_', ' ');
        int amount = RESOURCE_AMOUNTS[idx];
        // SDV: reward = item.Price * number (dynamic)
        int reward = Math.max(100, getItemPrice(item) * amount);

        q.setTargetNpc(RESOURCE_NPC);
        q.setItemId(item);
        q.setNumber(amount);
        q.setTitle("Resource Collection");
        q.setDescription("Clint needs " + amount + " " + itemName + " for the forge.");
        q.setObjectiveText("Collect " + amount + " " + itemName);
        // SDV two-phase: reward stored in subclass field, transferred to moneyReward on NPC report
        q.setReward(reward);
        return q;
    }

    private static SlayMonsterQuest generateMonsterQuest(Random rng, String id) {
        SlayMonsterQuest q = new SlayMonsterQuest();
        q.setId(id);

        int idx = rng.nextInt(MONSTER_TYPES.length);
        String monsterTag = MONSTER_TYPES[idx];
        String monsterName = MONSTER_DISPLAY_NAMES[idx];
        int killCount = MONSTER_KILL_COUNTS[idx];
        int reward = MONSTER_REWARDS[idx];

        q.setMonsterName(monsterTag);
        q.setTargetNpc("lewis"); // SDV: Lewis/Demetrius/Wizard depending on monster type
        q.setNumberToKill(killCount);
        q.setTitle("Monster Eradication");
        q.setDescription("The adventurer's guild needs someone to slay " + killCount + " " + monsterName + "s.");
        q.setObjectiveText("Slay " + killCount + " " + monsterName + "s (" + 0 + "/" + killCount + ")");
        // SDV two-phase: reward stored in subclass field, transferred to moneyReward on NPC report
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
