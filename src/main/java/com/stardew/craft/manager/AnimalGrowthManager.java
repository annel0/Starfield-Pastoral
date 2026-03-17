package com.stardew.craft.manager;

import com.stardew.craft.animal.data.AnimalWorldData;
import com.stardew.craft.animal.model.AnimalBuildingRecord;
import com.stardew.craft.animal.model.FarmAnimalRecord;
import com.stardew.craft.animal.service.AnimalProducePlacementService;
import com.stardew.craft.animal.service.AnimalEntitySyncService;
import com.stardew.craft.animal.service.AnimalDoorStateService;
import com.stardew.craft.blockentity.AutoFeedTroughBlockEntity;
import com.stardew.craft.blockentity.AutoPetterBlockEntity;
import com.stardew.craft.blockentity.FeedTroughBlockEntity;
import com.stardew.craft.blockentity.HeaterBlockEntity;
import com.stardew.craft.entity.animal.BaseCoopAnimalEntity;
import com.stardew.craft.item.ModItems;
import com.stardew.craft.item.quality.QualityHelper;
import com.stardew.craft.player.PlayerDataManager;
import com.stardew.craft.player.PlayerStardewDataAPI;
import com.stardew.craft.player.ProfessionType;
import com.stardew.craft.time.StardewTimeManager;
import com.stardew.craft.weather.WeatherManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.AABB;

import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

@SuppressWarnings("null")
public class AnimalGrowthManager extends SavedData {
    private static final String DATA_NAME = "stardew_animal_growth_manager";
    private static final AABB ENTITY_SCAN_BOX = new AABB(-30_000_000, -64, -30_000_000, 30_000_000, 320, 30_000_000);
    private static final double PIG_TRUFFLE_FIND_CHANCE_PER_TICK = 0.0002D;
    private static final int APPROX_TICKS_PER_TEN_MINUTE_SLOT = 167;
    private static final double PIG_TRUFFLE_FIND_CHANCE_PER_SLOT = 1.0D
        - Math.pow(1.0D - PIG_TRUFFLE_FIND_CHANCE_PER_TICK, APPROX_TICKS_PER_TEN_MINUTE_SLOT);

    private enum AnimalHarvestType {
        DROP_OVERNIGHT,
        HELD,
        DIG_UP
    }

    private record AnimalProfile(int daysToProduce,
                                 int friendshipForFasterProduce,
                                 int deluxeProduceMinimumFriendship,
                                 float deluxeProduceCareDivisor,
                                 float deluxeProduceLuckMultiplier,
                                 int happinessDrain,
                                 int professionForHappinessBoost,
                                 int professionForQualityBoost,
                                 int professionForFasterProduce,
                                 AnimalHarvestType harvestType,
                                 Supplier<Item> produceSupplier,
                                 Supplier<Item> deluxeProduceSupplier) {
    }

    private static final AnimalProfile DEFAULT_CHICKEN = new AnimalProfile(
        1, -1, 200, 1200.0f, 0.0f, 7, -1, -1, -1, AnimalHarvestType.DROP_OVERNIGHT,
        ModItems.EGG_WHITE,
        ModItems.LARGE_EGG_WHITE
    );

