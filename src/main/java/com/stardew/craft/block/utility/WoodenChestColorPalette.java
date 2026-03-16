package com.stardew.craft.block.utility;

public final class WoodenChestColorPalette {
    public static final int[] COLORS_RGB = new int[] {
        0x2B2B2B, // Replaced 0x000000 to keep texture detail
        0x5555FF,
        0x77BFFF,
        0x00AAAA,
        0x00EAAF,
        0x00AA00,
        0x9FEC00,
        0xFFEA12,
        0xFFA712,
        0xFF6912,
        0xFF0000,
        0x870023,
        0xFFADC7,
        0xFF75C3,
        0xAC00C6,
        0x8F00FF,
        0x590B8E,
        0x404040,
        0x646464,
        0xC8C8C8,
        0xFEFEFE
    };

    private WoodenChestColorPalette() {
    }

    public static int size() {
        return COLORS_RGB.length;
    }

    public static int clampIndex(int index) {
        if (index < 0) {
            return -1;
        }
        if (index >= COLORS_RGB.length) {
            return COLORS_RGB.length - 1;
        }
        return index;
    }

    public static int rgbAt(int index) {
        if (index < 0) {
            return 0xFFFFFF; // default white/uncolored multiplier
        }
        return COLORS_RGB[clampIndex(index)];
    }
}
