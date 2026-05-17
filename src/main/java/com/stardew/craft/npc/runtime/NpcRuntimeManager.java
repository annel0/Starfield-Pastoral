package com.stardew.craft.npc.runtime;

import com.stardew.craft.core.ModDimensions;
import com.stardew.craft.npc.data.NpcCapabilityProfile;
import com.stardew.craft.npc.data.NpcDataRegistry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Phase A runtime skeleton: tracks active implemented NPC ids and pathing eligibility.
 */
@SuppressWarnings("null")
public final class NpcRuntimeManager {
    private static final Map<MinecraftServer, RuntimeSnapshot> SNAPSHOTS = new ConcurrentHashMap<>();

    private NpcRuntimeManager() {
    }

    public static void tickServer(MinecraftServer server) {
        ServerLevel level = server.getLevel(ModDimensions.STARDEW_VALLEY);
        if (level == null) {
            return;
        }

        RuntimeSnapshot snapshot = SNAPSHOTS.computeIfAbsent(server, key -> new RuntimeSnapshot());
        boolean refreshed = refreshFromData(snapshot);
        if (refreshed || !snapshot.syncedRuntimeState) {
            syncRuntimeState(level, snapshot);
            snapshot.syncedRuntimeState = true;
        }

        if (!snapshot.loggedOnce) {
            snapshot.loggedOnce = true;
        }
    }

    private static Map<String, NpcCapabilityProfile> lastCapabilitiesSnapshot = Map.of();

    private static boolean refreshFromData(RuntimeSnapshot snapshot) {
        Map<String, NpcCapabilityProfile> capabilities = NpcDataRegistry.capabilities();
        if (capabilities == lastCapabilitiesSnapshot && !snapshot.implementedNpcIds.isEmpty()) {
            return false;
        }
        lastCapabilitiesSnapshot = capabilities;

        snapshot.implementedNpcIds.clear();
        snapshot.pathingNpcIds.clear();

        for (NpcCapabilityProfile profile : capabilities.values()) {
            if (!profile.implemented()) {
                continue;
            }

            snapshot.implementedNpcIds.add(profile.npcId());
            if (profile.canRunPathing()) {
                snapshot.pathingNpcIds.add(profile.npcId());
            }
        }
        return true;
    }

    private static void syncRuntimeState(ServerLevel level, RuntimeSnapshot snapshot) {
        NpcRuntimeDataManager data = NpcRuntimeDataManager.get(level);
        boolean changed = false;

        for (String npcId : snapshot.implementedNpcIds) {
            NpcRuntimeState state = data.getOrCreate(npcId);
            boolean shouldSuppressPathing = !snapshot.pathingNpcIds.contains(npcId);
            if (state.pathingSuppressed() != shouldSuppressPathing) {
                state.setPathingSuppressed(shouldSuppressPathing);
                changed = true;
            }
        }

        if (changed) {
            data.setDirty();
        }
    }

    private static final class RuntimeSnapshot {
        private final Set<String> implementedNpcIds = ConcurrentHashMap.newKeySet();
        private final Set<String> pathingNpcIds = ConcurrentHashMap.newKeySet();
        private boolean syncedRuntimeState;
        private boolean loggedOnce;
    }
}
