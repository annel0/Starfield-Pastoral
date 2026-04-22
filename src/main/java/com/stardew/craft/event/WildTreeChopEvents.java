package com.stardew.craft.event;

import com.stardew.craft.entity.FallenOakTreeEntity;
import com.stardew.craft.item.ModItems;
import com.stardew.craft.item.tool.StardewAxeItem;
import com.stardew.craft.manager.WildTreeSeedManager;
import com.stardew.craft.tree.WildTrees;
import com.stardew.craft.core.ModDimensions;
import com.stardew.craft.player.PlayerStardewDataAPI;
import com.stardew.craft.player.ProfessionType;
import com.stardew.craft.player.SkillType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class WildTreeChopEvents {
	private WildTreeChopEvents() {
	}

	private static final int MAX_CONNECTED_BLOCKS = 160;
	// Must match the duration of sounds/tree/tree_crack.ogg (in ticks, 20 ticks = 1s).
	private static final int FALL_ANIM_TICKS = 20;
	private static final int MAX_TRUNK1_HEIGHT = 48;
	private static final int LEAF_DECAY_SCHEDULE_RADIUS = 10;
	private static final int MAX_LEAF_BLOCKS = 512;

	// Normalize all axes (except Stardew axes) to SV level-0 feel.
	private static final float BASE_TRUNK0_SPEED = 6.0f;
	// Tuning target: make non-Stardew axes and tier-0 axes feel ~5-6s on a typical tree.
	private static final float OTHER_AXE_DAMAGE_MULTIPLIER = 0.55f;
	private static final int STUMP_HEALTH = 45;
	private static final double HEALTH_DENOM_BASE = 2.2;
	private static final double HEALTH_DENOM_PER = 0.035;
	// Approximate SV swing cadence (0.35s). Used for time->"swings" conversion when charging energy by time.
	private static final int AXE_SWING_TICKS = 7;

	private static final Map<UUID, MiningState> MINING = new ConcurrentHashMap<>();
	private static final Map<UUID, Long> LAST_EXHAUST_WARN_TICK = new ConcurrentHashMap<>();
	private static final Map<UUID, Long> LAST_TRUNK0_HINT_TICK = new ConcurrentHashMap<>();

	public static void removePlayer(UUID playerId) {
		MINING.remove(playerId);
		LAST_EXHAUST_WARN_TICK.remove(playerId);
		LAST_TRUNK0_HINT_TICK.remove(playerId);
	}

	private record MiningState(BlockPos pos, long startTick) {
	}

	@SuppressWarnings("null")
	@SubscribeEvent
	public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
		if (!(event.getEntity() instanceof Player player)) {
			return;
		}
		if (player.isCreative()) {
			return;
		}
		ItemStack tool = player.getItemInHand(InteractionHand.MAIN_HAND);
		if (!isAxeLike(tool)) {
			return;
		}

		BlockPos pos = event.getPosition().orElse(null);
		if (pos == null) {
			return;
		}
		Level level = player.level();
		BlockState state = level.getBlockState(pos);
		WildTrees.Def def = findByTrunk0(state);
		if (def == null) {
			return;
		}
		// If the tree uses stacked trunk0 blocks, always treat the lowest one as the pivot.
		pos = findLowestTrunk0(level, pos, def);

		// Energy gate: only applies in Stardew Valley dimension.
		if (!level.isClientSide
			&& player instanceof ServerPlayer serverPlayer
			&& player.level().dimension() == ModDimensions.STARDEW_VALLEY) {
			if (PlayerStardewDataAPI.getEnergy(serverPlayer) <= 0.0f) {
				// Stop mining progress and show message (rate-limited).
				event.setNewSpeed(0.0f);
				long now = level.getGameTime();
				long last = LAST_EXHAUST_WARN_TICK.getOrDefault(serverPlayer.getUUID(), 0L);
				if (now - last >= 20) {
					serverPlayer.displayClientMessage(net.minecraft.network.chat.Component.translatable("stardewcraft.message.player.exhausted"), true);
					LAST_EXHAUST_WARN_TICK.put(serverPlayer.getUUID(), now);
				}
				return;
			}
		}
		int health = computeTreeHealth(level, pos, def);
		if (health <= 0) {
			// Stump stage (lonely trunk0) should still reflect tier differences.
			if (isLonelyTrunk0(level, pos, def)) {
				health = STUMP_HEALTH;
			} else {
				return;
			}
		}

		// Track mining start for energy calculation on completion.
		if (!level.isClientSide) {
			UUID id = player.getUUID();
			long now = level.getGameTime();
			MiningState prev = MINING.get(id);
			if (prev == null || !prev.pos.equals(pos)) {
				MINING.put(id, new MiningState(pos, now));
			}
		}

		// Make larger trees slower to chop. Non-Stardew axes and tier-0 axes are intentionally slower.
		float damageMul = (float) getStardewAxeDamageMultiplier(tool);
		double denom = HEALTH_DENOM_BASE + (double) health * HEALTH_DENOM_PER;
		float normalized = (float) (BASE_TRUNK0_SPEED * damageMul / Math.max(0.001, denom));
		event.setNewSpeed((float) Math.max(0.01, normalized));
	}

	private static double getStardewAxeDamageMultiplier(ItemStack tool) {
		if (tool.getItem() instanceof StardewAxeItem stardewAxe) {
			return switch (stardewAxe.getStardewTier()) {
				case STARTER -> 0.55;
				case COPPER -> 0.75;
				case STEEL -> 1.0;
				case GOLD -> 1.5;
				case IRIDIUM -> 5.0;
			};
		}
		// Any other axe acts like tier-0 when chopping our wild trees.
		return OTHER_AXE_DAMAGE_MULTIPLIER;
	}

	private static WildTrees.Def findByTrunk0(BlockState state) {
		for (WildTrees.Def def : WildTrees.ALL) {
			if (state.getBlock() == def.trunk0().get()) {
				return def;
			}
		}
		return null;
	}

	private static int computeTreeHealth(Level level, BlockPos trunk0Pos, WildTrees.Def def) {
		TreeParts parts = collectTreeParts(level, trunk0Pos, def);
		if (parts == null) {
			return -1;
		}
		int trunkSegments = Math.max(0, parts.trunkColumn.size() - 1);
		int branches = parts.branches.size();
		int leaves = parts.leaves.size();
		// A valid tree must have at least some height, and either branches or leaves.
		// This also tolerates malformed trees that accidentally use trunk0 as trunk segments.
		if (trunkSegments < 1 || (branches < 1 && leaves < 1)) {
			return -1;
		}
		int leafFactor = Math.min(20, leaves / 8);
		return 10 + trunkSegments * 6 + branches * 4 + leafFactor;
	}

	@SuppressWarnings("null")
	@SubscribeEvent
	public static void onBlockBreak(BlockEvent.BreakEvent event) {
		if (event.isCanceled()) {
			return;
		}
		if (!(event.getPlayer() instanceof ServerPlayer player)) {
			return;
		}
		ServerLevel level = player.serverLevel();
		BlockPos pos = event.getPos();
		@SuppressWarnings("null")
		BlockState state = level.getBlockState(pos);
		WildTrees.Def def = WildTrees.findByAnyPart(state);
		if (def == null) {
			return;
		}

		// Farm protection: in Stardew Valley, non-creative players may only chop trees where they
		// have modify permission. Town/others' farms (without PERM_FULL) are off-limits.
		if (level.dimension() == ModDimensions.STARDEW_VALLEY
				&& !player.isCreative()
				&& !FarmAreaProtectionEvents.canModifyAt(player, pos)) {
			event.setCanceled(true);
			player.displayClientMessage(
					net.minecraft.network.chat.Component.translatable("stardewcraft.farm.build_farm_only"), true);
			return;
		}

		// --- Creative mode: instant tree removal, no drops, no animation ---
		if (player.isCreative()) {
			if (def.isTrunk0(state)) {
				BlockPos pivotPos = findLowestTrunk0(level, pos, def);
				TreeSnapshot snapshot = analyzeTree(level, pivotPos, def);
				if (snapshot != null) {
					for (BlockPos p : snapshot.allPositions) {
						if (p.equals(pos)) continue;
						level.removeBlock(p, false);
					}
					WildTreeSeedManager.get(level).untrackTree(level, pivotPos);
					scheduleNearbyLeafDecay(level, pivotPos, def);
					return;
				}
				WildTreeSeedManager.get(level).untrackTree(level, pos);
				return;
			}
			if (state.getBlock() == def.leaves().get()) {
				return;
			}
			BlockPos pivot = findPivotTrunk0(level, pos, def);
			TreeSnapshot snapshot = pivot == null ? null : analyzeTree(level, pivot, def);
			if (snapshot != null) {
				for (BlockPos p : snapshot.allPositions) {
					if (p.equals(pos)) continue;
					level.removeBlock(p, false);
				}
				WildTreeSeedManager.get(level).untrackTree(level, pivot);
				scheduleNearbyLeafDecay(level, pivot, def);
			}
			return;
		}

		// --- Survival mode ---
		ItemStack tool = player.getItemInHand(InteractionHand.MAIN_HAND);

		// Protect non-leaf, non-trunk0 tree parts regardless of tool.
		if (state.getBlock() != def.leaves().get() && !def.isTrunk0(state)) {
			BlockPos pivot = findPivotTrunk0(level, pos, def);
			TreeSnapshot snapshot = pivot == null ? null : analyzeTree(level, pivot, def);
			if (snapshot != null) {
				long now = level.getGameTime();
				long last = LAST_TRUNK0_HINT_TICK.getOrDefault(player.getUUID(), 0L);
				if (now - last >= 40) {
					player.displayClientMessage(net.minecraft.network.chat.Component.translatable("stardewcraft.message.tree.chop_stump"), true);
					LAST_TRUNK0_HINT_TICK.put(player.getUUID(), now);
				}
				event.setCanceled(true);
				return;
			}
			return;
		}

		// Trunk0 requires an axe.
		if (!isAxeLike(tool)) {
			if (def.isTrunk0(state)) {
				event.setCanceled(true);
			}
			return;
		}

		// Energy gate: only applies in Stardew Valley dimension.
		if (player.level().dimension() == ModDimensions.STARDEW_VALLEY) {
			if (PlayerStardewDataAPI.getEnergy(player) <= 0.0f) {
				player.displayClientMessage(net.minecraft.network.chat.Component.translatable("stardewcraft.message.player.exhausted"), true);
				event.setCanceled(true);
				return;
			}
		}

		// Main rule: only chopping trunk0 can fell the tree.
		if (def.isTrunk0(state)) {
			BlockPos pivotPos = findLowestTrunk0(level, pos, def);
			TreeSnapshot snapshot = analyzeTree(level, pivotPos, def);
			if (snapshot != null) {
				consumeEnergyForTreeChop(player, level, pos);
				java.util.ArrayList<ItemStack> fallDrops = new java.util.ArrayList<>();
				ItemStack seeds = rollSeedsOnChop(level, def, player);
				if (!seeds.isEmpty()) {
					fallDrops.add(seeds);
				}
				fallDrops.add(new ItemStack(ModItems.SAP.get(), 5));
				int dropCount = getFelledWoodDropCount(level, def, player);
				if (dropCount > 0) {
					var woodItem = getWoodDropItem(def);
					fallDrops.add(new ItemStack(woodItem, dropCount));
				}
				int hardwoodBonus = getLumberjackBonusHardwood(level, def, player);
				if (hardwoodBonus > 0) {
					fallDrops.add(new ItemStack(ModItems.WOOD_HARD.get(), hardwoodBonus));
				}
				WildTreeSeedManager.get(level).untrackTree(level, snapshot.pivotTrunk0Pos);
				Direction dir = computeFallDirection(player, snapshot.pivotTrunk0Pos);
				fellTree(level, snapshot, dir, def, fallDrops);
				PlayerStardewDataAPI.addExperience(player, SkillType.FORAGING, 12);
				event.setCanceled(true);
				return;
			}

			if (isLonelyTrunk0(level, pos, def)) {
				consumeEnergyForTreeChop(player, level, pos);
				level.removeBlock(pos, false);
				playStumpEffects(level, pos, def);
				int dropCount = getStumpWoodDropCount(level, def, player);
				if (dropCount > 0) {
					var woodItem = getWoodDropItem(def);
					Block.popResource(level, pos, new ItemStack(woodItem, dropCount));
				}
				int hardwoodBonus = getLumberjackBonusHardwood(level, def, player);
				if (hardwoodBonus > 0) {
					Block.popResource(level, pos, new ItemStack(ModItems.WOOD_HARD.get(), hardwoodBonus));
				}
				PlayerStardewDataAPI.addExperience(player, SkillType.FORAGING, 5);
				WildTreeSeedManager.get(level).untrackTree(level, pos);
				event.setCanceled(true);
				return;
			}
		}
	}


	@SuppressWarnings("null")
	private static boolean isAxeLike(ItemStack tool) {
		if (tool.isEmpty()) {
			return false;
		}
		// Prefer vanilla tag if available.
		if (tool.is(ItemTags.AXES)) {
			return true;
		}
		// Ensure our axes are always recognized even if tag data isn't applied in some dev setups.
		if (tool.getItem() instanceof StardewAxeItem) {
			return true;
		}
		// Recognize any other modded axe-like tool via NeoForge tool ability.
		return tool.canPerformAction(ItemAbilities.AXE_DIG);
	}

	private static void consumeEnergyForTreeChop(ServerPlayer player, ServerLevel level, BlockPos pos) {
		if (player.isCreative()) {
			return;
		}
		if (player.level().dimension() != ModDimensions.STARDEW_VALLEY) {
			return;
		}
		MiningState mining = MINING.remove(player.getUUID());
		if (mining == null || !mining.pos.equals(pos)) {
			return;
		}
		long durationTicks = Math.max(1L, level.getGameTime() - mining.startTick);

		int foraging = PlayerStardewDataAPI.getSkillLevel(player, SkillType.FORAGING);
		float perSwing = 2.0f - (foraging * 0.1f);
		if (perSwing <= 0.0f) {
			return;
		}
		float swings = (float) durationTicks / (float) AXE_SWING_TICKS;
		float cost = perSwing * swings;
		if (cost > 0.0f) {
			PlayerStardewDataAPI.consumeEnergy(player, cost);
		}
	}

	private static ItemStack rollSeedsOnChop(ServerLevel level, WildTrees.Def def, ServerPlayer player) {
		int foragingLevel = com.stardew.craft.player.PlayerStardewDataAPI.getSkillLevel(player, com.stardew.craft.player.SkillType.FORAGING);
		if (foragingLevel < 1) {
			return ItemStack.EMPTY;
		}
		// Stardew 是 data.SeedOnChopChance（数据驱动）；此处用 0.2 作为接近原版体感的默认值。
		if (level.random.nextFloat() >= 0.20f) {
			return ItemStack.EMPTY;
		}
		var seedItem = WildTreeSeedManager.getSeedItem(def);
		if (seedItem == null) {
			return ItemStack.EMPTY;
		}
		@SuppressWarnings("null")
		int count = Mth.nextInt(level.random, 1, 2);
		return new ItemStack(seedItem, count);
	}

	private static net.minecraft.world.item.Item getWoodDropItem(WildTrees.Def def) {
		// Stardew-like: Mahogany and Mystic Tree drop Hardwood.
		String id = def.id();
		if ("mahogany".equals(id) || "mystic_tree".equals(id)) {
			return ModItems.WOOD_HARD.get();
		}
		return ModItems.WOOD_NORMAL.get();
	}

	@SuppressWarnings("null")
	private static int getFelledWoodDropCount(ServerLevel level, WildTrees.Def def, ServerPlayer player) {
		// Match Stardew Valley ranges (approx):
		// - Normal trees: 12-16 Wood
		// - Mahogany: 8-13 Hardwood
		// - Mystic Tree: 7-11 Hardwood
		String id = def.id();
		int base = switch (id) {
			case "mahogany" -> Mth.nextInt(level.random, 8, 13);
			case "mystic_tree" -> Mth.nextInt(level.random, 7, 11);
			default -> Mth.nextInt(level.random, 12, 16);
		};
		if (!isHardwoodTree(def) && PlayerStardewDataAPI.hasProfession(player, ProfessionType.FORESTER)) {
			base = Math.max(1, Mth.floor(base * 1.25f));
		}
		return base;
	}

	@SuppressWarnings("null")
	private static int getStumpWoodDropCount(ServerLevel level, WildTrees.Def def, ServerPlayer player) {
		// Match Stardew Valley stump behavior (approx):
		// - Normal stumps: 5-9 Wood
		// - Mystic stump: +1 Hardwood
		// - Mahogany stump: treat as 1-3 Hardwood (close enough; exact rules vary by version)
		String id = def.id();
		int base = switch (id) {
			case "mystic_tree" -> 1;
			case "mahogany" -> Mth.nextInt(level.random, 1, 3);
			default -> Mth.nextInt(level.random, 5, 9);
		};
		if (!isHardwoodTree(def) && PlayerStardewDataAPI.hasProfession(player, ProfessionType.FORESTER)) {
			base = Math.max(1, Mth.floor(base * 1.25f));
		}
		return base;
	}

	private static boolean isHardwoodTree(WildTrees.Def def) {
		String id = def.id();
		return "mahogany".equals(id) || "mystic_tree".equals(id);
	}

	private static int getLumberjackBonusHardwood(ServerLevel level, WildTrees.Def def, ServerPlayer player) {
		if (isHardwoodTree(def)) {
			return 0;
		}
		if (!PlayerStardewDataAPI.hasProfession(player, ProfessionType.LUMBERJACK)) {
			return 0;
		}
		return level.random.nextFloat() < 0.25f ? 1 : 0;
	}

	private static Direction computeFallDirection(ServerPlayer player, BlockPos rootPos) {
		int dx = rootPos.getX() - player.blockPosition().getX();
		int dz = rootPos.getZ() - player.blockPosition().getZ();
		if (Math.abs(dx) >= Math.abs(dz)) {
			return dx >= 0 ? Direction.EAST : Direction.WEST;
		}
		return dz >= 0 ? Direction.SOUTH : Direction.NORTH;
	}

	@SuppressWarnings("null")
	private static TreeParts collectTreeParts(Level level, BlockPos trunk0Pos, WildTrees.Def def) {
		@SuppressWarnings("null")
		BlockState base = level.getBlockState(trunk0Pos);
		if (base.getBlock() != def.trunk0().get()) {
			return null;
		}

		Set<BlockPos> trunkColumn = new HashSet<>();
		trunkColumn.add(trunk0Pos.immutable());
		for (int i = 1; i <= MAX_TRUNK1_HEIGHT; i++) {
			BlockPos p = trunk0Pos.above(i);
			@SuppressWarnings("null")
			Block b = level.getBlockState(p).getBlock();
			// Tolerate malformed trees that accidentally use trunk0 as a trunk segment.
			if (b == def.trunk1().get() || b == def.trunk0().get()) {
				trunkColumn.add(p.immutable());
				continue;
			}
			break;
		}

		Set<BlockPos> branches = new HashSet<>();
		for (BlockPos tp : trunkColumn) {
			for (Direction d : Direction.Plane.HORIZONTAL) {
				@SuppressWarnings("null")
				BlockPos bp = tp.relative(d);
				@SuppressWarnings("null")
				Block b = level.getBlockState(bp).getBlock();
				if (b == def.branch1().get() || b == def.branch2().get()) {
					branches.add(bp.immutable());
				}
			}
		}

		// Collect leaves that belong to this tree, without flood-filling through neighbor trunks.
		Block leavesBlock = def.leaves().get();
		Set<BlockPos> wood = new HashSet<>();
		wood.addAll(trunkColumn);
		wood.addAll(branches);
		Set<BlockPos> leaves = new HashSet<>();
		ArrayDeque<BlockPos> q = new ArrayDeque<>();
		Set<BlockPos> visited = new HashSet<>();
		for (BlockPos wp : wood) {
			for (Direction d : Direction.values()) {
				@SuppressWarnings("null")
				BlockPos lp = wp.relative(d);
				if (level.getBlockState(lp).getBlock() == leavesBlock && visited.add(lp)) {
					q.add(lp);
				}
			}
		}
		while (!q.isEmpty() && leaves.size() < MAX_LEAF_BLOCKS) {
			BlockPos lp = q.removeFirst();
			if (isLeafClaimedByOtherWood(level, lp, wood, def)) {
				continue;
			}
			leaves.add(lp.immutable());
			for (Direction d : Direction.values()) {
				@SuppressWarnings("null")
				BlockPos np = lp.relative(d);
				if (!visited.add(np)) {
					continue;
				}
				if (level.getBlockState(np).getBlock() == leavesBlock) {
					q.add(np);
				}
			}
		}

		// Safety: if trunk column is absurdly large, treat as invalid.
		if (trunkColumn.size() > MAX_CONNECTED_BLOCKS) {
			return null;
		}
		return new TreeParts(trunk0Pos.immutable(), trunkColumn, branches, leaves);
	}

	private static boolean isLeafClaimedByOtherWood(Level level, BlockPos leafPos, Set<BlockPos> wood, WildTrees.Def def) {
		Block trunk0 = def.trunk0().get();
		Block trunk1 = def.trunk1().get();
		Block branch1 = def.branch1().get();
		Block branch2 = def.branch2().get();
		for (Direction d : Direction.values()) {
			@SuppressWarnings("null")
			BlockPos np = leafPos.relative(d);
			@SuppressWarnings("null")
			Block b = level.getBlockState(np).getBlock();
			if (b == trunk0 || b == trunk1 || b == branch1 || b == branch2) {
				if (!wood.contains(np)) {
					return true;
				}
			}
		}
		return false;
	}

	private static void fellTree(ServerLevel level, TreeSnapshot snapshot, Direction direction, WildTrees.Def def, java.util.List<ItemStack> fallDrops) {
		BlockPos pivot = snapshot.pivotTrunk0Pos;
		// Remove the tree's parts (wood + leaves). Keep trunk0 as a stump.
		for (BlockPos pos : snapshot.allPositions) {
			@SuppressWarnings("null")
			BlockState state = level.getBlockState(pos);
			Block b = state.getBlock();
			if (!(b == def.trunk0().get() || b == def.trunk1().get() || b == def.branch1().get() || b == def.branch2().get() || b == def.leaves().get())) {
				continue;
			}
			if (pos.equals(pivot)) {
				continue;
			}
			level.removeBlock(pos, false);
		}

		// Leaves -> seed drops. Keep them aligned with impact timing by adding to fallDrops.
		var seedItem = WildTreeSeedManager.getSeedItem(def);
		if (seedItem != null) {
			int leafCount = snapshot.leavesCount;
			int rolls = Math.max(1, leafCount / 12);
			int total = 0;
			for (int i = 0; i < rolls; i++) {
				if (level.random.nextFloat() < 0.20f) {
					total += 1 + level.random.nextInt(2);
				}
			}
			if (total > 0) {
				fallDrops.add(new ItemStack(seedItem, total));
			}
		}

		// Make leaf decay happen quickly by scheduling ticks for nearby leaves.
		scheduleNearbyLeafDecay(level, pivot, def);

		// Drops are handled when the fall animation completes (entity impact).
		FallenOakTreeEntity.spawn(level, pivot, direction, snapshot.fallPieces, FALL_ANIM_TICKS, fallDrops);
	}

	@SuppressWarnings({ "null", "deprecation" })
	private static void playStumpEffects(ServerLevel level, BlockPos pos, WildTrees.Def def) {
		BlockState dustState = def.trunk1().get().defaultBlockState();
		var breakSound = dustState.getSoundType().getBreakSound();
		level.playSound(null, pos, breakSound, net.minecraft.sounds.SoundSource.BLOCKS, 0.8F, 1.1F);
		level.sendParticles(
				new net.minecraft.core.particles.BlockParticleOption(net.minecraft.core.particles.ParticleTypes.BLOCK, dustState),
				pos.getX() + 0.5,
				pos.getY() + 0.5,
				pos.getZ() + 0.5,
				18,
				0.35,
				0.35,
				0.35,
				0.05
		);
	}

	@SuppressWarnings("null")
	private static boolean isLonelyTrunk0(Level level, BlockPos pos, WildTrees.Def def) {
		if (level.getBlockState(pos).getBlock() != def.trunk0().get()) {
			return false;
		}
		// Stump stage: trunk0 only.
		@SuppressWarnings("null")
		Block above = level.getBlockState(pos.above()).getBlock();
		if (above == def.trunk1().get() || above == def.trunk0().get()) {
			return false;
		}
		for (Direction d : Direction.Plane.HORIZONTAL) {
			@SuppressWarnings("null")
			Block b = level.getBlockState(pos.relative(d)).getBlock();
			if (b == def.branch1().get() || b == def.branch2().get()) {
				return false;
			}
		}
		return true;
	}

	@SuppressWarnings("null")
	private static TreeSnapshot analyzeTree(ServerLevel level, BlockPos trunk0Pos, WildTrees.Def def) {
		TreeParts parts = collectTreeParts(level, trunk0Pos, def);
		if (parts == null) {
			return null;
		}
		int trunkSegments = Math.max(0, parts.trunkColumn.size() - 1);
		int branches = parts.branches.size();
		int leaves = parts.leaves.size();
		// Definition: must have at least some height, and either branches or leaves.
		if (trunkSegments < 1 || (branches < 1 && leaves < 1)) {
			return null;
		}

		int leafFactor = Math.min(20, leaves / 8);
		int health = 10 + trunkSegments * 6 + branches * 4 + leafFactor;
		int woodDrop = Math.max(1, trunkSegments * 2 + branches);

		Set<BlockPos> all = new HashSet<>();
		all.addAll(parts.trunkColumn);
		all.addAll(parts.branches);
		all.addAll(parts.leaves);

		var fallPieces = new java.util.ArrayList<FallenOakTreeEntity.Piece>();
		for (BlockPos pos : all) {
			if (pos.equals(trunk0Pos)) {
				continue;
			}
			BlockState s = level.getBlockState(pos);
			Block b = s.getBlock();
			// Tolerate malformed trees that accidentally use trunk0 as trunk segments.
			if (!(b == def.trunk0().get() || b == def.trunk1().get() || b == def.branch1().get() || b == def.branch2().get())) {
				continue;
			}
			int dx = pos.getX() - trunk0Pos.getX();
			int dy = pos.getY() - trunk0Pos.getY();
			int dz = pos.getZ() - trunk0Pos.getZ();

			int blockId = BuiltInRegistries.BLOCK.getId(b);
			int facing2d = 0;
			if (s.hasProperty(net.minecraft.world.level.block.HorizontalDirectionalBlock.FACING)) {
				facing2d = s.getValue(net.minecraft.world.level.block.HorizontalDirectionalBlock.FACING).get2DDataValue();
			} else if (s.hasProperty(net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING)) {
				@SuppressWarnings("null")
				var dir = s.getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING);
				if (dir.getAxis().isHorizontal()) {
					facing2d = dir.get2DDataValue();
				}
			}
			fallPieces.add(new FallenOakTreeEntity.Piece(dx, dy, dz, blockId, facing2d));
		}

		return new TreeSnapshot(trunk0Pos, all, fallPieces, health, woodDrop, parts.leaves.size());
	}

	private static BlockPos findPivotTrunk0(ServerLevel level, BlockPos pos, WildTrees.Def def) {
		@SuppressWarnings("null")
		BlockState s = level.getBlockState(pos);
		if (s.getBlock() == def.trunk0().get()) {
			return findLowestTrunk0(level, pos, def);
		}
		// If this is trunk1, the pivot is directly below along the column.
		if (s.getBlock() == def.trunk1().get()) {
			BlockPos p = pos;
			for (int i = 0; i <= MAX_TRUNK1_HEIGHT; i++) {
				p = p.below();
				@SuppressWarnings("null")
				Block b = level.getBlockState(p).getBlock();
				if (b == def.trunk0().get()) {
					return findLowestTrunk0(level, p, def);
				}
				if (b != def.trunk1().get() && b != def.trunk0().get()) {
					break;
				}
			}
			return null;
		}
		// If this is a branch or leaves, try to find an adjacent trunk1/trunk0 and then walk down.
		for (Direction d : Direction.values()) {
			@SuppressWarnings("null")
			BlockPos np = pos.relative(d);
			@SuppressWarnings("null")
			Block nb = level.getBlockState(np).getBlock();
			if (nb == def.trunk0().get()) {
				return findLowestTrunk0(level, np, def);
			}
			if (nb == def.trunk1().get()) {
				return findPivotTrunk0(level, np, def);
			}
		}
		return null;
	}

	@SuppressWarnings("null")
	private static BlockPos findLowestTrunk0(Level level, BlockPos trunk0Pos, WildTrees.Def def) {
		BlockPos p = trunk0Pos;
		for (int i = 0; i < MAX_TRUNK1_HEIGHT; i++) {
			BlockPos below = p.below();
			if (level.getBlockState(below).getBlock() == def.trunk0().get()) {
				p = below;
				continue;
			}
			break;
		}
		return p;
	}

	@SuppressWarnings("null")
	private static void scheduleNearbyLeafDecay(ServerLevel level, BlockPos pivot, WildTrees.Def def) {
		Block leaves = def.leaves().get();
		Block trunk0 = def.trunk0().get();
		Block trunk1 = def.trunk1().get();
		Block branch1 = def.branch1().get();
		Block branch2 = def.branch2().get();
		int r = LEAF_DECAY_SCHEDULE_RADIUS;
		for (int dx = -r; dx <= r; dx++) {
			for (int dy = -r; dy <= r; dy++) {
				for (int dz = -r; dz <= r; dz++) {
					BlockPos p = pivot.offset(dx, dy, dz);
					if (level.getBlockState(p).getBlock() != leaves) {
						continue;
					}
					// Only decay leaves that are not adjacent to any living wood block.
					// Leaves next to a trunk/branch belong to a surviving tree.
					if (isAdjacentToWood(level, p, trunk0, trunk1, branch1, branch2)) {
						continue;
					}
					level.scheduleTick(p, leaves, 2 + level.random.nextInt(6));
				}
			}
		}
	}

	@SuppressWarnings("null")
	private static boolean isAdjacentToWood(ServerLevel level, BlockPos leafPos,
			Block trunk0, Block trunk1, Block branch1, Block branch2) {
		for (Direction d : Direction.values()) {
			Block b = level.getBlockState(leafPos.relative(d)).getBlock();
			if (b == trunk0 || b == trunk1 || b == branch1 || b == branch2) {
				return true;
			}
		}
		return false;
	}

	private record TreeParts(BlockPos pivotTrunk0, Set<BlockPos> trunkColumn, Set<BlockPos> branches, Set<BlockPos> leaves) {
	}

	private static final class TreeSnapshot {
		final BlockPos pivotTrunk0Pos;
		final Set<BlockPos> allPositions;
		final java.util.List<FallenOakTreeEntity.Piece> fallPieces;
		final int leavesCount;

		TreeSnapshot(BlockPos pivotTrunk0Pos, Set<BlockPos> allPositions, java.util.List<FallenOakTreeEntity.Piece> fallPieces, int health, int woodDrop, int leavesCount) {
			this.pivotTrunk0Pos = pivotTrunk0Pos;
			this.allPositions = allPositions;
			this.fallPieces = fallPieces;
			this.leavesCount = leavesCount;
		}
	}

}
