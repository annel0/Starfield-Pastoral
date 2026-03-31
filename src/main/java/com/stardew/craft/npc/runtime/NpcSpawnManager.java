package com.stardew.craft.npc.runtime;

import com.google.gson.JsonObject;
import com.stardew.craft.StardewCraft;
import com.stardew.craft.core.ModDimensions;
import com.stardew.craft.entity.ModEntities;
import com.stardew.craft.entity.npc.StardewNpcEntity;
import com.stardew.craft.npc.data.NpcCapabilityProfile;
import com.stardew.craft.npc.data.NpcDataRegistry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;
import java.util.Locale;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Prototype spawner for a limited roster so NPCs are visible in-world quickly.
 */
@SuppressWarnings("null")
public final class NpcSpawnManager {
    private static final int FULL_SWEEP_INTERVAL_TICKS = 200;
    private static final int SPAWN_CHECK_INTERVAL_TICKS = 20;
    private static final int RESPAWN_CONFIRM_MISSES = 3;
    private static final int RESPAWN_COOLDOWN_TICKS = 100;
    private static final AABB GLOBAL_NPC_SCAN = new AABB(-30_000_000D, -2_048D, -30_000_000D, 30_000_000D, 4_096D, 30_000_000D);

    private static int tickCounter;
    private static boolean initialSweepDone;
    private static MinecraftServer activeServer;
    private static final Map<String, UUID> TRACKED_NPC_UUIDS = new LinkedHashMap<>();
    private static final Map<String, Integer> TRACKED_MISS_COUNTS = new LinkedHashMap<>();
    private static final Map<String, Long> LAST_SPAWN_GAME_TIME = new LinkedHashMap<>();

    private NpcSpawnManager() {
    }

    /**
     * Called when the last player leaves the Stardew Valley dimension.
     * Teleports every tracked NPC to their current schedule target so that
     * when the player returns they are at the correct position.
     */
    public static void onAllPlayersLeft(ServerLevel level) {
        for (Map.Entry<String, UUID> entry : TRACKED_NPC_UUIDS.entrySet()) {
            String npcId = entry.getKey();
            StardewNpcEntity npc = getTrackedNpc(level, npcId);
            if (npc == null) continue;
            NpcRuntimeState state = NpcRuntimeDataManager.get(level).states().get(npcId);
            Vec3 sharedSpawn = Vec3.atCenterOf(level.getSharedSpawnPos());
            NpcScheduleRuntimeService.TargetPoint target = NpcScheduleRuntimeService.resolveWorldTarget(level, state, sharedSpawn);
            if (target != null && target.position() != null) {
                npc.setPos(target.position().x, target.position().y, target.position().z);
                npc.setDeltaMovement(Vec3.ZERO);
            }
        }
    }

    /**
     * Called when a player enters the Stardew Valley dimension.
     * Forces an immediate full sweep to clean up duplicates and snap NPCs.
     */
    public static void onPlayerEntered(ServerLevel level) {
        Set<String> implementedIds = new HashSet<>();
        for (Map.Entry<String, NpcCapabilityProfile> entry : NpcDataRegistry.capabilities().entrySet()) {
            if (entry.getValue().implemented()) {
                implementedIds.add(canonicalNpcId(entry.getValue().npcId()));
            }
        }
        cleanupUnknownAndDuplicated(level, implementedIds);
        initialSweepDone = true;
    }

    public static void onNpcJoin(ServerLevel level, StardewNpcEntity npc) {
        if (!ModDimensions.STARDEW_VALLEY.equals(level.dimension())) {
            return;
        }

        ensureServerContext(level);

        String npcId = canonicalNpcId(npc.getNpcId());
        if (npcId == null || npcId.isBlank()) {
            discardWithReason(npc, "join_invalid_blank_id");
            return;
        }

        UUID tracked = TRACKED_NPC_UUIDS.get(npcId);
        if (tracked == null) {
            TRACKED_NPC_UUIDS.put(npcId, npc.getUUID());
            reconcileSingletonByNpcId(level, npcId, npc.getUUID());
            return;
        }

        if (tracked.equals(npc.getUUID())) {
            reconcileSingletonByNpcId(level, npcId, npc.getUUID());
            return;
        }

        var existing = level.getEntity(tracked);
        if (existing instanceof StardewNpcEntity existingNpc && !existingNpc.isRemoved()) {
            discardWithReason(npc, "join_duplicate_uuid_conflict");
            reconcileSingletonByNpcId(level, npcId, existingNpc.getUUID());
            return;
        }

        TRACKED_NPC_UUIDS.put(npcId, npc.getUUID());
        reconcileSingletonByNpcId(level, npcId, npc.getUUID());
    }

