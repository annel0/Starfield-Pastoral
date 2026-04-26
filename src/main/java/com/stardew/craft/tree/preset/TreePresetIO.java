package com.stardew.craft.tree.preset;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.stardew.craft.StardewCraft;
import com.stardew.craft.tree.WildTrees;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.neoforged.fml.loading.FMLPaths;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public final class TreePresetIO {
	private TreePresetIO() {
	}

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	// Cache of loaded presets keyed by treeId. Invalidated when the active ResourceManager
	// instance changes (datapack reload yields a new MultiPackResourceManager). This avoids
	// walking the entire datapack tree on every tree-growth check, which previously froze the
	// server thread for many seconds during offline farm catch-up.
	private static volatile ResourceManager cachedResourceManager;
	private static final Map<String, List<TreePreset>> RESOURCE_CACHE = new ConcurrentHashMap<>();
	private static final Map<String, List<TreePreset>> CONFIG_CACHE = new ConcurrentHashMap<>();
	private static volatile long configCacheStampMs = 0L;
	// Refresh the config-folder cache at most every few seconds to allow live-edits without
	// re-walking the directory on every call during catch-up loops.
	private static final long CONFIG_CACHE_TTL_MS = 5_000L;

	public static synchronized void invalidateCaches() {
		cachedResourceManager = null;
		RESOURCE_CACHE.clear();
		CONFIG_CACHE.clear();
		configCacheStampMs = 0L;
	}

	public static Path presetsDir() {
		return FMLPaths.CONFIGDIR.get().resolve("stardewcraft").resolve("tree_presets");
	}

	public static Path presetPathByName(String nameNoExt) {
		return presetsDir().resolve(nameNoExt + ".json");
	}

	public static TreePreset loadByName(String nameNoExt) {
		Path path = presetPathByName(nameNoExt);
		return loadByPath(path);
	}

	public static List<TreePreset> loadAllForTree(String treeId) {
		// NOTE: This legacy method only reads from the config folder.
		// Prefer calling loadAllForTree(MinecraftServer, String) so presets packaged inside the mod JAR work.
		return loadAllForTreeFromConfig(treeId);
	}

	/**
	 * Load presets for a tree from:
	 * 1) Packaged datapack resources inside the mod JAR (data/stardewcraft/tree_presets/*.json), and
	 * 2) The config override folder (config/stardewcraft/tree_presets/*.json).
	 *
	 * For oak only, also supports legacy presets in data/stardewcraft/wild_oak_trees/*.json and
	 * config/stardewcraft/wild_oak_trees/*.json.
	 */
	public static List<TreePreset> loadAllForTree(MinecraftServer server, String treeId) {
		List<TreePreset> result = new ArrayList<>();
		if (server == null || treeId == null || treeId.isBlank()) {
			return result;
		}

		// 1) Packaged resources (cached per ResourceManager instance)
		ResourceManager rm = server.getResourceManager();
		if (rm != cachedResourceManager) {
			synchronized (TreePresetIO.class) {
				if (rm != cachedResourceManager) {
					RESOURCE_CACHE.clear();
					cachedResourceManager = rm;
				}
			}
		}
		List<TreePreset> resourcePresets = RESOURCE_CACHE.computeIfAbsent(treeId,
				id -> List.copyOf(loadAllForTreeFromResources(rm, id)));
		result.addAll(resourcePresets);

		// 2) Config overrides (cached briefly to avoid repeated dir walks during catch-up loops)
		long now = System.currentTimeMillis();
		if (now - configCacheStampMs > CONFIG_CACHE_TTL_MS) {
			synchronized (TreePresetIO.class) {
				if (now - configCacheStampMs > CONFIG_CACHE_TTL_MS) {
					CONFIG_CACHE.clear();
					configCacheStampMs = now;
				}
			}
		}
		List<TreePreset> configPresets = CONFIG_CACHE.computeIfAbsent(treeId,
				id -> List.copyOf(loadAllForTreeFromConfig(id)));
		result.addAll(configPresets);

		return result;
	}

	private static List<TreePreset> loadAllForTreeFromResources(ResourceManager rm, String treeId) {
		List<TreePreset> result = new ArrayList<>();
		if (rm == null || treeId == null || treeId.isBlank()) {
			return result;
		}

		// New format presets packaged under data/stardewcraft/tree_presets/*.json
		try {
			Map<ResourceLocation, Resource> resources = rm.listResources("tree_presets", loc -> loc.getPath().endsWith(".json"));
			for (Map.Entry<ResourceLocation, Resource> entry : resources.entrySet()) {
				TreePreset preset = loadByResource(entry.getValue());
				if (preset != null && treeId.equals(preset.tree)) {
					result.add(preset);
				}
			}
		} catch (Exception e) {
			StardewCraft.LOGGER.warn("Failed to list packaged tree presets (tree_presets)", e);
		}

		// Legacy oak presets packaged under data/stardewcraft/wild_oak_trees/*.json
		if (result.isEmpty() && "oak".equals(treeId)) {
			try {
				Map<ResourceLocation, Resource> resources = rm.listResources("wild_oak_trees", loc -> loc.getPath().endsWith(".json"));
				for (Map.Entry<ResourceLocation, Resource> entry : resources.entrySet()) {
					TreePreset preset = loadLegacyOakPresetFromResource(entry.getValue(), entry.getKey().toString());
					if (preset != null) {
						result.add(preset);
					}
				}
			} catch (Exception e) {
				StardewCraft.LOGGER.warn("Failed to list packaged legacy oak presets (wild_oak_trees)", e);
			}
		}

		return result;
	}

	private static List<TreePreset> loadAllForTreeFromConfig(String treeId) {
		List<TreePreset> result = new ArrayList<>();
		if (treeId == null || treeId.isBlank()) {
			return result;
		}

		Path dir = presetsDir();
		if (Files.isDirectory(dir)) {
			try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.json")) {
				for (Path p : stream) {
					TreePreset preset = loadByPath(p);
					if (preset != null && treeId.equals(preset.tree)) {
						result.add(preset);
					}
				}
			} catch (IOException e) {
				StardewCraft.LOGGER.warn("Failed to list tree presets in {}", dir.toAbsolutePath(), e);
			}
		}

		if ("oak".equals(treeId)) {
			result.addAll(loadLegacyOakPresets());
		}

		return result;
	}

	private static TreePreset loadByResource(Resource resource) {
		if (resource == null) {
			return null;
		}
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.open(), StandardCharsets.UTF_8))) {
			var el = JsonParser.parseReader(reader);
			if (!el.isJsonObject()) {
				return null;
			}
			TreePreset preset = GSON.fromJson(el, TreePreset.class);
			if (preset == null || preset.tree == null || preset.blocks == null || preset.blocks.isEmpty()) {
				return null;
			}
			return preset;
		} catch (IOException | IllegalStateException | JsonParseException e) {
			return null;
		}
	}

	private static List<TreePreset> loadLegacyOakPresets() {
		List<TreePreset> result = new ArrayList<>();
		Path dir = FMLPaths.CONFIGDIR.get().resolve("stardewcraft").resolve("wild_oak_trees");
		if (!Files.isDirectory(dir)) {
			return result;
		}
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.json")) {
			for (Path p : stream) {
				TreePreset preset = loadLegacyOakPresetByPath(p);
				if (preset != null) {
					result.add(preset);
				}
			}
		} catch (IOException e) {
			StardewCraft.LOGGER.warn("Failed to list legacy oak presets in {}", dir.toAbsolutePath(), e);
		}
		return result;
	}

	private static TreePreset loadLegacyOakPresetFromResource(Resource resource, String nameForLogging) {
		if (resource == null) {
			return null;
		}
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.open(), StandardCharsets.UTF_8))) {
			var el = JsonParser.parseReader(reader);
			if (!el.isJsonObject()) {
				return null;
			}
			LegacyOakPreset legacy = GSON.fromJson(el, LegacyOakPreset.class);
			if (legacy == null || legacy.blocks == null || legacy.blocks.isEmpty()) {
				return null;
			}
			TreePreset converted = new TreePreset();
			converted.format = 1;
			converted.tree = "oak";
			converted.name = legacy.id != null ? legacy.id : nameForLogging;
			for (LegacyOakPreset.BlockEntry b : legacy.blocks) {
				if (b == null || b.block == null) {
					continue;
				}
				String part = legacyBlockToPart(b.block);
				if (part == null) {
					continue;
				}
				TreePreset.BlockEntry e = new TreePreset.BlockEntry();
				e.x = b.dx;
				e.y = b.dy;
				e.z = b.dz;
				e.part = part;
				e.state = new LinkedHashMap<>();
				if (b.facing != null && !b.facing.isBlank()) {
					e.state.put("facing", b.facing);
				}
				converted.blocks.add(e);
			}
			if (converted.blocks.isEmpty()) {
				return null;
			}
			return converted;
		} catch (IOException | IllegalStateException | JsonParseException e) {
			StardewCraft.LOGGER.warn("Failed to load legacy oak preset resource {}", nameForLogging, e);
			return null;
		}
	}

	private static TreePreset loadLegacyOakPresetByPath(Path path) {
		if (!Files.exists(path)) {
			return null;
		}
		try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
			var el = JsonParser.parseReader(reader);
			if (!el.isJsonObject()) {
				return null;
			}
			LegacyOakPreset legacy = GSON.fromJson(el, LegacyOakPreset.class);
			if (legacy == null || legacy.blocks == null || legacy.blocks.isEmpty()) {
				return null;
			}
			TreePreset converted = new TreePreset();
			converted.format = 1;
			converted.tree = "oak";
			converted.name = legacy.id != null ? legacy.id : path.getFileName().toString();
			for (LegacyOakPreset.BlockEntry b : legacy.blocks) {
				if (b == null || b.block == null) {
					continue;
				}
				String part = legacyBlockToPart(b.block);
				if (part == null) {
					continue;
				}
				TreePreset.BlockEntry e = new TreePreset.BlockEntry();
				e.x = b.dx;
				e.y = b.dy;
				e.z = b.dz;
				e.part = part;
				e.state = new LinkedHashMap<>();
				if (b.facing != null && !b.facing.isBlank()) {
					e.state.put("facing", b.facing);
				}
				converted.blocks.add(e);
			}
			if (converted.blocks.isEmpty()) {
				return null;
			}
			return converted;
		} catch (IOException | IllegalStateException | JsonParseException e) {
			StardewCraft.LOGGER.warn("Failed to load legacy oak preset {}", path.toAbsolutePath(), e);
			return null;
		}
	}

	private static String legacyBlockToPart(String blockId) {
		Objects.requireNonNull(blockId);
		// Example: stardewcraft:wild_oak_trunk0
		if (blockId.endsWith("trunk0")) {
			return "trunk0";
		}
		if (blockId.endsWith("trunk1")) {
			return "trunk1";
		}
		if (blockId.endsWith("branch1")) {
			return "branch1";
		}
		if (blockId.endsWith("branch2")) {
			return "branch2";
		}
		if (blockId.endsWith("leaves")) {
			return "leaves";
		}
		return null;
	}

	private static final class LegacyOakPreset {
		String id;
		List<BlockEntry> blocks = new ArrayList<>();

		private static final class BlockEntry {
			int dx;
			int dy;
			int dz;
			String block;
			String facing;
		}
	}

	private static TreePreset loadByPath(Path path) {
		if (!Files.exists(path)) {
			return null;
		}
		try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
			var el = JsonParser.parseReader(reader);
			if (!el.isJsonObject()) {
				return null;
			}
			TreePreset preset = GSON.fromJson(el, TreePreset.class);
			if (preset == null || preset.tree == null || preset.blocks == null || preset.blocks.isEmpty()) {
				return null;
			}
			return preset;
		} catch (IOException | IllegalStateException | JsonParseException e) {
			StardewCraft.LOGGER.warn("Failed to load tree preset {}", path.toAbsolutePath(), e);
			return null;
		}
	}

	public static boolean writePreset(String nameNoExt, TreePreset preset, boolean overwrite) {
		try {
			Files.createDirectories(presetsDir());
		} catch (IOException e) {
			StardewCraft.LOGGER.warn("Failed to create tree preset dir {}", presetsDir().toAbsolutePath(), e);
			return false;
		}

		Path path = presetPathByName(nameNoExt);
		if (!overwrite && Files.exists(path)) {
			return false;
		}

		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			writer.write(GSON.toJson(preset));
			writer.write("\n");
			return true;
		} catch (IOException e) {
			StardewCraft.LOGGER.warn("Failed to write tree preset {}", path.toAbsolutePath(), e);
			return false;
		}
	}

	public static boolean writeDefaultPreset(WildTrees.Def def, boolean overwrite) {
		try {
			Files.createDirectories(presetsDir());
		} catch (IOException e) {
			StardewCraft.LOGGER.warn("Failed to create tree preset dir {}", presetsDir().toAbsolutePath(), e);
			return false;
		}

		Path path = presetPathByName(def.id());
		if (!overwrite && Files.exists(path)) {
			return false;
		}

		TreePreset preset = TreePresetTemplates.createFallbackTemplate(def);
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			writer.write(GSON.toJson(preset));
			writer.write("\n");
			return true;
		} catch (IOException e) {
			StardewCraft.LOGGER.warn("Failed to write tree preset {}", path.toAbsolutePath(), e);
			return false;
		}
	}
}
