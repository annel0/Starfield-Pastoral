package com.stardew.craft.blockentity;

import com.stardew.craft.time.StardewTimeManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;

public abstract class TimedProductionBlockEntity extends BlockEntity implements UtilityAutomationAccess, FairyDustAcceleratable, AdvanceableUtility {
    protected static final int EFFECTIVE_MINUTES_PER_DAY = 1260;

    protected ItemStack input = ItemStack.EMPTY;
    protected ItemStack product = ItemStack.EMPTY;
    protected long readyAtAbsMinute = -1;
    protected boolean ready = false;

    private long lastReadyCheckAbsMinute = Long.MIN_VALUE;
    private long lastReadyCheckReadyAt = Long.MIN_VALUE;
    private boolean lastReadyCheckHasProduct = false;
    private boolean lastReadyCheckResult = false;

    protected final UtilityItemHandler automationItemHandler = new UtilityItemHandler(this);

    protected TimedProductionBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    protected boolean readyCheckRequiresProduct() {
        return true;
    }

    protected boolean refreshReady() {
        if (readyAtAbsMinute < 0) {
            return false;
        }
        if (ready) {
            return true;
        }
        if (readyCheckRequiresProduct() && product.isEmpty()) {
            return false;
        }
        return computeReady();
    }

    protected boolean hasReadyPayload() {
        return readyCheckRequiresProduct() ? !product.isEmpty() : !input.isEmpty();
    }

    protected boolean computeReady() {
        boolean hasPayload = hasReadyPayload();
        if (!hasPayload || readyAtAbsMinute < 0) {
            lastReadyCheckAbsMinute = Long.MIN_VALUE;
            lastReadyCheckReadyAt = readyAtAbsMinute;
            lastReadyCheckHasProduct = hasPayload;
            lastReadyCheckResult = false;
            return false;
        }
        long currentAbsMinute = getCurrentAbsMinute();
        if (currentAbsMinute == lastReadyCheckAbsMinute
                && readyAtAbsMinute == lastReadyCheckReadyAt
                && lastReadyCheckHasProduct == hasPayload) {
            return lastReadyCheckResult;
        }
        boolean result = currentAbsMinute >= readyAtAbsMinute;
        lastReadyCheckAbsMinute = currentAbsMinute;
        lastReadyCheckReadyAt = readyAtAbsMinute;
        lastReadyCheckHasProduct = hasPayload;
        lastReadyCheckResult = result;
        return result;
    }

    public long getRemainingAbsMinutes() {
        if (!hasReadyPayload() || readyAtAbsMinute < 0) {
            return 0;
        }
        return Math.max(0, readyAtAbsMinute - getCurrentAbsMinute());
    }

    @Override
    public IItemHandler getAutomationItemHandler() {
        return automationItemHandler;
    }

    @Override
    public boolean canApplyFairyDust() {
        return false;
    }

    @Override
    public boolean applyFairyDust() {
        return false;
    }

    /**
     * Debug/utility: advance the current production timer by N days.
     */
    @Override
    @SuppressWarnings("null")
    public void advanceDays(int days) {
        if (days <= 0) {
            return;
        }
        Level currentLevel = level;
        if (currentLevel == null || currentLevel.isClientSide) {
            return;
        }
        if (!hasReadyPayload() || readyAtAbsMinute < 0) {
            return;
        }
        long delta = (long) days * (long) EFFECTIVE_MINUTES_PER_DAY;
        readyAtAbsMinute = Math.max(0, readyAtAbsMinute - delta);
        ready = computeReady();
        setChanged();
        syncToClient();
    }

    @SuppressWarnings("null")
    protected void syncToClient() {
        Level currentLevel = level;
        if (currentLevel == null || currentLevel.isClientSide) {
            return;
        }
        currentLevel.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }

    protected static long getCurrentAbsMinute() {
        StardewTimeManager tm = StardewTimeManager.get();
        int currentTime = tm.getCurrentTime();
        int effectiveMinuteOfDay;
        if (currentTime >= StardewTimeManager.MORNING_START) {
            effectiveMinuteOfDay = currentTime - StardewTimeManager.MORNING_START;
        } else {
            effectiveMinuteOfDay = 0;
        }
        return (getCurrentDayIndex() - 1) * EFFECTIVE_MINUTES_PER_DAY + (long) effectiveMinuteOfDay;
    }

    protected static long getCurrentDayIndex() {
        StardewTimeManager tm = StardewTimeManager.get();
        int year = tm.getCurrentYear();
        int season = tm.getCurrentSeason();
        int day = tm.getCurrentDay();
        return (long) (year - 1) * 112L + (long) season * 28L + (long) day;
    }
}
