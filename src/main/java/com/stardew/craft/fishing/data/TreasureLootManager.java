package com.stardew.craft.fishing.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.stardew.craft.player.PlayerDataManager;
import com.stardew.craft.player.PlayerStardewData;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Data-driven fishing treasure generator.
 *
 * Config path: data/stardewcraft/fishing/fishing_treasure.json
 */
@SuppressWarnings("null")
public class TreasureLootManager extends SimplePreparableReloadListener<TreasureLootManager.TreasureData> {
	private static final Logger LOGGER = LoggerFactory.getLogger(TreasureLootManager.class);
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	private TreasureData data = new TreasureData();

	public static class TreasureLootEntry {
		public String item;
		public int minCount = 1;
		public int maxCount = 1;
		public int weight = 100;
		public int minFishingLevel = 0;
	}

	public static class TreasureData {
		public List<TreasureLootEntry> commonLoot = new ArrayList<>();
		public List<TreasureLootEntry> rareLoot = new ArrayList<>();
		public List<TreasureLootEntry> goldenLoot = new ArrayList<>();
		public List<TreasureLootEntry> fallbackLoot = new ArrayList<>();

		public double rollChanceStart = 1.0;
		public double rollChanceDecayNormal = 0.4;
		public double rollChanceDecayGolden = 0.6;
		public double rareChance = 0.15;
		public double goldenPoolChance = 0.5;
	}

	@Override
	protected TreasureData prepare(@Nonnull ResourceManager resourceManager, @Nonnull ProfilerFiller profiler) {
		TreasureData loaded = loadFromResourceManager(resourceManager);
		return loaded != null ? loaded : createDefaultData();
	}

	@Override
	protected void apply(@Nonnull TreasureData prepared, @Nonnull ResourceManager resourceManager, @Nonnull ProfilerFiller profiler) {
		this.data = prepared;
		LOGGER.info("Fishing treasure table applied: common={}, rare={}, golden={}, fallback={}",
				data.commonLoot.size(), data.rareLoot.size(), data.goldenLoot.size(), data.fallbackLoot.size());
	}

	public void initializeWithDefaults() {
		this.data = createDefaultData();
	}

