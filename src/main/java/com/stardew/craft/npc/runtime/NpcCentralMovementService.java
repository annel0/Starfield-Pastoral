package com.stardew.craft.npc.runtime;

import com.stardew.craft.entity.npc.StardewNpcEntity;
import com.stardew.craft.interior.InteriorRegionRegistry;
import com.stardew.craft.interior.InteriorSubspaceManager;
import com.stardew.craft.npc.data.NpcCapabilityProfile;
import com.stardew.craft.npc.data.NpcDataRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * NPC movement controller driven by schedule targets and data-defined route profiles.
 *
 * <h2>Architecture (Citizens2-inspired rewrite)</h2>
 * <p>All pathfinding is delegated to vanilla MC's {@code GroundPathNavigation}.
 * For each route step, we call {@code npc.getNavigation().moveTo(stepTarget)}
 * directly — <b>no intermediate waypoints</b>. Vanilla nav handles terrain,
 * elevation, doors, slabs, stairs, jumping, and entity-width collision natively.</p>
 *
 * <p>Key mechanisms:</p>
 * <ul>
 *   <li><b>Periodic re-path</b>: every {@code REPATH_INTERVAL_TICKS} ticks, re-issue
 *       {@code moveTo()} so the NPC always has a fresh path toward the target.</li>
 *   <li><b>Block-level stuck detection</b>: track NPC block position each tick.
 *       If stationary for too long, recalculate the current path.</li>
 *   <li><b>WARP steps</b>: instant teleport for indoor/outdoor transitions (unchanged).</li>
 * </ul>
 */
@SuppressWarnings("null")
public final class NpcCentralMovementService {
    /** Distance² threshold for considering a step target reached (2D horizontal). */
    private static final double STEP_REACH_SQR = 2.25D; // 1.5 blocks
    /** Periodic re-path interval (~1.0s). Citizens2 uses updatePathRate=20 by default. */
    private static final int REPATH_INTERVAL_TICKS = 20;
    /**
     * Displacement-based progress check interval (ticks).
     * Every N ticks, measure how far the NPC has actually moved.
     * If displacement is below threshold → stuck.
     */
    private static final int PROGRESS_CHECK_INTERVAL = 20;
    /**
    * Minimum displacement² over a check interval to count as "making progress".
    * About 0.2 blocks over 20 ticks. Below this means the NPC should recalculate.
     */
    private static final double PROGRESS_MIN_DISP_SQR = 0.04D;
    /** How many consecutive "no progress" checks before re-pathing. */
    private static final int STUCK_REPATH_CHECKS = 1;
    /** How many consecutive moveTo() failures before forcing an immediate recalculation. */
    private static final int NAV_FAIL_REPATH_THRESHOLD = 2;
    /** Door close timeout after an NPC opened the door. */
    private static final int DOOR_CLOSE_TIMEOUT_TICKS = 30;
    /** Vertical scan above preferred Y when correcting a bad route point. */
    private static final int SAFE_TARGET_SCAN_UP = 3;
    /** Vertical scan below preferred Y when correcting a bad route point. */
    private static final int SAFE_TARGET_SCAN_DOWN = 8;
    /** Horizontal radius to search inside interiors before giving up on a bad authored column. */
    private static final int INTERIOR_SAFE_TARGET_RADIUS = 2;
    /**
     * When the step target is beyond this distance, use an intermediate
     * moveTo target ~40 blocks in the direction of the real target.
     * Vanilla nav creates partial paths up to FOLLOW_RANGE anyway, but
     * giving it a closer target reduces wasted A* exploration.
     */
    private static final double INTERMEDIATE_TARGET_DIST = 40.0D;

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
            // Joja Mart NPCs 由 JojaNpcEvents 独立管理（骆驼商人同款），
            // 不能让本服务的路径规划 / teleport 干预它们。
            if (com.stardew.craft.joja.JojaNpcEvents.isJojaMartNpc(npcId)) continue;
            StardewNpcEntity npc = NpcSpawnManager.getTrackedNpc(level, npcId);
            if (npc == null) {
                continue;
            }

