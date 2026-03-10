package com.stardew.craft.client.weapon.animation;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;

public final class SteelSmallswordCounterAnimation implements WeaponSkillAnimation {

    private static final WeaponSkillPose BASE_RIGHT = new WeaponSkillPose(0f, -90f, 25f, 1.13f, 3.2f, 1.13f);
    private static final WeaponSkillPose BASE_LEFT = WeaponSkillPose.mirrorRightToLeft(BASE_RIGHT);

    // P1/P2/P3 authored for right hand
    private static final WeaponSkillPose P1_RIGHT = new WeaponSkillPose(0.1f, -4.45f, 98.99f, -1.87f, 3.2f, 1.13f);
    private static final WeaponSkillPose P2_RIGHT = new WeaponSkillPose(-97.15f, -4.45f, 98.99f, -1.87f, 3.2f, 1.13f);
    private static final WeaponSkillPose P3_RIGHT = new WeaponSkillPose(-92.03f, 8.13f, -8.84f, 3.13f, 3.2f, 1.13f);

    @Override
    public boolean apply(PoseStack poseStack, HumanoidArm arm, float progress) {
        float t = Mth.clamp(progress, 0.0f, 1.0f);
        boolean right = arm == HumanoidArm.RIGHT;

        WeaponSkillPose base = right ? BASE_RIGHT : BASE_LEFT;
        WeaponSkillPose p1 = right ? P1_RIGHT : WeaponSkillPose.mirrorRightToLeft(P1_RIGHT);
        WeaponSkillPose p2 = right ? P2_RIGHT : WeaponSkillPose.mirrorRightToLeft(P2_RIGHT);
        WeaponSkillPose p3 = right ? P3_RIGHT : WeaponSkillPose.mirrorRightToLeft(P3_RIGHT);

        WeaponSkillPose out;
        if (t <= 0.20f) {
            float u = t / 0.20f;
            out = WeaponSkillPose.lerp(p1, p2, easeOutBack(u, 1.25f));
        } else if (t <= 0.60f) {
            float u = (t - 0.20f) / 0.40f;
            out = WeaponSkillPose.lerp(p2, p3, easeInCubic(u));
        } else {
            float u = (t - 0.60f) / 0.40f;
            out = WeaponSkillPose.lerp(p3, base, easeOutQuad(u));
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
