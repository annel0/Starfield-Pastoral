package com.stardew.craft.npc.runtime;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Manages forced chunk loading for NPC route targets and corridors.
 * Extracted from NpcCentralMovementService.
 */
@SuppressWarnings("null")
public final class NpcChunkForceManager {

    private static final int MAX_FORCED_CORRIDOR_CHUNK_DELTA = 16;

    private static final Map<String, Long> FORCED_TARGET_CHUNK_BY_NPC = new HashMap<>();
    private static final Map<String, Set<Long>> FORCED_ROUTE_CHUNKS_BY_NPC = new HashMap<>();

    private NpcChunkForceManager() {
    }

    /** Clear all state (call on server context change). */
    public static void resetState() {
        FORCED_TARGET_CHUNK_BY_NPC.clear();
        FORCED_ROUTE_CHUNKS_BY_NPC.clear();
    }

    /** Release ALL forced chunks (call when no player is in dimension). */
    public static void releaseAllForcedChunks(ServerLevel level) {
        if (level == null) return;
        for (Long key : FORCED_TARGET_CHUNK_BY_NPC.values()) {
            level.setChunkForced((int)(key >> 32), (int)(long)key, false);
        }
        FORCED_TARGET_CHUNK_BY_NPC.clear();
        for (Set<Long> chunks : FORCED_ROUTE_CHUNKS_BY_NPC.values()) {
            for (Long key : chunks) {
                level.setChunkForced((int)(key >> 32), (int)(long)key, false);
            }
        }
        FORCED_ROUTE_CHUNKS_BY_NPC.clear();
    }

    public static void ensureRouteTargetChunkForced(ServerLevel level, String rawNpcId, Vec3 target) {
        if (level == null || target == null || rawNpcId == null || rawNpcId.isBlank()) {
            return;
        }
        String npcId = rawNpcId.toLowerCase(Locale.ROOT);
        int chunkX = ((int) Math.floor(target.x)) >> 4;
        int chunkZ = ((int) Math.floor(target.z)) >> 4;
        long newKey = (((long) chunkX) << 32) | (((long) chunkZ) & 0xFFFFFFFFL);

        Long oldKey = FORCED_TARGET_CHUNK_BY_NPC.get(npcId);
        if (oldKey != null && oldKey == newKey) {
            return;
        }

        if (oldKey != null) {
            int oldChunkX = (int) (oldKey >> 32);
            int oldChunkZ = (int) (long) oldKey;
            level.setChunkForced(oldChunkX, oldChunkZ, false);
        }

        level.setChunkForced(chunkX, chunkZ, true);
        level.getChunk(chunkX, chunkZ);
        FORCED_TARGET_CHUNK_BY_NPC.put(npcId, newKey);
    }

    public static String currentForcedTargetChunk(String rawNpcId) {
        if (rawNpcId == null || rawNpcId.isBlank()) {
            return "<none>";
        }
        Long key = FORCED_TARGET_CHUNK_BY_NPC.get(rawNpcId.toLowerCase(Locale.ROOT));
        if (key == null) {
            return "<none>";
        }
        int chunkX = (int) (key >> 32);
        int chunkZ = (int) (long) key;
        return chunkX + "," + chunkZ;
    }

    public static void ensureRouteCorridorChunksForced(ServerLevel level, String rawNpcId, Vec3 from, Vec3 to) {
        if (level == null || rawNpcId == null || rawNpcId.isBlank() || from == null || to == null) {
            return;
        }

        String npcId = rawNpcId.toLowerCase(Locale.ROOT);
        int startX = ((int) Math.floor(from.x)) >> 4;
        int startZ = ((int) Math.floor(from.z)) >> 4;
        int endX = ((int) Math.floor(to.x)) >> 4;
        int endZ = ((int) Math.floor(to.z)) >> 4;

        if (Math.abs(endX - startX) > MAX_FORCED_CORRIDOR_CHUNK_DELTA
            || Math.abs(endZ - startZ) > MAX_FORCED_CORRIDOR_CHUNK_DELTA) {
            Set<Long> prev = FORCED_ROUTE_CHUNKS_BY_NPC.remove(npcId);
            if (prev != null) {
                for (Long oldKey : prev) {
                    int chunkX = (int) (oldKey >> 32);
                    int chunkZ = (int) (long) oldKey;
                    level.setChunkForced(chunkX, chunkZ, false);
                }
            }
            return;
        }

        Set<Long> next = chunkLine(startX, startZ, endX, endZ);
        Set<Long> prev = FORCED_ROUTE_CHUNKS_BY_NPC.getOrDefault(npcId, Set.of());

        for (Long oldKey : prev) {
            if (next.contains(oldKey)) {
                continue;
            }
            int chunkX = (int) (oldKey >> 32);
            int chunkZ = (int) (long) oldKey;
            level.setChunkForced(chunkX, chunkZ, false);
        }

        for (Long key : next) {
            int chunkX = (int) (key >> 32);
            int chunkZ = (int) (long) key;
            level.setChunkForced(chunkX, chunkZ, true);
            level.getChunk(chunkX, chunkZ);
        }

        FORCED_ROUTE_CHUNKS_BY_NPC.put(npcId, next);
    }

    public static void releaseInactiveForcedChunks(ServerLevel level, Set<String> activeNpcIds) {
        if (level == null) {
            return;
        }

        List<String> staleTargets = new java.util.ArrayList<>();
        for (String npcId : FORCED_TARGET_CHUNK_BY_NPC.keySet()) {
            if (!activeNpcIds.contains(npcId)) {
                staleTargets.add(npcId);
            }
        }
        for (String npcId : staleTargets) {
            Long key = FORCED_TARGET_CHUNK_BY_NPC.remove(npcId);
            if (key == null) {
                continue;
            }
            int chunkX = (int) (key >> 32);
            int chunkZ = (int) (long) key;
            level.setChunkForced(chunkX, chunkZ, false);
        }

        List<String> staleCorridors = new java.util.ArrayList<>();
        for (String npcId : FORCED_ROUTE_CHUNKS_BY_NPC.keySet()) {
            if (!activeNpcIds.contains(npcId)) {
                staleCorridors.add(npcId);
            }
        }
        for (String npcId : staleCorridors) {
            Set<Long> chunks = FORCED_ROUTE_CHUNKS_BY_NPC.remove(npcId);
            if (chunks == null) {
                continue;
            }
            for (Long key : chunks) {
                int chunkX = (int) (key >> 32);
                int chunkZ = (int) (long) key;
                level.setChunkForced(chunkX, chunkZ, false);
            }
        }
    }

    private static Set<Long> chunkLine(int x0, int z0, int x1, int z1) {
        Set<Long> out = new LinkedHashSet<>();

        int dx = Math.abs(x1 - x0);
        int dz = Math.abs(z1 - z0);
        int sx = x0 < x1 ? 1 : -1;
        int sz = z0 < z1 ? 1 : -1;
        int err = dx - dz;

        int x = x0;
        int z = z0;
        while (true) {
            out.add(packChunk(x, z));
            if (x == x1 && z == z1) {
                break;
            }
            int e2 = 2 * err;
            if (e2 > -dz) {
                err -= dz;
                x += sx;
            }
            if (e2 < dx) {
                err += dx;
                z += sz;
            }
        }

        return out;
    }

    private static long packChunk(int chunkX, int chunkZ) {
        return (((long) chunkX) << 32) | (((long) chunkZ) & 0xFFFFFFFFL);
    }
}
