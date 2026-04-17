package com.stardew.craft.npc.runtime;

import com.stardew.craft.entity.npc.StardewNpcEntity;
import com.stardew.craft.interior.InteriorSubspaceManager;
import com.stardew.craft.npc.data.NpcCapabilityProfile;
import com.stardew.craft.npc.data.NpcDataRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * NPC movement controller driven by schedule targets and data-defined route profiles.
 */
@SuppressWarnings("null")
public final class NpcCentralMovementService {
    private static final double WALK_SPEED = 0.14D;
    private static final double WAYPOINT_REACH_SQR = 0.45D * 0.45D;
    private static final double WAYPOINT_REACH_TURN_SQR = 0.20D * 0.20D;  // tighter threshold on sharp turns
    private static final double DONE_RESYNC_DIST_SQR = 9.0D * 9.0D;
    private static final double DOOR_AXIS_LOCK_DIST_SQR = 4.0D;
    private static final int REPATH_COOLDOWN_TICKS = 10;
    private static final int NO_PATH_BASE_COOLDOWN_TICKS = 60;
    private static final int NO_PATH_MAX_COOLDOWN_TICKS = 800;
    private static final int NO_PATH_CONSECUTIVE_FAIL_TELEPORT = 8;
    private static final int NO_PATH_NEAR_QUICK_SYNC_TICKS = 30;
    private static final double NO_PATH_NEAR_QUICK_SYNC_DIST_SQR = 4.0D * 4.0D;
    private static final int STUCK_REPATH_TICKS = 20;
    private static final int MAX_COLLISION_RETPATHS = 2;
    /** Block-level stationary: ticks at same block before repath + penalty (~1.0s). */
    private static final int STATIONARY_REPATH_TICKS = 20;
    /** Block-level stationary: ticks before skipping waypoint + repath (~2.0s). */
    private static final int STATIONARY_SKIP_TICKS = 40;
    /** Block-level stationary: ticks before teleport — absolute last resort (~3.0s). */
    private static final int STATIONARY_TELEPORT_TICKS = 60;
    /** Proactive repath interval (ticks) even when making progress (~3.0s). */
    private static final int PERIODIC_REPATH_TICKS = 60;
    private static final int NO_PATH_FORCE_SYNC_TICKS = 100;
    private static final double NO_PATH_FORCE_SYNC_DIST_SQR = 8.0D * 8.0D;
    private static final int NO_PATH_FORCE_SYNC_MAX_TICKS = 400;
    private static final double NO_PATH_FORCE_SYNC_MAX_DIST_SQR = 18.0D * 18.0D;
    private static final int NO_PATH_FORCE_SYNC_FAR_TICKS = 300;

    private static final Map<String, NpcRoutePlan> ACTIVE_PLANS = new HashMap<>();
    private static final Map<String, String> LAST_NODE_SIGNATURE = new HashMap<>();
    private static final Map<String, DebugSnapshot> DEBUG_SNAPSHOTS = new HashMap<>();
    private static MinecraftServer activeServer;

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

            String npcId = NpcRoutePlanner.canonicalNpcId(profile.npcId());
            activeNpcIds.add(npcId);
            // Skip NPCs that live in a different dimension (e.g. dwarf in mining)
            if (NpcSpawnManager.isMiningDimensionNpc(npcId)) continue;
            StardewNpcEntity npc = NpcSpawnManager.getTrackedNpc(level, npcId);
            if (npc == null) {
                continue;
            }

            npc.getNavigation().stop();

            // Pause movement while NPC is in a facing override (dialogue/gift interaction).
            // The entity should stand still and face the player until the interaction completes.
            if (npc.isFacingOverrideActive()) {
                npc.setDeltaMovement(Vec3.ZERO);
                DEBUG_SNAPSHOTS.computeIfAbsent(npcId, k -> new DebugSnapshot()).update("interaction_pause", "<none>", "<none>", 0, 0, false, npc.position(), npc.position(), "none", 0, "<none>");
                continue;
            }

            NpcRuntimeState state = runtimeStates.get(npcId);
            boolean pathingSuppressed = !profile.canRunPathing() || (state != null && state.pathingSuppressed());
            if (pathingSuppressed) {
                npc.setDeltaMovement(Vec3.ZERO);
                applyFacing(npc, state);
                DEBUG_SNAPSHOTS.computeIfAbsent(npcId, k -> new DebugSnapshot()).update("pathing_disabled", "<none>", "<none>", 0, 0, false, npc.position(), npc.position(), "none", 0, "<none>");
                continue;
            }

            NpcRoutePlanner.NpcRouteContext route = NpcRoutePlanner.resolveRoute(level, npcId, state);
            if (route == null) {
                npc.setDeltaMovement(Vec3.ZERO);
                applyFacing(npc, state);
                DEBUG_SNAPSHOTS.computeIfAbsent(npcId, k -> new DebugSnapshot()).update("no_route", "<none>", "<none>", 0, 0, false, npc.position(), npc.position(), "none", 0, "<none>");
                continue;
            }
            boolean nodeChanged = markAndCheckScheduleNodeChange(npcId, state);
            String signature = buildPlanSignature(state, route);

