package com.stardew.craft.client.ritual;

import com.mojang.blaze3d.systems.RenderSystem;
import com.stardew.craft.client.sound.StardewMusicManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@SuppressWarnings("null")
public final class GalaxySwordRitualClientState {

    private static final int FLASH_TICKS = 18;
    private static final int SPARKLE_COUNT = 500;
    private static final int SPARKLE_MAX_LIFE_TICKS = 40;
    private static final List<Sparkle> SPARKLES = new ArrayList<>();

    private static boolean active;
    private static boolean sparkleBurstSpawned;
    private static int ritualDurationTicks;
    private static int elapsedTicks;
    private static int flashTicksRemaining;

    private GalaxySwordRitualClientState() {}

    public static void start(int durationTicks) {
        active = true;
        sparkleBurstSpawned = false;
        ritualDurationTicks = durationTicks;
        elapsedTicks = 0;
        flashTicksRemaining = 0;
        SPARKLES.clear();

        Minecraft mc = Minecraft.getInstance();
        if (mc != null) {
            mc.getMusicManager().stopPlaying();
        }
        StardewMusicManager.stopAll();
    }

    public static void tick() {
        if (!active) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.player == null || mc.level == null) {
            return;
        }

        if (!sparkleBurstSpawned) {
            sparkleBurstSpawned = true;
            for (int i = 0; i < SPARKLE_COUNT; i++) {
                SPARKLES.add(Sparkle.create(mc));
            }
        }

        if (isPlayerFrozen()) {
            mc.player.setDeltaMovement(Vec3.ZERO);
            elapsedTicks++;
            if (elapsedTicks >= ritualDurationTicks) {
                elapsedTicks = ritualDurationTicks;
                flashTicksRemaining = FLASH_TICKS;
            }
        } else if (flashTicksRemaining > 0) {
            flashTicksRemaining--;
        }

        Iterator<Sparkle> iterator = SPARKLES.iterator();
        while (iterator.hasNext()) {
            Sparkle sparkle = iterator.next();
            sparkle.life--;
            if (sparkle.life <= 0) {
                iterator.remove();
            }
        }

        if (!isPlayerFrozen() && flashTicksRemaining <= 0 && SPARKLES.isEmpty()) {
            clear();
        }
    }

    public static boolean isPlayerFrozen() {
        return active && elapsedTicks < ritualDurationTicks;
    }

    public static void render(GuiGraphics graphics) {
        if (!active) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        int width = mc.getWindow().getGuiScaledWidth();
        int height = mc.getWindow().getGuiScaledHeight();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        if (isPlayerFrozen()) {
            float pulse = 0.42f + 0.18f * (float) Math.sin(elapsedTicks / 3.0D);
            int alpha = Mth.clamp((int) (pulse * 255.0f), 0, 255);
            graphics.fill(0, 0, width, height, (alpha << 24) | 0x1E0096);
        }

        for (Sparkle sparkle : SPARKLES) {
            int alpha = Mth.clamp((int) ((sparkle.life / (float) sparkle.maxLife) * 255.0f), 0, 255);
            graphics.fill(sparkle.x, sparkle.y, sparkle.x + sparkle.size, sparkle.y + sparkle.size, (alpha << 24) | 0xFFFFFF);
        }

        if (flashTicksRemaining > 0) {
            int flashAlpha = Mth.clamp((int) ((flashTicksRemaining / (float) FLASH_TICKS) * 255.0f), 0, 255);
            graphics.fill(0, 0, width, height, (flashAlpha << 24) | 0xFFFFFF);
        }

        RenderSystem.disableBlend();
    }

    public static void clear() {
        active = false;
        sparkleBurstSpawned = false;
        ritualDurationTicks = 0;
        elapsedTicks = 0;
        flashTicksRemaining = 0;
        SPARKLES.clear();
    }

    private static final class Sparkle {
        private final int x;
        private final int y;
        private final int size;
        private final int maxLife;
        private int life;

        private Sparkle(int x, int y, int size, int life) {
            this.x = x;
            this.y = y;
            this.size = size;
            this.life = life;
            this.maxLife = life;
        }

        private static Sparkle create(Minecraft mc) {
            int width = mc.getWindow().getGuiScaledWidth();
            int height = mc.getWindow().getGuiScaledHeight();
            int size = 1 + mc.level.random.nextInt(3);
            int life = 8 + mc.level.random.nextInt(SPARKLE_MAX_LIFE_TICKS - 7);
            int x = mc.level.random.nextInt(Math.max(1, width - size));
            int y = mc.level.random.nextInt(Math.max(1, height - size));
            return new Sparkle(x, y, size, life);
        }
    }
}