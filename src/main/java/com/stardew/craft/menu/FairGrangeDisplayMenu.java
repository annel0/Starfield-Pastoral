package com.stardew.craft.menu;

import com.stardew.craft.festival.FairFestivalService;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

@SuppressWarnings("null")
public class FairGrangeDisplayMenu extends AbstractContainerMenu {
    public static final int ROWS = 3;
    public static final int COLS = 3;
    public static final int DISPLAY_SIZE = ROWS * COLS;

    private final Container container;
    private final Runnable onChanged;
    private final Predicate<Player> stillValidPredicate;

    public FairGrangeDisplayMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(DISPLAY_SIZE), () -> {}, player -> true);
    }

    public FairGrangeDisplayMenu(int containerId, Inventory playerInventory, Container container,
                                 Runnable onChanged, Predicate<Player> stillValidPredicate) {
        super(ModMenuTypes.FAIR_GRANGE_DISPLAY.get(), containerId);
        this.container = container;
        this.onChanged = onChanged;
        this.stillValidPredicate = stillValidPredicate;

        checkContainerSize(container, DISPLAY_SIZE);
        container.startOpen(playerInventory.player);

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                int slotIndex = col + row * COLS;
                this.addSlot(new DisplaySlot(container, slotIndex, 62 + col * 18, 17 + row * 18));
            }
        }

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
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

        if (index < DISPLAY_SIZE) {
            if (!this.moveItemStackTo(stackInSlot, DISPLAY_SIZE, this.slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else {
            if (!FairFestivalService.isEligibleGrangeDisplayItem(stackInSlot)
                || !this.moveItemStackTo(stackInSlot, 0, DISPLAY_SIZE, false)) {
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
        return stillValidPredicate.test(player);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        onChanged.run();
        container.stopOpen(player);
    }

    private class DisplaySlot extends Slot {
        DisplaySlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return FairFestivalService.isEligibleGrangeDisplayItem(stack);
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }

        @Override
        public int getMaxStackSize(ItemStack stack) {
            return 1;
        }

        @Override
        public void setChanged() {
            super.setChanged();
            FairGrangeDisplayMenu.this.onChanged.run();
        }
    }
}
