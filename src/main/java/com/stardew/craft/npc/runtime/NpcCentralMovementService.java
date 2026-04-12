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
    private static final double DONE_RESYNC_DIST_SQR = 9.0D * 9.0D;
    private static final double DOOR_AXIS_LOCK_DIST_SQR = 4.0D;
    private static final int REPATH_COOLDOWN_TICKS = 10;
    private static final int NO_PATH_BASE_COOLDOWN_TICKS = 60;
    private static final int NO_PATH_MAX_COOLDOWN_TICKS = 800;
    private static final int NO_PATH_CONSECUTIVE_FAIL_TELEPORT = 3;
    private static final int NO_PATH_NEAR_QUICK_SYNC_TICKS = 30;
    private static final double NO_PATH_NEAR_QUICK_SYNC_DIST_SQR = 4.0D * 4.0D;
    private static final int STUCK_REPATH_TICKS = 20;
    private static final int STUCK_FORCE_SYNC_TICKS = 60;
    private static final int MAX_COLLISION_RETPATHS = 4;
    private static final double PROGRESS_MOVE_DIST_SQR = 0.16D;
    private static final double PROGRESS_TARGET_GAIN_EPSILON_SQR = 0.04D;
    private static final int NO_PATH_FORCE_SYNC_TICKS = 20;
    private static final double NO_PATH_FORCE_SYNC_DIST_SQR = 8.0D * 8.0D;
    private static final int NO_PATH_FORCE_SYNC_MAX_TICKS = 120;
    private static final double NO_PATH_FORCE_SYNC_MAX_DIST_SQR = 18.0D * 18.0D;
    private static final int NO_PATH_FORCE_SYNC_FAR_TICKS = 80;

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
            // Both in interior — walk directly to final target without exiting.
            expanded.add(NpcRoutePlanner.NpcRouteStep.walk("indoor_direct", finalTarget));
        } else if (npcIndoors) {
            // Need to exit interior first, then follow outdoor route.
            Vec3 exitIndoor = NpcRoutePlanner.nearestKnownIndoorEntry(npc.position());
            Vec3 exitOutdoor = NpcRoutePlanner.linkedOutdoorDoor(exitIndoor);
            if (exitIndoor != null && exitOutdoor != null) {
                expanded.add(NpcRoutePlanner.NpcRouteStep.walk("indoor_exit", exitIndoor));
                expanded.add(NpcRoutePlanner.NpcRouteStep.warp("outdoor_door", exitOutdoor));
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
        plan.lastPos = npc.position();
        return plan;
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
        plan.lastPos = npc.position();
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
        NpcRoutePlanner.NpcRouteStep step = plan.steps.get(plan.currentStepIndex);
        plan.debugPointId = step.pointId;
        plan.debugTarget = step.target;
        plan.debugNextWaypoint = step.target;
        long now = level.getGameTime();

        if (step.mode == NpcRoutePlanner.RouteStepMode.WARP) {
            NpcChunkForceManager.ensureRouteTargetChunkForced(level, npc.getNpcId(), step.target);
            plan.debugStage = "warp";
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
            }
            if (plan.path.isEmpty()) {
                plan.debugStage = "no_path_repath";
                int cooldown = applyPathFailureBackoff(plan, now);
                plan.debugRepathReason = "no_path_backoff_" + cooldown + "t_fail" + plan.consecutivePathFailures;
            } else {
                onPathSuccess(level, npc, plan, now);
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
                advanceStepByTeleport(npc, plan, step.target, now);
                plan.debugRepathReason = "consecutive_fail_teleport_" + NO_PATH_CONSECUTIVE_FAIL_TELEPORT;
                return false;
            }

            // Retry A* while stuck with no path
            if (now - plan.lastProgressTick > STUCK_REPATH_TICKS
                && now >= plan.noPathRetryBlockedUntilTick) {
                tryRepath(level, npc, plan, step.target, now);
                if (plan.path.isEmpty()) {
                    int cooldown = applyPathFailureBackoff(plan, now);
                    plan.debugRepathReason = "no_path_stuck_backoff_" + cooldown + "t_fail" + plan.consecutivePathFailures;
                } else {
                    onPathSuccess(level, npc, plan, now);
                    plan.debugRepathReason = "no_path_stuck_repath";
                }
            } else if (now < plan.noPathRetryBlockedUntilTick) {
                plan.debugRepathReason = "no_path_cooldown";
            }

            // Timed fallback teleport (escalating windows based on distance)
            if (shouldNoPathFallbackTeleport(plan, npc, step.target, now)) {
                plan.debugStage = "no_path_fallback_tp";
                plan.lastFallbackTeleportUsed = true;
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

        // Advance waypoint if reached
        if (horizontal.lengthSqr() <= WAYPOINT_REACH_SQR) {
            plan.pathIndex++;
            plan.collisionRetpathCount = 0;
            plan.lastProgressTick = now;
            plan.lastPos = npc.position();
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
            boolean stepUpNeeded = verticalRise > 1.05D;
            boolean needsToJump = stepUpNeeded || (npc.horizontalCollision && verticalRise > 0.55D);

            // ── Collision handling ──
            if (npc.horizontalCollision && !needsToJump
                && now - plan.lastRepathTick > REPATH_COOLDOWN_TICKS) {
                plan.collisionRetpathCount++;
                if (plan.collisionRetpathCount >= MAX_COLLISION_RETPATHS) {
                    // Micro-teleport past the obstacle
                    npc.setPos(waypoint.x, waypoint.y, waypoint.z);
                    plan.pathIndex++;
                    plan.collisionRetpathCount = 0;
                    plan.consecutivePathFailures = 0;
                    plan.lastProgressTick = now;
                    plan.lastRepathTick = now;
                    plan.lastPos = npc.position();
                    plan.debugStage = "collision_micro_tp";
                    plan.debugRepathReason = "collision_max_skip_waypoint";
                } else {
                    Vec3 penaltyPos = npc.position().add(Math.signum(vx), 0, Math.signum(vz));
                    NpcPathfinder.addCollisionPenalty(level, penaltyPos);
                    if (tryRepath(level, npc, plan, step.target, now) && !plan.path.isEmpty()) {
                        openAllDoorsOnPath(level, npc, plan.path);
                    }
                    plan.debugStage = "collision_repath";
                    plan.debugRepathReason = (NpcPathfinder.isBarrierAhead(level, npc.position(), vx, vz)
                        ? "barrier_repath_" : "collision_repath_") + plan.collisionRetpathCount;
                }
            }

            if (needsToJump && npc.onGround()) {
                vy = 0.42D;
                plan.debugStage = "walk_jump";
            }

            trySnapToDoorCenter(level, npc, waypoint);

            npc.setDeltaMovement(vx, vy, vz);
            float moveYaw = (float) (Math.toDegrees(Math.atan2(-vx, vz)));
            npc.setYRot(moveYaw);
            npc.setYHeadRot(moveYaw);
            npc.hurtMarked = true;
        }

        // ── Phase 4: Progress detection and stuck escalation ──
        double moved = npc.position().distanceToSqr(plan.lastPos);
        double currentToWaypoint = npc.position().distanceToSqr(waypoint);
        double lastToWaypoint = plan.lastPos.distanceToSqr(waypoint);
        boolean waypointProgressed = currentToWaypoint + PROGRESS_TARGET_GAIN_EPSILON_SQR < lastToWaypoint;
        if (moved > PROGRESS_MOVE_DIST_SQR && waypointProgressed) {
            plan.lastPos = npc.position();
            plan.lastProgressTick = now;
            plan.collisionRetpathCount = 0;
        }

        // Stuck repath
        if (now - plan.lastProgressTick > STUCK_REPATH_TICKS
            && now - plan.lastRepathTick > REPATH_COOLDOWN_TICKS
            && now >= plan.noPathRetryBlockedUntilTick) {
            if (tryRepath(level, npc, plan, step.target, now) && !plan.path.isEmpty()) {
                openAllDoorsOnPath(level, npc, plan.path);
            }
            plan.debugStage = "stuck_repath";
            if (plan.path.isEmpty()) {
                int cooldown = applyPathFailureBackoff(plan, now);
                plan.debugRepathReason = "stuck_repath_backoff_" + cooldown + "t";
            } else {
                plan.consecutivePathFailures = 0;
                plan.debugRepathReason = "stuck_timeout_repath";
            }
        }

        // Force teleport after being stuck too long
        if (now - plan.lastProgressTick > STUCK_FORCE_SYNC_TICKS) {
            plan.debugStage = "fallback_tp";
            plan.lastFallbackTeleportUsed = true;
            advanceStepByTeleport(npc, plan, step.target, now);
            plan.debugRepathReason = "stuck_force_teleport";
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
        private Vec3 lastPos;
        private String debugStage;
        private String debugPointId;
        private boolean lastFallbackTeleportUsed;
        private Vec3 debugTarget;
        private Vec3 debugNextWaypoint;
        private String debugRepathReason;

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
            this.lastPos = Vec3.ZERO;
            this.debugStage = "init";
            this.debugPointId = "<none>";
            this.lastFallbackTeleportUsed = false;
            this.debugTarget = Vec3.ZERO;
            this.debugNextWaypoint = Vec3.ZERO;
            this.debugRepathReason = "none";
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