            // Dialogue screens can outlive the short turning animation. Keep the NPC
            // frozen for the full conversation, not just the initial face-player hold.
            if (NpcInteractionService.isDialogueMovementLocked(npcId) || npc.isFacingOverrideActive()) {
                npc.getNavigation().stop();
                stopHorizontalMotionPreserveGravity(npc);
                DEBUG_SNAPSHOTS.computeIfAbsent(npcId, k -> new DebugSnapshot()).update("interaction_pause", "<none>", "<none>", 0, 0, false, npc.position(), npc.position(), "none", 0, "<none>");
                continue;
            }

            NpcRuntimeState state = runtimeStates.get(npcId);
            boolean pathingSuppressed = !profile.canRunPathing() || (state != null && state.pathingSuppressed());
            if (pathingSuppressed) {
                npc.getNavigation().stop();
                stopHorizontalMotionPreserveGravity(npc);
                applyFacing(npc, state);
                DEBUG_SNAPSHOTS.computeIfAbsent(npcId, k -> new DebugSnapshot()).update("pathing_disabled", "<none>", "<none>", 0, 0, false, npc.position(), npc.position(), "none", 0, "<none>");
                continue;
            }

            NpcRoutePlanner.NpcRouteContext route = NpcRoutePlanner.resolveRoute(level, npcId, state);
            if (route == null || !route.ready()) {
                npc.getNavigation().stop();
                stopHorizontalMotionPreserveGravity(npc);
                applyFacing(npc, state);
                DebugSnapshot snapshot = DEBUG_SNAPSHOTS.computeIfAbsent(npcId, k -> new DebugSnapshot());
                snapshot.update(route == null ? "no_route" : route.status.name().toLowerCase(java.util.Locale.ROOT),
                    route == null ? "<none>" : route.canonicalLocation,
                    route == null ? "<none>" : route.missingPointId,
                    0,
                    0,
                    false,
                    npc.position(),
                    npc.position(),
                    route == null ? "none" : route.diagnosticReason,
                    0,
                    "<none>");
                snapshot.updateRoute(route);
                continue;
            }
            boolean nodeChanged = markAndCheckScheduleNodeChange(npcId, state);
            String signature = buildPlanSignature(state, route);

            NpcRoutePlan plan = ACTIVE_PLANS.get(npcId);
            boolean entityReplaced = plan != null && !npc.getUUID().equals(plan.boundEntityUuid);
            boolean needNewPlan = plan == null || !signature.equals(plan.signature);
            // When the entity UUID changed (e.g. NPC respawned) but the plan
            // signature is identical, just rebind instead of rebuilding the
            // entire plan. This avoids the massive re-plan spam when the spawn
            // manager replaces entities.
            if (entityReplaced && !needNewPlan) {
                plan.boundEntityUuid = npc.getUUID();
            }
            if (needNewPlan || nodeChanged) {
                plan = buildPlan(level, npc, route, signature, level.getGameTime());
                ACTIVE_PLANS.put(npcId, plan);
            }

            if (plan == null || plan.steps.isEmpty()) {
                npc.getNavigation().stop();
                stopHorizontalMotionPreserveGravity(npc);
                applyFacing(npc, state);
                DebugSnapshot snapshot = DEBUG_SNAPSHOTS.computeIfAbsent(npcId, k -> new DebugSnapshot());
                snapshot.update("empty_plan", route.canonicalLocation, plan == null ? "<none>" : plan.missingPointId, 0, 0, false, npc.position(), npc.position(), plan == null ? "none" : plan.routeDiagnosticReason, 0, "<none>");
                snapshot.updateRoute(route);
                if (plan != null) {
                    snapshot.updatePlan(plan);
                }
                continue;
            }

            executePlanTick(level, npc, plan);
            if ("done".equals(plan.debugStage)) {
                applyFacing(npc, state);
            }
            DebugSnapshot snapshot = DEBUG_SNAPSHOTS.computeIfAbsent(npcId, k -> new DebugSnapshot());
            snapshot.update(
                plan.debugStage,
                route.canonicalLocation,
                plan.debugPointId,
                plan.steps.size(),
                plan.currentStepIndex,
                plan.lastForcedTeleportUsed,
                plan.debugTarget,
                plan.debugNextWaypoint,
                plan.debugRepathReason,
                level.getGameTime() - plan.lastProgressTick,
                NpcChunkForceManager.currentForcedTargetChunk(npcId)
            );
            snapshot.updateRoute(route);
            snapshot.updatePlan(plan);
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

