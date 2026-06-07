package com.stardew.craft.event;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.core.ModDimensions;
import com.stardew.craft.tree.WildTrees;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.ChunkEvent;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@EventBusSubscriber(modid = StardewCraft.MODID)
public final class LegacyTreeMigrationEvents {
	private static final int MAX_LEGACY_TREE_HEIGHT = 48;
	private static final int MAX_LEGACY_LEAVES = 512;
	private static final int MIGRATION_LOADED_RADIUS = 8;

	private LegacyTreeMigrationEvents() {
	}

	@SubscribeEvent
	public static void onChunkLoad(ChunkEvent.Load event) {
		if (!(event.getLevel() instanceof ServerLevel level)) {
			return;
		}
		if (level.dimension() != ModDimensions.STARDEW_VALLEY) {
			return;
		}
		if (!(event.getChunk() instanceof LevelChunk chunk)) {
			return;
		}
		int chunkX = chunk.getPos().x;
		int chunkZ = chunk.getPos().z;
		level.getServer().tell(new net.minecraft.server.TickTask(
				level.getServer().getTickCount() + 1,
				() -> {
					LevelChunk loaded = level.getChunkSource().getChunk(chunkX, chunkZ, false);
					if (loaded != null) {
						migrateChunk(level, loaded);
					}
				}
		));
	}

	private static void migrateChunk(ServerLevel level, LevelChunk chunk) {
		List<BlockPos> candidates = new ArrayList<>();
		int minX = chunk.getPos().getMinBlockX();
		int minZ = chunk.getPos().getMinBlockZ();
		LevelChunkSection[] sections = chunk.getSections();
		for (int si = 0; si < sections.length; si++) {
			LevelChunkSection section = sections[si];
			if (section == null || section.hasOnlyAir()) {
				continue;
			}
			int yBase = chunk.getSectionYFromSectionIndex(si) << 4;
			BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
			for (int y = 0; y < 16; y++) {
				for (int x = 0; x < 16; x++) {
					for (int z = 0; z < 16; z++) {
						cursor.set(minX + x, yBase + y, minZ + z);
						BlockState state = readLoadedState(level, cursor);
						if (state == null) {
							continue;
						}
						WildTrees.Def def = WildTrees.findByTrunk0(state);
						if (def != null && isLowestLegacyBase(level, cursor, def)) {
							candidates.add(cursor.immutable());
						}
					}
				}
			}
		}

		int changed = 0;
		for (BlockPos pos : candidates) {
			BlockState state = readLoadedState(level, pos);
			if (state == null) {
				continue;
			}
			WildTrees.Def def = WildTrees.findByTrunk0(state);
			if (def != null && clearLegacyTree(level, pos, def)) {
				changed++;
			}
		}
		if (changed > 0) {
			chunk.setUnsaved(true);
		}
	}

	private static boolean clearLegacyTree(ServerLevel level, BlockPos root, WildTrees.Def def) {
		if (!hasMigrationAreaLoaded(level, root)) {
			return false;
		}
		Map<BlockPos, BlockState> snapshot = collectLegacyTree(level, root, def);
		if (snapshot == null || snapshot.isEmpty()) {
			return false;
		}

		for (BlockPos pos : snapshot.keySet()) {
			level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
		}
		return true;
	}

	private static boolean hasMigrationAreaLoaded(ServerLevel level, BlockPos root) {
		int minChunkX = (root.getX() - MIGRATION_LOADED_RADIUS) >> 4;
		int maxChunkX = (root.getX() + MIGRATION_LOADED_RADIUS) >> 4;
		int minChunkZ = (root.getZ() - MIGRATION_LOADED_RADIUS) >> 4;
		int maxChunkZ = (root.getZ() + MIGRATION_LOADED_RADIUS) >> 4;
		for (int chunkX = minChunkX; chunkX <= maxChunkX; chunkX++) {
			for (int chunkZ = minChunkZ; chunkZ <= maxChunkZ; chunkZ++) {
				if (!level.hasChunk(chunkX, chunkZ)) {
					return false;
				}
			}
		}
		return true;
	}

	private static Map<BlockPos, BlockState> collectLegacyTree(ServerLevel level, BlockPos root, WildTrees.Def def) {
		LinkedHashMap<BlockPos, BlockState> snapshot = new LinkedHashMap<>();
		Set<BlockPos> wood = new HashSet<>();
		BlockState rootState = readLoadedState(level, root);
		if (rootState == null) {
			return null;
		}
		if (!def.isTrunk0(rootState)) {
			return null;
		}
		snapshot.put(root.immutable(), rootState);
		wood.add(root.immutable());

		for (int y = 1; y <= MAX_LEGACY_TREE_HEIGHT; y++) {
			BlockPos pos = root.above(y);
			BlockState state = readLoadedState(level, pos);
			if (state == null) {
				break;
			}
			Block block = state.getBlock();
			if (block != def.trunk0().get() && block != def.trunk1().get()) {
				break;
			}
			BlockPos immutable = pos.immutable();
			snapshot.put(immutable, state);
			wood.add(immutable);
		}
		if (wood.size() <= 1) {
			return null;
		}

		for (BlockPos woodPos : List.copyOf(wood)) {
			for (Direction direction : Direction.Plane.HORIZONTAL) {
				BlockPos pos = woodPos.relative(direction);
				BlockState state = readLoadedState(level, pos);
				if (state == null) {
					continue;
				}
				Block block = state.getBlock();
				if (block == def.branch1().get() || block == def.branch2().get()) {
					BlockPos immutable = pos.immutable();
					snapshot.putIfAbsent(immutable, state);
					wood.add(immutable);
				}
			}
		}

		collectLegacyLeaves(level, def, wood, snapshot);
		return snapshot;
	}

	private static void collectLegacyLeaves(ServerLevel level, WildTrees.Def def, Set<BlockPos> wood, Map<BlockPos, BlockState> snapshot) {
		ArrayDeque<BlockPos> queue = new ArrayDeque<>();
		Set<BlockPos> visited = new HashSet<>();
		for (BlockPos woodPos : wood) {
			for (Direction direction : Direction.values()) {
				BlockPos pos = woodPos.relative(direction);
				BlockState state = readLoadedState(level, pos);
				if (state != null && visited.add(pos) && state.getBlock() == def.leaves().get()) {
					queue.add(pos.immutable());
				}
			}
		}

		int leaves = 0;
		while (!queue.isEmpty() && leaves < MAX_LEGACY_LEAVES) {
			BlockPos pos = queue.removeFirst();
			BlockState state = readLoadedState(level, pos);
			if (state == null) {
				continue;
			}
			if (state.getBlock() != def.leaves().get()) {
				continue;
			}
			snapshot.putIfAbsent(pos.immutable(), state);
			leaves++;
			for (Direction direction : Direction.values()) {
				BlockPos next = pos.relative(direction);
				BlockState nextState = readLoadedState(level, next);
				if (nextState != null && visited.add(next) && nextState.getBlock() == def.leaves().get()) {
					queue.add(next.immutable());
				}
			}
		}
	}

	private static boolean isLowestLegacyBase(ServerLevel level, BlockPos pos, WildTrees.Def def) {
		BlockState belowState = readLoadedState(level, pos.below());
		if (belowState == null) {
			return false;
		}
		Block below = belowState.getBlock();
		return below != def.trunk0().get() && below != def.trunk1().get();
	}

	private static BlockState readLoadedState(ServerLevel level, BlockPos pos) {
		if (!level.hasChunk(pos.getX() >> 4, pos.getZ() >> 4)) {
			return null;
		}
		return level.getBlockState(pos);
	}
}
