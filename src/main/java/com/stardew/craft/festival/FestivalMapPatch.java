package com.stardew.craft.festival;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.block.portal.PortalTriggerBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
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
    public static FestivalMapPatch buildFromRuntimeBase(ServerLevel level, FestivalMapOverlayDefinition definition) {
        SchematicSnapshot festival = SchematicSnapshot.load(definition.festivalSchematicPath());
        if (festival.isEmpty()) {
            return null;
        }
        if (!matchesBounds(definition, festival)) {
            return null;
        }

        List<FestivalMapPatchEntry> entries = new ArrayList<>();
        for (int localY = 0; localY < festival.height(); localY++) {
            for (int localZ = 0; localZ < festival.length(); localZ++) {
                for (int localX = 0; localX < festival.width(); localX++) {
                    BlockPos worldPos = definition.origin().offset(localX, localY, localZ);
                    BlockState baseState = level.getBlockState(worldPos);
                    if (baseState.getBlock() instanceof PortalTriggerBlock) {
                        continue;
                    }
                    BlockState festivalState = festival.block(localX, localY, localZ);
                    CompoundTag baseBlockEntity = saveBlockEntity(level, worldPos);
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
        StardewCraft.LOGGER.info("[FESTIVAL_OVERLAY] Built runtime patch {} with {} changed blocks", definition.overlayId(), entries.size());
        return new FestivalMapPatch(definition.overlayId(), definition.origin(), festival.width(), festival.height(), festival.length(), List.copyOf(entries));
    }

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

    CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putString("OverlayId", overlayId);
        tag.putIntArray("Origin", new int[] { origin.getX(), origin.getY(), origin.getZ() });
        tag.putInt("Width", width);
        tag.putInt("Height", height);
        tag.putInt("Length", length);
        ListTag entryList = new ListTag();
        for (FestivalMapPatchEntry entry : entries) {
            entryList.add(entry.save());
        }
        tag.put("Entries", entryList);
        return tag;
    }

    static FestivalMapPatch load(CompoundTag tag) {
        int[] originArray = tag.getIntArray("Origin");
        BlockPos origin = originArray.length >= 3 ? new BlockPos(originArray[0], originArray[1], originArray[2]) : BlockPos.ZERO;
        List<FestivalMapPatchEntry> entries = new ArrayList<>();
        ListTag entryList = tag.getList("Entries", Tag.TAG_COMPOUND);
        for (int index = 0; index < entryList.size(); index++) {
            entries.add(FestivalMapPatchEntry.load(entryList.getCompound(index)));
        }
        return new FestivalMapPatch(
            tag.getString("OverlayId"),
            origin,
            tag.getInt("Width"),
            tag.getInt("Height"),
            tag.getInt("Length"),
            List.copyOf(entries)
        );
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

    private static boolean matchesBounds(FestivalMapOverlayDefinition definition, SchematicSnapshot snapshot) {
        int expectedWidth = Math.abs(definition.boundsMax().getX() - definition.boundsMin().getX()) + 1;
        int expectedHeight = Math.abs(definition.boundsMax().getY() - definition.boundsMin().getY()) + 1;
        int expectedLength = Math.abs(definition.boundsMax().getZ() - definition.boundsMin().getZ()) + 1;
        if (expectedWidth == snapshot.width() && expectedHeight == snapshot.height() && expectedLength == snapshot.length()) {
            return true;
        }
        StardewCraft.LOGGER.error("[FESTIVAL_OVERLAY] Schematic {} size mismatch for {}: expected {}x{}x{}, got {}x{}x{}",
            definition.festivalSchematicPath(), definition.overlayId(), expectedWidth, expectedHeight, expectedLength,
            snapshot.width(), snapshot.height(), snapshot.length());
        return false;
    }

    private static CompoundTag saveBlockEntity(ServerLevel level, BlockPos pos) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        return blockEntity == null ? null : blockEntity.saveWithFullMetadata(level.registryAccess());
    }
}