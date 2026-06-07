package com.stardew.craft.tree.generation;

public record StardewTreeProfile(
		String treeId,
		int minHeight,
		int maxHeight,
		int canopyRadius,
		float leafHoleChance,
		float hangingLeafChance
) {
	public int heightRange() {
		return Math.max(0, maxHeight - minHeight);
	}
}
