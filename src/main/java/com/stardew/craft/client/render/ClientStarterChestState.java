package com.stardew.craft.client.render;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

/**
 * Client-side state for the starter chest hint.
 * When set, PortalHintRenderer draws a golden hint at this position.
 */
public final class ClientStarterChestState {

    private ClientStarterChestState() {}

    @Nullable
    private static BlockPos hintPos = null;

    public static void setHintPos(BlockPos pos) {
        hintPos = pos;
    }

    public static void clear() {
        hintPos = null;
    }

    @Nullable
    public static BlockPos getHintPos() {
        return hintPos;
    }

    @Nullable
    public static Vec3 getHintVec() {
        return hintPos == null ? null : Vec3.atBottomCenterOf(hintPos);
    }
}
