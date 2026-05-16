package com.stardew.craft.menu;

import com.stardew.craft.blockentity.ShippingBinBlockEntity;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

@SuppressWarnings("null")
public class ShippingBinMenu extends AbstractContainerMenu {
    private static final int BIN_SLOTS = 1;

    private final Container container;

    public ShippingBinMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(BIN_SLOTS));
    }

    public ShippingBinMenu(int containerId, Inventory playerInventory, Container container) {
        super(ModMenuTypes.SHIPPING_BIN.get(), containerId);
        this.container = container;

        checkContainerSize(container, BIN_SLOTS);
        container.startOpen(playerInventory.player);

        // Keep the temporary shipping buffer slot in the visual center.
        this.addSlot(new Slot(container, 0, 80, 18) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return ShippingBinBlockEntity.canShip(stack);
            }
        });

        int playerInvY = 49;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, playerInvY + row * 18));
            }
        }

        int hotbarY = 107;
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, hotbarY));
        }
    }

    @Override
    public void clicked(int slotId, int button, ClickType clickType, Player player) {
        if (slotId == 0 && clickType == ClickType.PICKUP) {
            ItemStack carried = getCarried();
            if (!carried.isEmpty() && ShippingBinBlockEntity.canShip(carried)) {
                ItemStack toShip = carried.copy();
                if (button == 1) {
                    toShip.setCount(1);
                    carried.shrink(1);
                    setCarried(carried.isEmpty() ? ItemStack.EMPTY : carried);
                } else {
                    setCarried(ItemStack.EMPTY);
                }

                if (this.container instanceof ShippingBinBlockEntity bin) {
                    bin.depositFromPlayer(player, toShip);
                } else {
                    this.container.setItem(0, toShip);
                    this.container.setChanged();
                }
                return;
            }
        }

        super.clicked(slotId, button, clickType, player);
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

        if (index < BIN_SLOTS) {
            if (!this.moveItemStackTo(stackInSlot, BIN_SLOTS, this.slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else {
            if (!ShippingBinBlockEntity.canShip(stackInSlot)) {
                return ItemStack.EMPTY;
            }
            if (this.container instanceof ShippingBinBlockEntity bin) {
                bin.depositFromPlayer(player, stackInSlot);
                stackInSlot.setCount(0);
                slot.set(ItemStack.EMPTY);
                slot.setChanged();
                return ItemStack.EMPTY;
            } else if (!this.moveItemStackTo(stackInSlot, 0, BIN_SLOTS, false)) {
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
        return this.container.stillValid(player);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.container.stopOpen(player);
    }
}
