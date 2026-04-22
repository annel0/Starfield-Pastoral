package com.stardew.craft.cutscene.runtime;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * Simple screen fade overlay for the cutscene event system.
 * Separate from the CC {@link com.stardew.craft.communitycenter.cutscene.ScreenFade}
 * to avoid coupling.
 */
@OnlyIn(Dist.CLIENT)
public final class EventScreenFade {

    private EventScreenFade() {}

    private static float alpha = 0f;
    private static float alphaPerTick = 0f;
    private static boolean fadingOut = false;
    private static boolean active = false;

    public static void startFadeToBlack(int ticks) {
        alpha = 0f;
        alphaPerTick = 1.0f / ticks;
        fadingOut = true;
        active = true;
    }

    public static void startFadeFromBlack(int ticks) {
        alpha = 1f;
        alphaPerTick = 1.0f / ticks;
        fadingOut = false;
        active = true;
    }

    public static void tick() {
        if (!active) return;
        if (fadingOut) {
            alpha += alphaPerTick;
            if (alpha >= 1f) {
                alpha = 1f;
            }
        } else {
            alpha -= alphaPerTick;
            if (alpha <= 0f) {
                alpha = 0f;
                active = false;
            }
        }
    }

    public static void render(GuiGraphics g) {
        if (!active && alpha <= 0.001f) return;

        Minecraft mc = Minecraft.getInstance();
        int w = mc.getWindow().getGuiScaledWidth();
        int h = mc.getWindow().getGuiScaledHeight();

        int a = (int) (alpha * 255) << 24;
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        g.fill(0, 0, w, h, a); // black with alpha
        RenderSystem.disableBlend();
    }

    public static void clear() {
        alpha = 0f;
        active = false;
    }

    public static boolean isActive() {
        return active;
    }
}
