package com.stardew.craft.event;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.core.ModTags;
import com.stardew.craft.core.ModMiningDimensions;
import com.stardew.craft.item.tool.StardewPickaxeItem;
import com.stardew.craft.item.ModItems;
import com.stardew.craft.player.PlayerStardewDataAPI;
import com.stardew.craft.player.ProfessionType;
import com.stardew.craft.player.SkillType;
import com.stardew.craft.time.StardewTimeManager;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.ItemTags;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 矿石挖掘事件处理器 - 实现星露谷风格的镐子tier系统
 * 事件在 StardewCraft 主类中手动注册
 */
@SuppressWarnings("null")
public final class MinePickaxeEvents {
	private static final int BLOCKS_PER_ENERGY = 5;
	private static final float ENERGY_PER_BLOCK_GROUP = 1.0f;
	private static final Map<UUID, Integer> MINED_BLOCK_COUNTER = new ConcurrentHashMap<>();
	private static final double GEODE_BASE_CHANCE = 0.013; // 低于原版 0.022
	private static final double OMNI_GEODE_EXTRA_CHANCE = 0.0025; // 低于原版 0.005

	// 星露谷风格挖掘速度：tier0很慢，每升一级明显加快
	private static final float[] STARDEW_TIER_SPEEDS = {
		2.0F,   // tier0: 基础镐（很慢）
		4.5F,   // tier1: 铜镐（明显加快）
		8.0F,   // tier2: 钢镐（快）
		12.0F,  // tier3: 金镐（很快）
		18.0F   // tier4: 铱镐（极快）
	};
	private static final float TIER0_BASE_SPEED = 2.0F; // 非模组镐子使用这个速度
	private static final float STONE_BONUS_MULTIPLIER = 1.3F; // 挖石头额外加成

	private MinePickaxeEvents() {
	}

	@SuppressWarnings("null")
	@SubscribeEvent
	public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
		Player player = event.getEntity();
		if (player == null) {
			return;
		}
		BlockState state = event.getState();
		if (!isStardewMineBlock(state)) {
			return;
		}

		ItemStack tool = player.getMainHandItem();
		int stardewTier = getStardewPickaxeTier(tool);
		int requiredTier = getRequiredTier(state);

		// 非模组镐子：只能挖 tier 0 的石头和煤矿，不能挖铁矿及以上、宝石矿、矿物节点
		if (stardewTier < 0) {
			if (requiredTier > 0 || isStardewOre(state) || isMineralBlock(state)) {
				event.setNewSpeed(0.0F);
				return;
			}
		} else if (requiredTier > 0 && stardewTier < requiredTier) {
			// 模组镐子等级不足
			event.setNewSpeed(0.0F);
			return;
		}

		float baseToolSpeed;
		boolean isPickaxeLike = isPickaxeLike(tool);
		if (stardewTier >= 0 && stardewTier < STARDEW_TIER_SPEEDS.length) {
			// 使用我们定义的tier速度，让升级感更明显
			baseToolSpeed = STARDEW_TIER_SPEEDS[stardewTier];
		} else if (isPickaxeLike) {
			// Any non-mod pickaxe is treated like our tier0 pickaxe on mod mine blocks.
			baseToolSpeed = TIER0_BASE_SPEED;
		} else {
			baseToolSpeed = 1.0F;
		}

		float speed = computeDigSpeed(player, tool, baseToolSpeed);
		if (stardewTier >= 0 && state.is(ModTags.Blocks.STARDEW_STONES)) {
			speed *= STONE_BONUS_MULTIPLIER;
		}
		if (isMineralBlock(state)) {
			speed *= 4.0F; // 挖掘时间约为之前的1/4
		}

