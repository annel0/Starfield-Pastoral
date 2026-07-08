package com.stardew.craft.festival;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.stardew.craft.festival.FestivalNpcActorRuntime.actor;
import static com.stardew.craft.festival.FestivalNpcActorRuntime.actorMap;
import static com.stardew.craft.festival.FestivalNpcActorRuntime.canonical;
import static com.stardew.craft.festival.FestivalNpcActorRuntime.point;
import static com.stardew.craft.festival.FestivalNpcActorRuntime.rotating;
import static com.stardew.craft.festival.FestivalNpcActorRuntime.route;

public final class FlowerDanceNpcService {
    private static final String OVERLAY_ID = "Forest-FlowerFestival";
    private static final String MOVEMENT_OWNER = "flower_dance";
    private static final String ACTOR_TAG = "stardewcraft_flower_dance_actor";
    private static final int STATIC_VERIFY_TICKS = 100;
    private static final int SPAWN_RETRY_TICKS = 100;
    private static final int ROTATE_TICKS = 10;

    private static final Map<String, FestivalNpcActorRuntime.ActorDefinition> ACTORS = createActors();
    private static final FestivalNpcActorRuntime RUNTIME = new FestivalNpcActorRuntime(new FestivalNpcActorRuntime.Config(
        "Flower Dance",
        MOVEMENT_OWNER,
        ACTOR_TAG,
        "flower_dance_",
        null,
        STATIC_VERIFY_TICKS,
        SPAWN_RETRY_TICKS,
        ROTATE_TICKS,
        false,
        ACTORS
    ));

    private FlowerDanceNpcService() {
    }

    public static void tick(ServerLevel level, boolean activeRequested) {
        if (level == null) {
            return;
        }
        boolean debugActive = RUNTIME.isDebugRequested() && FestivalMapOverlayManager.isApplied(level, OVERLAY_ID);
        RUNTIME.tick(level, activeRequested || debugActive);
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

    public static String resolveDialogueKey(ServerPlayer player, String npcId) {
        if (player == null || npcId == null || npcId.isBlank()) {
            return null;
        }
        if (!RUNTIME.isActorsActive() && !FlowerDanceService.isParticipant(player)) {
            return null;
        }
        String canonicalId = canonical(npcId);
        if (!RUNTIME.actorIds().contains(canonicalId)) {
            return null;
        }
        return FestivalDialogueService.resolveDialogueKey(FlowerDanceService.FESTIVAL_ID, canonicalId);
    }

    public static String debugStatus(ServerLevel level) {
        return RUNTIME.debugStatus(level);
    }

    private static Map<String, FestivalNpcActorRuntime.ActorDefinition> createActors() {
        List<FestivalNpcActorRuntime.ActorDefinition> definitions = new ArrayList<>();
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

        return actorMap(definitions);
    }
}
