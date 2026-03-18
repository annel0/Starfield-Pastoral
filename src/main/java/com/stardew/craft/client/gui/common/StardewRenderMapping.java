package com.stardew.craft.client.gui.common;

/**
 * Mapping layer between Stardew's pixel-space UI assumptions and Minecraft's GUI-space rendering.
 */
public final class StardewRenderMapping {
    private final int screenWidth;
    private final int screenHeight;
    private final float guiScale;

    public StardewRenderMapping(int screenWidth, int screenHeight, float guiScale) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.guiScale = Math.max(1.0f, guiScale);
    }

    // Maps Stardew pixel unit to MC GUI unit.
    public int ui(int stardewPixels) {
        return Math.round(stardewPixels / guiScale);
    }

    // Equivalent of Stardew's pixelZoom(4) translated to current GUI scale.
    public float s4() {
        return 4.0f / guiScale;
    }

    public int centerX(int width) {
        return screenWidth / 2 - width / 2;
    }

    public int bottomY(int height, int bottomMarginSdvPx) {
        return screenHeight - height - ui(bottomMarginSdvPx);
    }
}