    public static void tick(ServerLevel level) {
        if (!ModDimensions.STARDEW_VALLEY.equals(level.dimension())) {
            return;
        }

        ensureServerContext(level);

        JsonObject spawnRoot = NpcDataRegistry.events().get("default_spawns");
        JsonObject spawns = null;
        if (spawnRoot != null && spawnRoot.has("spawns") && spawnRoot.get("spawns").isJsonObject()) {
            spawns = spawnRoot.getAsJsonObject("spawns");
        }
        Set<String> implementedIds = new HashSet<>();
        for (Map.Entry<String, NpcCapabilityProfile> entry : NpcDataRegistry.capabilities().entrySet()) {
            String canonicalId = canonicalNpcId(entry.getValue().npcId());
            if (entry.getValue().implemented()) {
                implementedIds.add(canonicalId);
            }
        }

        tickCounter++;

        if (!initialSweepDone) {
            cleanupUnknownAndDuplicated(level, implementedIds);
            initialSweepDone = true;
        } else if (tickCounter % FULL_SWEEP_INTERVAL_TICKS == 0) {
            cleanupUnknownAndDuplicated(level, implementedIds);
        }

        if (tickCounter % SPAWN_CHECK_INTERVAL_TICKS != 0) {
            return;
        }

        Map<String, StardewNpcEntity> loadedByNpcId = collectAndDeduplicateLoaded(level, implementedIds);

        for (Map.Entry<String, NpcCapabilityProfile> entry : NpcDataRegistry.capabilities().entrySet()) {
            NpcCapabilityProfile profile = entry.getValue();
            if (!profile.implemented()) {
                continue;
            }
            String npcId = canonicalNpcId(profile.npcId());
            JsonObject pos = resolveSpawnPos(spawns, npcId, profile.npcId());

            double x = 150.0D;
            double y = -13.0D;
            double z = 119.0D;
            float yaw = 0.0F;
            if (pos != null) {
                x = pos.has("x") ? pos.get("x").getAsDouble() : x;
                y = pos.has("y") ? pos.get("y").getAsDouble() : y;
                z = pos.has("z") ? pos.get("z").getAsDouble() : z;
                yaw = pos.has("yaw") ? pos.get("yaw").getAsFloat() : yaw;
            }

            // Prefer current runtime schedule target so newly spawned NPC starts at the
            // correct active location instead of a stale static bootstrap point.
            Vec3 runtimeFallback = resolveRuntimeFallbackSpawn(level, npcId);
            if (runtimeFallback != null) {
                x = runtimeFallback.x;
                y = runtimeFallback.y;
                z = runtimeFallback.z;
            } else if (pos == null) {
                continue;
            }

            double spawnX = x;
            double spawnY = y;
            double spawnZ = z;

            // Chunk forcing is fully managed by NpcChunkForceManager now.
            // SpawnManager only tracks UUID→entity mapping.

            if (hasTrackedNpc(level, npcId)) {
                TRACKED_MISS_COUNTS.put(npcId, 0);
                continue;
            }

            StardewNpcEntity existing = loadedByNpcId.get(npcId);
            if (existing != null) {
                TRACKED_NPC_UUIDS.put(npcId, existing.getUUID());
                TRACKED_MISS_COUNTS.put(npcId, 0);
                continue;
            }

            int misses = TRACKED_MISS_COUNTS.getOrDefault(npcId, 0) + 1;
            TRACKED_MISS_COUNTS.put(npcId, misses);
            if (misses < RESPAWN_CONFIRM_MISSES) {
                continue;
            }

            long now = level.getGameTime();
            Long lastSpawn = LAST_SPAWN_GAME_TIME.get(npcId);
            if (lastSpawn != null) {
                long delta = now - lastSpawn;
                if (delta < RESPAWN_COOLDOWN_TICKS) {
                    continue;
                }
            }

            StardewNpcEntity npc = ModEntities.STARDEW_NPC.get().create(level);
            if (npc == null) {
                continue;
            }

            npc.setNpcId(npcId);
            npc.moveTo(spawnX, spawnY, spawnZ, yaw, 0.0F);
            npc.setCustomNameVisible(false);
            boolean added = level.addFreshEntity(npc);
            if (!added) {
                TRACKED_NPC_UUIDS.remove(npcId);
                StardewCraft.LOGGER.warn(
                    "Failed to spawn prototype NPC '{}' at ({}, {}, {}) [addFreshEntity=false]",
                    npcId,
                    spawnX,
                    spawnY,
                    spawnZ
                );
                continue;
            }
            TRACKED_NPC_UUIDS.put(npcId, npc.getUUID());
            TRACKED_MISS_COUNTS.put(npcId, 0);
            LAST_SPAWN_GAME_TIME.put(npcId, now);
        }
    }

