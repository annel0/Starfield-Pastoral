package com.stardew.craft.npc.runtime;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.stardew.craft.npc.data.NpcDataRegistry;
import com.stardew.craft.npc.data.NpcLocationAnchor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Route resolution: converts schedule targets and route profiles into step sequences.
 */
final class NpcRoutePlanner {

    private static final Vec3 MANORHOUSE_OUTER = new Vec3(-196.0D, -17.0D, -22.0D);
    private static final Vec3 COMMUNITYCENTER_OUTER = new Vec3(-190.0D, -10.0D, 138.0D);
    private static final Vec3 SALOON_OUTER = new Vec3(-163.0D, -17.0D, 14.0D);
    private static final Vec3 SEEDSHOP_OUTER = new Vec3(-159.0D, -18.0D, 54.0D);
    private static final Vec3 SEEDSHOP_INNER_ENTRY = new Vec3(12038.0D, 71.0D, 12038.0D);
    private static final Vec3 FISHSHOP_OUTER = new Vec3(-237.0D, -15.0D, -212.0D);

    private static final Set<String> UNKNOWN_LOCATION_LOGGED = new HashSet<>();
    private static final Set<String> MISSING_CONFIG_POINT_LOGGED = new HashSet<>();
    private static final Set<String> HARD_ENDPOINT_WARNED = new HashSet<>();

    private NpcRoutePlanner() {
    }

    static void resetState() {
        UNKNOWN_LOCATION_LOGGED.clear();
        MISSING_CONFIG_POINT_LOGGED.clear();
        HARD_ENDPOINT_WARNED.clear();
    }

    // ---- public route resolution entry point ----

    static NpcRouteContext resolveRoute(ServerLevel level, String npcId, NpcRuntimeState state) {
        String canonicalNpcId = canonicalNpcId(npcId);
        NpcRouteContext profileRoute = resolveProfileRoute(canonicalNpcId, state);
        if (profileRoute != null) {
            return profileRoute;
        }
        return resolveGenericScheduleRoute(level, canonicalNpcId, state);
    }

    static String canonicalNpcId(String npcId) {
        if (npcId == null || npcId.isBlank()) {
            return "";
        }
        return npcId.trim().toLowerCase(Locale.ROOT);
    }

    static Vec3 nearestKnownIndoorEntry(Vec3 pos) {
        Vec3 nearest = null;
        double best = Double.MAX_VALUE;
        for (Map.Entry<String, NpcLocationAnchor> entry : NpcDataRegistry.locationAnchors().entrySet()) {
            NpcLocationAnchor anchor = entry.getValue();
            if (anchor.indoorEntryPoint().isEmpty()) {
                continue;
            }
            Vec3 entryVec = pointFromConfigStrict(anchor.indoorEntryPoint(), null, entry.getKey());
            if (entryVec == null) {
                continue;
            }
            double d = pos.distanceToSqr(entryVec);
            if (d < best) {
                best = d;
                nearest = entryVec;
            }
        }
        if (nearest != null) {
            return nearest;
        }
        // Legacy fallback
        Vec3[] entries = {pointFromConfig("seedshop_inner_entry", SEEDSHOP_INNER_ENTRY)};
        for (Vec3 entry : entries) {
            double d = pos.distanceToSqr(entry);
            if (d < best) {
                best = d;
                nearest = entry;
            }
        }
        return nearest;
    }

