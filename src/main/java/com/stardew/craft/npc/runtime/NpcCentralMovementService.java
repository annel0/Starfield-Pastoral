package com.stardew.craft.npc.runtime;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.stardew.craft.entity.npc.StardewNpcEntity;
import com.stardew.craft.interior.InteriorSubspaceManager;
import com.stardew.craft.npc.data.NpcCapabilityProfile;
import com.stardew.craft.npc.data.NpcDataRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.UUID;

/**
 * NPC movement controller driven by schedule targets and data-defined route profiles.
 */
@SuppressWarnings("null")
public final class NpcCentralMovementService {
    private static final double WALK_SPEED = 0.14D;
    private static final double WAYPOINT_REACH_SQR = 0.45D * 0.45D;
    private static final double DONE_RESYNC_DIST_SQR = 9.0D * 9.0D;
    private static final int REPATH_COOLDOWN_TICKS = 10;
    private static final int NO_PATH_REPATH_COOLDOWN_TICKS = 40;
    private static final int STUCK_REPATH_TICKS = 40;
    private static final int STUCK_FORCE_SYNC_TICKS = 120;
    private static final double PROGRESS_MOVE_DIST_SQR = 0.16D;
    private static final double PROGRESS_TARGET_GAIN_EPSILON_SQR = 0.04D;
    private static final int NO_PATH_FORCE_SYNC_TICKS = 20;
    private static final double NO_PATH_FORCE_SYNC_DIST_SQR = 8.0D * 8.0D;
    private static final int NO_PATH_FORCE_SYNC_MAX_TICKS = 240;
    private static final double NO_PATH_FORCE_SYNC_MAX_DIST_SQR = 18.0D * 18.0D;
    private static final int NO_PATH_FORCE_SYNC_FAR_TICKS = 140;
    private static final int TIME_JUMP_THRESHOLD = 200;
    private static final int ASTAR_MIN_VISITS = 4_000;
    private static final int ASTAR_MAX_VISITS = 24_000;
    private static final int ASTAR_WARN_INTERVAL_TICKS = 200;
    private static final int MAX_FORCED_CORRIDOR_CHUNK_DELTA = 24;

    private static final Vec3 MANORHOUSE_OUTER = new Vec3(-196.0D, -17.0D, -22.0D);
    private static final Vec3 COMMUNITYCENTER_OUTER = new Vec3(-190.0D, -10.0D, 138.0D);
    private static final Vec3 SALOON_OUTER = new Vec3(-163.0D, -17.0D, 14.0D);
    private static final Vec3 SEEDSHOP_OUTER = new Vec3(-159.0D, -18.0D, 54.0D);
    private static final Vec3 SEEDSHOP_INNER_ENTRY = new Vec3(12038.0D, 71.0D, 12038.0D);
    private static final Vec3 FISHSHOP_OUTER = new Vec3(-237.0D, -15.0D, -212.0D);

    private static final Map<String, LewisRoutePlan> ACTIVE_PLANS = new HashMap<>();
    private static final Map<String, Integer> LAST_CHECKPOINT = new HashMap<>();
    private static final Set<String> UNKNOWN_LOCATION_LOGGED = new HashSet<>();
    private static final Set<String> MISSING_CONFIG_POINT_LOGGED = new HashSet<>();
    private static final Set<String> HARD_ENDPOINT_WARNED = new HashSet<>();
    private static final Map<String, String> LAST_NODE_SIGNATURE = new HashMap<>();
    private static final Map<String, DebugSnapshot> DEBUG_SNAPSHOTS = new HashMap<>();
    private static final Map<String, Long> FORCED_TARGET_CHUNK_BY_NPC = new HashMap<>();
    private static final Map<String, Set<Long>> FORCED_ROUTE_CHUNKS_BY_NPC = new HashMap<>();
    private static MinecraftServer activeServer;
    private static long lastAstarWarnTick = Long.MIN_VALUE;

    private NpcCentralMovementService() {
    }

    public static DebugSnapshot getDebugSnapshot(String npcId) {
        return DEBUG_SNAPSHOTS.get(npcId == null ? "" : npcId.toLowerCase());
    }

    public static void tick(ServerLevel level) {
        ensureServerContext(level);
        Map<String, NpcRuntimeState> runtimeStates = NpcRuntimeDataManager.get(level).states();
        Set<String> activeNpcIds = new HashSet<>();

        for (Map.Entry<String, NpcCapabilityProfile> capabilityEntry : NpcDataRegistry.capabilities().entrySet()) {
            NpcCapabilityProfile profile = capabilityEntry.getValue();
            if (profile == null || !profile.implemented()) {
                continue;
            }

            String npcId = canonicalNpcId(profile.npcId());
            activeNpcIds.add(npcId);
            StardewNpcEntity npc = NpcSpawnManager.getTrackedNpc(level, npcId);
            if (npc == null) {
                continue;
            }

            npc.getNavigation().stop();

            NpcRuntimeState state = runtimeStates.get(npcId);
            boolean pathingSuppressed = !profile.canRunPathing() || (state != null && state.pathingSuppressed());
            if (pathingSuppressed) {
                npc.setDeltaMovement(Vec3.ZERO);
                applyFacing(npc, state);
                DEBUG_SNAPSHOTS.put(npcId, new DebugSnapshot("pathing_disabled", "<none>", "<none>", 0, 0, false, npc.position(), npc.position(), "none", 0, "<none>"));
                continue;
            }

            LewisRouteContext route = resolveRoute(level, npcId, state);
            if (route == null) {
                npc.setDeltaMovement(Vec3.ZERO);
                applyFacing(npc, state);
                DEBUG_SNAPSHOTS.put(npcId, new DebugSnapshot("no_route", "<none>", "<none>", 0, 0, false, npc.position(), npc.position(), "none", 0, "<none>"));
                continue;
            }
            boolean nodeChanged = markAndCheckScheduleNodeChange(npcId, state);
            boolean timeJump = isTimeJump(npcId, state);
            String signature = buildPlanSignature(state, route);

            LewisRoutePlan plan = ACTIVE_PLANS.get(npcId);
            boolean entityReplaced = plan != null && !npc.getUUID().equals(plan.boundEntityUuid);
            boolean needNewPlan = plan == null || !signature.equals(plan.signature) || entityReplaced;
            if (needNewPlan || nodeChanged) {
                plan = buildPlan(level, npc, route, signature, level.getGameTime());
                ACTIVE_PLANS.put(npcId, plan);
            }

            if (plan == null || plan.steps.isEmpty()) {
                npc.setDeltaMovement(Vec3.ZERO);
                applyFacing(npc, state);
                DEBUG_SNAPSHOTS.put(npcId, new DebugSnapshot("empty_plan", route.canonicalLocation, "<none>", 0, 0, false, npc.position(), npc.position(), "none", 0, "<none>"));
                continue;
            }

            executePlanTick(level, npc, plan, timeJump);
            DEBUG_SNAPSHOTS.put(npcId, new DebugSnapshot(
                plan.debugStage,
                route.canonicalLocation,
                plan.debugPointId,
                plan.path.size(),
                plan.pathIndex,
                plan.lastFallbackTeleportUsed,
                plan.debugTarget,
                plan.debugNextWaypoint,
                plan.debugRepathReason,
                level.getGameTime() - plan.lastProgressTick,
                currentForcedTargetChunk(npcId)
            ));
        }

        releaseInactiveForcedChunks(level, activeNpcIds);

        // Avoid mass forcing interior chunks per tick; indoor transitions are handled by
        // explicit route steps and bounded target chunk forcing.
        InteriorSubspaceManager.setInteriorChunksForced(level, false, "npc_runtime");
    }

