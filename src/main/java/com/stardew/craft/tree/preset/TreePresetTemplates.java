package com.stardew.craft.tree.preset;

import com.stardew.craft.tree.WildTrees;

public final class TreePresetTemplates {
	private TreePresetTemplates() {
	}

	/**
	 * 生成一个与 placeFallbackTree 对齐的最简树形，作为玩家编辑的起点。
	 */
	public static TreePreset createFallbackTemplate(WildTrees.Def def) {
		TreePreset preset = new TreePreset();
		preset.format = 1;
		preset.tree = def.id();
		preset.name = "default";

		add(preset, 0, 0, 0, "trunk0");
		add(preset, 0, 1, 0, "trunk1");
		add(preset, 0, 2, 0, "trunk1");

		// 3x3 leaves at y=3
		for (int dx = -1; dx <= 1; dx++) {
			for (int dz = -1; dz <= 1; dz++) {
				add(preset, dx, 3, dz, "leaves");
			}
		}

		// One branch at (1,2,0)
		add(preset, 1, 2, 0, "branch1");

		return preset;
	}

	private static void add(TreePreset preset, int x, int y, int z, String part) {
		TreePreset.BlockEntry e = new TreePreset.BlockEntry();
		e.x = x;
		e.y = y;
		e.z = z;
		e.part = part;
		preset.blocks.add(e);
	}
}
