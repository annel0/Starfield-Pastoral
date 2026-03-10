package com.stardew.craft.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public final class UtilityDropHelper {
    private UtilityDropHelper() {
    }

    @SuppressWarnings("null")
    public static void dropAutomationContents(Level level, BlockPos pos) {
        if (level.isClientSide) {
            return;
        }
        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof UtilityAutomationAccess access)) {
            return;
        }

        ItemStack drop = access.isAutomationReady()
            ? access.getAutomationOutput()
            : access.getAutomationInput();
        if (!drop.isEmpty()) {
            Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), drop.copy());
        }

        ItemStack extra = access.getAutomationExtraDrop();
        if (!extra.isEmpty()) {
            Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), extra.copy());
        }
    }
}