    private static void ensureServerContext(ServerLevel level) {
        if (level == null || level.getServer() == null) {
            return;
        }
        if (activeServer == level.getServer()) {
            return;
        }

        activeServer = level.getServer();
        ACTIVE_PLANS.clear();
        LAST_CHECKPOINT.clear();
        UNKNOWN_LOCATION_LOGGED.clear();
        MISSING_CONFIG_POINT_LOGGED.clear();
        HARD_ENDPOINT_WARNED.clear();
        LAST_NODE_SIGNATURE.clear();
        DEBUG_SNAPSHOTS.clear();
        FORCED_TARGET_CHUNK_BY_NPC.clear();
        FORCED_ROUTE_CHUNKS_BY_NPC.clear();
        lastAstarWarnTick = Long.MIN_VALUE;
    }

    private static void releaseInactiveForcedChunks(ServerLevel level, Set<String> activeNpcIds) {
        if (level == null) {
            return;
        }

        List<String> staleTargets = new ArrayList<>();
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

        List<String> staleCorridors = new ArrayList<>();
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

    private static LewisRouteContext resolveRoute(ServerLevel level,
                                                  String npcId,
                                                  NpcRuntimeState state) {
        String canonicalNpcId = canonicalNpcId(npcId);
        LewisRouteContext profileRoute = resolveProfileRoute(canonicalNpcId, state);
        if (profileRoute != null) {
            return profileRoute;
        }

        return resolveGenericScheduleRoute(level, canonicalNpcId, state);
    }

    private static LewisRouteContext resolveProfileRoute(String canonicalNpcId, NpcRuntimeState state) {
        if (canonicalNpcId == null || canonicalNpcId.isBlank() || state == null) {
            return null;
        }

        JsonObject root = NpcDataRegistry.events().get("npc_route_profiles");
        if (root == null || !root.has("profiles") || !root.get("profiles").isJsonObject()) {
            return null;
        }

        JsonObject profiles = root.getAsJsonObject("profiles");
        JsonObject npcProfile = getObjectCaseInsensitive(profiles, canonicalNpcId);
        if (npcProfile == null) {
            return null;
        }

        String location = state.locationName() == null ? "" : state.locationName().trim().toLowerCase(Locale.ROOT);
        String canonicalLocation = NpcDataRegistry.locationAliases().getOrDefault(location, location);
        JsonElement routeEl = npcProfile.get(canonicalLocation);
        if (routeEl == null && "town".equals(canonicalLocation)) {
            routeEl = npcProfile.get("towngarden");
        }
        if (routeEl == null || !routeEl.isJsonArray()) {
            return null;
        }

        List<LewisRouteStep> destination = new ArrayList<>();
        for (JsonElement stepEl : routeEl.getAsJsonArray()) {
            if (!stepEl.isJsonObject()) {
                continue;
            }
            JsonObject stepObj = stepEl.getAsJsonObject();
            if (!stepObj.has("point") || !stepObj.get("point").isJsonPrimitive()) {
                continue;
            }
            String pointId = stepObj.get("point").getAsString().trim();
            if (pointId.isBlank()) {
                continue;
            }

            String mode = "walk";
            if (stepObj.has("mode") && stepObj.get("mode").isJsonPrimitive()) {
                mode = stepObj.get("mode").getAsString().trim().toLowerCase(Locale.ROOT);
            }

            Vec3 point = pointFromConfigStrict(pointId, state, canonicalLocation);
            if (point == null) {
                return null;
            }

            if ("warp".equals(mode)) {
                destination.add(LewisRouteStep.warp(pointId, point));
            } else {
                destination.add(LewisRouteStep.walk(pointId, point));
            }
        }

        if (destination.isEmpty()) {
            return null;
        }

        return new LewisRouteContext(canonicalLocation, destination);
    }

    private static JsonObject getObjectCaseInsensitive(JsonObject root, String key) {
        if (root == null || key == null || key.isBlank()) {
            return null;
        }
        for (Map.Entry<String, JsonElement> entry : root.entrySet()) {
            if (!entry.getKey().equalsIgnoreCase(key)) {
                continue;
            }
            if (!entry.getValue().isJsonObject()) {
                return null;
            }
            return entry.getValue().getAsJsonObject();
        }
        return null;
    }

    private static LewisRouteContext resolveGenericScheduleRoute(ServerLevel level,
                                                                 String canonicalNpcId,
                                                                 NpcRuntimeState state) {
        if (state == null) {
            return null;
        }

        String location = state.locationName() == null ? "" : state.locationName().trim().toLowerCase(Locale.ROOT);
        String canonicalLocation = NpcDataRegistry.locationAliases().getOrDefault(location, location);
        NpcScheduleRuntimeService.TargetPoint target = NpcScheduleRuntimeService.resolveWorldTarget(level, state, null);
        if (target == null || target.position() == null) {
            String missingSig = "target_missing|" + canonicalNpcId + "|" + canonicalLocation;
            if (UNKNOWN_LOCATION_LOGGED.add(missingSig)) {
                com.stardew.craft.StardewCraft.LOGGER.warn(
                    "NPC route unresolved for npc='{}' location='{}'; movement paused.",
                    canonicalNpcId,
                    canonicalLocation
                );
            }
            return null;
        }

        List<LewisRouteStep> destination = new ArrayList<>();
        if (!target.indoorTarget()) {
            destination.add(LewisRouteStep.walk("schedule_anchor", target.position()));
            return new LewisRouteContext(canonicalLocation, destination);
        }

        Vec3 outdoorDoor = resolveOutdoorDoorForLocation(canonicalLocation, target.position());
        destination.add(LewisRouteStep.walk("outdoor_door", outdoorDoor));

        if ("seedshop".equals(canonicalLocation)
            || "pierreshop".equals(canonicalLocation)
            || "shop".equals(canonicalLocation)
            || "sunroom".equals(canonicalLocation)
            || "sebastianroom".equals(canonicalLocation)) {
            destination.add(LewisRouteStep.warp("indoor_entry", pointFromConfig("seedshop_inner_entry", SEEDSHOP_INNER_ENTRY)));
            destination.add(LewisRouteStep.walk("indoor_target", target.position()));
        } else {
            // Generic indoor fallback: complete outdoor approach first, then warp into indoor anchor.
            destination.add(LewisRouteStep.warp("indoor_target", target.position()));
        }
        return new LewisRouteContext(canonicalLocation, destination);
    }

    private static Vec3 resolveOutdoorDoorForLocation(String canonicalLocation, Vec3 fallback) {
        return switch (canonicalLocation) {
            case "seedshop", "pierreshop", "shop", "sunroom", "sebastianroom" -> pointFromConfig("seedshop_outer", SEEDSHOP_OUTER);
            case "saloon" -> pointFromConfig("saloon_outer", SALOON_OUTER);
            case "fishshop", "willyshop" -> pointFromConfig("fishshop_outer", FISHSHOP_OUTER);
            case "manorhouse" -> pointFromConfig("manorhouse_outer", MANORHOUSE_OUTER);
            case "communitycenter" -> pointFromConfig("communitycenter_outer", COMMUNITYCENTER_OUTER);
            default -> fallback;
        };
    }

    private static String canonicalNpcId(String npcId) {
        if (npcId == null || npcId.isBlank()) {
            return "";
        }
        return npcId.trim().toLowerCase(Locale.ROOT);
    }

    private static LewisRoutePlan buildPlan(ServerLevel level,
                                            StardewNpcEntity npc,
                                            LewisRouteContext route,
                                            String signature,
                                            long gameTime) {
        List<LewisRouteStep> expanded = new ArrayList<>();

        if (InteriorSubspaceManager.isInteriorRegion(level, npc.blockPosition())) {
            Vec3 exitIndoor = nearestKnownIndoorEntry(npc.position());
            Vec3 exitOutdoor = linkedOutdoorDoor(exitIndoor);
            if (exitIndoor != null && exitOutdoor != null) {
                expanded.add(LewisRouteStep.walk("indoor_exit", exitIndoor));
                expanded.add(LewisRouteStep.warp("outdoor_door", exitOutdoor));
            }
        }

        expanded.addAll(route.destinationSteps);
        LewisRoutePlan plan = new LewisRoutePlan(signature, npc.getUUID(), expanded, gameTime);
        plan.lastPos = npc.position();
        return plan;
    }

    private static boolean executePlanTick(ServerLevel level,
                                           StardewNpcEntity npc,
                                           LewisRoutePlan plan,
                                           boolean timeJump) {
        if (plan.currentStepIndex >= plan.steps.size()) {
            Vec3 finalTarget = plan.steps.isEmpty() ? null : plan.steps.get(plan.steps.size() - 1).target;
            if (finalTarget != null) {
                Vec3 delta = finalTarget.subtract(npc.position());
                double d2dSqr = delta.x * delta.x + delta.z * delta.z;
                if (d2dSqr > DONE_RESYNC_DIST_SQR) {
                    npc.setPos(finalTarget.x, finalTarget.y, finalTarget.z);
                    plan.lastPos = npc.position();
                    plan.lastProgressTick = level.getGameTime();
                    plan.debugStage = "done_resync";
                    return false;
                }
            }
            npc.setDeltaMovement(Vec3.ZERO);
            plan.debugStage = "done";
            return false;
        }

        plan.lastFallbackTeleportUsed = false;
        plan.debugRepathReason = "none";
        LewisRouteStep step = plan.steps.get(plan.currentStepIndex);
        plan.debugPointId = step.pointId;
        plan.debugTarget = step.target;
        plan.debugNextWaypoint = step.target;

        if (step.mode == RouteStepMode.WARP) {
            ensureRouteTargetChunkForced(level, npc.getNpcId(), step.target);
            plan.debugStage = "warp";
            npc.setDeltaMovement(Vec3.ZERO);
            npc.setPos(step.target.x, step.target.y, step.target.z);
            plan.currentStepIndex++;
            plan.path.clear();
            plan.pathIndex = 0;
            plan.lastProgressTick = level.getGameTime();
            plan.lastRepathTick = level.getGameTime();
            plan.lastPos = npc.position();
            return false;
        }

        ensureRouteTargetChunkForced(level, npc.getNpcId(), step.target);
        ensureRouteCorridorChunksForced(level, npc.getNpcId(), npc.position(), step.target);
        long now = level.getGameTime();

        if ((plan.path.isEmpty() || plan.pathIndex >= plan.path.size())
            && now - plan.lastRepathTick >= REPATH_COOLDOWN_TICKS
            && now >= plan.noPathRetryBlockedUntilTick) {
            plan.path = planPath(level, npc.position(), step.target);
            plan.pathIndex = 0;
            plan.lastRepathTick = now;
            plan.debugRepathReason = "path_init_or_exhausted";
            if (plan.path.isEmpty()) {
                plan.debugStage = "no_path_repath";
                plan.noPathRetryBlockedUntilTick = now + NO_PATH_REPATH_COOLDOWN_TICKS;
            } else {
                plan.noPathRetryBlockedUntilTick = now;
            }
        }

        if (plan.path.isEmpty()) {
            plan.debugStage = "no_path";
            npc.setDeltaMovement(Vec3.ZERO);
            if (now - plan.lastProgressTick > STUCK_REPATH_TICKS
                && now >= plan.noPathRetryBlockedUntilTick) {
                plan.path = planPath(level, npc.position(), step.target);
                plan.pathIndex = 0;
                plan.lastRepathTick = now;
                if (plan.path.isEmpty()) {
                    plan.noPathRetryBlockedUntilTick = now + NO_PATH_REPATH_COOLDOWN_TICKS;
                    plan.debugRepathReason = "no_path_stuck_repath_cooldown";
                } else {
                    plan.noPathRetryBlockedUntilTick = now;
                    plan.debugRepathReason = "no_path_stuck_repath";
                }
            } else if (now < plan.noPathRetryBlockedUntilTick) {
                plan.debugRepathReason = "no_path_cooldown";
            }

            long noPathDuration = level.getGameTime() - plan.lastProgressTick;
            double toTarget = npc.position().distanceToSqr(step.target);
            boolean nearTarget = toTarget <= NO_PATH_FORCE_SYNC_MAX_DIST_SQR;
            boolean shortWindowFallback = noPathDuration > NO_PATH_FORCE_SYNC_TICKS && toTarget > NO_PATH_FORCE_SYNC_DIST_SQR;
            boolean maxWindowFallback = noPathDuration > NO_PATH_FORCE_SYNC_MAX_TICKS;
            boolean farWindowFallback = noPathDuration > NO_PATH_FORCE_SYNC_FAR_TICKS;
            if ((nearTarget && (shortWindowFallback || maxWindowFallback)) || (!nearTarget && farWindowFallback)) {
                plan.debugStage = "no_path_fallback_tp";
                plan.lastFallbackTeleportUsed = true;
                npc.setPos(step.target.x, step.target.y, step.target.z);
                plan.currentStepIndex++;
                plan.path.clear();
                plan.pathIndex = 0;
                plan.lastProgressTick = level.getGameTime();
                plan.lastPos = npc.position();
                plan.debugRepathReason = nearTarget ? "no_path_near_fallback" : "no_path_far_timeout_fallback";
            }
            return false;
        }

        Vec3 waypoint = plan.path.get(Math.min(plan.pathIndex, plan.path.size() - 1));
        plan.debugNextWaypoint = waypoint;
        tryOpenNearbyDoors(level, npc, waypoint, step.target);
        Vec3 horizontal = new Vec3(waypoint.x - npc.getX(), 0.0D, waypoint.z - npc.getZ());

        if (horizontal.lengthSqr() <= WAYPOINT_REACH_SQR) {
            plan.pathIndex++;
            plan.lastProgressTick = level.getGameTime();
            plan.lastPos = npc.position();
            if (plan.pathIndex >= plan.path.size()) {
                npc.setDeltaMovement(Vec3.ZERO);
                npc.setPos(step.target.x, step.target.y, step.target.z);
                plan.currentStepIndex++;
                plan.path.clear();
                plan.pathIndex = 0;
                plan.lastProgressTick = level.getGameTime();
                plan.lastPos = npc.position();
                return false;
            }
            waypoint = plan.path.get(plan.pathIndex);
            horizontal = new Vec3(waypoint.x - npc.getX(), 0.0D, waypoint.z - npc.getZ());
        }

        double length = Math.sqrt(horizontal.lengthSqr());
        if (length > 0.0001D) {
            plan.debugStage = "walk";
            double vx = (horizontal.x / length) * WALK_SPEED;
            double vz = (horizontal.z / length) * WALK_SPEED;
            double vy = npc.getDeltaMovement().y;

            // Path nodes use block-center Y, so flat-ground rise is often around 0.5.
            // Only jump when we actually need to climb a higher step.
            double verticalRise = waypoint.y - npc.getY();
            boolean stepUpNeeded = verticalRise > 1.05D;
            boolean needsToJump = stepUpNeeded || (npc.horizontalCollision && verticalRise > 0.55D);

            if (npc.horizontalCollision && !needsToJump
                && level.getGameTime() - plan.lastRepathTick > REPATH_COOLDOWN_TICKS) {
                plan.path = planPath(level, npc.position(), step.target);
                plan.pathIndex = 0;
                plan.lastRepathTick = level.getGameTime();
                plan.debugStage = "collision_repath";
                plan.debugRepathReason = isBarrierAhead(level, npc.position(), vx, vz)
                    ? "barrier_repath"
                    : "collision_repath";
            }

            if (needsToJump && npc.onGround()) {
                vy = 0.42D; // Standard Minecraft jump velocity
                plan.debugStage = "walk_jump";
            }

            npc.setDeltaMovement(vx, vy, vz);
            float moveYaw = (float) (Math.toDegrees(Math.atan2(-vx, vz)));
            npc.setYRot(moveYaw);
            npc.setYHeadRot(moveYaw);
            npc.hurtMarked = true;
        }

        double moved = npc.position().distanceToSqr(plan.lastPos);
        double checkpointToTarget = plan.lastPos.distanceToSqr(step.target);
        double currentToTarget = npc.position().distanceToSqr(step.target);
        boolean targetProgressed = currentToTarget + PROGRESS_TARGET_GAIN_EPSILON_SQR < checkpointToTarget;
        if (moved > PROGRESS_MOVE_DIST_SQR && targetProgressed) {
            plan.lastPos = npc.position();
            plan.lastProgressTick = level.getGameTime();
        }

        if (level.getGameTime() - plan.lastProgressTick > STUCK_REPATH_TICKS
            && level.getGameTime() - plan.lastRepathTick > REPATH_COOLDOWN_TICKS) {
            plan.path = planPath(level, npc.position(), step.target);
            plan.pathIndex = 0;
            plan.lastRepathTick = level.getGameTime();
            plan.debugStage = "stuck_repath";
            plan.debugRepathReason = "stuck_timeout_repath";
        }

        if (level.getGameTime() - plan.lastProgressTick > STUCK_FORCE_SYNC_TICKS) {
            // Force teleport after being stuck too long, regardless of distance.
            plan.debugStage = "fallback_tp";
            plan.lastFallbackTeleportUsed = true;
            npc.setDeltaMovement(Vec3.ZERO);
            npc.setPos(step.target.x, step.target.y, step.target.z);
            plan.currentStepIndex++;
            plan.path.clear();
            plan.pathIndex = 0;
            plan.lastProgressTick = level.getGameTime();
            plan.lastPos = npc.position();
            plan.debugRepathReason = "stuck_force_teleport";
        }

        return false;
    }

    private static boolean isTimeJump(String npcId, NpcRuntimeState state) {
        if (npcId == null || state == null) {
            return false;
        }
        int current = state.scheduleCheckpoint();
        Integer previous = LAST_CHECKPOINT.put(npcId, current);
        if (previous == null) {
            return false;
        }
        return current - previous >= TIME_JUMP_THRESHOLD;
    }

    private static String buildPlanSignature(NpcRuntimeState state, LewisRouteContext route) {
        return state.activeScheduleKey() + "#" + state.scheduleCheckpoint() + "#" + state.scheduleNodeIndex()
            + "#" + route.canonicalLocation;
    }

    private static Vec3 nearestKnownIndoorEntry(Vec3 pos) {
        Vec3[] entries = {pointFromConfig("seedshop_inner_entry", SEEDSHOP_INNER_ENTRY)};
        Vec3 nearest = null;
        double best = Double.MAX_VALUE;
        for (Vec3 entry : entries) {
            double d = pos.distanceToSqr(entry);
            if (d < best) {
                best = d;
                nearest = entry;
            }
        }
        return nearest;
    }

    private static Vec3 linkedOutdoorDoor(Vec3 indoorEntry) {
        if (indoorEntry == null) {
            return null;
        }
        Vec3 configuredSeedshopEntry = pointFromConfig("seedshop_inner_entry", SEEDSHOP_INNER_ENTRY);
        if (indoorEntry.distanceToSqr(configuredSeedshopEntry) < 4.0D) {
            return pointFromConfig("seedshop_outer", SEEDSHOP_OUTER);
        }
        return null;
    }

    private static Vec3 pointFromConfig(String pointId, Vec3 fallback) {
        JsonObject root = routePointsRoot();
        if (root == null || !root.has("points") || !root.get("points").isJsonObject()) {
            return fallback;
        }

        JsonObject points = root.getAsJsonObject("points");
        JsonElement element = points.get(pointId);
        if (element == null || !element.isJsonObject()) {
            if (MISSING_CONFIG_POINT_LOGGED.add(pointId)) {
                com.stardew.craft.StardewCraft.LOGGER.warn("Missing npc route point '{}' in npc_route_points event; using fallback.", pointId);
            }
            return fallback;
        }

        JsonObject obj = element.getAsJsonObject();
        double x = obj.has("x") ? obj.get("x").getAsDouble() : fallback.x;
        double y = obj.has("y") ? obj.get("y").getAsDouble() : fallback.y;
        double z = obj.has("z") ? obj.get("z").getAsDouble() : fallback.z;
        return new Vec3(x, y, z);
    }

    private static Vec3 pointFromConfigStrict(String pointId, NpcRuntimeState state, String canonicalLocation) {
        JsonObject root = routePointsRoot();
        if (root == null || !root.has("points") || !root.get("points").isJsonObject()) {
            emitHardEndpointWarning("missing_points_root", pointId, canonicalLocation, state);
            return null;
        }

        JsonObject points = root.getAsJsonObject("points");
        JsonElement element = points.get(pointId);
        if (element == null || !element.isJsonObject()) {
            emitHardEndpointWarning("missing_point_id", pointId, canonicalLocation, state);
            return null;
        }

        JsonObject obj = element.getAsJsonObject();
        if (!obj.has("x") || !obj.has("y") || !obj.has("z")) {
            emitHardEndpointWarning("point_missing_xyz", pointId, canonicalLocation, state);
            return null;
        }

        return new Vec3(obj.get("x").getAsDouble(), obj.get("y").getAsDouble(), obj.get("z").getAsDouble());
    }

    private static JsonObject routePointsRoot() {
        return NpcDataRegistry.events().get("npc_route_points");
    }

    private static void emitHardEndpointWarning(String reason, String pointId, String canonicalLocation, NpcRuntimeState state) {
        String sig = String.format(
            Locale.ROOT,
            "%s|%s|%s|%s|%d|%d",
            canonicalLocation,
            pointId,
            reason,
            safe(state == null ? "" : state.activeScheduleKey()),
            state == null ? -1 : state.scheduleCheckpoint(),
            state == null ? -1 : state.scheduleNodeIndex()
        );
        if (!HARD_ENDPOINT_WARNED.add(sig)) {
            return;
        }

        com.stardew.craft.StardewCraft.LOGGER.warn(
            "[LEWIS_ENDPOINT_MISSING][HARD] reason={} location={} point={} scheduleKey={} checkpoint={} node={} action=movement_paused",
            reason,
            canonicalLocation,
            pointId,
            safe(state == null ? "" : state.activeScheduleKey()),
            state == null ? -1 : state.scheduleCheckpoint(),
            state == null ? -1 : state.scheduleNodeIndex()
        );
    }

    private static String safe(String value) {
        return value == null || value.isBlank() ? "<none>" : value;
    }

    private static List<Vec3> planPath(ServerLevel level, Vec3 rawStart, Vec3 rawGoal) {
        BlockPos start = nearestWalkable(level, rawStart);
        BlockPos goal = nearestWalkable(level, rawGoal);
        if (start == null || goal == null) {
            return new ArrayList<>();
        }

        int maxVisits = computeAstarVisitBudget(start, goal);

        PriorityQueue<Node> open = new PriorityQueue<>(Comparator.comparingDouble(n -> n.f));
        Map<Long, Node> bestByKey = new HashMap<>();
        Set<Long> closed = new HashSet<>();

        Node startNode = new Node(start, null, 0.0D, heuristic(start, goal));
        open.add(startNode);
        bestByKey.put(pack(start), startNode);

        int visits = 0;
        Node endNode = null;
        while (!open.isEmpty() && visits++ < maxVisits) {
            Node current = open.poll();
            long currentKey = pack(current.pos);
            if (!closed.add(currentKey)) {
                continue;
            }

            if (current.pos.distSqr(goal) <= 1.0D) {
                endNode = current;
                break;
            }

            for (int[] dir : DIR8) {
                BlockPos nextPos = stepTo(level, current.pos, dir[0], dir[1]);
                if (nextPos == null) {
                    continue;
                }

                long key = pack(nextPos);
                if (closed.contains(key)) {
                    continue;
                }

                double g = current.g + transitionCost(level, current.pos, nextPos, dir[0], dir[1]);
                Node best = bestByKey.get(key);
                if (best != null && g >= best.g) {
                    continue;
                }

                Node next = new Node(nextPos, current, g, g + heuristic(nextPos, goal));
                bestByKey.put(key, next);
                open.add(next);
            }
        }

        if (endNode == null) {
            maybeWarnAstarBudget(level, start, goal, visits, maxVisits);
            return new ArrayList<>();
        }

        List<Vec3> path = new ArrayList<>();
        Node cursor = endNode;
        while (cursor != null) {
            path.add(0, Vec3.atCenterOf(cursor.pos));
            cursor = cursor.parent;
        }

        // Do not force-append raw goal if it is not the solved walkable endpoint.
        // Appending an unreachable raw goal can make NPC push endlessly into walls.
        return smoothPath(path);
    }

    private static int computeAstarVisitBudget(BlockPos start, BlockPos goal) {
        int dx = Math.abs(start.getX() - goal.getX());
        int dz = Math.abs(start.getZ() - goal.getZ());
        int dist = Math.max(dx, dz);
        int budget = ASTAR_MIN_VISITS + (dist * 160);
        if (budget < ASTAR_MIN_VISITS) {
            return ASTAR_MIN_VISITS;
        }
        return Math.min(ASTAR_MAX_VISITS, budget);
    }

    private static void maybeWarnAstarBudget(ServerLevel level,
                                             BlockPos start,
                                             BlockPos goal,
                                             int visits,
                                             int maxVisits) {
        if (level == null) {
            return;
        }
        long now = level.getGameTime();
        if (lastAstarWarnTick != Long.MIN_VALUE && now - lastAstarWarnTick < ASTAR_WARN_INTERVAL_TICKS) {
            return;
        }
        lastAstarWarnTick = now;
        com.stardew.craft.StardewCraft.LOGGER.warn(
            "[NPC_ASTAR_BUDGET] no_path start=({}, {}, {}) goal=({}, {}, {}) visits={} budget={}",
            start.getX(), start.getY(), start.getZ(),
            goal.getX(), goal.getY(), goal.getZ(),
            visits,
            maxVisits
        );
    }

    private static List<Vec3> smoothPath(List<Vec3> path) {
        if (path.size() <= 2) {
            return path;
        }

        List<Vec3> out = new ArrayList<>();
        out.add(path.get(0));
        for (int i = 1; i < path.size() - 1; i++) {
            Vec3 prev = path.get(i - 1);
            Vec3 cur = path.get(i);
            Vec3 next = path.get(i + 1);
            Vec3 a = cur.subtract(prev);
            Vec3 b = next.subtract(cur);
            if (a.normalize().dot(b.normalize()) > 0.995D) {
                continue;
            }
            out.add(cur);
        }
        out.add(path.get(path.size() - 1));
        return out;
    }

    private static BlockPos nearestWalkable(ServerLevel level, Vec3 raw) {
        int x = (int) Math.floor(raw.x);
        int z = (int) Math.floor(raw.z);
        int baseY = (int) Math.floor(raw.y);

        BlockPos direct = resolveColumnStandNearY(level, x, z, baseY);
        if (direct != null) {
            return direct;
        }

        for (int r = 1; r <= 10; r++) {
            for (int dx = -r; dx <= r; dx++) {
                for (int dz = -r; dz <= r; dz++) {
                    int nx = x + dx;
                    int nz = z + dz;
                    BlockPos candidate = resolveColumnStandNearY(level, nx, nz, baseY);
                    if (candidate != null) {
                        return candidate;
                    }
                }
            }
        }
        return null;
    }

    private static BlockPos stepTo(ServerLevel level, BlockPos from, int dx, int dz) {
        int nx = from.getX() + dx;
        int nz = from.getZ() + dz;

        // Prevent diagonal corner cutting through blocked walls/fences.
        if (dx != 0 && dz != 0) {
            BlockPos sideA = resolveColumnStandNearY(level, from.getX() + dx, from.getZ(), from.getY());
            BlockPos sideB = resolveColumnStandNearY(level, from.getX(), from.getZ() + dz, from.getY());
            if (sideA == null || sideB == null || !canStand(level, sideA) || !canStand(level, sideB)) {
                return null;
            }
        }

        BlockPos next = resolveColumnStandNearY(level, nx, nz, from.getY());
        if (next == null) {
            return null;
        }
        if (!canStand(level, next)) {
            return null;
        }
        if (Math.abs(next.getY() - from.getY()) > 2) {
            return null;
        }
        return next;
    }

    private static BlockPos resolveColumnStandNearY(ServerLevel level, int x, int z, int preferredY) {
        for (int y = preferredY + 2; y >= preferredY - 4; y--) {
            BlockPos layered = new BlockPos(x, y, z);
            if (canStand(level, layered)) {
                return layered;
            }
        }

        int surfaceY = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
        BlockPos surface = new BlockPos(x, surfaceY, z);
        if (canStand(level, surface)) {
            return surface;
        }
        return null;
    }

    private static boolean canStand(ServerLevel level, BlockPos pos) {
        BlockState feet = level.getBlockState(pos);
        BlockState head = level.getBlockState(pos.above());
        var below = level.getBlockState(pos.below());
        if (feet.blocksMotion() && !isPathDoorPassable(feet)) {
            return false;
        }
        if (head.blocksMotion() && !isPathDoorPassable(head)) {
            return false;
        }
        if (isBarrierSupport(level, pos.below())) {
            return false;
        }
        if (!below.blocksMotion()) {
            return false;
        }

        // Validate with entity-size clearance so routes that look passable per-block
        // but are blocked for a 0.6x1.8 NPC are rejected by A*.
        double cx = pos.getX() + 0.5D;
        double cz = pos.getZ() + 0.5D;
        double y = pos.getY();
        AABB npcBox = new AABB(cx - 0.27D, y, cz - 0.27D, cx + 0.27D, y + 1.80D, cz + 0.27D);
        return level.noCollision(npcBox);
    }

    private static double transitionCost(ServerLevel level, BlockPos from, BlockPos to, int dx, int dz) {
        double base = ((dx != 0 && dz != 0) ? 1.41421356237D : 1.0D) + Math.abs(to.getY() - from.getY()) * 0.40D;
        base += edgePenalty(level, to);
        String id = BuiltInRegistries.BLOCK.getKey(level.getBlockState(to.below()).getBlock()).toString();
        if (isPreferredRoad(id)) {
            return base * 0.35D;
        }
        if (id.contains("grass") || id.contains("dirt") || id.contains("farmland")) {
            return base * 2.20D;
        }
        if (id.contains("water") || id.contains("leaves")) {
            return base * 5.00D;
        }
        return base;
    }

    private static boolean isPreferredRoad(String blockId) {
        if (blockId == null) {
            return false;
        }
        String id = blockId.toLowerCase();
        return id.contains("dirt_path")
            || id.contains("stone_path")
            || id.contains("grass_path")
            || id.contains("gravel")
            || id.contains("road")
            || id.contains("path")
            || id.contains("cobblestone")
            || id.contains("brick")
            || id.contains("weathered_copper")
            || id.contains("exposed_copper")
            || id.contains("oxidized_copper");
    }

    private static boolean isBarrierSupport(ServerLevel level, BlockPos pos) {
        String id = BuiltInRegistries.BLOCK.getKey(level.getBlockState(pos).getBlock()).toString().toLowerCase(Locale.ROOT);
        return id.contains("fence") || id.contains("_wall") || id.contains("iron_bars") || id.contains("glass_pane");
    }

    private static boolean isBarrierAhead(ServerLevel level, Vec3 pos, double vx, double vz) {
        double lookAhead = 0.7D;
        int bx = (int) Math.floor(pos.x + Math.signum(vx) * lookAhead);
        int by = (int) Math.floor(pos.y);
        int bz = (int) Math.floor(pos.z + Math.signum(vz) * lookAhead);
        BlockPos feet = new BlockPos(bx, by, bz);
        return isBarrierSupport(level, feet) || isBarrierSupport(level, feet.below()) || level.getBlockState(feet).blocksMotion();
    }

    private static boolean isPathDoorPassable(BlockState state) {
        if (!(state.getBlock() instanceof DoorBlock)) {
            return false;
        }
        return state.getBlock() != Blocks.IRON_DOOR;
    }

    private static void tryOpenNearbyDoors(ServerLevel level,
                                           StardewNpcEntity npc,
                                           Vec3 waypoint,
                                           Vec3 target) {
        if (level == null || npc == null) {
            return;
        }
        BlockPos npcPos = npc.blockPosition();
        tryOpenDoorAt(level, npc, npcPos);
        tryOpenDoorAt(level, npc, npcPos.above());

        if (waypoint != null) {
            BlockPos wayPos = BlockPos.containing(waypoint);
            tryOpenDoorAt(level, npc, wayPos);
            tryOpenDoorAt(level, npc, wayPos.above());
        }

        if (target != null) {
            BlockPos targetPos = BlockPos.containing(target);
            tryOpenDoorAt(level, npc, targetPos);
            tryOpenDoorAt(level, npc, targetPos.above());
        }
    }

    private static void tryOpenDoorAt(ServerLevel level, StardewNpcEntity npc, BlockPos probePos) {
        if (probePos == null) {
            return;
        }

        BlockState state = level.getBlockState(probePos);
        if (!(state.getBlock() instanceof DoorBlock)) {
            return;
        }
        DoorBlock door = (DoorBlock) state.getBlock();

        BlockPos lowerPos = probePos;
        if (state.hasProperty(DoorBlock.HALF) && state.getValue(DoorBlock.HALF) == DoubleBlockHalf.UPPER) {
            lowerPos = probePos.below();
            state = level.getBlockState(lowerPos);
            if (!(state.getBlock() instanceof DoorBlock lowerDoor)) {
                return;
            }
            door = lowerDoor;
        }

        if (state.getBlock() == Blocks.IRON_DOOR) {
            return;
        }

        if (!state.hasProperty(DoorBlock.OPEN) || state.getValue(DoorBlock.OPEN)) {
            return;
        }

        door.setOpen(npc, level, state, lowerPos, true);
    }

    private static double edgePenalty(ServerLevel level, BlockPos pos) {
        int blocked = 0;
        int[][] dirs = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        for (int[] d : dirs) {
            BlockPos side = new BlockPos(pos.getX() + d[0], pos.getY(), pos.getZ() + d[1]);
            if (!canStand(level, side)) {
                blocked++;
            }
        }
        return blocked * 0.05D;
    }

    private static double heuristic(BlockPos a, BlockPos b) {
        int dx = a.getX() - b.getX();
        int dz = a.getZ() - b.getZ();
        return Math.sqrt(dx * dx + dz * dz);
    }

    private static long pack(BlockPos pos) {
        long x = ((long) pos.getX() & 0x3FFFFFFL) << 38;
        long z = ((long) pos.getZ() & 0x3FFFFFFL) << 12;
        long y = (long) pos.getY() & 0xFFFL;
        return x | z | y;
    }

    private static void ensureRouteTargetChunkForced(ServerLevel level, String rawNpcId, Vec3 target) {
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

    private static String currentForcedTargetChunk(String rawNpcId) {
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

    private static void ensureRouteCorridorChunksForced(ServerLevel level, String rawNpcId, Vec3 from, Vec3 to) {
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

    private static boolean markAndCheckScheduleNodeChange(String npcId, NpcRuntimeState state) {
        if (state == null || npcId == null || npcId.isBlank()) {
            return false;
        }
        String signature = state.activeScheduleKey() + "#" + state.scheduleCheckpoint() + "#" + state.scheduleNodeIndex()
            + "#" + state.locationName() + "#" + state.tileX() + "#" + state.tileY();
        String previous = LAST_NODE_SIGNATURE.put(npcId, signature);
        return previous != null && !previous.equals(signature);
    }

    private static void applyFacing(StardewNpcEntity npc, NpcRuntimeState state) {
        if (state == null) {
            return;
        }

        float yaw = switch (state.facing()) {
            case 1 -> 180.0F;
            case 2 -> 0.0F;
            case 3 -> 90.0F;
            case 0 -> -90.0F;
            default -> npc.getYRot();
        };
        npc.setYRot(yaw);
        npc.setYHeadRot(yaw);
    }

    private static final int[][] DIR8 = {
        {1, 0}, {-1, 0}, {0, 1}, {0, -1},
        {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
    };

    private enum RouteStepMode {
        WALK,
        WARP
    }

    private static final class LewisRouteStep {
        private final RouteStepMode mode;
        private final String pointId;
        private final Vec3 target;

        private LewisRouteStep(RouteStepMode mode, String pointId, Vec3 target) {
            this.mode = mode;
            this.pointId = pointId;
            this.target = target;
        }

        private static LewisRouteStep walk(String pointId, Vec3 target) {
            return new LewisRouteStep(RouteStepMode.WALK, pointId, target);
        }

        private static LewisRouteStep warp(String pointId, Vec3 target) {
            return new LewisRouteStep(RouteStepMode.WARP, pointId, target);
        }
    }

    private static final class LewisRouteContext {
        private final String canonicalLocation;
        private final List<LewisRouteStep> destinationSteps;

        private LewisRouteContext(String canonicalLocation, List<LewisRouteStep> destinationSteps) {
            this.canonicalLocation = canonicalLocation;
            this.destinationSteps = destinationSteps;
        }
    }

    private static final class LewisRoutePlan {
        private final String signature;
        private final UUID boundEntityUuid;
        private final List<LewisRouteStep> steps;
        private int currentStepIndex;
        private List<Vec3> path;
        private int pathIndex;
        private long lastProgressTick;
        private long lastRepathTick;
        private long noPathRetryBlockedUntilTick;
        private Vec3 lastPos;
        private String debugStage;
        private String debugPointId;
        private boolean lastFallbackTeleportUsed;
        private Vec3 debugTarget;
        private Vec3 debugNextWaypoint;
        private String debugRepathReason;

        private LewisRoutePlan(String signature, UUID boundEntityUuid, List<LewisRouteStep> steps, long now) {
            this.signature = signature;
            this.boundEntityUuid = boundEntityUuid;
            this.steps = steps;
            this.currentStepIndex = 0;
            this.path = new ArrayList<>();
            this.pathIndex = 0;
            this.lastProgressTick = now;
            this.lastRepathTick = now;
            this.noPathRetryBlockedUntilTick = now;
            this.lastPos = Vec3.ZERO;
            this.debugStage = "init";
            this.debugPointId = "<none>";
            this.lastFallbackTeleportUsed = false;
            this.debugTarget = Vec3.ZERO;
            this.debugNextWaypoint = Vec3.ZERO;
            this.debugRepathReason = "none";
        }
    }

    public record DebugSnapshot(
        String stage,
        String location,
        String pointId,
        int pathSize,
        int pathIndex,
        boolean fallbackTeleportUsed,
        Vec3 target,
        Vec3 nextWaypoint,
        String repathReason,
        long noPathTicks,
        String forcedTargetChunk
    ) {
    }

    private static final class Node {
        private final BlockPos pos;
        private final Node parent;
        private final double g;
        private final double f;

        private Node(BlockPos pos, Node parent, double g, double f) {
            this.pos = pos;
            this.parent = parent;
            this.g = g;
            this.f = f;
        }
    }
}
