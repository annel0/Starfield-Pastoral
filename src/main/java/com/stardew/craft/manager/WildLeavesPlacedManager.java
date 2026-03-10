package com.stardew.craft.manager;

import com.stardew.craft.StardewCraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashSet;
import java.util.Set;

/**
 * Tracks player-placed wild leaves so they do not decay.
 *
 * We avoid introducing extra blockstate properties (which would require blockstate json changes).
 */
public final class WildLeavesPlacedManager extends SavedData {
	private static final String NAME = StardewCraft.MODID + "_wild_leaves_placed";
	private static final String TAG_POSITIONS = "Positions";

	private final Set<BlockPos> placed = new HashSet<>();

	public WildLeavesPlacedManager() {
	}

	public static WildLeavesPlacedManager load(CompoundTag tag) {
		WildLeavesPlacedManager m = new WildLeavesPlacedManager();
		ListTag list = tag.getList(TAG_POSITIONS, Tag.TAG_COMPOUND);
		for (int i = 0; i < list.size(); i++) {
			CompoundTag e = list.getCompound(i);
			m.placed.add(BlockPos.of(e.getLong("p")));
		}
		return m;
	}

	@Override
	public CompoundTag save(@SuppressWarnings("null") CompoundTag tag, @SuppressWarnings("null") HolderLookup.Provider provider) {
		ListTag list = new ListTag();
		for (BlockPos pos : placed) {
			CompoundTag e = new CompoundTag();
			e.putLong("p", pos.asLong());
			list.add(e);
		}
		tag.put(TAG_POSITIONS, list);
		return tag;
	}

	public static WildLeavesPlacedManager load(CompoundTag tag, HolderLookup.Provider provider) {
		return load(tag);
	}

	public void markPlaced(BlockPos pos) {
		if (placed.add(pos.immutable())) {
			setDirty();
		}
	}

	public void unmarkPlaced(BlockPos pos) {
		if (placed.remove(pos)) {
			setDirty();
		}
	}

	public boolean isPlaced(BlockPos pos) {
		return placed.contains(pos);
	}

	@SuppressWarnings("null")
	public static WildLeavesPlacedManager get(ServerLevel level) {
		return level.getDataStorage().computeIfAbsent(
				new SavedData.Factory<WildLeavesPlacedManager>(WildLeavesPlacedManager::new, WildLeavesPlacedManager::load, null),
				NAME
		);
	}
}
