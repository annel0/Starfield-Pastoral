package com.stardew.craft.npc.runtime;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * NPC A* pathfinding engine extracted from NpcCentralMovementService.
 * Supports both synchronous and asynchronous path computation.
 */
@SuppressWarnings("null")
public final class NpcPathfinder {

    static final int ASTAR_MIN_VISITS = 2_000;
    static final int ASTAR_MAX_VISITS = 8_000;
    private static final int ASTAR_WARN_INTERVAL_TICKS = 200;
    static final int ASTAR_MAX_CALLS_PER_TICK = 2;

    private static long lastAstarWarnTick = Long.MIN_VALUE;
    private static long astarCallBudgetTick = Long.MIN_VALUE;
    private static int astarCallsThisTick = 0;

    private static final ExecutorService ASYNC_EXECUTOR =
            Executors.newFixedThreadPool(2, r -> {
                Thread t = new Thread(r, "NPC-Pathfinder");
                t.setDaemon(true);
                return t;
            });

    private static final int[][] DIR8 = {
        {1, 0}, {-1, 0}, {0, 1}, {0, -1},
        {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
    };

    /** Temporary collision penalties: packed BlockPos -> expiry gameTime. */
    private static final Map<Long, Long> COLLISION_PENALTIES = new HashMap<>();
    private static final double COLLISION_PENALTY_COST = 8.0D;
    private static final long COLLISION_PENALTY_DURATION_TICKS = 200;

    private NpcPathfinder() {
    }

    /** Reset per-tick budget and warn state (call on server context change). */
    public static void resetState() {
        lastAstarWarnTick = Long.MIN_VALUE;
        astarCallBudgetTick = Long.MIN_VALUE;
        astarCallsThisTick = 0;
        COLLISION_PENALTIES.clear();
    }

    /**
     * Register a collision penalty at the given position.
     * Future A* calls will heavily penalize paths passing through this block.
     */
    public static void addCollisionPenalty(ServerLevel level, Vec3 pos) {
        if (level == null || pos == null) return;
        long key = BlockPos.asLong((int) Math.floor(pos.x), (int) Math.floor(pos.y), (int) Math.floor(pos.z));
        COLLISION_PENALTIES.put(key, level.getGameTime() + COLLISION_PENALTY_DURATION_TICKS);
    }

    /**
     * Check and consume one A* call from the per-tick budget.
     * Returns true if a call is allowed this tick.
     */
    public static boolean allowAstarCall(ServerLevel level) {
        if (level == null) {
            return false;
        }
        long now = level.getGameTime();
        if (astarCallBudgetTick != now) {
            astarCallBudgetTick = now;
            astarCallsThisTick = 0;
        }
        if (astarCallsThisTick >= ASTAR_MAX_CALLS_PER_TICK) {
            return false;
        }
        astarCallsThisTick++;
        return true;
    }

    /**
     * Synchronous A* path search. Must be called on the server thread.
     */
    public static List<Vec3> planPath(ServerLevel level, Vec3 rawStart, Vec3 rawGoal) {
        BlockPos start = nearestWalkable(level, rawStart);
        BlockPos goal = nearestWalkable(level, rawGoal);
        if (start == null || goal == null) {
            return new ArrayList<>();
        }

        int maxVisits = computeAstarVisitBudget(start, goal);

        PriorityQueue<Node> open = new PriorityQueue<>(Comparator.comparingDouble(n -> n.f));
        Map<Long, Node> bestByKey = new HashMap<>();
        Set<Long> closed = new HashSet<>();

        Node startNode = new Node(start, null, 0.0D, heuristic(start, goal));
        open.add(startNode);
        bestByKey.put(pack(start), startNode);

        int visits = 0;
        Node endNode = null;
        while (!open.isEmpty() && visits++ < maxVisits) {
            Node current = open.poll();
            long currentKey = pack(current.pos);
            if (!closed.add(currentKey)) {
                continue;
            }

            if (current.pos.distSqr(goal) <= 1.0D) {
                endNode = current;
                break;
            }

            for (int[] dir : DIR8) {
                BlockPos nextPos = stepTo(level, current.pos, dir[0], dir[1]);
                if (nextPos == null) {
                    continue;
                }

                long key = pack(nextPos);
                if (closed.contains(key)) {
                    continue;
                }

                double g = current.g + transitionCost(level, current.pos, nextPos, dir[0], dir[1]);
                Node best = bestByKey.get(key);
                if (best != null && g >= best.g) {
                    continue;
                }

                Node next = new Node(nextPos, current, g, g + heuristic(nextPos, goal));
                bestByKey.put(key, next);
                open.add(next);
            }
        }

        if (endNode == null) {
            maybeWarnAstarBudget(level, start, goal, visits, maxVisits);
            return new ArrayList<>();
        }

        List<Vec3> path = new ArrayList<>();
        Set<Integer> doorIndices = new HashSet<>();
        Node cursor = endNode;
        while (cursor != null) {
            Vec3 waypoint = isDoorPosition(level, cursor.pos)
                ? computeDoorWaypoint(level, cursor.pos)
                : Vec3.atCenterOf(cursor.pos);
            path.add(0, waypoint);
            if (isDoorPosition(level, cursor.pos)) {
                doorIndices.add(0); // will be adjusted below
            }
            cursor = cursor.parent;
        }
        // Fix door indices: we inserted at 0 each time, so indices shifted.
        // Re-scan after building the full path.
        doorIndices.clear();
        for (int i = 0; i < path.size(); i++) {
            BlockPos bp = BlockPos.containing(path.get(i));
            if (isDoorPosition(level, bp)) {
                doorIndices.add(i);
            }
        }

        return smoothPath(path, doorIndices);
    }

    /**
     * Request an asynchronous path computation. The returned future completes
     * on the pathfinder thread pool; the caller should check completion on
     * subsequent server ticks.
     *
     * Because block state reads are NOT thread-safe, this snapshots a bounding
     * region of block data on the calling (server) thread, then runs A* against
     * the snapshot off-thread. This is safe but slightly more expensive to start.
     */
    public static CompletableFuture<List<Vec3>> planPathAsync(ServerLevel level, Vec3 rawStart, Vec3 rawGoal) {
        // Snapshot block data on the server thread before handing off.
        BlockPos start = nearestWalkable(level, rawStart);
        BlockPos goal = nearestWalkable(level, rawGoal);
        if (start == null || goal == null) {
            return CompletableFuture.completedFuture(new ArrayList<>());
        }

        // Build a snapshot of the relevant region.
        BlockSnapshot snapshot = BlockSnapshot.capture(level, start, goal);

        return CompletableFuture.supplyAsync(() -> planPathOnSnapshot(snapshot, start, goal), ASYNC_EXECUTOR);
    }

    // ──── Snapshot-based A* (thread-safe) ────

    private static List<Vec3> planPathOnSnapshot(BlockSnapshot snapshot, BlockPos start, BlockPos goal) {
        int maxVisits = computeAstarVisitBudget(start, goal);

        PriorityQueue<Node> open = new PriorityQueue<>(Comparator.comparingDouble(n -> n.f));
        Map<Long, Node> bestByKey = new HashMap<>();
        Set<Long> closed = new HashSet<>();

        Node startNode = new Node(start, null, 0.0D, heuristic(start, goal));
        open.add(startNode);
        bestByKey.put(pack(start), startNode);

        int visits = 0;
        Node endNode = null;
        while (!open.isEmpty() && visits++ < maxVisits) {
            Node current = open.poll();
            long currentKey = pack(current.pos);
            if (!closed.add(currentKey)) {
                continue;
            }

            if (current.pos.distSqr(goal) <= 1.0D) {
                endNode = current;
                break;
            }

            for (int[] dir : DIR8) {
                BlockPos nextPos = stepToSnapshot(snapshot, current.pos, dir[0], dir[1]);
                if (nextPos == null) {
                    continue;
                }

                long key = pack(nextPos);
                if (closed.contains(key)) {
                    continue;
                }

                double g = current.g + transitionCostSnapshot(snapshot, current.pos, nextPos, dir[0], dir[1]);
                Node best = bestByKey.get(key);
                if (best != null && g >= best.g) {
                    continue;
                }

                Node next = new Node(nextPos, current, g, g + heuristic(nextPos, goal));
                bestByKey.put(key, next);
                open.add(next);
            }
        }

        if (endNode == null) {
            return new ArrayList<>();
        }

        List<Vec3> path = new ArrayList<>();
        Node cursor = endNode;
        while (cursor != null) {
            Vec3 waypoint = isDoorPositionSnapshot(snapshot, cursor.pos)
                ? computeDoorWaypointSnapshot(snapshot, cursor.pos)
                : Vec3.atCenterOf(cursor.pos);
            path.add(0, waypoint);
            cursor = cursor.parent;
        }
        // Identify door indices for smooth-path preservation.
        Set<Integer> doorIndices = new HashSet<>();
        for (int i = 0; i < path.size(); i++) {
            BlockPos bp = BlockPos.containing(path.get(i));
            if (isDoorPositionSnapshot(snapshot, bp)) {
                doorIndices.add(i);
            }
        }

        return smoothPath(path, doorIndices);
    }

    // ──── Terrain helpers (ServerLevel - main thread) ────

    static BlockPos nearestWalkable(ServerLevel level, Vec3 raw) {
        int x = (int) Math.floor(raw.x);
        int z = (int) Math.floor(raw.z);
        int baseY = (int) Math.floor(raw.y);

        BlockPos direct = resolveColumnStandNearY(level, x, z, baseY);
        if (direct != null) {
            return direct;
        }

        for (int r = 1; r <= 10; r++) {
            for (int dx = -r; dx <= r; dx++) {
                for (int dz = -r; dz <= r; dz++) {
                    int nx = x + dx;
                    int nz = z + dz;
                    BlockPos candidate = resolveColumnStandNearY(level, nx, nz, baseY);
                    if (candidate != null) {
                        return candidate;
                    }
                }
            }
        }
        return null;
    }

    private static BlockPos stepTo(ServerLevel level, BlockPos from, int dx, int dz) {
        int nx = from.getX() + dx;
        int nz = from.getZ() + dz;

        if (dx != 0 && dz != 0) {
            // Block diagonal movement if any adjacent block has a door
            // (diagonal through 1-block doors causes entity to clip doorframe).
            BlockPos sideA = resolveColumnStandNearY(level, from.getX() + dx, from.getZ(), from.getY());
            BlockPos sideB = resolveColumnStandNearY(level, from.getX(), from.getZ() + dz, from.getY());
            if (sideA == null || sideB == null || !canStand(level, sideA) || !canStand(level, sideB)) {
                return null;
            }
            // Forbid diagonal near doors to prevent clipping
            if (hasDoorNearby(level, from) || hasDoorNearby(level, sideA) || hasDoorNearby(level, sideB)) {
                return null;
            }
        }

        BlockPos next = resolveColumnStandNearY(level, nx, nz, from.getY());
        if (next == null) {
            return null;
        }
        if (!canStand(level, next)) {
            return null;
        }
        // Width check: entity is 0.6 wide, centered on block center.
        // If any cardinal neighbor at this Y has a solid collision shape,
        // the entity may clip it. Penalize via canStandWide.
        if (!canStandWide(level, next)) {
            return null;
        }
        if (Math.abs(next.getY() - from.getY()) > 2) {
            return null;
        }
        return next;
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

    static boolean canStand(ServerLevel level, BlockPos pos) {
        BlockState feet = level.getBlockState(pos);
        BlockState head = level.getBlockState(pos.above());
        BlockState below = level.getBlockState(pos.below());
        if (!feet.getCollisionShape(level, pos).isEmpty() && !isPathDoorPassable(feet)) {
            return false;
        }
        if (!head.getCollisionShape(level, pos.above()).isEmpty() && !isPathDoorPassable(head)) {
            return false;
        }
        if (isBarrierBlock(below)) {
            return false;
        }
        if (below.getCollisionShape(level, pos.below()).isEmpty()) {
            return false;
        }
        return true;
    }

    /**
     * Width-aware passability: checks that the entity (0.6 wide) won't clip
     * into adjacent solid blocks when walking through this position.
     * Returns false if TWO OPPOSITE cardinal neighbors are both solid
     * (1-block corridor that the 0.6-wide entity cannot traverse).
     */
    private static boolean canStandWide(ServerLevel level, BlockPos pos) {
        // Check X-axis corridor: if both +X and -X at feet level are solid, passage is <1 block
        boolean solidPosX = !level.getBlockState(pos.east()).getCollisionShape(level, pos.east()).isEmpty()
            && !isPathDoorPassable(level.getBlockState(pos.east()));
        boolean solidNegX = !level.getBlockState(pos.west()).getCollisionShape(level, pos.west()).isEmpty()
            && !isPathDoorPassable(level.getBlockState(pos.west()));
        boolean solidPosZ = !level.getBlockState(pos.south()).getCollisionShape(level, pos.south()).isEmpty()
            && !isPathDoorPassable(level.getBlockState(pos.south()));
        boolean solidNegZ = !level.getBlockState(pos.north()).getCollisionShape(level, pos.north()).isEmpty()
            && !isPathDoorPassable(level.getBlockState(pos.north()));
        // If both walls on the same axis, the gap is exactly 1 block — too narrow for 0.6 entity.
        // Allow if at least one axis is clear.
        if (solidPosX && solidNegX && solidPosZ && solidNegZ) {
            return false; // completely boxed in
        }
        return true;
    }

    /** Check if any neighboring block (including self) is a door. */
    private static boolean hasDoorNearby(ServerLevel level, BlockPos pos) {
        if (pos == null) return false;
        for (int[] d : new int[][]{{0,0},{1,0},{-1,0},{0,1},{0,-1}}) {
            BlockPos p = pos.offset(d[0], 0, d[1]);
            BlockState s = level.getBlockState(p);
            if (s.getBlock() instanceof DoorBlock) return true;
            s = level.getBlockState(p.above());
            if (s.getBlock() instanceof DoorBlock) return true;
        }
        return false;
    }

    static double transitionCost(ServerLevel level, BlockPos from, BlockPos to, int dx, int dz) {
        double base = ((dx != 0 && dz != 0) ? 1.41421356237D : 1.0D) + Math.abs(to.getY() - from.getY()) * 0.40D;
        base += edgePenalty(level, to);
        BlockState belowState = level.getBlockState(to.below());
        net.minecraft.world.level.block.Block belowBlock = belowState.getBlock();
        int surfaceCategory = classifySurface(belowBlock);
        return switch (surfaceCategory) {
            case 1 -> base * 0.35D;
            case 2 -> base * 2.20D;
            case 3 -> base * 5.00D;
            default -> base;
        };
    }

    static int classifySurface(net.minecraft.world.level.block.Block block) {
        if (block instanceof net.minecraft.world.level.block.DirtPathBlock) return 1;
        ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(block);
        String path = blockId.getPath();
        if (path.contains("gravel") || path.contains("cobblestone") || path.contains("brick")
            || path.contains("path") || path.contains("road") || path.contains("copper")) return 1;
        if (block instanceof net.minecraft.world.level.block.GrassBlock
            || block instanceof net.minecraft.world.level.block.FarmBlock) return 2;
        if (path.contains("dirt")) return 2;
        if (block instanceof net.minecraft.world.level.block.LiquidBlock) return 3;
        if (block instanceof net.minecraft.world.level.block.LeavesBlock) return 3;
        return 0;
    }

    static boolean isBarrierBlock(BlockState state) {
        net.minecraft.world.level.block.Block block = state.getBlock();
        if (block instanceof net.minecraft.world.level.block.FenceBlock) return true;
        if (block instanceof net.minecraft.world.level.block.FenceGateBlock) return true;
        if (block instanceof net.minecraft.world.level.block.WallBlock) return true;
        if (block instanceof net.minecraft.world.level.block.IronBarsBlock) return true;
        return false;
    }

    static boolean isBarrierAhead(ServerLevel level, Vec3 pos, double vx, double vz) {
        double lookAhead = 0.7D;
        int bx = (int) Math.floor(pos.x + Math.signum(vx) * lookAhead);
        int by = (int) Math.floor(pos.y);
        int bz = (int) Math.floor(pos.z + Math.signum(vz) * lookAhead);
        BlockPos feet = new BlockPos(bx, by, bz);
        return isBarrierBlock(level.getBlockState(feet))
            || isBarrierBlock(level.getBlockState(feet.below()))
            || !level.getBlockState(feet).getCollisionShape(level, feet).isEmpty();
    }

    static boolean isPathDoorPassable(BlockState state) {
        if (!(state.getBlock() instanceof DoorBlock)) {
            return false;
        }
        return state.getBlock() != Blocks.IRON_DOOR;
    }

    private static double edgePenalty(ServerLevel level, BlockPos pos) {
        int blocked = 0;
        int[][] dirs = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        for (int[] d : dirs) {
            BlockPos side = new BlockPos(pos.getX() + d[0], pos.getY(), pos.getZ() + d[1]);
            if (!canStand(level, side)) {
                blocked++;
            }
        }
        // Heavy penalty near walls to avoid tight wall-hugging paths that
        // the entity hitbox cannot physically follow.
        double penalty = blocked * 0.40D;

        // Add temporary collision penalty if this position was marked
        long posKey = pos.asLong();
        Long expiry = COLLISION_PENALTIES.get(posKey);
        if (expiry != null) {
            if (level.getGameTime() < expiry) {
                penalty += COLLISION_PENALTY_COST;
            } else {
                COLLISION_PENALTIES.remove(posKey);
            }
        }

        return penalty;
    }

    // ──── Door waypoint offset helpers ────

    /** Check if feet or head at this position is a non-iron door. */
    private static boolean isDoorPosition(ServerLevel level, BlockPos pos) {
        BlockState feet = level.getBlockState(pos);
        if (feet.getBlock() instanceof DoorBlock && feet.getBlock() != Blocks.IRON_DOOR) return true;
        BlockState head = level.getBlockState(pos.above());
        return head.getBlock() instanceof DoorBlock && head.getBlock() != Blocks.IRON_DOOR;
    }

    private static boolean isDoorPositionSnapshot(BlockSnapshot snap, BlockPos pos) {
        BlockState feet = snap.getBlockState(pos);
        if (feet != null && feet.getBlock() instanceof DoorBlock && feet.getBlock() != Blocks.IRON_DOOR) return true;
        BlockState head = snap.getBlockState(pos.above());
        return head != null && head.getBlock() instanceof DoorBlock && head.getBlock() != Blocks.IRON_DOOR;
    }

    /**
     * Compute an offset waypoint for a door block.
     * When a door is open, the hinge-side has a thin (3/16) collision strip.
     * Entity at block center barely fits → offset 0.2 blocks away from hinge.
     */
    private static Vec3 computeDoorWaypoint(ServerLevel level, BlockPos pos) {
        BlockState doorState = findDoorState(level.getBlockState(pos), level.getBlockState(pos.above()));
        if (doorState == null) return Vec3.atCenterOf(pos);
        return applyDoorOffset(pos, doorState);
    }

    private static Vec3 computeDoorWaypointSnapshot(BlockSnapshot snap, BlockPos pos) {
        BlockState feet = snap.getBlockState(pos);
        BlockState head = snap.getBlockState(pos.above());
        BlockState doorState = findDoorState(feet, head);
        if (doorState == null) return Vec3.atCenterOf(pos);
        return applyDoorOffset(pos, doorState);
    }

    private static BlockState findDoorState(BlockState feet, BlockState head) {
        if (feet != null && feet.getBlock() instanceof DoorBlock && feet.getBlock() != Blocks.IRON_DOOR) return feet;
        if (head != null && head.getBlock() instanceof DoorBlock && head.getBlock() != Blocks.IRON_DOOR) return head;
        return null;
    }

    private static Vec3 applyDoorOffset(BlockPos pos, BlockState doorState) {
        if (!doorState.hasProperty(DoorBlock.FACING) || !doorState.hasProperty(DoorBlock.HINGE)) {
            return Vec3.atCenterOf(pos);
        }
        Direction facing = doorState.getValue(DoorBlock.FACING);
        DoorHingeSide hinge = doorState.getValue(DoorBlock.HINGE);

        // When open, collision strip is on the hinge side of the block.
        // LEFT hinge → strip at facing.getCounterClockWise() edge
        // RIGHT hinge → strip at facing.getClockWise() edge
        // Offset 0.2 blocks AWAY from hinge to maximize clearance.
        Direction hingeDir = (hinge == DoorHingeSide.LEFT)
            ? facing.getCounterClockWise()
            : facing.getClockWise();

        double offsetX = -hingeDir.getStepX() * 0.2;
        double offsetZ = -hingeDir.getStepZ() * 0.2;
        return new Vec3(pos.getX() + 0.5 + offsetX, pos.getY() + 0.5, pos.getZ() + 0.5 + offsetZ);
    }

    // ──── Snapshot terrain helpers (off-thread safe) ────

    private static BlockPos stepToSnapshot(BlockSnapshot snap, BlockPos from, int dx, int dz) {
        int nx = from.getX() + dx;
        int nz = from.getZ() + dz;

        if (dx != 0 && dz != 0) {
            BlockPos sideA = resolveColumnStandNearYSnapshot(snap, from.getX() + dx, from.getZ(), from.getY());
            BlockPos sideB = resolveColumnStandNearYSnapshot(snap, from.getX(), from.getZ() + dz, from.getY());
            if (sideA == null || sideB == null || !canStandSnapshot(snap, sideA) || !canStandSnapshot(snap, sideB)) {
                return null;
            }
            // Forbid diagonal near doors to prevent clipping (parity with sync stepTo)
            if (hasDoorNearbySnapshot(snap, from)
                || hasDoorNearbySnapshot(snap, sideA)
                || hasDoorNearbySnapshot(snap, sideB)) {
                return null;
            }
        }

        BlockPos next = resolveColumnStandNearYSnapshot(snap, nx, nz, from.getY());
        if (next == null) {
            return null;
        }
        if (!canStandSnapshot(snap, next)) {
            return null;
        }
        // Width check: parity with sync stepTo
        if (!canStandWideSnapshot(snap, next)) {
            return null;
        }
        if (Math.abs(next.getY() - from.getY()) > 2) {
            return null;
        }
        return next;
    }

    private static BlockPos resolveColumnStandNearYSnapshot(BlockSnapshot snap, int x, int z, int preferredY) {
        for (int y = preferredY + 2; y >= preferredY - 4; y--) {
            BlockPos layered = new BlockPos(x, y, z);
            if (canStandSnapshot(snap, layered)) {
                return layered;
            }
        }
        // No heightmap in snapshot; fall back to scan range only.
        return null;
    }

    private static boolean canStandSnapshot(BlockSnapshot snap, BlockPos pos) {
        BlockState feet = snap.getBlockState(pos);
        BlockState head = snap.getBlockState(pos.above());
        BlockState below = snap.getBlockState(pos.below());
        if (feet == null || head == null || below == null) {
            return false; // Out of snapshot bounds
        }
        if (!feet.getCollisionShape(snap.level(), pos).isEmpty() && !isPathDoorPassable(feet)) {
            return false;
        }
        if (!head.getCollisionShape(snap.level(), pos.above()).isEmpty() && !isPathDoorPassable(head)) {
            return false;
        }
        if (isBarrierBlock(below)) {
            return false;
        }
        if (below.getCollisionShape(snap.level(), pos.below()).isEmpty()) {
            return false;
        }
        return true;
    }

    private static double transitionCostSnapshot(BlockSnapshot snap, BlockPos from, BlockPos to, int dx, int dz) {
        double base = ((dx != 0 && dz != 0) ? 1.41421356237D : 1.0D) + Math.abs(to.getY() - from.getY()) * 0.40D;
        base += edgePenaltySnapshot(snap, to);
        BlockState belowState = snap.getBlockState(to.below());
        if (belowState == null) return base;
        int surfaceCategory = classifySurface(belowState.getBlock());
        return switch (surfaceCategory) {
            case 1 -> base * 0.35D;
            case 2 -> base * 2.20D;
            case 3 -> base * 5.00D;
            default -> base;
        };
    }

    /** Width check (snapshot parity with canStandWide). */
    private static boolean canStandWideSnapshot(BlockSnapshot snap, BlockPos pos) {
        boolean solidPosX = isSnapshotSolid(snap, pos.east());
        boolean solidNegX = isSnapshotSolid(snap, pos.west());
        boolean solidPosZ = isSnapshotSolid(snap, pos.south());
        boolean solidNegZ = isSnapshotSolid(snap, pos.north());
        if (solidPosX && solidNegX && solidPosZ && solidNegZ) {
            return false;
        }
        return true;
    }

    private static boolean isSnapshotSolid(BlockSnapshot snap, BlockPos pos) {
        BlockState s = snap.getBlockState(pos);
        if (s == null) return false;
        return !s.getCollisionShape(snap.level(), pos).isEmpty() && !isPathDoorPassable(s);
    }

    /** Door proximity check (snapshot parity with hasDoorNearby). */
    private static boolean hasDoorNearbySnapshot(BlockSnapshot snap, BlockPos pos) {
        if (pos == null) return false;
        for (int[] d : new int[][]{{0,0},{1,0},{-1,0},{0,1},{0,-1}}) {
            BlockPos p = pos.offset(d[0], 0, d[1]);
            BlockState s = snap.getBlockState(p);
            if (s != null && s.getBlock() instanceof DoorBlock) return true;
            s = snap.getBlockState(p.above());
            if (s != null && s.getBlock() instanceof DoorBlock) return true;
        }
        return false;
    }

    private static double edgePenaltySnapshot(BlockSnapshot snap, BlockPos pos) {
        int blocked = 0;
        int[][] dirs = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        for (int[] d : dirs) {
            BlockPos side = new BlockPos(pos.getX() + d[0], pos.getY(), pos.getZ() + d[1]);
            if (!canStandSnapshot(snap, side)) {
                blocked++;
            }
        }
        return blocked * 0.40D;
    }

    // ──── Shared utilities ────

    static int computeAstarVisitBudget(BlockPos start, BlockPos goal) {
        int dx = Math.abs(start.getX() - goal.getX());
        int dz = Math.abs(start.getZ() - goal.getZ());
        int dist = Math.max(dx, dz);
        int budget = ASTAR_MIN_VISITS + (dist * 80);
        if (budget < ASTAR_MIN_VISITS) {
            return ASTAR_MIN_VISITS;
        }
        return Math.min(ASTAR_MAX_VISITS, budget);
    }

    private static void maybeWarnAstarBudget(ServerLevel level,
                                             BlockPos start,
                                             BlockPos goal,
                                             int visits,
                                             int maxVisits) {
        if (level == null) {
            return;
        }
        long now = level.getGameTime();
        if (lastAstarWarnTick != Long.MIN_VALUE && now - lastAstarWarnTick < ASTAR_WARN_INTERVAL_TICKS) {
            return;
        }
        lastAstarWarnTick = now;
        com.stardew.craft.StardewCraft.LOGGER.warn(
            "[NPC_ASTAR_BUDGET] no_path start=({}, {}, {}) goal=({}, {}, {}) visits={} budget={}",
            start.getX(), start.getY(), start.getZ(),
            goal.getX(), goal.getY(), goal.getZ(),
            visits,
            maxVisits
        );
    }

    private static List<Vec3> smoothPath(List<Vec3> path, Set<Integer> doorIndices) {
        if (path.size() <= 2) {
            return path;
        }

        List<Vec3> out = new ArrayList<>();
        out.add(path.get(0));
        for (int i = 1; i < path.size() - 1; i++) {
            // Never skip door waypoints or their immediate neighbors —
            // these must be preserved to maintain correct door approach angle.
            if (doorIndices.contains(i)
                || doorIndices.contains(i - 1)
                || doorIndices.contains(i + 1)) {
                out.add(path.get(i));
                continue;
            }
            Vec3 prev = path.get(i - 1);
            Vec3 cur = path.get(i);
            Vec3 next = path.get(i + 1);
            Vec3 a = cur.subtract(prev);
            Vec3 b = next.subtract(cur);
            if (a.normalize().dot(b.normalize()) > 0.995D) {
                continue;
            }
            out.add(cur);
        }
        out.add(path.get(path.size() - 1));
        return out;
    }

    private static double heuristic(BlockPos a, BlockPos b) {
        int dx = a.getX() - b.getX();
        int dz = a.getZ() - b.getZ();
        return Math.sqrt(dx * dx + dz * dz);
    }

    private static long pack(BlockPos pos) {
        long x = ((long) pos.getX() & 0x3FFFFFFL) << 38;
        long z = ((long) pos.getZ() & 0x3FFFFFFL) << 12;
        long y = (long) pos.getY() & 0xFFFL;
        return x | z | y;
    }

    // ──── Block Snapshot ────

    /**
     * Thread-safe snapshot of block states in a bounded region.
     * Captured on the server thread, read-only on pathfinder threads.
     */
    static final class BlockSnapshot {
        private final ServerLevel serverLevel;
        private final int minX, minY, minZ;
        private final int sizeX, sizeY, sizeZ;
        private final BlockState[] states;

        private BlockSnapshot(ServerLevel serverLevel, int minX, int minY, int minZ, int sizeX, int sizeY, int sizeZ, BlockState[] states) {
            this.serverLevel = serverLevel;
            this.minX = minX;
            this.minY = minY;
            this.minZ = minZ;
            this.sizeX = sizeX;
            this.sizeY = sizeY;
            this.sizeZ = sizeZ;
            this.states = states;
        }

        static BlockSnapshot capture(ServerLevel level, BlockPos start, BlockPos goal) {
            int padding = 12; // Extra blocks around the bounding box for pathfinding exploration
            int yPadding = 6;
            int minX = Math.min(start.getX(), goal.getX()) - padding;
            int maxX = Math.max(start.getX(), goal.getX()) + padding;
            int minZ = Math.min(start.getZ(), goal.getZ()) - padding;
            int maxZ = Math.max(start.getZ(), goal.getZ()) + padding;
            int minY = Math.min(start.getY(), goal.getY()) - yPadding;
            int maxY = Math.max(start.getY(), goal.getY()) + yPadding;

            int sizeX = maxX - minX + 1;
            int sizeY = maxY - minY + 1;
            int sizeZ = maxZ - minZ + 1;
            BlockState[] states = new BlockState[sizeX * sizeY * sizeZ];

            for (int x = 0; x < sizeX; x++) {
                for (int y = 0; y < sizeY; y++) {
                    for (int z = 0; z < sizeZ; z++) {
                        states[x * sizeY * sizeZ + y * sizeZ + z] =
                            level.getBlockState(new BlockPos(minX + x, minY + y, minZ + z));
                    }
                }
            }

            return new BlockSnapshot(level, minX, minY, minZ, sizeX, sizeY, sizeZ, states);
        }

        BlockState getBlockState(BlockPos pos) {
            int x = pos.getX() - minX;
            int y = pos.getY() - minY;
            int z = pos.getZ() - minZ;
            if (x < 0 || x >= sizeX || y < 0 || y >= sizeY || z < 0 || z >= sizeZ) {
                return null;
            }
            return states[x * sizeY * sizeZ + y * sizeZ + z];
        }

        /** Used for collision shape queries which need a level reference. */
        ServerLevel level() {
            return serverLevel;
        }
    }

    // ──── Node ────

    private static final class Node {
        final BlockPos pos;
        final Node parent;
        final double g;
        final double f;

        Node(BlockPos pos, Node parent, double g, double f) {
            this.pos = pos;
            this.parent = parent;
            this.g = g;
            this.f = f;
        }
    }
}
