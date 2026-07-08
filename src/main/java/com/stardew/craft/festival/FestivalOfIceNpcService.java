package com.stardew.craft.festival;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.stardew.craft.festival.FestivalNpcActorRuntime.actor;
import static com.stardew.craft.festival.FestivalNpcActorRuntime.actorMap;
import static com.stardew.craft.festival.FestivalNpcActorRuntime.canonical;
import static com.stardew.craft.festival.FestivalNpcActorRuntime.point;
import static com.stardew.craft.festival.FestivalNpcActorRuntime.route;

public final class FestivalOfIceNpcService {
    private static final String OVERLAY_ID = "Forest-IceFestival";
    private static final String MOVEMENT_OWNER = "festival_of_ice";
    private static final String FISHING_MOVEMENT_OWNER = "festival_of_ice_fishing";
    private static final String ACTOR_TAG = "stardewcraft_festival_of_ice_actor";
    private static final String FISHING_ACTOR_TAG = "stardewcraft_festival_of_ice_fishing_actor";
    private static final int STATIC_VERIFY_TICKS = 100;
    private static final int SPAWN_RETRY_TICKS = 100;
    private static final int SHUTTLE_WAIT_TICKS = 40;
    private static final AABB VENUE_BOUNDS = new AABB(-192.0D, 63.0D, -2.0D, -32.0D, 73.0D, 83.0D);

    private static final Map<String, FestivalNpcActorRuntime.ActorDefinition> ACTORS = createActors();
    private static final Map<String, FestivalNpcActorRuntime.ActorDefinition> FISHING_ACTORS = createFishingActors();
    private static final FestivalNpcActorRuntime RUNTIME = new FestivalNpcActorRuntime(new FestivalNpcActorRuntime.Config(
        "Festival of Ice",
        MOVEMENT_OWNER,
        ACTOR_TAG,
        "festival_of_ice_",
        VENUE_BOUNDS,
        STATIC_VERIFY_TICKS,
        SPAWN_RETRY_TICKS,
        FestivalNpcActorRuntime.DEFAULT_ROTATE_TICKS,
        false,
        ACTORS
    ));
    private static final FestivalNpcActorRuntime FISHING_RUNTIME = new FestivalNpcActorRuntime(new FestivalNpcActorRuntime.Config(
        "Festival of Ice Fishing",
        FISHING_MOVEMENT_OWNER,
        FISHING_ACTOR_TAG,
        "festival_of_ice_fishing_",
        VENUE_BOUNDS,
        STATIC_VERIFY_TICKS,
        SPAWN_RETRY_TICKS,
        FestivalNpcActorRuntime.DEFAULT_ROTATE_TICKS,
        false,
        FISHING_ACTORS
    ));

    private FestivalOfIceNpcService() {
    }

    public static void tick(ServerLevel level, boolean activeRequested) {
        tick(level, activeRequested, false);
    }

    public static void tick(ServerLevel level, boolean activeRequested, boolean fishingStage) {
        if (fishingStage) {
            RUNTIME.restore(level);
            FISHING_RUNTIME.tick(level, activeRequested);
        } else {
            FISHING_RUNTIME.restore(level);
            RUNTIME.tick(level, activeRequested);
        }
    }

    public static void requestDebugStart(ServerLevel level) {
        RUNTIME.requestDebugStart(level);
        if (level != null && FestivalMapOverlayManager.isApplied(level, OVERLAY_ID)) {
            tick(level, true);
        }
    }

    public static void restore(ServerLevel level) {
        RUNTIME.restore(level);
        FISHING_RUNTIME.restore(level);
    }

    public static boolean controlsNpc(String npcId) {
        return RUNTIME.controlsNpc(npcId) || FISHING_RUNTIME.controlsNpc(npcId);
    }

    public static String resolveDialogueKey(ServerPlayer player, String npcId) {
        if (player == null || npcId == null || npcId.isBlank()) {
            return null;
        }
        if (!RUNTIME.isActorsActive() && !FISHING_RUNTIME.isActorsActive() && !FestivalOfIceService.isParticipant(player)) {
            return null;
        }
        String canonicalId = canonical(npcId);
        if (!RUNTIME.actorIds().contains(canonicalId) && !FISHING_RUNTIME.actorIds().contains(canonicalId)) {
            return null;
        }
        return FestivalDialogueService.resolveDialogueKey(FestivalOfIceService.FESTIVAL_ID, canonicalId);
    }

    public static String debugStatus(ServerLevel level) {
        return RUNTIME.debugStatus(level) + " | " + FISHING_RUNTIME.debugStatus(level);
    }

    private static FestivalNpcActorRuntime.ActorDefinition shuttle(String npcId,
                                                                   FestivalNpcActorRuntime.Waypoint a,
                                                                   FestivalNpcActorRuntime.Waypoint b) {
        return route(npcId, true, SHUTTLE_WAIT_TICKS, a, b);
    }

