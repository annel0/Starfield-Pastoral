package com.stardew.craft.animal.service;

import com.stardew.craft.Config;
import com.stardew.craft.block.utility.AutoFeedTroughBlock;
import com.stardew.craft.block.utility.FeedTroughBlock;
import com.stardew.craft.block.utility.HayHopperBlock;
import com.stardew.craft.block.utility.IncubatorBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@SuppressWarnings("null")
public final class BarnManagerValidationService {
    private BarnManagerValidationService() {
    }

    public static ValidationResult validateForTier(ServerLevel level, BlockPos managerPos, int targetTier) {
        TierRequirement requirement = TierRequirement.fromTier(targetTier);
        ScanResult scan = scan(level, managerPos, requirement.minInteriorBlocks());

        List<String> reasons = new ArrayList<>();

        if (!scan.hasInteriorSpace()) {
            reasons.add("未检测到畜棚内部空间（请确保管理器附近有可站立空气空间）");
        }

        if (scan.feedTroughCount() < requirement.feedTroughCount()) {
            reasons.add("普通喂食槽不足：需要 " + requirement.feedTroughCount() + "，当前 " + scan.feedTroughCount());
        }

        if (scan.autoFeedTroughCount() < requirement.autoFeedTroughCount()) {
            reasons.add("自动喂食槽不足：需要 " + requirement.autoFeedTroughCount() + "，当前 " + scan.autoFeedTroughCount());
        }

        if (scan.hayHopperCount() < requirement.hayHopperCount()) {
            reasons.add("喂料斗不足：需要 " + requirement.hayHopperCount() + "，当前 " + scan.hayHopperCount());
        }

        if (scan.incubatorCount() < requirement.incubatorCount()) {
            reasons.add("孵化器不足：需要 " + requirement.incubatorCount() + "，当前 " + scan.incubatorCount());
        }

        if (scan.interiorAirCount() < requirement.minInteriorBlocks()) {
            reasons.add("畜棚内部空间不足：至少 " + requirement.minInteriorBlocks() + " 格，当前 " + scan.interiorAirCount() + " 格");
        }

        if (Config.BARN_REQUIRE_ENCLOSED.get() && !scan.enclosed()) {
            reasons.add("畜棚未封闭（检测到内部空间与扫描边界连通）");
        }

        if (Config.BARN_REQUIRE_DOOR.get() && scan.doorCount() < Config.BARN_MIN_DOOR_COUNT.get()) {
            reasons.add("门/栅栏门不足：需要 " + Config.BARN_MIN_DOOR_COUNT.get() + "，当前 " + scan.doorCount());
        }

        boolean ok = reasons.isEmpty();
        String message = ok ? "校验通过" : String.join("；", reasons);
        return new ValidationResult(ok, targetTier, requirement, scan, message);
    }

    private static ScanResult scan(ServerLevel level, BlockPos managerPos, int minInteriorBlocks) {
        int inferredExtent = Math.max(8, (int) Math.ceil(Math.cbrt(Math.max(1, minInteriorBlocks))) + 2);
        int rangeXZ = Math.max(Config.BARN_SCAN_RANGE_XZ.get(), inferredExtent);
        int rangeUp = Math.max(Config.BARN_SCAN_RANGE_UP.get(), inferredExtent);
        int rangeDown = Math.max(Config.BARN_SCAN_RANGE_DOWN.get(), inferredExtent);

        int scanMinX = managerPos.getX() - rangeXZ;
        int scanMaxX = managerPos.getX() + rangeXZ;
        int scanMinY = managerPos.getY() - rangeDown;
        int scanMaxY = managerPos.getY() + rangeUp;
        int scanMinZ = managerPos.getZ() - rangeXZ;
        int scanMaxZ = managerPos.getZ() + rangeXZ;

        List<BlockPos> startCandidates = new ArrayList<>();
        for (Direction direction : Direction.values()) {
            BlockPos candidate = managerPos.relative(direction);
            if (!withinScan(candidate, scanMinX, scanMaxX, scanMinY, scanMaxY, scanMinZ, scanMaxZ)) {
                continue;
            }
            if (level.getBlockState(candidate).isAir()) {
                startCandidates.add(candidate.immutable());
            }
        }

        if (startCandidates.isEmpty()) {
            return new ScanResult(
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                true,
                0,
                scanMinX,
                scanMinY,
                scanMinZ,
                scanMaxX,
                scanMaxY,
                scanMaxZ,
                Collections.emptySet(),
                Collections.emptySet()
            );
        }

        FloodResult flood = floodInterior(level, startCandidates, scanMinX, scanMaxX, scanMinY, scanMaxY, scanMinZ, scanMaxZ);
        Set<Long> scopedInterior = flood.enclosed ? flood.interiorAirCells : Collections.emptySet();
        FacilityCount facilities = countInteriorFacilities(level, scopedInterior, scanMinX, scanMaxX, scanMinY, scanMaxY, scanMinZ, scanMaxZ);

        int interiorAirCount = flood.enclosed ? flood.airCount : 0;
        int width = flood.enclosed && flood.maxX >= flood.minX ? flood.maxX - flood.minX + 1 : 0;
        int length = flood.enclosed && flood.maxZ >= flood.minZ ? flood.maxZ - flood.minZ + 1 : 0;
        int height = flood.enclosed && flood.maxY >= flood.minY ? flood.maxY - flood.minY + 1 : 0;

        return new ScanResult(
            facilities.feedTroughCount(),
            facilities.autoFeedTroughCount(),
            facilities.hayHopperCount(),
            facilities.incubatorCount(),
            interiorAirCount,
            width,
            length,
            height,
            flood.enclosed,
            flood.doorCount,
            flood.minX,
            flood.minY,
            flood.minZ,
            flood.maxX,
            flood.maxY,
            flood.maxZ,
            Collections.unmodifiableSet(new LinkedHashSet<>(scopedInterior)),
            Collections.unmodifiableSet(flood.boundaryDoorCells)
        );
    }

