package com.stardew.craft.tree.generation;

import com.stardew.craft.block.tree.WildTreeSaplingBlock;
import com.stardew.craft.blockentity.NewTreePartBlockEntity;
import com.stardew.craft.tree.WildTrees;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import javax.annotation.Nonnull;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class StardewTreeGenerator {
	private static final int MAX_TREE_BLOCKS = 420;
	private static final int GENERATION_ATTEMPTS = 6;
	private static final Direction[] HORIZONTALS = {
			Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST
	};

	private StardewTreeGenerator() {
	}

	public static boolean canGenerate(@Nonnull ServerLevel level, @Nonnull BlockPos origin, @Nonnull WildTrees.Def def) {
		long seed = stableSeed(origin, def);
		for (int attempt = 0; attempt < GENERATION_ATTEMPTS; attempt++) {
			TreePlan plan = createPlan(origin, def, RandomSource.create(seed + attempt * 0x9E3779B97F4A7C15L));
			if (canPlace(level, origin, plan)) {
				return true;
			}
		}
		return false;
	}

	public static boolean tryGenerate(@Nonnull ServerLevel level, @Nonnull BlockPos origin, @Nonnull WildTrees.Def def, @Nonnull RandomSource random) {
		long seed = stableSeed(origin, def);
		for (int attempt = 0; attempt < GENERATION_ATTEMPTS; attempt++) {
			TreePlan plan = createPlan(origin, def, RandomSource.create(seed + attempt * 0x9E3779B97F4A7C15L));
			if (!canPlace(level, origin, plan)) {
				continue;
			}
			place(level, plan);
			return true;
		}
		return false;
	}

	private static long stableSeed(BlockPos origin, WildTrees.Def def) {
		return origin.asLong() ^ ((long) def.id().hashCode() << 32) ^ 0x6A09E667F3BCC909L;
	}

	private static StardewTreeProfile profile(WildTrees.Def def) {
		return switch (def.id()) {
			case "maple" -> new StardewTreeProfile(def.id(), 6, 8, 3, 0.08F, 0.18F);
			case "pine" -> new StardewTreeProfile(def.id(), 7, 9, 3, 0.01F, 0.05F);
			case "mahogany" -> new StardewTreeProfile(def.id(), 7, 9, 3, 0.12F, 0.15F);
			case "mystic_tree" -> new StardewTreeProfile(def.id(), 7, 9, 3, 0.22F, 0.36F);
			default -> new StardewTreeProfile(def.id(), 5, 7, 2, 0.09F, 0.12F);
		};
	}

	private static TreePlan createPlan(BlockPos origin, WildTrees.Def def, RandomSource random) {
		StardewTreeProfile profile = profile(def);
		int height = profile.minHeight() + random.nextInt(profile.heightRange() + 1);
		TreePlan plan = new TreePlan(origin, def);
		addRoot(plan, origin, def);
		switch (def.id()) {
			case "maple" -> buildMaple(plan, origin, def, profile, height, random);
			case "pine" -> buildPine(plan, origin, def, profile, height, random);
			case "mahogany" -> buildMahogany(plan, origin, def, profile, height, random);
			case "mystic_tree" -> buildMystic(plan, origin, def, profile, height, random);
			default -> buildOak(plan, origin, def, profile, height, random);
		}
		pruneDisconnectedWood(plan);
		pruneUnsupportedLeaves(plan);
		return plan;
	}

	private static void buildOak(TreePlan plan, BlockPos origin, WildTrees.Def def, StardewTreeProfile profile, int height, RandomSource random) {
		addTrunkColumn(plan, origin, def, 1, height - 1);
		BlockPos crown = origin.above(height);
		addBranch(plan, crown, def);

		Direction side = randomHorizontal(random);
		Direction counterSide = random.nextBoolean() ? side.getClockWise() : side.getCounterClockWise();
		BlockPos sideLog = origin.above(Math.max(3, height - 2)).relative(side);
		BlockPos lowSideLog = origin.above(Math.max(2, height - 3)).relative(counterSide);
		addLog(plan, sideLog, def, side.getAxis());
		addLog(plan, lowSideLog, def, counterSide.getAxis());

		addLayeredCanopy(plan, crown.above(), def, profile, random,
				new Layer(-2, 2, 2, 0.18F),
				new Layer(-1, 3, 2, 0.10F),
				new Layer(0, 3, 3, 0.08F),
				new Layer(1, 2, 2, 0.24F),
				new Layer(2, 1, 1, 0.30F));
		addLeafDisc(plan, sideLog.above(), def, 2, 1, true, 0.36F, profile.leafHoleChance(), random);
		addLeafDisc(plan, lowSideLog.above(), def, 1, 2, true, 0.34F, profile.leafHoleChance(), random);
		addLeafTuft(plan, crown.above(3), def, random);
		addHangingLeaves(plan, crown.below(2), def, 2, profile.hangingLeafChance(), random);
	}

	private static void buildMaple(TreePlan plan, BlockPos origin, WildTrees.Def def, StardewTreeProfile profile, int height, RandomSource random) {
		addTrunkColumn(plan, origin, def, 1, height - 2);
		BlockPos fork = origin.above(height - 1);
		addBranch(plan, fork, def);
		addLog(plan, fork.above(), def, Direction.Axis.Y);

		Direction primary = randomHorizontal(random);
		Direction secondary = random.nextBoolean() ? primary.getClockWise() : primary.getCounterClockWise();
		BlockPos primaryTip = addHorizontalRun(plan, fork, def, primary, 2, true);
		BlockPos secondaryTip = addHorizontalRun(plan, fork.above(), def, secondary, 2, false);

		addLayeredCanopy(plan, fork.above(2), def, profile, random,
				new Layer(-2, 3, 2, 0.18F),
				new Layer(-1, 3, 3, 0.08F),
				new Layer(0, 3, 3, 0.06F),
				new Layer(1, 3, 2, 0.18F),
				new Layer(2, 2, 1, 0.32F));
		addLeafDisc(plan, primaryTip.above(), def, 1, 1, true, 0.18F, profile.leafHoleChance(), random);
		addLeafDisc(plan, primaryTip.above(2), def, 2, 2, true, 0.22F, profile.leafHoleChance(), random);
		addLeafDisc(plan, secondaryTip.above(1), def, 2, 1, true, 0.30F, profile.leafHoleChance(), random);
		addLeafDisc(plan, fork.above(), def, 3, 1, true, 0.26F, profile.leafHoleChance(), random);
		addLeafTuft(plan, fork.above(4).relative(primary), def, random);
		addLeafTuft(plan, fork.above(3).relative(secondary), def, random);
		addHangingLeaves(plan, fork, def, 3, profile.hangingLeafChance(), random);
	}

	private static void buildPine(TreePlan plan, BlockPos origin, WildTrees.Def def, StardewTreeProfile profile, int height, RandomSource random) {
		addTrunkColumn(plan, origin, def, 1, height);
		addBranch(plan, origin.above(height), def);
		addBranch(plan, origin.above(height - 3), def);

		int crownStart = Math.max(2, height - 7);
		for (int y = crownStart; y <= height + 1; y++) {
			int fromTop = height + 1 - y;
			int radius = switch (fromTop) {
				case 0 -> 0;
				case 1 -> 1;
				case 2, 3, 6 -> 2;
				case 4, 5 -> 3;
				default -> 1;
			};
			float edgeDrop = y % 2 == 0 ? 0.06F : 0.14F;
			if (radius >= 2 && y <= height - 1) {
				addPineWhorl(plan, origin.above(y), def, Math.min(2, radius - 1), random);
			}
			addLeafDisc(plan, origin.above(y), def, radius, radius, true, edgeDrop, profile.leafHoleChance(), random);
			if (radius >= 2 && y % 2 == 1) {
				Direction d = HORIZONTALS[Math.floorMod(y + random.nextInt(4), 4)];
				addLeaves(plan, origin.above(y).relative(d, radius + 1), def);
			}
		}
		addLeafDisc(plan, origin.above(crownStart - 1), def, 1, 1, true, 0.18F, 0.0F, random);
		addLeaves(plan, origin.above(height + 2), def);
	}

	private static void buildMahogany(TreePlan plan, BlockPos origin, WildTrees.Def def, StardewTreeProfile profile, int height, RandomSource random) {
		int forkY = height - 3 + random.nextInt(2);
		addTrunkColumn(plan, origin, def, 1, forkY);
		addBranch(plan, origin.above(forkY), def);
		addLog(plan, origin.above(forkY + 1), def, Direction.Axis.Y);

		Direction primary = randomHorizontal(random);
		Direction secondary = primary.getClockWise();
		Direction tertiary = primary.getCounterClockWise();
		BlockPos a = addRisingFork(plan, origin.above(forkY), def, primary, 2, random);
		BlockPos b = addRisingFork(plan, origin.above(forkY + 1), def, secondary, 2, random);
		BlockPos c = addRisingFork(plan, origin.above(forkY), def, tertiary, 1 + random.nextInt(2), random);

		addLeafCluster(plan, a.above(2), def, profile, 2, random);
		addLeafCluster(plan, b.above(1 + random.nextInt(2)), def, profile, 2, random);
		addLeafCluster(plan, c.above(2), def, profile, 1 + random.nextInt(2), random);
		addLeafDisc(plan, origin.above(height - 1), def, 2, 1, true, 0.20F, profile.leafHoleChance(), random);
		addLeafDisc(plan, origin.above(height), def, 2, 2, true, 0.24F, profile.leafHoleChance(), random);
		addLeafDisc(plan, origin.above(height + 1).relative(primary), def, 2, 1, true, 0.38F, profile.leafHoleChance(), random);
		addLeafTuft(plan, origin.above(height + 2).relative(primary), def, random);
		addLeafTuft(plan, b.above(3), def, random);
		addHangingLeaves(plan, origin.above(height - 2), def, 3, profile.hangingLeafChance(), random);
	}

	private static void buildMystic(TreePlan plan, BlockPos origin, WildTrees.Def def, StardewTreeProfile profile, int height, RandomSource random) {
		int forkY = height - 3 + random.nextInt(2);
		addTrunkColumn(plan, origin, def, 1, forkY);
		addBranch(plan, origin.above(forkY), def);
		addLog(plan, origin.above(forkY + 1), def, Direction.Axis.Y);

		Direction primary = randomHorizontal(random);
		Direction opposite = primary.getOpposite();
		BlockPos a = addRisingFork(plan, origin.above(forkY), def, primary, 2, random);
		BlockPos b = addRisingFork(plan, origin.above(forkY + 1), def, opposite, 1 + random.nextInt(2), random);
		BlockPos highCrown = origin.above(height + 1);

		addLeafCluster(plan, a.above(2), def, profile, 2, random);
		addLeafCluster(plan, b.above(1), def, profile, 2, random);
		addLeafDisc(plan, highCrown.below(2), def, 1, 1, true, 0.25F, profile.leafHoleChance(), random);
		addLeafDisc(plan, highCrown.below(), def, 1, 1, true, 0.32F, profile.leafHoleChance(), random);
		addLeafDisc(plan, highCrown, def, 2, 2, true, 0.42F, profile.leafHoleChance(), random);
		addLeafDisc(plan, highCrown.relative(primary.getClockWise()), def, 2, 1, true, 0.52F, profile.leafHoleChance(), random);
		addHangingLeaves(plan, highCrown.below(2), def, 3, profile.hangingLeafChance(), random);
		addHangingLeaves(plan, a, def, 2, profile.hangingLeafChance() * 0.75F, random);
		addLeafTuft(plan, highCrown.above().relative(primary), def, random);
		addMysticTendrils(plan, highCrown.below(), def, random);
	}

	private static void addLeafTuft(TreePlan plan, BlockPos center, WildTrees.Def def, RandomSource random) {
		addLeaves(plan, center, def);
		if (random.nextFloat() < 0.60F) {
			addLeaves(plan, center.above(), def);
		}
		Direction first = randomHorizontal(random);
		addLeaves(plan, center.relative(first), def);
		if (random.nextFloat() < 0.50F) {
			addLeaves(plan, center.relative(random.nextBoolean() ? first.getClockWise() : first.getCounterClockWise()), def);
		}
	}

	private static void addPineWhorl(TreePlan plan, BlockPos center, WildTrees.Def def, int length, RandomSource random) {
		for (Direction direction : HORIZONTALS) {
			if (random.nextFloat() < 0.18F) {
				continue;
			}
			int armLength = length + (random.nextFloat() < 0.25F ? 1 : 0);
			BlockPos tip = center;
			for (int i = 1; i <= armLength; i++) {
				tip = tip.relative(direction);
				addLog(plan, tip, def, direction.getAxis());
				addLeaves(plan, tip.above(), def);
				if (i == armLength || random.nextFloat() < 0.55F) {
					addLeaves(plan, tip.below(), def);
				}
				if (i == armLength) {
					addLeaves(plan, tip.relative(direction), def);
					addLeaves(plan, tip.relative(direction.getClockWise()), def);
					addLeaves(plan, tip.relative(direction.getCounterClockWise()), def);
				}
			}
		}
	}

	private static void addMysticTendrils(TreePlan plan, BlockPos center, WildTrees.Def def, RandomSource random) {
		for (Direction direction : HORIZONTALS) {
			if (random.nextFloat() < 0.35F) {
				continue;
			}
			BlockPos start = center.relative(direction, 1 + random.nextInt(2));
			int length = 2 + random.nextInt(2);
			for (int i = 0; i < length; i++) {
				addLeaves(plan, start.below(i), def);
			}
		}
	}

	private static BlockPos addHorizontalRun(TreePlan plan, BlockPos start, WildTrees.Def def, Direction direction, int length, boolean branchAtEnd) {
		BlockPos p = start;
		for (int i = 1; i <= length; i++) {
			p = p.relative(direction);
			addLog(plan, p, def, direction.getAxis());
		}
		if (branchAtEnd) {
			addBranch(plan, p.above(), def);
			return p.above();
		}
		return p;
	}

	private static BlockPos addRisingFork(TreePlan plan, BlockPos start, WildTrees.Def def, Direction direction, int length, RandomSource random) {
		BlockPos p = start;
		for (int i = 1; i <= length; i++) {
			p = p.relative(direction);
			addLog(plan, p, def, direction.getAxis());
			if (i == length || random.nextFloat() < 0.45F) {
				p = p.above();
				addLog(plan, p, def, Direction.Axis.Y);
			}
		}
		addBranch(plan, p.above(), def);
		return p.above();
	}

	private static void addTrunkColumn(TreePlan plan, BlockPos origin, WildTrees.Def def, int minY, int maxY) {
		for (int y = minY; y <= maxY; y++) {
			addLog(plan, origin.above(y), def, Direction.Axis.Y);
		}
	}

	private static void addLayeredCanopy(TreePlan plan, BlockPos center, WildTrees.Def def, StardewTreeProfile profile, RandomSource random, Layer... layers) {
		for (Layer layer : layers) {
			addLeafDisc(plan, center.above(layer.yOffset()), def, layer.radiusX(), layer.radiusZ(), true, layer.edgeDropChance(), profile.leafHoleChance(), random);
		}
	}

	private static void addLeafCluster(TreePlan plan, BlockPos center, WildTrees.Def def, StardewTreeProfile profile, int radius, RandomSource random) {
		addLayeredCanopy(plan, center, def, profile, random,
				new Layer(-1, radius, radius, 0.28F),
				new Layer(0, radius + 1, radius, 0.18F),
				new Layer(1, radius, radius, 0.32F));
	}

	private static void addLeafDisc(TreePlan plan, BlockPos center, WildTrees.Def def, int radiusX, int radiusZ, boolean rounded, float edgeDropChance, float holeChance, RandomSource random) {
		if (radiusX <= 0 && radiusZ <= 0) {
			addLeaves(plan, center, def);
			return;
		}
		double rx = Math.max(0.75D, radiusX + 0.35D);
		double rz = Math.max(0.75D, radiusZ + 0.35D);
		for (int dx = -radiusX; dx <= radiusX; dx++) {
			for (int dz = -radiusZ; dz <= radiusZ; dz++) {
				double normalized = (dx * dx) / (rx * rx) + (dz * dz) / (rz * rz);
				if (rounded && normalized > 1.08D) {
					continue;
				}
				if (normalized > 0.72D && random.nextFloat() < edgeDropChance) {
					continue;
				}
				if (Math.abs(dx) + Math.abs(dz) > 1 && random.nextFloat() < holeChance) {
					continue;
				}
				addLeaves(plan, center.offset(dx, 0, dz), def);
			}
		}
	}

	private static void addHangingLeaves(TreePlan plan, BlockPos center, WildTrees.Def def, int radius, float chance, RandomSource random) {
		for (int dx = -radius; dx <= radius; dx++) {
			for (int dz = -radius; dz <= radius; dz++) {
				if (Math.abs(dx) + Math.abs(dz) > radius + 1 || random.nextFloat() >= chance) {
					continue;
				}
				addLeaves(plan, center.offset(dx, 0, dz), def);
			}
		}
	}

	private static void pruneDisconnectedWood(TreePlan plan) {
		Set<BlockPos> connected = new HashSet<>();
		ArrayDeque<BlockPos> queue = new ArrayDeque<>();
		connected.add(plan.root);
		queue.add(plan.root);
		while (!queue.isEmpty()) {
			BlockPos pos = queue.removeFirst();
			for (Direction direction : Direction.values()) {
				BlockPos neighbor = pos.relative(direction);
				if (!plan.wood.containsKey(neighbor) || connected.contains(neighbor)) {
					continue;
				}
				connected.add(neighbor);
				queue.add(neighbor);
			}
		}
		plan.wood.keySet().retainAll(connected);
	}

	private static void pruneUnsupportedLeaves(TreePlan plan) {
		Set<BlockPos> supported = new HashSet<>();
		ArrayDeque<BlockPos> queue = new ArrayDeque<>();
		for (BlockPos woodPos : plan.wood.keySet()) {
			for (Direction direction : Direction.values()) {
				BlockPos leafPos = woodPos.relative(direction);
				if (plan.leaves.contains(leafPos) && supported.add(leafPos)) {
					queue.add(leafPos);
				}
			}
		}
		Map<BlockPos, Integer> distances = new HashMap<>();
		for (BlockPos leafPos : supported) {
			distances.put(leafPos, 1);
		}
		while (!queue.isEmpty()) {
			BlockPos pos = queue.removeFirst();
			int nextDistance = distances.get(pos) + 1;
			if (nextDistance > 6) {
				continue;
			}
			for (Direction direction : Direction.values()) {
				BlockPos neighbor = pos.relative(direction);
				if (!plan.leaves.contains(neighbor) || supported.contains(neighbor)) {
					continue;
				}
				supported.add(neighbor);
				distances.put(neighbor, nextDistance);
				queue.add(neighbor);
			}
		}
		plan.leaves.retainAll(supported);
	}

	private static void addRoot(TreePlan plan, BlockPos pos, WildTrees.Def def) {
		plan.wood.put(pos.immutable(), def.modernRoot().get().defaultBlockState());
	}

	private static void addLog(TreePlan plan, BlockPos pos, WildTrees.Def def, Direction.Axis axis) {
		BlockState state = def.modernLog().get().defaultBlockState();
		if (state.hasProperty(RotatedPillarBlock.AXIS)) {
			state = state.setValue(RotatedPillarBlock.AXIS, axis);
		}
		plan.wood.put(pos.immutable(), state);
	}

	private static void addBranch(TreePlan plan, BlockPos pos, WildTrees.Def def) {
		plan.wood.put(pos.immutable(), def.modernBranch().get().defaultBlockState());
	}

	private static void addLeaves(TreePlan plan, BlockPos pos, WildTrees.Def def) {
		if (plan.wood.containsKey(pos)) {
			return;
		}
		plan.leaves.add(pos.immutable());
	}

	private static Direction randomHorizontal(RandomSource random) {
		return HORIZONTALS[random.nextInt(HORIZONTALS.length)];
	}

	private static boolean canPlace(ServerLevel level, BlockPos origin, TreePlan plan) {
		if (!hasRequiredShape(plan)) {
			return false;
		}
		if (plan.wood.size() + plan.leaves.size() > MAX_TREE_BLOCKS) {
			return false;
		}
		if (!hasRootClearance(level, origin)) {
			return false;
		}
		for (BlockPos pos : plan.wood.keySet()) {
			if (!canReplace(level, origin, pos, false)) {
				return false;
			}
		}
		for (BlockPos pos : plan.leaves) {
			if (plan.wood.containsKey(pos)) {
				continue;
			}
			if (!canReplace(level, origin, pos, true)) {
				return false;
			}
		}
		return true;
	}

	private static boolean hasRequiredShape(TreePlan plan) {
		if (!plan.wood.containsKey(plan.root) || plan.leaves.size() < 12) {
			return false;
		}
		boolean hasLog = false;
		boolean hasBranch = false;
		for (BlockState state : plan.wood.values()) {
			hasLog |= plan.def.isModernLog(state);
			hasBranch |= plan.def.isModernBranch(state);
		}
		return hasLog && hasBranch;
	}

	private static boolean hasRootClearance(ServerLevel level, BlockPos origin) {
		for (int dx = -1; dx <= 1; dx++) {
			for (int dz = -1; dz <= 1; dz++) {
				if (dx == 0 && dz == 0) {
					continue;
				}
				BlockState state = level.getBlockState(origin.offset(dx, 0, dz));
				if (!state.canBeReplaced()) {
					return false;
				}
			}
		}
		return true;
	}

	private static boolean canReplace(ServerLevel level, BlockPos origin, BlockPos pos, boolean leaf) {
		BlockState state = level.getBlockState(pos);
		if (pos.equals(origin) && state.getBlock() instanceof WildTreeSaplingBlock) {
			return true;
		}
		if (state.canBeReplaced()) {
			return true;
		}
		return leaf && state.is(BlockTags.LEAVES);
	}

	private static void place(ServerLevel level, TreePlan plan) {
		UUID treeId = UUID.randomUUID();
		for (Map.Entry<BlockPos, BlockState> entry : plan.wood.entrySet()) {
			level.setBlock(entry.getKey(), entry.getValue(), 3);
			if (level.getBlockEntity(entry.getKey()) instanceof NewTreePartBlockEntity treePart) {
				treePart.markGeneratedTree(treeId, plan.def.id(), plan.root);
			}
		}
		for (BlockPos pos : plan.leaves) {
			if (plan.wood.containsKey(pos)) {
				continue;
			}
			level.setBlock(pos, generatedLeafState(level, pos, plan), 3);
		}
		updateLeafDistances(level, plan);
	}

	private static BlockState generatedLeafState(ServerLevel level, BlockPos pos, TreePlan plan) {
		BlockState state = plan.leafBlockState();
		if (state.hasProperty(LeavesBlock.PERSISTENT)) {
			state = state.setValue(LeavesBlock.PERSISTENT, Boolean.FALSE);
		}
		if (state.hasProperty(LeavesBlock.DISTANCE)) {
			state = state.setValue(LeavesBlock.DISTANCE, 7);
		}
		if (state.hasProperty(BlockStateProperties.WATERLOGGED)) {
			state = state.setValue(BlockStateProperties.WATERLOGGED, level.getFluidState(pos).is(FluidTags.WATER));
		}
		return state;
	}

	private static void updateLeafDistances(ServerLevel level, TreePlan plan) {
		Map<BlockPos, Integer> distances = new HashMap<>();
		ArrayDeque<BlockPos> queue = new ArrayDeque<>();
		for (BlockPos woodPos : plan.wood.keySet()) {
			for (Direction direction : Direction.values()) {
				BlockPos leafPos = woodPos.relative(direction);
				if (plan.leaves.contains(leafPos) && distances.putIfAbsent(leafPos, 1) == null) {
					queue.add(leafPos);
				}
			}
		}
		while (!queue.isEmpty()) {
			BlockPos pos = queue.removeFirst();
			int nextDistance = distances.get(pos) + 1;
			if (nextDistance > 6) {
				continue;
			}
			for (Direction direction : Direction.values()) {
				BlockPos neighbor = pos.relative(direction);
				if (!plan.leaves.contains(neighbor) || distances.containsKey(neighbor)) {
					continue;
				}
				distances.put(neighbor, nextDistance);
				queue.add(neighbor);
			}
		}
		for (BlockPos pos : plan.leaves) {
			BlockState state = level.getBlockState(pos);
			if (!state.hasProperty(LeavesBlock.DISTANCE)) {
				continue;
			}
			int distance = Math.min(7, distances.getOrDefault(pos, 7));
			level.setBlock(pos, state.setValue(LeavesBlock.DISTANCE, distance), 3);
		}
	}

	private record Layer(int yOffset, int radiusX, int radiusZ, float edgeDropChance) {
	}

	private static final class TreePlan {
		private final BlockPos root;
		private final WildTrees.Def def;
		private final Map<BlockPos, BlockState> wood = new LinkedHashMap<>();
		private final Set<BlockPos> leaves = new HashSet<>();

		private TreePlan(BlockPos origin, WildTrees.Def def) {
			this.root = origin.immutable();
			this.def = def;
		}

		private BlockState leafBlockState() {
			return def.modernLeaves().get().defaultBlockState();
		}
	}
}
