package com.stardew.craft.tree.preset;

import com.stardew.craft.tree.WildTrees;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class TreePresetPlacer {
	private TreePresetPlacer() {
	}

	public static boolean canPlaceAnyFromConfig(ServerLevel level, BlockPos origin, WildTrees.Def def) {
		var presets = TreePresetIO.loadAllForTree(level.getServer(), def.id());
		if (presets.isEmpty()) {
			return false;
		}
		for (TreePreset preset : presets) {
			if (canPlace(level, origin, preset)) {
				return true;
			}
		}
		return false;
	}

	public static boolean placeFromConfigOrNull(ServerLevel level, BlockPos origin, WildTrees.Def def) {
		var presets = TreePresetIO.loadAllForTree(level.getServer(), def.id());
		if (presets.isEmpty()) {
			return false;
		}

		// Try all presets in random order to avoid "can place but random picked blocked".
		java.util.List<TreePreset> shuffled = new java.util.ArrayList<>(presets);
		java.util.Collections.shuffle(shuffled, new java.util.Random(level.getRandom().nextLong()));
		for (TreePreset preset : shuffled) {
			if (tryPlace(level, origin, def, preset)) {
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("null")
	private static boolean tryPlace(ServerLevel level, BlockPos origin, WildTrees.Def def, TreePreset preset) {
		// 先检查：所有目标方块（除 origin 位置允许是树苗）都可以替换。
		if (!canPlace(level, origin, preset)) {
			return false;
		}

		// 再放置
		for (TreePreset.BlockEntry e : preset.blocks) {
			BlockPos p = origin.offset(e.x, e.y, e.z);
			BlockState state = partToState(def, e.part);
			if (state == null) {
				return false;
			}
			state = applyState(state, e.state);
			level.setBlock(p, state, 3);
		}

		return true;
	}

	private static boolean canPlace(ServerLevel level, BlockPos origin, TreePreset preset) {
		if (preset == null || preset.blocks == null) {
			return false;
		}
		for (TreePreset.BlockEntry e : preset.blocks) {
			if (e == null) {
				return false;
			}
			String part = e.part;
			if (part == null || part.isBlank()) {
				return false;
			}
			BlockPos p = origin.offset(e.x, e.y, e.z);
			if (p.equals(origin)) {
				// origin 位置允许是树苗（成熟替换）
				continue;
			}
			if (!level.getBlockState(p).canBeReplaced()) {
				return false;
			}
		}
		return true;
	}

	private static BlockState partToState(WildTrees.Def def, String part) {
		Objects.requireNonNull(def);
		switch (part) {
			case "trunk0":
				return def.trunk0().get().defaultBlockState();
			case "trunk1":
				return def.trunk1().get().defaultBlockState();
			case "branch1":
				return def.branch1().get().defaultBlockState();
			case "branch2":
				return def.branch2().get().defaultBlockState();
			case "leaves":
				return def.leaves().get().defaultBlockState();
			default:
				return null;
		}
	}

	private static BlockState applyState(BlockState base, Map<String, String> state) {
		if (state == null || state.isEmpty()) {
			return base;
		}
		BlockState result = base;
		for (Map.Entry<String, String> entry : state.entrySet()) {
			String name = entry.getKey();
			String value = entry.getValue();
			if (name == null || value == null) {
				continue;
			}
			Property<?> prop = result.getBlock().getStateDefinition().getProperty(name);
			if (prop == null) {
				continue;
			}
			result = setPropertyFromString(result, prop, value);
		}
		return result;
	}

	@SuppressWarnings({"unchecked", "rawtypes", "null"})
	private static BlockState setPropertyFromString(BlockState state, Property prop, String value) {
		@SuppressWarnings("null")
		Optional parsed = prop.getValue(value);
		if (parsed.isEmpty()) {
			return state;
		}
		return state.setValue(prop, (Comparable) parsed.get());
	}
}