    private static FacilityCount countInteriorFacilities(ServerLevel level,
                                                         Set<Long> interiorAirCells,
                                                         int scanMinX,
                                                         int scanMaxX,
                                                         int scanMinY,
                                                         int scanMaxY,
                                                         int scanMinZ,
                                                         int scanMaxZ) {
        if (interiorAirCells.isEmpty()) {
            return new FacilityCount(0, 0, 0, 0);
        }

        int feedTroughCount = 0;
        int autoFeedTroughCount = 0;
        int hayHopperCount = 0;
        int incubatorCount = 0;

        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int x = scanMinX; x <= scanMaxX; x++) {
            for (int y = scanMinY; y <= scanMaxY; y++) {
                for (int z = scanMinZ; z <= scanMaxZ; z++) {
                    cursor.set(x, y, z);
                    BlockState state = level.getBlockState(cursor);
                    if (!isAdjacentToInteriorAir(interiorAirCells, cursor)) {
                        continue;
                    }
                    if (state.getBlock() instanceof FeedTroughBlock) {
                        feedTroughCount++;
                        continue;
                    }
                    if (state.getBlock() instanceof AutoFeedTroughBlock) {
                        autoFeedTroughCount++;
                        continue;
                    }
                    if (state.getBlock() instanceof HayHopperBlock) {
                        if (state.getValue(HayHopperBlock.PART) == HayHopperBlock.Part.MAIN) {
                            hayHopperCount++;
                        }
                        continue;
                    }
                    if (state.getBlock() instanceof IncubatorBlock
                        && state.getValue(IncubatorBlock.PART) == IncubatorBlock.Part.MAIN) {
                        incubatorCount++;
                    }
                }
            }
        }

