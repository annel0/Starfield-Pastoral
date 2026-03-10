package com.stardew.craft.client.weapon;

import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;

public final class WeaponSkillAnimationClient {

    private static long startTick = -1;
    private static int durationTicks = 0;
    private static String weaponId;
    private static String skillId;
    private static Vec3 dragonBreathOrigin;
    private static Vec3 dragonBreathDir;
    private static long dragonBreathTick = -9999;
    private static Vec3 windSpireOrigin;
    private static long windSpireTick = -9999;

    private WeaponSkillAnimationClient() {}

    public static void start(int durationTicks) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            return;
        }
        start(null, null, durationTicks);
    }

    @SuppressWarnings("null")
    public static void start(String weaponId, String skillId, int durationTicks) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            return;
        }
        WeaponSkillAnimationClient.startTick = mc.level.getGameTime();
        WeaponSkillAnimationClient.durationTicks = Math.max(1, durationTicks);
        WeaponSkillAnimationClient.weaponId = weaponId;
        WeaponSkillAnimationClient.skillId = skillId;

        if ("dragon_breath_thrust".equals(skillId) && mc.level != null) {
            var player = mc.player;
            if (player == null) {
                return;
            }
            long now = mc.level.getGameTime();
            if (now - dragonBreathTick > 2) {
                dragonBreathOrigin = player.position();
                Vec3 look = player.getLookAngle();
                dragonBreathDir = new Vec3(look.x, 0.0, look.z);
                if (dragonBreathDir.lengthSqr() < 1.0E-4) {
                    dragonBreathDir = look;
                }
                dragonBreathDir = dragonBreathDir.normalize();
                dragonBreathTick = now;
            }
        }

        if ("wind_spire_thrust".equals(skillId) && mc.level != null) {
            var player = mc.player;
            if (player == null) {
                return;
            }
            long now = mc.level.getGameTime();
            if (now - windSpireTick > 2) {
                windSpireOrigin = player.position();
                windSpireTick = now;
            }
        }
        
        // 播放技能特效（粒子+声音）
        if (skillId != null && mc.player != null) {
            SkillEffectsClient.playSkillEffects(skillId, mc.player);
        }
    }

    public static Vec3 getDragonBreathOrigin() {
        return dragonBreathOrigin;
    }

    public static Vec3 getDragonBreathDir() {
        return dragonBreathDir;
    }

    public static long getDragonBreathTick() {
        return dragonBreathTick;
    }

    public static Vec3 getWindSpireOrigin() {
        return windSpireOrigin;
    }

    public static long getWindSpireTick() {
        return windSpireTick;
    }

    @SuppressWarnings("null")
    public static float getProgress(float partialTick) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || startTick < 0 || durationTicks <= 0) {
            return -1.0f;
        }
        float age = (mc.level.getGameTime() - startTick) + partialTick;
        float t = age / durationTicks;
        if (t >= 1.0f) {
            startTick = -1;
            durationTicks = 0;
            weaponId = null;
            skillId = null;
            return -1.0f;
        }
        return t;
    }

    public static boolean isActive() {
        return startTick >= 0 && durationTicks > 0;
    }

    public static void stop() {
        startTick = -1;
        durationTicks = 0;
        weaponId = null;
        skillId = null;
    }

    public static String getWeaponId() {
        return weaponId;
    }

    public static String getSkillId() {
        return skillId;
    }
}
