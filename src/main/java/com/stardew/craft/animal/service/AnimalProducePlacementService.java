package com.stardew.craft.animal.service;

import com.stardew.craft.animal.data.AnimalWorldData;
import com.stardew.craft.animal.model.AnimalBuildingRecord;
import com.stardew.craft.animal.model.FarmAnimalRecord;
import com.stardew.craft.block.ModBlocks;
import com.stardew.craft.block.utility.AutoFeedTroughBlock;
import com.stardew.craft.block.utility.CoopManagerBlock;
import com.stardew.craft.block.utility.FeedTroughBlock;
import com.stardew.craft.block.utility.HayHopperBlock;
import com.stardew.craft.blockentity.AutoGrabberBlockEntity;
import com.stardew.craft.blockentity.AnimalProduceSpotBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.Containers;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("null")
public final class AnimalProducePlacementService {
    private AnimalProducePlacementService() {
    }

    public static boolean placeInHome(ServerLevel level,
                                      AnimalWorldData data,
                                      FarmAnimalRecord record,
                                      ItemStack produceStack) {
        if (produceStack == null || produceStack.isEmpty()) {
            return false;
        }

        AnimalBuildingRecord building = data.getBuilding(record.buildingId()).orElse(null);
        if (building == null) {
            return false;
        }

        ItemStack remaining = produceStack.copy();
        remaining = insertIntoAutoGrabbers(level, building, remaining);

        int toPlace = Math.max(0, remaining.getCount());
        boolean placedAny = false;
        for (int i = 0; i < toPlace; i++) {
            BlockPos targetPos = findAvailableTile(level, building);
            if (targetPos == null) {
                break;
            }

            BlockState state = ModBlocks.ANIMAL_PRODUCE_SPOT.get().defaultBlockState();
            if (!level.setBlock(targetPos, state, 3)) {
                continue;
            }

            BlockEntity be = level.getBlockEntity(targetPos);
            if (!(be instanceof AnimalProduceSpotBlockEntity produceBe)) {
                level.removeBlock(targetPos, false);
                continue;
            }

            ItemStack single = remaining.copy();
            single.setCount(1);
            produceBe.setProduceStack(single);
            produceBe.setAnimalId(record.animalId());
            produceBe.setBuildingId(record.buildingId());
            produceBe.setChanged();
            remaining.shrink(1);
            placedAny = true;
        }

        return placedAny || remaining.getCount() < produceStack.getCount();
    }

    private static ItemStack insertIntoAutoGrabbers(ServerLevel level, AnimalBuildingRecord building, ItemStack stack) {
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }

        ItemStack remaining = stack.copy();
        for (int y = building.minY(); y <= building.maxY() && !remaining.isEmpty(); y++) {
            for (int z = building.minZ(); z <= building.maxZ() && !remaining.isEmpty(); z++) {
                for (int x = building.minX(); x <= building.maxX() && !remaining.isEmpty(); x++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockEntity be = level.getBlockEntity(pos);
                    if (!(be instanceof AutoGrabberBlockEntity autoGrabber)) {
                        continue;
                    }
                    remaining = autoGrabber.insertAutomation(remaining, false);
                }
            }
        }

