package com.stardew.craft.festival;

import com.stardew.craft.StardewCraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;

public final class FestivalMapOverlayManager {
    private static final int BLOCKS_PER_TICK = 1500;
    private static final Map<String, FestivalMapPatch> PATCH_CACHE = new HashMap<>();

    private FestivalMapOverlayManager() {
    }

    public static boolean beginApply(ServerLevel level, FestivalDefinition festival, int year, int season, int day) {
        if (level == null || festival == null || festival.mapOverlayId().isBlank()) {
            return false;
        }
        FestivalMapOverlayDefinition definition = FestivalMapOverlayRegistry.get(festival.mapOverlayId()).orElse(null);
        if (definition == null) {
            StardewCraft.LOGGER.warn("[FESTIVAL_OVERLAY] Overlay {} for festival {} is not registered yet", festival.mapOverlayId(), festival.id());
            return false;
        }
        FestivalWorldData data = FestivalWorldData.get(level);
        FestivalMapOverlayState state = data.getOrCreateOverlayState(definition.overlayId());
        if (state.phase() == FestivalMapOverlayPhase.APPLIED || state.phase() == FestivalMapOverlayPhase.APPLYING) {
            return true;
        }
        state.begin(festival.id(), year, season, day, FestivalMapOverlayPhase.APPLYING);
        forceChunks(level, patch(definition), true);
        data.setDirty();
        return true;
    }

    public static boolean beginRestore(ServerLevel level, String overlayId) {
        if (level == null || overlayId == null || overlayId.isBlank()) {
            return false;
        }
        FestivalMapOverlayDefinition definition = FestivalMapOverlayRegistry.get(overlayId).orElse(null);
        if (definition == null) {
            return false;
        }
        FestivalWorldData data = FestivalWorldData.get(level);
        FestivalMapOverlayState state = data.getOrCreateOverlayState(definition.overlayId());
        if (state.phase() != FestivalMapOverlayPhase.APPLIED) {
            return false;
        }
        state.begin(state.festivalId(), state.year(), state.season(), state.day(), FestivalMapOverlayPhase.RESTORING);
        forceChunks(level, patch(definition), true);
        data.setDirty();
        return true;
    }

    public static boolean isApplied(ServerLevel level, String overlayId) {
        if (level == null || overlayId == null || overlayId.isBlank()) {
            return false;
        }
        return FestivalWorldData.get(level).getOverlayState(overlayId)
            .map(state -> state.phase() == FestivalMapOverlayPhase.APPLIED)
            .orElse(false);
    }

    public static boolean isRestored(ServerLevel level, String overlayId) {
        if (level == null || overlayId == null || overlayId.isBlank()) {
            return false;
        }
        return FestivalWorldData.get(level).getOverlayState(overlayId)
            .map(state -> state.phase() == FestivalMapOverlayPhase.RESTORED || state.phase() == FestivalMapOverlayPhase.NONE)
            .orElse(true);
    }

    public static void tick(ServerLevel level) {
        if (level == null) {
            return;
        }
        FestivalWorldData data = FestivalWorldData.get(level);
        for (FestivalMapOverlayState state : data.overlayStates()) {
            if (state.phase() != FestivalMapOverlayPhase.APPLYING && state.phase() != FestivalMapOverlayPhase.RESTORING) {
                continue;
            }
            FestivalMapOverlayDefinition definition = FestivalMapOverlayRegistry.get(state.overlayId()).orElse(null);
            if (definition == null) {
                continue;
            }
            FestivalMapPatch patch = patch(definition);
            applyBatch(level, state, patch);
            data.setDirty();
        }
    }

    private static void applyBatch(ServerLevel level, FestivalMapOverlayState state, FestivalMapPatch patch) {
        if (patch.isEmpty()) {
            state.setPhase(state.phase() == FestivalMapOverlayPhase.APPLYING ? FestivalMapOverlayPhase.APPLIED : FestivalMapOverlayPhase.RESTORED);
            forceChunks(level, patch, false);
            return;
        }
        int end = Math.min(state.cursor() + BLOCKS_PER_TICK, patch.entries().size());
        boolean applying = state.phase() == FestivalMapOverlayPhase.APPLYING;
        for (int index = state.cursor(); index < end; index++) {
            FestivalMapPatchEntry entry = patch.entries().get(index);
            BlockPos worldPos = patch.origin().offset(entry.relativePos());
            BlockState targetState = applying ? entry.festivalState() : entry.baseState();
            CompoundTag targetBlockEntity = applying ? entry.festivalBlockEntityTag() : entry.baseBlockEntityTag();
            level.setBlock(worldPos, targetState, Block.UPDATE_CLIENTS);
            applyBlockEntity(level, worldPos, targetBlockEntity);
        }
        state.setCursor(end);
        if (end >= patch.entries().size()) {
            state.setPhase(applying ? FestivalMapOverlayPhase.APPLIED : FestivalMapOverlayPhase.RESTORED);
            forceChunks(level, patch, false);
            StardewCraft.LOGGER.info("[FESTIVAL_OVERLAY] {} overlay {} ({} blocks)", applying ? "Applied" : "Restored", state.overlayId(), patch.entries().size());
        }
    }

    private static void applyBlockEntity(ServerLevel level, BlockPos pos, CompoundTag rawTag) {
        if (rawTag == null || rawTag.isEmpty()) {
            level.removeBlockEntity(pos);
            return;
        }
        CompoundTag normalized = rawTag.copy();
        if (!normalized.contains("id", Tag.TAG_STRING) && normalized.contains("Id", Tag.TAG_STRING)) {
            normalized.putString("id", normalized.getString("Id"));
        }
        normalized.putInt("x", pos.getX());
        normalized.putInt("y", pos.getY());
        normalized.putInt("z", pos.getZ());
        if (!normalized.contains("id", Tag.TAG_STRING)) {
            return;
        }
        BlockEntity blockEntity = BlockEntity.loadStatic(pos, level.getBlockState(pos), normalized, level.registryAccess());
        if (blockEntity != null) {
            level.setBlockEntity(blockEntity);
            blockEntity.setChanged();
        }
    }

    private static FestivalMapPatch patch(FestivalMapOverlayDefinition definition) {
        return PATCH_CACHE.computeIfAbsent(definition.overlayId(), ignored -> FestivalMapPatch.build(definition));
    }

    private static void forceChunks(ServerLevel level, FestivalMapPatch patch, boolean forced) {
        if (patch.width() <= 0 || patch.length() <= 0) {
            return;
        }
        int minChunkX = patch.origin().getX() >> 4;
        int minChunkZ = patch.origin().getZ() >> 4;
        int maxChunkX = (patch.origin().getX() + patch.width() - 1) >> 4;
        int maxChunkZ = (patch.origin().getZ() + patch.length() - 1) >> 4;
        for (int chunkX = minChunkX; chunkX <= maxChunkX; chunkX++) {
            for (int chunkZ = minChunkZ; chunkZ <= maxChunkZ; chunkZ++) {
                level.setChunkForced(chunkX, chunkZ, forced);
            }
        }
    }
}