package com.stardew.craft.client.weapon.animation;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;

public final class CarvingKnifeThrustAnimation implements WeaponSkillAnimation {

    private static final WeaponSkillPose BASE_RIGHT = new WeaponSkillPose(0f, -90f, 25f, 1.13f, 3.2f, 1.13f);
    private static final WeaponSkillPose BASE_LEFT = WeaponSkillPose.mirrorRightToLeft(BASE_RIGHT);

    private static final WeaponSkillPose P2_RIGHT = new WeaponSkillPose(-110.5f, -90f, 0f, 1.13f, 2.45f, -0.62f);
    private static final WeaponSkillPose P3_RIGHT = new WeaponSkillPose(-110.5f, -90f, 0f, 1.13f, 2.45f, -5.62f);

    private static final float T_ENTRY = 0.08f;
    private static final float CYCLE = 0.18f;
    private static final float TOTAL_CYCLES = 3.0f;

    @Override
    public boolean apply(PoseStack poseStack, HumanoidArm arm, float progress) {
        float t = Mth.clamp(progress, 0.0f, 1.0f);
        boolean right = arm == HumanoidArm.RIGHT;

        WeaponSkillPose base = right ? BASE_RIGHT : BASE_LEFT;
        WeaponSkillPose p1 = base;
        WeaponSkillPose p2 = right ? P2_RIGHT : WeaponSkillPose.mirrorRightToLeft(P2_RIGHT);
        WeaponSkillPose p3 = right ? P3_RIGHT : WeaponSkillPose.mirrorRightToLeft(P3_RIGHT);

        WeaponSkillPose out;
        float loopEnd = T_ENTRY + (CYCLE * TOTAL_CYCLES);
        if (t <= T_ENTRY) {
            float u = t / T_ENTRY;
            out = WeaponSkillPose.lerp(p1, p2, easeOutQuad(u));
        } else if (t <= loopEnd) {
            float u = (t - T_ENTRY) / CYCLE;
            float phase = u - (float) Math.floor(u);
            if (phase <= 0.4f) {
                float p = phase / 0.4f;
                out = WeaponSkillPose.lerp(p2, p3, easeInCubic(p));
            } else {
                float p = (phase - 0.4f) / 0.6f;
                out = WeaponSkillPose.lerp(p3, p2, easeOutQuad(p));
            }
        } else {
            float u = (t - loopEnd) / (1.0f - loopEnd);
            out = WeaponSkillPose.lerp(p2, p1, easeOutQuad(u));
        }

        WeaponSkillAnimationMath.applyDeltaFromBaseDisplay(poseStack, base, out);
        return true;
    }

    private static float easeInCubic(float t) {
        t = Mth.clamp(t, 0.0f, 1.0f);
        return t * t * t;
    }

    private static float easeOutQuad(float t) {
        t = Mth.clamp(t, 0.0f, 1.0f);
        float inv = 1.0f - t;
        return 1.0f - inv * inv;
    }
}