		// Do not cap to originalSpeed here: originalSpeed depends on vanilla tags/tool-components and may be
		// extremely low if the block isn't recognized as pickaxe-mineable. Our design intentionally overrides it.
		event.setNewSpeed(Math.max(0.0F, speed));
	}

	@SuppressWarnings("null")
	@SubscribeEvent
	public static void onBlockBreak(BlockEvent.BreakEvent event) {
		if (!(event.getPlayer() instanceof ServerPlayer player)) {
			return;
		}
		if (player.isCreative()) {
			return;
		}

		BlockState state = event.getState();
		if (!isStardewMineBlock(state)) {
			return;
		}

		ItemStack tool = player.getMainHandItem();
		int requiredTier = getRequiredTier(state);
		int stardewTier = getStardewPickaxeTier(tool);
		
		// Debug log
		StardewCraft.LOGGER.info("[MinePickaxe] Block: {}, requiredTier: {}, stardewTier: {}, isPickaxe: {}", 
			net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(state.getBlock()),
			requiredTier, stardewTier, isPickaxeLike(tool));

		if (!isPickaxeLike(tool)) {
			event.setCanceled(true); // 不是镐子，阻止破坏
			return;
		}

		// 非模组镐子：只能挖 tier 0 的石头和煤矿，不能挖铁矿及以上、宝石矿、矿物节点
		if (stardewTier < 0 && (requiredTier > 0 || isStardewOre(state) || isMineralBlock(state))) {
			event.setCanceled(true);
			StardewCraft.LOGGER.info("[MinePickaxe] Blocked non-mod pickaxe on ore/mineral: {}", 
				net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(state.getBlock()));
			return;
		}

		if (requiredTier > 0 && stardewTier < requiredTier) {
			event.setCanceled(true); // tier不够，阻止破坏
			StardewCraft.LOGGER.info("[MinePickaxe] Blocked: tier {} < required {}", stardewTier, requiredTier);
			return;
		}

		if (!(event.getLevel() instanceof ServerLevel level)) {
			return;
		}

		// 能量不足时，禁止继续挖矿（仅矿井维度生效）
		if (level.dimension() == ModMiningDimensions.STARDEW_MINING
				&& PlayerStardewDataAPI.getEnergy(player) <= 0.0f) {
			player.displayClientMessage(Component.translatable("stardewcraft.message.player.exhausted"), true);
			event.setCanceled(true);
			return;
		}

		BlockPos pos = event.getPos();
		event.setCanceled(true);

		// Break block without vanilla drops.
		level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
		level.levelEvent(2001, pos, Block.getId(state));

		// For ores: drop resource items with Stardew quantity logic
		if (isStardewOre(state)) {
			Item dropItem = getOreDropItem(state);
			if (dropItem != null) {
				int count = isGemOre(state) ? computeGemOreCount(level, player) : computeStardewOreCount(player, pos);
				if (count <= 0) {
					count = 1;
				}
				Block.popResource(level, pos, new ItemStack(dropItem, count));
			}
		} else {
			// For stones: drop themselves (1 block)
			Block.popResource(level, pos, new ItemStack(state.getBlock().asItem(), 1));

			// 额外晶洞掉落（概率降低，但逻辑与原版一致）
			tryDropGeode(level, player, pos, state);
			tryDropCoalFromStone(level, player, pos, state);
		}

		// 触发梯子概率逻辑（生存模式自定义挖掘路径）
		MiningBlockBreakHandler.handleStoneBreak(level, player, pos, state);

		int miningExp = getMiningExperienceForBlock(state);
		if (miningExp > 0) {
			PlayerStardewDataAPI.addExperience(player, SkillType.MINING, miningExp);
		}

		consumeMiningEnergy(player, level);
	}

	private static int getMiningExperienceForBlock(BlockState state) {
		@SuppressWarnings("null")
		String path = net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(state.getBlock()).getPath();

		if (path.contains("prismatic_shard")) return 50;
		if (path.contains("diamond_ore") || path.contains("amethyst_ore") || path.contains("aquamarine_ore")
				|| path.contains("emerald_ore") || path.contains("jade_ore") || path.contains("ruby_ore")
				|| path.contains("topaz_ore")) {
			return 25;
		}
		if (path.contains("iridium_ore")) return 25;
		if (path.contains("gold_ore")) return 15;
		if (path.contains("iron_ore")) return 10;
		if (path.contains("coal_ore") || path.contains("copper_ore")) return 5;

		if (state.is(ModTags.Blocks.STARDEW_STONES)
				|| path.equals("earth_shale")
				|| path.equals("frost_gneiss")
				|| path.equals("lava_basalt")
				|| path.equals("dark_earth_shale")
				|| path.equals("dark_frost_gneiss")
				|| path.equals("dark_lava_basalt")
				|| path.equals("banded_marble")
				|| path.equals("limestone")
				|| path.equals("mossy_sandstone")
				|| path.equals("cracked_slate")
				|| path.equals("scoria")
				|| path.equals("salt_rock")) {
			return 1;
		}

		return 0;
	}

	@SuppressWarnings("null")
	private static void tryDropGeode(ServerLevel level, ServerPlayer player, BlockPos pos, BlockState state) {
		if (level.dimension() != ModMiningDimensions.STARDEW_MINING) {
			return;
		}
		if (isStardewOre(state)) {
			return;
		}

		int floorNumber = getFloorNumber(pos);
		int luckLevel = PlayerStardewDataAPI.getLuckLevel(player);
		double dailyLuck = PlayerStardewDataAPI.getDailyLuck(player);
		int miningLevel = PlayerStardewDataAPI.getSkillLevel(player, SkillType.MINING);
		double chanceModifier = dailyLuck / 2.0 + miningLevel * 0.005 + luckLevel * 0.001;
		int excavatorMultiplier = PlayerStardewDataAPI.hasProfession(player, ProfessionType.EXCAVATOR) ? 2 : 1;

		double geodeChance = GEODE_BASE_CHANCE * (1.0 + chanceModifier) * excavatorMultiplier;
		if (level.getRandom().nextDouble() < geodeChance) {
			Item geode = pickGeodeItemForFloor(floorNumber);
			if (geode != null) {
				Block.popResource(level, pos, new ItemStack(geode, 1));
			}
		}

		// 普通矿井 20 层以上仍可额外掉落万能晶洞
		if (floorNumber >= 20 && level.getRandom().nextDouble() < OMNI_GEODE_EXTRA_CHANCE * (1.0 + chanceModifier) * excavatorMultiplier) {
			Block.popResource(level, pos, new ItemStack(ModItems.OMNI_GEODE.get(), 1));
		}

	}

	private static Item pickGeodeItemForFloor(int floorNumber) {
		if (floorNumber >= 80) {
			return ModItems.MAGMA_GEODE.get();
		}
		if (floorNumber >= 40) {
			return ModItems.FROZEN_GEODE.get();
		}
		return ModItems.GEODE.get();
	}

	private static int getFloorNumber(BlockPos pos) {
		int z = pos.getZ();
		int floor = Math.round((z - 14) / (float) com.stardew.craft.mining.MiningCoordinates.FLOOR_SPACING);
		return Math.max(0, floor);
	}

	@SubscribeEvent
	public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
		MINED_BLOCK_COUNTER.remove(event.getEntity().getUUID());
	}

	private static void consumeMiningEnergy(ServerPlayer player, ServerLevel level) {
		if (level.dimension() != ModMiningDimensions.STARDEW_MINING) {
			return;
		}
		UUID id = player.getUUID();
		int count = MINED_BLOCK_COUNTER.getOrDefault(id, 0) + 1;
		int energyTicks = count / BLOCKS_PER_ENERGY;
		if (energyTicks > 0) {
			for (int i = 0; i < energyTicks; i++) {
				PlayerStardewDataAPI.consumeEnergy(player, ENERGY_PER_BLOCK_GROUP);
			}
			count = count % BLOCKS_PER_ENERGY;
		}
		MINED_BLOCK_COUNTER.put(id, count);
	}

	private static Item getOreDropItem(BlockState state) {
		@SuppressWarnings("null")
		String path = net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(state.getBlock()).getPath();
		if (path.contains("coal_ore")) {
			return ModItems.COAL.get();
		}
		if (path.contains("copper_ore")) {
			return ModItems.COPPER_ORE.get();
		}
		if (path.contains("iron_ore")) {
			return ModItems.IRON_ORE.get();
		}
		if (path.contains("gold_ore")) {
			return ModItems.GOLD_ORE.get();
		}
		if (path.contains("iridium_ore")) {
			return ModItems.IRIDIUM_ORE.get();
		}
		return getGemOreDropItem(path);
	}

	private static boolean isGemOre(BlockState state) {
		@SuppressWarnings("null")
		String path = net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(state.getBlock()).getPath();
		return getGemOreDropItem(path) != null;
	}

	private static Item getGemOreDropItem(String path) {
		return switch (path) {
			case "amethyst_ore" -> ModItems.AMETHYST.get();
			case "aquamarine_ore" -> ModItems.AQUAMARINE.get();
			case "diamond_ore" -> ModItems.DIAMOND.get();
			case "emerald_ore" -> ModItems.EMERALD.get();
			case "jade_ore" -> ModItems.JADE.get();
			case "ruby_ore" -> ModItems.RUBY.get();
			case "topaz_ore" -> ModItems.TOPAZ.get();
			default -> null;
		};
	}

	private static int computeGemOreCount(ServerLevel level, ServerPlayer player) {
		int count = 1;
		if (PlayerStardewDataAPI.hasProfession(player, ProfessionType.GEOLOGIST) && level.getRandom().nextFloat() < 0.5f) {
			count += 1;
		}
		return count;
	}

	private static void tryDropCoalFromStone(ServerLevel level, ServerPlayer player, BlockPos pos, BlockState state) {
		if (level.dimension() != ModMiningDimensions.STARDEW_MINING || !isMineralBlock(state)) {
			return;
		}
		int miningLevel = PlayerStardewDataAPI.getSkillLevel(player, SkillType.MINING);
		double dailyLuck = PlayerStardewDataAPI.getDailyLuck(player);
		double luckBuff = PlayerStardewDataAPI.getLuckBuffLevel(player);

		double chance = 0.035 + miningLevel * 0.001 + dailyLuck * 0.10 + luckBuff * 0.001;
		if (PlayerStardewDataAPI.hasProfession(player, ProfessionType.PROSPECTOR)) {
			chance *= 2.0;
		}
		chance = Mth.clamp(chance, 0.0, 0.95);

		if (level.getRandom().nextDouble() < chance) {
			Block.popResource(level, pos, new ItemStack(ModItems.COAL.get(), 1));
		}
	}

	private static int computeStardewOreCount(ServerPlayer player, BlockPos pos) {
		// Match Stardew Valley's node logic for copper/iron/gold/iridium/coal:
		// count = addedOres + r.Next(1, 4) + (rand < luckLevel/100 ? 1 : 0) + (rand < miningLevel/100 ? 1 : 0)
		// (note: SV uses LuckLevel (buff) here, not DailyLuck)
		int addedOres = PlayerStardewDataAPI.hasProfession(player, ProfessionType.MINER) ? 1 : 0;
		int luckLevel = PlayerStardewDataAPI.getLuckBuffLevel(player);
		int miningLevel = PlayerStardewDataAPI.getSkillLevel(player, SkillType.MINING);

		RandomSource r = RandomSource.create(makeDailyPosSeed(pos));
		int count = addedOres + (r.nextInt(3) + 1);
		if (r.nextDouble() < (luckLevel / 100.0)) {
			count += 1;
		}
		if (r.nextDouble() < (miningLevel / 100.0)) {
			count += 1;
		}
		return Mth.clamp(count, 1, 999);
	}

	private static long makeDailyPosSeed(BlockPos pos) {
		StardewTimeManager time = StardewTimeManager.get();
		int year = time.getCurrentYear();
		int season = time.getCurrentSeason();
		int day = time.getCurrentDay();
		int dateKey = ((year * 4) + season) * 28 + (day - 1);
		long seed = ((long) dateKey * 0x9E3779B97F4A7C15L) ^ pos.asLong() ^ 0xD1B54A32D192ED03L;
		return seed;
	}

	@SuppressWarnings("null")
	private static int getRequiredTier(BlockState state) {
		// 通过 tag 统一管理所有方块的挖掘等级（矿石 + 宝石 + 矿物节点 + 石头）
		if (state.is(ModTags.Blocks.REQUIRES_STARDEW_PICKAXE_TIER3)) {
			return 3;
		}
		if (state.is(ModTags.Blocks.REQUIRES_STARDEW_PICKAXE_TIER2)) {
			return 2;
		}
		if (state.is(ModTags.Blocks.REQUIRES_STARDEW_PICKAXE_TIER1)) {
			return 1;
		}
		return 0;
	}

	private static int getStardewPickaxeTier(ItemStack tool) {
		if (tool.getItem() instanceof StardewPickaxeItem stardewPickaxe) {
			return stardewPickaxe.getStardewTier();
		}
		@SuppressWarnings("null")
		var key = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(tool.getItem());
		if (key != null && StardewCraft.MODID.equals(key.getNamespace())) {
			return switch (key.getPath()) {
				case "pickaxe" -> 0;
				case "copper_pickaxe" -> 1;
				case "steel_pickaxe" -> 2;
				case "gold_pickaxe" -> 3;
				case "iridium_pickaxe" -> 4;
				default -> -1;
			};
		}
		return -1;
	}

	@SuppressWarnings("null")
	private static boolean isPickaxeLike(ItemStack tool) {
		return tool.is(ItemTags.PICKAXES)
				|| tool.getItem() instanceof PickaxeItem
				|| tool.is(Items.WOODEN_PICKAXE);
	}

	@SuppressWarnings("null")
	private static boolean isStardewMineBlock(BlockState state) {
		if (state.is(ModTags.Blocks.STARDEW_STONES) || state.is(ModTags.Blocks.STARDEW_ORES)) {
			return true;
		}
		if (isMineralBlock(state)) {
			return true;
		}
		@SuppressWarnings("null")
		var key = net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(state.getBlock());
		if (key == null || !StardewCraft.MODID.equals(key.getNamespace())) {
			return false;
		}
		String path = key.getPath();
		return path.endsWith("_ore")
				|| path.equals("earth_shale")
				|| path.equals("frost_gneiss")
				|| path.equals("lava_basalt")
				|| path.equals("dark_earth_shale")
				|| path.equals("dark_frost_gneiss")
				|| path.equals("dark_lava_basalt")
				|| path.equals("banded_marble")
				|| path.equals("limestone")
				|| path.equals("mossy_sandstone")
				|| path.equals("cracked_slate")
				|| path.equals("scoria")
				|| path.equals("salt_rock");
	}

	@SuppressWarnings("null")
	private static boolean isStardewOre(BlockState state) {
		if (state.is(ModTags.Blocks.STARDEW_ORES)) {
			return true;
		}
		@SuppressWarnings("null")
		var key = net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(state.getBlock());
		return key != null && StardewCraft.MODID.equals(key.getNamespace()) && key.getPath().endsWith("_ore");
	}

	private static boolean isMineralBlock(BlockState state) {
		@SuppressWarnings("null")
		var key = net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(state.getBlock());
		if (key == null || !StardewCraft.MODID.equals(key.getNamespace())) {
			return false;
		}
		String path = key.getPath();
		return path.equals("quartz")
				|| path.equals("earth_crystal")
				|| path.equals("frozen_tear")
				|| path.equals("fire_quartz")
				|| path.equals("amethyst_ore")
				|| path.equals("aquamarine_ore")
				|| path.equals("diamond_ore")
				|| path.equals("emerald_ore")
				|| path.equals("jade_ore")
				|| path.equals("ruby_ore")
				|| path.equals("topaz_ore");
	}

	private static float computeDigSpeed(Player player, ItemStack tool, float baseToolSpeed) {
		float speed = baseToolSpeed;

		if (speed > 1.0F) {
			int efficiency = getItemEnchantmentLevel(player, tool, Enchantments.EFFICIENCY);
			if (efficiency > 0) {
				speed += (float) (efficiency * efficiency + 1);
			}
		}

		@SuppressWarnings("null")
		MobEffectInstance haste = player.getEffect(MobEffects.DIG_SPEED);
		if (haste != null) {
			speed *= 1.0F + 0.2F * (haste.getAmplifier() + 1);
		}

		@SuppressWarnings("null")
		MobEffectInstance fatigue = player.getEffect(MobEffects.DIG_SLOWDOWN);
		if (fatigue != null) {
			float mult;
			switch (fatigue.getAmplifier()) {
				case 0 -> mult = 0.3F;
				case 1 -> mult = 0.09F;
				case 2 -> mult = 0.0027F;
				case 3 -> mult = 8.1E-4F;
				default -> mult = 2.43E-4F;
			}
			speed *= mult;
		}

		if (player.isInWater() && !hasAquaAffinity(player)) {
			speed /= 5.0F;
		}
		if (!player.onGround()) {
			speed /= 5.0F;
		}

		return speed;
	}

	@SuppressWarnings({ "null", "deprecation" })
	private static int getItemEnchantmentLevel(Player player, ItemStack stack, net.minecraft.resources.ResourceKey<Enchantment> enchantmentKey) {
		@SuppressWarnings("null")
		var lookup = player.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
		@SuppressWarnings("null")
		var holder = lookup.getOrThrow(enchantmentKey);
		return EnchantmentHelper.getItemEnchantmentLevel(holder, stack);
	}

	@SuppressWarnings({ "null", "deprecation" })
	private static boolean hasAquaAffinity(Player player) {
		@SuppressWarnings("null")
		var lookup = player.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
		@SuppressWarnings("null")
		var holder = lookup.getOrThrow(Enchantments.AQUA_AFFINITY);
		for (ItemStack armor : player.getArmorSlots()) {
			if (EnchantmentHelper.getItemEnchantmentLevel(holder, armor) > 0) {
				return true;
			}
		}
		return false;
	}
}
