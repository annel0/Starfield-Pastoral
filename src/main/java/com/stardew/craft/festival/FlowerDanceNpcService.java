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

public final class FlowerDanceNpcService {
    private static final String OVERLAY_ID = "Forest-FlowerFestival";
    private static final String MOVEMENT_OWNER = "flower_dance";
    private static final String ACTOR_TAG = "stardewcraft_flower_dance_actor";
    private static final int STATIC_VERIFY_TICKS = 100;
    private static final int SPAWN_RETRY_TICKS = 100;
    private static final int ROTATE_TICKS = 10;

    private static final Map<String, ActorDefinition> ACTORS = createActors();
    private static final Set<String> ACTOR_IDS = Set.copyOf(ACTORS.keySet());
    private static final Map<String, ActorRuntime> RUNTIME = new LinkedHashMap<>();
    private static boolean actorsActive;
    private static boolean debugRequested;

    private FlowerDanceNpcService() {
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
            return;
        }
        debugRequested = false;
        actorsActive = false;
        for (String npcId : ACTORS.keySet()) {
            NpcSpawnManager.resumeNpcSpawn(npcId);
        }
        if (level == null) {
            RUNTIME.clear();
            return;
        }
        NpcSpawnManager.tick(level);
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
            NpcSpawnManager.snapNpcToCurrentSchedule(level, npcId);
            NpcCentralMovementService.resetMovementPlan(npcId);
            NpcCentralMovementService.resetAuthoredMovementPlan(npcId, MOVEMENT_OWNER);
        }
        RUNTIME.clear();
    }

    public static boolean controlsNpc(String npcId) {
        return actorsActive && ACTOR_IDS.contains(canonical(npcId));
    }

    public static String resolveDialogueKey(ServerPlayer player, String npcId) {
        if (player == null || npcId == null || npcId.isBlank()) {
            return null;
        }
        if (!actorsActive && !FlowerDanceService.isParticipant(player)) {
            return null;
        }
        String canonicalId = canonical(npcId);
        if (!ACTOR_IDS.contains(canonicalId)) {
            return null;
        }
        return FestivalDialogueService.resolveDialogueKey(FlowerDanceService.FESTIVAL_ID, canonicalId);
    }

    public static String debugStatus(ServerLevel level) {
        StringBuilder message = new StringBuilder("Flower Dance NPC actors: ")
            .append(actorsActive ? "ACTIVE" : "INACTIVE")
            .append(", debugRequested=").append(debugRequested)
            .append(", tracked=").append(RUNTIME.size()).append('/').append(ACTORS.size());
        if (level == null) {
            return message.toString();
        }
        for (ActorDefinition definition : ACTORS.values()) {
            StardewNpcEntity npc = NpcSpawnManager.getTrackedNpc(level, definition.npcId());
            message.append('\n').append(definition.npcId())
                .append(" pos=").append(npc == null ? "<missing>" : fmt(npc.position()))
                .append(" route=").append(definition.routeTargets().size());
        }
        return message.toString();
    }

    private static void tickActor(ServerLevel level, ActorDefinition definition, long now) {
        ActorRuntime runtime = RUNTIME.computeIfAbsent(definition.npcId(), id -> new ActorRuntime());
        StardewNpcEntity npc = NpcSpawnManager.getTrackedNpc(level, definition.npcId());
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
            runtime.waitUntilTick = now;
        }

        npc.addTag(ACTOR_TAG);
        npc.setInvisible(false);
        npc.setNoAi(false);
        npc.setInvulnerable(true);
        npc.setPersistenceRequired();

        if (NpcInteractionService.isDialogueMovementLocked(definition.npcId()) || npc.isFacingOverrideActive()) {
            NpcCentralMovementService.stopAuthoredMovement(npc);
            return;
        }

        if (definition.rotatingFacings().length > 0) {
            keepRotatingAtPoint(level, npc, definition, runtime, now);
            return;
        }

        if (definition.points().size() <= 1) {
            keepAtPoint(level, npc, definition.points().get(0), runtime, now);
            return;
        }

        tickRoute(level, npc, definition, runtime, now);
    }

    private static void keepRotatingAtPoint(ServerLevel level,
                                            StardewNpcEntity npc,
                                            ActorDefinition definition,
                                            ActorRuntime runtime,
                                            long now) {
        Waypoint point = definition.points().get(0);
        if (npc.position().distanceToSqr(point.position()) > 0.64D) {
            placeAt(level, npc, point);
        }
        if (now - runtime.lastRotationTick >= ROTATE_TICKS) {
            runtime.lastRotationTick = now;
            runtime.rotationIndex = Math.floorMod(runtime.rotationIndex + 1, definition.rotatingFacings().length);
            applyYaw(npc, yaw(definition.rotatingFacings()[runtime.rotationIndex]));
        }
        NpcCentralMovementService.stopAuthoredMovement(npc);
        npc.setWalking(false);
    }

    private static void keepAtPoint(ServerLevel level, StardewNpcEntity npc, Waypoint point, ActorRuntime runtime, long now) {
        if (now - runtime.lastStaticVerifyTick < STATIC_VERIFY_TICKS) {
            return;
        }
        runtime.lastStaticVerifyTick = now;
        if (npc.position().distanceToSqr(point.position()) > 0.64D) {
            placeAt(level, npc, point);
        } else {
            applyYaw(npc, point.yaw());
            NpcCentralMovementService.stopAuthoredMovement(npc);
            npc.setWalking(false);
        }
    }

    private static void tickRoute(ServerLevel level, StardewNpcEntity npc, ActorDefinition definition, ActorRuntime runtime, long now) {
        if (now < runtime.waitUntilTick) {
            NpcCentralMovementService.stopAuthoredMovement(npc);
            return;
        }
        int reachedRouteIndex = NpcCentralMovementService.tickAuthoredWalkRoute(
            level,
            npc,
            MOVEMENT_OWNER,
            "flower_dance_" + definition.npcId(),
            definition.routeTargets(),
            true
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
        return ActorDefinition.create(canonical(npcId), List.of(point), true, 0, new char[0]);
    }

    private static ActorDefinition route(String npcId, Waypoint... points) {
        return ActorDefinition.create(canonical(npcId), List.of(points), true, 0, new char[0]);
    }

    private static ActorDefinition rotating(String npcId, Waypoint point, char... facings) {
        return ActorDefinition.create(canonical(npcId), List.of(point), true, 0, facings);
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

    private static String fmt(Vec3 position) {
        if (position == null) {
            return "<none>";
        }
        return String.format(Locale.ROOT, "(%.1f,%.1f,%.1f)", position.x, position.y, position.z);
    }

    private static Map<String, ActorDefinition> createActors() {
        List<ActorDefinition> definitions = new ArrayList<>();
        definitions.add(actor("lewis", point(-235, 60, 109, 'S')));
        definitions.add(actor("marnie", point(-234, 60, 109, 'S')));
        definitions.add(actor("shane", point(-227, 60, 108, 'S')));
        definitions.add(actor("sebastian", point(-242, 60, 108, 'S')));
        definitions.add(actor("abigail", point(-244, 60, 109, 'S')));
        definitions.add(actor("wizard", point(-237, 64, 103, 'S')));
        definitions.add(actor("leah", point(-246, 60, 110, 'S')));
        definitions.add(actor("elliott", point(-245, 60, 112, 'N')));
        definitions.add(route("emily", point(-241, 60, 118, 'N'), point(-241, 60, 114, 'S')));
        definitions.add(route("haley",
            point(-236, 60, 120, 'N'),
            point(-236, 60, 118, 'N'),
            point(-239, 60, 118, 'W'),
            point(-236, 60, 118, 'N')));
        definitions.add(route("vincent",
            point(-229, 60, 119, 'N'),
            point(-229, 60, 116, 'N'),
            point(-231, 60, 116, 'W'),
            point(-231, 60, 119, 'S'),
            point(-229, 60, 119, 'E')));
        definitions.add(rotating("jas", point(-232, 60, 112, 'N'), 'N', 'E', 'W', 'S'));
        definitions.add(actor("linus", point(-225, 60, 113, 'W')));
        definitions.add(actor("penny", point(-227, 60, 116, 'W')));
        definitions.add(actor("sam", point(-227, 60, 117, 'W')));
        definitions.add(actor("george", point(-228, 60, 123, 'W')));
        definitions.add(actor("evelyn", point(-227, 60, 122, 'W')));
        definitions.add(actor("alex", point(-234, 60, 126, 'N')));
        definitions.add(actor("clint", point(-246, 60, 120, 'E')));
        definitions.add(actor("pam", point(-238, 60, 129, 'E')));
        definitions.add(route("gus", point(-234, 60, 132, 'N'), point(-236, 60, 132, 'N')));
        definitions.add(actor("maru", point(-229, 60, 131, 'W')));
        definitions.add(actor("harvey", point(-231, 60, 131, 'E')));
        definitions.add(actor("demetrius", point(-239, 60, 134, 'W')));
        definitions.add(actor("willy", point(-236, 60, 136, 'S')));
        definitions.add(actor("marlon", point(-247, 60, 136, 'S')));
        definitions.add(actor("robin", point(-246, 60, 132, 'S')));
        definitions.add(actor("caroline", point(-247, 60, 133, 'E')));
        definitions.add(actor("jodi", point(-245, 60, 133, 'W')));
        definitions.add(actor("pierre", point(-221, 60, 133, 'S')));

        Map<String, ActorDefinition> result = new LinkedHashMap<>();
        for (ActorDefinition definition : definitions) {
            result.put(definition.npcId(), definition);
        }
        return result;
    }

    private record ActorDefinition(String npcId, List<Waypoint> points, List<Waypoint> routePoints, List<Vec3> routeTargets,
                                   boolean loop, int waitTicks, char[] rotatingFacings) {
        private static ActorDefinition create(String npcId, List<Waypoint> points, boolean loop, int waitTicks, char[] rotatingFacings) {
            List<Waypoint> routePoints = points.size() <= 1 ? List.of() : routePoints(points);
            List<Vec3> routeTargets = routePoints.stream().map(Waypoint::position).toList();
            return new ActorDefinition(npcId, points, routePoints, routeTargets, loop, waitTicks, rotatingFacings);
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
        private long lastRotationTick = -ROTATE_TICKS;
        private int rotationIndex = 0;
    }
}