    private static Map<String, FestivalNpcActorRuntime.ActorDefinition> createActors() {
        List<FestivalNpcActorRuntime.ActorDefinition> definitions = new ArrayList<>();
        definitions.add(actor("wizard", point(-179, 69, 51, 'S')));
        definitions.add(actor("linus", point(-175, 64, 70, 'S')));
        definitions.add(actor("gus", point(-121, 64, 71, 'S')));
        definitions.add(actor("harvey", point(-120, 64, 72, 'W')));
        definitions.add(actor("marnie", point(-124, 64, 71, 'W')));
        definitions.add(actor("willy", point(-113, 64, 75, 'S')));
        definitions.add(actor("demetrius", point(-112, 64, 75, 'S')));
        definitions.add(actor("vincent", point(-116, 64, 57, 'S')));
        definitions.add(actor("sam", point(-120, 64, 53, 'E')));
        definitions.add(actor("sebastian", point(-119, 64, 52, 'S')));
        definitions.add(actor("lewis", point(-111, 64, 51, 'S')));
        definitions.add(actor("jodi", point(-115, 64, 44, 'S')));
        definitions.add(actor("caroline", point(-116, 64, 46, 'E')));
        definitions.add(actor("evelyn", point(-107, 64, 46, 'S')));
        definitions.add(actor("george", point(-107, 64, 48, 'S')));
        definitions.add(actor("robin", point(-113, 64, 27, 'W')));
        definitions.add(actor("leah", point(-115, 64, 22, 'W')));
        definitions.add(actor("shane", point(-110, 64, 46, 'S')));
        definitions.add(shuttle("pam", point(-100, 64, 63, 'S'), point(-105, 64, 66, 'S')));
        definitions.add(actor("alex", point(-104, 64, 57, 'N')));
        definitions.add(actor("haley", point(-105, 64, 53, 'E')));
        definitions.add(actor("clint", point(-92, 64, 57, 'W')));
        definitions.add(actor("emily", point(-95, 64, 57, 'E')));
        definitions.add(actor("abigail", point(-104, 64, 50, 'W')));
        definitions.add(actor("pierre", point(-105, 64, 51, 'N')));
        definitions.add(actor("maru", point(-98, 64, 48, 'S')));
        definitions.add(actor("jas", point(-91, 64, 45, 'W')));
        definitions.add(actor("penny", point(-92, 64, 46, 'N')));
        definitions.add(actor("elliott", point(-119, 64, 21, 'S')));

        return actorMap(definitions);
    }

    private static Map<String, FestivalNpcActorRuntime.ActorDefinition> createFishingActors() {
        List<FestivalNpcActorRuntime.ActorDefinition> definitions = new ArrayList<>();
        definitions.add(actor("gus", point(-111, 64, 50, 'S')));
        definitions.add(actor("alex", point(-110, 64, 50, 'S')));
        definitions.add(actor("leah", point(-108, 64, 50, 'S')));
        definitions.add(actor("caroline", point(-108, 64, 51, 'S')));
        definitions.add(actor("abigail", point(-107, 64, 51, 'S')));
        definitions.add(actor("pierre", point(-106, 64, 52, 'S')));
        definitions.add(actor("jodi", point(-106, 64, 56, 'W')));
        definitions.add(actor("clint", point(-105, 64, 57, 'W')));
        definitions.add(actor("haley", point(-105, 64, 61, 'W')));
        definitions.add(actor("maru", point(-107, 64, 63, 'N')));
        definitions.add(actor("harvey", point(-109, 64, 65, 'N')));
        definitions.add(actor("marnie", point(-114, 64, 65, 'N')));
        definitions.add(actor("sam", point(-117, 64, 65, 'N')));
        definitions.add(actor("sebastian", point(-118, 64, 65, 'N')));
        definitions.add(actor("shane", point(-118, 64, 63, 'E')));
        definitions.add(actor("vincent", point(-119, 64, 61, 'E')));
        definitions.add(actor("jas", point(-119, 64, 62, 'E')));
        definitions.add(actor("george", point(-120, 64, 57, 'E')));
        definitions.add(actor("evelyn", point(-121, 64, 57, 'E')));
        definitions.add(actor("robin", point(-118, 64, 54, 'E')));
        definitions.add(actor("demetrius", point(-119, 64, 53, 'E')));
        definitions.add(actor("penny", point(-116, 64, 50, 'S')));
        definitions.add(actor("lewis", point(-112, 64, 55, 'S')));
        definitions.add(actor("pam", point(-118, 64, 57, 'S')));
        definitions.add(actor("willy", point(-113, 64, 61, 'S')));
        definitions.add(actor("elliott", point(-109, 64, 60, 'E')));

        return actorMap(definitions);
    }
}
