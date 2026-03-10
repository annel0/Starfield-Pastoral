package com.stardew.craft.fishing;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * 钓鱼宝箱菜单 - 9x4网格，类似原版箱子
 */
public class TreasureChestMenu extends AbstractContainerMenu {
	private static final int CHEST_ROWS = 4;
	private static final int CHEST_COLS = 9;
	private static final int CHEST_SIZE = CHEST_ROWS * CHEST_COLS;
	
	private final Container container;
	private final boolean isGolden;

	@SuppressWarnings("null")
	public TreasureChestMenu(int containerId, Inventory playerInventory, Container container, boolean isGolden) {
		super(null, containerId);  // MenuType为null，因为这是临时容器
		this.container = container;
		this.isGolden = isGolden;
		
		// 宝箱格子 (4行 x 9列)
		for (int row = 0; row < CHEST_ROWS; row++) {
			for (int col = 0; col < CHEST_COLS; col++) {
				int index = row * CHEST_COLS + col;
				this.addSlot(new Slot(container, index, 8 + col * 18, 18 + row * 18));
			}
		}
		
		// 玩家背包 (3行)
		int yOffset = 18 + CHEST_ROWS * 18 + 14;  // 宝箱下方留点间隔
		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 9; col++) {
				this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, yOffset + row * 18));
			}
		}
		
		// 玩家快捷栏
		for (int col = 0; col < 9; col++) {
			this.addSlot(new Slot(playerInventory, col, 8 + col * 18, yOffset + 58));
		}
	}
	
	public boolean isGolden() {
		return isGolden;
	}

	@SuppressWarnings("null")
	@Override
	public @NotNull ItemStack quickMoveStack(@SuppressWarnings("null") @NotNull Player player, int index) {
		ItemStack result = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);
		
		if (slot.hasItem()) {
			ItemStack stackInSlot = slot.getItem();
			result = stackInSlot.copy();
			
			if (index < CHEST_SIZE) {
				// 从宝箱移到背包
				if (!this.moveItemStackTo(stackInSlot, CHEST_SIZE, this.slots.size(), true)) {
					return ItemStack.EMPTY;
				}
			} else {
				// 从背包移到宝箱（不允许）
				return ItemStack.EMPTY;
			}
			
			if (stackInSlot.isEmpty()) {
				slot.set(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}
		}
		
		return result;
	}

	@SuppressWarnings("null")
	@Override
	public boolean stillValid(@SuppressWarnings("null") @NotNull Player player) {
		return this.container.stillValid(player);
	}
	
	@SuppressWarnings("null")
	@Override
	public void removed(@SuppressWarnings("null") @NotNull Player player) {
		super.removed(player);
		// 关闭时清空容器（宝箱内物品消失）
		if (!player.level().isClientSide) {
			this.container.clearContent();
		}
	}
}
