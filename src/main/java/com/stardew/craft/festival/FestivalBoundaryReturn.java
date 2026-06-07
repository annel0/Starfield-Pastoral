package com.stardew.craft.festival;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

final class FestivalBoundaryReturn {
    static final double DEFAULT_MARGIN = 1.5D;

    private FestivalBoundaryReturn() {
    }

    static Vec3 findSafeInside(ServerPlayer player, AABB bounds, Vec3 preferred, Vec3 fallback) {
        Vec3 target = findSafeInside(player, bounds, preferred, DEFAULT_MARGIN);
        if (target != null) {
            return target;
        }
        return findSafeInside(player, bounds, fallback, DEFAULT_MARGIN);
    }

    static Vec3 findSafeOutside(ServerPlayer player, AABB bounds, Vec3 preferred) {
        if (isSafeOutside(player, bounds, preferred, DEFAULT_MARGIN)) {
            return preferred;
        }
        return findSafeOutside(player, bounds, pushOutside(bounds, preferred == null ? player.position() : preferred, DEFAULT_MARGIN), DEFAULT_MARGIN);
    }

    static Vec3 pushInside(AABB bounds, Vec3 current) {
        return pushInside(bounds, current, DEFAULT_MARGIN);
    }

    static Vec3 pushOutside(AABB bounds, Vec3 current) {
        return pushOutside(bounds, current, DEFAULT_MARGIN);
    }

    private static Vec3 findSafeInside(ServerPlayer player, AABB bounds, Vec3 preferred, double margin) {
        if (isSafeInside(player, bounds, preferred, margin)) {
            return preferred;
        }
        Vec3 base = pushInside(bounds, preferred == null ? player.position() : preferred, margin);
        BlockPos center = BlockPos.containing(base.x, base.y, base.z);
        for (int radius = 0; radius <= 8; radius++) {
            for (int dy = -3; dy <= 4; dy++) {
                for (int dx = -radius; dx <= radius; dx++) {
                    for (int dz = -radius; dz <= radius; dz++) {
                        if (Math.max(Math.abs(dx), Math.abs(dz)) != radius) {
                            continue;
                        }
                        Vec3 candidate = pushInside(bounds, new Vec3(center.getX() + dx + 0.5D, center.getY() + dy, center.getZ() + dz + 0.5D), margin);
                        if (isSafeInside(player, bounds, candidate, margin)) {
                            return candidate;
                        }
                    }
                }
            }
        }
        return null;
    }

    private static Vec3 findSafeOutside(ServerPlayer player, AABB bounds, Vec3 preferred, double margin) {
        if (isSafeOutside(player, bounds, preferred, margin)) {
            return preferred;
        }
        BlockPos center = BlockPos.containing(preferred.x, preferred.y, preferred.z);
        for (int radius = 0; radius <= 8; radius++) {
            for (int dy = -3; dy <= 4; dy++) {
                for (int dx = -radius; dx <= radius; dx++) {
                    for (int dz = -radius; dz <= radius; dz++) {
                        if (Math.max(Math.abs(dx), Math.abs(dz)) != radius) {
                            continue;
                        }
                        Vec3 candidate = new Vec3(center.getX() + dx + 0.5D, center.getY() + dy, center.getZ() + dz + 0.5D);
                        if (isSafeOutside(player, bounds, candidate, margin)) {
                            return candidate;
                        }
                    }
                }
            }
        }
        return null;
    }

    private static boolean isSafeInside(ServerPlayer player, AABB bounds, Vec3 target, double margin) {
        return player != null
            && bounds != null
            && target != null
            && bounds.contains(target)
            && isInsideSafeMargin(bounds, target, margin)
            && canStandAt(player, target);
    }

    private static boolean isSafeOutside(ServerPlayer player, AABB bounds, Vec3 target, double margin) {
        return player != null
            && bounds != null
            && target != null
            && isOutsideSafeMargin(bounds, target, margin)
            && canStandAt(player, target);
    }

    private static boolean canStandAt(ServerPlayer player, Vec3 target) {
        BlockPos feet = BlockPos.containing(target.x, target.y, target.z);
        BlockPos head = feet.above();
        return player.serverLevel().getFluidState(feet).isEmpty()
            && player.serverLevel().getFluidState(head).isEmpty()
            && player.serverLevel().getFluidState(feet.below()).isEmpty()
            && !player.serverLevel().getBlockState(feet.below()).isAir()
            && player.serverLevel().noCollision(player, player.getBoundingBox().move(target.subtract(player.position())));
    }

    private static boolean isInsideSafeMargin(AABB bounds, Vec3 position, double margin) {
        return position.x >= safeMin(bounds.minX, bounds.maxX, margin)
            && position.x <= safeMax(bounds.minX, bounds.maxX, margin)
            && position.z >= safeMin(bounds.minZ, bounds.maxZ, margin)
            && position.z <= safeMax(bounds.minZ, bounds.maxZ, margin);
    }

    private static boolean isOutsideSafeMargin(AABB bounds, Vec3 position, double margin) {
        return position.x <= bounds.minX - margin
            || position.x >= bounds.maxX + margin
            || position.z <= bounds.minZ - margin
            || position.z >= bounds.maxZ + margin;
    }

    private static Vec3 pushInside(AABB bounds, Vec3 current, double margin) {
        return new Vec3(
            clamp(current.x, safeMin(bounds.minX, bounds.maxX, margin), safeMax(bounds.minX, bounds.maxX, margin)),
            clamp(current.y, bounds.minY + 0.1D, bounds.maxY - 1.0D),
            clamp(current.z, safeMin(bounds.minZ, bounds.maxZ, margin), safeMax(bounds.minZ, bounds.maxZ, margin))
        );
    }

    private static Vec3 pushOutside(AABB bounds, Vec3 current, double margin) {
        double x = current.x;
        double y = current.y;
        double z = current.z;
        double left = Math.abs(x - bounds.minX);
        double right = Math.abs(bounds.maxX - x);
        double north = Math.abs(z - bounds.minZ);
        double south = Math.abs(bounds.maxZ - z);
        double nearest = Math.min(Math.min(left, right), Math.min(north, south));
        if (nearest == left) {
            x = bounds.minX - margin;
        } else if (nearest == right) {
            x = bounds.maxX + margin;
        } else if (nearest == north) {
            z = bounds.minZ - margin;
        } else {
            z = bounds.maxZ + margin;
        }
        return new Vec3(x, y, z);
    }

    private static double safeMin(double min, double max, double margin) {
        return min + margin <= max - margin ? min + margin : (min + max) * 0.5D;
    }

    private static double safeMax(double min, double max, double margin) {
        return min + margin <= max - margin ? max - margin : (min + max) * 0.5D;
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
