package com.stardew.craft.tree.preset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 预制树模板（写入 config/stardewcraft/tree_presets/*.json）。
 *
 * JSON 示例：
 * {
 *   "format": 1,
 *   "tree": "oak",
 *   "name": "default",
 *   "blocks": [
 *     {"x":0,"y":0,"z":0,"part":"trunk0"}
 *   ]
 * }
 */
public final class TreePreset {
	public int format = 1;
	public String tree;
	public String name = "default";
	public List<BlockEntry> blocks = new ArrayList<>();

	public static final class BlockEntry {
		public int x;
		public int y;
		public int z;
		/**
		 * 可选值：trunk0, trunk1, branch1, branch2, leaves
		 */
		public String part;
		/**
		 * 可选：保存方块状态（propertyName -> value），用于还原朝向/轴等。
		 */
		public Map<String, String> state = new HashMap<>();
	}
}
