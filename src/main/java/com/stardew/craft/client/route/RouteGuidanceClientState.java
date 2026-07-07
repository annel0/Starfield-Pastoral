package com.stardew.craft.client.route;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public final class RouteGuidanceClientState {
    private static ActiveRoute activeRoute;

    private RouteGuidanceClientState() {
    }

    public static void start(String routeId, List<BlockPos> points, int durationTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        long now = minecraft.level == null ? 0L : minecraft.level.getGameTime();
        activeRoute = new ActiveRoute(
            routeId == null ? "" : routeId,
            List.copyOf(points == null ? List.of() : points),
            now + Math.max(1, durationTicks)
        );
    }

    public static ActiveRoute active(long gameTime) {
        if (activeRoute == null || gameTime > activeRoute.expiresAtGameTime() || activeRoute.points().size() < 2) {
            activeRoute = null;
            return null;
        }
        return activeRoute;
    }

    public static void clear() {
        activeRoute = null;
    }

    public static double remainingDistance(Vec3 playerPos, List<BlockPos> points) {
        return Math.max(0.0D, totalDistance(points) - progressDistance(playerPos, points));
    }

    public static double progressDistance(Vec3 playerPos, List<BlockPos> points) {
        if (playerPos == null || points == null || points.size() < 2) {
            return 0.0D;
        }
        double bestDistanceSq = Double.MAX_VALUE;
        double bestProgress = 0.0D;
        double walked = 0.0D;
        Vec3 playerFlat = new Vec3(playerPos.x, 0.0D, playerPos.z);

        for (int i = 0; i + 1 < points.size(); i++) {
            Vec3 a = flatCenter(points.get(i));
            Vec3 b = flatCenter(points.get(i + 1));
            Vec3 ab = b.subtract(a);
            double lenSq = ab.lengthSqr();
            double t = lenSq <= 0.0001D ? 0.0D : playerFlat.subtract(a).dot(ab) / lenSq;
            t = Math.max(0.0D, Math.min(1.0D, t));
            Vec3 projected = a.add(ab.scale(t));
            double distSq = projected.distanceToSqr(playerFlat);
            double segmentLength = Math.sqrt(lenSq);
            if (distSq < bestDistanceSq) {
                bestDistanceSq = distSq;
                bestProgress = walked + segmentLength * t;
            }
            walked += segmentLength;
        }
        return bestProgress;
    }

    public static double totalDistance(List<BlockPos> points) {
        if (points == null || points.size() < 2) {
            return 0.0D;
        }
        double walked = 0.0D;
        for (int i = 0; i + 1 < points.size(); i++) {
            walked += flatCenter(points.get(i)).distanceTo(flatCenter(points.get(i + 1)));
        }
        return walked;
    }

    private static Vec3 flatCenter(BlockPos pos) {
        return new Vec3(pos.getX() + 0.5D, 0.0D, pos.getZ() + 0.5D);
    }

    public record ActiveRoute(String routeId, List<BlockPos> points, long expiresAtGameTime) {
    }
}
