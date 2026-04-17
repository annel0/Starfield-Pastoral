package com.stardew.craft.blockentity;

import com.stardew.craft.animal.data.AnimalWorldData;
import com.stardew.craft.animal.model.AnimalBuildingRecord;
import com.stardew.craft.block.animal.AnimalProduceSpotBlock;
import com.stardew.craft.block.utility.AutoGrabberBlock;
import com.stardew.craft.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.sounds.SoundSource;

import javax.annotation.Nullable;
import java.util.Objects;

@SuppressWarnings("null")
public class AutoGrabberBlockEntity extends BlockEntity implements UtilityAutomationAccess, Container, MenuProvider {
    private static final String TAG_BUILDING_ID = "buildingId";
    private static final String TAG_ITEMS = "items";
    private static final int SLOT_COUNT = 36;
    private static final int COLLECT_INTERVAL_TICKS = 20;

    private final NonNullList<ItemStack> items = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);
    private String buildingId = "";
    private int openCount = 0;

    public AutoGrabberBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.AUTO_GRABBER.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, AutoGrabberBlockEntity blockEntity) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        if (serverLevel.getGameTime() % COLLECT_INTERVAL_TICKS != 0) {
            return;
        }

        AnimalBuildingRecord building = blockEntity.resolveBuilding(serverLevel);
        if (building == null) {
            return;
        }

        int collected = blockEntity.collectBuildingProduce(serverLevel, building);
        if (collected > 0) {
            blockEntity.setChanged();
            blockEntity.syncToClient();
        }
    }

    @Nullable
    private AnimalBuildingRecord resolveBuilding(ServerLevel level) {
        AnimalWorldData data = AnimalWorldData.get(level);
        if (!buildingId.isBlank()) {
            AnimalBuildingRecord existing = data.getBuilding(buildingId).orElse(null);
            if (existing != null
                && Objects.equals(existing.dimensionId(), level.dimension().location().toString())
                && existing.isInBounds(worldPosition)) {
                return existing;
            }
        }

        for (AnimalBuildingRecord candidate : data.getBuildings()) {
            if (!Objects.equals(candidate.dimensionId(), level.dimension().location().toString())) {
                continue;
            }
            if (!candidate.isInBounds(worldPosition)) {
                continue;
            }
            if (!Objects.equals(buildingId, candidate.buildingId())) {
                buildingId = candidate.buildingId();
                setChanged();
                syncToClient();
            }
            return candidate;
        }

        if (!buildingId.isBlank()) {
            buildingId = "";
            setChanged();
            syncToClient();
        }
        return null;
    }

    private int collectBuildingProduce(ServerLevel level, AnimalBuildingRecord building) {
        int collected = 0;
        if (!building.interiorAirCells().isEmpty()) {
            for (Long cell : building.interiorAirCells()) {
                BlockPos targetPos = BlockPos.of(cell);
                collected += tryCollectAt(level, targetPos, building);
            }
        } else {
            for (int y = building.minY(); y <= building.maxY(); y++) {
                for (int z = building.minZ(); z <= building.maxZ(); z++) {
                    for (int x = building.minX(); x <= building.maxX(); x++) {
                        collected += tryCollectAt(level, new BlockPos(x, y, z), building);
                    }
                }
            }
        }
        return collected;
    }

    private int tryCollectAt(ServerLevel level, BlockPos targetPos, AnimalBuildingRecord building) {
        BlockState targetState = level.getBlockState(targetPos);
        if (!(targetState.getBlock() instanceof AnimalProduceSpotBlock)) {
            return 0;
        }

        BlockEntity be = level.getBlockEntity(targetPos);
        if (!(be instanceof AnimalProduceSpotBlockEntity produceBe)) {
            return 0;
        }

        ItemStack produce = produceBe.getProduceStack();
        if (produce.isEmpty()) {
            return 0;
        }
        if (!Objects.equals(produceBe.getBuildingId(), building.buildingId())) {
            return 0;
        }

        ItemStack remainder = insertIntoStorage(produce, false);
        if (remainder.isEmpty()) {
            level.removeBlock(targetPos, false);
            return produce.getCount();
        }
        return 0;
    }

    private ItemStack extractUpTo(int amount, boolean simulate) {
        if (amount <= 0) {
            return ItemStack.EMPTY;
        }
        for (int i = 0; i < items.size(); i++) {
            ItemStack stack = items.get(i);
            if (stack.isEmpty()) {
                continue;
            }
            int taken = Math.min(amount, stack.getCount());
            ItemStack out = stack.copy();
            out.setCount(taken);
            if (!simulate) {
                if (taken >= stack.getCount()) {
                    items.set(i, ItemStack.EMPTY);
                } else {
                    stack.shrink(taken);
                }
                setChanged();
                syncToClient();
            }
            return out;
        }
        return ItemStack.EMPTY;
    }

    private ItemStack insertIntoStorage(ItemStack stack, boolean simulate) {
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }

        ItemStack remaining = stack.copy();

        for (int i = 0; i < items.size(); i++) {
            ItemStack slot = items.get(i);
            if (slot.isEmpty() || !ItemStack.isSameItemSameComponents(slot, remaining)) {
                continue;
            }
            int max = slot.getMaxStackSize();
            int canAdd = Math.min(max - slot.getCount(), remaining.getCount());
            if (canAdd <= 0) {
                continue;
            }
            if (!simulate) {
                slot.grow(canAdd);
            }
            remaining.shrink(canAdd);
            if (remaining.isEmpty()) {
                if (!simulate) {
                    setChanged();
                }
                return ItemStack.EMPTY;
            }
        }

        for (int i = 0; i < items.size(); i++) {
            ItemStack slot = items.get(i);
            if (!slot.isEmpty()) {
                continue;
            }
            int toPut = Math.min(remaining.getCount(), remaining.getMaxStackSize());
            if (!simulate) {
                ItemStack newStack = remaining.copy();
                newStack.setCount(toPut);
                items.set(i, newStack);
            }
            remaining.shrink(toPut);
            if (remaining.isEmpty()) {
                if (!simulate) {
                    setChanged();
                }
                return ItemStack.EMPTY;
            }
        }

        if (!simulate) {
            setChanged();
            syncToClient();
        }
        return remaining;
    }

    private boolean hasAnyItem() {
        for (ItemStack item : items) {
            if (!item.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public ItemStack getAutomationInput() {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack getAutomationOutput() {
        for (ItemStack item : items) {
            if (!item.isEmpty()) {
                return item;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack insertAutomation(ItemStack stack, boolean simulate) {
        return insertIntoStorage(stack, simulate);
    }

    @Override
    public ItemStack extractAutomation(int amount, boolean simulate) {
        return extractUpTo(amount, simulate);
    }

    @Override
    public int getAutomationSlotLimit(int slot) {
        return 64;
    }

    @Override
    public int getContainerSize() {
        return SLOT_COUNT;
    }

    @Override
    public boolean isEmpty() {
        return !hasAnyItem();
    }

    @Override
    public ItemStack getItem(int slot) {
        if (slot < 0 || slot >= items.size()) {
            return ItemStack.EMPTY;
        }
        return items.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        if (slot < 0 || slot >= items.size() || amount <= 0) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = items.get(slot);
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        int removed = Math.min(amount, stack.getCount());
        ItemStack out = stack.copy();
        out.setCount(removed);
        if (removed >= stack.getCount()) {
            items.set(slot, ItemStack.EMPTY);
        } else {
            stack.shrink(removed);
        }
        setChanged();
        syncToClient();
        return out;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        if (slot < 0 || slot >= items.size()) {
            return ItemStack.EMPTY;
        }
        ItemStack out = items.get(slot);
        items.set(slot, ItemStack.EMPTY);
        setChanged();
        syncToClient();
        return out;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (slot < 0 || slot >= items.size()) {
            return;
        }
        if (stack.isEmpty()) {
            items.set(slot, ItemStack.EMPTY);
        } else {
            ItemStack copy = stack.copy();
            copy.setCount(Math.min(copy.getCount(), copy.getMaxStackSize()));
            items.set(slot, copy);
        }
        setChanged();
        syncToClient();
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return true;
    }

    @Override
    public void clearContent() {
        for (int i = 0; i < items.size(); i++) {
            items.set(i, ItemStack.EMPTY);
        }
        setChanged();
        syncToClient();
    }

    public void dropAllContents(Level level, BlockPos pos) {
        if (level.isClientSide) {
            return;
        }
        SimpleContainer container = new SimpleContainer(items.toArray(new ItemStack[0]));
        Containers.dropContents(level, pos, container);
        clearContent();
        setChanged();
        syncToClient();
    }

    @Override
    public void startOpen(Player player) {
        if (player.isSpectator()) {
            return;
        }
        openCount++;
        if (openCount == 1 && level != null) {
            level.playSound(null, worldPosition, ModSounds.OPEN_CHEST.get(), SoundSource.BLOCKS, 0.7f, 1.0f);
        }
    }

    @Override
    public void stopOpen(Player player) {
        if (player.isSpectator()) {
            return;
        }
        openCount = Math.max(0, openCount - 1);
        if (openCount == 0 && level != null) {
            level.playSound(null, worldPosition, ModSounds.DOOR_CREAK_REVERSE.get(), SoundSource.BLOCKS, 0.7f, 1.0f);
        }
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.stardew_craft.auto_grabber");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return ChestMenu.threeRows(containerId, playerInventory, this);
    }

    private void syncToClient() {
        Level currentLevel = level;
        if (currentLevel == null || currentLevel.isClientSide) {
            return;
        }

        BlockState state = getBlockState();
        if (state.getBlock() instanceof AutoGrabberBlock autoGrabber && state.hasProperty(AutoGrabberBlock.FULL)) {
            boolean fullNow = hasAnyItem();
            if (state.getValue(AutoGrabberBlock.FULL) != fullNow) {
                BlockState mainUpdated = state.setValue(AutoGrabberBlock.FULL, fullNow);
                currentLevel.setBlock(worldPosition, mainUpdated, 3);

                BlockPos extensionPos = AutoGrabberBlock.getExtensionPos(worldPosition, mainUpdated);
                BlockState extensionState = currentLevel.getBlockState(extensionPos);
                if (extensionState.is(autoGrabber) && extensionState.hasProperty(AutoGrabberBlock.FULL)) {
                    currentLevel.setBlock(extensionPos, extensionState.setValue(AutoGrabberBlock.FULL, fullNow), 3);
                }
            }
        }

        currentLevel.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 11);
        if (currentLevel instanceof ServerLevel serverLevel) {
            serverLevel.getChunkSource().blockChanged(worldPosition);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putString(TAG_BUILDING_ID, buildingId);
        ListTag list = new ListTag();
        for (int i = 0; i < items.size(); i++) {
            ItemStack stack = items.get(i);
            if (stack.isEmpty()) {
                continue;
            }
            CompoundTag entry = new CompoundTag();
            entry.putInt("Slot", i);
            entry.put("Stack", stack.save(registries));
            list.add(entry);
        }
        tag.put(TAG_ITEMS, list);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        buildingId = tag.contains(TAG_BUILDING_ID) ? tag.getString(TAG_BUILDING_ID) : "";
        for (int i = 0; i < items.size(); i++) {
            items.set(i, ItemStack.EMPTY);
        }
        if (tag.contains(TAG_ITEMS, 9)) {
            ListTag list = tag.getList(TAG_ITEMS, 10);
            for (int i = 0; i < list.size(); i++) {
                CompoundTag entry = list.getCompound(i);
                int slot = entry.getInt("Slot");
                if (slot < 0 || slot >= items.size()) {
                    continue;
                }
                ItemStack parsed = ItemStack.parse(registries, entry.getCompound("Stack")).orElse(ItemStack.EMPTY);
                items.set(slot, parsed);
            }
        }
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(net.minecraft.core.HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        saveAdditional(tag, registries);
        return tag;
    }
}
