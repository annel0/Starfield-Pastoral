package com.stardew.craft.dimension;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.core.ModDimensions;
import com.stardew.craft.desert.DesertConstants;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.chunk.PalettedContainerRO;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.ChunkEvent;

/**
 * Replaces {@code minecraft:the_void} and {@code minecraft:plains} biomes
 * with {@code stardewcraft:stardew_default} when chunks load in the
 * Stardew Valley dimension.
 *
 * <p>Prebuilt region files (.mca) were saved with void/plains biome data.
 * This handler transparently patches them at load time so that
 * precipitation works and fishing falls back to the trash pool.
 */
@EventBusSubscriber(modid = StardewCraft.MODID)
@SuppressWarnings("null")
public final class StardewBiomePatcher {

    private static final ResourceKey<Biome> STARDEW_DEFAULT_KEY =
            ResourceKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath("stardewcraft", "stardew_default"));

    private static final ResourceKey<Biome> CALICO_DESERT_KEY =
            ResourceKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath("stardewcraft", "calico_desert"));

    // 沙漠 schem 包围盒（世界坐标）
    private static final int DESERT_MIN_X = DesertConstants.DESERT_ORIGIN.getX();        // -466
    private static final int DESERT_MAX_X = DESERT_MIN_X + 297;                          // -169
    private static final int DESERT_MIN_Z = DesertConstants.DESERT_ORIGIN.getZ();        // 1160
    private static final int DESERT_MAX_Z = DESERT_MIN_Z + 323;                          // 1483

    private StardewBiomePatcher() {}

    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel serverLevel)) return;
        if (!serverLevel.dimension().equals(ModDimensions.STARDEW_VALLEY)) return;
        if (!(event.getChunk() instanceof LevelChunk chunk)) return;

        // 判断此 chunk 是否与沙漠区域重叠
        boolean isDesertChunk = isDesertChunk(chunk.getPos());

        Holder<Biome> targetBiome;
        if (isDesertChunk) {
            targetBiome = serverLevel.registryAccess()
                    .registryOrThrow(Registries.BIOME)
                    .getHolderOrThrow(CALICO_DESERT_KEY);
        } else {
            targetBiome = serverLevel.registryAccess()
                    .registryOrThrow(Registries.BIOME)
                    .getHolderOrThrow(STARDEW_DEFAULT_KEY);
        }

        boolean modified = false;

        LevelChunkSection[] sections = chunk.getSections();
        for (int sIdx = 0; sIdx < sections.length; sIdx++) {
            LevelChunkSection section = sections[sIdx];
            if (section == null) continue;

            PalettedContainerRO<Holder<Biome>> biomesRO = section.getBiomes();
            if (!(biomesRO instanceof PalettedContainer<Holder<Biome>> biomes)) continue;

            // Biome container is 4×4×4 (one biome per 4-block cube)
            for (int bx = 0; bx < 4; bx++) {
                for (int by = 0; by < 4; by++) {
                    for (int bz = 0; bz < 4; bz++) {
                        Holder<Biome> current = biomes.get(bx, by, bz);
                        if (shouldReplace(current, isDesertChunk)) {
                            biomes.getAndSetUnchecked(bx, by, bz, targetBiome);
                            modified = true;
                        }
                    }
                }
            }
        }

        if (modified) {
            chunk.setUnsaved(true);
        }
    }

    /**
     * Returns true if this chunk's block range overlaps the desert schem bounding box.
     */
    private static boolean isDesertChunk(ChunkPos pos) {
        int chunkMinX = pos.getMinBlockX();
        int chunkMaxX = pos.getMaxBlockX();
        int chunkMinZ = pos.getMinBlockZ();
        int chunkMaxZ = pos.getMaxBlockZ();
        return chunkMaxX >= DESERT_MIN_X && chunkMinX <= DESERT_MAX_X
                && chunkMaxZ >= DESERT_MIN_Z && chunkMinZ <= DESERT_MAX_Z;
    }

    /**
     * Returns true for biomes that should be replaced with the target biome.
     * <ul>
     *   <li>非沙漠区域：替换所有 vanilla (minecraft:*) 生物群系为 stardew_default。</li>
     *   <li>沙漠区域：替换所有 vanilla 以及 stardewcraft:stardew_default（之前被
     *       误覆写的 chunk 也要纠正）为 calico_desert，但保留已是 calico_desert 的格子。</li>
     * </ul>
     */
    private static boolean shouldReplace(Holder<Biome> biome, boolean isDesertChunk) {
        return biome.unwrapKey().map(key -> {
            String ns = key.location().getNamespace();
            String path = key.location().getPath();
            if ("minecraft".equals(ns)) return true;
            if (isDesertChunk && "stardewcraft".equals(ns) && "stardew_default".equals(path)) {
                return true;
            }
            return false;
        }).orElse(false);
    }
}