    static Vec3 linkedOutdoorDoor(Vec3 indoorEntry) {
        if (indoorEntry == null) {
            return null;
        }
        for (Map.Entry<String, NpcLocationAnchor> entry : NpcDataRegistry.locationAnchors().entrySet()) {
            NpcLocationAnchor anchor = entry.getValue();
            if (anchor.indoorEntryPoint().isEmpty() || anchor.outdoorDoorPoint().isEmpty()) {
                continue;
            }
            Vec3 entryVec = pointFromConfigStrict(anchor.indoorEntryPoint(), null, entry.getKey());
            if (entryVec != null && indoorEntry.distanceToSqr(entryVec) < 4.0D) {
                Vec3 doorVec = pointFromConfigStrict(anchor.outdoorDoorPoint(), null, entry.getKey());
                if (doorVec != null) {
                    return doorVec;
                }
            }
        }
        // Legacy fallback
        Vec3 configuredSeedshopEntry = pointFromConfig("seedshop_inner_entry", SEEDSHOP_INNER_ENTRY);
        if (indoorEntry.distanceToSqr(configuredSeedshopEntry) < 4.0D) {
            return pointFromConfig("seedshop_outer", SEEDSHOP_OUTER);
        }
        return null;
    }

    static Vec3 pointFromConfig(String pointId, Vec3 fallback) {
        JsonObject root = routePointsRoot();
        if (root == null || !root.has("points") || !root.get("points").isJsonObject()) {
            return fallback;
        }

        JsonObject points = root.getAsJsonObject("points");
        JsonElement element = points.get(pointId);
        if (element == null || !element.isJsonObject()) {
            if (MISSING_CONFIG_POINT_LOGGED.add(pointId)) {
                com.stardew.craft.StardewCraft.LOGGER.warn("Missing npc route point '{}' in npc_route_points event; using fallback.", pointId);
            }
            return fallback;
        }

        JsonObject obj = element.getAsJsonObject();
        double x = obj.has("x") ? obj.get("x").getAsDouble() : fallback.x;
        double y = obj.has("y") ? obj.get("y").getAsDouble() : fallback.y;
        double z = obj.has("z") ? obj.get("z").getAsDouble() : fallback.z;
        return new Vec3(x, y, z);
    }

    // ---- inner types ----

    enum RouteStepMode {
        WALK,
        WARP
    }

    static final class NpcRouteStep {
        final RouteStepMode mode;
        final String pointId;
        final Vec3 target;

        private NpcRouteStep(RouteStepMode mode, String pointId, Vec3 target) {
            this.mode = mode;
            this.pointId = pointId;
            this.target = target;
        }

        static NpcRouteStep walk(String pointId, Vec3 target) {
            return new NpcRouteStep(RouteStepMode.WALK, pointId, target);
        }

        static NpcRouteStep warp(String pointId, Vec3 target) {
            return new NpcRouteStep(RouteStepMode.WARP, pointId, target);
        }
    }

    static final class NpcRouteContext {
        final String canonicalLocation;
        final List<NpcRouteStep> destinationSteps;

        NpcRouteContext(String canonicalLocation, List<NpcRouteStep> destinationSteps) {
            this.canonicalLocation = canonicalLocation;
            this.destinationSteps = destinationSteps;
        }
    }

    // ---- private helpers ----

