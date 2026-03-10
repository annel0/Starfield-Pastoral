package com.stardew.craft.client.weapon.animation;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;

public final class RustySwordSkillAnimation implements WeaponSkillAnimation {

    private static final WeaponSkillPose BASE_RIGHT = new WeaponSkillPose(0f, -90f, 25f, 1.13f, 3.2f, 1.13f);
    private static final WeaponSkillPose BASE_LEFT = WeaponSkillPose.mirrorRightToLeft(BASE_RIGHT);

    private static final WeaponSkillPose P2_RIGHT = new WeaponSkillPose(-123f, -90f, 0f, 1.13f, 1.95f, -1.37f);
    private static final WeaponSkillPose P3_RIGHT = new WeaponSkillPose(-123f, -90f, 0f, 1.13f, 1.95f, -6.87f);

    private static final float T1 = 0.12f;
    private static final float T2 = 0.32f;
    private static final float T3 = 0.50f;

    @Override
    public boolean apply(PoseStack poseStack, HumanoidArm arm, float progress) {
        float t = Mth.clamp(progress, 0.0f, 1.0f);
        boolean right = arm == HumanoidArm.RIGHT;

        WeaponSkillPose base = right ? BASE_RIGHT : BASE_LEFT;
        WeaponSkillPose p1 = base;
        WeaponSkillPose p2 = right ? P2_RIGHT : WeaponSkillPose.mirrorRightToLeft(P2_RIGHT);
        WeaponSkillPose p3 = right ? P3_RIGHT : WeaponSkillPose.mirrorRightToLeft(P3_RIGHT);

        WeaponSkillPose out;
        if (t <= T1) {
            float u = t / T1;
            out = WeaponSkillPose.lerp(p1, p2, easeOutBack(u, 1.45f));
        } else if (t <= T2) {
            float u = (t - T1) / (T2 - T1);
            out = WeaponSkillPose.lerp(p2, p3, easeInCubic(u));
        } else if (t <= T3) {
            float u = (t - T2) / (T3 - T2);
            out = WeaponSkillPose.lerp(p3, p2, easeOutQuad(u));
        } else {
            float u = (t - T3) / (1.0f - T3);
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

    private static float easeOutBack(float t, float overshoot) {
        t = Mth.clamp(t, 0.0f, 1.0f);
        float x = t - 1.0f;
        return 1.0f + (overshoot + 1.0f) * x * x * x + overshoot * x * x;
    }
}