    private static final Map<String, AnimalProfile> PROFILES = Map.ofEntries(
        Map.entry("white_chicken", new AnimalProfile(1, -1, 200, 1200.0f, 0.0f, 7, ProfessionType.COOPMASTER.getId(), -1, -1, AnimalHarvestType.DROP_OVERNIGHT, ModItems.EGG_WHITE, ModItems.LARGE_EGG_WHITE)),
        Map.entry("brown_chicken", new AnimalProfile(1, -1, 200, 1200.0f, 0.0f, 7, ProfessionType.COOPMASTER.getId(), -1, -1, AnimalHarvestType.DROP_OVERNIGHT, ModItems.EGG_BROWN, ModItems.LARGE_EGG_BROWN)),
        Map.entry("blue_chicken", new AnimalProfile(1, -1, 200, 1200.0f, 0.0f, 7, ProfessionType.COOPMASTER.getId(), -1, -1, AnimalHarvestType.DROP_OVERNIGHT, ModItems.EGG_WHITE, ModItems.LARGE_EGG_WHITE)),
        Map.entry("void_chicken", new AnimalProfile(1, -1, 200, 1200.0f, 0.0f, 5, ProfessionType.COOPMASTER.getId(), -1, -1, AnimalHarvestType.DROP_OVERNIGHT, ModItems.VOID_EGG, null)),
        Map.entry("golden_chicken", new AnimalProfile(1, -1, 200, 1200.0f, 0.0f, 10, ProfessionType.COOPMASTER.getId(), -1, -1, AnimalHarvestType.DROP_OVERNIGHT, ModItems.GOLDEN_EGG, null)),
        Map.entry("duck", new AnimalProfile(2, -1, 200, 4750.0f, 1.01f, 5, ProfessionType.COOPMASTER.getId(), -1, -1, AnimalHarvestType.DROP_OVERNIGHT, ModItems.DUCK_EGG, ModItems.DUCK_FEATHER)),
        Map.entry("rabbit", new AnimalProfile(4, -1, 0, 5000.0f, 1.02f, 5, ProfessionType.COOPMASTER.getId(), -1, -1, AnimalHarvestType.DROP_OVERNIGHT, ModItems.WOOL, ModItems.RABBITS_FOOT)),
        Map.entry("dinosaur", new AnimalProfile(7, -1, 200, 1200.0f, 0.0f, 4, ProfessionType.COOPMASTER.getId(), -1, -1, AnimalHarvestType.DROP_OVERNIGHT, ModItems.DINOSAUR_EGG, null)),
        Map.entry("ostrich", new AnimalProfile(7, -1, 200, 1200.0f, 0.0f, 5, ProfessionType.COOPMASTER.getId(), -1, -1, AnimalHarvestType.DROP_OVERNIGHT, ModItems.OSTRICH_EGG, null)),
        Map.entry("cow", new AnimalProfile(1, -1, 200, 1200.0f, 0.0f, 8, ProfessionType.SHEPHERD.getId(), -1, -1, AnimalHarvestType.HELD, ModItems.MILK, ModItems.LARGE_MILK)),
        Map.entry("goat", new AnimalProfile(2, -1, 200, 1200.0f, 0.0f, 8, ProfessionType.SHEPHERD.getId(), -1, -1, AnimalHarvestType.HELD, ModItems.GOAT_MILK, ModItems.LARGE_GOAT_MILK)),
        Map.entry("sheep", new AnimalProfile(3, -1, 0, 5000.0f, 1.02f, 8, ProfessionType.SHEPHERD.getId(), -1, ProfessionType.SHEPHERD.getId(), AnimalHarvestType.HELD, ModItems.WOOL, null)),
        Map.entry("pig", new AnimalProfile(1, -1, 0, 1200.0f, 0.0f, 8, ProfessionType.SHEPHERD.getId(), -1, -1, AnimalHarvestType.DIG_UP, ModItems.TRUFFLE, null))
    );

    public AnimalGrowthManager() {
    }