        return new FacilityCount(feedTroughCount, autoFeedTroughCount, hayHopperCount, incubatorCount);
    }

    private static boolean isAdjacentToInteriorAir(Set<Long> interiorAirCells, BlockPos facilityPos) {
        for (Direction direction : Direction.values()) {
            if (interiorAirCells.contains(pack(facilityPos.relative(direction)))) {
                return true;
            }
        }
        return false;
    }

    private static FloodResult floodInterior(ServerLevel level,
                                             List<BlockPos> starts,
                                             int scanMinX,
                                             int scanMaxX,
                                             int scanMinY,
                                             int scanMaxY,
                                             int scanMinZ,
                                             int scanMaxZ) {
        ArrayDeque<BlockPos> queue = new ArrayDeque<>();
        Set<Long> visited = new HashSet<>();
        Set<Long> interiorAirCells = new LinkedHashSet<>();
        Set<Long> boundaryDoorCells = new LinkedHashSet<>();

        for (BlockPos start : starts) {
            long key = pack(start);
            if (visited.add(key)) {
                queue.add(start);
            }
        }

        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int minZ = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        int maxZ = Integer.MIN_VALUE;

        boolean enclosed = true;
        int doorCount = 0;

        while (!queue.isEmpty()) {
            BlockPos pos = queue.removeFirst();
            if (!withinScan(pos, scanMinX, scanMaxX, scanMinY, scanMaxY, scanMinZ, scanMaxZ)) {
                enclosed = false;
                continue;
            }

            BlockState state = level.getBlockState(pos);
            if (!state.isAir()) {
                continue;
            }

            long packed = pack(pos);
            interiorAirCells.add(packed);

            minX = Math.min(minX, pos.getX());
            minY = Math.min(minY, pos.getY());
            minZ = Math.min(minZ, pos.getZ());
            maxX = Math.max(maxX, pos.getX());
            maxY = Math.max(maxY, pos.getY());
            maxZ = Math.max(maxZ, pos.getZ());

            for (Direction direction : Direction.values()) {
                BlockPos next = pos.relative(direction);
                if (!withinScan(next, scanMinX, scanMaxX, scanMinY, scanMaxY, scanMinZ, scanMaxZ)) {
                    enclosed = false;
                    continue;
                }

                BlockState nextState = level.getBlockState(next);
                if (nextState.isAir()) {
                    long nextKey = pack(next);
                    if (visited.add(nextKey)) {
                        queue.add(next);
                    }
                    continue;
                }

                if (isDoorLike(nextState)) {
                    long doorKey = pack(next);
                    if (boundaryDoorCells.add(doorKey)) {
                        doorCount++;
                    }
                }
            }
        }

        if (interiorAirCells.isEmpty()) {
            minX = scanMinX;
            minY = scanMinY;
            minZ = scanMinZ;
            maxX = scanMaxX;
            maxY = scanMaxY;
            maxZ = scanMaxZ;
        }

        return new FloodResult(interiorAirCells.size(), enclosed, doorCount, minX, minY, minZ, maxX, maxY, maxZ, interiorAirCells, boundaryDoorCells);
    }

    private static boolean isDoorLike(BlockState state) {
        return state.is(BlockTags.DOORS) || state.is(BlockTags.FENCE_GATES);
    }

    private static boolean withinScan(BlockPos pos,
                                      int scanMinX,
                                      int scanMaxX,
                                      int scanMinY,
                                      int scanMaxY,
                                      int scanMinZ,
                                      int scanMaxZ) {
        return pos.getX() >= scanMinX && pos.getX() <= scanMaxX
            && pos.getY() >= scanMinY && pos.getY() <= scanMaxY
            && pos.getZ() >= scanMinZ && pos.getZ() <= scanMaxZ;
    }

    private static long pack(BlockPos pos) {
        return BlockPos.asLong(pos.getX(), pos.getY(), pos.getZ());
    }

    private record FloodResult(int airCount,
                               boolean enclosed,
                               int doorCount,
                               int minX,
                               int minY,
                               int minZ,
                               int maxX,
                               int maxY,
                               int maxZ,
                               Set<Long> interiorAirCells,
                               Set<Long> boundaryDoorCells) {
    }

    public record ValidationResult(boolean success,
                                   int targetTier,
                                   TierRequirement requirement,
                                   ScanResult scan,
                                   String message) {
    }

    public record ScanResult(int feedTroughCount,
                             int autoFeedTroughCount,
                             int hayHopperCount,
                             int incubatorCount,
                             int interiorAirCount,
                             int width,
                             int length,
                             int height,
                             boolean enclosed,
                             int doorCount,
                             int interiorMinX,
                             int interiorMinY,
                             int interiorMinZ,
                             int interiorMaxX,
                             int interiorMaxY,
                             int interiorMaxZ,
                             Set<Long> interiorAirCells,
                             Set<Long> boundaryDoorCells) {
        public boolean hasInteriorSpace() {
            return width > 0 && length > 0 && height > 0;
        }
    }

    public record TierRequirement(int feedTroughCount,
                                  int autoFeedTroughCount,
                                  int hayHopperCount,
                                  int incubatorCount,
                                  int minInteriorBlocks) {
        public static TierRequirement fromTier(int tier) {
            return switch (tier) {
                case 1 -> new TierRequirement(
                    Config.BARN_T1_FEED_TROUGH.get(),
                    Config.BARN_T1_AUTOFEED_TROUGH.get(),
                    Config.BARN_T1_HAY_HOPPER.get(),
                    Config.BARN_T1_INCUBATOR.get(),
                    Config.BARN_T1_MIN_INTERIOR_BLOCKS.get()
                );
                case 2 -> new TierRequirement(
                    Config.BARN_T2_FEED_TROUGH.get(),
                    Config.BARN_T2_AUTOFEED_TROUGH.get(),
                    Config.BARN_T2_HAY_HOPPER.get(),
                    Config.BARN_T2_INCUBATOR.get(),
                    Config.BARN_T2_MIN_INTERIOR_BLOCKS.get()
                );
                case 3 -> new TierRequirement(
                    Config.BARN_T3_FEED_TROUGH.get(),
                    Config.BARN_T3_AUTOFEED_TROUGH.get(),
                    Config.BARN_T3_HAY_HOPPER.get(),
                    Config.BARN_T3_INCUBATOR.get(),
                    Config.BARN_T3_MIN_INTERIOR_BLOCKS.get()
                );
                default -> throw new IllegalArgumentException("Invalid barn tier: " + tier);
            };
        }
    }

    private record FacilityCount(int feedTroughCount,
                                 int autoFeedTroughCount,
                                 int hayHopperCount,
                                 int incubatorCount) {
    }
}
