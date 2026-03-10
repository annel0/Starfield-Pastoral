package com.stardew.craft.fishing.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 管理钓鱼宝箱的战利品生成。支持从data/{namespace}/fishing_treasure.json加载配置。
 * 配置格式参考星露谷物语的宝箱掉落表。
 */
public class TreasureLootManager extends SimplePreparableReloadListener<TreasureLootManager.TreasureData> {
	private static final Logger LOGGER = LoggerFactory.getLogger(TreasureLootManager.class);
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private TreasureData data = new TreasureData();

	public static class TreasureLootEntry {
		public String item;           // 物品ID（如 "minecraft:iron_ingot"）
		public int minCount = 1;
		public int maxCount = 1;
		public int weight = 100;      // 权重
		public int minFishingLevel = 0;  // 最低钓鱼等级要求
	}

	public static class TreasureData {
		public List<TreasureLootEntry> commonLoot = new ArrayList<>();
		public List<TreasureLootEntry> rareLoot = new ArrayList<>();
		public List<TreasureLootEntry> goldenLoot = new ArrayList<>();
		public int minItems = 1;
		public int maxItems = 3;
		public double rareChance = 0.15;  // 稀有物品生成概率
	}

	@Override
	protected TreasureData prepare(@SuppressWarnings("null") ResourceManager resourceManager, @SuppressWarnings("null") ProfilerFiller profiler) {
		TreasureData result = new TreasureData();

		try {
			Map<ResourceLocation, Resource> resources = resourceManager.listResources("data/stardewcraft", loc -> loc.getPath().endsWith("fishing_treasure.json"));
			if (!resources.isEmpty()) {
				Resource resource = resources.values().iterator().next();
				try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.open(), StandardCharsets.UTF_8))) {
					JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
					result = GSON.fromJson(json, TreasureData.class);
					LOGGER.info("Loaded fishing treasure loot from data");
				}
			} else {
				LOGGER.warn("No fishing_treasure.json found, using defaults");
				result = createDefaultData();
			}
		} catch (Exception e) {
			LOGGER.error("Failed to load fishing treasure loot, using defaults", e);
			result = createDefaultData();
		}

		return result;
	}

	@Override
	protected void apply(@SuppressWarnings("null") TreasureData prepared, @SuppressWarnings("null") ResourceManager resourceManager, @SuppressWarnings("null") ProfilerFiller profiler) {
		this.data = prepared;
		LOGGER.info("Fishing treasure loot table applied: {} common, {} rare, {} golden entries",
				data.commonLoot.size(), data.rareLoot.size(), data.goldenLoot.size());
	}

	/**
	 * 手动初始化默认数据（用于不通过ResourceManager的情况）
	 */
	public void initializeWithDefaults() {
		this.data = createDefaultData();
		LOGGER.info("Initialized fishing treasure with defaults: {} common, {} rare, {} golden entries",
				data.commonLoot.size(), data.rareLoot.size(), data.goldenLoot.size());
	}

	/**
	 * 创建默认的宝箱战利品表
	 */
	private TreasureData createDefaultData() {
		TreasureData d = new TreasureData();
		d.minItems = 3;
		d.maxItems = 6;
		d.rareChance = 0.25;

		// 常见物品（矿物、材料）
		d.commonLoot.add(createEntry("minecraft:iron_ingot", 2, 5, 100));
		d.commonLoot.add(createEntry("minecraft:coal", 3, 8, 150));
		d.commonLoot.add(createEntry("minecraft:copper_ingot", 2, 6, 120));
		d.commonLoot.add(createEntry("minecraft:gold_ingot", 1, 3, 80));
		d.commonLoot.add(createEntry("minecraft:emerald", 1, 2, 60));
		d.commonLoot.add(createEntry("minecraft:amethyst_shard", 2, 4, 80));
		d.commonLoot.add(createEntry("minecraft:lapis_lazuli", 3, 6, 90));
		d.commonLoot.add(createEntry("minecraft:redstone", 4, 8, 110));
		d.commonLoot.add(createEntry("minecraft:experience_bottle", 1, 3, 70));
		d.commonLoot.add(createEntry("minecraft:nautilus_shell", 1, 1, 40));
		d.commonLoot.add(createEntry("minecraft:prismarine_shard", 2, 4, 85));
		d.commonLoot.add(createEntry("minecraft:prismarine_crystals", 1, 3, 75));

		// 稀有物品（宝石、特殊物品）
		d.rareLoot.add(createEntry("minecraft:diamond", 1, 3, 100));
		d.rareLoot.add(createEntry("minecraft:enchanted_book", 1, 1, 80));
		d.rareLoot.add(createEntry("minecraft:golden_apple", 1, 2, 60));
		d.rareLoot.add(createEntry("minecraft:heart_of_the_sea", 1, 1, 30));
		d.rareLoot.add(createEntry("minecraft:trident", 1, 1, 20));
		d.rareLoot.add(createEntry("minecraft:name_tag", 1, 2, 50));
		d.rareLoot.add(createEntry("minecraft:saddle", 1, 1, 45));
		d.rareLoot.add(createEntry("minecraft:netherite_scrap", 1, 1, 25));

		// 金色宝箱物品（超稀有奖励）
		d.goldenLoot.add(createEntry("minecraft:diamond", 3, 6, 100));
		d.goldenLoot.add(createEntry("minecraft:netherite_ingot", 1, 2, 80));
		d.goldenLoot.add(createEntry("minecraft:enchanted_golden_apple", 1, 2, 60));
		d.goldenLoot.add(createEntry("minecraft:netherite_scrap", 2, 4, 70));
		d.goldenLoot.add(createEntry("minecraft:elytra", 1, 1, 30));
		d.goldenLoot.add(createEntry("minecraft:nether_star", 1, 1, 40));
		d.goldenLoot.add(createEntry("minecraft:totem_of_undying", 1, 1, 50));
		d.goldenLoot.add(createEntry("minecraft:ancient_debris", 1, 2, 45));

		return d;
	}

	private TreasureLootEntry createEntry(String item, int minCount, int maxCount, int weight) {
		return createEntry(item, minCount, maxCount, weight, 0);
	}

	private TreasureLootEntry createEntry(String item, int minCount, int maxCount, int weight, int minLevel) {
		TreasureLootEntry e = new TreasureLootEntry();
		e.item = item;
		e.minCount = minCount;
		e.maxCount = maxCount;
		e.weight = weight;
		e.minFishingLevel = minLevel;
		return e;
	}

	/**
	 * 生成宝箱战利品（完全按照星露谷物语逻辑）
	 *
	 * @param fishingLevel 玩家钓鱼等级
	 * @param golden       是否为金色宝箱
	 * @param random       随机源
	 * @param clearWaterDistance 清水距离（影响矿石质量，默认50）
	 * @param dailyLuck 每日运气（-0.1 到 0.1，默认0）
	 * @return 生成的物品列表
	 */
	@SuppressWarnings("null")
	public List<ItemStack> generateTreasure(int fishingLevel, boolean golden, RandomSource random, 
	                                        int clearWaterDistance, double dailyLuck) {
		List<ItemStack> treasures = new ArrayList<>();
		
		// 核心算法：星露谷物语的概率递减循环
		float chance = 1.0f; // 初始100%概率
		float chanceMult = golden ? 0.6f : 0.4f; // 金色宝箱60%，普通40%
		
		// Luck modifier - 影响稀有物品概率
		float luckModifier = (1.0f + (float)dailyLuck) * ((float)clearWaterDistance / 5.0f);
		
		while (random.nextDouble() <= chance) {
			chance *= chanceMult; // 每轮递减概率
			
			// 随机选择一个类别 (0-3)
			int category = random.nextInt(4);
			
			switch (category) {
				case 0: // 矿物和基础资源
					addOresByDistance(treasures, clearWaterDistance, random);
					break;
					
				case 1: // 鱼饵和浮标
					addBaitAndTackle(treasures, random);
					break;
					
				case 2: // 书籍和文物
					if (fishingLevel >= 2) {
						addBooksAndArtifacts(treasures, random);
					} else {
						// 低等级玩家只能获得鱼饵
						treasures.add(new ItemStack(Items.COD, random.nextInt(1, 4)));
					}
					break;
					
				case 3: // 宝石和高级物品
					if (fishingLevel >= 2) {
						addGemsAndRareItems(treasures, fishingLevel, luckModifier, random);
					}
					break;
			}
		}
		
		// 金色宝箱特殊奖励：50%概率额外获得一个特殊物品
		if (golden && random.nextDouble() < 0.5) {
			addGoldenSpecialItem(treasures, random);
		}
		
		// 保底机制：如果宝箱为空，给5-15个鱼饵
		if (treasures.isEmpty()) {
			treasures.add(new ItemStack(Items.COD, 5 + random.nextInt(11)));
		}
		
		return treasures;
	}
	
	/**
	 * 兼容旧接口的简化版本（使用默认参数）
	 */
	public List<ItemStack> generateTreasure(int fishingLevel, boolean golden, RandomSource random) {
		return generateTreasure(fishingLevel, golden, random, 50, 0.0);
	}
	
	/**
	 * 根据清水距离添加矿物（星露谷物语 Case 0）
	 */
	@SuppressWarnings("null")
	private void addOresByDistance(List<ItemStack> treasures, int clearWaterDistance, RandomSource random) {
		int count = 2 + random.nextInt(15); // 2-16个
		ItemStack ore;
		
		if (clearWaterDistance <= 20) {
			ore = new ItemStack(Items.COAL, count);
		} else if (clearWaterDistance <= 40) {
			ore = new ItemStack(Items.RAW_IRON, count);
		} else if (clearWaterDistance <= 80) {
			ore = new ItemStack(Items.RAW_GOLD, count);
		} else {
			ore = new ItemStack(Items.NETHERITE_SCRAP, count); // Iridium = Netherite
		}
		
		treasures.add(ore);
		
		// 偶尔添加木头或石头
		if (random.nextDouble() < 0.3) {
			treasures.add(new ItemStack(random.nextBoolean() ? Items.OAK_PLANKS : Items.COBBLESTONE, 
			                           5 + random.nextInt(10)));
		}
	}
	
	/**
	 * 添加鱼饵和浮标（星露谷物语 Case 1）
	 */
	@SuppressWarnings("null")
	private void addBaitAndTackle(List<ItemStack> treasures, RandomSource random) {
		int choice = random.nextInt(3);
		
		switch (choice) {
			case 0: // Dressed Spinner = 附魔钓鱼竿
				@SuppressWarnings("null") ItemStack rod = new ItemStack(Items.FISHING_ROD);
				// TODO: 添加随机附魔
				treasures.add(rod);
				break;
				
			case 1: // Wild Bait = 热带鱼
				treasures.add(new ItemStack(Items.TROPICAL_FISH, 5 + random.nextInt(6)));
				break;
				
			case 2: // Bait = 鳕鱼
			default:
				treasures.add(new ItemStack(Items.COD, 5 + random.nextInt(11)));
				break;
		}
	}
	
	/**
	 * 添加书籍和文物（星露谷物语 Case 2）
	 */
	@SuppressWarnings("null")
	private void addBooksAndArtifacts(List<ItemStack> treasures, RandomSource random) {
		int choice = random.nextInt(4);
		
		switch (choice) {
			case 0: // Lost Book
				treasures.add(new ItemStack(Items.BOOK));
				break;
				
			case 1: // Artifacts = 陶片
				treasures.add(new ItemStack(Items.BRICK, 1 + random.nextInt(3)));
				break;
				
			case 2: // Geode = 紫水晶簇
				treasures.add(new ItemStack(Items.AMETHYST_CLUSTER));
				break;
				
			case 3: // Random gems
			default:
				addRandomGem(treasures, random);
				break;
		}
	}
	
	/**
	 * 添加宝石和高级物品（星露谷物语 Case 3）
	 */
	@SuppressWarnings("null")
	private void addGemsAndRareItems(List<ItemStack> treasures, int fishingLevel, 
	                                 float luckModifier, RandomSource random) {
		// 基础宝石
		if (random.nextDouble() < 0.5) {
			addRandomGem(treasures, random);
		}
		
		// 钻石（5%概率）
		if (random.nextDouble() < 0.05) {
			@SuppressWarnings("null")
			ItemStack diamond = new ItemStack(Items.DIAMOND, 1);
			// 5%概率翻倍
			if (random.nextDouble() < 0.05) {
				diamond.setCount(2);
			}
			treasures.add(diamond);
		}
		
		// 戒指 - 马铠代替（7% * luckModifier）
		if (random.nextDouble() < 0.07 * luckModifier) {
			int ringType = random.nextInt(3);
			switch (ringType) {
				case 0: // Glow Ring
					treasures.add(new ItemStack(Items.IRON_HORSE_ARMOR));
					break;
				case 1: // Magnet Ring
					treasures.add(new ItemStack(Items.GOLDEN_HORSE_ARMOR));
					break;
				case 2: // Special rings
					treasures.add(new ItemStack(Items.DIAMOND_HORSE_ARMOR));
					break;
			}
		}
		
		// 附魔书（2% * luckModifier）
		if (random.nextDouble() < 0.02 * luckModifier) {
			treasures.add(new ItemStack(Items.ENCHANTED_BOOK));
		}
		
		// 下界之星（0.1% * luckModifier，需要钓鱼等级>5）
		if (fishingLevel > 5 && random.nextDouble() < 0.001 * luckModifier) {
			treasures.add(new ItemStack(Items.NETHER_STAR)); // Prismatic Shard
		}
		
		// 海蓝宝石（1% * luckModifier）
		if (random.nextDouble() < 0.01 * luckModifier) {
			treasures.add(new ItemStack(Items.PRISMARINE_CRYSTALS)); // Aquamarine
		}
		
		// 紫水晶（1% * luckModifier）
		if (random.nextDouble() < 0.01 * luckModifier) {
			treasures.add(new ItemStack(Items.AMETHYST_SHARD));
		}
		
		// 靴子（1% * luckModifier）
		if (random.nextDouble() < 0.01 * luckModifier) {
			treasures.add(new ItemStack(Items.LEATHER_BOOTS));
		}
		
		// Case 3保底：如果只有1个物品，额外给钻石
		if (treasures.size() == 1) {
			treasures.add(new ItemStack(Items.DIAMOND));
		}
	}
	
	/**
	 * 金色宝箱特殊物品（50%概率）
	 */
	@SuppressWarnings("null")
	private void addGoldenSpecialItem(List<ItemStack> treasures, RandomSource random) {
		double roll = random.nextDouble();
		
		if (roll < 0.25) { // 25% - Iridium Bar
			treasures.add(new ItemStack(Items.NETHERITE_INGOT, 1 + random.nextInt(3)));
		} else if (roll < 0.45) { // 20% - Skill Books (用附魔书代替)
			treasures.add(new ItemStack(Items.ENCHANTED_BOOK));
		} else if (roll < 0.60) { // 15% - 特殊鱼饵
			treasures.add(new ItemStack(Items.TROPICAL_FISH, 10));
		} else if (roll < 0.70) { // 10% - Magnet
			treasures.add(new ItemStack(Items.GOLDEN_HORSE_ARMOR));
		} else if (roll < 0.80) { // 10% - Fairy Dust (荧石粉)
			treasures.add(new ItemStack(com.stardew.craft.item.ModItems.FAIRY_DUST.get(), 5));
		} else if (roll < 0.85) { // 5% - Dressed Spinner
			treasures.add(new ItemStack(Items.FISHING_ROD));
		} else if (roll < 0.90) { // 5% - 特殊茶（蜂蜜瓶）
			treasures.add(new ItemStack(Items.HONEY_BOTTLE));
		} else if (roll < 0.95) { // 5% - Pearl (海晶灯)
			treasures.add(new ItemStack(Items.SEA_LANTERN));
		} else if (roll < 0.975) { // 2.5% - Lucky Ring
			treasures.add(new ItemStack(Items.DIAMOND_HORSE_ARMOR));
		} else if (roll < 0.995) { // 2% - Prismatic Shard
			treasures.add(new ItemStack(Items.NETHER_STAR));
		} else { // 0.5% - 三叉戟
			treasures.add(new ItemStack(Items.TRIDENT));
		}
	}
	
	/**
	 * 添加随机宝石
	 */
	@SuppressWarnings("null")
	private void addRandomGem(List<ItemStack> treasures, RandomSource random) {
		int gemChoice = random.nextInt(7);
		ItemStack gem;
		
		switch (gemChoice) {
			case 0: gem = new ItemStack(Items.EMERALD); break;
			case 1: gem = new ItemStack(Items.PRISMARINE_CRYSTALS); break; // Aquamarine
			case 2: gem = new ItemStack(Items.REDSTONE); break; // Ruby
			case 3: gem = new ItemStack(Items.AMETHYST_SHARD); break;
			case 4: gem = new ItemStack(Items.GOLD_NUGGET); break; // Topaz
			case 5: gem = new ItemStack(Items.LIME_DYE); break; // Jade
			case 6:
			default: gem = new ItemStack(Items.DIAMOND); break;
		}
		
		gem.setCount(1 + random.nextInt(3)); // 1-3个
		treasures.add(gem);
	}
}
