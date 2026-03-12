package com.stardew.craft.manager;

import com.stardew.craft.block.tree.WildTreeSaplingBlock;
import com.stardew.craft.tree.WildTrees;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.SavedData;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 野生树苗生长管理器：每天结算一次生长（28天成熟），中间 2 阶段。
 * 与 CropGrowthManager 一样，使用 SavedData 记录位置，避免 chunk unload 丢状态。
 */
public class TreeGrowthManager extends SavedData {
	private static final String DATA_NAME = "stardew_tree_manager";

	private static final int TOTAL_DAYS = 28;
	private static final int STAGE1_DAY = 14;

	private final Set<GlobalPos> saplingPositions = new HashSet<>();
	private final Map<GlobalPos, Integer> daysGrown = new ConcurrentHashMap<>();

	private boolean isProcessing = false;
	private final Set<GlobalPos> pendingAdds = new HashSet<>();
	private final Set<GlobalPos> pendingRemoves = new HashSet<>();

	public void addSapling(@Nonnull Level level, @Nonnull BlockPos pos) {
		if (!(level instanceof ServerLevel)) {
			return;
		}
		GlobalPos gp = GlobalPos.of(
			Objects.requireNonNull(level.dimension(), "dimension"),
			Objects.requireNonNull(pos.immutable(), "pos")
		);
		if (isProcessing) {
			pendingAdds.add(gp);
			pendingRemoves.remove(gp);
			daysGrown.putIfAbsent(gp, 0);
			setDirty();
			return;
		}
		if (saplingPositions.add(gp)) {
			daysGrown.putIfAbsent(gp, 0);
			setDirty();
		}
	}

	public void removeSapling(@Nonnull Level level, @Nonnull BlockPos pos) {
		if (!(level instanceof ServerLevel)) {
			return;
		}
		GlobalPos gp = GlobalPos.of(
			Objects.requireNonNull(level.dimension(), "dimension"),
			Objects.requireNonNull(pos.immutable(), "pos")
		);
		if (isProcessing) {
			pendingRemoves.add(gp);
			pendingAdds.remove(gp);
			daysGrown.remove(gp);
			setDirty();
			return;
		}
		if (saplingPositions.remove(gp)) {
			daysGrown.remove(gp);
			setDirty();
		}
	}

	private void applyPendingChanges() {
		boolean changed = false;
		if (!pendingRemoves.isEmpty()) {
			changed |= saplingPositions.removeAll(pendingRemoves);
			for (GlobalPos p : pendingRemoves) {
				daysGrown.remove(p);
			}
			pendingRemoves.clear();
		}
		if (!pendingAdds.isEmpty()) {
			changed |= saplingPositions.addAll(pendingAdds);
			for (GlobalPos p : pendingAdds) {
				daysGrown.putIfAbsent(p, 0);
			}
			pendingAdds.clear();
		}
		if (changed) {
			setDirty();
		}
	}

	public void growDaily(ServerLevel level) {
		isProcessing = true;
		try {
			java.util.List<GlobalPos> snapshot = new java.util.ArrayList<>(saplingPositions);
			for (GlobalPos gp : snapshot) {
				if (gp.dimension() != level.dimension()) {
					continue;
				}
				BlockPos pos = Objects.requireNonNull(gp.pos(), "pos");
				if (!level.isLoaded(pos)) {
					continue;
				}

				BlockState state = level.getBlockState(pos);
				Block block = state.getBlock();
				if (!(block instanceof WildTreeSaplingBlock saplingBlock)) {
					removeSapling(level, pos);
					continue;
				}

				WildTrees.Def def = Objects.requireNonNull(saplingBlock.getDef(), "def");
				int currentDay = daysGrown.getOrDefault(gp, 0);

				// If already mature but previously blocked, keep trying whenever the space becomes available.
				if (saplingBlock.getStage() == 1 && currentDay >= TOTAL_DAYS) {
					if (canMature(level, pos, def) && placePresetOrFallbackTree(level, pos, def)) {
						removeSapling(level, pos);
					}
					continue;
				}

				// Stardew-like: blocked saplings do not advance growth.
				int nextDay = currentDay + 1;
				if (!canAdvance(level, pos, def, saplingBlock.getStage(), nextDay)) {
					continue;
				}

				daysGrown.put(gp, nextDay);
				setDirty();

				// Stage transition: 0 -> 1 at day 14 (1/2 of cycle)
				if (saplingBlock.getStage() == 0 && nextDay >= STAGE1_DAY) {
					BlockState nextState = Objects.requireNonNull(def.sapling1().get().defaultBlockState(), "sapling1");
					level.setBlock(pos, nextState, 3);
					continue;
				}

				// Mature: stage1 at day 28
				if (saplingBlock.getStage() == 1 && nextDay >= TOTAL_DAYS) {
					if (placePresetOrFallbackTree(level, pos, def)) {
						removeSapling(level, pos);
					}
				}
			}
		} finally {
			isProcessing = false;
			applyPendingChanges();
		}
	}

