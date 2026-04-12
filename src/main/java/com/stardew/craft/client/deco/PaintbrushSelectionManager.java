package com.stardew.craft.client.deco;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;

/**
 * Client-side singleton managing paintbrush selection mode state.
 * Tracks mode (flood-fill vs region-select), two corners, and smooth animation targets.
 */
public final class PaintbrushSelectionManager {
    private static final PaintbrushSelectionManager INSTANCE = new PaintbrushSelectionManager();

    public enum Mode {
        FLOOD_FILL,
        REGION_SELECT
    }

    private Mode mode = Mode.FLOOD_FILL;

    @Nullable private BlockPos firstPos;
    @Nullable private BlockPos secondPos;

    // Smoothly interpolated AABB for rendering
    @Nullable private AABB displayAABB;
    @Nullable private AABB targetAABB;

    // Animation timing
    private long lastTickMs;
    private static final float CHASE_SPEED = 0.4f;

    private PaintbrushSelectionManager() {}

    public static PaintbrushSelectionManager get() {
        return INSTANCE;
    }

    // ---- Mode ----

    public Mode getMode() {
        return mode;
    }

    public void cycleMode(int direction) {
        Mode[] values = Mode.values();
        int next = (mode.ordinal() + direction + values.length) % values.length;
        mode = values[next];
        clearSelection();
    }

    // ---- Selection ----

    @Nullable
    public BlockPos getFirstPos() {
        return firstPos;
    }

    @Nullable
    public BlockPos getSecondPos() {
        return secondPos;
    }

    public void setFirstPos(BlockPos pos) {
        this.firstPos = pos.immutable();
        this.secondPos = null;
        updateTargetAABB();
    }

    public void setSecondPos(BlockPos pos) {
        this.secondPos = pos.immutable();
        updateTargetAABB();
    }

    public boolean hasFirstPos() {
        return firstPos != null;
    }

    public boolean hasCompleteSelection() {
        return firstPos != null && secondPos != null;
    }

    public void clearSelection() {
        firstPos = null;
        secondPos = null;
        targetAABB = null;
        displayAABB = null;
    }

    // ---- AABB for rendering ----

    @Nullable
    public AABB getDisplayAABB() {
        return displayAABB;
    }

    /**
     * Called every client tick to smoothly interpolate the display AABB towards the target.
     */
    @SuppressWarnings("null")
    public void tick() {
        long now = System.currentTimeMillis();
        if (lastTickMs == 0) {
            lastTickMs = now;
        }
        lastTickMs = now;

        if (targetAABB == null) {
            displayAABB = null;
            return;
        }

        if (displayAABB == null) {
            displayAABB = targetAABB;
            return;
        }

        // Smooth chase towards target
        displayAABB = new AABB(
            lerp(CHASE_SPEED, displayAABB.minX, targetAABB.minX),
            lerp(CHASE_SPEED, displayAABB.minY, targetAABB.minY),
            lerp(CHASE_SPEED, displayAABB.minZ, targetAABB.minZ),
            lerp(CHASE_SPEED, displayAABB.maxX, targetAABB.maxX),
            lerp(CHASE_SPEED, displayAABB.maxY, targetAABB.maxY),
            lerp(CHASE_SPEED, displayAABB.maxZ, targetAABB.maxZ)
        );
    }

    /**
     * Update the target AABB when a look position changes (before second click is confirmed).
     * Used for live preview: first corner is fixed, second corner follows crosshair.
     */
    @SuppressWarnings("null")
    public void updatePreviewTarget(@Nullable BlockPos lookPos) {
        if (firstPos == null || lookPos == null) {
            return;
        }
        int minX = Math.min(firstPos.getX(), lookPos.getX());
        int minY = Math.min(firstPos.getY(), lookPos.getY());
        int minZ = Math.min(firstPos.getZ(), lookPos.getZ());
        int maxX = Math.max(firstPos.getX(), lookPos.getX()) + 1;
        int maxY = Math.max(firstPos.getY(), lookPos.getY()) + 1;
        int maxZ = Math.max(firstPos.getZ(), lookPos.getZ()) + 1;
        targetAABB = new AABB(minX, minY, minZ, maxX, maxY, maxZ);
    }

    // ---- Internal ----

    @SuppressWarnings("null")
    private void updateTargetAABB() {
        if (firstPos == null) {
            targetAABB = null;
            return;
        }
        BlockPos a = firstPos;
        BlockPos b = secondPos != null ? secondPos : firstPos;
        int minX = Math.min(a.getX(), b.getX());
        int minY = Math.min(a.getY(), b.getY());
        int minZ = Math.min(a.getZ(), b.getZ());
        int maxX = Math.max(a.getX(), b.getX()) + 1;
        int maxY = Math.max(a.getY(), b.getY()) + 1;
        int maxZ = Math.max(a.getZ(), b.getZ()) + 1;
        targetAABB = new AABB(minX, minY, minZ, maxX, maxY, maxZ);
    }

    private static double lerp(float t, double a, double b) {
        return a + t * (b - a);
    }
}