    private static NpcRouteContext resolveProfileRoute(String canonicalNpcId, NpcRuntimeState state) {
        if (canonicalNpcId == null || canonicalNpcId.isBlank() || state == null) {
            return null;
        }

        JsonObject root = NpcDataRegistry.events().get("npc_route_profiles");
        if (root == null || !root.has("profiles") || !root.get("profiles").isJsonObject()) {
            return null;
        }

        JsonObject profiles = root.getAsJsonObject("profiles");
        JsonObject npcProfile = getObjectCaseInsensitive(profiles, canonicalNpcId);
        if (npcProfile == null) {
            return null;
        }

        String location = state.locationName() == null ? "" : state.locationName().trim().toLowerCase(Locale.ROOT);
        String canonicalLocation = NpcDataRegistry.locationAliases().getOrDefault(location, location);
        JsonElement routeEl = npcProfile.get(canonicalLocation);
        if (routeEl == null && "town".equals(canonicalLocation)) {
            routeEl = npcProfile.get("towngarden");
        }
        if (routeEl == null || !routeEl.isJsonArray()) {
            return null;
        }

        List<NpcRouteStep> destination = new ArrayList<>();
        for (JsonElement stepEl : routeEl.getAsJsonArray()) {
            if (!stepEl.isJsonObject()) {
                continue;
            }
            JsonObject stepObj = stepEl.getAsJsonObject();
            if (!stepObj.has("point") || !stepObj.get("point").isJsonPrimitive()) {
                continue;
            }
            String pointId = stepObj.get("point").getAsString().trim();
            if (pointId.isBlank()) {
                continue;
            }

            String mode = "walk";
            if (stepObj.has("mode") && stepObj.get("mode").isJsonPrimitive()) {
                mode = stepObj.get("mode").getAsString().trim().toLowerCase(Locale.ROOT);
            }

            Vec3 point = pointFromConfigStrict(pointId, state, canonicalLocation);
            if (point == null) {
                return null;
            }

            if ("warp".equals(mode)) {
                destination.add(NpcRouteStep.warp(pointId, point));
            } else {
                destination.add(NpcRouteStep.walk(pointId, point));
            }
        }

        if (destination.isEmpty()) {
            return null;
        }

        return new NpcRouteContext(canonicalLocation, destination);
    }

    private static JsonObject getObjectCaseInsensitive(JsonObject root, String key) {
        if (root == null || key == null || key.isBlank()) {
            return null;
        }
        for (Map.Entry<String, JsonElement> entry : root.entrySet()) {
            if (!entry.getKey().equalsIgnoreCase(key)) {
                continue;
            }
            if (!entry.getValue().isJsonObject()) {
                return null;
            }
            return entry.getValue().getAsJsonObject();
        }
        return null;
    }

    private static NpcRouteContext resolveGenericScheduleRoute(ServerLevel level,
                                                               String canonicalNpcId,
                                                               NpcRuntimeState state) {
        if (state == null) {
            return null;
        }

        String location = state.locationName() == null ? "" : state.locationName().trim().toLowerCase(Locale.ROOT);
        String canonicalLocation = NpcDataRegistry.locationAliases().getOrDefault(location, location);
        NpcScheduleRuntimeService.TargetPoint target = NpcScheduleRuntimeService.resolveWorldTarget(level, state, null);
        if (target == null || target.position() == null) {
            String missingSig = "target_missing|" + canonicalNpcId + "|" + canonicalLocation;
            if (UNKNOWN_LOCATION_LOGGED.add(missingSig)) {
                com.stardew.craft.StardewCraft.LOGGER.warn(
                    "NPC route unresolved for npc='{}' location='{}'; movement paused.",
                    canonicalNpcId,
                    canonicalLocation
                );
            }
            return null;
        }

        List<NpcRouteStep> destination = new ArrayList<>();
        if (!target.indoorTarget()) {
            destination.add(NpcRouteStep.walk("schedule_anchor", target.position()));
            return new NpcRouteContext(canonicalLocation, destination);
        }

        Vec3 outdoorDoor = resolveOutdoorDoorForLocation(canonicalLocation, target.position());
        destination.add(NpcRouteStep.walk("outdoor_door", outdoorDoor));

        NpcLocationAnchor anchor = NpcDataRegistry.locationAnchors().get(canonicalLocation);
        if (anchor != null && !anchor.indoorEntryPoint().isEmpty()) {
            Vec3 indoorEntry = pointFromConfigStrict(anchor.indoorEntryPoint(), null, canonicalLocation);
            if (indoorEntry != null) {
                destination.add(NpcRouteStep.warp("indoor_entry", indoorEntry));
                destination.add(NpcRouteStep.walk("indoor_target", target.position()));
            } else {
                destination.add(NpcRouteStep.warp("indoor_target", target.position()));
            }
        } else if ("seedshop".equals(canonicalLocation)
            || "pierreshop".equals(canonicalLocation)
            || "shop".equals(canonicalLocation)
            || "sunroom".equals(canonicalLocation)
            || "sebastianroom".equals(canonicalLocation)) {
            destination.add(NpcRouteStep.warp("indoor_entry", pointFromConfig("seedshop_inner_entry", SEEDSHOP_INNER_ENTRY)));
            destination.add(NpcRouteStep.walk("indoor_target", target.position()));
        } else {
            destination.add(NpcRouteStep.warp("indoor_target", target.position()));
        }
        return new NpcRouteContext(canonicalLocation, destination);
    }