	/**
	 * Debug/utility: advance a single sapling by one day using the same rules as {@link #growDaily(ServerLevel)}.
	 * Ensures the sapling is tracked in SavedData.
	 */
	public void growOneDay(@Nonnull ServerLevel level, @Nonnull BlockPos pos) {
		GlobalPos gp = GlobalPos.of(
			Objects.requireNonNull(level.dimension(), "dimension"),
			Objects.requireNonNull(pos.immutable(), "pos")
		);
		addSapling(level, pos);

		BlockState state = level.getBlockState(pos);
		Block block = state.getBlock();
		if (!(block instanceof WildTreeSaplingBlock saplingBlock)) {
			removeSapling(level, pos);
			return;
		}

		WildTrees.Def def = Objects.requireNonNull(saplingBlock.getDef(), "def");
		int currentDay = daysGrown.getOrDefault(gp, 0);

		// If already mature but previously blocked, keep trying whenever the space becomes available.
		if (saplingBlock.getStage() == 1 && currentDay >= TOTAL_DAYS) {
			if (canMature(level, pos, def) && placePresetOrFallbackTree(level, pos, def)) {
				removeSapling(level, pos);
			}
			return;
		}

		int nextDay = currentDay + 1;
		if (!canAdvance(level, pos, def, saplingBlock.getStage(), nextDay)) {
			return;
		}

		daysGrown.put(gp, nextDay);
		setDirty();

		// Stage transition: 0 -> 1 at day 14 (1/2 of cycle)
		if (saplingBlock.getStage() == 0 && nextDay >= STAGE1_DAY) {
			BlockState nextState = Objects.requireNonNull(def.sapling1().get().defaultBlockState(), "sapling1");
			level.setBlock(pos, nextState, 3);
			return;
		}

		// Mature: stage1 at day 28
		if (saplingBlock.getStage() == 1 && nextDay >= TOTAL_DAYS) {
			if (placePresetOrFallbackTree(level, pos, def)) {
				removeSapling(level, pos);
			}
		}
	}

	public int getDaysGrown(@Nonnull ServerLevel level, @Nonnull BlockPos pos) {
		GlobalPos gp = GlobalPos.of(
			Objects.requireNonNull(level.dimension(), "dimension"),
			Objects.requireNonNull(pos.immutable(), "pos")
		);
		return daysGrown.getOrDefault(gp, 0);
	}

	public boolean isBlockedNow(@Nonnull ServerLevel level, @Nonnull BlockPos pos) {
		BlockState state = level.getBlockState(pos);
		Block block = state.getBlock();
		if (!(block instanceof WildTreeSaplingBlock saplingBlock)) {
			return false;
		}
		WildTrees.Def def = Objects.requireNonNull(saplingBlock.getDef(), "def");
		int day = getDaysGrown(level, pos);
		if (saplingBlock.getStage() == 1 && day >= TOTAL_DAYS) {
			return !canMature(level, pos, def);
		}
		return !isSaplingAreaClear(level, pos);
	}

