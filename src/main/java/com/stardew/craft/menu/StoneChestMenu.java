package com.stardew.craft.menu;

import com.stardew.craft.block.utility.WoodenChestColorPalette;
import com.stardew.craft.blockentity.StoneChestBlockEntity;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

@SuppressWarnings("null")
public class StoneChestMenu extends AbstractContainerMenu {
    private static final int ROWS = 6;
    private static final int COLS = 9;
    private static final int CHEST_SIZE = ROWS * COLS;

    private final Container container;
    @Nullable
    private final StoneChestBlockEntity chestEntity;
    private int colorSelection;

    public StoneChestMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(CHEST_SIZE), null);
    }

    public StoneChestMenu(int containerId, Inventory playerInventory, Container container, @Nullable StoneChestBlockEntity chestEntity) {
        super(ModMenuTypes.STONE_CHEST.get(), containerId);
        this.container = container;
        this.chestEntity = chestEntity;
        this.colorSelection = chestEntity != null ? chestEntity.getColorSelection() : -1;

        checkContainerSize(container, CHEST_SIZE);
        container.startOpen(playerInventory.player);

        this.addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return StoneChestMenu.this.chestEntity != null ? StoneChestMenu.this.chestEntity.getColorSelection() : StoneChestMenu.this.colorSelection;
            }

            @Override
            public void set(int value) {
                StoneChestMenu.this.colorSelection = WoodenChestColorPalette.clampIndex(value);
            }
        });

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                this.addSlot(new Slot(container, col + row * COLS, 8 + col * 18, 18 + row * 18));
            }
        }

        int playerInvY = 140;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < COLS; col++) {
                this.addSlot(new Slot(playerInventory, col + row * COLS + COLS, 8 + col * 18, playerInvY + row * 18));
            }
        }

        int hotbarY = 198;
        for (int col = 0; col < COLS; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, hotbarY));
        }
    }

    public int getColorSelection() {
        return colorSelection;
    }

    public void setClientPreviewColorSelection(int selection) {
        colorSelection = WoodenChestColorPalette.clampIndex(selection);
    }

    public void setColorSelectionFromClient(int selection) {
        if (chestEntity == null) {
            return;
        }
        chestEntity.setColorSelection(selection);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack stackInSlot = slot.getItem();
        result = stackInSlot.copy();

        if (index < CHEST_SIZE) {
            if (!this.moveItemStackTo(stackInSlot, CHEST_SIZE, this.slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else if (!this.moveItemStackTo(stackInSlot, 0, CHEST_SIZE, false)) {
            return ItemStack.EMPTY;
        }

        if (stackInSlot.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        return result;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.container.stopOpen(player);
    }
}
