package com.stardew.craft.festival;

import com.stardew.craft.entity.npc.StardewNpcEntity;
import com.stardew.craft.npc.runtime.NpcCentralMovementService;
import com.stardew.craft.npc.runtime.NpcChunkForceManager;
import com.stardew.craft.npc.runtime.NpcInteractionService;
import com.stardew.craft.npc.runtime.NpcSpawnManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public final class FestivalNpcActorRuntime {
    public static final int DEFAULT_STATIC_VERIFY_TICKS = 100;
    public static final int DEFAULT_SPAWN_RETRY_TICKS = 100;
    public static final int DEFAULT_ROTATE_TICKS = 10;

    private final Config config;
    private final Hooks hooks;
    private final Map<String, ActorRuntime> runtime = new LinkedHashMap<>();
    private boolean actorsActive;
    private boolean debugRequested;

    public FestivalNpcActorRuntime(Config config) {
        this(config, Hooks.DEFAULT);
    }

    public FestivalNpcActorRuntime(Config config, Hooks hooks) {
        this.config = config;
        this.hooks = hooks == null ? Hooks.DEFAULT : hooks;
    }

    public void tick(ServerLevel level, boolean activeRequested) {
        if (level == null) {
            return;
        }
        if (!activeRequested) {
            if (actorsActive) {
                restore(level);
            }
            return;
        }
        actorsActive = true;
        long now = level.getGameTime();
        for (ActorDefinition definition : config.actors().values()) {
            if (hooks.shouldTickActor(definition)) {
                tickActor(level, definition, now);
            }
        }
    }

    public void requestDebugStart(ServerLevel level) {
        debugRequested = true;
        actorsActive = false;
        runtime.clear();
        for (String npcId : config.actors().keySet()) {
            if (hooks.shouldResumeNpcSpawn(npcId)) {
                NpcSpawnManager.resumeNpcSpawn(npcId);
            }
            if (hooks.shouldForceSpawnNpc(npcId)) {
                NpcSpawnManager.forceSpawnNpc(npcId);
            }
        }
    }

    public void restore(ServerLevel level) {
        if (!actorsActive && !debugRequested && runtime.isEmpty()) {
            return;
        }
        debugRequested = false;
        actorsActive = false;
        NpcSpawnManager.releaseFestivalSpawnTargets(config.movementOwner());
        for (String npcId : config.actors().keySet()) {
            if (level != null) {
                NpcChunkForceManager.releaseNpcForcedChunks(level, npcId);
            }
            if (hooks.shouldResumeNpcSpawn(npcId)) {
                NpcSpawnManager.resumeNpcSpawn(npcId);
            }
            if (hooks.shouldForceSpawnNpc(npcId)) {
                NpcSpawnManager.forceSpawnNpc(npcId);
            }
        }
        if (level == null) {
            runtime.clear();
            return;
        }
        NpcSpawnManager.prepareCurrentScheduleTargets(level);
        for (String npcId : config.actors().keySet()) {
            StardewNpcEntity npc = findActorEntity(level, npcId);
            if (npc == null) {
                continue;
            }
            npc.setInvisible(false);
            npc.removeTag(config.actorTag());
            npc.getNavigation().stop();
            npc.setNoAi(false);
            npc.setInvulnerable(true);
            npc.setDeltaMovement(Vec3.ZERO);
            npc.hasImpulse = false;
            NpcCentralMovementService.resetMovementPlan(npcId);
            NpcCentralMovementService.resetAuthoredMovementPlan(npcId, config.movementOwner());
            hooks.onRestoreNpc(level, npcId, npc);
        }
        runtime.clear();
    }

    public boolean controlsNpc(String npcId) {
        return (actorsActive || config.controlsWhenInactive()) && config.actorIds().contains(canonical(npcId));
    }

    public boolean isActorsActive() {
        return actorsActive;
    }

    public boolean isDebugRequested() {
        return debugRequested;
    }

    public void setActorsActive(boolean actorsActive) {
        this.actorsActive = actorsActive;
    }

    public void setDebugRequested(boolean debugRequested) {
        this.debugRequested = debugRequested;
    }

    public void clearRuntime() {
        runtime.clear();
    }

    public ActorRuntime runtime(String npcId) {
        return runtime.computeIfAbsent(canonical(npcId), ignored -> new ActorRuntime(config));
    }

    public ActorRuntime existingRuntime(String npcId) {
        return runtime.get(canonical(npcId));
    }

    public Map<String, ActorRuntime> runtimes() {
        return runtime;
    }

    public Map<String, ActorDefinition> actors() {
        return config.actors();
    }

    public Set<String> actorIds() {
        return config.actorIds();
    }

    public String debugStatus(ServerLevel level) {
        StringBuilder message = new StringBuilder(config.label()).append(" NPC actors: ")
            .append(actorsActive ? "ACTIVE" : "INACTIVE")
            .append(", debugRequested=").append(debugRequested)
            .append(", tracked=").append(runtime.size()).append('/').append(config.actors().size());
        if (level == null) {
            return message.toString();
        }
        int present = 0;
        List<String> missing = new ArrayList<>();
        for (ActorDefinition definition : config.actors().values()) {
            StardewNpcEntity npc = findActorEntity(level, definition.npcId());
            if (npc == null) {
                missing.add(definition.npcId());
            } else {
                present++;
            }
            ActorRuntime actorRuntime = runtime.get(definition.npcId());
            NpcCentralMovementService.AuthoredDebugSnapshot movement =
                NpcCentralMovementService.getAuthoredDebugSnapshot(definition.npcId(), config.movementOwner());
            NpcSpawnManager.SpawnDebugSnapshot spawn =
                NpcSpawnManager.getDebugSnapshot(level, definition.npcId());
            message.append('\n').append(definition.npcId())
                .append(" pos=").append(npc == null ? "<missing>" : fmt(npc.position()))
                .append(" route=").append(definition.routeTargets().size())
                .append(" bound=").append(actorRuntime == null ? -1 : actorRuntime.boundEntityId())
                .append(" wait=").append(actorRuntime == null ? 0 : Math.max(0L, actorRuntime.waitUntilTick() - level.getGameTime()));
            if (spawn != null) {
                message.append(" tracked=").append(spawn.trackedUuid())
                    .append(" trackedAlive=").append(spawn.trackedAlive())
                    .append(" loaded=").append(spawn.loadedCount())
                    .append(" miss=").append(spawn.missCount())
                    .append(" spawnAge=").append(spawn.spawnAgeTicks())
                    .append(" forcedChunk=").append(spawn.forcedChunk());
            }
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
        message.insert(message.indexOf("\n") < 0 ? message.length() : message.indexOf("\n"),
            ", present=" + present + "/" + config.actors().size() + ", missing=" + missing);
        return message.toString();
    }

    public StardewNpcEntity findActorEntity(ServerLevel level, String npcId) {
        StardewNpcEntity tracked = NpcSpawnManager.getTrackedNpc(level, npcId);
        if (tracked != null) {
            return tracked;
        }
        AABB searchBounds = config.searchBounds();
        if (level == null || searchBounds == null) {
            return null;
        }
        String canonicalId = canonical(npcId);
        return level.getEntitiesOfClass(StardewNpcEntity.class, searchBounds.inflate(16.0D), npc ->
                npc.isAlive() && !npc.isRemoved() && canonicalId.equals(canonical(npc.getNpcId())))
            .stream()
            .min(Comparator.comparingInt(Entity::getId))
            .orElse(null);
    }

    public void placeAt(ServerLevel level, StardewNpcEntity npc, Waypoint point) {
        if (npc == null || point == null) {
            return;
        }
        npc.getNavigation().stop();
        npc.moveTo(point.position().x, point.position().y, point.position().z, point.yaw(), 0.0F);
        NpcCentralMovementService.snapToSurface(level, npc);
        npc.setDeltaMovement(Vec3.ZERO);
        npc.hasImpulse = false;
        applyYaw(npc, point.yaw());
        npc.setWalking(false);
    }

    private void tickActor(ServerLevel level, ActorDefinition definition, long now) {
        ActorRuntime actorRuntime = runtime.computeIfAbsent(definition.npcId(), ignored -> new ActorRuntime(config));
        Waypoint spawnPoint = definition.points().get(0);
        NpcSpawnManager.claimFestivalSpawnTarget(
            config.movementOwner(), definition.npcId(), spawnPoint.position(), spawnPoint.yaw());
        NpcChunkForceManager.ensureRouteTargetChunkForced(level, definition.npcId(), spawnPoint.position());
        if (hooks.beforeTickActor(level, definition, actorRuntime, now)) {
            return;
        }
        StardewNpcEntity npc = findActorEntity(level, definition.npcId());
        if (npc == null) {
            npc = hooks.createMissingActor(level, definition, actorRuntime);
            if (npc == null && now - actorRuntime.lastSpawnRequestTick() >= config.spawnRetryTicks()
                && hooks.shouldForceSpawnNpc(definition.npcId())) {
                actorRuntime.setLastSpawnRequestTick(now);
                NpcSpawnManager.forceSpawnNpc(definition.npcId());
            }
            if (npc == null) {
                return;
            }
        }

        if (!npc.getTags().contains(config.actorTag()) || actorRuntime.boundEntityId() != npc.getId()) {
            actorRuntime.setBoundEntityId(npc.getId());
            NpcCentralMovementService.resetAuthoredMovementPlan(definition.npcId(), config.movementOwner());
            placeAt(level, npc, definition.points().get(0));
            actorRuntime.setLastStaticVerifyTick(now);
            actorRuntime.setWaitUntilTick(now + definition.waitTicks());
            hooks.onActorBound(level, definition, npc, actorRuntime);
        }

        NpcSpawnManager.claimFestivalSpawnTarget(
            config.movementOwner(), definition.npcId(), npc.position(), npc.getYRot());
        NpcChunkForceManager.ensureRouteTargetChunkForced(level, definition.npcId(), npc.position());

        npc.addTag(config.actorTag());
        npc.setInvisible(false);
        npc.setNoAi(false);
        npc.setInvulnerable(true);
        npc.setPersistenceRequired();

        if (hooks.beforeMovement(level, definition, npc, actorRuntime, now)) {
            return;
        }
        if (NpcInteractionService.isDialogueMovementLocked(definition.npcId()) || npc.isFacingOverrideActive()) {
            NpcCentralMovementService.stopAuthoredMovement(npc);
            return;
        }
        if (definition.rotatingFacings().length > 0) {
            keepRotatingAtPoint(level, npc, definition, actorRuntime, now);
            return;
        }
        if (definition.hasRoute()) {
            tickRoute(level, npc, definition, actorRuntime, now);
        } else {
            keepAtPoint(level, npc, definition.points().get(0), actorRuntime, now);
        }
    }

    private void keepRotatingAtPoint(ServerLevel level,
                                     StardewNpcEntity npc,
                                     ActorDefinition definition,
                                     ActorRuntime actorRuntime,
                                     long now) {
        Waypoint point = definition.points().get(0);
        if (npc.position().distanceToSqr(point.position()) > 0.64D) {
            placeAt(level, npc, point);
        }
        if (now - actorRuntime.lastRotationTick() >= config.rotateTicks()) {
            actorRuntime.setLastRotationTick(now);
            actorRuntime.setRotationIndex(Math.floorMod(actorRuntime.rotationIndex() + 1, definition.rotatingFacings().length));
            applyYaw(npc, yaw(definition.rotatingFacings()[actorRuntime.rotationIndex()]));
        }
        NpcCentralMovementService.stopAuthoredMovement(npc);
        npc.setWalking(false);
    }

    private void keepAtPoint(ServerLevel level, StardewNpcEntity npc, Waypoint point, ActorRuntime actorRuntime, long now) {
        if (now - actorRuntime.lastStaticVerifyTick() < config.staticVerifyTicks()) {
            return;
        }
        actorRuntime.setLastStaticVerifyTick(now);
        if (npc.position().distanceToSqr(point.position()) > 0.64D) {
            placeAt(level, npc, point);
        } else {
            applyYaw(npc, point.yaw());
            NpcCentralMovementService.stopAuthoredMovement(npc);
            npc.setWalking(false);
        }
    }

    private void tickRoute(ServerLevel level, StardewNpcEntity npc, ActorDefinition definition, ActorRuntime actorRuntime, long now) {
        if (now < actorRuntime.waitUntilTick()) {
            NpcCentralMovementService.stopAuthoredMovement(npc);
            npc.setWalking(false);
            return;
        }
        int reachedRouteIndex = NpcCentralMovementService.tickAuthoredWalkRoute(
            level,
            npc,
            config.movementOwner(),
            config.routePrefix() + definition.npcId(),
            definition.routeTargets(),
            definition.loop()
        );
        if (reachedRouteIndex >= 0 && reachedRouteIndex < definition.routePoints().size()) {
            Waypoint reached = definition.routePoints().get(reachedRouteIndex);
            applyYaw(npc, reached.yaw());
            npc.setWalking(false);
            actorRuntime.setWaitUntilTick(now + definition.waitTicks());
        }
    }

    public static ActorDefinition actor(String npcId, Waypoint point) {
        return ActorDefinition.create(canonical(npcId), List.of(point), true, 0, new char[0]);
    }

    public static ActorDefinition actor(String npcId, Waypoint point, boolean loop, int waitTicks) {
        return ActorDefinition.create(canonical(npcId), List.of(point), loop, waitTicks, new char[0]);
    }

    public static ActorDefinition route(String npcId, Waypoint... points) {
        return ActorDefinition.create(canonical(npcId), List.of(points), true, 0, new char[0]);
    }

    public static ActorDefinition route(String npcId, boolean loop, int waitTicks, Waypoint... points) {
        return ActorDefinition.create(canonical(npcId), List.of(points), loop, waitTicks, new char[0]);
    }

    public static ActorDefinition rotating(String npcId, Waypoint point, char... facings) {
        return ActorDefinition.create(canonical(npcId), List.of(point), true, 0, facings);
    }

    public static Map<String, ActorDefinition> actorMap(List<ActorDefinition> definitions) {
        Map<String, ActorDefinition> result = new LinkedHashMap<>();
        for (ActorDefinition definition : definitions) {
            result.put(definition.npcId(), definition);
        }
        return result;
    }

    public static Waypoint point(double x, double y, double z, char facing) {
        return new Waypoint(new Vec3(x + 0.5D, y, z + 0.5D), yaw(facing));
    }

    public static void applyYaw(StardewNpcEntity npc, float yaw) {
        npc.setYRot(yaw);
        npc.setYHeadRot(yaw);
        npc.setYBodyRot(yaw);
    }

    public static float yaw(char facing) {
        return switch (Character.toUpperCase(facing)) {
            case 'N' -> 180.0F;
            case 'E' -> -90.0F;
            case 'W' -> 90.0F;
            case 'S' -> 0.0F;
            default -> 0.0F;
        };
    }

    public static String canonical(String npcId) {
        return npcId == null ? "" : npcId.trim().toLowerCase(Locale.ROOT);
    }

    public static String fmt(Vec3 position) {
        if (position == null) {
            return "<none>";
        }
        return String.format(Locale.ROOT, "(%.1f,%.1f,%.1f)", position.x, position.y, position.z);
    }

    public record Config(String label,
                         String movementOwner,
                         String actorTag,
                         String routePrefix,
                         AABB searchBounds,
                         int staticVerifyTicks,
                         int spawnRetryTicks,
                         int rotateTicks,
                         boolean controlsWhenInactive,
                         Map<String, ActorDefinition> actors,
                         Set<String> actorIds) {
        public Config(String label,
                      String movementOwner,
                      String actorTag,
                      String routePrefix,
                      AABB searchBounds,
                      int staticVerifyTicks,
                      int spawnRetryTicks,
                      int rotateTicks,
                      boolean controlsWhenInactive,
                      Map<String, ActorDefinition> actors) {
            this(label, movementOwner, actorTag, routePrefix, searchBounds, staticVerifyTicks, spawnRetryTicks,
                rotateTicks, controlsWhenInactive, Map.copyOf(actors), Set.copyOf(actors.keySet()));
        }
    }

    public record ActorDefinition(String npcId,
                                  List<Waypoint> points,
                                  List<Waypoint> routePoints,
                                  List<Vec3> routeTargets,
                                  boolean loop,
                                  int waitTicks,
                                  char[] rotatingFacings) {
        public static ActorDefinition create(String npcId, List<Waypoint> points, boolean loop, int waitTicks) {
            return create(npcId, points, loop, waitTicks, new char[0]);
        }

        public static ActorDefinition create(String npcId, List<Waypoint> points, boolean loop, int waitTicks, char[] rotatingFacings) {
            List<Waypoint> routePoints = points.size() <= 1 ? List.of() : routePoints(points);
            List<Vec3> routeTargets = routePoints.stream().map(Waypoint::position).toList();
            return new ActorDefinition(canonical(npcId), List.copyOf(points), routePoints, routeTargets, loop, waitTicks,
                rotatingFacings == null ? new char[0] : rotatingFacings.clone());
        }

        private static List<Waypoint> routePoints(List<Waypoint> points) {
            List<Waypoint> route = new ArrayList<>();
            for (int i = 1; i < points.size(); i++) {
                route.add(points.get(i));
            }
            route.add(points.get(0));
            return List.copyOf(route);
        }

        public boolean hasRoute() {
            return !routeTargets.isEmpty();
        }

        public Waypoint point() {
            return points.get(0);
        }
    }

    public record Waypoint(Vec3 position, float yaw) {
    }

    public interface Hooks {
        Hooks DEFAULT = new Hooks() {
        };

        default boolean shouldTickActor(ActorDefinition definition) {
            return true;
        }

        default boolean shouldResumeNpcSpawn(String npcId) {
            return true;
        }

        default boolean shouldForceSpawnNpc(String npcId) {
            return true;
        }

        default boolean beforeTickActor(ServerLevel level, ActorDefinition definition, ActorRuntime actorRuntime, long now) {
            return false;
        }

        default StardewNpcEntity createMissingActor(ServerLevel level, ActorDefinition definition, ActorRuntime actorRuntime) {
            return null;
        }

        default void onActorBound(ServerLevel level, ActorDefinition definition, StardewNpcEntity npc, ActorRuntime actorRuntime) {
        }

        default boolean beforeMovement(ServerLevel level, ActorDefinition definition, StardewNpcEntity npc, ActorRuntime actorRuntime, long now) {
            return false;
        }

        default void onRestoreNpc(ServerLevel level, String npcId, StardewNpcEntity npc) {
            NpcSpawnManager.snapNpcToCurrentSchedule(level, npcId);
        }
    }

    public static final class ActorRuntime {
        private int boundEntityId = -1;
        private long waitUntilTick;
        private long lastSpawnRequestTick;
        private long lastStaticVerifyTick;
        private long lastRotationTick;
        private int rotationIndex;

        private ActorRuntime(Config config) {
            this.lastSpawnRequestTick = -config.spawnRetryTicks();
            this.lastStaticVerifyTick = -config.staticVerifyTicks();
            this.lastRotationTick = -config.rotateTicks();
        }

        public int boundEntityId() {
            return boundEntityId;
        }

        public void setBoundEntityId(int boundEntityId) {
            this.boundEntityId = boundEntityId;
        }

        public long waitUntilTick() {
            return waitUntilTick;
        }

        public void setWaitUntilTick(long waitUntilTick) {
            this.waitUntilTick = waitUntilTick;
        }

        public long lastSpawnRequestTick() {
            return lastSpawnRequestTick;
        }

        public void setLastSpawnRequestTick(long lastSpawnRequestTick) {
            this.lastSpawnRequestTick = lastSpawnRequestTick;
        }

        public long lastStaticVerifyTick() {
            return lastStaticVerifyTick;
        }

        public void setLastStaticVerifyTick(long lastStaticVerifyTick) {
            this.lastStaticVerifyTick = lastStaticVerifyTick;
        }

        public long lastRotationTick() {
            return lastRotationTick;
        }

        public void setLastRotationTick(long lastRotationTick) {
            this.lastRotationTick = lastRotationTick;
        }

        public int rotationIndex() {
            return rotationIndex;
        }

        public void setRotationIndex(int rotationIndex) {
            this.rotationIndex = rotationIndex;
        }
    }
}
