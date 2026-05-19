package com.stardew.craft.festival;

import com.stardew.craft.StardewCraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record FestivalMapPatch(
    String overlayId,
    BlockPos origin,
    int width,
    int height,
    int length,
    List<FestivalMapPatchEntry> entries
) {
    public static FestivalMapPatch build(FestivalMapOverlayDefinition definition) {
        SchematicSnapshot base = SchematicSnapshot.load(definition.baseSchematicPath());
        SchematicSnapshot festival = SchematicSnapshot.load(definition.festivalSchematicPath());
        if (!base.hasSameSize(festival)) {
            StardewCraft.LOGGER.error("[FESTIVAL_OVERLAY] Cannot build patch {}: base/festival size mismatch ({}x{}x{} vs {}x{}x{})",
                definition.overlayId(), base.width(), base.height(), base.length(), festival.width(), festival.height(), festival.length());
            return new FestivalMapPatch(definition.overlayId(), definition.origin(), 0, 0, 0, List.of());
        }

        List<FestivalMapPatchEntry> entries = new ArrayList<>();
        for (int localY = 0; localY < base.height(); localY++) {
            for (int localZ = 0; localZ < base.length(); localZ++) {
                for (int localX = 0; localX < base.width(); localX++) {
                    BlockState baseState = base.block(localX, localY, localZ);
                    BlockState festivalState = festival.block(localX, localY, localZ);
                    CompoundTag baseBlockEntity = base.blockEntity(localX, localY, localZ);
                    CompoundTag festivalBlockEntity = festival.blockEntity(localX, localY, localZ);
                    if (!Objects.equals(baseState, festivalState) || !tagEquals(baseBlockEntity, festivalBlockEntity)) {
                        entries.add(new FestivalMapPatchEntry(
                            new BlockPos(localX, localY, localZ),
                            baseState,
                            festivalState,
                            copyOrNull(baseBlockEntity),
                            copyOrNull(festivalBlockEntity)
                        ));
                    }
                }
            }
        }
        StardewCraft.LOGGER.info("[FESTIVAL_OVERLAY] Built patch {} with {} changed blocks", definition.overlayId(), entries.size());
        return new FestivalMapPatch(definition.overlayId(), definition.origin(), base.width(), base.height(), base.length(), List.copyOf(entries));
    }

    public boolean isEmpty() {
        return entries.isEmpty();
    }

    private static boolean tagEquals(CompoundTag first, CompoundTag second) {
        if (first == null || first.isEmpty()) {
            return second == null || second.isEmpty();
        }
        return first.equals(second);
    }

    private static CompoundTag copyOrNull(CompoundTag tag) {
        return tag == null || tag.isEmpty() ? null : tag.copy();
    }
}