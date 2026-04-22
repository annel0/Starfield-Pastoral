package com.stardew.craft.npc.runtime;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

/**
 * NPC pathfinding terrain helpers.
 *
 * <p>All actual pathfinding is delegated to vanilla MC's {@code GroundPathNavigation}
 * (via {@code npc.getNavigation().moveTo(target)}). This class provides terrain
 * validation utilities used by debug commands and destination checks.</p>
 *
 * <p>Architecture (Citizens2-inspired): the movement service calls
 * {@code moveTo(stepTarget)} directly — no intermediate waypoints. Vanilla nav
 * handles terrain, elevation, doors, slabs, stairs, etc. natively. Periodic
 * re-pathing and stuck detection are managed by NpcCentralMovementService.</p>
 */
@SuppressWarnings("null")
public final class NpcPathfinder {

    /** Cardinal direction offsets: N/E/S/W. Used by canStand width checks. */
    private static final int[][] DIR4 = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

    private NpcPathfinder() {
    }

    /** Reset state (call on server context change). */
    public static void resetState() {
        // No mutable state.
    }

    // ──── Terrain helpers (used by NpcDebugCommand & movement service) ────

    public static BlockPos nearestWalkable(ServerLevel level, Vec3 raw) {
        int x = (int) Math.floor(raw.x);
        int z = (int) Math.floor(raw.z);
        int baseY = (int) Math.floor(raw.y);

        net.minecraft.core.BlockPos.MutableBlockPos probe = new net.minecraft.core.BlockPos.MutableBlockPos();
        if (level.isLoaded(probe.set(x, baseY, z))) {
            BlockPos direct = resolveColumnStandNearY(level, x, z, baseY);
            if (direct != null) {
                return direct;
            }
        }

        for (int r = 1; r <= 10; r++) {
            for (int ddx = -r; ddx <= r; ddx++) {
                BlockPos c = checkWalkableAt(level, probe, x + ddx, z - r, baseY);
                if (c != null) return c;
                c = checkWalkableAt(level, probe, x + ddx, z + r, baseY);
                if (c != null) return c;
            }
            for (int dz = -r + 1; dz <= r - 1; dz++) {
                BlockPos c = checkWalkableAt(level, probe, x - r, z + dz, baseY);
                if (c != null) return c;
                c = checkWalkableAt(level, probe, x + r, z + dz, baseY);
                if (c != null) return c;
            }
        }
        return null;
    }

    private static BlockPos checkWalkableAt(ServerLevel level, net.minecraft.core.BlockPos.MutableBlockPos probe, int nx, int nz, int baseY) {
        if (!level.isLoaded(probe.set(nx, baseY, nz))) return null;
        return resolveColumnStandNearY(level, nx, nz, baseY);
    }

    private static BlockPos resolveColumnStandNearY(ServerLevel level, int x, int z, int preferredY) {
        for (int y = preferredY + 2; y >= preferredY - 4; y--) {
            BlockPos layered = new BlockPos(x, y, z);
            if (canStand(level, layered)) {
                return layered;
            }
        }

        int surfaceY = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
        BlockPos surface = new BlockPos(x, surfaceY, z);
        if (canStand(level, surface)) {
            return surface;
        }
        return null;
    }

    public static boolean canStand(ServerLevel level, BlockPos pos) {
        BlockPos abovePos = pos.above();
        BlockPos belowPos = pos.below();
        BlockState feet = level.getBlockState(pos);
        BlockState head = level.getBlockState(abovePos);
        BlockState below = level.getBlockState(belowPos);
        if (!feet.getCollisionShape(level, pos).isEmpty() && !isPathDoorPassable(feet)) {
            return false;
        }
        if (!head.getCollisionShape(level, abovePos).isEmpty() && !isPathDoorPassable(head)) {
            return false;
        }
        if (isBarrierBlock(below)) {
            return false;
        }
        if (below.getCollisionShape(level, belowPos).isEmpty()) {
            return false;
        }
        if (hasAdjacentBlockingShapeLevel(level, pos) || hasAdjacentBlockingShapeLevel(level, abovePos)) {
            return false;
        }
        return true;
    }

    private static boolean hasAdjacentBlockingShapeLevel(ServerLevel level, BlockPos pos) {
        for (int[] d : DIR4) {
            BlockPos neighbor = pos.offset(d[0], 0, d[1]);
            BlockState neighborState = level.getBlockState(neighbor);
            if (isPathDoorPassable(neighborState)) continue;
            net.minecraft.world.phys.shapes.VoxelShape shape = neighborState.getCollisionShape(level, neighbor);
            if (shape.isEmpty()) continue;
            net.minecraft.world.phys.AABB bounds = shape.bounds();
            if (d[0] == 1 && bounds.minX < 0.3) return true;
            if (d[0] == -1 && bounds.maxX > 0.7) return true;
            if (d[1] == 1 && bounds.minZ < 0.3) return true;
            if (d[1] == -1 && bounds.maxZ > 0.7) return true;
        }
        return false;
    }

    static boolean isBarrierBlock(BlockState state) {
        net.minecraft.world.level.block.Block block = state.getBlock();
        if (block instanceof net.minecraft.world.level.block.FenceBlock) return true;
        if (block instanceof net.minecraft.world.level.block.FenceGateBlock) return true;
        if (block instanceof net.minecraft.world.level.block.WallBlock) return true;
        if (block instanceof net.minecraft.world.level.block.IronBarsBlock) return true;
        return false;
    }

    static boolean isPathDoorPassable(BlockState state) {
        if (!(state.getBlock() instanceof DoorBlock)) {
            return false;
        }
        return state.getBlock() != Blocks.IRON_DOOR;
    }
}
