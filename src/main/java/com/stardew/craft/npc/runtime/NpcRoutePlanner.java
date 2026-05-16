package com.stardew.craft.npc.runtime;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.stardew.craft.interior.InteriorRegionRegistry;
import com.stardew.craft.interior.InteriorPortalRegistry;
import com.stardew.craft.interior.InteriorSubspaceManager;
import com.stardew.craft.npc.data.NpcDataRegistry;
import com.stardew.craft.npc.data.NpcLocationAnchor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Route resolution: converts schedule targets and route profiles into step sequences.
 */
@SuppressWarnings("null")
final class NpcRoutePlanner {
    private static final Set<String> UNKNOWN_LOCATION_LOGGED = new HashSet<>();
    private static final Set<String> MISSING_CONFIG_POINT_LOGGED = new HashSet<>();
    private static final Set<String> HARD_ENDPOINT_WARNED = new HashSet<>();

    // ── Hot-path caches ──
    private static final Map<String, String> CANONICAL_NPC_ID_CACHE = new HashMap<>();
    private static List<Vec3> cachedIndoorEntryPoints = null;
    private static Map<Vec3, Vec3> cachedIndoorToOutdoorMap = null;
    /** Reference to the locationAnchors map at the time the indoor cache was built. */
    private static Map<String, NpcLocationAnchor> cachedIndoorAnchorSource = null;
    private static boolean portalTargetsInitialized = false;

    private NpcRoutePlanner() {
    }

    static void resetState() {
        UNKNOWN_LOCATION_LOGGED.clear();
        MISSING_CONFIG_POINT_LOGGED.clear();
        HARD_ENDPOINT_WARNED.clear();
        CANONICAL_NPC_ID_CACHE.clear();
        cachedIndoorEntryPoints = null;
        cachedIndoorToOutdoorMap = null;
        cachedIndoorAnchorSource = null;
        portalTargetsInitialized = false;
    }

    // ---- public route resolution entry point ----

    static NpcRouteContext resolveRoute(ServerLevel level, String npcId, NpcRuntimeState state) {
        String canonicalNpcId = canonicalNpcId(npcId);
        if (state == null) {
            return NpcRouteContext.invalid("", "missing_runtime_state", "", "");
        }
        if (state != null) {
            String namedPointId = state.namedPointId();
            if (namedPointId != null && !namedPointId.isBlank()) {
                NpcRouteContext directScheduleRoute = resolveGenericScheduleRoute(level, canonicalNpcId, state);
                if (directScheduleRoute != null) {
                    return directScheduleRoute;
                }
            }
        }
        NpcRouteContext profileRoute = resolveProfileRoute(canonicalNpcId, state);
        if (profileRoute != null) {
            return profileRoute;
        }
        NpcRouteContext genericRoute = resolveGenericScheduleRoute(level, canonicalNpcId, state);
        return genericRoute == null
            ? NpcRouteContext.waitingForCoordinates("", "target_missing", state.namedPointId(), "")
            : genericRoute;
    }

    static String canonicalNpcId(String npcId) {
        if (npcId == null || npcId.isBlank()) {
            return "";
        }
        return CANONICAL_NPC_ID_CACHE.computeIfAbsent(npcId, k -> k.trim().toLowerCase(Locale.ROOT));
    }

    private static void ensureIndoorCaches() {
        // Invalidate if the underlying data source has been replaced (e.g. /reload).
        Map<String, NpcLocationAnchor> currentAnchors = NpcDataRegistry.locationAnchors();
        if (cachedIndoorEntryPoints != null && cachedIndoorAnchorSource == currentAnchors) return;
        List<Vec3> entries = new ArrayList<>();
        Map<Vec3, Vec3> indoorToOutdoor = new HashMap<>();
        for (Map.Entry<String, NpcLocationAnchor> entry : currentAnchors.entrySet()) {
            NpcLocationAnchor anchor = entry.getValue();
            if (anchor.indoorEntryPoint().isEmpty()) continue;
            Vec3 entryVec = pointFromConfigStrict(anchor.indoorEntryPoint(), null, entry.getKey());
            if (entryVec == null) continue;
            entries.add(entryVec);
            if (!anchor.outdoorDoorPoint().isEmpty()) {
                Vec3 outdoor = pointFromConfigStrict(anchor.outdoorDoorPoint(), null, entry.getKey());
                if (outdoor != null) {
                    indoorToOutdoor.put(entryVec, outdoor);
                }
            }
        }
        cachedIndoorEntryPoints = entries;
        cachedIndoorToOutdoorMap = indoorToOutdoor;
        cachedIndoorAnchorSource = currentAnchors;
    }