	private static boolean canAdvance(@Nonnull ServerLevel level, @Nonnull BlockPos pos, @Nonnull WildTrees.Def def, int stage, int nextDay) {
		// Default daily obstruction: 3x3 around sapling at sapling-level must be clear.
		// This is consistent with Stardew tree growth (blocked -> no progress).
		if (stage == 1 && nextDay >= TOTAL_DAYS) {
			return canMature(level, pos, def);
		}
		return isSaplingAreaClear(level, pos);
	}

	private static boolean isSaplingAreaClear(@Nonnull ServerLevel level, @Nonnull BlockPos saplingPos) {
		for (int dx = -1; dx <= 1; dx++) {
			for (int dz = -1; dz <= 1; dz++) {
				if (dx == 0 && dz == 0) {
					continue;
				}
				BlockPos p = Objects.requireNonNull(saplingPos.offset(dx, 0, dz), "p");
				if (!level.getBlockState(p).canBeReplaced()) {
					return false;
				}
			}
		}
		return true;
	}

	private static boolean canMature(@Nonnull ServerLevel level, @Nonnull BlockPos pos, @Nonnull WildTrees.Def def) {
		// Maturity MUST be based on presets. If no preset exists, do not mature.
		// This prevents the ugly placeholder fallback tree from ever being used.
		return com.stardew.craft.tree.preset.TreePresetPlacer.canPlaceAnyFromConfig(level, pos, def);
	}

	private static boolean placePresetOrFallbackTree(@Nonnull ServerLevel level, @Nonnull BlockPos pos, @Nonnull WildTrees.Def def) {
		// Only place preset(s). No fallback.
		boolean ok = com.stardew.craft.tree.preset.TreePresetPlacer.placeFromConfigOrNull(level, pos, def);
		if (ok) {
			com.stardew.craft.manager.WildTreeSeedManager.get(level).trackTree(level, pos, def);
		}
		return ok;
	}

	@Override
	public @Nonnull CompoundTag save(@Nonnull CompoundTag tag, @Nonnull net.minecraft.core.HolderLookup.Provider provider) {
		ListTag list = new ListTag();
		for (GlobalPos gp : saplingPositions) {
			CompoundTag t = new CompoundTag();
			String dimId = Objects.requireNonNull(gp.dimension().location().toString(), "dimension");
			Tag posTag = Objects.requireNonNull(
				NbtUtils.writeBlockPos(Objects.requireNonNull(gp.pos(), "pos")),
				"posTag"
			);
			t.putString("Dimension", dimId);
			t.put("Pos", posTag);
			t.putInt("Days", daysGrown.getOrDefault(gp, 0));
			list.add(t);
		}
		tag.put("Saplings", list);
		return tag;
	}

	public static TreeGrowthManager load(@Nonnull CompoundTag tag, @Nonnull net.minecraft.core.HolderLookup.Provider provider) {
		TreeGrowthManager manager = new TreeGrowthManager();
		if (tag.contains("Saplings", Tag.TAG_LIST)) {
			ListTag list = tag.getList("Saplings", Tag.TAG_COMPOUND);
			for (int i = 0; i < list.size(); i++) {
				CompoundTag t = list.getCompound(i);
				ResourceKey<Level> dim = ResourceKey.create(
					Objects.requireNonNull(net.minecraft.core.registries.Registries.DIMENSION, "DIMENSION"),
					Objects.requireNonNull(
						net.minecraft.resources.ResourceLocation.parse(
							Objects.requireNonNull(t.getString("Dimension"), "dimension")
						),
						"dimensionId"
					)
				);
				BlockPos pos = NbtUtils.readBlockPos(t, "Pos").orElse(BlockPos.ZERO);
				GlobalPos gp = GlobalPos.of(
					Objects.requireNonNull(dim, "dim"),
					Objects.requireNonNull(pos, "pos")
				);
				manager.saplingPositions.add(gp);
				manager.daysGrown.put(gp, t.getInt("Days"));
			}
		}
		return manager;
	}

	public static TreeGrowthManager get(ServerLevel level) {
		ServerLevel overworld = level.getServer().overworld();
		return overworld.getDataStorage().computeIfAbsent(
				new SavedData.Factory<>(
						TreeGrowthManager::new,
						TreeGrowthManager::load
				),
				DATA_NAME
		);
	}
}
