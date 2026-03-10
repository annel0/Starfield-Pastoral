package com.stardew.craft.client.weapon;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;

/**
 * 客户端镜头抖动/轻微FOV反馈。
 */
public final class CameraShakeState {
    private static int remainingTicks = 0;
    private static int totalTicks = 0;
    private static float strength = 0.0f;
    private static float fovBoost = 0.0f;

    private CameraShakeState() {}

    public static void kick(float strengthIn, int ticks, float fovBoostIn) {
        if (ticks <= 0 || strengthIn <= 0.0f) {
            return;
        }
        totalTicks = Math.max(totalTicks, ticks);
        remainingTicks = Math.max(remainingTicks, ticks);
        strength = Math.max(strength, strengthIn);
        fovBoost = Math.max(fovBoost, fovBoostIn);
    }

    public static void tick() {
        if (remainingTicks > 0) {
            remainingTicks--;
            if (remainingTicks == 0) {
                totalTicks = 0;
                strength = 0.0f;
                fovBoost = 0.0f;
            }
        }
    }

    public static boolean isActive() {
        return remainingTicks > 0;
    }

    public static float getYawOffset(float partialTick) {
        float amp = getAmplitude(partialTick);
        if (amp <= 0.0f) {
            return 0.0f;
        }
        float time = getTime(partialTick);
        return Mth.sin(time * 12.0f) * amp * 1.8f;
    }

    public static float getPitchOffset(float partialTick) {
        float amp = getAmplitude(partialTick);
        if (amp <= 0.0f) {
            return 0.0f;
        }
        float time = getTime(partialTick);
        return Mth.cos(time * 15.0f) * amp * 1.2f;
    }

    public static float getFovDelta(float partialTick) {
        if (remainingTicks <= 0 || fovBoost <= 0.0f) {
            return 0.0f;
        }
        float t = getProgress(partialTick);
        float envelope = Mth.sin((float) Math.PI * t);
        return fovBoost * envelope;
    }

    private static float getAmplitude(float partialTick) {
        if (remainingTicks <= 0 || strength <= 0.0f) {
            return 0.0f;
        }
        float t = getProgress(partialTick);
        float envelope = Mth.sin((float) Math.PI * t);
        return strength * envelope;
    }

    private static float getProgress(float partialTick) {
        if (remainingTicks <= 0 || totalTicks <= 0) {
            return 0.0f;
        }
        float elapsed = (totalTicks - remainingTicks) + partialTick;
        float t = elapsed / (float) totalTicks;
        return Mth.clamp(t, 0.0f, 1.0f);
    }

    private static float getTime(float partialTick) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            return 0.0f;
        }
        return mc.level.getGameTime() + partialTick;
    }
}
