package com.stardew.craft.menu;

import com.stardew.craft.specialorder.SpecialOrderDropBoxAnchor;
import com.stardew.craft.specialorder.SpecialOrderManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

@SuppressWarnings("null")
public class SpecialOrderDropBoxMenu extends AbstractContainerMenu {
    public static final int ROWS = 4;
    public static final int COLS = 9;
    public static final int DROPBOX_SIZE = ROWS * COLS;

    private final Container container;
    @Nullable
    private final SpecialOrderDropBoxAnchor anchor;
    private boolean committed;

    public SpecialOrderDropBoxMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(DROPBOX_SIZE), null);
    }

    public SpecialOrderDropBoxMenu(int containerId, Inventory playerInventory, SpecialOrderDropBoxAnchor anchor) {
        this(containerId, playerInventory, new SimpleContainer(DROPBOX_SIZE), anchor);
    }

    private SpecialOrderDropBoxMenu(int containerId, Inventory playerInventory, Container container,
                                    @Nullable SpecialOrderDropBoxAnchor anchor) {
        super(ModMenuTypes.SPECIAL_ORDER_DROPBOX.get(), containerId);
        this.container = container;
        this.anchor = anchor;

        checkContainerSize(container, DROPBOX_SIZE);
        container.startOpen(playerInventory.player);

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                this.addSlot(new Slot(container, col + row * COLS, 8 + col * 18, 18 + row * 18) {
                    @Override
                    public boolean mayPlace(ItemStack stack) {
                        return SpecialOrderDropBoxMenu.this.mayDonate(playerInventory.player, stack);
                    }
                });
            }
        }

        int playerInvY = 18 + ROWS * 18 + 13;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < COLS; col++) {
                this.addSlot(new Slot(playerInventory, col + row * COLS + COLS, 8 + col * 18, playerInvY + row * 18));
            }
        }

        int hotbarY = playerInvY + 58;
        for (int col = 0; col < COLS; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, hotbarY));
        }
    }

    private boolean mayDonate(Player player, ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        if (anchor == null) {
            return true;
        }
        return player instanceof ServerPlayer serverPlayer
            ? SpecialOrderManager.canDonateToDropBox(serverPlayer, anchor.dropBoxId(), stack)
            : true;
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

        if (index < DROPBOX_SIZE) {
            if (!this.moveItemStackTo(stackInSlot, DROPBOX_SIZE, this.slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else {
            if (!mayDonate(player, stackInSlot) || !this.moveItemStackTo(stackInSlot, 0, DROPBOX_SIZE, false)) {
                return ItemStack.EMPTY;
            }
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
        return true;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        if (!committed && anchor != null && player instanceof ServerPlayer serverPlayer) {
            committed = true;
            SpecialOrderManager.confirmDropBoxDonations(serverPlayer, anchor, container);
        }
        clearContainer(player, container);
        container.stopOpen(player);
    }
}
