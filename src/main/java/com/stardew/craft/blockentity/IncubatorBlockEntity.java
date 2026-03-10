package com.stardew.craft.blockentity;

import com.stardew.craft.item.artisan.ArtisanRecipeDataManager;
import com.stardew.craft.animal.data.AnimalWorldData;
import com.stardew.craft.animal.model.AnimalBuildingRecord;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import javax.annotation.Nullable;

public class IncubatorBlockEntity extends TimedProductionBlockEntity {
    private static final String TAG_INPUT = "input";
    private static final String TAG_READY_AT = "readyAtAbsMinute";
    private static final String TAG_READY = "ready";

    public record RemainingTime(int days, int hours, int minutes) {
    }

    public IncubatorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.INCUBATOR.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, IncubatorBlockEntity be) {
        if (level.isClientSide) {
            return;
        }

        if (!be.isInsideActiveAnimalBuilding(level, pos)) {
            be.updateWorkingState(level, pos, state);
            return;
        }

        boolean newReady = be.refreshReady();
        if (newReady != be.ready) {
            be.ready = newReady;
            be.setChanged();
            be.syncToClient();
        }
        be.updateWorkingState(level, pos, state);
    }

    private boolean isInsideActiveAnimalBuilding(Level level, BlockPos pos) {
        if (!(level instanceof net.minecraft.server.level.ServerLevel serverLevel)) {
            return true;
        }

        String dim = serverLevel.dimension().location().toString();
        for (AnimalBuildingRecord record : AnimalWorldData.get(serverLevel).getBuildings()) {
            if (!dim.equals(record.dimensionId())) {
                continue;
            }
            String family = record.buildingType().family();
            if (!("coop".equalsIgnoreCase(family) || "barn".equalsIgnoreCase(family))) {
                continue;
            }
            if (record.isInBounds(pos)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected boolean readyCheckRequiresProduct() {
        return false;
    }

    public boolean isReady() {
        return ready;
    }

    public boolean isWorking() {
        return !input.isEmpty() && !ready && readyAtAbsMinute > 0;
    }

    public boolean hasInput() {
        return !input.isEmpty();
    }

    public ItemStack getInput() {
        return input;
    }

    public RemainingTime getRemainingTime() {
        long remaining = getRemainingAbsMinutes();
        int days = (int) (remaining / EFFECTIVE_MINUTES_PER_DAY);
        int minutesRemainder = (int) (remaining % EFFECTIVE_MINUTES_PER_DAY);
        int hours = minutesRemainder / com.stardew.craft.time.StardewTimeManager.MINUTES_PER_HOUR;
        int minutes = minutesRemainder % com.stardew.craft.time.StardewTimeManager.MINUTES_PER_HOUR;
        return new RemainingTime(days, hours, minutes);
    }

    public boolean tryInsert(ItemStack stack, Player player) {
        return tryInsertWithResult(stack, player).inserted();
    }

    @SuppressWarnings("null")
    public InsertResult tryInsertWithResult(ItemStack stack, Player player) {
        if (stack.isEmpty()) {
            return InsertResult.fail();
        }
        if (!input.isEmpty() || readyAtAbsMinute >= 0) {
            return InsertResult.fail();
        }
        var recipeOpt = ArtisanRecipeDataManager.getRecipe("incubator", stack);
        if (recipeOpt.isEmpty()) {
            return InsertResult.fail();
        }
        ArtisanRecipeDataManager.Recipe recipe = recipeOpt.get();
        startIncubation(stack, recipe.minutes(), player);
        return InsertResult.success();
    }

    private void startIncubation(ItemStack inputStack, int minutesUntilReady, Player player) {
        input = inputStack.copy();
        input.setCount(1);
        product = ItemStack.EMPTY;
        readyAtAbsMinute = getCurrentAbsMinute() + minutesUntilReady;
        ready = false;
        if (player == null || !player.isCreative()) {
            inputStack.shrink(1);
        }
        setChanged();
        syncToClient();
    }

    public void completeIncubation() {
        if (!ready) {
            return;
        }
        // TODO: 接入动物系统后，在这里生成孵化后的动物并触发相关事件。
        input = ItemStack.EMPTY;
        product = ItemStack.EMPTY;
        readyAtAbsMinute = -1;
        ready = false;
        setChanged();
        syncToClient();
    }

    @Override
    public ItemStack getAutomationInput() {
        return input;
    }

    @Override
    public ItemStack getAutomationOutput() {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack insertAutomation(ItemStack stack, boolean simulate) {
        if (stack.isEmpty() || !input.isEmpty() || readyAtAbsMinute >= 0) {
            return stack;
        }
        var recipeOpt = ArtisanRecipeDataManager.getRecipe("incubator", stack);
        if (recipeOpt.isEmpty()) {
            return stack;
        }
        if (simulate) {
            return AutomationStackHelper.remainderAfterInsert(stack, 1);
        }
        ItemStack inputCopy = stack.copy();
        startIncubation(inputCopy, recipeOpt.get().minutes(), null);
        return AutomationStackHelper.remainderAfterInsert(stack, 1);
    }

    @Override
    public ItemStack extractAutomation(int amount, boolean simulate) {
        return ItemStack.EMPTY;
    }

    @SuppressWarnings("null")
    private void updateWorkingState(Level level, BlockPos pos, BlockState state) {
        BooleanProperty workingProp = com.stardew.craft.block.utility.IncubatorBlock.WORKING;
        boolean workingNow = isWorking();
        if (state.hasProperty(workingProp) && state.getValue(workingProp) != workingNow) {
            level.setBlock(pos, state.setValue(workingProp, workingNow), 3);
            BlockPos extensionPos = com.stardew.craft.block.utility.IncubatorBlock.getExtensionPos(pos, state);
            BlockState extensionState = level.getBlockState(extensionPos);
            if (extensionState.is(state.getBlock()) && extensionState.hasProperty(workingProp)) {
                level.setBlock(extensionPos, extensionState.setValue(workingProp, workingNow), 3);
            }
        }
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @SuppressWarnings("null")
    @Override
    public CompoundTag getUpdateTag(@SuppressWarnings("null") net.minecraft.core.HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        saveAdditional(tag, registries);
        return tag;
    }

    @SuppressWarnings("null")
    @Override
    protected void saveAdditional(@SuppressWarnings("null") CompoundTag tag, @SuppressWarnings("null") net.minecraft.core.HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (!input.isEmpty()) {
            tag.put(TAG_INPUT, input.save(registries));
        }
        tag.putLong(TAG_READY_AT, readyAtAbsMinute);
        tag.putBoolean(TAG_READY, ready);
    }

    @SuppressWarnings("null")
    @Override
    protected void loadAdditional(@SuppressWarnings("null") CompoundTag tag, @SuppressWarnings("null") net.minecraft.core.HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        input = tag.contains(TAG_INPUT) ? ItemStack.parse(registries, tag.getCompound(TAG_INPUT)).orElse(ItemStack.EMPTY) : ItemStack.EMPTY;
        product = ItemStack.EMPTY;
        readyAtAbsMinute = tag.getLong(TAG_READY_AT);
        ready = tag.getBoolean(TAG_READY);
    }
}
