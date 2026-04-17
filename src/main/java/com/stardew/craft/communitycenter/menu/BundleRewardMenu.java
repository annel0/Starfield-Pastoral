package com.stardew.craft.communitycenter.menu;

import com.stardew.craft.communitycenter.data.BundleDataManager;
import com.stardew.craft.communitycenter.data.BundleDefinition;
import com.stardew.craft.communitycenter.network.BundleClaimRewardPayload;
import com.stardew.craft.communitycenter.state.CommunityCenterSavedData;
import com.stardew.craft.menu.ModMenuTypes;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * SDV parity: ItemGrabMenu for bundle rewards.
 * Shows reward items for completed bundles in an area; player takes items manually.
 * On slot pick-up, marks reward as claimed on the server.
 */
public class BundleRewardMenu extends AbstractContainerMenu {

    public static final int MAX_REWARD_SLOTS = 9;

    private final SimpleContainer rewardContainer;
    private int areaId;
    /** bundleId for each reward slot (-1 if empty) */
    private final int[] slotBundleIds = new int[MAX_REWARD_SLOTS];
    /** 记录菜单拥有者的UUID，用于per-player CC数据访问 */
    private java.util.UUID playerUUID;

    /** Client-side constructor (from MenuType factory) */
    public BundleRewardMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, 0);
    }

    /** Server-side constructor */
    @SuppressWarnings("null")
    public BundleRewardMenu(int containerId, Inventory playerInventory, int areaId) {
        super(ModMenuTypes.BUNDLE_REWARD.get(), containerId);
        this.areaId = areaId;
        this.rewardContainer = new SimpleContainer(MAX_REWARD_SLOTS);

        // DataSlot for areaId sync
        this.addDataSlot(new DataSlot() {
            @Override public int get() { return BundleRewardMenu.this.areaId; }
            @Override public void set(int value) { BundleRewardMenu.this.areaId = value; }
        });

        // Populate reward items (server-side only; client sees via slot sync)
        if (!playerInventory.player.level().isClientSide) {
            this.playerUUID = playerInventory.player.getUUID();
            populateRewards(areaId);
        }

        // Reward slots — 1 row of 9, standard chest layout
        for (int i = 0; i < MAX_REWARD_SLOTS; i++) {
            this.addSlot(new RewardSlot(rewardContainer, i, 8 + i * 18, 18));
        }

        // Player inventory (3×9) — vanilla 1-row chest: y = 1*18 + 31 = 49
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 49 + row * 18));
            }
        }

        // Hotbar — vanilla 1-row chest: y = 1*18 + 89 = 107
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 107));
        }
    }

    private void populateRewards(int areaId) {
        CommunityCenterSavedData data = CommunityCenterSavedData.get();
        List<BundleDefinition> bundles = BundleDataManager.getBundlesForArea(areaId);
        java.util.Arrays.fill(slotBundleIds, -1);

        int slot = 0;
        for (BundleDefinition def : bundles) {
            if (slot >= MAX_REWARD_SLOTS) break;
            if (data.isRewardAvailable(playerUUID, def.bundleId())) {
                ItemStack reward = BundleClaimRewardPayload.parseRewardString(def.rewardString());
                if (!reward.isEmpty()) {
                    rewardContainer.setItem(slot, reward);
                    slotBundleIds[slot] = def.bundleId();
                    slot++;
                }
            }
        }
    }

    public int getAreaId() {
        return areaId;
    }

    @SuppressWarnings("null")
    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            result = stackInSlot.copy();

            if (index < MAX_REWARD_SLOTS) {
                // Reward → player inventory
                if (!this.moveItemStackTo(stackInSlot, MAX_REWARD_SLOTS, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
                // Mark reward as claimed
                onRewardTaken(index);
            } else {
                // Don't allow moving items back into reward slots
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

    /** Called when a reward item is taken from a slot */
    private void onRewardTaken(int slotIndex) {
        if (slotIndex < 0 || slotIndex >= MAX_REWARD_SLOTS) return;
        int bundleId = slotBundleIds[slotIndex];
        if (bundleId >= 0) {
            CommunityCenterSavedData data = CommunityCenterSavedData.get();
            data.setRewardAvailable(playerUUID, bundleId, false);
            slotBundleIds[slotIndex] = -1;
        }
    }

    @SuppressWarnings("null")
    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }

    @SuppressWarnings("null")
    @Override
    public void removed(@NotNull Player player) {
        super.removed(player);
        // Unclaimed rewards stay in savedData — don't drop anything
        // (SDV: rewards persist until claimed)
    }

    /** Reward slot — take-only, marks bundle reward as claimed on pickup */
    @SuppressWarnings("null")
    private class RewardSlot extends Slot {
        public RewardSlot(SimpleContainer container, int index, int x, int y) {
            super(container, index, x, y);
        }

        @Override
        public boolean mayPlace(@NotNull ItemStack stack) {
            return false; // Can't put items in
        }

        @Override
        public void onTake(@NotNull Player player, @NotNull ItemStack stack) {
            super.onTake(player, stack);
            onRewardTaken(this.getContainerSlot());
        }
    }
}
