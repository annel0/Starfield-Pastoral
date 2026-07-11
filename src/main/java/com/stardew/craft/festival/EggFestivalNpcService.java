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

import static com.stardew.craft.festival.FestivalNpcActorRuntime.actorMap;
import static com.stardew.craft.festival.FestivalNpcActorRuntime.canonical;
import static com.stardew.craft.festival.FestivalNpcActorRuntime.point;

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

    private static final Map<String, FestivalNpcActorRuntime.ActorDefinition> ACTORS = createActors();
    private static final List<String> MAIN_EVENT_CONTESTANT_IDS = List.of(
        "abigail", "maru", "jas", "sam", "vincent"
    );
    private static final Set<String> HUNT_CONTESTANT_IDS = Set.of(
        "abigail", "maru", "jas", "sam", "vincent", "leo"
    );
    private static final Set<String> HUNT_STAGE_ACTOR_IDS = Set.of(
        "abigail", "maru", "jas", "sam", "vincent", "leo", "lewis"
    );
    private static final Map<String, HuntRuntime> HUNT_RUNTIME = new LinkedHashMap<>();
    private static final FestivalNpcActorRuntime NPC_ACTORS = new FestivalNpcActorRuntime(new FestivalNpcActorRuntime.Config(
        "Egg Festival",
        MOVEMENT_OWNER,
        ACTOR_TAG,
        "egg_festival_",
        null,
        STATIC_VERIFY_TICKS,
        SPAWN_RETRY_TICKS,
        FestivalNpcActorRuntime.DEFAULT_ROTATE_TICKS,
        false,
        ACTORS
    ), new FestivalNpcActorRuntime.Hooks() {
        @Override
        public boolean beforeTickActor(ServerLevel level,
                                       FestivalNpcActorRuntime.ActorDefinition definition,
                                       FestivalNpcActorRuntime.ActorRuntime actorRuntime,
                                       long now) {
            if ((huntStageActive || mainEventStageActive) && !HUNT_STAGE_ACTOR_IDS.contains(definition.npcId())) {
                despawnForHuntStage(level, definition.npcId());
                return true;
            }
            return false;
        }

        @Override
        public boolean beforeMovement(ServerLevel level,
                                      FestivalNpcActorRuntime.ActorDefinition definition,
                                      StardewNpcEntity npc,
                                      FestivalNpcActorRuntime.ActorRuntime actorRuntime,
                                      long now) {
            if (huntStageActive) {
                tickHuntStageActor(level, npc, definition, actorRuntime, now);
                return true;
            }
            if (mainEventStageActive) {
                tickMainEventStageActor(level, npc, definition, actorRuntime, now);
                return true;
            }
            return false;
        }
    });
    private static boolean huntStageActive;
    private static boolean mainEventStageActive;

    private EggFestivalNpcService() {
    }

    public static void tick(ServerLevel level, boolean activeRequested) {
        if (level == null) {
            return;
        }
        boolean debugActive = NPC_ACTORS.isDebugRequested() && FestivalMapOverlayManager.isApplied(level, OVERLAY_ID);
        NPC_ACTORS.tick(level, activeRequested || debugActive);
    }

    public static void requestDebugStart(ServerLevel level) {
        huntStageActive = false;
        mainEventStageActive = false;
        HUNT_RUNTIME.clear();
        NPC_ACTORS.requestDebugStart(level);
        if (level != null && FestivalMapOverlayManager.isApplied(level, OVERLAY_ID)) {
            tick(level, true);
        }
    }

    public static void restore(ServerLevel level) {
        huntStageActive = false;
        mainEventStageActive = false;
        HUNT_RUNTIME.clear();
        NPC_ACTORS.restore(level);
    }

    public static boolean controlsNpc(String npcId) {
        return NPC_ACTORS.controlsNpc(npcId);
    }

    public static int mainEventContestantCount() {
        return MAIN_EVENT_CONTESTANT_IDS.size();
    }

    public static void setHuntStageActive(boolean active) {
        huntStageActive = active;
        if (!active) {
            for (HuntRuntime runtime : HUNT_RUNTIME.values()) {
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
        NPC_ACTORS.setActorsActive(true);
        for (FestivalNpcActorRuntime.ActorDefinition definition : ACTORS.values()) {
            HuntRuntime huntRuntime = HUNT_RUNTIME.computeIfAbsent(definition.npcId(), id -> new HuntRuntime());
            huntRuntime.clearHuntTarget();
            NpcCentralMovementService.resetAuthoredMovementPlan(definition.npcId(), MOVEMENT_OWNER);
            if (!HUNT_STAGE_ACTOR_IDS.contains(definition.npcId())) {
                despawnForHuntStage(level, definition.npcId());
                continue;
            }
            NpcSpawnManager.resumeNpcSpawn(definition.npcId());
            StardewNpcEntity npc = NpcSpawnManager.getTrackedNpc(level, definition.npcId());
            if (npc == null) {
                NpcSpawnManager.forceSpawnNpc(definition.npcId());
            }
            if (npc != null) {
                FestivalNpcActorRuntime.Waypoint point = mainEventPoint(definition.npcId());
                if (point != null) {
                    if (!EggFestivalService.isInsideFestivalBounds(npc.position())) {
                        placeAt(level, npc, point);
                    } else {
                        applyYaw(npc, point.yaw());
                    }
                } else {
                    placeAt(level, npc, definition.points().get(0));
                }
                NPC_ACTORS.runtime(definition.npcId()).setBoundEntityId(npc.getId());
            }
        }
    }

    public static String resolveDialogueKey(ServerPlayer player, String npcId) {
        if (player == null || npcId == null || npcId.isBlank()) {
            return null;
        }
        if (!NPC_ACTORS.isActorsActive() && !EggFestivalService.isParticipant(player)) {
            return null;
        }
        String canonicalId = canonical(npcId);
        if (!NPC_ACTORS.actorIds().contains(canonicalId)) {
            return null;
        }
        return FestivalDialogueService.resolveDialogueKey(EggFestivalService.FESTIVAL_ID, canonicalId);
    }

    public static String debugStatus() {
        return "Egg Festival NPC actors: " + (NPC_ACTORS.isActorsActive() ? "ACTIVE" : "INACTIVE")
            + ", debugRequested=" + NPC_ACTORS.isDebugRequested()
            + ", tracked=" + NPC_ACTORS.runtimes().size() + "/" + ACTORS.size();
    }

    public static String debugStatus(ServerLevel level) {
        StringBuilder message = new StringBuilder(debugStatus());
        if (level == null) {
            return message.toString();
        }
        for (FestivalNpcActorRuntime.ActorDefinition definition : ACTORS.values()) {
            FestivalNpcActorRuntime.ActorRuntime runtime = NPC_ACTORS.existingRuntime(definition.npcId());
            StardewNpcEntity npc = NpcSpawnManager.getTrackedNpc(level, definition.npcId());
            NpcCentralMovementService.AuthoredDebugSnapshot movement = NpcCentralMovementService.getAuthoredDebugSnapshot(definition.npcId(), MOVEMENT_OWNER);
            message.append('\n').append(definition.npcId())
                .append(" pos=").append(npc == null ? "<missing>" : fmt(npc.position()))
                .append(" route=").append(definition.routeTargets().size())
                .append(" bound=").append(runtime == null ? -1 : runtime.boundEntityId())
                .append(" wait=").append(runtime == null ? 0 : Math.max(0L, runtime.waitUntilTick() - level.getGameTime()));
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

    private static void tickMainEventStageActor(ServerLevel level,
                                                StardewNpcEntity npc,
                                                FestivalNpcActorRuntime.ActorDefinition definition,
                                                FestivalNpcActorRuntime.ActorRuntime runtime,
                                                long now) {
        FestivalNpcActorRuntime.Waypoint point = mainEventPoint(definition.npcId());
        if (point == null) {
            keepAtInitialPoint(level, npc, definition, runtime, now);
            return;
        }
        keepAtPoint(level, npc, point, runtime, now);
    }

    private static void tickHuntStageActor(ServerLevel level,
                                           StardewNpcEntity npc,
                                           FestivalNpcActorRuntime.ActorDefinition definition,
                                           FestivalNpcActorRuntime.ActorRuntime runtime,
                                           long now) {
        HuntRuntime huntRuntime = HUNT_RUNTIME.computeIfAbsent(definition.npcId(), id -> new HuntRuntime());
        if (!EggFestivalService.isInsideFestivalBounds(npc.position())) {
            placeAt(level, npc, definition.points().get(0));
            huntRuntime.clearHuntTarget();
        }

        if (!HUNT_CONTESTANT_IDS.contains(definition.npcId())) {
            keepAtInitialPoint(level, npc, definition, runtime, now);
            return;
        }

        if (NpcInteractionService.isDialogueMovementLocked(definition.npcId()) || npc.isFacingOverrideActive()) {
            NpcCentralMovementService.stopAuthoredMovement(npc);
            return;
        }

        if (huntRuntime.huntTarget == null
            || now >= huntRuntime.huntRetargetAfterTick
            || npc.position().distanceToSqr(huntRuntime.huntTarget) <= HUNT_TARGET_REACHED_SQR
            || npc.getNavigation().isDone()) {
            chooseNextHuntTarget(level, npc, definition, huntRuntime, now);
        }

        if (huntRuntime.huntTarget == null) {
            keepAtInitialPoint(level, npc, definition, runtime, now);
            return;
        }

        boolean reached = NpcCentralMovementService.tickAuthoredWalkTarget(
            level,
            npc,
            MOVEMENT_OWNER,
            "egg_hunt_wander_" + definition.npcId(),
            huntRuntime.huntTarget
        );
        if (reached) {
            huntRuntime.clearHuntTarget();
            huntRuntime.huntRetargetAfterTick = now + HUNT_WANDER_MIN_TICKS;
            npc.setWalking(false);
        }
    }

    private static void chooseNextHuntTarget(ServerLevel level,
                                             StardewNpcEntity npc,
                                             FestivalNpcActorRuntime.ActorDefinition definition,
                                             HuntRuntime huntRuntime,
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
        huntRuntime.huntTarget = target;
        huntRuntime.huntRetargetAfterTick = now + HUNT_WANDER_MIN_TICKS + level.getRandom().nextInt(HUNT_WANDER_RANDOM_TICKS + 1);
        NpcCentralMovementService.resetAuthoredMovementPlan(definition.npcId(), MOVEMENT_OWNER);
    }

    private static void keepAtInitialPoint(ServerLevel level,
                                           StardewNpcEntity npc,
                                           FestivalNpcActorRuntime.ActorDefinition definition,
                                           FestivalNpcActorRuntime.ActorRuntime runtime,
                                           long now) {
        keepAtPoint(level, npc, definition.points().get(0), runtime, now);
    }

    private static void keepAtPoint(ServerLevel level,
                                    StardewNpcEntity npc,
                                    FestivalNpcActorRuntime.Waypoint point,
                                    FestivalNpcActorRuntime.ActorRuntime runtime,
                                    long now) {
        if (now - runtime.lastStaticVerifyTick() < STATIC_VERIFY_TICKS) {
            return;
        }
        runtime.setLastStaticVerifyTick(now);
        if (npc.position().distanceToSqr(point.position()) > 0.64D) {
            placeAt(level, npc, point);
        } else {
            applyYaw(npc, point.yaw());
            NpcCentralMovementService.stopAuthoredMovement(npc);
        }
    }

    private static void despawnForHuntStage(ServerLevel level, String npcId) {
        NPC_ACTORS.runtime(npcId).setBoundEntityId(-1);
        HuntRuntime runtime = HUNT_RUNTIME.get(canonical(npcId));
        if (runtime != null) {
            runtime.clearHuntTarget();
        }
        NpcCentralMovementService.resetMovementPlan(npcId);
        NpcCentralMovementService.resetAuthoredMovementPlan(npcId, MOVEMENT_OWNER);
        NpcSpawnManager.temporarilyRemoveNpc(level, npcId);
    }

    private static void prepareMainEventActors(ServerLevel level) {
        if (level == null) {
            return;
        }
        NPC_ACTORS.setActorsActive(true);
        for (FestivalNpcActorRuntime.ActorDefinition definition : ACTORS.values()) {
            HuntRuntime huntRuntime = HUNT_RUNTIME.computeIfAbsent(definition.npcId(), id -> new HuntRuntime());
            huntRuntime.clearHuntTarget();
            NpcCentralMovementService.resetAuthoredMovementPlan(definition.npcId(), MOVEMENT_OWNER);
            if (!HUNT_STAGE_ACTOR_IDS.contains(definition.npcId())) {
                despawnForHuntStage(level, definition.npcId());
                continue;
            }
            NpcSpawnManager.resumeNpcSpawn(definition.npcId());
            StardewNpcEntity npc = NpcSpawnManager.getTrackedNpc(level, definition.npcId());
            if (npc == null) {
                NpcSpawnManager.forceSpawnNpc(definition.npcId());
            }
            FestivalNpcActorRuntime.Waypoint point = mainEventPoint(definition.npcId());
            if (npc != null && point != null) {
                placeAt(level, npc, point);
                npc.addTag(ACTOR_TAG);
                NPC_ACTORS.runtime(definition.npcId()).setBoundEntityId(npc.getId());
            }
        }
    }

    private static FestivalNpcActorRuntime.Waypoint mainEventPoint(String npcId) {
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

    private static FestivalNpcActorRuntime.Waypoint lineupPoint(int index) {
        return new FestivalNpcActorRuntime.Waypoint(lineupPosition(index), FestivalNpcActorRuntime.yaw('N'));
    }

    private static void placeAt(ServerLevel level, StardewNpcEntity npc, FestivalNpcActorRuntime.Waypoint point) {
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

    private static FestivalNpcActorRuntime.ActorDefinition actor(String npcId, FestivalNpcActorRuntime.Waypoint point) {
        return FestivalNpcActorRuntime.actor(npcId, point, false, 0);
    }

    private static FestivalNpcActorRuntime.ActorDefinition shuttle(String npcId,
                                                                   FestivalNpcActorRuntime.Waypoint a,
                                                                   FestivalNpcActorRuntime.Waypoint b) {
        return FestivalNpcActorRuntime.route(npcId, true, SHUTTLE_WAIT_TICKS, a, b);
    }

    private static FestivalNpcActorRuntime.ActorDefinition loop(String npcId, FestivalNpcActorRuntime.Waypoint... points) {
        return FestivalNpcActorRuntime.route(npcId, true, 0, points);
    }

    private static String canonical(String npcId) {
        return npcId == null ? "" : npcId.trim().toLowerCase(Locale.ROOT);
    }

    private static Map<String, FestivalNpcActorRuntime.ActorDefinition> createActors() {
        List<FestivalNpcActorRuntime.ActorDefinition> definitions = new ArrayList<>();
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

        return actorMap(definitions);
    }

    private static final class HuntRuntime {
        private Vec3 huntTarget;
        private long huntRetargetAfterTick = 0L;

        private void clearHuntTarget() {
            huntTarget = null;
            huntRetargetAfterTick = 0L;
        }
    }
}