    static Vec3 nearestKnownIndoorEntry(Vec3 pos) {
        ensureIndoorCaches();
        Vec3 nearest = null;
        double best = Double.MAX_VALUE;
        for (Vec3 entryVec : cachedIndoorEntryPoints) {
            double d = pos.distanceToSqr(entryVec);
            if (d < best) {
                best = d;
                nearest = entryVec;
            }
        }
        if (nearest != null) {
            return nearest;
        }
        return nearest;
    }

    static Vec3 linkedOutdoorDoor(Vec3 indoorEntry) {
        if (indoorEntry == null) {
            return null;
        }
        ensureIndoorCaches();
        // Check cached indoor→outdoor map first
        for (Map.Entry<Vec3, Vec3> mapped : cachedIndoorToOutdoorMap.entrySet()) {
            if (indoorEntry.distanceToSqr(mapped.getKey()) < 4.0D) {
                return mapped.getValue();
            }
        }
        return null;
    }

    static Vec3 anchorPosition(String canonicalLocation, NpcLocationAnchor anchor) {
        if (anchor == null) {
            return null;
        }
        if (anchor.indoor() && !anchor.indoorEntryPoint().isBlank()) {
            Vec3 point = pointFromConfig(anchor.indoorEntryPoint(), null);
            return point;
        }
        if (!anchor.indoor() && !anchor.outdoorDoorPoint().isBlank()) {
            Vec3 point = pointFromConfig(anchor.outdoorDoorPoint(), null);
            return point;
        }
        if (InteriorRegionRegistry.hasFixedInteriorAlias(canonicalLocation)) {
            return null;
        }
        if (!anchor.portalTarget().isBlank()) {
            String targetId = anchor.indoor() ? anchor.portalTarget() : exitPortalTargetId(anchor.portalTarget());
            return portalTargetPosition(targetId);
        }
        return new Vec3(anchor.x(), anchor.y(), anchor.z());
    }

    static Vec3 pointFromConfig(String pointId, Vec3 defaultPoint) {
        JsonObject root = routePointsRoot();
        if (root != null && root.has("points") && root.get("points").isJsonObject()) {
            JsonObject points = root.getAsJsonObject("points");
            JsonElement element = points.get(pointId);
            if (element != null && element.isJsonObject()) {
                JsonObject obj = element.getAsJsonObject();
                if (defaultPoint == null && (!obj.has("x") || !obj.has("y") || !obj.has("z"))) {
                    return null;
                }
                return routePointPosition(obj, defaultPoint);
            }
            if (MISSING_CONFIG_POINT_LOGGED.add(pointId)) {
            }
        }

        return defaultPoint;
    }

    static Vec3 routePointPosition(JsonObject obj, Vec3 defaultPoint) {
        double x = obj.has("x") ? obj.get("x").getAsDouble() : defaultPoint.x;
        double y = obj.has("y") ? obj.get("y").getAsDouble() : defaultPoint.y;
        double z = obj.has("z") ? obj.get("z").getAsDouble() : defaultPoint.z;
        return new Vec3(centerRouteAxis(x), y, centerRouteAxis(z));
    }

    private static double centerRouteAxis(double value) {
        return Math.rint(value) == value ? value + 0.5D : value;
    }

    // ---- inner types ----

    enum RouteStepMode {
        WALK,
        WARP
    }

    enum RouteStatus {
        READY,
        WAITING_FOR_COORDINATES,
        INVALID_DATA,
        UNSUPPORTED_CONTEXT
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
        final RouteStatus status;
        final String diagnosticReason;
        final String missingPointId;
        final String missingPortalLinkId;

