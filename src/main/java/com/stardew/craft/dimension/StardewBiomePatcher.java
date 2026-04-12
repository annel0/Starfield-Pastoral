package com.stardew.craft.dimension;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.core.ModDimensions;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
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

    private StardewBiomePatcher() {}

    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel serverLevel)) return;
        if (!serverLevel.dimension().equals(ModDimensions.STARDEW_VALLEY)) return;
        if (!(event.getChunk() instanceof LevelChunk chunk)) return;

        Holder<Biome> defaultBiome = serverLevel.registryAccess()
                .registryOrThrow(Registries.BIOME)
                .getHolderOrThrow(STARDEW_DEFAULT_KEY);

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
                        if (shouldReplace(current)) {
                            biomes.getAndSetUnchecked(bx, by, bz, defaultBiome);
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
     * Returns true for biomes that should be replaced with stardew_default.
     * Replaces any vanilla (minecraft:*) biome — custom biomes already set
     * via /fillbiome or other stardewcraft biomes are kept.
     */
    private static boolean shouldReplace(Holder<Biome> biome) {
        return biome.unwrapKey()
                .map(key -> key.location().getNamespace().equals("minecraft"))
                .orElse(false);
    }
}