            NpcRoutePlan plan = ACTIVE_PLANS.get(npcId);
            boolean entityReplaced = plan != null && !npc.getUUID().equals(plan.boundEntityUuid);
            boolean needNewPlan = plan == null || !signature.equals(plan.signature) || entityReplaced;
            if (needNewPlan || nodeChanged) {
                plan = buildPlan(level, npc, route, signature, level.getGameTime());
                ACTIVE_PLANS.put(npcId, plan);
            }

            if (plan == null || plan.steps.isEmpty()) {
                npc.setDeltaMovement(Vec3.ZERO);
                applyFacing(npc, state);
                DEBUG_SNAPSHOTS.computeIfAbsent(npcId, k -> new DebugSnapshot()).update("empty_plan", route.canonicalLocation, "<none>", 0, 0, false, npc.position(), npc.position(), "none", 0, "<none>");
                continue;
            }

            executePlanTick(level, npc, plan);
            if ("done".equals(plan.debugStage) || "done_resync".equals(plan.debugStage)) {
                applyFacing(npc, state);
            }
            DEBUG_SNAPSHOTS.computeIfAbsent(npcId, k -> new DebugSnapshot()).update(
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
                NpcChunkForceManager.currentForcedTargetChunk(npcId)
            );
        }

        NpcChunkForceManager.releaseInactiveForcedChunks(level, activeNpcIds);

        // Evict expired collision penalties every 200 ticks to prevent unbounded HashMap growth.
        if (level.getGameTime() % 200 == 0) {
            NpcPathfinder.cleanupExpiredPenalties(level.getGameTime());
        }

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
        LAST_NODE_SIGNATURE.clear();
        DEBUG_SNAPSHOTS.clear();
        NpcRoutePlanner.resetState();
        NpcChunkForceManager.resetState();
        NpcScheduleRuntimeService.invalidateCache();
        NpcPathfinder.resetState();
    }

    private static NpcRoutePlan buildPlan(ServerLevel level,
                                            StardewNpcEntity npc,
                                            NpcRoutePlanner.NpcRouteContext route,
                                            String signature,
                                            long gameTime) {
        List<NpcRoutePlanner.NpcRouteStep> expanded = new ArrayList<>();

        boolean npcIndoors = InteriorSubspaceManager.isInteriorRegion(level, npc.blockPosition());
        Vec3 finalTarget = route.destinationSteps.get(route.destinationSteps.size() - 1).target;
        boolean destIndoors = InteriorSubspaceManager.isInteriorRegion(level, BlockPos.containing(finalTarget));

        if (npcIndoors && destIndoors) {
            // Check if both are in the SAME interior (close enough for direct walk).
            double indoorDist = npc.position().distanceToSqr(finalTarget);
            if (indoorDist < 80.0D * 80.0D) {
                // Same interior — walk directly to final target without exiting.
                expanded.add(NpcRoutePlanner.NpcRouteStep.walk("indoor_direct", finalTarget));
            } else {
                // DIFFERENT interiors — must exit current, walk outdoor, enter destination.
                Vec3 exitIndoor = NpcRoutePlanner.nearestKnownIndoorEntry(npc.position());
                Vec3 exitOutdoor = (exitIndoor != null) ? NpcRoutePlanner.linkedOutdoorDoor(exitIndoor) : null;
                if (exitIndoor != null && exitOutdoor != null) {
                    expanded.add(NpcRoutePlanner.NpcRouteStep.walk("indoor_exit", exitIndoor));
                    expanded.add(NpcRoutePlanner.NpcRouteStep.warp("outdoor_door", exitOutdoor));
                    // Then follow the destination steps (skip the outdoor door we already warped to)
                    for (NpcRoutePlanner.NpcRouteStep ds : route.destinationSteps) {
                        if (ds.mode == NpcRoutePlanner.RouteStepMode.WALK
                            && ds.target.distanceToSqr(exitOutdoor) < 4.0D) {
                            continue;
                        }
                        expanded.add(ds);
                    }
                } else {
                    // Cannot resolve proper exit — warp directly to destination's first WALK target
                    // to avoid generating impossible cross-interior WALK steps.
                    Vec3 warpTarget = null;
                    for (NpcRoutePlanner.NpcRouteStep ds : route.destinationSteps) {
                        if (ds.mode == NpcRoutePlanner.RouteStepMode.WALK) {
                            warpTarget = ds.target;
                            break;
                        }
                    }
                    if (warpTarget == null) {
                        warpTarget = finalTarget;
                    }
                    expanded.add(NpcRoutePlanner.NpcRouteStep.warp("cross_interior_emergency", warpTarget));
                    // Add remaining steps after the warp target
                    boolean past = false;
                    for (NpcRoutePlanner.NpcRouteStep ds : route.destinationSteps) {
                        if (!past && ds.target.distanceToSqr(warpTarget) < 4.0D) {
                            past = true;
                            continue;
                        }
                        if (past) {
                            expanded.add(ds);
                        }
                    }
                }
            }
        } else if (npcIndoors) {
            // Need to exit interior first, then follow outdoor route.
            Vec3 exitIndoor = NpcRoutePlanner.nearestKnownIndoorEntry(npc.position());
            Vec3 exitOutdoor = NpcRoutePlanner.linkedOutdoorDoor(exitIndoor);
            if (exitIndoor != null && exitOutdoor != null) {
                expanded.add(NpcRoutePlanner.NpcRouteStep.walk("indoor_exit", exitIndoor));
                expanded.add(NpcRoutePlanner.NpcRouteStep.warp("outdoor_door", exitOutdoor));
            } else {
                // Cannot resolve exit path — find the first outdoor WALK target
                // or any WALK target in the destination steps and warp there.
                Vec3 emergencyTarget = null;
                for (NpcRoutePlanner.NpcRouteStep ds : route.destinationSteps) {
                    if (ds.mode == NpcRoutePlanner.RouteStepMode.WALK) {
                        emergencyTarget = ds.target;
                        if (!InteriorSubspaceManager.isInteriorRegion(level, BlockPos.containing(ds.target))) {
                            break; // prefer outdoor target
                        }
                    }
                }
                if (emergencyTarget == null) {
                    emergencyTarget = finalTarget;
                }
                expanded.add(NpcRoutePlanner.NpcRouteStep.warp("indoor_exit_emergency", emergencyTarget));
            }
            // Skip destination steps that duplicate the outdoor door we already warped to.
            for (NpcRoutePlanner.NpcRouteStep ds : route.destinationSteps) {
                if (exitOutdoor != null && ds.mode == NpcRoutePlanner.RouteStepMode.WALK
                    && ds.target.distanceToSqr(exitOutdoor) < 4.0D) {
                    continue; // already at this position after warp
                }
                expanded.add(ds);
            }
        } else {
            expanded.addAll(route.destinationSteps);
        }
        NpcRoutePlan plan = new NpcRoutePlan(signature, npc.getUUID(), expanded, gameTime);

        // ── Diagnostic: log plan steps once per new plan ──
        StringBuilder sb = new StringBuilder();
        sb.append("[NPC_PLAN] npc=").append(npc.getNpcId())
          .append(" indoor=").append(npcIndoors)
          .append(" destIndoor=").append(destIndoors)
          .append(" pos=").append(fmt(npc.position()))
          .append(" steps=[");
        for (int i = 0; i < expanded.size(); i++) {
            NpcRoutePlanner.NpcRouteStep s = expanded.get(i);
            if (i > 0) sb.append(", ");
            sb.append(s.mode).append(':').append(s.pointId).append('@').append(fmt(s.target));
        }
        sb.append(']');
        com.stardew.craft.StardewCraft.LOGGER.info(sb.toString());

        return plan;
    }

    private static String fmt(Vec3 v) {
        return v == null ? "null" : String.format(java.util.Locale.ROOT, "(%.1f,%.1f,%.1f)", v.x, v.y, v.z);
    }

    // ──── Plan execution helpers (de-duplicated from executePlanTick) ────

    /** Teleport NPC to the current step target and advance to the next step. */
    private static void advanceStepByTeleport(StardewNpcEntity npc, NpcRoutePlan plan, Vec3 target, long now) {
        npc.setDeltaMovement(Vec3.ZERO);
        npc.setPos(target.x, target.y, target.z);
        plan.currentStepIndex++;
        plan.path.clear();
        plan.pathIndex = 0;
        plan.consecutivePathFailures = 0;
        plan.lastProgressTick = now;
        plan.stationaryTicks = 0;
        BlockPos tpBlock = npc.blockPosition();
        plan.lastBlockX = tpBlock.getX();
        plan.lastBlockY = tpBlock.getY();
        plan.lastBlockZ = tpBlock.getZ();
    }

    /** Record a path failure and apply exponential backoff to the next retry. */
    private static int applyPathFailureBackoff(NpcRoutePlan plan, long now) {
        plan.consecutivePathFailures++;
        int cooldown = Math.min(NO_PATH_MAX_COOLDOWN_TICKS,
            NO_PATH_BASE_COOLDOWN_TICKS * (1 << Math.min(plan.consecutivePathFailures - 1, 4)));
        plan.noPathRetryBlockedUntilTick = now + cooldown;
        return cooldown;
    }

    /** Called when a fresh A* path was successfully computed. Resets failure counters and opens doors. */
    private static void onPathSuccess(ServerLevel level, StardewNpcEntity npc, NpcRoutePlan plan, long now) {
        plan.consecutivePathFailures = 0;
        plan.noPathRetryBlockedUntilTick = now;
        openAllDoorsOnPath(level, npc, plan.path);
    }

    /** Try to compute a new A* path. Returns true if A* was actually invoked (budget permitting). */
    private static boolean tryRepath(ServerLevel level, StardewNpcEntity npc, NpcRoutePlan plan, Vec3 target, long now) {
        if (!NpcPathfinder.allowAstarCall(level)) {
            plan.debugRepathReason = "astar_tick_budget";
            return false;
        }
        plan.path = NpcPathfinder.planPath(level, npc.position(), target);
        plan.pathIndex = 0;
        plan.lastRepathTick = now;
        return true;
    }

    /**
     * Penalize a 3×3 area in the direction the NPC was heading.
     * This prevents A* from finding nearly-identical paths that hit the same wall.
     */
    private static void addAreaCollisionPenalty(ServerLevel level, Vec3 npcPos, double dirX, double dirZ) {
        int cx = (int) Math.floor(npcPos.x + Math.signum(dirX));
        int cy = (int) Math.floor(npcPos.y);
        int cz = (int) Math.floor(npcPos.z + Math.signum(dirZ));
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                NpcPathfinder.addCollisionPenalty(level, new Vec3(cx + dx + 0.5D, cy, cz + dz + 0.5D));
            }
        }
        // Also penalize the NPC's own position
        NpcPathfinder.addCollisionPenalty(level, npcPos);
    }

    private static boolean executePlanTick(ServerLevel level,
                                           StardewNpcEntity npc,
                                           NpcRoutePlan plan) {
        if (plan.currentStepIndex >= plan.steps.size()) {
            Vec3 finalTarget = plan.steps.isEmpty() ? null : plan.steps.get(plan.steps.size() - 1).target;
            if (finalTarget != null) {
                Vec3 delta = finalTarget.subtract(npc.position());
                double d2dSqr = delta.x * delta.x + delta.z * delta.z;
                if (d2dSqr > DONE_RESYNC_DIST_SQR) {
                    npc.setPos(finalTarget.x, finalTarget.y, finalTarget.z);
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
        NpcRoutePlanner.NpcRouteStep step = plan.steps.get(plan.currentStepIndex);
        plan.debugPointId = step.pointId;
        plan.debugTarget = step.target;
        plan.debugNextWaypoint = step.target;
        long now = level.getGameTime();

        if (step.mode == NpcRoutePlanner.RouteStepMode.WARP) {
            NpcChunkForceManager.ensureRouteTargetChunkForced(level, npc.getNpcId(), step.target);
            plan.debugStage = "warp";
            com.stardew.craft.StardewCraft.LOGGER.info("[NPC_EXEC] npc={} WARP step={} target={}", npc.getNpcId(), step.pointId, fmt(step.target));
            advanceStepByTeleport(npc, plan, step.target, now);
            plan.lastRepathTick = now;
            return false;
        }

        NpcChunkForceManager.ensureRouteTargetChunkForced(level, npc.getNpcId(), step.target);
        NpcChunkForceManager.ensureRouteCorridorChunksForced(level, npc.getNpcId(), npc.position(), step.target);

        // ── Phase 1: Acquire a path if we don't have one ──
        if ((plan.path.isEmpty() || plan.pathIndex >= plan.path.size())
            && now - plan.lastRepathTick >= REPATH_COOLDOWN_TICKS
            && now >= plan.noPathRetryBlockedUntilTick) {
            boolean attempted = tryRepath(level, npc, plan, step.target, now);
            if (attempted) {
                plan.debugRepathReason = "path_init_or_exhausted";
                if (plan.path.isEmpty()) {
                    plan.debugStage = "no_path_repath";
                    int cooldown = applyPathFailureBackoff(plan, now);
                    plan.debugRepathReason = "no_path_backoff_" + cooldown + "t_fail" + plan.consecutivePathFailures;
                    if (plan.consecutivePathFailures <= 2) {
                        com.stardew.craft.StardewCraft.LOGGER.warn("[NPC_ASTAR_FAIL] npc={} step={} pos={} target={} fail#{}", npc.getNpcId(), step.pointId, fmt(npc.position()), fmt(step.target), plan.consecutivePathFailures);
                    }
                } else {
                    onPathSuccess(level, npc, plan, now);
                }
            } else {
                plan.debugRepathReason = "astar_tick_budget";
            }
        }

        // ── Phase 2: Handle empty path (no route to target) ──
        if (plan.path.isEmpty()) {
            plan.debugStage = "no_path";
            npc.setDeltaMovement(Vec3.ZERO);

            // Fast teleport after N consecutive failures
            if (plan.consecutivePathFailures >= NO_PATH_CONSECUTIVE_FAIL_TELEPORT) {
                plan.debugStage = "no_path_consecutive_fail_tp";
                plan.lastFallbackTeleportUsed = true;
                com.stardew.craft.StardewCraft.LOGGER.warn("[NPC_TELEPORT] npc={} reason=consecutive_fail step={} pos={} target={}", npc.getNpcId(), step.pointId, fmt(npc.position()), fmt(step.target));
                advanceStepByTeleport(npc, plan, step.target, now);
                plan.debugRepathReason = "consecutive_fail_teleport_" + NO_PATH_CONSECUTIVE_FAIL_TELEPORT;
                return false;
            }

            // Retry A* while stuck with no path
            if (now - plan.lastProgressTick > STUCK_REPATH_TICKS
                && now >= plan.noPathRetryBlockedUntilTick) {
                boolean retryAttempted = tryRepath(level, npc, plan, step.target, now);
                if (retryAttempted) {
                    if (plan.path.isEmpty()) {
                        int cooldown = applyPathFailureBackoff(plan, now);
                        plan.debugRepathReason = "no_path_stuck_backoff_" + cooldown + "t_fail" + plan.consecutivePathFailures;
                    } else {
                        onPathSuccess(level, npc, plan, now);
                        plan.debugRepathReason = "no_path_stuck_repath";
                    }
                }
            } else if (now < plan.noPathRetryBlockedUntilTick) {
                plan.debugRepathReason = "no_path_cooldown";
            }

            // Timed fallback teleport (escalating windows based on distance)
            if (shouldNoPathFallbackTeleport(plan, npc, step.target, now)) {
                plan.debugStage = "no_path_fallback_tp";
                plan.lastFallbackTeleportUsed = true;
                com.stardew.craft.StardewCraft.LOGGER.warn("[NPC_TELEPORT] npc={} reason=no_path_timeout step={} pos={} target={}", npc.getNpcId(), step.pointId, fmt(npc.position()), fmt(step.target));
                advanceStepByTeleport(npc, plan, step.target, now);
            }
            return false;
        }

        // ── Phase 3: Walk along the path ──
        Vec3 waypoint = plan.path.get(Math.min(plan.pathIndex, plan.path.size() - 1));
        plan.debugNextWaypoint = waypoint;

        // Open doors ahead of the NPC
        tryOpenNearbyDoors(level, npc, waypoint, step.target);
        for (int ahead = 1; ahead <= 5 && plan.pathIndex + ahead < plan.path.size(); ahead++) {
            Vec3 futureWp = plan.path.get(plan.pathIndex + ahead);
            BlockPos futurePos = BlockPos.containing(futureWp);
            tryOpenDoorAt(level, npc, futurePos);
            tryOpenDoorAt(level, npc, futurePos.above());
        }

        Vec3 horizontal = new Vec3(waypoint.x - npc.getX(), 0.0D, waypoint.z - npc.getZ());

        // Advance waypoint if reached.
        // Use a tighter threshold when the path turns sharply at this point,
        // so the NPC reaches the center of the corridor before changing direction.
        double reachSqr = WAYPOINT_REACH_SQR;
        if (plan.pathIndex + 1 < plan.path.size()) {
            Vec3 nextWp = plan.path.get(plan.pathIndex + 1);
            Vec3 toNext = new Vec3(nextWp.x - waypoint.x, 0, nextWp.z - waypoint.z);
            Vec3 toCur = horizontal; // direction FROM npc TO current waypoint
            double dot = toCur.normalize().dot(toNext.normalize());
            // dot < 0.5 means > 60° turn — tighten reach to prevent corner cutting
            if (dot < 0.5D) {
                reachSqr = WAYPOINT_REACH_TURN_SQR;
            }
        }
        if (horizontal.lengthSqr() <= reachSqr) {
            plan.pathIndex++;
            plan.collisionRetpathCount = 0;
            plan.lastProgressTick = now;
            if (plan.pathIndex >= plan.path.size()) {
                advanceStepByTeleport(npc, plan, step.target, now);
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

            // Force axis-aligned movement near doors to prevent collision stutter
            if (shouldAxisLockNearOpenDoor(level, npc, waypoint)
                && Math.abs(vx) > 1.0E-4D
                && Math.abs(vz) > 1.0E-4D) {
                if (Math.abs(vx) >= Math.abs(vz)) {
                    vx = Math.signum(vx) * WALK_SPEED;
                    vz = 0.0D;
                } else {
                    vz = Math.signum(vz) * WALK_SPEED;
                    vx = 0.0D;
                }
                plan.debugRepathReason = "door_axis_lock";
            }

            double verticalRise = waypoint.y - npc.getY();
            // Only jump for genuine step-ups; never jump just because of horizontal collision.
            boolean needsToJump = verticalRise > 0.7D;

            // ── ALWAYS stop pushing into walls, even during repath cooldown ──
            if (npc.horizontalCollision && !needsToJump) {
                vx = 0.0D;
                vz = 0.0D;
            }

            // ── Collision handling — repath when cooldown allows ──
            if (npc.horizontalCollision && !needsToJump
                && now - plan.lastRepathTick > REPATH_COOLDOWN_TICKS) {
                plan.collisionRetpathCount++;
                // Penalize a 3x3 area around the obstacle to prevent repath finding
                // nearly-identical routes that also hit the same wall.
                addAreaCollisionPenalty(level, npc.position(), horizontal.x, horizontal.z);
                if (plan.collisionRetpathCount >= MAX_COLLISION_RETPATHS) {
                    // Skip this waypoint and repath (no teleport — NPC must walk)
                    plan.pathIndex++;
                    plan.collisionRetpathCount = 0;
                    plan.lastRepathTick = now;
                    addAreaCollisionPenalty(level, npc.position(), horizontal.x, horizontal.z);
                    if (tryRepath(level, npc, plan, step.target, now) && !plan.path.isEmpty()) {
                        openAllDoorsOnPath(level, npc, plan.path);
                    }
                    plan.debugStage = "collision_skip_waypoint";
                    plan.debugRepathReason = "collision_skip_repath";
                } else {
                    // Repath immediately on first collision
                    if (tryRepath(level, npc, plan, step.target, now) && !plan.path.isEmpty()) {
                        openAllDoorsOnPath(level, npc, plan.path);
                    }
                    plan.debugStage = "collision_repath";
                    plan.debugRepathReason = "collision_repath_" + plan.collisionRetpathCount;
                }
            }

            if (needsToJump && npc.onGround() && !npc.horizontalCollision) {
                vy = 0.42D;
                plan.debugStage = "walk_jump";
            }

            trySnapToDoorCenter(level, npc, waypoint);

            npc.setDeltaMovement(vx, vy, vz);
            // Only update facing when actually moving; avoids snapping to south
            // (atan2(0,0)=0) during collision frames, which caused visual jitter.
            if (vx != 0.0D || vz != 0.0D) {
                float moveYaw = (float) (Math.toDegrees(Math.atan2(-vx, vz)));
                npc.setYRot(moveYaw);
                npc.setYHeadRot(moveYaw);
            }
            npc.hurtMarked = true;
        }

        // ── Phase 4: Block-level stationary detection (Citizens2-inspired) ──
        // Integer block coordinates are immune to sub-block jitter/oscillation
        // that fooled the old XZ-displacement detection.
        int curBlockX = npc.blockPosition().getX();
        int curBlockY = npc.blockPosition().getY();
        int curBlockZ = npc.blockPosition().getZ();

        if (curBlockX != plan.lastBlockX || curBlockY != plan.lastBlockY || curBlockZ != plan.lastBlockZ) {
            // NPC moved to a different block — genuine progress
            plan.lastBlockX = curBlockX;
            plan.lastBlockY = curBlockY;
            plan.lastBlockZ = curBlockZ;
            plan.stationaryTicks = 0;
            plan.lastProgressTick = now;
            plan.collisionRetpathCount = 0;
        } else {
            plan.stationaryTicks++;
        }

        // ── Stuck escalation ladder (single unified system) ──
        if (plan.stationaryTicks >= STATIONARY_TELEPORT_TICKS) {
            // TIER 3 (3.0s): Absolute last resort — teleport to step target
            plan.debugStage = "stationary_teleport";
            plan.lastFallbackTeleportUsed = true;
            com.stardew.craft.StardewCraft.LOGGER.warn("[NPC_TELEPORT] npc={} reason=stationary step={} pos={} target={} ticks={}",
                npc.getNpcId(), step.pointId, fmt(npc.position()), fmt(step.target), plan.stationaryTicks);
            advanceStepByTeleport(npc, plan, step.target, now);
            plan.debugRepathReason = "stationary_teleport";
        } else if (plan.stationaryTicks >= STATIONARY_SKIP_TICKS
                   && now - plan.lastRepathTick > REPATH_COOLDOWN_TICKS) {
            // TIER 2 (2.0s): Skip current waypoint, penalize area, repath
            npc.setDeltaMovement(Vec3.ZERO);
            addAreaCollisionPenalty(level, npc.position(), waypoint.x - npc.getX(), waypoint.z - npc.getZ());
            plan.pathIndex++;
            plan.collisionRetpathCount = 0;
            if (tryRepath(level, npc, plan, step.target, now) && !plan.path.isEmpty()) {
                openAllDoorsOnPath(level, npc, plan.path);
            }
            plan.debugStage = "stationary_skip";
            plan.debugRepathReason = "stationary_skip_" + plan.stationaryTicks + "t";
        } else if (plan.stationaryTicks >= STATIONARY_REPATH_TICKS
                   && now - plan.lastRepathTick > REPATH_COOLDOWN_TICKS) {
            // TIER 1 (1.0s): Penalize obstacle area and repath
            npc.setDeltaMovement(Vec3.ZERO);
            addAreaCollisionPenalty(level, npc.position(), waypoint.x - npc.getX(), waypoint.z - npc.getZ());
            if (tryRepath(level, npc, plan, step.target, now) && !plan.path.isEmpty()) {
                openAllDoorsOnPath(level, npc, plan.path);
            }
            plan.debugStage = "stationary_repath";
            plan.debugRepathReason = "stationary_repath_" + plan.stationaryTicks + "t";
        }

        // ── Phase 5: Periodic proactive repath ──
        // Even if making progress, periodically recompute path to catch stale
        // routes that may lead to obstacles the world has changed around.
        if (plan.stationaryTicks == 0
            && now - plan.lastRepathTick >= PERIODIC_REPATH_TICKS) {
            if (tryRepath(level, npc, plan, step.target, now) && !plan.path.isEmpty()) {
                openAllDoorsOnPath(level, npc, plan.path);
                plan.debugRepathReason = "periodic_repath";
            }
        }

        return false;
    }

    /** Determine if the no-path fallback teleport should fire based on distance and time thresholds. */
    private static boolean shouldNoPathFallbackTeleport(NpcRoutePlan plan, StardewNpcEntity npc, Vec3 target, long now) {
        long noPathDuration = now - plan.lastProgressTick;
        double toTarget = npc.position().distanceToSqr(target);
        boolean nearQuickFallback = noPathDuration > NO_PATH_NEAR_QUICK_SYNC_TICKS
            && toTarget <= NO_PATH_NEAR_QUICK_SYNC_DIST_SQR;
        boolean nearTarget = toTarget <= NO_PATH_FORCE_SYNC_MAX_DIST_SQR;
        boolean shortWindowFallback = noPathDuration > NO_PATH_FORCE_SYNC_TICKS && toTarget > NO_PATH_FORCE_SYNC_DIST_SQR;
        boolean maxWindowFallback = noPathDuration > NO_PATH_FORCE_SYNC_MAX_TICKS;
        boolean farWindowFallback = noPathDuration > NO_PATH_FORCE_SYNC_FAR_TICKS;

        if (nearQuickFallback) {
            plan.debugRepathReason = "no_path_near_quick_sync";
            return true;
        }
        if (nearTarget && (shortWindowFallback || maxWindowFallback)) {
            plan.debugRepathReason = "no_path_near_fallback";
            return true;
        }
        if (!nearTarget && farWindowFallback) {
            plan.debugRepathReason = "no_path_far_timeout_fallback";
            return true;
        }
        return false;
    }

    private static String buildPlanSignature(NpcRuntimeState state, NpcRoutePlanner.NpcRouteContext route) {
        return state.activeScheduleKey() + "#" + state.scheduleCheckpoint() + "#" + state.scheduleNodeIndex()
            + "#" + route.canonicalLocation;
    }

    private static boolean shouldAxisLockNearOpenDoor(ServerLevel level,
                                                      StardewNpcEntity npc,
                                                      Vec3 waypoint) {
        if (level == null || npc == null || waypoint == null) {
            return false;
        }
        BlockPos npcPos = npc.blockPosition();
        BlockPos waypointPos = BlockPos.containing(waypoint);
        if (!isOpenDoorNearby(level, npcPos) && !isOpenDoorNearby(level, waypointPos)) {
            return false;
        }
        return npc.position().distanceToSqr(waypoint) <= DOOR_AXIS_LOCK_DIST_SQR;
    }

    private static boolean isOpenDoorNearby(ServerLevel level, BlockPos center) {
        if (level == null || center == null) {
            return false;
        }
        int[][] offsets = {
            {0, 0},
            {1, 0}, {-1, 0},
            {0, 1}, {0, -1}
        };
        for (int[] off : offsets) {
            BlockPos p = center.offset(off[0], 0, off[1]);
            if (isOpenDoorBlock(level.getBlockState(p))
                || isOpenDoorBlock(level.getBlockState(p.above()))
                || isOpenDoorBlock(level.getBlockState(p.below()))) {
                return true;
            }
        }
        return false;
    }

    private static boolean isOpenDoorBlock(BlockState state) {
        if (!(state.getBlock() instanceof DoorBlock)) {
            return false;
        }
        return state.hasProperty(DoorBlock.OPEN) && state.getValue(DoorBlock.OPEN);
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

    /**
     * Open every door block on the given A* path immediately.
     * Called each time a new path is computed so that no door is ever
     * closed when the entity physically reaches it.
     */
    private static void openAllDoorsOnPath(ServerLevel level, StardewNpcEntity npc, List<Vec3> path) {
        if (level == null || npc == null || path == null) return;
        for (Vec3 wp : path) {
            BlockPos pos = BlockPos.containing(wp);
            tryOpenDoorAt(level, npc, pos);
            tryOpenDoorAt(level, npc, pos.above());
        }
    }

    /**
     * When the NPC is at or immediately adjacent to a door block, snap its
     * perpendicular coordinate to the exact centre of the door column.
     * A wooden door open on the hinge side has a 3/16-block collision strip;
     * an entity centred at X+0.5 has a margin of 0.0125 on each side — tiny
     * enough that floating-point drift can cause a clip. Snapping guarantees
     * the entity is always perfectly centred before it passes through.
     */
    private static void trySnapToDoorCenter(ServerLevel level, StardewNpcEntity npc, Vec3 waypoint) {
        if (level == null || npc == null || waypoint == null) return;
        BlockPos npcPos = npc.blockPosition();
        BlockPos wpPos = BlockPos.containing(waypoint);
        for (BlockPos checkPos : new BlockPos[]{npcPos, wpPos}) {
            BlockState state = level.getBlockState(checkPos);
            // Try lower or upper half
            if (!(state.getBlock() instanceof DoorBlock) || state.getBlock() == Blocks.IRON_DOOR) {
                state = level.getBlockState(checkPos.above());
                if (!(state.getBlock() instanceof DoorBlock) || state.getBlock() == Blocks.IRON_DOOR) {
                    continue;
                }
            }
            // Resolve FACING from the lower half
            BlockPos lowerPos = checkPos;
            if (state.hasProperty(DoorBlock.HALF)
                && state.getValue(DoorBlock.HALF) == net.minecraft.world.level.block.state.properties.DoubleBlockHalf.UPPER) {
                lowerPos = checkPos.below();
                state = level.getBlockState(lowerPos);
                if (!(state.getBlock() instanceof DoorBlock)) continue;
            }
            if (!state.hasProperty(DoorBlock.FACING)) continue;
            net.minecraft.core.Direction facing = state.getValue(DoorBlock.FACING);
            // NORTH/SOUTH door: passage runs along Z → snap X to block centre
            // EAST/WEST door:   passage runs along X → snap Z to block centre
            // Door gap = 1 - 3/16 (strip) - 0.3 (half-width) = 0.0125; any X/Z
            // drift > 0.0125 causes a clip, so snap with a very tight tolerance.
            if (facing == net.minecraft.core.Direction.NORTH
                || facing == net.minecraft.core.Direction.SOUTH) {
                double cx = lowerPos.getX() + 0.5;
                if (Math.abs(npc.getX() - cx) > 0.005) {
                    npc.setPos(cx, npc.getY(), npc.getZ());
                }
            } else {
                double cz = lowerPos.getZ() + 0.5;
                if (Math.abs(npc.getZ() - cz) > 0.005) {
                    npc.setPos(npc.getX(), npc.getY(), cz);
                }
            }
            return; // snap to first door found
        }
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
        // Don't override yaw while the NPC is turning to face a player (dialogue / gift)
        // or idle-looking at a nearby player.
        if (npc.isFacingOverrideActive() || npc.isIdleLookActive()) {
            return;
        }

        // SDV facing: 0=north(up), 1=east(right), 2=south(down), 3=west(left)
        // MC yaw:     0°=south, 90°=west, 180°=north, -90°=east
        float yaw = switch (state.facing()) {
            case 0 -> 180.0F;   // north
            case 1 -> -90.0F;   // east
            case 2 -> 0.0F;     // south
            case 3 -> 90.0F;    // west
            default -> npc.getYRot();
        };
        npc.setYRot(yaw);
        npc.setYHeadRot(yaw);
        npc.setYBodyRot(yaw);
    }

    private static final class NpcRoutePlan {
        private final String signature;
        private final UUID boundEntityUuid;
        private final List<NpcRoutePlanner.NpcRouteStep> steps;
        private int currentStepIndex;
        private List<Vec3> path;
        private int pathIndex;
        private long lastProgressTick;
        private long lastRepathTick;
        private long noPathRetryBlockedUntilTick;
        private int consecutivePathFailures;
        private int collisionRetpathCount;
        private String debugStage;
        private String debugPointId;
        private boolean lastFallbackTeleportUsed;
        private Vec3 debugTarget;
        private Vec3 debugNextWaypoint;
        private String debugRepathReason;
        /** Block-level stationary tick counter (Citizens2-style). */
        private int stationaryTicks;
        /** Last known block coordinates for stationary detection. */
        private int lastBlockX, lastBlockY, lastBlockZ;

        private NpcRoutePlan(String signature, UUID boundEntityUuid, List<NpcRoutePlanner.NpcRouteStep> steps, long now) {
            this.signature = signature;
            this.boundEntityUuid = boundEntityUuid;
            this.steps = steps;
            this.currentStepIndex = 0;
            this.path = new ArrayList<>();
            this.pathIndex = 0;
            this.lastProgressTick = now;
            this.lastRepathTick = now;
            this.noPathRetryBlockedUntilTick = now;
            this.consecutivePathFailures = 0;
            this.collisionRetpathCount = 0;
            this.debugStage = "init";
            this.debugPointId = "<none>";
            this.lastFallbackTeleportUsed = false;
            this.debugTarget = Vec3.ZERO;
            this.debugNextWaypoint = Vec3.ZERO;
            this.debugRepathReason = "none";
            this.stationaryTicks = 0;
            this.lastBlockX = Integer.MIN_VALUE;
            this.lastBlockY = Integer.MIN_VALUE;
            this.lastBlockZ = Integer.MIN_VALUE;
        }
    }

    public static final class DebugSnapshot {
        public String stage;
        public String location;
        public String pointId;
        public int pathSize;
        public int pathIndex;
        public boolean fallbackTeleportUsed;
        public Vec3 target;
        public Vec3 nextWaypoint;
        public String repathReason;
        public long noPathTicks;
        public String forcedTargetChunk;

        public DebugSnapshot() {}

        public void update(String stage, String location, String pointId,
                           int pathSize, int pathIndex, boolean fallbackTeleportUsed,
                           Vec3 target, Vec3 nextWaypoint, String repathReason,
                           long noPathTicks, String forcedTargetChunk) {
            this.stage = stage;
            this.location = location;
            this.pointId = pointId;
            this.pathSize = pathSize;
            this.pathIndex = pathIndex;
            this.fallbackTeleportUsed = fallbackTeleportUsed;
            this.target = target;
            this.nextWaypoint = nextWaypoint;
            this.repathReason = repathReason;
            this.noPathTicks = noPathTicks;
            this.forcedTargetChunk = forcedTargetChunk;
        }
    }

    /**
     * Snap an NPC entity down to the top of the block surface below it,
     * fixing floating on slabs/stairs after spawn or warp.
     */
    public static void snapToSurface(ServerLevel level, net.minecraft.world.entity.Mob npc) {
        BlockPos below = npc.blockPosition().below();
        net.minecraft.world.level.block.state.BlockState state = level.getBlockState(below);
        if (!state.isAir()) {
            net.minecraft.world.phys.shapes.VoxelShape shape = state.getCollisionShape(level, below);
            if (!shape.isEmpty()) {
                double surfaceY = below.getY() + shape.max(net.minecraft.core.Direction.Axis.Y);
                npc.setPos(npc.getX(), surfaceY, npc.getZ());
            }
        }
    }
}