        NpcRouteContext(String canonicalLocation, List<NpcRouteStep> destinationSteps) {
            this(canonicalLocation, destinationSteps, RouteStatus.READY, "none", "", "");
        }

        private NpcRouteContext(String canonicalLocation,
                                List<NpcRouteStep> destinationSteps,
                                RouteStatus status,
                                String diagnosticReason,
                                String missingPointId,
                                String missingPortalLinkId) {
            this.canonicalLocation = canonicalLocation;
            this.destinationSteps = destinationSteps;
            this.status = status;
            this.diagnosticReason = diagnosticReason == null || diagnosticReason.isBlank() ? "none" : diagnosticReason;
            this.missingPointId = missingPointId == null ? "" : missingPointId;
            this.missingPortalLinkId = missingPortalLinkId == null ? "" : missingPortalLinkId;
        }

        static NpcRouteContext ready(String canonicalLocation, List<NpcRouteStep> destinationSteps) {
            return new NpcRouteContext(canonicalLocation, destinationSteps);
        }

        static NpcRouteContext waitingForCoordinates(String canonicalLocation,
                                                     String diagnosticReason,
                                                     String missingPointId,
                                                     String missingPortalLinkId) {
            return new NpcRouteContext(canonicalLocation, List.of(), RouteStatus.WAITING_FOR_COORDINATES, diagnosticReason, missingPointId, missingPortalLinkId);
        }

        static NpcRouteContext invalid(String canonicalLocation,
                                       String diagnosticReason,
                                       String missingPointId,
                                       String missingPortalLinkId) {
            return new NpcRouteContext(canonicalLocation, List.of(), RouteStatus.INVALID_DATA, diagnosticReason, missingPointId, missingPortalLinkId);
        }

        boolean ready() {
            return status == RouteStatus.READY;
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
                return NpcRouteContext.waitingForCoordinates(canonicalLocation, "missing_route_point", pointId, "");
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

        // If the active schedule provides a named point ("@point_id"), always prefer it
        // as the final destination — both for indoor routes (after warp) and outdoor-only
        // routes. This prevents hard-coded profile endpoints from overriding per-time
        // schedule targets such as town hangout vs football, or indoor sleep vs gaming points.
        String namedPointId = state.namedPointId();
        if (namedPointId != null && !namedPointId.isBlank()) {
            Vec3 scheduleNamedTarget = pointFromConfigStrict(namedPointId, state, canonicalLocation);
            if (scheduleNamedTarget != null) {
                // If the named point is outdoor but the profile route contains a WARP step,
                // abandon the profile route entirely — the NPC would warp indoor and then
                // try to A* walk to unreachable outdoor coordinates, causing infinite
                // plan rebuilds.  Fall through to resolveGenericScheduleRoute() instead.
                boolean routeHasWarp = destination.stream()
                        .anyMatch(s -> s.mode == RouteStepMode.WARP);
                if (routeHasWarp && !isPointIndoor(namedPointId)) {
                    return null;
                }

                int last = destination.size() - 1;
                NpcRouteStep lastStep = destination.get(last);
                if (lastStep.mode == RouteStepMode.WALK) {
                    destination.set(last, NpcRouteStep.walk(namedPointId, scheduleNamedTarget));
                } else {
                    destination.add(NpcRouteStep.walk(namedPointId, scheduleNamedTarget));
                }
            }
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
            }
            return NpcRouteContext.waitingForCoordinates(canonicalLocation, "target_missing", state.namedPointId(), "");
        }

        List<NpcRouteStep> destination = new ArrayList<>();
        if (!target.indoorTarget()) {
            destination.add(NpcRouteStep.walk("schedule_anchor", target.position()));
            return NpcRouteContext.ready(canonicalLocation, destination);
        }

