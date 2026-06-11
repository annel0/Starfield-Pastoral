package com.stardew.craft.tree;

import com.stardew.craft.block.ModBlocks;
import com.stardew.craft.blockentity.NewTreePartBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class WildTrees {
	private static final int MAX_MODERN_TREE_HEIGHT = 48;
	private static final int MODERN_CANOPY_RADIUS = 6;
	private static final int MAX_CONNECTED_MODERN_WOOD = MAX_MODERN_TREE_HEIGHT * 4;

	private WildTrees() {
	}

	public record Def(
			String id,
			Supplier<Block> trunk0,
			Supplier<Block> trunk1,
			Supplier<Block> branch1,
			Supplier<Block> branch2,
			Supplier<Block> leaves,
			Supplier<Block> sapling0,
			Supplier<Block> sapling1,
			Supplier<Block> modernRoot,
			Supplier<Block> modernLog,
			Supplier<Block> modernLeaves,
			Supplier<Block> modernBranch,
			float growthChance,
			float fertilizedGrowthChance,
			float seedSpreadChance,
			float seedOnShakeChance,
			float seedOnChopChance,
			boolean growsInWinter
	) {
		public boolean isAnyPart(BlockState state) {
			Block b = state.getBlock();
			return b == trunk0.get()
					|| b == trunk1.get()
					|| b == branch1.get()
					|| b == branch2.get()
					|| b == leaves.get()
					|| b == modernRoot.get()
					|| b == modernLog.get()
					|| b == modernLeaves.get()
					|| b == modernBranch.get();
		}

		public boolean isTrunk0(BlockState state) {
			return state.getBlock() == trunk0.get();
		}

		public boolean isModernRoot(BlockState state) {
			return state.getBlock() == modernRoot.get();
		}

		public boolean isModernLog(BlockState state) {
			return state.getBlock() == modernLog.get();
		}

		public boolean isModernLeaves(BlockState state) {
			return state.getBlock() == modernLeaves.get();
		}

		public boolean isModernBranch(BlockState state) {
			return state.getBlock() == modernBranch.get();
		}

		public boolean isModernPart(BlockState state) {
			Block b = state.getBlock();
			return b == modernRoot.get()
					|| b == modernLog.get()
					|| b == modernLeaves.get()
					|| b == modernBranch.get();
		}

		public boolean isSapling(BlockState state) {
			Block b = state.getBlock();
			return b == sapling0.get() || b == sapling1.get();
		}
	}

	public static final Def OAK = new Def(
			"oak",
			() -> ModBlocks.WILD_OAK_TRUNK0.get(),
			() -> ModBlocks.WILD_OAK_TRUNK1.get(),
			() -> ModBlocks.WILD_OAK_BRANCH1.get(),
			() -> ModBlocks.WILD_OAK_BRANCH2.get(),
			() -> ModBlocks.WILD_OAK_LEAVES.get(),
			() -> ModBlocks.WILD_OAK_SAPLING0.get(),
			() -> ModBlocks.WILD_OAK_SAPLING1.get(),
			() -> ModBlocks.OAK_ROOT.get(),
			() -> ModBlocks.OAK_LOG.get(),
			() -> ModBlocks.OAK_LEAVES.get(),
			() -> ModBlocks.OAK_BRANCH.get(),
			0.2f,
			1.0f,
			0.15f,
			0.05f,
			0.75f,
			false
	);

	public static final Def MAPLE = new Def(
			"maple",
			() -> ModBlocks.WILD_MAPLE_TRUNK0.get(),
			() -> ModBlocks.WILD_MAPLE_TRUNK1.get(),
			() -> ModBlocks.WILD_MAPLE_BRANCH1.get(),
			() -> ModBlocks.WILD_MAPLE_BRANCH2.get(),
			() -> ModBlocks.WILD_MAPLE_LEAVES.get(),
			() -> ModBlocks.WILD_MAPLE_SAPLING0.get(),
			() -> ModBlocks.WILD_MAPLE_SAPLING1.get(),
			() -> ModBlocks.MAPLE_ROOT.get(),
			() -> ModBlocks.MAPLE_LOG.get(),
			() -> ModBlocks.MAPLE_LEAVES.get(),
			() -> ModBlocks.MAPLE_BRANCH.get(),
			0.2f,
			1.0f,
			0.15f,
			0.05f,
			0.75f,
			false
	);

	public static final Def PINE = new Def(
			"pine",
			() -> ModBlocks.WILD_PINE_TRUNK0.get(),
			() -> ModBlocks.WILD_PINE_TRUNK1.get(),
			() -> ModBlocks.WILD_PINE_BRANCH1.get(),
			() -> ModBlocks.WILD_PINE_BRANCH2.get(),
			() -> ModBlocks.WILD_PINE_LEAVES.get(),
			() -> ModBlocks.WILD_PINE_SAPLING0.get(),
			() -> ModBlocks.WILD_PINE_SAPLING1.get(),
			() -> ModBlocks.PINE_ROOT.get(),
			() -> ModBlocks.PINE_LOG.get(),
			() -> ModBlocks.PINE_LEAVES.get(),
			() -> ModBlocks.PINE_BRANCH.get(),
			0.2f,
			1.0f,
			0.15f,
			0.05f,
			0.75f,
			false
	);

	public static final Def MAHOGANY = new Def(
			"mahogany",
			() -> ModBlocks.WILD_MAHOGANY_TRUNK0.get(),
			() -> ModBlocks.WILD_MAHOGANY_TRUNK1.get(),
			() -> ModBlocks.WILD_MAHOGANY_BRANCH1.get(),
			() -> ModBlocks.WILD_MAHOGANY_BRANCH2.get(),
			() -> ModBlocks.WILD_MAHOGANY_LEAVES.get(),
			() -> ModBlocks.WILD_MAHOGANY_SAPLING0.get(),
			() -> ModBlocks.WILD_MAHOGANY_SAPLING1.get(),
			() -> ModBlocks.MAHOGANY_ROOT.get(),
			() -> ModBlocks.MAHOGANY_LOG.get(),
			() -> ModBlocks.MAHOGANY_LEAVES.get(),
			() -> ModBlocks.MAHOGANY_BRANCH.get(),
			0.15f,
			0.6f,
			0.15f,
			0.05f,
			0.5625f,
			false
	);

	public static final Def MYSTIC_TREE = new Def(
			"mystic_tree",
			() -> ModBlocks.WILD_MYSTIC_TREE_TRUNK0.get(),
			() -> ModBlocks.WILD_MYSTIC_TREE_TRUNK1.get(),
			() -> ModBlocks.WILD_MYSTIC_TREE_BRANCH1.get(),
			() -> ModBlocks.WILD_MYSTIC_TREE_BRANCH2.get(),
			() -> ModBlocks.WILD_MYSTIC_TREE_LEAVES.get(),
			() -> ModBlocks.WILD_MYSTIC_TREE_SAPLING0.get(),
			() -> ModBlocks.WILD_MYSTIC_TREE_SAPLING1.get(),
			() -> ModBlocks.MYSTIC_TREE_ROOT.get(),
			() -> ModBlocks.MYSTIC_TREE_LOG.get(),
			() -> ModBlocks.MYSTIC_TREE_LEAVES.get(),
			() -> ModBlocks.MYSTIC_TREE_BRANCH.get(),
			0.15f,
			0.3f,
			0.0f,
			0.0f,
			0.0f,
			false
	);

	public static final List<Def> ALL = List.of(OAK, MAPLE, PINE, MAHOGANY, MYSTIC_TREE);

	public static Def findByAnyPart(BlockState state) {
		for (Def def : ALL) {
			if (def.isAnyPart(state)) {
				return def;
			}
		}
		return null;
	}

	public static Def findBySapling(BlockState state) {
		for (Def def : ALL) {
			if (def.isSapling(state)) {
				return def;
			}
		}
		return null;
	}

	public static boolean isAnyWildTreePart(BlockState state) {
		return findByAnyPart(state) != null;
	}

	public static boolean isAnyWildTreeTrunk0(BlockState state) {
		for (Def def : ALL) {
			if (state.getBlock() == def.trunk0().get()) {
				return true;
			}
		}
		return false;
	}

	public static Def findByModernPart(BlockState state) {
		for (Def def : ALL) {
			if (def.isModernPart(state)) {
				return def;
			}
		}
		return null;
	}

	public static Def findByModernLog(BlockState state) {
		for (Def def : ALL) {
			if (def.isModernLog(state)) {
				return def;
			}
		}
		return null;
	}

	public static Def findByModernRoot(BlockState state) {
		for (Def def : ALL) {
			if (def.isModernRoot(state)) {
				return def;
			}
		}
		return null;
	}

	public static Def findByTrunk0(BlockState state) {
		for (Def def : ALL) {
			if (def.isTrunk0(state)) {
				return def;
			}
		}
		return null;
	}

	public static Def findTapperSupportDef(LevelReader level, BlockPos supportPos) {
		BlockState state = level.getBlockState(supportPos);
		Def modern = findByModernLog(state);
		if (modern != null) {
			if (!isLiveGeneratedModernTreePart(level, supportPos, modern) && level instanceof ServerLevel serverLevel) {
				BlockPos root = findModernRootFromLog(level, supportPos, modern);
				if (root != null) {
					com.stardew.craft.manager.WildTreeSeedManager.get(serverLevel)
							.tryMigrateGeneratedTreeMarker(serverLevel, root, modern);
				}
			}
			return isLiveGeneratedModernTreePart(level, supportPos, modern) ? modern : null;
		}
		Def legacy = findByTrunk0(state);
		if (legacy != null && isLegacyCompleteTree(level, supportPos, legacy)) {
			return legacy;
		}
		return null;
	}

	public static BlockPos findTapperTreeRoot(LevelReader level, BlockPos supportPos) {
		BlockState state = level.getBlockState(supportPos);
		Def modern = findByModernLog(state);
		if (modern != null) {
			NewTreePartBlockEntity marker = getGeneratedModernTreeMarker(level, supportPos, modern);
			return marker != null && isLiveGeneratedModernTreePart(level, supportPos, modern)
					? marker.getGeneratedTreeRoot()
					: null;
		}
		Def legacy = findByTrunk0(state);
		return legacy == null ? null : supportPos;
	}

	public static BlockPos findModernRootFromLog(LevelReader level, BlockPos logPos, Def def) {
		if (!def.isModernLog(level.getBlockState(logPos))) {
			return null;
		}
		return findModernRootFromWood(level, logPos, def);
	}

	public static boolean isModernCompleteTree(LevelReader level, BlockPos rootPos, Def def) {
		if (!def.isModernRoot(level.getBlockState(rootPos))) {
			return false;
		}
		// 预制树（结构树）木质不靠连通性，按登记表判定：登记在册且未砍倒即为「完整树」。
		if (level instanceof ServerLevel serverLevel) {
			com.stardew.craft.tree.prefab.PrefabTreeInstance prefab =
					com.stardew.craft.tree.prefab.PrefabTreeRegistry.get(serverLevel).getByRoot(rootPos);
			if (prefab != null) {
				return !prefab.felled();
			}
		}
		Set<BlockPos> wood = collectConnectedModernWood(level, rootPos, def);
		boolean hasLog = false;
		boolean hasBranch = false;
		boolean hasLeaves = false;
		for (BlockPos pos : wood) {
			BlockState state = level.getBlockState(pos);
			hasLog |= def.isModernLog(state);
			hasBranch |= def.isModernBranch(state);
			for (Direction direction : Direction.values()) {
				if (def.isModernLeaves(level.getBlockState(pos.relative(direction)))) {
					hasLeaves = true;
				}
			}
		}
		return hasLog && (hasBranch || hasLeaves);
	}

	public static void forEachModernLogInTree(LevelReader level, BlockPos rootPos, Def def, Consumer<BlockPos> consumer) {
		if (!def.isModernRoot(level.getBlockState(rootPos))) {
			return;
		}
		for (BlockPos pos : collectConnectedModernWood(level, rootPos, def)) {
			if (def.isModernLog(level.getBlockState(pos))) {
				consumer.accept(pos);
			}
		}
	}

	public static void forEachLiveGeneratedModernLogInTree(LevelReader level, BlockPos rootPos, Def def, Consumer<BlockPos> consumer) {
		NewTreePartBlockEntity rootMarker = getGeneratedModernTreeMarker(level, rootPos, def);
		if (rootMarker == null || !rootPos.equals(rootMarker.getGeneratedTreeRoot())) {
			return;
		}
		for (BlockPos pos : collectConnectedModernWood(level, rootPos, def)) {
			BlockState state = level.getBlockState(pos);
			if (!def.isModernLog(state)) {
				continue;
			}
			NewTreePartBlockEntity partMarker = getGeneratedModernTreeMarker(level, pos, def);
			if (partMarker != null && rootMarker.getGeneratedTreeId().equals(partMarker.getGeneratedTreeId())) {
				consumer.accept(pos);
			}
		}
	}

	public static boolean markGeneratedModernTree(ServerLevel level, BlockPos rootPos, Def def) {
		if (!isModernCompleteTree(level, rootPos, def)) {
			return false;
		}
		UUID treeId = UUID.randomUUID();
		for (BlockPos pos : collectConnectedModernWood(level, rootPos, def)) {
			if (level.getBlockEntity(pos) instanceof NewTreePartBlockEntity treePart) {
				treePart.markGeneratedTree(treeId, def.id(), rootPos);
			}
		}
		return true;
	}

	/**
	 * 从任意现代树部位（树根/原木/树枝/树叶）找到这棵树的树根坐标。
	 * 供预制树系统「收养」老存档算法生成树时定位树根用。
	 */
	public static BlockPos findGeneratedModernRoot(LevelReader level, BlockPos pos, Def def) {
		BlockState state = level.getBlockState(pos);
		if (def.isModernRoot(state)) {
			return pos.immutable();
		}
		if (isModernWood(state, def)) {
			return findModernRootFromWood(level, pos, def);
		}
		// 树叶或其它：在相邻找现代木质，再回溯到树根。
		for (Direction direction : Direction.values()) {
			BlockPos neighbor = pos.relative(direction);
			if (isModernWood(level.getBlockState(neighbor), def)) {
				BlockPos root = findModernRootFromWood(level, neighbor, def);
				if (root != null) {
					return root;
				}
			}
		}
		return null;
	}

	/**
	 * 收集一棵现代树的全部「构成方块」：连通的木质（树根/原木/树枝）+ 与木质相连的树叶。
	 * 供预制树系统收养算法生成树时构建占地成员集。
	 */
	public static Set<BlockPos> collectGeneratedModernTreeMembers(LevelReader level, BlockPos rootPos, Def def) {
		Set<BlockPos> members = new HashSet<>(collectConnectedModernWood(level, rootPos, def));
		Block leavesBlock = def.modernLeaves().get();
		int cap = MAX_CONNECTED_MODERN_WOOD * 4;
		Set<BlockPos> visited = new HashSet<>(members);
		ArrayDeque<BlockPos> queue = new ArrayDeque<>();
		for (BlockPos wood : new HashSet<>(members)) {
			for (Direction direction : Direction.values()) {
				BlockPos leaf = wood.relative(direction);
				if (level.getBlockState(leaf).getBlock() == leavesBlock && visited.add(leaf)) {
					members.add(leaf.immutable());
					queue.add(leaf);
				}
			}
		}
		while (!queue.isEmpty() && members.size() < cap) {
			BlockPos leaf = queue.removeFirst();
			for (Direction direction : Direction.values()) {
				BlockPos next = leaf.relative(direction);
				if (level.getBlockState(next).getBlock() == leavesBlock && visited.add(next)) {
					members.add(next.immutable());
					queue.add(next);
				}
			}
		}
		return members;
	}

	private static boolean isLiveGeneratedModernTreePart(LevelReader level, BlockPos pos, Def def) {
		NewTreePartBlockEntity marker = getGeneratedModernTreeMarker(level, pos, def);
		if (marker == null) {
			return false;
		}
		BlockPos root = marker.getGeneratedTreeRoot();
		if (root == null || !def.isModernRoot(level.getBlockState(root))) {
			return false;
		}
		NewTreePartBlockEntity rootMarker = getGeneratedModernTreeMarker(level, root, def);
		return rootMarker != null
				&& root.equals(rootMarker.getGeneratedTreeRoot())
				&& marker.getGeneratedTreeId().equals(rootMarker.getGeneratedTreeId());
	}

	private static NewTreePartBlockEntity getGeneratedModernTreeMarker(LevelReader level, BlockPos pos, Def def) {
		if (!isModernWood(level.getBlockState(pos), def)) {
			return null;
		}
		if (!(level.getBlockEntity(pos) instanceof NewTreePartBlockEntity marker)) {
			return null;
		}
		if (!marker.hasGeneratedTreeMarker()) {
			return null;
		}
		return def.id().equals(marker.getGeneratedTreeSpecies()) ? marker : null;
	}

	private static BlockPos findModernRootFromWood(LevelReader level, BlockPos start, Def def) {
		for (BlockPos pos : collectConnectedModernWood(level, start, def)) {
			if (def.isModernRoot(level.getBlockState(pos))) {
				return pos.immutable();
			}
		}
		return null;
	}

	private static Set<BlockPos> collectConnectedModernWood(LevelReader level, BlockPos start, Def def) {
		// 预制树（结构树）木质用装饰块自由搭、并不 6-连通，按登记表返回全部木质成员（树根/原木/树枝），
		// 让采集器计数、标记、完整性判定等不再受连通性限制。
		if (level instanceof ServerLevel serverLevel) {
			com.stardew.craft.tree.prefab.PrefabTreeInstance prefab =
					com.stardew.craft.tree.prefab.PrefabTreeRegistry.get(serverLevel).getByMember(start);
			if (prefab != null) {
				Set<BlockPos> wood = new HashSet<>();
				Block rootBlock = def.modernRoot().get();
				Block logBlock = def.modernLog().get();
				Block branchBlock = def.modernBranch().get();
				for (BlockPos member : prefab.members()) {
					Block b = level.getBlockState(member).getBlock();
					if (b == rootBlock || b == logBlock || b == branchBlock) {
						wood.add(member.immutable());
					}
				}
				if (!wood.isEmpty()) {
					return wood;
				}
			}
		}
		Set<BlockPos> visited = new HashSet<>();
		ArrayDeque<BlockPos> queue = new ArrayDeque<>();
		if (!isModernWood(level.getBlockState(start), def)) {
			return visited;
		}
		visited.add(start.immutable());
		queue.add(start.immutable());
		while (!queue.isEmpty() && visited.size() < MAX_CONNECTED_MODERN_WOOD) {
			BlockPos pos = queue.removeFirst();
			for (Direction direction : Direction.values()) {
				BlockPos next = pos.relative(direction);
				if (visited.contains(next)) {
					continue;
				}
				if (!isModernWood(level.getBlockState(next), def)) {
					continue;
				}
				BlockPos immutable = next.immutable();
				visited.add(immutable);
				queue.add(immutable);
			}
		}
		return visited;
	}

	private static boolean isModernWood(BlockState state, Def def) {
		return def.isModernRoot(state) || def.isModernLog(state) || def.isModernBranch(state);
	}

	private static boolean isLegacyCompleteTree(LevelReader level, BlockPos trunk0Pos, Def def) {
		for (int i = 1; i <= MAX_MODERN_TREE_HEIGHT; i++) {
			BlockState above = level.getBlockState(trunk0Pos.above(i));
			if (above.getBlock() == def.trunk1().get() || above.getBlock() == def.trunk0().get()) {
				return true;
			}
			break;
		}
		for (Direction d : Direction.Plane.HORIZONTAL) {
			Block b = level.getBlockState(trunk0Pos.relative(d)).getBlock();
			if (b == def.branch1().get() || b == def.branch2().get()) {
				return true;
			}
		}
		return false;
	}

	public static void requireNonNullBlocks() {
		// Defensive helper for early crash if registrations are missing.
		for (Def def : ALL) {
			Objects.requireNonNull(def.trunk0().get());
			Objects.requireNonNull(def.trunk1().get());
			Objects.requireNonNull(def.branch1().get());
			Objects.requireNonNull(def.branch2().get());
			Objects.requireNonNull(def.leaves().get());
			Objects.requireNonNull(def.sapling0().get());
			Objects.requireNonNull(def.sapling1().get());
			Objects.requireNonNull(def.modernRoot().get());
			Objects.requireNonNull(def.modernLog().get());
			Objects.requireNonNull(def.modernLeaves().get());
			Objects.requireNonNull(def.modernBranch().get());
		}
	}
}
