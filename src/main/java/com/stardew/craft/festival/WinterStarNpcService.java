package com.stardew.craft.festival;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.stardew.craft.festival.FestivalNpcActorRuntime.actor;
import static com.stardew.craft.festival.FestivalNpcActorRuntime.actorMap;
import static com.stardew.craft.festival.FestivalNpcActorRuntime.point;

public final class WinterStarNpcService {
    private static final String OVERLAY_ID = "Town-Christmas";
    private static final String MOVEMENT_OWNER = "winter_star";
    private static final String ACTOR_TAG = "stardewcraft_winter_star_actor";
    private static final int STATIC_VERIFY_TICKS = 100;
    private static final int SPAWN_RETRY_TICKS = 100;
    private static final AABB VENUE_BOUNDS = new AABB(-31.0D, 64.0D, -26.0D, 42.0D, 79.0D, 26.0D);

    private static final Map<String, FestivalNpcActorRuntime.ActorDefinition> ACTORS = createActors();
    private static final FestivalNpcActorRuntime RUNTIME = new FestivalNpcActorRuntime(new FestivalNpcActorRuntime.Config(
        "Feast of the Winter Star",
        MOVEMENT_OWNER,
        ACTOR_TAG,
        "winter_star_",
        VENUE_BOUNDS,
        STATIC_VERIFY_TICKS,
        SPAWN_RETRY_TICKS,
        FestivalNpcActorRuntime.DEFAULT_ROTATE_TICKS,
        false,
        ACTORS
    ));

    private WinterStarNpcService() {
    }

    public static void tick(ServerLevel level, boolean activeRequested) {
        RUNTIME.tick(level, activeRequested);
    }

    public static void requestDebugStart(ServerLevel level) {
        RUNTIME.requestDebugStart(level);
        if (level != null && FestivalMapOverlayManager.isApplied(level, OVERLAY_ID)) {
            tick(level, true);
        }
    }

    public static void restore(ServerLevel level) {
        RUNTIME.restore(level);
    }

    public static boolean controlsNpc(String npcId) {
        return RUNTIME.controlsNpc(npcId);
    }

    public static boolean containsActor(String npcId) {
        return RUNTIME.actorIds().contains(FestivalNpcActorRuntime.canonical(npcId));
    }

    public static boolean isActorsActive() {
        return RUNTIME.isActorsActive();
    }

    public static boolean isDebugRequested() {
        return RUNTIME.isDebugRequested();
    }

    public static String debugStatus(ServerLevel level) {
        return RUNTIME.debugStatus(level);
    }

    private static Map<String, FestivalNpcActorRuntime.ActorDefinition> createActors() {
        List<FestivalNpcActorRuntime.ActorDefinition> definitions = new ArrayList<>();
        definitions.add(actor("marnie", point(-3, 64, -19, 'E')));
        definitions.add(actor("lewis", point(-3, 64, -18, 'E')));
        definitions.add(actor("jas", point(1, 64, -14, 'N')));
        definitions.add(actor("pam", point(4, 64, -6, 'S')));
        definitions.add(actor("penny", point(3, 64, -6, 'S')));
        definitions.add(actor("willy", point(12, 64, -5, 'E')));
        definitions.add(actor("marlon", point(13, 64, -7, 'S')));
        definitions.add(actor("pierre", point(-9, 64, 1, 'S')));
        definitions.add(actor("linus", point(29, 64, -6, 'S')));
        definitions.add(actor("robin", point(9, 64, 2, 'S')));
        definitions.add(actor("sebastian", point(10, 64, 2, 'S')));
        definitions.add(actor("maru", point(7, 64, 3, 'E')));
        definitions.add(actor("demetrius", point(11, 64, 6, 'W')));
        definitions.add(actor("abigail", point(-1, 64, 4, 'W')));
        definitions.add(actor("caroline", point(-5, 64, 6, 'E')));
        definitions.add(actor("shane", point(-5, 64, 10, 'E')));
        definitions.add(actor("clint", point(-5, 64, 13, 'E')));
        definitions.add(actor("harvey", point(-1, 64, 12, 'W')));
        definitions.add(actor("gus", point(-3, 64, 14, 'S')));
        definitions.add(actor("leah", point(-4, 64, 15, 'E')));
        definitions.add(actor("elliott", point(-2, 64, 15, 'W')));
        definitions.add(actor("emily", point(2, 64, 13, 'N')));
        definitions.add(actor("haley", point(4, 64, 13, 'N')));
        definitions.add(actor("george", point(8, 64, 16, 'W')));
        definitions.add(actor("evelyn", point(9, 64, 16, 'W')));
        definitions.add(actor("alex", point(9, 64, 14, 'S')));
        definitions.add(actor("jodi", point(7, 64, 11, 'E')));
        definitions.add(actor("sam", point(8, 64, 9, 'S')));
        definitions.add(actor("vincent", point(9, 64, 9, 'S')));
        return actorMap(definitions);
    }
}
