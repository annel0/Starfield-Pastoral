package com.stardew.craft.museum;

import com.stardew.craft.block.ModBlocks;
import com.stardew.craft.block.decor.MapDecorStaticBlock;
import com.stardew.craft.core.ModDimensions;
import com.stardew.craft.interior.InteriorRegionRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class MuseumExhibitStandManager {
    private static final String MUSEUM_REGION_ID = "museum";
    private static final String STARDEW_DIMENSION_ID = ModDimensions.STARDEW_VALLEY.location().toString();

    private MuseumExhibitStandManager() {
    }

    public static boolean isManagedMuseumStand(Level level, BlockPos pos) {
        return level != null
            && ModDimensions.STARDEW_VALLEY.equals(level.dimension())
            && isMuseumRegionPos(pos);
    }

    public static boolean isMuseumRegionPos(BlockPos pos) {
        return pos != null
            && InteriorRegionRegistry.fixedInteriorAt(pos)
                .map(region -> MUSEUM_REGION_ID.equals(region.id()))
                .orElse(false);
    }

    public static List<BlockPos> collectManagedStandPositions(ServerLevel level) {
        if (level == null || !ModDimensions.STARDEW_VALLEY.equals(level.dimension())) {
            return List.of();
        }
        InteriorRegionRegistry.InteriorRegion museumRegion = findMuseumRegion();
        if (museumRegion == null) {
            return List.of();
        }

        List<BlockPos> stands = new ArrayList<>();
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        for (int y = museumRegion.minY(); y <= museumRegion.maxY(); y++) {
            for (int z = museumRegion.minZ(); z <= museumRegion.maxZ(); z++) {
                for (int x = museumRegion.minX(); x <= museumRegion.maxX(); x++) {
                    mutablePos.set(x, y, z);
                    BlockState state = level.getBlockState(mutablePos);
                    if (state.is(ModBlocks.MUSEUM_EXHIBIT_STAND.get())
                        && state.hasProperty(MapDecorStaticBlock.PART)
                        && state.getValue(MapDecorStaticBlock.PART) == MapDecorStaticBlock.Part.MAIN) {
                        stands.add(mutablePos.immutable());
                    }
                }
            }
        }
        stands.sort(Comparator.<BlockPos>comparingInt(BlockPos::getX)
            .thenComparingInt(BlockPos::getZ)
            .thenComparingInt(BlockPos::getY));
        return stands;
    }

    @Nullable
    public static String normalizeManagedStandKey(@Nullable String standKey) {
        if (standKey == null || standKey.isBlank()) {
            return null;
        }

        String dimensionId = STARDEW_DIMENSION_ID;
        String coordPart = standKey.trim();
        int separator = coordPart.indexOf('|');
        if (separator >= 0) {
            dimensionId = coordPart.substring(0, separator);
            coordPart = coordPart.substring(separator + 1);
        }

        if (!STARDEW_DIMENSION_ID.equals(dimensionId)) {
            return null;
        }

        BlockPos pos = parsePos(coordPart);
        if (pos == null) {
            return null;
        }
        if (!isMuseumRegionPos(pos)) {
            return null;
        }
        return STARDEW_DIMENSION_ID + "|" + pos.getX() + "," + pos.getY() + "," + pos.getZ();
    }

    @Nullable
    public static BlockPos parseManagedStandPos(ResourceKey<Level> dimension, @Nullable String standKey) {
        if (!ModDimensions.STARDEW_VALLEY.equals(dimension)) {
            return null;
        }
        String normalized = normalizeManagedStandKey(standKey);
        if (normalized == null) {
            return null;
        }
        int separator = normalized.indexOf('|');
        return parsePos(normalized.substring(separator + 1));
    }

    @Nullable
    private static BlockPos parsePos(String coordPart) {
        if (coordPart == null || coordPart.isBlank()) {
            return null;
        }
        String[] parts = coordPart.split(",");
        if (parts.length != 3) {
            return null;
        }
        try {
            return new BlockPos(
                Integer.parseInt(parts[0].trim()),
                Integer.parseInt(parts[1].trim()),
                Integer.parseInt(parts[2].trim())
            );
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    @Nullable
    private static InteriorRegionRegistry.InteriorRegion findMuseumRegion() {
        for (InteriorRegionRegistry.InteriorRegion region : InteriorRegionRegistry.regions()) {
            if (MUSEUM_REGION_ID.equals(region.id())) {
                return region;
            }
        }
        return null;
    }
}