        String npcFixedInterior = InteriorRegionRegistry.fixedInteriorIdAt(npc.blockPosition());
        boolean npcInFixedInterior = !npcFixedInterior.isBlank();
        boolean npcIndoors = npcInFixedInterior || InteriorSubspaceManager.isInteriorRegion(level, npc.blockPosition());
        Vec3 finalTarget = route.destinationSteps.get(route.destinationSteps.size() - 1).target;
        String destFixedInterior = InteriorRegionRegistry.fixedInteriorIdAt(BlockPos.containing(finalTarget));
        boolean destInFixedInterior = !destFixedInterior.isBlank();
        boolean destIndoors = destInFixedInterior || InteriorSubspaceManager.isInteriorRegion(level, BlockPos.containing(finalTarget));

        String routeStatus = route.status.name();
        String routeDiagnosticReason = route.diagnosticReason;
        String missingPointId = route.missingPointId;
        String missingPortalLinkId = route.missingPortalLinkId;

        if (npcIndoors && destIndoors) {
            boolean sameFixedInterior = npcInFixedInterior && destInFixedInterior && npcFixedInterior.equals(destFixedInterior);
            if (sameFixedInterior) {
                expanded.add(NpcRoutePlanner.NpcRouteStep.walk("indoor_direct", finalTarget));
            } else {
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
                    routeStatus = NpcRoutePlanner.RouteStatus.WAITING_FOR_COORDINATES.name();
                    routeDiagnosticReason = exitIndoor == null ? "missing_indoor_exit_walk_target" : "missing_outdoor_exit_landing";
                    missingPortalLinkId = npcFixedInterior.isBlank() ? "<unknown>" : npcFixedInterior;
                }
            }
        } else if (npcIndoors) {
            Vec3 exitIndoor = NpcRoutePlanner.nearestKnownIndoorEntry(npc.position());
            Vec3 exitOutdoor = NpcRoutePlanner.linkedOutdoorDoor(exitIndoor);
            if (exitIndoor != null && exitOutdoor != null) {
                expanded.add(NpcRoutePlanner.NpcRouteStep.walk("indoor_exit", exitIndoor));
                expanded.add(NpcRoutePlanner.NpcRouteStep.warp("outdoor_door", exitOutdoor));
                for (NpcRoutePlanner.NpcRouteStep ds : route.destinationSteps) {
                    if (ds.mode == NpcRoutePlanner.RouteStepMode.WALK
                        && ds.target.distanceToSqr(exitOutdoor) < 4.0D) {
                        continue;
                    }
                    expanded.add(ds);
                }
            } else {
                routeStatus = NpcRoutePlanner.RouteStatus.WAITING_FOR_COORDINATES.name();
                routeDiagnosticReason = exitIndoor == null ? "missing_indoor_exit_walk_target" : "missing_outdoor_exit_landing";
                missingPortalLinkId = npcFixedInterior.isBlank() ? "<unknown>" : npcFixedInterior;
            }
        } else {
            expanded.addAll(route.destinationSteps);
        }
        NpcRoutePlan plan = new NpcRoutePlan(signature, npc.getUUID(), expanded, gameTime);
        plan.routeStatus = routeStatus;
        plan.routeDiagnosticReason = routeDiagnosticReason;
        plan.missingPointId = missingPointId;
        plan.missingPortalLinkId = missingPortalLinkId;

        // ── Diagnostic: log plan steps once per new plan ──
        StringBuilder sb = new StringBuilder();
        sb.append("[NPC_PLAN] npc=").append(npc.getNpcId())
          .append(" indoor=").append(npcIndoors)
          .append(" destIndoor=").append(destIndoors)
          .append(" fixedInterior=").append(npcFixedInterior.isBlank() ? "<none>" : npcFixedInterior)
          .append(" destFixedInterior=").append(destFixedInterior.isBlank() ? "<none>" : destFixedInterior)
          .append(" pos=").append(fmt(npc.position()))
          .append(" steps=[");
        for (int i = 0; i < expanded.size(); i++) {
            NpcRoutePlanner.NpcRouteStep s = expanded.get(i);
            if (i > 0) sb.append(", ");
            sb.append(s.mode).append(':').append(s.pointId).append('@').append(fmt(s.target));
        }
        sb.append(']');

        return plan;
    }

    private static String fmt(Vec3 v) {
        return v == null ? "null" : String.format(java.util.Locale.ROOT, "(%.1f,%.1f,%.1f)", v.x, v.y, v.z);
    }

    // ──── Plan execution helpers ────

    /** Teleport NPC to the current step target and advance to the next step. */
    private static void advanceStepByTeleport(ServerLevel level, StardewNpcEntity npc, NpcRoutePlan plan, Vec3 target, long now) {
        npc.getNavigation().stop();
        stopHorizontalMotionPreserveGravity(npc);
        npc.setPos(target.x, target.y, target.z);
        snapToSurface(level, npc);
        plan.currentStepIndex++;
        plan.consecutiveNavFailures = 0;
        plan.lastProgressTick = now;
        plan.stuckCheckCount = 0;
        plan.progressCheckTick = now;
        plan.progressCheckX = target.x;
        plan.progressCheckZ = target.z;
        plan.lastRepathTick = now;
    }


    private static boolean executePlanTick(ServerLevel level,
                                           StardewNpcEntity npc,
                                           NpcRoutePlan plan) {
        if (plan.currentStepIndex >= plan.steps.size()) {
            closeOpenedDoors(level, npc, plan, level.getGameTime(), true);
            npc.getNavigation().stop();
            stopHorizontalMotionPreserveGravity(npc);
            plan.debugStage = "done";
            return false;
        }

        plan.lastForcedTeleportUsed = false;
        plan.debugRepathReason = "none";
        NpcRoutePlanner.NpcRouteStep step = plan.steps.get(plan.currentStepIndex);
        Vec3 safeTarget = resolveSafeStepTarget(level, step.target);
        plan.debugPointId = step.pointId;
        plan.debugTarget = safeTarget;
        plan.debugNextWaypoint = safeTarget;
        long now = level.getGameTime();

        // ── WARP steps: instant teleport ──
        if (step.mode == NpcRoutePlanner.RouteStepMode.WARP) {
            NpcChunkForceManager.ensureRouteTargetChunkForced(level, npc.getNpcId(), safeTarget);
            plan.debugStage = "warp";
            advanceStepByTeleport(level, npc, plan, safeTarget, now);
            return false;
        }

        // ── WALK steps: delegate to vanilla GroundPathNavigation ──
        NpcChunkForceManager.ensureRouteTargetChunkForced(level, npc.getNpcId(), safeTarget);
        NpcChunkForceManager.ensureRouteCorridorChunksForced(level, npc.getNpcId(), npc.position(), safeTarget);

        // Open doors near NPC and toward target
        long npcBlockKey = npc.blockPosition().asLong();
        if (npcBlockKey != plan.lastDoorCheckBlockKey) {
            plan.lastDoorCheckBlockKey = npcBlockKey;
            tryOpenNearbyDoors(level, npc, plan, safeTarget, safeTarget);
        }
        closeOpenedDoors(level, npc, plan, now, false);

        // Check horizontal arrival at step target
        Vec3 toTarget = new Vec3(safeTarget.x - npc.getX(), 0.0D, safeTarget.z - npc.getZ());
        double distSqr = toTarget.lengthSqr();
        if (distSqr <= STEP_REACH_SQR) {
            // Arrived at step target — advance
            npc.getNavigation().stop();
            stopHorizontalMotionPreserveGravity(npc);
            plan.currentStepIndex++;
            plan.consecutiveNavFailures = 0;
            plan.lastProgressTick = now;
            plan.stuckCheckCount = 0;
            plan.progressCheckTick = now;
            plan.progressCheckX = npc.getX();
            plan.progressCheckZ = npc.getZ();
            plan.lastRepathTick = now;
            plan.debugStage = "step_reached";
            return false;
        }

        plan.debugStage = "walk";

        // Compute effective moveTo target.
        // For far-away targets, give vanilla nav a closer intermediate point
        // to reduce wasted A* exploration and improve partial path quality.
        double dist = Math.sqrt(distSqr);
        double moveToX, moveToZ;
        if (dist > INTERMEDIATE_TARGET_DIST) {
            double ratio = INTERMEDIATE_TARGET_DIST / dist;
            moveToX = npc.getX() + (safeTarget.x - npc.getX()) * ratio;
            moveToZ = npc.getZ() + (safeTarget.z - npc.getZ()) * ratio;
            plan.debugRepathReason = "intermediate_" + (int) dist + "m";
        } else {
            moveToX = safeTarget.x;
            moveToZ = safeTarget.z;
        }
        Vec3 moveToTarget = dist > INTERMEDIATE_TARGET_DIST
            ? resolveSafeStepTarget(level, new Vec3(moveToX, npc.getY(), moveToZ))
            : safeTarget;

        // Issue or re-issue moveTo at periodic intervals
        boolean navIdle = npc.getNavigation().isDone();
        boolean repathDue = (now - plan.lastRepathTick >= REPATH_INTERVAL_TICKS);
        if (navIdle || repathDue) {
            // moveTo() returns true if a path was successfully created
            boolean pathFound = npc.getNavigation().moveTo(moveToTarget.x, moveToTarget.y, moveToTarget.z, 1.0D);
            plan.lastRepathTick = now;

            if (!pathFound) {
                plan.consecutiveNavFailures++;
                plan.debugRepathReason = "moveTo_fail_" + plan.consecutiveNavFailures;
            } else {
                plan.consecutiveNavFailures = 0;
                if (plan.debugRepathReason.equals("none")) {
                    plan.debugRepathReason = navIdle ? "nav_idle_repath" : "periodic_repath";
                }
            }
        }

        // Update facing from movement velocity
        Vec3 vel = npc.getDeltaMovement();
        if (vel.x != 0.0D || vel.z != 0.0D) {
            float moveYaw = (float) (Math.toDegrees(Math.atan2(-vel.x, vel.z)));
            npc.setYRot(moveYaw);
            npc.setYHeadRot(moveYaw);
        }

        // ── Displacement-based progress detection ──
        // Every PROGRESS_CHECK_INTERVAL ticks, measure how far the NPC actually
        // moved (2D). This catches BOTH stationary AND oscillating-between-blocks
        // stuck patterns that block-level detection misses.
        if (now - plan.progressCheckTick >= PROGRESS_CHECK_INTERVAL) {
            double dx = npc.getX() - plan.progressCheckX;
            double dz = npc.getZ() - plan.progressCheckZ;
            double dispSqr = dx * dx + dz * dz;

            if (dispSqr < PROGRESS_MIN_DISP_SQR) {
                // No meaningful progress in this check window
                plan.stuckCheckCount++;
            } else {
                // Making real progress — reset
                plan.stuckCheckCount = 0;
                plan.lastProgressTick = now;
            }
            // Update checkpoint for next measurement
            plan.progressCheckTick = now;
            plan.progressCheckX = npc.getX();
            plan.progressCheckZ = npc.getZ();
        }

        if (plan.stuckCheckCount >= STUCK_REPATH_CHECKS
            || plan.consecutiveNavFailures >= NAV_FAIL_REPATH_THRESHOLD
            || npc.getNavigation().isStuck()) {
            npc.getNavigation().stop();
            npc.getNavigation().moveTo(safeTarget.x, safeTarget.y, safeTarget.z, 1.0D);
            plan.lastRepathTick = now;
            plan.debugStage = "stuck_repath";
            plan.debugRepathReason = npc.getNavigation().isStuck()
                ? "navigation_stuck"
                : plan.consecutiveNavFailures >= NAV_FAIL_REPATH_THRESHOLD
                    ? "nav_fail_repath_" + plan.consecutiveNavFailures
                    : "stuck_repath_check" + plan.stuckCheckCount;
            plan.stuckCheckCount = 0;
        }

        return false;
    }

    private static void stopHorizontalMotionPreserveGravity(StardewNpcEntity npc) {
        Vec3 movement = npc.getDeltaMovement();
        npc.setDeltaMovement(0.0D, movement.y, 0.0D);
    }

    private static String buildPlanSignature(NpcRuntimeState state, NpcRoutePlanner.NpcRouteContext route) {
        return state.activeScheduleKey() + "#" + state.scheduleCheckpoint() + "#" + state.scheduleNodeIndex()
            + "#" + route.canonicalLocation + "#" + route.status + "#" + route.diagnosticReason + "#" + route.missingPointId + "#" + route.missingPortalLinkId;
    }

    @SuppressWarnings("unused")
    private static boolean shouldAxisLockNearOpenDoor(ServerLevel level,
                                                      StardewNpcEntity npc,
                                                      Vec3 target) {
        if (level == null || npc == null || target == null) {
            return false;
        }
        BlockPos npcPos = npc.blockPosition();
        BlockPos targetPos = BlockPos.containing(target);
        if (!isOpenDoorNearby(level, npcPos) && !isOpenDoorNearby(level, targetPos)) {
            return false;
        }
        return npc.position().distanceToSqr(target) <= 4.0D;
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
                                           NpcRoutePlan plan,
                                           Vec3 waypoint,
                                           Vec3 target) {
        if (level == null || npc == null) {
            return;
        }
        BlockPos npcPos = npc.blockPosition();
        tryOpenDoorAt(level, npc, plan, npcPos);
        tryOpenDoorAt(level, npc, plan, npcPos.above());

        if (waypoint != null) {
            BlockPos wayPos = BlockPos.containing(waypoint);
            tryOpenDoorAt(level, npc, plan, wayPos);
            tryOpenDoorAt(level, npc, plan, wayPos.above());
        }

        if (target != null) {
            BlockPos targetPos = BlockPos.containing(target);
            tryOpenDoorAt(level, npc, plan, targetPos);
            tryOpenDoorAt(level, npc, plan, targetPos.above());
        }
    }

    private static void tryOpenDoorAt(ServerLevel level, StardewNpcEntity npc, NpcRoutePlan plan, BlockPos probePos) {
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
        plan.openedDoors.put(lowerPos.immutable(), level.getGameTime());
    }

    private static void closeOpenedDoors(ServerLevel level, StardewNpcEntity npc, NpcRoutePlan plan, long now, boolean force) {
        if (plan.openedDoors.isEmpty()) {
            return;
        }
        List<BlockPos> closed = new ArrayList<>();
        for (Map.Entry<BlockPos, Long> entry : plan.openedDoors.entrySet()) {
            BlockPos pos = entry.getKey();
            if (!force && now - entry.getValue() < DOOR_CLOSE_TIMEOUT_TICKS && npc.blockPosition().distSqr(pos) <= 4.0D) {
                continue;
            }
            BlockState state = level.getBlockState(pos);
            if (!(state.getBlock() instanceof DoorBlock door) || !state.hasProperty(DoorBlock.OPEN) || !state.getValue(DoorBlock.OPEN)) {
                closed.add(pos);
                continue;
            }
            if (isDoorwayOccupied(level, pos)) {
                continue;
            }
            door.setOpen(npc, level, state, pos, false);
            closed.add(pos);
        }
        for (BlockPos pos : closed) {
            plan.openedDoors.remove(pos);
        }
    }

    private static boolean isDoorwayOccupied(ServerLevel level, BlockPos pos) {
        AABB doorway = new AABB(pos).inflate(0.2D, 0.0D, 0.2D).expandTowards(0.0D, 1.0D, 0.0D);
        return !level.getEntitiesOfClass(LivingEntity.class, doorway, LivingEntity::isAlive).isEmpty();
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
        private UUID boundEntityUuid;
        private final List<NpcRoutePlanner.NpcRouteStep> steps;
        private int currentStepIndex;
        private long lastProgressTick;
        private long lastRepathTick;
        private int consecutiveNavFailures;
        private String debugStage;
        private String debugPointId;
        private boolean lastForcedTeleportUsed;
        private Vec3 debugTarget;
        private Vec3 debugNextWaypoint;
        private String debugRepathReason;
        private String routeStatus;
        private String routeDiagnosticReason;
        private String missingPointId;
        private String missingPortalLinkId;
        /** Displacement-based progress detection: consecutive "no progress" checks. */
        private int stuckCheckCount;
        /** Tick when last progress check was performed. */
        private long progressCheckTick;
        /** X/Z position at last progress checkpoint. */
        private double progressCheckX, progressCheckZ;
        /** Block key where door detection last ran; skip when unchanged. */
        private long lastDoorCheckBlockKey = Long.MIN_VALUE;
        private final Map<BlockPos, Long> openedDoors = new HashMap<>();

        private NpcRoutePlan(String signature, UUID boundEntityUuid, List<NpcRoutePlanner.NpcRouteStep> steps, long now) {
            this.signature = signature;
            this.boundEntityUuid = boundEntityUuid;
            this.steps = steps;
            this.currentStepIndex = 0;
            this.lastProgressTick = now;
            this.lastRepathTick = now;
            this.consecutiveNavFailures = 0;
            this.debugStage = "init";
            this.debugPointId = "<none>";
            this.lastForcedTeleportUsed = false;
            this.debugTarget = Vec3.ZERO;
            this.debugNextWaypoint = Vec3.ZERO;
            this.debugRepathReason = "none";
            this.routeStatus = NpcRoutePlanner.RouteStatus.READY.name();
            this.routeDiagnosticReason = "none";
            this.missingPointId = "";
            this.missingPortalLinkId = "";
            this.stuckCheckCount = 0;
            this.progressCheckTick = now;
            this.progressCheckX = 0.0D;
            this.progressCheckZ = 0.0D;
        }
    }

    public static final class DebugSnapshot {
        public String stage;
        public String location;
        public String pointId;
        public int pathSize;
        public int pathIndex;
        public boolean forcedTeleportUsed;
        public Vec3 target;
        public Vec3 nextWaypoint;
        public String repathReason;
        public long noPathTicks;
        public String forcedTargetChunk;
        public String routeStatus;
        public String routeDiagnosticReason;
        public String missingPointId;
        public String missingPortalLinkId;

        public DebugSnapshot() {}

        public void update(String stage, String location, String pointId,
                           int pathSize, int pathIndex, boolean forcedTeleportUsed,
                           Vec3 target, Vec3 nextWaypoint, String repathReason,
                           long noPathTicks, String forcedTargetChunk) {
            this.stage = stage;
            this.location = location;
            this.pointId = pointId;
            this.pathSize = pathSize;
            this.pathIndex = pathIndex;
            this.forcedTeleportUsed = forcedTeleportUsed;
            this.target = target;
            this.nextWaypoint = nextWaypoint;
            this.repathReason = repathReason;
            this.noPathTicks = noPathTicks;
            this.forcedTargetChunk = forcedTargetChunk;
            this.routeStatus = NpcRoutePlanner.RouteStatus.READY.name();
            this.routeDiagnosticReason = "none";
            this.missingPointId = "";
            this.missingPortalLinkId = "";
        }

        public void updateRoute(NpcRoutePlanner.NpcRouteContext route) {
            if (route == null) {
                this.routeStatus = "NO_ROUTE";
                this.routeDiagnosticReason = "route_context_missing";
                this.missingPointId = "";
                this.missingPortalLinkId = "";
                return;
            }
            this.routeStatus = route.status.name();
            this.routeDiagnosticReason = route.diagnosticReason;
            this.missingPointId = route.missingPointId;
            this.missingPortalLinkId = route.missingPortalLinkId;
        }

        private void updatePlan(NpcRoutePlan plan) {
            if (plan == null || NpcRoutePlanner.RouteStatus.READY.name().equals(plan.routeStatus)) {
                return;
            }
            this.routeStatus = plan.routeStatus;
            this.routeDiagnosticReason = plan.routeDiagnosticReason;
            this.missingPointId = plan.missingPointId;
            this.missingPortalLinkId = plan.missingPortalLinkId;
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

    private static Vec3 resolveSafeStepTarget(ServerLevel level, Vec3 rawTarget) {
        if (level == null || rawTarget == null) {
            return rawTarget;
        }

        int baseX = Mth.floor(rawTarget.x);
        int baseZ = Mth.floor(rawTarget.z);
        if (level.getChunkSource().getChunkNow(baseX >> 4, baseZ >> 4) == null) {
            return rawTarget;
        }

        boolean interiorTarget = InteriorRegionRegistry.isInFixedInterior(BlockPos.containing(rawTarget))
            || InteriorSubspaceManager.isInteriorRegion(level, BlockPos.containing(rawTarget));

        BlockPos safeStand = resolveColumnSafeStand(level, baseX, baseZ, Mth.floor(rawTarget.y), !interiorTarget, interiorTarget);
        if (safeStand == null && interiorTarget) {
            safeStand = resolveNearbyInteriorStand(level, baseX, baseZ, Mth.floor(rawTarget.y));
        }
        if (safeStand == null) {
            return rawTarget;
        }

        return new Vec3(rawTarget.x, safeStand.getY(), rawTarget.z);
    }

    private static BlockPos resolveColumnSafeStand(ServerLevel level,
                                                   int x,
                                                   int z,
                                                   int preferredY,
                                                   boolean allowSurfaceCorrection,
                                                   boolean interiorTarget) {
        if (level.getChunkSource().getChunkNow(x >> 4, z >> 4) == null) {
            return null;
        }

        for (int y = preferredY + SAFE_TARGET_SCAN_UP; y >= preferredY - SAFE_TARGET_SCAN_DOWN; y--) {
            BlockPos candidate = new BlockPos(x, y, z);
            if (isSafeStandPosition(level, candidate, interiorTarget)) {
                return candidate;
            }
        }

        if (!allowSurfaceCorrection) {
            return null;
        }

        int surfaceY = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
        BlockPos surface = new BlockPos(x, surfaceY, z);
        if (isSafeStandPosition(level, surface, false)) {
            return surface;
        }
        return null;
    }

    private static BlockPos resolveNearbyInteriorStand(ServerLevel level, int x, int z, int preferredY) {
        for (int radius = 1; radius <= INTERIOR_SAFE_TARGET_RADIUS; radius++) {
            for (int dx = -radius; dx <= radius; dx++) {
                BlockPos north = resolveColumnSafeStand(level, x + dx, z - radius, preferredY, false, true);
                if (north != null) {
                    return north;
                }
                BlockPos south = resolveColumnSafeStand(level, x + dx, z + radius, preferredY, false, true);
                if (south != null) {
                    return south;
                }
            }
            for (int dz = -radius + 1; dz <= radius - 1; dz++) {
                BlockPos west = resolveColumnSafeStand(level, x - radius, z + dz, preferredY, false, true);
                if (west != null) {
                    return west;
                }
                BlockPos east = resolveColumnSafeStand(level, x + radius, z + dz, preferredY, false, true);
                if (east != null) {
                    return east;
                }
            }
        }
        return null;
    }

    private static boolean isSafeStandPosition(ServerLevel level, BlockPos pos, boolean interiorTarget) {
        if (interiorTarget) {
            return isSafeInteriorStandPosition(level, pos);
        }
        return NpcPathfinder.canStand(level, pos)
            && level.getFluidState(pos).isEmpty()
            && level.getFluidState(pos.above()).isEmpty();
    }

    private static boolean isSafeInteriorStandPosition(ServerLevel level, BlockPos pos) {
        BlockPos abovePos = pos.above();
        BlockPos belowPos = pos.below();
        BlockState feet = level.getBlockState(pos);
        BlockState head = level.getBlockState(abovePos);
        BlockState below = level.getBlockState(belowPos);

        if (!feet.getCollisionShape(level, pos).isEmpty() && !NpcPathfinder.isPathDoorPassable(feet)) {
            return false;
        }
        if (!head.getCollisionShape(level, abovePos).isEmpty() && !NpcPathfinder.isPathDoorPassable(head)) {
            return false;
        }
        if (NpcPathfinder.isBarrierBlock(below)) {
            return false;
        }
        if (below.getCollisionShape(level, belowPos).isEmpty()) {
            return false;
        }
        return level.getFluidState(pos).isEmpty()
            && level.getFluidState(abovePos).isEmpty();
    }
}
