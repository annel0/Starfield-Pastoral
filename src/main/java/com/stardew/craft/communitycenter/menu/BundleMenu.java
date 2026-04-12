package com.stardew.craft.communitycenter.menu;

import com.stardew.craft.communitycenter.data.BundleDataManager;
import com.stardew.craft.communitycenter.data.BundleDefinition;
import com.stardew.craft.communitycenter.data.BundleIngredient;
import com.stardew.craft.communitycenter.data.BundleItemResolver;
import com.stardew.craft.communitycenter.state.CommunityCenterSavedData;
import com.stardew.craft.item.quality.QualityHelper;
import com.stardew.craft.menu.ModMenuTypes;
import com.stardew.craft.player.PlayerStardewDataAPI;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Server-side container for the Community Center bundle interface.
 * Mirrors SDV JunimoNoteMenu's inventory + slot logic.
 *
 * Opened via /cc open [areaId] command (or JunimoNoteBlock interaction later).
 * The areaId is encoded as containerId data — for now this menu only hosts
 * the player inventory; all bundle rendering logic lives client-side in BundleScreen.
 */
@SuppressWarnings("null")
public class BundleMenu extends AbstractContainerMenu {

    /** Which area this menu was opened for (0-6). */
    private int areaId;

    // ── Client-side constructor (from network) ──
    public BundleMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, 1); // default, will be synced via DataSlot
    }

    // ── Server-side constructor ──
    public BundleMenu(int containerId, Inventory playerInventory, int areaId) {
        super(ModMenuTypes.BUNDLE.get(), containerId);
        this.areaId = areaId;

        // Sync areaId to client via DataSlot (automatic S↔C each tick)
        this.addDataSlot(new DataSlot() {
            @Override public int get() { return BundleMenu.this.areaId; }
            @Override public void set(int value) { BundleMenu.this.areaId = value; }
        });

        // SDV JunimoNoteMenu: inventory at (xPositionOnScreen + 128, yPositionOnScreen + 140)
        // In MC, slot positions are in GUI coords relative to screen top-left.
        // We use standard inventory layout; BundleScreen positions visually.
        int xOffset = 32;
        int yOffset = 140;

        // Player inventory (3 rows of 9)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9,
                        xOffset + col * 18, yOffset + row * 18));
            }
        }

        // Hotbar (1 row of 9)
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col,
                    xOffset + col * 18, yOffset + 58));
        }
    }

    public int getAreaId() {
        return areaId;
    }

    @Override
    public boolean stillValid(@Nonnull Player player) {
        return true;
    }

    @Override
    public @Nonnull ItemStack quickMoveStack(@Nonnull Player player, int index) {
        // Shift-click moves between inventory rows (identical to CookingPotMenu pattern)
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            result = stackInSlot.copy();

            if (index < 27) {
                if (!this.moveItemStackTo(stackInSlot, 27, 36, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
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

    // ── Server-side bundle deposit logic ──

    /**
     * Try to deposit an item into a bundle slot.
     * Called when the client sends a deposit request.
     *
     * @param player    the depositing player
     * @param bundleId  which bundle
     * @param slotIndex which ingredient slot
     * @param stack     the item to deposit
     * @return true if deposit succeeded
     */
    public boolean tryDeposit(Player player, int bundleId, int slotIndex, ItemStack stack) {
        if (player.level().isClientSide()) return false;

        BundleDefinition def = BundleDataManager.getBundle(bundleId);
        if (def == null || def.areaId() != this.areaId) return false;

        CommunityCenterSavedData data = CommunityCenterSavedData.get();
        if (data.isSlotComplete(bundleId, slotIndex)) return false;

        List<BundleIngredient> ingredients = def.ingredients();
        if (slotIndex < 0 || slotIndex >= ingredients.size()) return false;

        BundleIngredient ingredient = ingredients.get(slotIndex);
        if (!isValidItem(stack, ingredient)) return false;

        // Consume items
        int toConsume = ingredient.stack();
        if (stack.getCount() < toConsume) return false;

        stack.shrink(toConsume);
        data.markSlotComplete(bundleId, slotIndex);

        // Check bundle completion
        if (data.isBundleComplete(bundleId)) {
            data.setRewardAvailable(bundleId, true);
            data.markBundleAllSlotsComplete(bundleId);

            // Check area completion
            boolean allDone = true;
            for (BundleDefinition bd : BundleDataManager.getBundlesForArea(this.areaId)) {
                if (!data.isBundleComplete(bd.bundleId())) {
                    allDone = false;
                    break;
                }
            }
            if (allDone) {
                data.markAreaComplete(this.areaId);
            }
        }

        return true;
    }

    /**
     * Try to purchase a Vault bundle (money only).
     */
    public boolean tryPurchaseVault(Player player, int bundleId) {
        if (player.level().isClientSide()) return false;
        if (this.areaId != 4) return false;

        BundleDefinition def = BundleDataManager.getBundle(bundleId);
        if (def == null || !def.isVaultBundle()) return false;

        CommunityCenterSavedData data = CommunityCenterSavedData.get();
        if (data.isBundleComplete(bundleId)) return false;

        // Vault bundles: first ingredient is money
        BundleIngredient moneyIngredient = def.ingredients().get(0);
        if (!moneyIngredient.isMoneyIngredient()) return false;

        int cost = moneyIngredient.moneyRequired();

        // Deduct gold via PlayerStardewDataAPI
        if (!(player instanceof ServerPlayer sp)) return false;
        int currentMoney = PlayerStardewDataAPI.getMoney(sp);
        if (currentMoney < cost) return false;
        PlayerStardewDataAPI.removeMoney(sp, cost);

        data.markBundleAllSlotsComplete(bundleId);
        data.setRewardAvailable(bundleId, true);

        // Check area completion
        boolean allDone = true;
        for (BundleDefinition bd : BundleDataManager.getBundlesForArea(4)) {
            if (!data.isBundleComplete(bd.bundleId())) {
                allDone = false;
                break;
            }
        }
        if (allDone) {
            data.markAreaComplete(4);
        }

        return true;
    }

    /**
     * Mirrors SDV Bundle.IsValidItemForThisIngredientDescription().
     * Checks if an ItemStack matches an ingredient requirement.
     */
    public static boolean isValidItem(ItemStack stack, BundleIngredient ingredient) {
        if (stack.isEmpty()) return false;
        if (ingredient.isMoneyIngredient()) return false;

        // Quality check — SDV: item quality >= required quality
        int itemQuality = QualityHelper.getQuality(stack);
        if (itemQuality < ingredient.quality()) return false;

        // Stack size check
        if (stack.getCount() < ingredient.stack()) return false;

        // Category match (negative IDs like -4=fish, -5=egg, etc.)
        // Currently no category-based ingredients in bundle data (only -1=money handled above).
        // If added later, would need item tag matching here.
        if (ingredient.category() < -1) {
            return false;
        }

        // Exact item match
        if (ingredient.itemId() == null) return false;

        ItemStack required = BundleItemResolver.resolveItemStack(ingredient.itemId());
        if (required.isEmpty()) return false;

        return ItemStack.isSameItem(stack, required);
    }
}