    private static Vec3 resolveOutdoorDoorForLocation(String canonicalLocation, Vec3 fallback) {
        NpcLocationAnchor anchor = NpcDataRegistry.locationAnchors().get(canonicalLocation);
        if (anchor != null && !anchor.outdoorDoorPoint().isEmpty()) {
            Vec3 resolved = pointFromConfigStrict(anchor.outdoorDoorPoint(), null, canonicalLocation);
            if (resolved != null) {
                return resolved;
            }
        }
        return switch (canonicalLocation) {
            case "seedshop", "pierreshop", "shop", "sunroom", "sebastianroom" -> pointFromConfig("seedshop_outer", SEEDSHOP_OUTER);
            case "saloon" -> pointFromConfig("saloon_outer", SALOON_OUTER);
            case "fishshop", "willyshop" -> pointFromConfig("fishshop_outer", FISHSHOP_OUTER);
            case "manorhouse" -> pointFromConfig("manorhouse_outer", MANORHOUSE_OUTER);
            case "communitycenter" -> pointFromConfig("communitycenter_outer", COMMUNITYCENTER_OUTER);
            default -> fallback;
        };
    }

    private static Vec3 pointFromConfigStrict(String pointId, NpcRuntimeState state, String canonicalLocation) {
        JsonObject root = routePointsRoot();
        if (root == null || !root.has("points") || !root.get("points").isJsonObject()) {
            emitHardEndpointWarning("missing_points_root", pointId, canonicalLocation, state);
            return null;
        }

        JsonObject points = root.getAsJsonObject("points");
        JsonElement element = points.get(pointId);
        if (element == null || !element.isJsonObject()) {
            emitHardEndpointWarning("missing_point_id", pointId, canonicalLocation, state);
            return null;
        }

        JsonObject obj = element.getAsJsonObject();
        if (!obj.has("x") || !obj.has("y") || !obj.has("z")) {
            emitHardEndpointWarning("point_missing_xyz", pointId, canonicalLocation, state);
            return null;
        }

        return new Vec3(obj.get("x").getAsDouble(), obj.get("y").getAsDouble(), obj.get("z").getAsDouble());
    }

    private static JsonObject routePointsRoot() {
        return NpcDataRegistry.events().get("npc_route_points");
    }

    private static void emitHardEndpointWarning(String reason, String pointId, String canonicalLocation, NpcRuntimeState state) {
        String sig = String.format(
            Locale.ROOT,
            "%s|%s|%s|%s|%d|%d",
            canonicalLocation,
            pointId,
            reason,
            safe(state == null ? "" : state.activeScheduleKey()),
            state == null ? -1 : state.scheduleCheckpoint(),
            state == null ? -1 : state.scheduleNodeIndex()
        );
        if (!HARD_ENDPOINT_WARNED.add(sig)) {
            return;
        }

        com.stardew.craft.StardewCraft.LOGGER.warn(
            "[LEWIS_ENDPOINT_MISSING][HARD] reason={} location={} point={} scheduleKey={} checkpoint={} node={} action=movement_paused",
            reason,
            canonicalLocation,
            pointId,
            safe(state == null ? "" : state.activeScheduleKey()),
            state == null ? -1 : state.scheduleCheckpoint(),
            state == null ? -1 : state.scheduleNodeIndex()
        );
    }

    private static String safe(String value) {
        return value == null || value.isBlank() ? "<none>" : value;
    }
}
