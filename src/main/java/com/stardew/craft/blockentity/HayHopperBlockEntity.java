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

    @Nullable
    private UUID resolveOwner(Player player) {
        if (ownerPlayerId == null) {
            ownerPlayerId = player.getUUID();
            setChanged();
        }
        return ownerPlayerId;
    }

    @SuppressWarnings("null")
    public int extractHayToPlayer(Player player) {
        Level currentLevel = level;
        if (!(currentLevel instanceof ServerLevel serverLevel)) {
            return 0;
        }

        UUID owner = resolveOwner(player);
        if (owner == null) {
            return 0;
        }

        AnimalWorldData worldData = AnimalWorldData.get(serverLevel);
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

    private int getRequestedHayAmount(UUID owner, AnimalWorldData worldData) {
        int requested = 1;
        if (!worldData.hasAnySilo(owner)) {
            return requested;
        }
        Level currentLevel = level;
        if (!(currentLevel instanceof ServerLevel serverLevel)) {
            return requested;
        }
        Optional<com.stardew.craft.animal.model.AnimalBuildingRecord> building = worldData.findBuildingAt(
            serverLevel.dimension().location().toString(),
            worldPosition,
            owner,
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
        boolean shouldBeFull = AnimalWorldData.get(level).hasAnyStoredHay();
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