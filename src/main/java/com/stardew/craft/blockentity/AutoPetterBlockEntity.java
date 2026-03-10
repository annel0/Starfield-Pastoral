package com.stardew.craft.blockentity;

import com.stardew.craft.animal.data.AnimalWorldData;
import com.stardew.craft.animal.model.AnimalBuildingRecord;
import com.stardew.craft.block.utility.AutoPetterBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Objects;

public class AutoPetterBlockEntity extends net.minecraft.world.level.block.entity.BlockEntity implements GeoBlockEntity {
    private static final String TAG_BUILDING_ID = "buildingId";
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final int CHECK_INTERVAL_TICKS = 20;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private String buildingId = "";
    private boolean working;

    public AutoPetterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.AUTO_PETTER.get(), pos, state);
    }

    @SuppressWarnings("null")
    public static void serverTick(Level level, BlockPos pos, BlockState state, AutoPetterBlockEntity be) {
        if (!(level instanceof ServerLevel serverLevel) || serverLevel.getGameTime() % CHECK_INTERVAL_TICKS != 0) {
            return;
        }
        be.refreshWorkingState(serverLevel, pos, state);
    }

    public boolean isWorking() {
        return working;
    }

    @SuppressWarnings("null")
    private void refreshWorkingState(ServerLevel level, BlockPos pos, BlockState state) {
        AnimalBuildingRecord building = resolveSupportedBuilding(level, pos);
        boolean workingNow = building != null;
        if (working != workingNow) {
            working = workingNow;
            setChanged();
        }

        if (state.hasProperty(AutoPetterBlock.WORKING) && state.getValue(AutoPetterBlock.WORKING) != workingNow) {
            level.setBlock(pos, state.setValue(AutoPetterBlock.WORKING, workingNow), 3);
        }
    }

    private AnimalBuildingRecord resolveSupportedBuilding(ServerLevel level, BlockPos pos) {
        AnimalWorldData data = AnimalWorldData.get(level);
        if (!buildingId.isBlank()) {
            AnimalBuildingRecord existing = data.getBuilding(buildingId).orElse(null);
            if (existing != null
                && Objects.equals(existing.dimensionId(), level.dimension().location().toString())
                && existing.isWithinBoundingBox(pos)
                && isSupportedFamily(existing)) {
                return existing;
            }
        }

        for (AnimalBuildingRecord candidate : data.getBuildings()) {
            if (!Objects.equals(candidate.dimensionId(), level.dimension().location().toString())) {
                continue;
            }
            if (!candidate.isWithinBoundingBox(pos) || !isSupportedFamily(candidate)) {
                continue;
            }
            if (!Objects.equals(buildingId, candidate.buildingId())) {
                buildingId = candidate.buildingId();
                setChanged();
            }
            return candidate;
        }

        if (!buildingId.isBlank()) {
            buildingId = "";
            setChanged();
        }
        return null;
    }

    private boolean isSupportedFamily(AnimalBuildingRecord building) {
        String family = building.buildingType().family();
        return "coop".equalsIgnoreCase(family) || "barn".equalsIgnoreCase(family);
    }

    @SuppressWarnings("null")
    @Override
    protected void saveAdditional(@SuppressWarnings("null") CompoundTag tag, @SuppressWarnings("null") net.minecraft.core.HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (!buildingId.isBlank()) {
            tag.putString(TAG_BUILDING_ID, buildingId);
        }
        tag.putBoolean("working", working);
    }

    @SuppressWarnings("null")
    @Override
    protected void loadAdditional(@SuppressWarnings("null") CompoundTag tag, @SuppressWarnings("null") net.minecraft.core.HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        buildingId = tag.getString(TAG_BUILDING_ID);
        working = tag.getBoolean("working");
    }

    @SuppressWarnings("null")
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "main", 5, state -> {
            BlockState blockState = getBlockState();
            boolean shouldAnimate = blockState.hasProperty(AutoPetterBlock.WORKING) && blockState.getValue(AutoPetterBlock.WORKING);
            if (shouldAnimate) {
                state.setAndContinue(IDLE);
                return PlayState.CONTINUE;
            }
            return PlayState.STOP;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}