package com.stardew.craft.tree.preset;

import com.stardew.craft.tree.WildTrees;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public final class TreePresetExporter {
	private TreePresetExporter() {
	}

	/**
	 * 从选区导出一棵树的预制模板。
	 * - origin 必须是 trunk0，用于计算相对偏移
	 * - from/to 仅用于框选范围（不要求包含 origin，但通常会包含）
	 */
	@SuppressWarnings("null")
	public static TreePreset export(ServerLevel level, WildTrees.Def def, BlockPos from, BlockPos to, BlockPos origin, String name) {
		int minX = Math.min(from.getX(), to.getX());
		int minY = Math.min(from.getY(), to.getY());
		int minZ = Math.min(from.getZ(), to.getZ());
		int maxX = Math.max(from.getX(), to.getX());
		int maxY = Math.max(from.getY(), to.getY());
		int maxZ = Math.max(from.getZ(), to.getZ());

		TreePreset preset = new TreePreset();
		preset.format = 1;
		preset.tree = def.id();
		preset.name = name;

		for (int y = minY; y <= maxY; y++) {
			for (int x = minX; x <= maxX; x++) {
				for (int z = minZ; z <= maxZ; z++) {
					BlockPos p = new BlockPos(x, y, z);
					BlockState state = level.getBlockState(p);
					if (!def.isAnyPart(state)) {
						continue;
					}
					String part = partFromState(def, state);
					if (part == null) {
						continue;
					}
					TreePreset.BlockEntry e = new TreePreset.BlockEntry();
					e.x = p.getX() - origin.getX();
					e.y = p.getY() - origin.getY();
					e.z = p.getZ() - origin.getZ();
					e.part = part;
					// 复制方块状态（朝向/轴/含水/树叶距离等），确保模板可复刻。
					for (Property<?> prop : state.getProperties()) {
						Comparable<?> v = state.getValue(prop);
						if (v != null) {
							e.state.put(prop.getName(), v.toString());
						}
					}
					preset.blocks.add(e);
				}
			}
		}

		if (preset.blocks.isEmpty()) {
			return null;
		}
		return preset;
	}

	private static String partFromState(WildTrees.Def def, BlockState state) {
		Block b = state.getBlock();
		if (b == def.trunk0().get()) {
			return "trunk0";
		}
		if (b == def.trunk1().get()) {
			return "trunk1";
		}
		if (b == def.branch1().get()) {
			return "branch1";
		}
		if (b == def.branch2().get()) {
			return "branch2";
		}
		if (b == def.leaves().get()) {
			return "leaves";
		}
		return null;
	}
}
