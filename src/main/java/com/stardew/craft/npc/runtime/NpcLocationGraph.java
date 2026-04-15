package com.stardew.craft.npc.runtime;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.stardew.craft.StardewCraft;
import com.stardew.craft.npc.data.NpcDataRegistry;
import com.stardew.craft.npc.data.NpcLocationAnchor;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;

/**
 * BFS-based location connectivity graph.
 * <p>
 * Data is loaded from {@code npc/events/location_graph.json}. Each edge describes an
 * outdoor walk or walk+warp transition between two locations. The graph is bidirectional.
 * <p>
 * When {@link NpcRoutePlanner} cannot find a profile route for an NPC, it falls back here
 * to automatically stitch a multi-hop route from current location to destination.
 */
public final class NpcLocationGraph {

    /** One directed edge in the graph. */
    record Edge(String from, String to, String viaOutdoor, String viaIndoor, String mode) {
    }

    /** BFS result: ordered list of edges from source to destination. */
    record GraphRoute(List<Edge> edges) {
        static final GraphRoute EMPTY = new GraphRoute(Collections.emptyList());
    }

    private static volatile Map<String, List<Edge>> adjacency = Collections.emptyMap();

    private NpcLocationGraph() {
    }

    // ---- loading ----

    static void reload() {
        JsonObject root = NpcDataRegistry.events().get("location_graph");
        if (root == null || !root.has("edges") || !root.get("edges").isJsonArray()) {
            adjacency = Collections.emptyMap();
            return;
        }

        Map<String, List<Edge>> adj = new HashMap<>();
        JsonArray arr = root.getAsJsonArray("edges");
        for (JsonElement el : arr) {
            if (!el.isJsonObject()) continue;
            JsonObject obj = el.getAsJsonObject();
            String from = str(obj, "from");
            String to = str(obj, "to");
            if (from.isEmpty() || to.isEmpty()) continue;

            String viaOutdoor = str(obj, "via_outdoor");
            String viaIndoor = str(obj, "via_indoor");
            String mode = str(obj, "mode");
            if (mode.isEmpty()) mode = "walk";

            Edge forward = new Edge(from, to, viaOutdoor, viaIndoor, mode);
            Edge reverse = new Edge(to, from, viaOutdoor, viaIndoor, mode);

            adj.computeIfAbsent(from, k -> new ArrayList<>()).add(forward);
            adj.computeIfAbsent(to, k -> new ArrayList<>()).add(reverse);
        }

        adjacency = Collections.unmodifiableMap(adj);
        StardewCraft.LOGGER.info("NpcLocationGraph loaded: {} locations, {} directed edges.",
            adj.size(), adj.values().stream().mapToInt(List::size).sum());
    }

    // ---- BFS shortest-path ----

    static GraphRoute findRoute(String fromLocation, String toLocation) {
        if (fromLocation == null || toLocation == null) return GraphRoute.EMPTY;
        String src = canonical(fromLocation);
        String dst = canonical(toLocation);
        if (src.equals(dst) || src.isEmpty() || dst.isEmpty()) return GraphRoute.EMPTY;
        if (adjacency.isEmpty()) return GraphRoute.EMPTY;

        // BFS
        Queue<String> queue = new ArrayDeque<>();
        Map<String, Edge> cameFrom = new HashMap<>();
        queue.add(src);
        cameFrom.put(src, null);

        while (!queue.isEmpty()) {
            String current = queue.poll();
            if (current.equals(dst)) break;
            List<Edge> neighbors = adjacency.getOrDefault(current, Collections.emptyList());
            for (Edge edge : neighbors) {
                String next = edge.from.equals(current) ? edge.to : edge.from;
                if (cameFrom.containsKey(next)) continue;
                cameFrom.put(next, edge);
                queue.add(next);
            }
        }

        if (!cameFrom.containsKey(dst)) return GraphRoute.EMPTY;

        // Reconstruct path
        List<Edge> path = new ArrayList<>();
        String cursor = dst;
        while (!cursor.equals(src)) {
            Edge edge = cameFrom.get(cursor);
            if (edge == null) break;
            path.add(edge);
            cursor = edge.from.equals(cursor) ? edge.to : edge.from;
        }
        Collections.reverse(path);
        return new GraphRoute(path);
    }

    // ---- route step conversion ----

    /**
     * Convert a graph route into a sequence of {@link NpcRoutePlanner.NpcRouteStep}s.
     * For each hop: walk to via_outdoor (if set), warp to via_indoor (if set),
     * walk to destination anchor.
     */
    static List<NpcRoutePlanner.NpcRouteStep> toRouteSteps(GraphRoute route, Vec3 finalTarget) {
        if (route == null || route.edges.isEmpty()) return Collections.emptyList();

        List<NpcRoutePlanner.NpcRouteStep> steps = new ArrayList<>();

        for (int i = 0; i < route.edges.size(); i++) {
            Edge edge = route.edges.get(i);
            boolean isLast = (i == route.edges.size() - 1);

            // Walk to outdoor transition point if defined
            if (!edge.viaOutdoor.isEmpty()) {
                Vec3 outdoor = NpcRoutePlanner.pointFromConfig(edge.viaOutdoor, null);
                if (outdoor != null) {
                    steps.add(NpcRoutePlanner.NpcRouteStep.walk("graph_outdoor_" + edge.to, outdoor));
                }
            }

            // Warp to indoor entry if defined
            if (!edge.viaIndoor.isEmpty()) {
                Vec3 indoor = NpcRoutePlanner.pointFromConfig(edge.viaIndoor, null);
                if (indoor != null) {
                    steps.add(NpcRoutePlanner.NpcRouteStep.warp("graph_indoor_" + edge.to, indoor));
                }
            }

            // For intermediate hops without warp, walk to the destination location anchor
            if (!isLast && edge.viaIndoor.isEmpty()) {
                NpcLocationAnchor anchor = NpcDataRegistry.locationAnchors().get(edge.to);
                if (anchor != null) {
                    steps.add(NpcRoutePlanner.NpcRouteStep.walk("graph_hop_" + edge.to,
                        new Vec3(anchor.x(), anchor.y(), anchor.z())));
                }
            }
        }

        // Final target
        if (finalTarget != null) {
            steps.add(NpcRoutePlanner.NpcRouteStep.walk("graph_final_target", finalTarget));
        }

        return steps;
    }

    /** Check if the graph has any edges loaded. */
    static boolean isAvailable() {
        return !adjacency.isEmpty();
    }

    private static String canonical(String loc) {
        if (loc == null) return "";
        String raw = loc.trim().toLowerCase(Locale.ROOT);
        return NpcDataRegistry.locationAliases().getOrDefault(raw, raw);
    }

    private static String str(JsonObject obj, String key) {
        if (!obj.has(key) || !obj.get(key).isJsonPrimitive()) return "";
        String v = obj.get(key).getAsString();
        return v == null ? "" : v.trim().toLowerCase(Locale.ROOT);
    }
}
