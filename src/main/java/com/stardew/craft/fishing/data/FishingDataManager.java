package com.stardew.craft.fishing.data;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.stardew.craft.StardewCraft;
import com.stardew.craft.player.PlayerStardewDataAPI;
import com.stardew.craft.player.SkillType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.biome.Biome;

import java.util.*;

public final class FishingDataManager {
	private static final Gson GSON = new Gson();
	private static volatile FishingDataManager INSTANCE = new FishingDataManager(
			Collections.singletonMap("Default", FishingLocationData.defaultFallback())
	);

	public static FishingDataManager get() {
		return INSTANCE;
	}

	private final Map<String, FishingLocationData> byLocationKey;

	private FishingDataManager(Map<String, FishingLocationData> byLocationKey) {
		this.byLocationKey = byLocationKey;
	}

	/**
	 * 根据钓鱼位置选择鱼
	 *
	 * @param player     钓鱼玩家
	 * @param level      服务端世界
	 * @param bobberPos  浮漂位置
	 * @param waterDepth 水深/离岸距离
	 * @param random     随机源
	 * @return 选中的鱼
	 */
	@SuppressWarnings("null")
	public Optional<FishSelection> selectFish(ServerPlayer player, ServerLevel level, BlockPos bobberPos, int waterDepth, RandomSource random) {
		int fishingLevel = PlayerStardewDataAPI.getSkillLevel(player, SkillType.FISHING);
		boolean hasCuriosityLure = hasCuriosityLure(player);
		ItemStack rodStack = getRodFromPlayer(player);
		boolean usingMagicBait = !rodStack.isEmpty()
				&& (rodStack.getItem() instanceof com.stardew.craft.item.tool.FishingRodItem)
				&& com.stardew.craft.item.tool.FishingRodItem.hasBait(rodStack, "stardewcraft:magic_bait");
		boolean usingGoodBait = isUsingGoodBait(rodStack);

		// 获取浮漂位置的群系
		@SuppressWarnings("null")
		Holder<Biome> biomeHolder = level.getBiome(bobberPos);
		ResourceLocation biomeId = biomeHolder.unwrapKey()
				.map(key -> key.location())
				.orElse(ResourceLocation.withDefaultNamespace("plains"));

		// 获取当前环境条件
		boolean isRaining = level.isRaining();
		long timeOfDay = level.getDayTime() % 24000;
		String currentSeason = getCurrentSeason(level);

		// 获取维度数据和默认数据
		String dimensionKey = level.dimension().location().toString();
		FishingLocationData defaults = byLocationKey.getOrDefault("Default", FishingLocationData.defaultFallback());
		FishingLocationData specific = byLocationKey.getOrDefault(dimensionKey, FishingLocationData.empty(dimensionKey));

		List<SpawnFishRule> candidates = new ArrayList<>();
		candidates.addAll(defaults.fish());
		candidates.addAll(specific.fish());

		if (candidates.isEmpty()) {
			// 如果没有任何候选鱼（数据未加载或配置错误），直接返回垃圾
			StardewCraft.LOGGER.warn("No fish candidates found for dimension {} at biome {}. Check if fishing data loaded correctly!",
					dimensionKey, biomeId);
			return Optional.of(new FishSelection(getRandomJunk(random), 0, 0, true));
		}

		// 按优先级排序，同优先级内随机
		candidates.sort(Comparator.comparingInt(SpawnFishRule::precedence));
		List<SpawnFishRule> ordered = stableShuffleByPrecedence(candidates, random);

		// Stardew Valley style selection: iterate by precedence and roll chance (not weighted).
		SpawnFishRule chosen = null;
		for (int pass = 0; pass < 2 && chosen == null; pass++) {
			for (SpawnFishRule rule : ordered) {
				if (!rule.matchesBasic(fishingLevel, waterDepth)) {
					continue;
				}
				if (!rule.matchesBiome(biomeHolder)) {
					continue;
				}
				if (!usingMagicBait) {
					if (!rule.matchesSeason(currentSeason) || !rule.matchesWeather(isRaining) || !rule.matchesTime(timeOfDay)) {
						continue;
					}
				}
				float chance = Math.max(0f, rule.chance());
				if (hasCuriosityLure) {
					chance = applyCuriosityLureWeight(chance);
				}
				if (chance <= 0f) {
					continue;
				}
				if (random.nextFloat() >= chance) {
					continue;
				}
				chosen = rule;
				break;
			}

			// In Stardew, "good bait" effectively gives an extra pass; otherwise only one attempt.
			if (!usingGoodBait) {
				break;
			}
		}

		if (chosen == null) {
			return Optional.of(new FishSelection(getRandomJunk(random), 0, 0, true));
		}
		Item item;
		try {
			item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(chosen.itemId()));
		} catch (Exception ex) {
			item = Items.COD;
		}
		if (item == Items.AIR) {
			item = Items.COD;
		}

