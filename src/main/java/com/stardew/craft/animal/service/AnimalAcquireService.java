package com.stardew.craft.animal.service;

import com.stardew.craft.animal.data.AnimalWorldData;
import com.stardew.craft.animal.model.AnimalAcquisitionSource;
import com.stardew.craft.animal.model.AnimalBuildingRecord;
import com.stardew.craft.animal.model.AnimalTypeCatalog;
import com.stardew.craft.animal.model.FarmAnimalRecord;
import net.minecraft.server.level.ServerLevel;

public final class AnimalAcquireService {
    private AnimalAcquireService() {}

    public static FarmAnimalRecord purchase(ServerLevel level,
                                            String animalTypeId,
                                            String customName,
                                            String buildingId) {
        AnimalWorldData worldData = AnimalWorldData.get(level);
        AnimalBuildingRecord building = worldData.getBuilding(buildingId)
            .orElseThrow(() -> new IllegalArgumentException("Building not found: " + buildingId));

        validateBuildingFamily(animalTypeId, building);

        String finalName = (customName == null || customName.isBlank())
            ? defaultName(animalTypeId)
            : customName;

        FarmAnimalRecord record = worldData.createAnimal(animalTypeId, finalName, buildingId, AnimalAcquisitionSource.PURCHASE);
        AnimalEntitySyncService.spawnOrSyncSingle(level, record);
        return record;
    }

    public static FarmAnimalRecord pregnancy(ServerLevel level,
                                             String animalTypeId,
                                             String buildingId) {
        AnimalWorldData worldData = AnimalWorldData.get(level);
        AnimalBuildingRecord building = worldData.getBuilding(buildingId)
            .orElseThrow(() -> new IllegalArgumentException("Building not found: " + buildingId));

        validateBuildingFamily(animalTypeId, building);
        FarmAnimalRecord record = worldData.createAnimal(animalTypeId, defaultName(animalTypeId), buildingId, AnimalAcquisitionSource.PREGNANCY);
        AnimalEntitySyncService.spawnOrSyncSingle(level, record);
        return record;
    }

    public static FarmAnimalRecord incubation(ServerLevel level,
                                              String animalTypeId,
                                              String buildingId) {
        return incubation(level, animalTypeId, null, buildingId);
    }

    public static FarmAnimalRecord incubation(ServerLevel level,
                                              String animalTypeId,
                                              String customName,
                                              String buildingId) {
        AnimalWorldData worldData = AnimalWorldData.get(level);
        AnimalBuildingRecord building = worldData.getBuilding(buildingId)
            .orElseThrow(() -> new IllegalArgumentException("Building not found: " + buildingId));

        validateBuildingFamily(animalTypeId, building);
        String finalName = (customName == null || customName.isBlank())
            ? defaultName(animalTypeId)
            : customName;
        FarmAnimalRecord record = worldData.createAnimal(animalTypeId, finalName, buildingId, AnimalAcquisitionSource.INCUBATION);
        AnimalEntitySyncService.spawnOrSyncSingle(level, record);
        return record;
    }

    private static void validateBuildingFamily(String animalTypeId, AnimalBuildingRecord building) {
        String family = AnimalTypeCatalog.resolve(animalTypeId).family();
        if (!building.buildingType().family().equals(family)) {
            throw new IllegalStateException("Animal type " + animalTypeId
                + " requires " + family + " but building is " + building.buildingType().family());
        }
    }

    private static String defaultName(String animalTypeId) {
        return animalTypeId;
    }
}