        return remaining;
    }

    public static boolean dropInHome(ServerLevel level,
                                     AnimalWorldData data,
                                     FarmAnimalRecord record,
                                     ItemStack produceStack) {
        if (produceStack == null || produceStack.isEmpty()) {
            return false;
        }

        AnimalBuildingRecord building = data.getBuilding(record.buildingId()).orElse(null);
        if (building == null) {
            return false;
        }

        double x = (building.minX() + building.maxX() + 1) / 2.0;
        double y = building.minY() + 1.0;
        double z = (building.minZ() + building.maxZ() + 1) / 2.0;
        Containers.dropItemStack(level, x, y, z, produceStack.copy());
        return true;
    }

    public static boolean placeNearAnimal(ServerLevel level,
                                          AnimalWorldData data,
                                          FarmAnimalRecord record,
                                          BlockPos center,
                                          ItemStack produceStack,
                                          int maxRadius) {
        if (produceStack == null || produceStack.isEmpty()) {
            return false;
        }

        AnimalBuildingRecord building = data.getBuilding(record.buildingId()).orElse(null);
        if (building == null) {
            return false;
        }

        BlockPos targetPos = findAvailableTileNear(level, building, center, Math.max(1, maxRadius), true);
        if (targetPos == null) {
            return false;
        }

        BlockState state = ModBlocks.ANIMAL_PRODUCE_SPOT.get().defaultBlockState();
        if (!level.setBlock(targetPos, state, 3)) {
            return false;
        }

        BlockEntity be = level.getBlockEntity(targetPos);
        if (!(be instanceof AnimalProduceSpotBlockEntity produceBe)) {
            level.removeBlock(targetPos, false);
            return false;
        }

        ItemStack single = produceStack.copy();
        single.setCount(1);
        produceBe.setProduceStack(single);
        produceBe.setAnimalId(record.animalId());
        produceBe.setBuildingId(record.buildingId());
        produceBe.setChanged();
        return true;
    }

    private static BlockPos findAvailableTile(ServerLevel level, AnimalBuildingRecord building) {
        List<BlockPos> candidates = new ArrayList<>();

        if (!building.interiorAirCells().isEmpty()) {
            // Use precise interior air cells for placement
            for (Long cell : building.interiorAirCells()) {
                BlockPos pos = BlockPos.of(cell);
                if (!level.isEmptyBlock(pos)) {
                    continue;
                }
                BlockPos below = pos.below();
                if (!hasValidProduceSupport(level, below)) {
                    continue;
                }
                candidates.add(pos.immutable());
            }
        } else {
            // Fallback to bounding box scan
            for (int y = building.minY(); y <= building.maxY(); y++) {
                for (int z = building.minZ(); z <= building.maxZ(); z++) {
                    for (int x = building.minX(); x <= building.maxX(); x++) {
                        BlockPos pos = new BlockPos(x, y, z);
                        if (!level.isEmptyBlock(pos)) {
                            continue;
                        }
                        BlockPos below = pos.below();
                        if (!hasValidProduceSupport(level, below)) {
                            continue;
                        }
                        candidates.add(pos.immutable());
                    }
                }
            }
        }

        if (candidates.isEmpty()) {
            return null;
        }

        return candidates.get(level.random.nextInt(candidates.size()));
    }

    private static BlockPos findAvailableTileNear(ServerLevel level,
                                                  AnimalBuildingRecord building,
                                                  BlockPos center,
                                                  int maxRadius,
                                                  boolean allowOutdoorNearManager) {
        List<BlockPos> candidates = new ArrayList<>();
        int managerRangeSq = (building.range() + 2) * (building.range() + 2);

        for (int dy = -1; dy <= 1; dy++) {
            int y = center.getY() + dy;
            if (!allowOutdoorNearManager && (y < building.minY() + 1 || y > building.maxY())) {
                continue;
            }

            for (int radius = 1; radius <= maxRadius; radius++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    for (int dx = -radius; dx <= radius; dx++) {
                        if (Math.abs(dx) != radius && Math.abs(dz) != radius) {
                            continue;
                        }

                        BlockPos pos = new BlockPos(center.getX() + dx, y, center.getZ() + dz);
                        boolean insideHome = building.isInBounds(pos);
                        boolean nearManager = pos.distSqr(building.managerPos()) <= managerRangeSq;
                        if (!insideHome && !(allowOutdoorNearManager && nearManager)) {
                            continue;
                        }
                        BlockPos below = pos.below();
                        if (!level.isEmptyBlock(pos)) {
                            continue;
                        }
                        if (!hasValidProduceSupport(level, below)) {
                            continue;
                        }
                        if (isDisallowedSupportBlock(level.getBlockState(below).getBlock())) {
                            continue;
                        }
                        candidates.add(pos.immutable());
                    }
                }
            }
        }

        if (candidates.isEmpty()) {
            return null;
        }
        return candidates.get(level.random.nextInt(candidates.size()));
    }

    private static boolean hasValidProduceSupport(ServerLevel level, BlockPos supportPos) {
        BlockState supportState = level.getBlockState(supportPos);
        if (isDisallowedSupportBlock(supportState.getBlock())) {
            return false;
        }
        return !supportState.isAir() && !supportState.getCollisionShape(level, supportPos).isEmpty();
    }

    private static boolean isDisallowedSupportBlock(Block block) {
        if (block instanceof FeedTroughBlock
            || block instanceof AutoFeedTroughBlock
            || block instanceof HayHopperBlock
            || block instanceof CoopManagerBlock) {
            return true;
        }

        String simple = block.getClass().getSimpleName();
        return simple.endsWith("ManagerBlock") || simple.contains("Manager");
    }
}