    private static Map<String, StardewNpcEntity> collectAndDeduplicateLoaded(ServerLevel level, Set<String> implementedIds) {
        List<StardewNpcEntity> all = level.getEntitiesOfClass(StardewNpcEntity.class, GLOBAL_NPC_SCAN);
        Map<String, StardewNpcEntity> byId = new LinkedHashMap<>();
        List<StardewNpcEntity> toDiscard = new ArrayList<>();

        for (StardewNpcEntity entity : all) {
            String id = entity.getNpcId();
            String canonicalId = canonicalNpcId(id);
            if (canonicalId == null || canonicalId.isBlank() || !implementedIds.contains(canonicalId)) {
                toDiscard.add(entity);
                continue;
            }

            StardewNpcEntity keep = byId.putIfAbsent(canonicalId, entity);
            if (keep != null && keep != entity) {
                toDiscard.add(entity);
            }
        }

        for (StardewNpcEntity duplicate : toDiscard) {
            discardWithReason(duplicate, "collect_deduplicate");
        }

        for (Map.Entry<String, StardewNpcEntity> entry : byId.entrySet()) {
            TRACKED_NPC_UUIDS.put(entry.getKey(), entry.getValue().getUUID());
        }

        return byId;
    }

    private static void cleanupUnknownAndDuplicated(ServerLevel level, Set<String> implementedIds) {
        List<StardewNpcEntity> all = level.getEntitiesOfClass(StardewNpcEntity.class, GLOBAL_NPC_SCAN);
        Set<String> seen = new HashSet<>();

        for (StardewNpcEntity entity : all) {
            String id = canonicalNpcId(entity.getNpcId());
            if (id == null || id.isBlank() || !implementedIds.contains(id)) {
                discardWithReason(entity, "sweep_unknown_or_unimplemented");
                continue;
            }

            if (!seen.add(id)) {
                discardWithReason(entity, "sweep_duplicate_same_id");
                continue;
            }

            TRACKED_NPC_UUIDS.put(id, entity.getUUID());
        }
    }

    private static boolean hasTrackedNpc(ServerLevel level, String npcId) {
        String canonicalId = canonicalNpcId(npcId);
        UUID tracked = TRACKED_NPC_UUIDS.get(canonicalId);
        if (tracked == null) {
            StardewNpcEntity byId = findLoadedNpcById(level, canonicalId, null);
            if (byId != null) {
                TRACKED_NPC_UUIDS.put(canonicalId, byId.getUUID());
                TRACKED_MISS_COUNTS.put(canonicalId, 0);
                return true;
            }
            return false;
        }

        var entity = level.getEntity(tracked);
        if (!(entity instanceof StardewNpcEntity npc)) {
            StardewNpcEntity byId = findLoadedNpcById(level, canonicalId, null);
            if (byId != null) {
                TRACKED_NPC_UUIDS.put(canonicalId, byId.getUUID());
                TRACKED_MISS_COUNTS.put(canonicalId, 0);
                return true;
            }
            TRACKED_NPC_UUIDS.remove(canonicalId);
            TRACKED_MISS_COUNTS.put(canonicalId, TRACKED_MISS_COUNTS.getOrDefault(canonicalId, 0) + 1);
            return false;
        }

        // Removed/dead entities can remain addressable by UUID briefly; treat them as missing
        // so prototype respawn can recover immediately.
        if (npc.isRemoved() || !npc.isAlive()) {
            TRACKED_NPC_UUIDS.remove(canonicalId);
            TRACKED_MISS_COUNTS.put(canonicalId, TRACKED_MISS_COUNTS.getOrDefault(canonicalId, 0) + 1);
            return false;
        }

        if (!canonicalId.equals(canonicalNpcId(npc.getNpcId()))) {
            TRACKED_NPC_UUIDS.remove(canonicalId);
            TRACKED_MISS_COUNTS.put(canonicalId, TRACKED_MISS_COUNTS.getOrDefault(canonicalId, 0) + 1);
            return false;
        }
        TRACKED_MISS_COUNTS.put(canonicalId, 0);
        return true;
    }

