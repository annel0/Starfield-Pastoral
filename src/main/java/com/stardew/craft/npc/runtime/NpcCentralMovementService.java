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
    private static final int STUCK_REPATH_TICKS = 60;
    private static final int STUCK_FORCE_SYNC_TICKS = 100;
    private static final double PROGRESS_MOVE_DIST_SQR = 0.16D;
    private static final double PROGRESS_TARGET_GAIN_EPSILON_SQR = 0.04D;
    private static final int NO_PATH_FORCE_SYNC_TICKS = 20;
    private static final double NO_PATH_FORCE_SYNC_DIST_SQR = 8.0D * 8.0D;
    private static final int NO_PATH_FORCE_SYNC_MAX_TICKS = 120;
    private static final double NO_PATH_FORCE_SYNC_MAX_DIST_SQR = 18.0D * 18.0D;
    private static final int NO_PATH_FORCE_SYNC_FAR_TICKS = 80;
    private static final int TIME_JUMP_THRESHOLD = 200;

    private static final Map<String, NpcRoutePlan> ACTIVE_PLANS = new HashMap<>();
    private static final Map<String, Integer> LAST_CHECKPOINT = new HashMap<>();
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

            NpcRuntimeState state = runtimeStates.get(npcId);
            boolean pathingSuppressed = !profile.canRunPathing() || (state != null && state.pathingSuppressed());
            if (pathingSuppressed) {
                npc.setDeltaMovement(Vec3.ZERO);
                applyFacing(npc, state);
                DEBUG_SNAPSHOTS.put(npcId, new DebugSnapshot("pathing_disabled", "<none>", "<none>", 0, 0, false, npc.position(), npc.position(), "none", 0, "<none>"));
                continue;
            }

            NpcRoutePlanner.NpcRouteContext route = NpcRoutePlanner.resolveRoute(level, npcId, state);
            if (route == null) {
                npc.setDeltaMovement(Vec3.ZERO);
                applyFacing(npc, state);
                DEBUG_SNAPSHOTS.put(npcId, new DebugSnapshot("no_route", "<none>", "<none>", 0, 0, false, npc.position(), npc.position(), "none", 0, "<none>"));
                continue;
            }
            boolean nodeChanged = markAndCheckScheduleNodeChange(npcId, state);
            boolean timeJump = isTimeJump(npcId, state);
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
                NpcChunkForceManager.currentForcedTargetChunk(npcId)
            ));
        }

        NpcChunkForceManager.releaseInactiveForcedChunks(level, activeNpcIds);

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

    private static boolean executePlanTick(ServerLevel level,
                                           StardewNpcEntity npc,
                                           NpcRoutePlan plan,
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
        NpcRoutePlanner.NpcRouteStep step = plan.steps.get(plan.currentStepIndex);
        plan.debugPointId = step.pointId;
        plan.debugTarget = step.target;
        plan.debugNextWaypoint = step.target;

        if (step.mode == NpcRoutePlanner.RouteStepMode.WARP) {
            NpcChunkForceManager.ensureRouteTargetChunkForced(level, npc.getNpcId(), step.target);
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

        NpcChunkForceManager.ensureRouteTargetChunkForced(level, npc.getNpcId(), step.target);
        NpcChunkForceManager.ensureRouteCorridorChunksForced(level, npc.getNpcId(), npc.position(), step.target);
        long now = level.getGameTime();

        if ((plan.path.isEmpty() || plan.pathIndex >= plan.path.size())
            && now - plan.lastRepathTick >= REPATH_COOLDOWN_TICKS
            && now >= plan.noPathRetryBlockedUntilTick) {
            boolean attemptedAstar = false;
            if (NpcPathfinder.allowAstarCall(level)) {
                plan.path = NpcPathfinder.planPath(level, npc.position(), step.target);
                attemptedAstar = true;
            } else {
                plan.debugRepathReason = "astar_tick_budget";
            }
            plan.pathIndex = 0;
            plan.lastRepathTick = now;
            if (attemptedAstar) {
                plan.debugRepathReason = "path_init_or_exhausted";
            }
            if (plan.path.isEmpty()) {
                plan.debugStage = "no_path_repath";
                plan.consecutivePathFailures++;
                int cooldown = Math.min(NO_PATH_MAX_COOLDOWN_TICKS,
                    NO_PATH_BASE_COOLDOWN_TICKS * (1 << Math.min(plan.consecutivePathFailures - 1, 4)));
                plan.noPathRetryBlockedUntilTick = now + cooldown;
                plan.debugRepathReason = "no_path_backoff_" + cooldown + "t_fail" + plan.consecutivePathFailures;
            } else {
                plan.consecutivePathFailures = 0;
                plan.noPathRetryBlockedUntilTick = now;
            }
        }

        if (plan.path.isEmpty()) {
            plan.debugStage = "no_path";
            npc.setDeltaMovement(Vec3.ZERO);

            // Fast teleport: if pathfinding has failed N consecutive times, give up and teleport.
            if (plan.consecutivePathFailures >= NO_PATH_CONSECUTIVE_FAIL_TELEPORT) {
                plan.debugStage = "no_path_consecutive_fail_tp";
                plan.lastFallbackTeleportUsed = true;
                npc.setPos(step.target.x, step.target.y, step.target.z);
                plan.currentStepIndex++;
                plan.path.clear();
                plan.pathIndex = 0;
                plan.consecutivePathFailures = 0;
                plan.lastProgressTick = level.getGameTime();
                plan.lastPos = npc.position();
                plan.debugRepathReason = "consecutive_fail_teleport_" + NO_PATH_CONSECUTIVE_FAIL_TELEPORT;
                return false;
            }

            if (now - plan.lastProgressTick > STUCK_REPATH_TICKS
                && now >= plan.noPathRetryBlockedUntilTick) {
                if (NpcPathfinder.allowAstarCall(level)) {
                    plan.path = NpcPathfinder.planPath(level, npc.position(), step.target);
                } else {
                    plan.debugRepathReason = "astar_tick_budget";
                }
                plan.pathIndex = 0;
                plan.lastRepathTick = now;
                if (plan.path.isEmpty()) {
                    plan.consecutivePathFailures++;
                    int cooldown = Math.min(NO_PATH_MAX_COOLDOWN_TICKS,
                        NO_PATH_BASE_COOLDOWN_TICKS * (1 << Math.min(plan.consecutivePathFailures - 1, 4)));
                    plan.noPathRetryBlockedUntilTick = now + cooldown;
                    plan.debugRepathReason = "no_path_stuck_backoff_" + cooldown + "t_fail" + plan.consecutivePathFailures;
                } else {
                    plan.consecutivePathFailures = 0;
                    plan.noPathRetryBlockedUntilTick = now;
                    plan.debugRepathReason = "no_path_stuck_repath";
                }
            } else if (now < plan.noPathRetryBlockedUntilTick) {
                plan.debugRepathReason = "no_path_cooldown";
            }

            long noPathDuration = level.getGameTime() - plan.lastProgressTick;
            double toTarget = npc.position().distanceToSqr(step.target);
            boolean nearQuickFallback = noPathDuration > NO_PATH_NEAR_QUICK_SYNC_TICKS
                && toTarget <= NO_PATH_NEAR_QUICK_SYNC_DIST_SQR;
            boolean nearTarget = toTarget <= NO_PATH_FORCE_SYNC_MAX_DIST_SQR;
            boolean shortWindowFallback = noPathDuration > NO_PATH_FORCE_SYNC_TICKS && toTarget > NO_PATH_FORCE_SYNC_DIST_SQR;
            boolean maxWindowFallback = noPathDuration > NO_PATH_FORCE_SYNC_MAX_TICKS;
            boolean farWindowFallback = noPathDuration > NO_PATH_FORCE_SYNC_FAR_TICKS;
            if (nearQuickFallback
                || (nearTarget && (shortWindowFallback || maxWindowFallback))
                || (!nearTarget && farWindowFallback)) {
                plan.debugStage = "no_path_fallback_tp";
                plan.lastFallbackTeleportUsed = true;
                npc.setPos(step.target.x, step.target.y, step.target.z);
                plan.currentStepIndex++;
                plan.path.clear();
                plan.pathIndex = 0;
                plan.consecutivePathFailures = 0;
                plan.lastProgressTick = level.getGameTime();
                plan.lastPos = npc.position();
                if (nearQuickFallback) {
                    plan.debugRepathReason = "no_path_near_quick_sync";
                } else {
                    plan.debugRepathReason = nearTarget ? "no_path_near_fallback" : "no_path_far_timeout_fallback";
                }
            }
            return false;
        }

        Vec3 waypoint = plan.path.get(Math.min(plan.pathIndex, plan.path.size() - 1));
        plan.debugNextWaypoint = waypoint;
        tryOpenNearbyDoors(level, npc, waypoint, step.target);
        // Look ahead 2-3 path nodes to open doors before NPC arrives
        for (int ahead = 1; ahead <= 3 && plan.pathIndex + ahead < plan.path.size(); ahead++) {
            Vec3 futureWp = plan.path.get(plan.pathIndex + ahead);
            BlockPos futurePos = BlockPos.containing(futureWp);
            tryOpenDoorAt(level, npc, futurePos);
            tryOpenDoorAt(level, npc, futurePos.above());
        }
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

            if (shouldAxisLockNearOpenDoor(level, npc, waypoint)
                && Math.abs(vx) > 1.0E-4D
                && Math.abs(vz) > 1.0E-4D) {
                // In 1-block narrow doorways, diagonal motion rubs collision and causes stutter.
                // Force axis-aligned movement for stable door traversal.
                if (Math.abs(vx) >= Math.abs(vz)) {
                    vx = Math.signum(vx) * WALK_SPEED;
                    vz = 0.0D;
                } else {
                    vz = Math.signum(vz) * WALK_SPEED;
                    vx = 0.0D;
                }
                plan.debugRepathReason = "door_axis_lock";
            }

            // Path nodes use block-center Y, so flat-ground rise is often around 0.5.
            // Only jump when we actually need to climb a higher step.
            double verticalRise = waypoint.y - npc.getY();
            boolean stepUpNeeded = verticalRise > 1.05D;
            boolean needsToJump = stepUpNeeded || (npc.horizontalCollision && verticalRise > 0.55D);

            if (npc.horizontalCollision && !needsToJump
                && level.getGameTime() - plan.lastRepathTick > REPATH_COOLDOWN_TICKS) {
                // Register a collision penalty at current position so A* avoids it next time.
                NpcPathfinder.addCollisionPenalty(level, npc.position());
                if (NpcPathfinder.allowAstarCall(level)) {
                    plan.path = NpcPathfinder.planPath(level, npc.position(), step.target);
                }
                plan.pathIndex = 0;
                plan.lastRepathTick = level.getGameTime();
                plan.debugStage = "collision_repath";
                plan.debugRepathReason = NpcPathfinder.isBarrierAhead(level, npc.position(), vx, vz)
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
            && level.getGameTime() - plan.lastRepathTick > REPATH_COOLDOWN_TICKS
            && level.getGameTime() >= plan.noPathRetryBlockedUntilTick) {
            if (NpcPathfinder.allowAstarCall(level)) {
                plan.path = NpcPathfinder.planPath(level, npc.position(), step.target);
            }
            plan.pathIndex = 0;
            plan.lastRepathTick = level.getGameTime();
            plan.debugStage = "stuck_repath";
            if (plan.path.isEmpty()) {
                plan.consecutivePathFailures++;
                int cooldown = Math.min(NO_PATH_MAX_COOLDOWN_TICKS,
                    NO_PATH_BASE_COOLDOWN_TICKS * (1 << Math.min(plan.consecutivePathFailures - 1, 4)));
                plan.noPathRetryBlockedUntilTick = level.getGameTime() + cooldown;
                plan.debugRepathReason = "stuck_repath_backoff_" + cooldown + "t";
            } else {
                plan.consecutivePathFailures = 0;
                plan.debugRepathReason = "stuck_timeout_repath";
            }
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
            plan.consecutivePathFailures = 0;
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
}
