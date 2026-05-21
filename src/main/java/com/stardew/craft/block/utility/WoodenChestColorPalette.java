package com.stardew.craft.block.utility;

public final class WoodenChestColorPalette {
    public static final int[] COLORS_RGB = new int[] {
        0x2B2A28,
        0x4659A8,
        0x5FAFD3,
        0x2F8F94,
        0x5ABF9A,
        0x5E8E3E,
        0x9FBE45,
        0xE7C957,
        0xD88B36,
        0xC75A32,
        0xB8443E,
        0x7A2734,
        0xDEA0B3,
        0xC96A9D,
        0x9652A8,
        0x6D56B0,
        0x4B367A,
        0x3F3F3C,
        0x70706A,
        0xBFB8A8,
        0xF2EEE2
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
