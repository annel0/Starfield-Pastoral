package com.stardew.craft.festival;

import com.stardew.craft.entity.npc.StardewNpcEntity;
import com.stardew.craft.npc.runtime.NpcCentralMovementService;
import com.stardew.craft.npc.runtime.NpcInteractionService;
import com.stardew.craft.npc.runtime.NpcSpawnManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public final class EggFestivalNpcService {
    private static final String OVERLAY_ID = "Town-EggFestival";
    private static final String MOVEMENT_OWNER = "egg_festival";
    private static final String ACTOR_TAG = "stardewcraft_egg_festival_actor";
    private static final int STATIC_VERIFY_TICKS = 100;
    private static final int SPAWN_RETRY_TICKS = 100;
    private static final int SHUTTLE_WAIT_TICKS = 40;
    private static final int HUNT_WANDER_MIN_TICKS = 30;
    private static final int HUNT_WANDER_RANDOM_TICKS = 60;
    private static final double HUNT_TARGET_REACHED_SQR = 2.25D;

    private static final Map<String, ActorDefinition> ACTORS = createActors();
    private static final Set<String> ACTOR_IDS = Set.copyOf(ACTORS.keySet());
    private static final List<String> MAIN_EVENT_CONTESTANT_IDS = List.of(
        "abigail", "maru", "jas", "sam", "vincent"
    );
    private static final Set<String> HUNT_CONTESTANT_IDS = Set.of(
        "abigail", "maru", "jas", "sam", "vincent", "leo"
    );
    private static final Set<String> HUNT_STAGE_ACTOR_IDS = Set.of(
        "abigail", "maru", "jas", "sam", "vincent", "leo", "lewis"
    );
    private static final Map<String, ActorRuntime> RUNTIME = new LinkedHashMap<>();
    private static boolean actorsActive;
    private static boolean debugRequested;
    private static boolean huntStageActive;
    private static boolean mainEventStageActive;

    private EggFestivalNpcService() {
    }

    public static void tick(ServerLevel level, boolean activeRequested) {
        if (level == null) {
            return;
        }
        boolean debugActive = debugRequested && FestivalMapOverlayManager.isApplied(level, OVERLAY_ID);
        boolean shouldRun = activeRequested || debugActive;
        if (!shouldRun) {
            if (actorsActive) {
                restore(level);
            }
            return;
        }

        actorsActive = true;
        long now = level.getGameTime();
        for (ActorDefinition definition : ACTORS.values()) {
            tickActor(level, definition, now);
        }
    }

    public static void requestDebugStart(ServerLevel level) {
        debugRequested = true;
        huntStageActive = false;
        mainEventStageActive = false;
        actorsActive = false;
        RUNTIME.clear();
        for (String npcId : ACTORS.keySet()) {
            NpcSpawnManager.resumeNpcSpawn(npcId);
            NpcSpawnManager.forceSpawnNpc(npcId);
        }
        if (level != null) {
            NpcSpawnManager.tick(level);
        }
        if (level != null && FestivalMapOverlayManager.isApplied(level, OVERLAY_ID)) {
            tick(level, true);
        }
    }

    public static void restore(ServerLevel level) {
        if (!actorsActive && !debugRequested && RUNTIME.isEmpty()) {
            huntStageActive = false;
            mainEventStageActive = false;
            return;
        }
        debugRequested = false;
        huntStageActive = false;
        mainEventStageActive = false;
        actorsActive = false;
        for (String npcId : ACTORS.keySet()) {
            NpcSpawnManager.resumeNpcSpawn(npcId);
        }
        if (level != null) {
            NpcSpawnManager.tick(level);
        }
        for (String npcId : ACTORS.keySet()) {
            StardewNpcEntity npc = NpcSpawnManager.getTrackedNpc(level, npcId);
            if (npc == null) {
                continue;
            }
            npc.setInvisible(false);
            npc.removeTag(ACTOR_TAG);
            npc.getNavigation().stop();
            npc.setNoAi(false);
            npc.setInvulnerable(true);
            npc.setDeltaMovement(Vec3.ZERO);
            npc.hasImpulse = false;
            runtimeClear(npcId);
            NpcSpawnManager.snapNpcToCurrentSchedule(level, npcId);
            NpcCentralMovementService.resetMovementPlan(npcId);
            NpcCentralMovementService.resetAuthoredMovementPlan(npcId, MOVEMENT_OWNER);
        }
        RUNTIME.clear();
    }

    public static boolean controlsNpc(String npcId) {
        return actorsActive && ACTOR_IDS.contains(canonical(npcId));
    }

    public static int mainEventContestantCount() {
        return MAIN_EVENT_CONTESTANT_IDS.size();
    }

    public static void setHuntStageActive(boolean active) {
        huntStageActive = active;
        if (!active) {
            for (ActorRuntime runtime : RUNTIME.values()) {
                runtime.clearHuntTarget();
            }
        }
    }

    public static void startMainEventStage(ServerLevel level) {
        mainEventStageActive = true;
        huntStageActive = false;
        prepareMainEventActors(level);
    }

    public static void startAwardStage(ServerLevel level) {
        mainEventStageActive = true;
        huntStageActive = false;
        prepareMainEventActors(level);
    }

    public static void startHuntStage(ServerLevel level) {
        mainEventStageActive = true;
        huntStageActive = true;
        if (level == null) {
            return;
        }
        for (ActorDefinition definition : ACTORS.values()) {
            ActorRuntime runtime = RUNTIME.computeIfAbsent(definition.npcId(), id -> new ActorRuntime());
            runtime.clearHuntTarget();
            NpcCentralMovementService.resetAuthoredMovementPlan(definition.npcId(), MOVEMENT_OWNER);
            if (!HUNT_STAGE_ACTOR_IDS.contains(definition.npcId())) {
                despawnForHuntStage(level, definition.npcId(), runtime);
                continue;
            }
            NpcSpawnManager.resumeNpcSpawn(definition.npcId());
            StardewNpcEntity npc = NpcSpawnManager.getTrackedNpc(level, definition.npcId());
            if (npc == null) {
                NpcSpawnManager.forceSpawnNpc(definition.npcId());
                NpcSpawnManager.tick(level);
                npc = NpcSpawnManager.getTrackedNpc(level, definition.npcId());
            }
            if (npc != null) {
                Waypoint point = mainEventPoint(definition.npcId());
                if (point != null) {
                    if (!EggFestivalService.isInsideFestivalBounds(npc.position())) {
                        placeAt(level, npc, point);
                    } else {
                        applyYaw(npc, point.yaw());
                    }
                } else {
                    placeAt(level, npc, definition.points().get(0));
                }
                runtime.boundEntityId = npc.getId();
            }
        }
    }

    public static String resolveDialogueKey(ServerPlayer player, String npcId) {
        if (player == null || npcId == null || npcId.isBlank()) {
            return null;
        }
        if (!actorsActive && !EggFestivalService.isParticipant(player)) {
            return null;
        }
        String canonicalId = canonical(npcId);
        if (!ACTOR_IDS.contains(canonicalId)) {
            return null;
        }
        return FestivalDialogueService.resolveDialogueKey(EggFestivalService.FESTIVAL_ID, canonicalId);
    }

    public static String debugStatus() {
        return "Egg Festival NPC actors: " + (actorsActive ? "ACTIVE" : "INACTIVE")
            + ", debugRequested=" + debugRequested
            + ", tracked=" + RUNTIME.size() + "/" + ACTORS.size();
    }

    public static String debugStatus(ServerLevel level) {
        StringBuilder message = new StringBuilder(debugStatus());
        if (level == null) {
            return message.toString();
        }
        for (ActorDefinition definition : ACTORS.values()) {
            ActorRuntime runtime = RUNTIME.get(definition.npcId());
            StardewNpcEntity npc = NpcSpawnManager.getTrackedNpc(level, definition.npcId());
            NpcCentralMovementService.AuthoredDebugSnapshot movement = NpcCentralMovementService.getAuthoredDebugSnapshot(definition.npcId(), MOVEMENT_OWNER);
            message.append('\n').append(definition.npcId())
                .append(" pos=").append(npc == null ? "<missing>" : fmt(npc.position()))
                .append(" route=").append(definition.routeTargets().size())
                .append(" bound=").append(runtime == null ? -1 : runtime.boundEntityId)
                .append(" wait=").append(runtime == null ? 0 : Math.max(0L, runtime.waitUntilTick - level.getGameTime()));
            if (movement != null) {
                message.append(" stage=").append(movement.stage)
                    .append(" step=").append(movement.pathIndex).append('/').append(movement.pathSize)
                    .append(" point=").append(movement.pointId)
                    .append(" target=").append(fmt(movement.target))
                    .append(" hasPath=").append(movement.hasPath)
                    .append(" navDone=").append(movement.navDone)
                    .append(" navTarget=").append(movement.navTarget)
                    .append(" reason=").append(movement.repathReason)
                    .append(" failures=").append(movement.navFailures);
            }
        }
        return message.toString();
    }

    private static String fmt(Vec3 position) {
        if (position == null) {
            return "<none>";
        }
        return String.format(Locale.ROOT, "(%.1f,%.1f,%.1f)", position.x, position.y, position.z);
    }

    private static void tickActor(ServerLevel level, ActorDefinition definition, long now) {
        ActorRuntime runtime = RUNTIME.computeIfAbsent(definition.npcId(), id -> new ActorRuntime());
        StardewNpcEntity npc = NpcSpawnManager.getTrackedNpc(level, definition.npcId());
        if ((huntStageActive || mainEventStageActive) && !HUNT_STAGE_ACTOR_IDS.contains(definition.npcId())) {
            despawnForHuntStage(level, definition.npcId(), runtime);
            return;
        }
        if (npc == null) {
            if (now - runtime.lastSpawnRequestTick >= SPAWN_RETRY_TICKS) {
                runtime.lastSpawnRequestTick = now;
                NpcSpawnManager.forceSpawnNpc(definition.npcId());
            }
            return;
        }

        if (!npc.getTags().contains(ACTOR_TAG) || runtime.boundEntityId != npc.getId()) {
            runtime.boundEntityId = npc.getId();
            NpcCentralMovementService.resetAuthoredMovementPlan(definition.npcId(), MOVEMENT_OWNER);
            placeAt(level, npc, definition.points().get(0));
            runtime.waitUntilTick = now + definition.waitTicks();
        }

        npc.addTag(ACTOR_TAG);
        npc.setInvisible(false);
        npc.setNoAi(false);
        npc.setInvulnerable(true);
        npc.setPersistenceRequired();

        if (huntStageActive) {
            tickHuntStageActor(level, npc, definition, runtime, now);
            return;
        }

        if (mainEventStageActive) {
            tickMainEventStageActor(level, npc, definition, runtime, now);
            return;
        }

        if (NpcInteractionService.isDialogueMovementLocked(definition.npcId()) || npc.isFacingOverrideActive()) {
            NpcCentralMovementService.stopAuthoredMovement(npc);
            return;
        }

        if (definition.points().size() <= 1) {
            keepAtInitialPoint(level, npc, definition, runtime, now);
            return;
        }

        tickRoute(level, npc, definition, runtime, now);
    }

    private static void tickMainEventStageActor(ServerLevel level,
                                                StardewNpcEntity npc,
                                                ActorDefinition definition,
                                                ActorRuntime runtime,
                                                long now) {
        Waypoint point = mainEventPoint(definition.npcId());
        if (point == null) {
            keepAtInitialPoint(level, npc, definition, runtime, now);
            return;
        }
        keepAtPoint(level, npc, point, runtime, now);
    }

    private static void tickHuntStageActor(ServerLevel level,
                                           StardewNpcEntity npc,
                                           ActorDefinition definition,
                                           ActorRuntime runtime,
                                           long now) {
        if (!EggFestivalService.isInsideFestivalBounds(npc.position())) {
            placeAt(level, npc, definition.points().get(0));
            runtime.clearHuntTarget();
        }

        if (!HUNT_CONTESTANT_IDS.contains(definition.npcId())) {
            keepAtInitialPoint(level, npc, definition, runtime, now);
            return;
        }

        if (NpcInteractionService.isDialogueMovementLocked(definition.npcId()) || npc.isFacingOverrideActive()) {
            NpcCentralMovementService.stopAuthoredMovement(npc);
            return;
        }

        if (runtime.huntTarget == null
            || now >= runtime.huntRetargetAfterTick
            || npc.position().distanceToSqr(runtime.huntTarget) <= HUNT_TARGET_REACHED_SQR
            || npc.getNavigation().isDone()) {
            chooseNextHuntTarget(level, npc, definition, runtime, now);
        }

        if (runtime.huntTarget == null) {
            keepAtInitialPoint(level, npc, definition, runtime, now);
            return;
        }

        boolean reached = NpcCentralMovementService.tickAuthoredWalkTarget(
            level,
            npc,
            MOVEMENT_OWNER,
            "egg_hunt_wander_" + definition.npcId(),
            runtime.huntTarget
        );
        if (reached) {
            runtime.clearHuntTarget();
            runtime.huntRetargetAfterTick = now + HUNT_WANDER_MIN_TICKS;
            npc.setWalking(false);
        }
    }

    private static void chooseNextHuntTarget(ServerLevel level,
                                             StardewNpcEntity npc,
                                             ActorDefinition definition,
                                             ActorRuntime runtime,
                                             long now) {
        Vec3 target = null;
        for (int attempt = 0; attempt < 8; attempt++) {
            Vec3 candidate = EggFestivalService.randomEggHuntNpcWanderTarget(level);
            if (candidate != null
                && EggFestivalService.isInsideFestivalBounds(candidate)
                && npc.position().distanceToSqr(candidate) > 4.0D) {
                target = candidate;
                break;
            }
        }
        if (target == null) {
            target = definition.points().get(0).position();
        }
        runtime.huntTarget = target;
        runtime.huntRetargetAfterTick = now + HUNT_WANDER_MIN_TICKS + level.getRandom().nextInt(HUNT_WANDER_RANDOM_TICKS + 1);
        NpcCentralMovementService.resetAuthoredMovementPlan(definition.npcId(), MOVEMENT_OWNER);
    }

    private static void keepAtInitialPoint(ServerLevel level,
                                           StardewNpcEntity npc,
                                           ActorDefinition definition,
                                           ActorRuntime runtime,
                                           long now) {
        keepAtPoint(level, npc, definition.points().get(0), runtime, now);
    }

    private static void keepAtPoint(ServerLevel level,
                                    StardewNpcEntity npc,
                                    Waypoint point,
                                    ActorRuntime runtime,
                                    long now) {
        if (now - runtime.lastStaticVerifyTick < STATIC_VERIFY_TICKS) {
            return;
        }
        runtime.lastStaticVerifyTick = now;
        if (npc.position().distanceToSqr(point.position()) > 0.64D) {
            placeAt(level, npc, point);
        } else {
            applyYaw(npc, point.yaw());
            NpcCentralMovementService.stopAuthoredMovement(npc);
        }
    }

    private static void despawnForHuntStage(ServerLevel level, String npcId, ActorRuntime runtime) {
        runtime.boundEntityId = -1;
        runtime.clearHuntTarget();
        NpcCentralMovementService.resetMovementPlan(npcId);
        NpcCentralMovementService.resetAuthoredMovementPlan(npcId, MOVEMENT_OWNER);
        NpcSpawnManager.temporarilyRemoveNpc(level, npcId);
    }

    private static void prepareMainEventActors(ServerLevel level) {
        if (level == null) {
            return;
        }
        actorsActive = true;
        for (ActorDefinition definition : ACTORS.values()) {
            ActorRuntime runtime = RUNTIME.computeIfAbsent(definition.npcId(), id -> new ActorRuntime());
            runtime.clearHuntTarget();
            NpcCentralMovementService.resetAuthoredMovementPlan(definition.npcId(), MOVEMENT_OWNER);
            if (!HUNT_STAGE_ACTOR_IDS.contains(definition.npcId())) {
                despawnForHuntStage(level, definition.npcId(), runtime);
                continue;
            }
            NpcSpawnManager.resumeNpcSpawn(definition.npcId());
            StardewNpcEntity npc = NpcSpawnManager.getTrackedNpc(level, definition.npcId());
            if (npc == null) {
                NpcSpawnManager.forceSpawnNpc(definition.npcId());
                NpcSpawnManager.tick(level);
                npc = NpcSpawnManager.getTrackedNpc(level, definition.npcId());
            }
            Waypoint point = mainEventPoint(definition.npcId());
            if (npc != null && point != null) {
                placeAt(level, npc, point);
                npc.addTag(ACTOR_TAG);
                runtime.boundEntityId = npc.getId();
            }
        }
    }

    private static Waypoint mainEventPoint(String npcId) {
        String canonicalId = canonical(npcId);
        if ("lewis".equals(canonicalId)) {
            return point(7, 64, 4, 'S');
        }
        int index = MAIN_EVENT_CONTESTANT_IDS.indexOf(canonicalId);
        if (index < 0) {
            return null;
        }
        return lineupPoint(index);
    }

    static Vec3 lineupPosition(int index) {
        int column = Math.floorMod(index, 6);
        int row = Math.floorDiv(index, 6);
        return new Vec3(1.5D + column * 2.0D, 64.0D, 7.5D + row * 2.0D);
    }

    private static Waypoint lineupPoint(int index) {
        return new Waypoint(lineupPosition(index), yaw('N'));
    }

    private static void runtimeClear(String npcId) {
        ActorRuntime runtime = RUNTIME.get(canonical(npcId));
        if (runtime != null) {
            runtime.clearHuntTarget();
        }
    }

    private static void tickRoute(ServerLevel level,
                                  StardewNpcEntity npc,
                                  ActorDefinition definition,
                                  ActorRuntime runtime,
                                  long now) {
        if (now < runtime.waitUntilTick) {
            NpcCentralMovementService.stopAuthoredMovement(npc);
            return;
        }

        int reachedRouteIndex = NpcCentralMovementService.tickAuthoredWalkRoute(
            level,
            npc,
            MOVEMENT_OWNER,
            "egg_festival_" + definition.npcId(),
            definition.routeTargets(),
            definition.loop()
        );
        if (reachedRouteIndex >= 0 && reachedRouteIndex < definition.routePoints().size()) {
            Waypoint reached = definition.routePoints().get(reachedRouteIndex);
            applyYaw(npc, reached.yaw());
            npc.setWalking(false);
            runtime.waitUntilTick = now + definition.waitTicks();
        }
    }

    private static void placeAt(ServerLevel level, StardewNpcEntity npc, Waypoint point) {
        npc.getNavigation().stop();
        npc.moveTo(point.position().x, point.position().y, point.position().z, point.yaw(), 0.0F);
        NpcCentralMovementService.snapToSurface(level, npc);
        npc.setDeltaMovement(Vec3.ZERO);
        npc.hasImpulse = false;
        applyYaw(npc, point.yaw());
        npc.setWalking(false);
    }

    private static void applyYaw(StardewNpcEntity npc, float yaw) {
        npc.setYRot(yaw);
        npc.setYHeadRot(yaw);
        npc.setYBodyRot(yaw);
    }

    private static ActorDefinition actor(String npcId, Waypoint point) {
        return ActorDefinition.create(canonical(npcId), List.of(point), false, 0);
    }

    private static ActorDefinition shuttle(String npcId, Waypoint a, Waypoint b) {
        return ActorDefinition.create(canonical(npcId), List.of(a, b), true, SHUTTLE_WAIT_TICKS);
    }

    private static ActorDefinition loop(String npcId, Waypoint... points) {
        return ActorDefinition.create(canonical(npcId), List.of(points), true, 0);
    }

    private static Waypoint point(double x, double y, double z, char facing) {
        return new Waypoint(new Vec3(x + 0.5D, y, z + 0.5D), yaw(facing));
    }

    private static float yaw(char facing) {
        return switch (Character.toUpperCase(facing)) {
            case 'N' -> 180.0F;
            case 'E' -> -90.0F;
            case 'W' -> 90.0F;
            case 'S' -> 0.0F;
            default -> 0.0F;
        };
    }

    private static String canonical(String npcId) {
        return npcId == null ? "" : npcId.trim().toLowerCase(Locale.ROOT);
    }

    private static Map<String, ActorDefinition> createActors() {
        List<ActorDefinition> definitions = new ArrayList<>();
        definitions.add(shuttle("abigail", point(-21, 64, -2, 'S'), point(-21, 64, -8, 'W')));
        definitions.add(actor("alex", point(21, 64, -5, 'S')));
        definitions.add(actor("caroline", point(11, 64, 10, 'E')));
        definitions.add(shuttle("clint", point(7, 64, -3, 'W'), point(13, 64, -7, 'S')));
        definitions.add(shuttle("demetrius", point(-8, 64, 0, 'S'), point(5, 64, -4, 'S')));
        definitions.add(actor("elliott", point(13, 64, 3, 'S')));
        definitions.add(actor("emily", point(-5, 64, 5, 'E')));
        definitions.add(actor("evelyn", point(13, 64, 11, 'W')));
        definitions.add(actor("george", point(13, 64, 10, 'W')));
        definitions.add(shuttle("gus", point(1, 64, -3, 'E'), point(5, 64, 0, 'N')));
        definitions.add(actor("haley", point(-1, 64, 8, 'N')));
        definitions.add(actor("harvey", point(2, 64, 2, 'S')));
        definitions.add(actor("jodi", point(10, 64, 13, 'N')));
        definitions.add(actor("leah", point(5, 64, 12, 'E')));
        definitions.add(actor("linus", point(-11, 64, 11, 'E')));
        definitions.add(actor("marlon", point(-34, 64, -6, 'S')));
        definitions.add(actor("marnie", point(6, 64, 4, 'S')));
        definitions.add(actor("maru", point(7, 64, 12, 'W')));
        definitions.add(shuttle("pam", point(4, 64, -2, 'E'), point(-2, 64, 1, 'S')));
        definitions.add(actor("penny", point(-3, 64, 14, 'S')));
        definitions.add(actor("pierre", point(-5, 64, -10, 'S')));
        definitions.add(actor("robin", point(-8, 64, 0, 'E')));
        definitions.add(actor("sam", point(22, 64, -4, 'W')));
        definitions.add(actor("sebastian", point(20, 64, -4, 'E')));
        definitions.add(shuttle("shane", point(1, 64, -8, 'S'), point(5, 64, -7, 'E')));
        definitions.add(actor("willy", point(14, 64, 15, 'W')));
        definitions.add(actor("lewis", point(7, 64, 4, 'S')));
        definitions.add(loop("vincent",
            point(-3, 64, 21, 'W'),
            point(-8, 64, 21, 'W'),
            point(-8, 64, 25, 'S'),
            point(-3, 64, 25, 'E')));
        definitions.add(loop("jas",
            point(-2, 64, 21, 'W'),
            point(-3, 64, 21, 'W'),
            point(-8, 64, 21, 'W'),
            point(-8, 64, 25, 'S'),
            point(-3, 64, 25, 'E'),
            point(-3, 64, 21, 'N')));

        Map<String, ActorDefinition> result = new LinkedHashMap<>();
        for (ActorDefinition definition : definitions) {
            result.put(definition.npcId(), definition);
        }
        return result;
    }

    private record ActorDefinition(String npcId, List<Waypoint> points, List<Waypoint> routePoints, List<Vec3> routeTargets, boolean loop, int waitTicks) {
        private static ActorDefinition create(String npcId, List<Waypoint> points, boolean loop, int waitTicks) {
            List<Waypoint> routePoints = points.size() <= 1
                ? List.of()
                : routePoints(points);
            List<Vec3> routeTargets = routePoints.stream()
                .map(Waypoint::position)
                .toList();
            return new ActorDefinition(npcId, points, routePoints, routeTargets, loop, waitTicks);
        }

        private static List<Waypoint> routePoints(List<Waypoint> points) {
            List<Waypoint> route = new ArrayList<>();
            for (int i = 1; i < points.size(); i++) {
                route.add(points.get(i));
            }
            route.add(points.get(0));
            return List.copyOf(route);
        }
    }

    private record Waypoint(Vec3 position, float yaw) {
    }

    private static final class ActorRuntime {
        private int boundEntityId = -1;
        private long waitUntilTick = 0L;
        private long lastSpawnRequestTick = -SPAWN_RETRY_TICKS;
        private long lastStaticVerifyTick = -STATIC_VERIFY_TICKS;
        private Vec3 huntTarget;
        private long huntRetargetAfterTick = 0L;

        private void clearHuntTarget() {
            huntTarget = null;
            huntRetargetAfterTick = 0L;
        }
    }
}