    private static StardewNpcEntity findLoadedNpcById(ServerLevel level, String canonicalNpcId, UUID excludeUuid) {
        if (level == null || canonicalNpcId == null || canonicalNpcId.isBlank()) {
            return null;
        }
        for (StardewNpcEntity entity : level.getEntitiesOfClass(StardewNpcEntity.class, GLOBAL_NPC_SCAN)) {
            if (entity.isRemoved() || !entity.isAlive()) {
                continue;
            }
            if (excludeUuid != null && excludeUuid.equals(entity.getUUID())) {
                continue;
            }
            if (canonicalNpcId.equals(canonicalNpcId(entity.getNpcId()))) {
                return entity;
            }
        }
        return null;
    }

    private static void reconcileSingletonByNpcId(ServerLevel level, String canonicalNpcId, UUID preferredUuid) {
        if (level == null || canonicalNpcId == null || canonicalNpcId.isBlank()) {
            return;
        }

        StardewNpcEntity keeper = null;
        if (preferredUuid != null) {
            var preferred = level.getEntity(preferredUuid);
            if (preferred instanceof StardewNpcEntity preferredNpc
                && !preferredNpc.isRemoved()
                && preferredNpc.isAlive()
                && canonicalNpcId.equals(canonicalNpcId(preferredNpc.getNpcId()))) {
                keeper = preferredNpc;
            }
        }

        List<StardewNpcEntity> sameId = new ArrayList<>();
        for (StardewNpcEntity entity : level.getEntitiesOfClass(StardewNpcEntity.class, GLOBAL_NPC_SCAN)) {
            if (!canonicalNpcId.equals(canonicalNpcId(entity.getNpcId()))) {
                continue;
            }
            sameId.add(entity);
            if (keeper == null && !entity.isRemoved() && entity.isAlive()) {
                keeper = entity;
            }
        }

        if (keeper == null) {
            return;
        }

        for (StardewNpcEntity entity : sameId) {
            if (entity == keeper) {
                continue;
            }
            discardWithReason(entity, "join_singleton_reconcile");
        }

        TRACKED_NPC_UUIDS.put(canonicalNpcId, keeper.getUUID());
        TRACKED_MISS_COUNTS.put(canonicalNpcId, 0);
    }

    public static StardewNpcEntity getTrackedNpc(ServerLevel level, String npcId) {
        if (level == null || npcId == null || npcId.isBlank()) {
            return null;
        }

        String canonicalId = canonicalNpcId(npcId);
        UUID tracked = TRACKED_NPC_UUIDS.get(canonicalId);
        if (tracked == null) {
            return null;
        }

        var entity = level.getEntity(tracked);
        if (!(entity instanceof StardewNpcEntity npc) || npc.isRemoved() || !npc.isAlive()) {
            TRACKED_NPC_UUIDS.remove(canonicalId);
            return null;
        }

        if (!canonicalId.equals(canonicalNpcId(npc.getNpcId()))) {
            TRACKED_NPC_UUIDS.remove(canonicalId);
            return null;
        }

        return npc;
    }

    private static JsonObject resolveSpawnPos(JsonObject spawns, String canonicalNpcId, String rawNpcId) {
        if (spawns == null) {
            return null;
        }
        if (spawns.has(canonicalNpcId) && spawns.get(canonicalNpcId).isJsonObject()) {
            return spawns.getAsJsonObject(canonicalNpcId);
        }
        if (rawNpcId != null && spawns.has(rawNpcId) && spawns.get(rawNpcId).isJsonObject()) {
            return spawns.getAsJsonObject(rawNpcId);
        }
        return null;
    }

