package com.stardew.craft.client.render;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

/**
 * Client-side cache of this player's active ore-pan point (if any).
 * Populated by {@link com.stardew.craft.network.payload.PanPointSyncPayload}.
 */
public final class ClientPanPointState {

    private ClientPanPointState() {}

    @Nullable
    private static BlockPos point = null;

    public static void setPoint(BlockPos pos) { point = pos; }
    public static void clear() { point = null; }

    @Nullable
    public static BlockPos getPoint() { return point; }

    @Nullable
    public static Vec3 getCenterVec() {
        BlockPos p = point;
        return p == null ? null : new Vec3(p.getX() + 0.5, p.getY() + 1.0, p.getZ() + 0.5);
    }
}
