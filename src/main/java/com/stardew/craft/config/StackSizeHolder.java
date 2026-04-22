package com.stardew.craft.config;

/**
 * Runtime holder for the effective max stack size.
 *
 * <p>This lives outside the mixin package so regular mod code and mixins can
 * both reference it safely on NeoForge and hybrid servers such as Mohist.</p>
 */
public final class StackSizeHolder {
    private static volatile int maxStackSize = 999;

    public static int get() {
        return maxStackSize;
    }

    public static void set(int value) {
        maxStackSize = Math.max(1, Math.min(999, value));
    }

    private StackSizeHolder() {
    }
}