    public static AnimalGrowthManager get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
            new SavedData.Factory<>(AnimalGrowthManager::new, AnimalGrowthManager::load),
            DATA_NAME
        );
    }

    public static AnimalGrowthManager load(@Nonnull CompoundTag tag, @Nonnull HolderLookup.Provider provider) {
        return new AnimalGrowthManager();
    }

    @Override
    public CompoundTag save(@Nonnull CompoundTag tag, @Nonnull HolderLookup.Provider provider) {
        return tag;
    }

    public void growDaily(ServerLevel level) {
        AnimalWorldData worldData = AnimalWorldData.get(level);

        StardewTimeManager time = StardewTimeManager.get();
        int absoluteDaysPlayed = (time.getCurrentYear() - 1) * (28 * 4) + time.getCurrentSeason() * 28 + time.getCurrentDay();

        for (FarmAnimalRecord record : worldData.getAnimals()) {
            applyDayUpdate(level, worldData, record, absoluteDaysPlayed);
        }

        worldData.markChanged();
        AnimalEntitySyncService.syncAll(level);
        setDirty();
    }

    public void updatePerTenMinutes(ServerLevel level, int timeOfDay) {
        AnimalWorldData worldData = AnimalWorldData.get(level);
        boolean changed = false;

        changed |= tryPigDigUpTruffles(level, worldData, timeOfDay);

        if (timeOfDay < 1800) {
            if (changed) {
                worldData.markChanged();
                setDirty();
            }
            return;
        }

        boolean isWinter = StardewTimeManager.get().getCurrentSeason() == 3;
        boolean isRaining = level.isRaining();

        for (FarmAnimalRecord record : worldData.getAnimals()) {
            AnimalProfile profile = resolveProfile(record.animalTypeId());
            AnimalBuildingRecord building = worldData.getBuilding(record.buildingId()).orElse(null);
            boolean outdoors = isAnimalOutdoors(level, record, building);

            int change = 0;
            if (outdoors) {
                // Parity: outdoors animals lose happiness in rain, winter, or after 19:00.
                change = (timeOfDay > 1900 || isRaining || isWinter) ? -profile.happinessDrain() : profile.happinessDrain();
            } else if (isWinter && record.happiness() > 150) {
                change = hasWorkingHeater(level, building) ? profile.happinessDrain() : -profile.happinessDrain();
            }

            if (change != 0) {
                record.addHappiness(change);
                changed = true;
            }
        }

        if (changed) {
            worldData.markChanged();
            setDirty();
        }
    }

    private boolean tryPigDigUpTruffles(ServerLevel level, AnimalWorldData worldData, int timeOfDay) {
        if (timeOfDay < 600 || timeOfDay >= 1900) {
            return false;
        }
        if (StardewTimeManager.get().getCurrentSeason() == 3 || level.isRaining()) {
            return false;
        }

        boolean changed = false;
        StardewTimeManager time = StardewTimeManager.get();
        int absoluteDaysPlayed = (time.getCurrentYear() - 1) * (28 * 4) + time.getCurrentSeason() * 28 + time.getCurrentDay();

        for (FarmAnimalRecord record : worldData.getAnimals()) {
            if (!"pig".equals(record.animalTypeId()) || record.isBaby() || record.currentProduceId().isBlank()) {
                continue;
            }

            AnimalBuildingRecord building = worldData.getBuilding(record.buildingId()).orElse(null);
            if (!canPigDigNow(level, record, building)) {
                continue;
            }

            if (level.getRandom().nextDouble() >= PIG_TRUFFLE_FIND_CHANCE_PER_SLOT) {
                continue;
            }

            BaseCoopAnimalEntity pigEntity = findEntityByManagedId(level, record.animalId());
            if (pigEntity == null) {
                continue;
            }

            ItemStack produce = resolveCurrentProduce(record);
            if (produce.isEmpty()) {
                continue;
            }

            if (!AnimalProducePlacementService.placeNearAnimal(level, worldData, record, pigEntity.blockPosition(), produce, 4)) {
                continue;
            }

            pigEntity.triggerForageAnimation();
            if (shouldConsumePigCurrentProduce(record, absoluteDaysPlayed, timeOfDay)) {
                record.setCurrentProduceId("");
                record.setProduceQuality(0);
            }
            changed = true;
        }

        return changed;
    }

    private boolean canPigDigNow(ServerLevel level, FarmAnimalRecord record, AnimalBuildingRecord building) {
        if (building == null) {
            return false;
        }
        if (StardewTimeManager.get().getCurrentSeason() == 3 || level.isRaining()) {
            return false;
        }
        return isAnimalOutdoors(level, record, building);
    }

    private ItemStack resolveCurrentProduce(FarmAnimalRecord record) {
        ResourceLocation id = ResourceLocation.tryParse(record.currentProduceId());
        if (id == null) {
            return ItemStack.EMPTY;
        }
        Item item = BuiltInRegistries.ITEM.get(id);
        if (item == Items.AIR) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = new ItemStack(item);
        QualityHelper.setQuality(stack, record.produceQuality());
        return stack;
    }

    private boolean shouldConsumePigCurrentProduce(FarmAnimalRecord record, int absoluteDaysPlayed, int timeOfDay) {
        long seed = record.animalId() / 2L + absoluteDaysPlayed + timeOfDay;
        RandomSource random = RandomSource.create(seed);
        return random.nextDouble() > (record.friendship() / 1500.0D);
    }

    private BaseCoopAnimalEntity findEntityByManagedId(ServerLevel level, long animalId) {
        if (animalId <= 0L) {
            return null;
        }
        for (BaseCoopAnimalEntity entity : level.getEntitiesOfClass(BaseCoopAnimalEntity.class, ENTITY_SCAN_BOX)) {
            if (entity.getManagedAnimalId() == animalId) {
                return entity;
            }
        }
        return null;
    }

    private boolean applyDayUpdate(ServerLevel level, AnimalWorldData worldData, FarmAnimalRecord record, int absoluteDaysPlayed) {
        AnimalProfile profile = resolveProfile(record.animalTypeId());
        RandomSource random = randomForAnimalDay(record.animalId(), absoluteDaysPlayed);
        AnimalBuildingRecord building = worldData.getBuilding(record.buildingId()).orElse(null);
        boolean hasHappinessProfession = hasBuildingOwnerProfession(building, profile.professionForHappinessBoost());
        boolean hasQualityProfession = hasBuildingOwnerProfession(building, profile.professionForQualityBoost());
        boolean hasFasterProduceProfession = hasBuildingOwnerProfession(building, profile.professionForFasterProduce());
        boolean animalOutdoors = false;
        double averageDailyLuck = computeAverageDailyLuck(level);
        boolean wasLeftOutLastNight = false;

        if (building != null) {
            animalOutdoors = isAnimalOutdoors(level, record, building);
            boolean doorOpen = AnimalDoorStateService.isAnyBoundaryDoorOpen(level, building);
            if (animalOutdoors && !doorOpen) {
                record.setMoodMessage(6);
                record.setHappiness(record.happiness() / 2);
                wasLeftOutLastNight = true;
            } else if (animalOutdoors) {
                // Parity: animals that remained outdoors overnight still lose happiness before being considered back home.
                record.setHappiness(record.happiness() / 2);
                animalOutdoors = false;
            } else if (!animalOutdoors && !doorOpen) {
                record.addHappiness(profile.happinessDrain() * 2);
            }
        }

        record.incrementDaysSinceLastProduce();
        record.incrementDaysOwned();

        if (!record.wasPetToday() && !record.wasAutoPetToday()) {
            int friendshipPenalty = 10 - record.friendship() / 200;
            record.addFriendship(-Math.max(0, friendshipPenalty));
            record.addHappiness(-50);
        }
        record.setWasPetToday(false);
        record.setWasAutoPetToday(false);

        if (building != null && hasWorkingAutoPetter(level, building)) {
            applyAutoPetterEffect(record, profile, hasHappinessProfession);
        }

        // Parity: overnight feeding comes from indoor hay when the animal is considered back at home.
        if (record.fullness() < 200 && building != null && !animalOutdoors && consumeOneHayFromTrough(level, building)) {
            record.setFullness(255);
        }

        if (record.fullness() > 200 || random.nextDouble() < (record.fullness() - 30) / 170.0) {
            record.incrementAgeDays(1);
            record.addHappiness(profile.happinessDrain() * 2);
        }

        if (record.fullness() < 200) {
            record.addHappiness(-100);
            record.addFriendship(-20);
        }

        int produceSpeedBonus = (profile.friendshipForFasterProduce() >= 0
            && record.friendship() >= profile.friendshipForFasterProduce()) ? 1 : 0;
        if (hasFasterProduceProfession) {
            produceSpeedBonus += 1;
        }
        int daysToProduce = Math.max(1, profile.daysToProduce() - produceSpeedBonus);
        boolean produceToday = record.daysSinceLastProduce() >= daysToProduce
            && random.nextDouble() < record.fullness() / 200.0
            && random.nextDouble() < record.happiness() / 70.0;

        boolean produced = false;
        ItemStack produceStack = ItemStack.EMPTY;
        int produceQuality = record.produceQuality();
        String produceId = "";

        if (produceToday && !record.isBaby()) {
            Item defaultProduce = profile.produceSupplier() == null ? null : profile.produceSupplier().get();
            if (defaultProduce != null) {
                produceStack = new ItemStack(defaultProduce);
            }

            if (!produceStack.isEmpty() && random.nextDouble() < record.happiness() / 150.0) {
                float happinessModifier = record.happiness() > 200
                    ? record.happiness() * 1.5f
                    : (record.happiness() <= 100 ? record.happiness() - 100 : 0f);

                Item deluxeProduce = profile.deluxeProduceSupplier() == null ? null : profile.deluxeProduceSupplier().get();
                if (deluxeProduce != null
                    && record.friendship() >= profile.deluxeProduceMinimumFriendship()
                    && random.nextDouble() < ((record.friendship() + happinessModifier) / profile.deluxeProduceCareDivisor()) + averageDailyLuck * profile.deluxeProduceLuckMultiplier()) {
                    produceStack = new ItemStack(deluxeProduce);
                }

                record.resetDaysSinceLastProduce();
                produceQuality = rollQuality(record.friendship(), record.happiness(), hasQualityProfession, random);
            }

            if (!produceStack.isEmpty()) {
                if (record.hasEatenAnimalCracker()) {
                    produceStack.grow(1);
                }
                QualityHelper.setQuality(produceStack, produceQuality);
                produceId = getProduceId(produceStack);
            }
        }

        if (!produceStack.isEmpty()) {
            if (profile.harvestType() == AnimalHarvestType.HELD && produceToday) {
                record.setCurrentProduceId(produceId);
                record.setProduceQuality(produceQuality);
                produced = true;
            } else if (profile.harvestType() == AnimalHarvestType.DIG_UP && produceToday) {
                // Parity: pigs store ready produce first; actual truffle dig happens later during daytime behavior.
                record.setCurrentProduceId(produceId);
                record.setProduceQuality(produceQuality);
                produced = true;
            } else if (AnimalProducePlacementService.placeInHome(level, worldData, record, produceStack)) {
                produced = true;
            } else if (AnimalProducePlacementService.dropInHome(level, worldData, record, produceStack)) {
                produced = true;
            }
        }

        if (!wasLeftOutLastNight) {
            if (record.fullness() < 30) {
                record.setMoodMessage(4);
            } else if (record.happiness() < 30) {
                record.setMoodMessage(3);
            } else if (record.happiness() < 200) {
                record.setMoodMessage(2);
            } else {
                record.setMoodMessage(1);
            }
        }

        record.setFullness(0);
        if (isFestivalDay(level)) {
            // Parity: festival days keep animals sufficiently fed after overnight update.
            record.setFullness(250);
        }
        return produced;
    }

    private int rollQuality(int friendship, int happiness, boolean hasQualityProfession, RandomSource random) {
        double chance = friendship / 1000.0 - (1.0 - happiness / 225.0);
        if (hasQualityProfession) {
            chance += 0.33;
        }
        if (chance >= 0.95f && random.nextFloat() < chance * 0.5f) {
            return QualityHelper.IRIDIUM;
        }
        if (random.nextFloat() < chance * 0.5f) {
            return QualityHelper.GOLD;
        }
        if (random.nextFloat() < chance) {
            return QualityHelper.SILVER;
        }
        return QualityHelper.NORMAL;
    }

    private String getProduceId(ItemStack stack) {
        return stack.getItemHolder().unwrapKey().map(key -> key.location().toString()).orElse("");
    }

    private AnimalProfile resolveProfile(String animalTypeId) {
        return PROFILES.getOrDefault(animalTypeId, DEFAULT_CHICKEN);
    }

    private RandomSource randomForAnimalDay(long animalId, int absoluteDaysPlayed) {
        long seed = 1469598103934665603L;
        seed = (seed ^ (animalId / 2L)) * 1099511628211L;
        seed = (seed ^ absoluteDaysPlayed) * 1099511628211L;
        return RandomSource.create(seed);
    }

    private boolean isFestivalDay(ServerLevel level) {
        return "Festival".equals(WeatherManager.getCurrentWeather(level));
    }

    private double computeAverageDailyLuck(ServerLevel level) {
        java.util.List<ServerPlayer> players = level.getServer().getPlayerList().getPlayers();
        if (players.isEmpty()) {
            return 0.0;
        }

        double totalLuck = 0.0;
        for (ServerPlayer player : players) {
            totalLuck += PlayerStardewDataAPI.getDailyLuck(player);
        }
        return totalLuck / players.size();
    }

    private boolean consumeOneHayFromTrough(ServerLevel level, AnimalBuildingRecord building) {
        for (int y = building.minY(); y <= building.maxY(); y++) {
            for (int z = building.minZ(); z <= building.maxZ(); z++) {
                for (int x = building.minX(); x <= building.maxX(); x++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockEntity be = level.getBlockEntity(pos);

                    if (be instanceof FeedTroughBlockEntity trough) {
                        ItemStack removed = trough.takeOneFromSelf(false);
                        if (removed.isEmpty()) {
                            removed = trough.extractAutomation(1, false);
                        }
                        if (!removed.isEmpty()) {
                            return true;
                        }
                    }

                    if (be instanceof AutoFeedTroughBlockEntity autoTrough) {
                        ItemStack removed = autoTrough.takeOneFromSelf(false);
                        if (removed.isEmpty()) {
                            removed = autoTrough.extractAutomation(1, false);
                        }
                        if (!removed.isEmpty()) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean isAnimalOutdoors(ServerLevel level, FarmAnimalRecord record, AnimalBuildingRecord building) {
        if (building == null) {
            return true;
        }

        for (BaseCoopAnimalEntity entity : level.getEntitiesOfClass(BaseCoopAnimalEntity.class, new AABB(ENTITY_SCAN_BOX.minX, ENTITY_SCAN_BOX.minY, ENTITY_SCAN_BOX.minZ, ENTITY_SCAN_BOX.maxX, ENTITY_SCAN_BOX.maxY, ENTITY_SCAN_BOX.maxZ))) {
            if (entity.getManagedAnimalId() != record.animalId()) {
                continue;
            }
            return !isEntityInsideBuilding(entity, building);
        }

        return true;
    }

    private boolean hasWorkingAutoPetter(ServerLevel level, AnimalBuildingRecord building) {
        if (building == null) {
            return false;
        }
        return hasUtilityBlockEntityInBuilding(level, building, AutoPetterBlockEntity.class);
    }

    private boolean hasWorkingHeater(ServerLevel level, AnimalBuildingRecord building) {
        if (building == null) {
            return false;
        }
        return hasUtilityBlockEntityInBuilding(level, building, HeaterBlockEntity.class);
    }

    private boolean hasUtilityBlockEntityInBuilding(ServerLevel level, AnimalBuildingRecord building, Class<? extends BlockEntity> utilityType) {
        for (int y = building.minY(); y <= building.maxY(); y++) {
            for (int z = building.minZ(); z <= building.maxZ(); z++) {
                for (int x = building.minX(); x <= building.maxX(); x++) {
                    BlockEntity be = level.getBlockEntity(new BlockPos(x, y, z));
                    if (utilityType.isInstance(be)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean hasBuildingOwnerProfession(AnimalBuildingRecord building, int professionId) {
        if (building == null || professionId < 0) {
            return false;
        }
        ProfessionType profession = ProfessionType.fromId(professionId);
        if (profession == null) {
            return false;
        }

        UUID ownerUuid;
        try {
            ownerUuid = UUID.fromString(building.ownerPlayerUuid());
        } catch (IllegalArgumentException ex) {
            return false;
        }
        return PlayerDataManager.getPlayerData(ownerUuid).hasProfession(profession);
    }

    private void applyAutoPetterEffect(FarmAnimalRecord record, AnimalProfile profile, boolean hasHappinessProfession) {
        if (record.wasPetToday()) {
            return;
        }

        int autoPetReduction = 7;
        if (record.wasAutoPetToday()) {
            record.addFriendship(autoPetReduction);
        } else {
            record.addFriendship(15 - autoPetReduction);
        }
        record.setWasAutoPetToday(true);

        int happinessGain = Math.max(5, 30 + profile.happinessDrain());
        if (hasHappinessProfession) {
            record.addFriendship(15);
            record.addHappiness(happinessGain);
        }
        record.addHappiness(happinessGain);
    }

    private boolean isEntityInsideBuilding(BaseCoopAnimalEntity entity, AnimalBuildingRecord building) {
        if (entity == null || building == null) {
            return false;
        }

        if (building.isInBounds(entity.blockPosition())) {
            return true;
        }

        AABB box = entity.getBoundingBox();
        int minX = (int) Math.floor(box.minX);
        int minY = (int) Math.floor(box.minY);
        int minZ = (int) Math.floor(box.minZ);
        int maxX = (int) Math.floor(box.maxX - 1.0E-4D);
        int maxY = (int) Math.floor(box.maxY - 1.0E-4D);
        int maxZ = (int) Math.floor(box.maxZ - 1.0E-4D);

        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    cursor.set(x, y, z);
                    if (building.isInBounds(cursor)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
}