        NpcLocationAnchor anchor = NpcDataRegistry.locationAnchors().get(canonicalLocation);
        if (anchor == null) {
            String sig = "missing_anchor|" + canonicalNpcId + "|" + canonicalLocation;
            if (UNKNOWN_LOCATION_LOGGED.add(sig)) {
            }
            return NpcRouteContext.waitingForCoordinates(canonicalLocation, "missing_location_anchor", "", canonicalLocation);
        }
        if (anchor.outdoorDoorPoint().isBlank()) {
            return NpcRouteContext.waitingForCoordinates(canonicalLocation, "missing_outdoor_entry_walk_target", "", canonicalLocation);
        }
        Vec3 outdoorDoor = resolveOutdoorDoorForLocation(canonicalLocation);
        if (outdoorDoor == null) {
            return NpcRouteContext.waitingForCoordinates(canonicalLocation, "missing_outdoor_entry_walk_target", anchor.outdoorDoorPoint(), canonicalLocation);
        }
        destination.add(NpcRouteStep.walk("outdoor_door", outdoorDoor));

        if (anchor.indoorEntryPoint().isBlank()) {
            return NpcRouteContext.waitingForCoordinates(canonicalLocation, "missing_indoor_entry_landing", "", canonicalLocation);
        }
        Vec3 indoorEntry = pointFromConfigStrict(anchor.indoorEntryPoint(), null, canonicalLocation);
        if (indoorEntry == null) {
            return NpcRouteContext.waitingForCoordinates(canonicalLocation, "missing_indoor_entry_landing", anchor.indoorEntryPoint(), canonicalLocation);
        }
        destination.add(NpcRouteStep.warp("indoor_entry", indoorEntry));
        destination.add(NpcRouteStep.walk("indoor_target", target.position()));
        return NpcRouteContext.ready(canonicalLocation, destination);
    }

    private static Vec3 resolveOutdoorDoorForLocation(String canonicalLocation) {
        NpcLocationAnchor anchor = NpcDataRegistry.locationAnchors().get(canonicalLocation);
        if (anchor != null && !anchor.outdoorDoorPoint().isEmpty()) {
            return pointFromConfigStrict(anchor.outdoorDoorPoint(), null, canonicalLocation);
        }
        return null;
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

        return routePointPosition(obj, Vec3.ZERO);
    }

    private static Vec3 portalTargetPosition(String targetId) {
        if (targetId == null || targetId.isBlank()) {
            return null;
        }
        ensurePortalTargetsInitialized();
        return InteriorPortalRegistry.resolve(targetId)
            .map(target -> new Vec3(target.x(), target.y(), target.z()))
            .orElse(null);
    }

    private static String exitPortalTargetId(String enterTargetId) {
        String trimmed = enterTargetId == null ? "" : enterTargetId.trim().toLowerCase(Locale.ROOT);
        if (trimmed.endsWith("_enter")) {
            return trimmed.substring(0, trimmed.length() - "_enter".length()) + "_exit";
        }
        return "";
    }

    private static void ensurePortalTargetsInitialized() {
        if (portalTargetsInitialized) {
            return;
        }
        InteriorSubspaceManager.allStructures();
        portalTargetsInitialized = true;
    }

    /**
     * Checks if the given route point has {@code "indoor": true} in npc_route_points.json.
     */
    private static boolean isPointIndoor(String pointId) {
        if (pointId != null) {
            String normalizedPointId = pointId.trim().toLowerCase(Locale.ROOT);
            for (Map.Entry<String, NpcLocationAnchor> entry : NpcDataRegistry.locationAnchors().entrySet()) {
                NpcLocationAnchor anchor = entry.getValue();
                if (pointId.equalsIgnoreCase(anchor.indoorEntryPoint())) {
                    return true;
                }
                String location = entry.getKey().trim().toLowerCase(Locale.ROOT);
                if (normalizedPointId.equals(location + "_indoor_entry")
                    || normalizedPointId.equals(location + "_inner_entry")) {
                    return true;
                }
            }
        }

        JsonObject root = routePointsRoot();
        if (root == null || !root.has("points") || !root.get("points").isJsonObject()) {
            return false;
        }
        JsonElement element = root.getAsJsonObject("points").get(pointId);
        if (element == null || !element.isJsonObject()) {
            return false;
        }
        JsonObject obj = element.getAsJsonObject();
        return obj.has("indoor") && obj.get("indoor").getAsBoolean();
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

    }

    private static String safe(String value) {
        return value == null || value.isBlank() ? "<none>" : value;
    }
}
