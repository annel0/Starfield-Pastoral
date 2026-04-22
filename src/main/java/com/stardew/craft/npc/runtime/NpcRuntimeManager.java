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
        refreshFromData(snapshot);
        syncRuntimeState(level, snapshot);

        if (!snapshot.loggedOnce) {
            snapshot.loggedOnce = true;
        }
    }

    private static int lastCapabilitiesVersion = -1;

    private static void refreshFromData(RuntimeSnapshot snapshot) {
        // NPC 能力数据只在 datapack reload 时变化，用版本号避免每 tick 重建
        int currentVersion = NpcDataRegistry.capabilities().size();
        if (currentVersion == lastCapabilitiesVersion && !snapshot.implementedNpcIds.isEmpty()) {
            return;
        }
        lastCapabilitiesVersion = currentVersion;

        snapshot.implementedNpcIds.clear();
        snapshot.pathingNpcIds.clear();

        for (NpcCapabilityProfile profile : NpcDataRegistry.capabilities().values()) {
            if (!profile.implemented()) {
                continue;
            }

            snapshot.implementedNpcIds.add(profile.npcId());
            if (profile.canRunPathing()) {
                snapshot.pathingNpcIds.add(profile.npcId());
            }
        }
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
        private boolean loggedOnce;
    }
}
