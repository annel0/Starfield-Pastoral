package com.stardew.craft.client.route;

import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.List;

public final class RouteEditorClientState {
    private static String routeId = "spirit_eve_maze_exit";
    private static List<BlockPos> points = List.of();

    private RouteEditorClientState() {
    }

    public static void replace(String newRouteId, List<BlockPos> newPoints) {
        routeId = newRouteId == null || newRouteId.isBlank() ? "spirit_eve_maze_exit" : newRouteId;
        points = List.copyOf(newPoints == null ? List.of() : newPoints);
    }

    public static String routeId() {
        return routeId;
    }

    public static List<BlockPos> points() {
        return points;
    }

    public static String exportJsonLike(String overrideRouteId) {
        String id = overrideRouteId == null || overrideRouteId.isBlank() ? routeId : overrideRouteId.trim();
        StringBuilder out = new StringBuilder();
        out.append("\"").append(id).append("\": [\n");
        List<BlockPos> copy = new ArrayList<>(points);
        for (int i = 0; i < copy.size(); i++) {
            BlockPos p = copy.get(i);
            out.append("  { \"x\": ").append(p.getX())
                .append(", \"y\": ").append(p.getY())
                .append(", \"z\": ").append(p.getZ()).append(" }");
            if (i + 1 < copy.size()) {
                out.append(',');
            }
            out.append('\n');
        }
        out.append(']');
        return out.toString();
    }
}
