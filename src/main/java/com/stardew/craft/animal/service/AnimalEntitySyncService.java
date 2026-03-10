package com.stardew.craft.animal.service;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.animal.data.AnimalWorldData;
import com.stardew.craft.animal.model.AnimalBuildingRecord;
import com.stardew.craft.animal.model.FarmAnimalRecord;
import com.stardew.craft.entity.ModEntities;
import com.stardew.craft.entity.animal.BaseCoopAnimalEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.AABB;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings("null")
public final class AnimalEntitySyncService {
    private static final AABB FULL_LEVEL_BOX = new AABB(-30_000_000, -64, -30_000_000, 30_000_000, 320, 30_000_000);

    private AnimalEntitySyncService() {
    }

    public record SyncResult(int updated, int spawned) {
    }

    public static SyncResult syncAll(ServerLevel level) {
        AnimalWorldData data = AnimalWorldData.get(level);
        CollectionState state = collectLoaded(level);
        int updated = 0;
        int spawned = 0;

        for (FarmAnimalRecord record : data.getAnimals()) {
            BaseCoopAnimalEntity entity = state.byManagedId.get(record.animalId());
            if (entity == null) {
                entity = spawnEntityForRecord(level, data, record);
                if (entity != null) {
                    spawned++;
                    updated++;
                }
                continue;
            }
            applyRecord(entity, record);
            updated++;
        }

        return new SyncResult(updated, spawned);
    }

    public static BaseCoopAnimalEntity spawnOrSyncSingle(ServerLevel level, FarmAnimalRecord record) {
        BaseCoopAnimalEntity existing = findLoadedByManagedId(level, record.animalId());
        if (existing != null) {
            applyRecord(existing, record);
            return existing;
        }
        return spawnEntityForRecord(level, AnimalWorldData.get(level), record);
    }

    private static BaseCoopAnimalEntity spawnEntityForRecord(ServerLevel level,
                                                             AnimalWorldData data,
                                                             FarmAnimalRecord record) {
        EntityType<? extends BaseCoopAnimalEntity> type = resolveEntityType(record.animalTypeId());
        if (type == null) {
            StardewCraft.LOGGER.warn("[ANIMAL_SYNC] Unknown animal type: {}", record.animalTypeId());
            return null;
        }

        BaseCoopAnimalEntity entity = type.create(level);
        if (entity == null) {
            StardewCraft.LOGGER.warn("[ANIMAL_SYNC] Failed to create entity for type {}", record.animalTypeId());
            return null;
        }

        BlockPos spawnPos = findSpawnPos(level, data.getBuilding(record.buildingId()));
        entity.moveTo(
            spawnPos.getX() + 0.5D,
            spawnPos.getY(),
            spawnPos.getZ() + 0.5D,
            level.random.nextFloat() * 360.0F,
            0.0F
        );
        applyRecord(entity, record);
        level.addFreshEntity(entity);
        return entity;
    }

    private static void applyRecord(BaseCoopAnimalEntity entity, FarmAnimalRecord record) {
        entity.setManagedAnimalId(record.animalId());
        entity.setManagedAnimalType(record.animalTypeId());
        entity.setBaby(record.isBaby());
        entity.setPersistenceRequired();

        String customName = record.customName();
        if (customName == null || customName.isBlank()) {
            entity.setCustomName(null);
            entity.setCustomNameVisible(false);
            return;
        }

        entity.setCustomName(Component.literal(customName));
        entity.setCustomNameVisible(true);
    }

    private static BaseCoopAnimalEntity findLoadedByManagedId(ServerLevel level, long animalId) {
        if (animalId <= 0L) {
            return null;
        }
        for (BaseCoopAnimalEntity entity : level.getEntitiesOfClass(BaseCoopAnimalEntity.class, FULL_LEVEL_BOX)) {
            if (entity.getManagedAnimalId() == animalId) {
                return entity;
            }
        }
        return null;
    }

    private static BlockPos findSpawnPos(ServerLevel level, Optional<AnimalBuildingRecord> buildingOpt) {
        if (buildingOpt.isEmpty()) {
            return level.getSharedSpawnPos().above();
        }

        AnimalBuildingRecord building = buildingOpt.get();
        BlockPos base = building.managerPos().above();
        if (canStand(level, base)) {
            return base;
        }

        int maxRange = Math.max(1, Math.min(8, building.range()));
        for (int radius = 1; radius <= maxRange; radius++) {
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    BlockPos candidate = base.offset(dx, 0, dz);
                    if (!building.isInBounds(candidate)) {
                        continue;
                    }
                    if (canStand(level, candidate)) {
                        return candidate;
                    }
                }
            }
        }

        return base;
    }

    private static boolean canStand(ServerLevel level, BlockPos pos) {
        return level.getBlockState(pos).isAir()
            && level.getBlockState(pos.above()).isAir()
            && level.getBlockState(pos.below()).blocksMotion();
    }

    private static EntityType<? extends BaseCoopAnimalEntity> resolveEntityType(String animalTypeId) {
        return switch (animalTypeId) {
            case "white_chicken" -> ModEntities.WHITE_CHICKEN.get();
            case "golden_chicken" -> ModEntities.GOLDEN_CHICKEN.get();
            case "duck" -> ModEntities.DUCK.get();
            case "void_chicken" -> ModEntities.VOID_CHICKEN.get();
            case "rabbit" -> ModEntities.RABBIT.get();
            case "ostrich" -> ModEntities.OSTRICH.get();
            case "dinosaur" -> ModEntities.DINOSAUR.get();
            case "cow" -> ModEntities.COW.get();
            case "goat" -> ModEntities.GOAT.get();
            case "sheep" -> ModEntities.SHEEP.get();
            case "pig" -> ModEntities.PIG.get();
            default -> null;
        };
    }

    private static CollectionState collectLoaded(ServerLevel level) {
        Map<Long, BaseCoopAnimalEntity> byManagedId = new HashMap<>();
        for (BaseCoopAnimalEntity entity : level.getEntitiesOfClass(BaseCoopAnimalEntity.class, FULL_LEVEL_BOX)) {
            long managedId = entity.getManagedAnimalId();
            if (managedId > 0L) {
                byManagedId.putIfAbsent(managedId, entity);
            }
        }
        return new CollectionState(byManagedId);
    }

    private record CollectionState(Map<Long, BaseCoopAnimalEntity> byManagedId) {
    }
}