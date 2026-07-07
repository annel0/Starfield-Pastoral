package com.stardew.craft.client.festival;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class FairGrangeDisplayClientCache {
    private static final int MIN_X = 11;
    private static final int Y = 64;
    private static final int MIN_Z = -6;
    private static final int SIZE = 3;
    private static final Map<BlockPos, DisplayEntry> ENTRIES = new HashMap<>();
    private static boolean active;

    private FairGrangeDisplayClientCache() {
    }

    public static void apply(boolean activeDisplay, List<ItemStack> display) {
        active = activeDisplay;
        ENTRIES.clear();
        if (!activeDisplay) {
            return;
        }
        for (int index = 0; index < 9; index++) {
            ItemStack stack = index < display.size() ? display.get(index).copy() : ItemStack.EMPTY;
            ENTRIES.put(posForIndex(index), new DisplayEntry(stack, 180.0F));
        }
    }

    public static Optional<DisplayEntry> get(BlockPos pos) {
        if (!active || pos == null) {
            return Optional.empty();
        }
        BlockPos immutable = pos.immutable();
        if (!isGrangeDisplayPos(immutable)) {
            return Optional.empty();
        }
        return Optional.ofNullable(ENTRIES.getOrDefault(immutable, new DisplayEntry(ItemStack.EMPTY, 180.0F)));
    }

    public static boolean isGrangeDisplayPos(BlockPos pos) {
        return pos != null
            && pos.getY() == Y
            && pos.getX() >= MIN_X
            && pos.getX() < MIN_X + SIZE
            && pos.getZ() >= MIN_Z
            && pos.getZ() < MIN_Z + SIZE;
    }

    private static BlockPos posForIndex(int index) {
        int row = Math.floorDiv(index, SIZE);
        int col = Math.floorMod(index, SIZE);
        return new BlockPos(MIN_X + col, Y, MIN_Z + row);
    }

    public record DisplayEntry(ItemStack stack, float yawDegrees) {
    }
}