		@SuppressWarnings("null")
		ItemStack stack = new ItemStack(item);
		StardewCraft.LOGGER.debug("Fish selected: {} in biome {} (depth={}, level={})",
				chosen.itemId(), biomeId, waterDepth, fishingLevel);
		return Optional.of(new FishSelection(stack, chosen.difficulty(), chosen.motionTypeId(), chosen.skipMinigame()));
	}

	private static ItemStack getRodFromPlayer(ServerPlayer player) {
		if (player == null) {
			return ItemStack.EMPTY;
		}
		ItemStack rod = player.getMainHandItem();
		if (!rod.isEmpty() && rod.getItem() instanceof com.stardew.craft.item.tool.FishingRodItem) {
			return rod;
		}
		rod = player.getOffhandItem();
		if (!rod.isEmpty() && rod.getItem() instanceof com.stardew.craft.item.tool.FishingRodItem) {
			return rod;
		}
		return ItemStack.EMPTY;
	}

	private static boolean isUsingGoodBait(ItemStack rodStack) {
		if (rodStack == null || rodStack.isEmpty()) {
			return false;
		}
		if (!(rodStack.getItem() instanceof com.stardew.craft.item.tool.FishingRodItem rodItem)) {
			return false;
		}
		ItemStack bait = rodItem.getAttachmentsForTooltip(rodStack).bait();
		if (bait.isEmpty()) {
			return false;
		}
		@SuppressWarnings("null")
		ResourceLocation baitId = BuiltInRegistries.ITEM.getKey(bait.getItem());
		// Stardew: "good bait" means anything other than the basic bait item.
		return !baitId.toString().equals("stardewcraft:bait");
	}

	private static boolean hasCuriosityLure(ServerPlayer player) {
		if (player == null) {
			return false;
		}
		var main = player.getMainHandItem();
		if (!main.isEmpty() && main.getItem() instanceof com.stardew.craft.item.tool.FishingRodItem) {
			return com.stardew.craft.item.tool.FishingRodItem.hasTackle(main, "stardewcraft:curiosity_lure");
		}
		var off = player.getOffhandItem();
		if (!off.isEmpty() && off.getItem() instanceof com.stardew.craft.item.tool.FishingRodItem) {
			return com.stardew.craft.item.tool.FishingRodItem.hasTackle(off, "stardewcraft:curiosity_lure");
		}
		return false;
	}

	/**
	 * Stardew Valley (1.6) curiosity lure behavior: for low-chance fish (chance < 0.25),
	 * linearly raise the chance toward a minimum floor.
	 */
	private static float applyCuriosityLureWeight(float baseChance) {
		if (!(baseChance < 0.25f)) {
			return baseChance;
		}
		float max = 0.25f;
		float min = 0.08f;
		return (max - min) / max * baseChance + (max - min) / 2f;
	}

	/**
	 * 旧版兼容方法
	 */
	public Optional<FishSelection> selectFish(ServerPlayer player, ServerLevel level, int waterDepth, RandomSource random) {
		// 使用玩家位置作为浮漂位置（兼容旧代码）
		return selectFish(player, level, player.blockPosition(), waterDepth, random);
	}

	/**
	 * 获取当前季节
	 */
	private String getCurrentSeason(ServerLevel level) {
		// 根据游戏天数计算季节
		long dayTime = level.getDayTime();
		long days = dayTime / 24000;
		int seasonIndex = (int) ((days / 28) % 4);
		return switch (seasonIndex) {
			case 0 -> "spring";
			case 1 -> "summer";
			case 2 -> "fall";
			case 3 -> "winter";
			default -> "spring";
		};
	}

	/**
	 * 获取随机垃圾物品
	 */
	@SuppressWarnings("null")
	public ItemStack getRandomJunk(RandomSource random) {
		// 垃圾物品列表（对应星露谷的168-172、167、2046）
		// 使用mod的垃圾物品代替原版物品
		try {
			Item trash = BuiltInRegistries.ITEM.get(ResourceLocation.parse("stardewcraft:trash"));
			Item driftwood = BuiltInRegistries.ITEM.get(ResourceLocation.parse("stardewcraft:driftwood"));
			Item soggyNewspaper = BuiltInRegistries.ITEM.get(ResourceLocation.parse("stardewcraft:soggy_newspaper"));
			Item brokenCd = BuiltInRegistries.ITEM.get(ResourceLocation.parse("stardewcraft:broken_cd"));
			Item brokenGlasses = BuiltInRegistries.ITEM.get(ResourceLocation.parse("stardewcraft:broken_glasses"));
			Item jojaCola = BuiltInRegistries.ITEM.get(ResourceLocation.parse("stardewcraft:joja_cola"));
			Item rottenPlant = BuiltInRegistries.ITEM.get(ResourceLocation.parse("stardewcraft:rotten_plant"));
			Item seaweed = BuiltInRegistries.ITEM.get(ResourceLocation.parse("stardewcraft:seaweed"));
			Item greenAlgae = BuiltInRegistries.ITEM.get(ResourceLocation.parse("stardewcraft:green_algae"));
			Item whiteAlgae = BuiltInRegistries.ITEM.get(ResourceLocation.parse("stardewcraft:white_algae"));
			
			Item[] junkItems = {
				trash,            // 垃圾 (168)
				driftwood,        // 浮木 (169)
				soggyNewspaper,   // 湿透的报纸 (170)
				brokenCd,         // 破损的CD (171)
				brokenGlasses,    // 破损的眼镜 (172)
				jojaCola,         // Joja可乐 (167)
				rottenPlant,      // 腐烂的植物 (2046)
				seaweed,          // 海草 (152)
				greenAlgae,       // 绿藻 (153)
				whiteAlgae        // 白藻 (157)
			};
			return new ItemStack(junkItems[random.nextInt(junkItems.length)]);
		} catch (Exception e) {
			// Fallback to vanilla junk
			StardewCraft.LOGGER.error("Failed to get trash item", e);
			return new ItemStack(Items.STICK);
		}
	}

	private static List<SpawnFishRule> stableShuffleByPrecedence(List<SpawnFishRule> sorted, RandomSource random) {
		List<SpawnFishRule> result = new ArrayList<>(sorted.size());
		int i = 0;
		while (i < sorted.size()) {
			int precedence = sorted.get(i).precedence();
			int j = i + 1;
			while (j < sorted.size() && sorted.get(j).precedence() == precedence) {
				j++;
			}
			List<SpawnFishRule> group = new ArrayList<>(sorted.subList(i, j));
			Collections.shuffle(group, new Random(random.nextLong()));
			result.addAll(group);
			i = j;
		}
		return result;
	}

	public record FishSelection(ItemStack stack, int difficulty, int motionTypeId, boolean skipMinigame) {
	}

	/**
	 * 获取所有已加载的鱼类规则 (用于JEI等展示)
	 */
	public List<SpawnFishRule> getAllFishRules() {
		List<SpawnFishRule> allRules = new ArrayList<>();
		for (FishingLocationData data : byLocationKey.values()) {
			allRules.addAll(data.fish());
		}
		return allRules;
	}

	/**
	 * 根据物品ID获取对应的鱼类规则
	 */
	public Optional<SpawnFishRule> getRuleByItemId(String itemId) {
		for (FishingLocationData data : byLocationKey.values()) {
			for (SpawnFishRule rule : data.fish()) {
				if (rule.itemId().equals(itemId)) {
					return Optional.of(rule);
				}
			}
		}
		return Optional.empty();
	}

	public static final class ReloadListener extends SimpleJsonResourceReloadListener {
		public ReloadListener() {
			super(GSON, "fishing/locations");
		}

		@Override
		protected void apply(@SuppressWarnings("null") Map<ResourceLocation, JsonElement> objects, @SuppressWarnings("null") ResourceManager resourceManager, @SuppressWarnings("null") ProfilerFiller profiler) {
			Map<String, FishingLocationData> loaded = new HashMap<>();
			for (Map.Entry<ResourceLocation, JsonElement> entry : objects.entrySet()) {
				try {
					JsonObject root = entry.getValue().getAsJsonObject();
					FishingLocationData data = FishingLocationData.fromJson(root);
					loaded.put(data.locationKey(), data);
				} catch (Exception ex) {
					StardewCraft.LOGGER.error("Failed to load fishing location data {}", entry.getKey(), ex);
				}
			}

			// 确保至少有 Default
			loaded.putIfAbsent("Default", FishingLocationData.defaultFallback());
			INSTANCE = new FishingDataManager(Collections.unmodifiableMap(loaded));
			StardewCraft.LOGGER.info("Loaded fishing locations: {}", INSTANCE.byLocationKey.keySet());
		}
	}
}
