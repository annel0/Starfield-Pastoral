package com.stardew.craft.fishing.data;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.stardew.craft.StardewCraft;
import com.stardew.craft.core.ModDimensions;
import com.stardew.craft.core.ModMiningDimensions;
import com.stardew.craft.item.SpecificBaitItem;
import com.stardew.craft.player.PlayerStardewData;
import com.stardew.craft.player.PlayerStardewDataAPI;
import com.stardew.craft.player.SkillType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
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
import net.minecraft.tags.TagKey;

import java.util.*;

public final class FishingDataManager {
	private static final Gson GSON = new Gson();
	private static final Set<String> INHERITED_POOL_KEYS = Set.of("Default");
	private static final String LEGACY_COMPAT_POOL_KEY = "stardewcraft:stardew_valley";
	private static volatile RuleEligibilityHook RULE_ELIGIBILITY_HOOK = RuleEligibilityHook.ALLOW_ALL;
	private static volatile FishingDataManager INSTANCE = new FishingDataManager(
			Collections.singletonMap("Default", FishingLocationData.defaultFallback())
	);

	public static FishingDataManager get() {
		return INSTANCE;
	}

	static {
		setRuleEligibilityHook(FishingDataManager::matchesVanillaCondition);
	}

	/**
	 * Leave a single extension point for vanilla-gated conditions that require runtime state
	 * outside raw fish JSON rules (e.g. special order unlocks).
	 */
	public static void setRuleEligibilityHook(RuleEligibilityHook hook) {
		RULE_ELIGIBILITY_HOOK = hook == null ? RuleEligibilityHook.ALLOW_ALL : hook;
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
		if (!isStardewFishingDimension(level)) {
			return Optional.of(new FishSelection(getRandomJunk(random), 0, 0, true));
		}

		int fishingLevel = PlayerStardewDataAPI.getSkillLevel(player, SkillType.FISHING);
		int luckBuffLevel = Math.max(0, PlayerStardewDataAPI.getLuckBuffLevel(player));
		PlayerStardewData playerData = PlayerStardewDataAPI.getData(player);
		boolean hasCuriosityLure = hasCuriosityLure(player);
		ItemStack rodStack = getRodFromPlayer(player);
		boolean usingMagicBait = !rodStack.isEmpty()
				&& (rodStack.getItem() instanceof com.stardew.craft.item.tool.FishingRodItem)
				&& com.stardew.craft.item.tool.FishingRodItem.hasBait(rodStack, "stardewcraft:magic_bait");
		boolean usingGoodBait = isUsingGoodBait(rodStack);
		String baitTargetFishId = getTargetedBaitFishId(rodStack);

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

		List<String> lookupKeys = resolveVanillaAlignedLocationKeys(level, biomeHolder);
		String fishAreaId = resolveVanillaFishAreaId(biomeHolder);

		List<CandidateRule> candidates = collectCandidatesByKeys(lookupKeys);

		if (candidates.isEmpty()) {
			// 如果没有任何候选鱼（数据未加载或配置错误），直接返回垃圾
			StardewCraft.LOGGER.warn("No fish candidates found for keys {} at biome {}. Check if fishing data loaded correctly!",
					lookupKeys, biomeId);
			return Optional.of(new FishSelection(getRandomJunk(random), 0, 0, true));
		}

		// 按优先级排序，同优先级内随机
		candidates.sort(Comparator.comparingInt(c -> c.rule().precedence()));
		List<CandidateRule> ordered = stableShuffleByPrecedence(candidates, random);

		// Stardew Valley style selection: iterate by precedence and roll chance (not weighted).
		SpawnFishRule chosen = null;
		int targetedBaitTries = 0;
		SpawnFishRule firstNonTargetRule = null;
		for (int pass = 0; pass < 2 && chosen == null; pass++) {
			for (CandidateRule candidate : ordered) {
				SpawnFishRule rule = candidate.rule();
				if (candidate.inherited() && !rule.canBeInherited()) {
					continue;
				}
				if (!RULE_ELIGIBILITY_HOOK.allow(player, level, bobberPos, biomeHolder, rule)) {
					continue;
				}
				if (rule.catchLimit() >= 0 && playerData.getFishCatchCount(rule.itemId()) >= rule.catchLimit()) {
					continue;
				}
				if (rule.requireMagicBait() && !usingMagicBait) {
					continue;
				}
				if (rule.fishAreaId() != null && !rule.fishAreaId().isBlank()) {
					if (fishAreaId == null || !rule.fishAreaId().equalsIgnoreCase(fishAreaId)) {
						continue;
					}
				}
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
				if (luckBuffLevel > 0) {
					// Spirit Blessing now gives tangible fishing benefit: +2% base catch roll per luck level.
					chance = Math.min(1.0f, chance + 0.02f * luckBuffLevel);
				}
				if (hasCuriosityLure) {
					chance = applyCuriosityLureWeight(chance);
				}
				if (chance <= 0f) {
					continue;
				}
				if (random.nextFloat() >= chance) {
					continue;
				}
				if (baitTargetFishId != null && !baitTargetFishId.isBlank()
						&& !baitTargetFishId.equals(rule.itemId())
						&& targetedBaitTries < 2) {
					if (firstNonTargetRule == null) {
						firstNonTargetRule = rule;
					}
					targetedBaitTries++;
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

		if (chosen == null && firstNonTargetRule != null) {
			chosen = firstNonTargetRule;
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
		return Optional.of(new FishSelection(stack, chosen.difficulty(), chosen.motionTypeId(), chosen.skipMinigame()));
	}

	private List<CandidateRule> collectCandidatesByKeys(List<String> lookupKeys) {
		LinkedHashSet<String> uniqueKeys = new LinkedHashSet<>();
		uniqueKeys.add("Default");
		uniqueKeys.addAll(lookupKeys);

		boolean hasMappedLocationData = lookupKeys.stream().anyMatch(byLocationKey::containsKey);
		if (!hasMappedLocationData && byLocationKey.containsKey(LEGACY_COMPAT_POOL_KEY)) {
			// Compatibility fallback during Step 2 migration: only use the legacy mega-pool
			// when no mapped vanilla location file is present for this biome route.
			uniqueKeys.add(LEGACY_COMPAT_POOL_KEY);
		}

		List<CandidateRule> out = new ArrayList<>();
		for (String key : uniqueKeys) {
			FishingLocationData data = byLocationKey.get(key);
			if (data != null) {
				boolean inherited = (INHERITED_POOL_KEYS.contains(key) && !lookupKeys.contains(key))
						|| LEGACY_COMPAT_POOL_KEY.equals(key);
				for (SpawnFishRule rule : data.fish()) {
					out.add(new CandidateRule(rule, inherited));
				}
			}
		}
		return out;
	}

	private record CandidateRule(SpawnFishRule rule, boolean inherited) {
	}

	/**
	 * Match vanilla location buckets first (from Data/Locations), then fall back to legacy keys.
	 */
	private List<String> resolveVanillaAlignedLocationKeys(ServerLevel level, Holder<Biome> biomeHolder) {
		List<String> keys = new ArrayList<>();

		if (hasBiomeTag(biomeHolder, "stardewcraft:is_night_market")) {
			keys.add("BeachNightMarket");
			keys.add("Submarine");
		}
		if (hasBiomeTag(biomeHolder, "stardewcraft:is_pirate_cove")) {
			keys.add("IslandSouthEastCave");
		}
		if (hasBiomeTag(biomeHolder, "stardewcraft:is_ginger_island_ocean")) {
			keys.add("IslandSouth");
			keys.add("IslandSouthEast");
			keys.add("IslandWest");
		}
		if (hasBiomeTag(biomeHolder, "stardewcraft:is_ginger_island_river")) {
			keys.add("IslandNorth");
			keys.add("IslandWest");
		}
		if (hasBiomeTag(biomeHolder, "stardewcraft:is_ginger_island_pond")) {
			keys.add("IslandWest");
		}
		if (hasBiomeTag(biomeHolder, "stardewcraft:is_volcano")) {
			keys.add("Caldera");
		}
		if (hasBiomeTag(biomeHolder, "stardewcraft:is_witch_swamp")) {
			keys.add("WitchSwamp");
		}
		if (hasBiomeTag(biomeHolder, "stardewcraft:is_mutant_bug_lair")) {
			keys.add("BugLand");
		}
		if (hasBiomeTag(biomeHolder, "stardewcraft:is_sewers")) {
			keys.add("Sewer");
		}
		if (hasBiomeTag(biomeHolder, "stardewcraft:is_desert")) {
			keys.add("Desert");
		}
		if (hasBiomeTag(biomeHolder, "stardewcraft:is_mines_20")
				|| hasBiomeTag(biomeHolder, "stardewcraft:is_mines_60")
				|| hasBiomeTag(biomeHolder, "stardewcraft:is_mines_100")) {
			keys.add("UndergroundMine");
		}
		if (hasBiomeTag(biomeHolder, "stardewcraft:is_beach") || hasBiomeTag(biomeHolder, "stardewcraft:is_ocean")) {
			keys.add("Beach");
		}
		if (hasBiomeTag(biomeHolder, "stardewcraft:is_town_river") || hasBiomeTag(biomeHolder, "stardewcraft:is_jojamart_bridge")) {
			keys.add("Town");
		}
		if (hasBiomeTag(biomeHolder, "stardewcraft:is_mountain_lake")) {
			keys.add("Mountain");
		}
		if (hasBiomeTag(biomeHolder, "stardewcraft:is_forest_pond")
				|| hasBiomeTag(biomeHolder, "stardewcraft:is_forest_river")
				|| hasBiomeTag(biomeHolder, "stardewcraft:is_forest_waterfall")) {
			keys.add("Forest");
		}
		if (hasBiomeTag(biomeHolder, "stardewcraft:is_secret_woods")) {
			keys.add("Woods");
		}

		if (keys.isEmpty()) {
			keys.add("Default");
		}

		LinkedHashSet<String> deduped = new LinkedHashSet<>(keys);
		return new ArrayList<>(deduped);
	}

	private String resolveVanillaFishAreaId(Holder<Biome> biomeHolder) {
		if (hasBiomeTag(biomeHolder, "stardewcraft:is_beach_pier")) {
			return "Ocean";
		}
		if (hasBiomeTag(biomeHolder, "stardewcraft:is_beach") || hasBiomeTag(biomeHolder, "stardewcraft:is_ocean")) {
			return "Ocean";
		}
		if (hasBiomeTag(biomeHolder, "stardewcraft:is_town_river") || hasBiomeTag(biomeHolder, "stardewcraft:is_river")) {
			return "River";
		}
		if (hasBiomeTag(biomeHolder, "stardewcraft:is_mountain_lake")) {
			return "Lake";
		}
		if (hasBiomeTag(biomeHolder, "stardewcraft:is_forest_pond")) {
			return "Lake";
		}
		if (hasBiomeTag(biomeHolder, "stardewcraft:is_ginger_island_pond")) {
			return "Freshwater";
		}
		if (hasBiomeTag(biomeHolder, "stardewcraft:is_ginger_island_river")) {
			return "Freshwater";
		}
		if (hasBiomeTag(biomeHolder, "stardewcraft:is_mutant_bug_lair")) {
			return "Marsh";
		}
		if (hasBiomeTag(biomeHolder, "stardewcraft:is_desert")) {
			return "TopPond";
		}
		return null;
	}

	@SuppressWarnings("null")
	private static boolean hasBiomeTag(Holder<Biome> biomeHolder, String tagId) {
		ResourceLocation id = ResourceLocation.parse(tagId);
		TagKey<Biome> tag = TagKey.create(Registries.BIOME, id);
		return biomeHolder.is(tag);
	}

	private static boolean isStardewFishingDimension(ServerLevel level) {
		return level.dimension() == ModDimensions.STARDEW_VALLEY
				|| level.dimension() == ModMiningDimensions.STARDEW_MINING;
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

	private static String getTargetedBaitFishId(ItemStack rodStack) {
		if (rodStack == null || rodStack.isEmpty()) {
			return null;
		}
		if (!(rodStack.getItem() instanceof com.stardew.craft.item.tool.FishingRodItem rodItem)) {
			return null;
		}
		ItemStack bait = rodItem.getAttachmentsForTooltip(rodStack).bait();
		if (bait.isEmpty() || !(bait.getItem() instanceof SpecificBaitItem)) {
			return null;
		}
		String fishId = SpecificBaitItem.getTargetFishId(bait);
		if (fishId == null || fishId.isBlank()) {
			return null;
		}
		return fishId;
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

	private static boolean matchesVanillaCondition(ServerPlayer player, ServerLevel level, BlockPos bobberPos,
									  Holder<Biome> biomeHolder, SpawnFishRule rule) {
		String condition = rule.condition();
		if (condition == null || condition.isBlank()) {
			return true;
		}

		boolean negate = condition.startsWith("!");
		String normalized = negate ? condition.substring(1).trim() : condition.trim();
		String marker = "PLAYER_SPECIAL_ORDER_RULE_ACTIVE Current ";
		if (normalized.startsWith(marker)) {
			String ruleId = normalized.substring(marker.length()).trim();
			boolean active = PlayerStardewDataAPI.isSpecialOrderRuleActive(player, ruleId);
			return negate ? !active : active;
		}

		// Unknown condition expression: keep current behavior permissive for compatibility.
		return true;
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

	private static List<CandidateRule> stableShuffleByPrecedence(List<CandidateRule> sorted, RandomSource random) {
		List<CandidateRule> result = new ArrayList<>(sorted.size());
		int i = 0;
		while (i < sorted.size()) {
			int precedence = sorted.get(i).rule().precedence();
			int j = i + 1;
			while (j < sorted.size() && sorted.get(j).rule().precedence() == precedence) {
				j++;
			}
			List<CandidateRule> group = new ArrayList<>(sorted.subList(i, j));
			Collections.shuffle(group, new Random(random.nextLong()));
			result.addAll(group);
			i = j;
		}
		return result;
	}

	public record FishSelection(ItemStack stack, int difficulty, int motionTypeId, boolean skipMinigame) {
	}

	@FunctionalInterface
	public interface RuleEligibilityHook {
		RuleEligibilityHook ALLOW_ALL = (player, level, bobberPos, biomeHolder, rule) -> true;

		boolean allow(ServerPlayer player, ServerLevel level, BlockPos bobberPos, Holder<Biome> biomeHolder, SpawnFishRule rule);
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
