package com.stardew.craft.client.specialorder;

import com.stardew.craft.client.ClientPlayerDataCache;
import com.stardew.craft.client.hud.StardewTimeHud;
import com.stardew.craft.core.ModDimensions;
import com.stardew.craft.specialorder.SpecialOrderBoardInstaller;
import com.stardew.craft.specialorder.SpecialOrderManager;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;

public final class ClientSpecialOrderUnlockState {
    private static Boolean lastUnlocked;

    private ClientSpecialOrderUnlockState() {
    }

    public static boolean isUnlocked() {
        if (!ClientPlayerDataCache.isSynced() || !StardewTimeHud.isTimeSynced()) {
            return false;
        }
        return ClientPlayerDataCache.hasMailFlag(SpecialOrderManager.BOARD_UNLOCK_FLAG);
    }

    public static void refreshBoardRenderIfChanged() {
        boolean unlocked = isUnlocked();
        if (lastUnlocked != null && lastUnlocked == unlocked) {
            return;
        }
        lastUnlocked = unlocked;
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || !ModDimensions.STARDEW_VALLEY.equals(mc.level.dimension())) {
            return;
        }
        BlockPos pos = SpecialOrderBoardInstaller.BOARD_POS;
        mc.levelRenderer.setBlocksDirty(
            pos.getX() - 2, pos.getY() - 1, pos.getZ() - 2,
            pos.getX() + 2, pos.getY() + 3, pos.getZ() + 2);
    }
}
