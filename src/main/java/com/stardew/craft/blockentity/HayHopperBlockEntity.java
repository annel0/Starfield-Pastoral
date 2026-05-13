package com.stardew.craft.blockentity;

import com.stardew.craft.animal.data.AnimalWorldData;
import com.stardew.craft.block.utility.HayHopperBlock;
import com.stardew.craft.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class HayHopperBlockEntity extends net.minecraft.world.level.block.entity.BlockEntity {
    private static final String TAG_OWNER = "ownerPlayerUuid";
    private static final Set<String> FEED_BUILDING_FAMILIES = Set.of("coop", "barn");

    @Nullable
    private UUID ownerPlayerId;

    public HayHopperBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.HAY_HOPPER.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, HayHopperBlockEntity blockEntity) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        if ((serverLevel.getGameTime() + pos.asLong()) % 20L != 0L) {
            return;
        }
        blockEntity.syncFullState(serverLevel);
    }

    public void setOwnerIfAbsent(UUID owner) {
        if (ownerPlayerId != null) {
            return;
        }
        ownerPlayerId = owner;
        setChanged();
    }

    @Nullable
    public UUID getOwnerPlayerId() {
        return ownerPlayerId;
    }

    @SuppressWarnings("null")
    public int extractHayToPlayer(Player player) {
        Level currentLevel = level;
        if (!(currentLevel instanceof ServerLevel serverLevel)) {
            return 0;
        }

        AnimalWorldData worldData = AnimalWorldData.get(serverLevel);
        UUID owner = resolveStorageOwner(player, worldData, serverLevel);
        if (owner == null) {
            return 0;
        }

        int requested = getRequestedHayAmount(owner, worldData);
        int removed = worldData.takeHay(owner, requested);
        if (removed <= 0) {
            syncFullState(serverLevel);
            return 0;
        }

        ItemStack stack = new ItemStack((ItemLike) ModItems.HAY.get(), removed);
        if (!player.addItem(stack)) {
            player.drop(stack, false);
        }

        syncFullState(serverLevel);
        return removed;
    }

    @Nullable
    public UUID resolveStorageOwner(@Nullable Player player) {
        Level currentLevel = level;
        if (!(currentLevel instanceof ServerLevel serverLevel)) {
            return ownerPlayerId != null ? ownerPlayerId : player == null ? null : player.getUUID();
        }
        return resolveStorageOwner(player, AnimalWorldData.get(serverLevel), serverLevel);
    }

    @Nullable
    private UUID resolveStorageOwner(@Nullable Player player, AnimalWorldData worldData, ServerLevel serverLevel) {
        Optional<com.stardew.craft.animal.model.AnimalBuildingRecord> building = worldData.findBuildingAtAnyOwner(
            serverLevel.dimension().location().toString(),
            worldPosition,
            FEED_BUILDING_FAMILIES
        );
        if (building.isPresent()) {
            return rememberResolvedOwner(building.get().ownerPlayerUuid());
        }

        UUID farmOwner = com.stardew.craft.core.FarmAreaResolver.getOwnerAt(worldPosition);
        if (farmOwner != null) {
            rememberResolvedOwner(farmOwner);
            return farmOwner;
        }

        if (ownerPlayerId != null) {
            return ownerPlayerId;
        }
        if (player == null) {
            return null;
        }
        setOwnerIfAbsent(player.getUUID());
        return ownerPlayerId;
    }

    @Nullable
    private UUID rememberResolvedOwner(String ownerUuid) {
        try {
            UUID resolved = UUID.fromString(ownerUuid);
            rememberResolvedOwner(resolved);
            return resolved;
        } catch (IllegalArgumentException ex) {
            return ownerPlayerId;
        }
    }

    private void rememberResolvedOwner(UUID resolved) {
        if (!resolved.equals(ownerPlayerId)) {
            ownerPlayerId = resolved;
            setChanged();
        }
    }

    private int getRequestedHayAmount(UUID owner, AnimalWorldData worldData) {
        int requested = 1;
        if (!worldData.hasAnySilo(owner)) {
            return requested;
        }
        Level currentLevel = level;
        if (!(currentLevel instanceof ServerLevel serverLevel)) {
            return requested;
        }
        Optional<com.stardew.craft.animal.model.AnimalBuildingRecord> building = worldData.findBuildingAtAnyOwner(
            serverLevel.dimension().location().toString(),
            worldPosition,
            FEED_BUILDING_FAMILIES
        );
        if (building.isEmpty()) {
            return requested;
        }
        int animalCount = building.get().memberAnimalIds().size();
        return Math.max(1, animalCount);
    }

    @SuppressWarnings("null")
    private void syncFullState(ServerLevel level) {
        BlockState state = getBlockState();
        if (!(state.getBlock() instanceof HayHopperBlock)) {
            return;
        }
        AnimalWorldData data = AnimalWorldData.get(level);
        UUID owner = resolveStorageOwner(null, data, level);
        boolean shouldBeFull = owner != null && data.getHayAmount(owner) > 0;
        boolean current = state.getValue(HayHopperBlock.FULL);
        if (current != shouldBeFull) {
            level.setBlock(worldPosition, state.setValue(HayHopperBlock.FULL, shouldBeFull), 3);
        }

        BlockPos extensionPos = worldPosition.above();
        BlockState extensionState = level.getBlockState(extensionPos);
        if (extensionState.getBlock() instanceof HayHopperBlock && extensionState.getValue(HayHopperBlock.PART) == HayHopperBlock.Part.EXTENSION) {
            boolean extCurrent = extensionState.getValue(HayHopperBlock.FULL);
            if (extCurrent != shouldBeFull) {
                level.setBlock(extensionPos, extensionState.setValue(HayHopperBlock.FULL, shouldBeFull), 3);
            }
        }
    }

    @SuppressWarnings("null")
    @Override
    protected void saveAdditional(@SuppressWarnings("null") CompoundTag tag, @SuppressWarnings("null") net.minecraft.core.HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (ownerPlayerId != null) {
            tag.putUUID(TAG_OWNER, ownerPlayerId);
        }
    }

    @SuppressWarnings("null")
    @Override
    protected void loadAdditional(@SuppressWarnings("null") CompoundTag tag, @SuppressWarnings("null") net.minecraft.core.HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        ownerPlayerId = tag.hasUUID(TAG_OWNER) ? tag.getUUID(TAG_OWNER) : null;
    }
}