	public void loadFromBundledData() {
		try (InputStream in = TreasureLootManager.class.getResourceAsStream("/data/stardewcraft/fishing/fishing_treasure.json")) {
			if (in == null) {
				LOGGER.warn("Missing bundled fishing treasure data, using defaults");
				this.data = createDefaultData();
				return;
			}
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
				JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
				TreasureData loaded = GSON.fromJson(json, TreasureData.class);
				this.data = sanitize(loaded);
				LOGGER.info("Loaded bundled fishing treasure data");
			}
		} catch (Exception e) {
			LOGGER.error("Failed to load bundled fishing treasure data, using defaults", e);
			this.data = createDefaultData();
		}
	}

	private TreasureData loadFromResourceManager(ResourceManager resourceManager) {
		try {
			Map<ResourceLocation, Resource> resources = resourceManager.listResources(
					"fishing",
					loc -> loc.getPath().endsWith("fishing_treasure.json")
			);
			for (Map.Entry<ResourceLocation, Resource> entry : resources.entrySet()) {
				ResourceLocation id = entry.getKey();
				if (!"stardewcraft".equals(id.getNamespace())) {
					continue;
				}
				try (BufferedReader reader = new BufferedReader(new InputStreamReader(entry.getValue().open(), StandardCharsets.UTF_8))) {
					JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
					LOGGER.info("Loaded fishing treasure table from {}", id);
					return sanitize(GSON.fromJson(json, TreasureData.class));
				}
			}
		} catch (Exception e) {
			LOGGER.error("Failed to load treasure table from resource manager", e);
		}
		return null;
	}

	private TreasureData sanitize(TreasureData in) {
		if (in == null) {
			return createDefaultData();
		}
		if (in.commonLoot == null) in.commonLoot = new ArrayList<>();
		if (in.rareLoot == null) in.rareLoot = new ArrayList<>();
		if (in.goldenLoot == null) in.goldenLoot = new ArrayList<>();
		if (in.fallbackLoot == null) in.fallbackLoot = new ArrayList<>();

		if (in.rollChanceStart <= 0) in.rollChanceStart = 1.0;
		if (in.rollChanceDecayNormal <= 0 || in.rollChanceDecayNormal >= 1.0) in.rollChanceDecayNormal = 0.4;
		if (in.rollChanceDecayGolden <= 0 || in.rollChanceDecayGolden >= 1.0) in.rollChanceDecayGolden = 0.6;
		in.rareChance = Mth.clamp((float) in.rareChance, 0f, 1f);
		in.goldenPoolChance = Mth.clamp((float) in.goldenPoolChance, 0f, 1f);
		return in;
	}

	public List<ItemStack> generateTreasure(int fishingLevel, boolean golden, RandomSource random, int clearWaterDistance, double dailyLuck) {
		return generateTreasure(fishingLevel, golden, random, clearWaterDistance, dailyLuck, null);
	}

	/**
	 * Generate treasure with player context for specialItem dedup and equipment drops.
	 * SDV parity: FishingRod.openTreasureMenuEndFunction
	 */
	public List<ItemStack> generateTreasure(int fishingLevel, boolean golden, RandomSource random,
			int clearWaterDistance, double dailyLuck, @Nullable ServerPlayer player) {
		List<ItemStack> treasures = new ArrayList<>();

		// SDV: luckModifier = (1 + dailyLuck) * (clearWaterDistance / 5)
		float luckModifier = (1f + (float) dailyLuck) * ((float) clearWaterDistance / 5f);

		// ---- Special equipment rolls (SDV: case 3 → sub-case 2, prob 1/12 per round) ----
		if (random.nextInt(12) == 0) {
			rollSpecialEquipment(treasures, fishingLevel, random, luckModifier, player);
		}

		double chance = data.rollChanceStart;
		double chanceDecay = golden ? data.rollChanceDecayGolden : data.rollChanceDecayNormal;
		double luckMultiplier = Mth.clamp((float) (1.0 + dailyLuck), 0.5f, 2.0f);

		while (random.nextDouble() <= chance) {
			chance *= chanceDecay;

			List<TreasureLootEntry> pool;
			if (golden && !data.goldenLoot.isEmpty() && random.nextDouble() < data.goldenPoolChance) {
				pool = data.goldenLoot;
			} else if (!data.rareLoot.isEmpty() && random.nextDouble() < data.rareChance * luckMultiplier) {
				pool = data.rareLoot;
			} else {
				pool = data.commonLoot;
			}

			ItemStack stack = rollOne(pool, fishingLevel, random);
			if (!stack.isEmpty()) {
				treasures.add(stack);
			}
		}

		if (treasures.isEmpty()) {
			ItemStack fallback = rollOne(data.fallbackLoot, fishingLevel, random);
			if (fallback.isEmpty()) {
				fallback = createHardFallback(random);
			}
			if (!fallback.isEmpty()) {
				treasures.add(fallback);
			}
		}

		return treasures;
	}

	/**
	 * SDV parity: FishingRod.openTreasureMenuEndFunction case 3 → sub-case 2.
	 * Independent probability checks for weapons, rings, boots, Prismatic Shard.
	 */
	@SuppressWarnings("null")
	private void rollSpecialEquipment(List<ItemStack> treasures, int fishingLevel,
			RandomSource random, float luckModifier, @Nullable ServerPlayer player) {
		PlayerStardewData data = player != null ? PlayerDataManager.getPlayerData(player) : null;

		// Neptune's Glaive — 5% × luckModifier, once per save
		if (random.nextFloat() < 0.05f * luckModifier) {
			if (data == null || !data.hasSpecialItem("stardewcraft:neptunes_glaive")) {
				addItem(treasures, "stardewcraft:neptunes_glaive", 1);
				if (data != null) data.addSpecialItem("stardewcraft:neptunes_glaive");
			}
		}

		// Broken Trident — 5% × luckModifier, once per save
		if (random.nextFloat() < 0.05f * luckModifier) {
			if (data == null || !data.hasSpecialItem("stardewcraft:broken_trident")) {
				addItem(treasures, "stardewcraft:broken_trident", 1);
				if (data != null) data.addSpecialItem("stardewcraft:broken_trident");
			}
		}

		// Rings — 7% × luckModifier
		if (random.nextFloat() < 0.07f * luckModifier) {
			String[] rings;
			int sub = random.nextInt(3);
			if (sub == 0) {
				rings = new String[]{"stardewcraft:small_glow_ring", "stardewcraft:glow_ring"};
			} else if (sub == 1) {
				rings = new String[]{"stardewcraft:small_magnet_ring", "stardewcraft:magnet_ring"};
			} else {
				rings = new String[]{
					"stardewcraft:amethyst_ring", "stardewcraft:topaz_ring",
					"stardewcraft:aquamarine_ring", "stardewcraft:jade_ring",
					"stardewcraft:emerald_ring", "stardewcraft:ruby_ring"
				};
			}
			String ring = rings[random.nextInt(rings.length)];
			addItem(treasures, ring, 1);
		}

		// Boots — 1% × luckModifier
		if (random.nextFloat() < 0.01f * luckModifier) {
			String[] boots = {
				"stardewcraft:sneakers", "stardewcraft:rubber_boots",
				"stardewcraft:leather_boots", "stardewcraft:work_boots",
				"stardewcraft:combat_boots", "stardewcraft:tundra_boots",
				"stardewcraft:thermal_boots", "stardewcraft:dark_boots",
				"stardewcraft:firewalker_boots", "stardewcraft:space_boots"
			};
			addItem(treasures, boots[random.nextInt(boots.length)], 1);
		}

		// Prismatic Shard — 0.1% × luckModifier, fishing level > 5
		if (fishingLevel > 5 && random.nextFloat() < 0.001f * luckModifier) {
			addItem(treasures, "stardewcraft:prismatic_shard", 1);
		}

		// Iridium Band — 1% × luckModifier
		if (random.nextFloat() < 0.01f * luckModifier) {
			addItem(treasures, "stardewcraft:iridium_band", 1);
		}
	}

	private void addItem(List<ItemStack> treasures, String itemId, int count) {
		ResourceLocation rl = ResourceLocation.tryParse(itemId);
		if (rl != null && BuiltInRegistries.ITEM.containsKey(rl)) {
			treasures.add(new ItemStack(BuiltInRegistries.ITEM.get(rl), count));
		}
	}

	public List<ItemStack> generateTreasure(int fishingLevel, boolean golden, RandomSource random) {
		return generateTreasure(fishingLevel, golden, random, 50, 0.0);
	}

	@SuppressWarnings("null")
	private ItemStack rollOne(List<TreasureLootEntry> source, int fishingLevel, RandomSource random) {
		List<TreasureLootEntry> eligible = new ArrayList<>();
		int totalWeight = 0;
		for (TreasureLootEntry entry : source) {
			if (entry == null || entry.item == null || entry.item.isBlank()) {
				continue;
			}
			if (entry.minFishingLevel > fishingLevel) {
				continue;
			}
			int w = Math.max(0, entry.weight);
			if (w == 0) {
				continue;
			}
			eligible.add(entry);
			totalWeight += w;
		}
		if (eligible.isEmpty() || totalWeight <= 0) {
			return ItemStack.EMPTY;
		}

		int roll = random.nextInt(totalWeight);
		for (TreasureLootEntry entry : eligible) {
			roll -= Math.max(0, entry.weight);
			if (roll >= 0) {
				continue;
			}
			ResourceLocation id = ResourceLocation.tryParse(entry.item);
			if (id == null || !BuiltInRegistries.ITEM.containsKey(id)) {
				return ItemStack.EMPTY;
			}
			Item item = BuiltInRegistries.ITEM.get(id);
			int min = Math.max(1, entry.minCount);
			int max = Math.max(min, entry.maxCount);
			int count = min + random.nextInt(max - min + 1);
			return new ItemStack(item, count);
		}

		return ItemStack.EMPTY;
	}

	private TreasureData createDefaultData() {
		TreasureData d = new TreasureData();

		d.commonLoot.add(createEntry("stardewcraft:copper_ore", 2, 7, 120));
		d.commonLoot.add(createEntry("stardewcraft:iron_ore", 2, 6, 100));
		d.commonLoot.add(createEntry("stardewcraft:coal", 3, 8, 140));
		d.commonLoot.add(createEntry("stardewcraft:stone", 5, 15, 80));
		d.commonLoot.add(createEntry("stardewcraft:quartz", 1, 4, 60));

		d.rareLoot.add(createEntry("stardewcraft:gold_ore", 1, 3, 90));
		d.rareLoot.add(createEntry("stardewcraft:diamond", 1, 2, 50));
		d.rareLoot.add(createEntry("stardewcraft:omni_geode", 1, 1, 45));
		d.rareLoot.add(createEntry("stardewcraft:sonar_bobber", 1, 1, 25, 6));
		d.rareLoot.add(createEntry("stardewcraft:deluxe_bait", 5, 5, 35, 6));
		d.rareLoot.add(createEntry("stardewcraft:fish_stew", 1, 1, 16, 2));
		d.rareLoot.add(createEntry("stardewcraft:shrimp_cocktail", 1, 1, 14, 2));

		d.goldenLoot.add(createEntry("stardewcraft:diamond", 2, 5, 100));
		d.goldenLoot.add(createEntry("stardewcraft:prismatic_shard", 1, 2, 35));
		d.goldenLoot.add(createEntry("stardewcraft:fairy_dust", 3, 6, 40));
		d.goldenLoot.add(createEntry("stardewcraft:challenge_bait", 3, 6, 45));
		d.goldenLoot.add(createEntry("stardewcraft:magnet", 3, 6, 30));
		d.goldenLoot.add(createEntry("stardewcraft:fish_stew", 1, 2, 18, 2));
		d.goldenLoot.add(createEntry("stardewcraft:shrimp_cocktail", 1, 2, 16, 2));

		d.fallbackLoot.add(createEntry("stardewcraft:bait", 5, 15, 100));
		return d;
	}

	private ItemStack createHardFallback(RandomSource random) {
		ResourceLocation baitId = ResourceLocation.tryParse("stardewcraft:bait");
		if (baitId != null && BuiltInRegistries.ITEM.containsKey(baitId)) {
			return new ItemStack(BuiltInRegistries.ITEM.get(baitId), 5 + random.nextInt(11));
		}
		ResourceLocation stoneId = ResourceLocation.tryParse("stardewcraft:stone");
		if (stoneId != null && BuiltInRegistries.ITEM.containsKey(stoneId)) {
			return new ItemStack(BuiltInRegistries.ITEM.get(stoneId), 10 + random.nextInt(11));
		}
		return ItemStack.EMPTY;
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
}