    private static Vec3 resolveRuntimeFallbackSpawn(ServerLevel level, String npcId) {
        if (level == null || npcId == null || npcId.isBlank()) {
            return null;
        }
        NpcRuntimeState state = NpcRuntimeDataManager.get(level).states().get(npcId);
        if (state == null) {
            return null;
        }
        Vec3 sharedSpawn = Vec3.atCenterOf(level.getSharedSpawnPos());
        NpcScheduleRuntimeService.TargetPoint target = NpcScheduleRuntimeService.resolveWorldTarget(level, state, sharedSpawn);
        if (target == null || target.position() == null) {
            return null;
        }

        // If schedule resolution fell back to world shared spawn, avoid spawning NPC at origin-like
        // coordinates and let static/explicit spawn points handle bootstrap instead.
        if (target.position().distanceToSqr(sharedSpawn) < 0.25D) {
            return null;
        }
        return target.position();
    }

    private static String canonicalNpcId(String npcId) {
        if (npcId == null) {
            return null;
        }
        return npcId.trim().toLowerCase(Locale.ROOT);
    }

    private static void ensureServerContext(ServerLevel level) {
        MinecraftServer server = level.getServer();
        if (activeServer == server) {
            return;
        }

        activeServer = server;
        tickCounter = 0;
        initialSweepDone = false;
        TRACKED_NPC_UUIDS.clear();
        TRACKED_MISS_COUNTS.clear();
        LAST_SPAWN_GAME_TIME.clear();
        StardewCraft.LOGGER.info("Reset NPC prototype spawn tracking for server '{}'.", server.getWorldData().getLevelName());
    }

    private static void discardWithReason(StardewNpcEntity npc, String reason) {
        if (npc == null || npc.isRemoved()) {
            return;
        }
        StardewCraft.LOGGER.info(
            "Discard NPC id='{}' uuid={} reason={} pos=({}, {}, {})",
            npc.getNpcId(),
            npc.getUUID(),
            reason,
            npc.getX(),
            npc.getY(),
            npc.getZ()
        );
        npc.discard();
    }

    public static boolean isOfficialInstance(StardewNpcEntity npc) {
        if (npc == null || npc.isRemoved() || npc.level() == null || npc.level().isClientSide) {
            return false;
        }
        String id = canonicalNpcId(npc.getNpcId());
        if (id == null || id.isBlank()) {
            return false;
        }
        UUID tracked = TRACKED_NPC_UUIDS.get(id);
        if (tracked == null) {
            // Only forgive during early boot (first 60 ticks) before initial sweep runs.
            // After that, untracked entities are NOT official.
            return !initialSweepDone;
        }
        return tracked.equals(npc.getUUID());
    }

    public static SpawnDebugSnapshot getDebugSnapshot(ServerLevel level, String npcId) {
        if (level == null || npcId == null || npcId.isBlank()) {
            return null;
        }

        String canonicalId = canonicalNpcId(npcId);
        UUID trackedUuid = TRACKED_NPC_UUIDS.get(canonicalId);
        int missCount = TRACKED_MISS_COUNTS.getOrDefault(canonicalId, 0);
        Long lastSpawn = LAST_SPAWN_GAME_TIME.get(canonicalId);
        long spawnAge = lastSpawn == null ? -1L : (level.getGameTime() - lastSpawn);

        int loadedCount = 0;
        UUID firstLoadedUuid = null;
        for (StardewNpcEntity entity : level.getEntitiesOfClass(StardewNpcEntity.class, GLOBAL_NPC_SCAN)) {
            if (!canonicalId.equals(canonicalNpcId(entity.getNpcId()))) {
                continue;
            }
            loadedCount++;
            if (firstLoadedUuid == null) {
                firstLoadedUuid = entity.getUUID();
            }
        }

        String chunk = NpcChunkForceManager.currentForcedTargetChunk(canonicalId);

        boolean trackedAlive = false;
        if (trackedUuid != null) {
            var trackedEntity = level.getEntity(trackedUuid);
            trackedAlive = trackedEntity instanceof StardewNpcEntity npc && !npc.isRemoved() && npc.isAlive();
        }

        return new SpawnDebugSnapshot(
            canonicalId,
            trackedUuid == null ? "<none>" : trackedUuid.toString(),
            trackedAlive,
            loadedCount,
            firstLoadedUuid == null ? "<none>" : firstLoadedUuid.toString(),
            missCount,
            spawnAge,
            chunk
        );
    }

    public record SpawnDebugSnapshot(
        String npcId,
        String trackedUuid,
        boolean trackedAlive,
        int loadedCount,
        String firstLoadedUuid,
        int missCount,
        long spawnAgeTicks,
        String forcedChunk
    ) {
    }

}
