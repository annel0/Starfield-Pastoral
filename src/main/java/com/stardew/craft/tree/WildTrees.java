package com.stardew.craft.tree;

import com.stardew.craft.block.ModBlocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public final class WildTrees {
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
			Supplier<Block> sapling1
	) {
		public boolean isAnyPart(BlockState state) {
			Block b = state.getBlock();
			return b == trunk0.get()
					|| b == trunk1.get()
					|| b == branch1.get()
					|| b == branch2.get()
					|| b == leaves.get();
		}

		public boolean isTrunk0(BlockState state) {
			return state.getBlock() == trunk0.get();
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
			() -> ModBlocks.WILD_OAK_SAPLING1.get()
	);

	public static final Def MAPLE = new Def(
			"maple",
			() -> ModBlocks.WILD_MAPLE_TRUNK0.get(),
			() -> ModBlocks.WILD_MAPLE_TRUNK1.get(),
			() -> ModBlocks.WILD_MAPLE_BRANCH1.get(),
			() -> ModBlocks.WILD_MAPLE_BRANCH2.get(),
			() -> ModBlocks.WILD_MAPLE_LEAVES.get(),
			() -> ModBlocks.WILD_MAPLE_SAPLING0.get(),
			() -> ModBlocks.WILD_MAPLE_SAPLING1.get()
	);

	public static final Def PINE = new Def(
			"pine",
			() -> ModBlocks.WILD_PINE_TRUNK0.get(),
			() -> ModBlocks.WILD_PINE_TRUNK1.get(),
			() -> ModBlocks.WILD_PINE_BRANCH1.get(),
			() -> ModBlocks.WILD_PINE_BRANCH2.get(),
			() -> ModBlocks.WILD_PINE_LEAVES.get(),
			() -> ModBlocks.WILD_PINE_SAPLING0.get(),
			() -> ModBlocks.WILD_PINE_SAPLING1.get()
	);

	public static final Def MAHOGANY = new Def(
			"mahogany",
			() -> ModBlocks.WILD_MAHOGANY_TRUNK0.get(),
			() -> ModBlocks.WILD_MAHOGANY_TRUNK1.get(),
			() -> ModBlocks.WILD_MAHOGANY_BRANCH1.get(),
			() -> ModBlocks.WILD_MAHOGANY_BRANCH2.get(),
			() -> ModBlocks.WILD_MAHOGANY_LEAVES.get(),
			() -> ModBlocks.WILD_MAHOGANY_SAPLING0.get(),
			() -> ModBlocks.WILD_MAHOGANY_SAPLING1.get()
	);

	public static final Def MYSTIC_TREE = new Def(
			"mystic_tree",
			() -> ModBlocks.WILD_MYSTIC_TREE_TRUNK0.get(),
			() -> ModBlocks.WILD_MYSTIC_TREE_TRUNK1.get(),
			() -> ModBlocks.WILD_MYSTIC_TREE_BRANCH1.get(),
			() -> ModBlocks.WILD_MYSTIC_TREE_BRANCH2.get(),
			() -> ModBlocks.WILD_MYSTIC_TREE_LEAVES.get(),
			() -> ModBlocks.WILD_MYSTIC_TREE_SAPLING0.get(),
			() -> ModBlocks.WILD_MYSTIC_TREE_SAPLING1.get()
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

	public static Def findByTrunk0(BlockState state) {
		for (Def def : ALL) {
			if (def.isTrunk0(state)) {
				return def;
			}
		}
		return null;
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
		}
	}
}
