package com.stardew.craft.menu;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import javax.annotation.Nonnull;

@SuppressWarnings("null")
public class CookingPotMenu extends AbstractContainerMenu {

    public CookingPotMenu(int containerId, Inventory playerInventory) {
        super(ModMenuTypes.COOKING_POT.get(), containerId);
        
        // 我们给上方留出足够的高度用于绘制漂亮的UI，比如140像素
        // 原版箱子的物品栏Y坐标通常是开始于自定义界面的下方。
        // 假设我们的自定义界面高度为 Y_OFFSET - 某些值，我们把Y位移设置得往下一大截
        // UI宽度增加到 360，高度为 260，排版空间更奢侈
        // 背包宽度 162。 X偏移 = (360 - 162)/2 = 99
        int xOffset = 99;
        int yOffset = 166;

        // 玩家背包 (3行)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, xOffset + col * 18, yOffset + row * 18));
            }
        }
        
        // 玩家快捷栏
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, xOffset + col * 18, yOffset + 58));
        }
    }

    @Override
    public boolean stillValid(@Nonnull Player player) {
        return true;
    }

    @Override
    public @Nonnull ItemStack quickMoveStack(@Nonnull Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        
        if (slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            result = stackInSlot.copy();
            
            // 只有玩家的格子，在快捷栏和背包之间移动
            if (index < 27) {
                // 从背包移到快捷栏
                if (!this.moveItemStackTo(stackInSlot, 27, 36, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // 从快捷栏移到背包
                if (!this.moveItemStackTo(stackInSlot, 0, 27, false)) {
                    return ItemStack.EMPTY;
                }
            }
            
            if (stackInSlot.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        
        return result;
    }
}
