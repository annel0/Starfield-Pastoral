package com.stardew.craft.client.weapon.animation;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;

public final class PirateSwordPlunderAnimation implements WeaponSkillAnimation {

    private static final WeaponSkillPose BASE_RIGHT = new WeaponSkillPose(0f, -90f, 25f, 1.13f, 3.2f, 1.13f);
    private static final WeaponSkillPose BASE_LEFT = WeaponSkillPose.mirrorRightToLeft(BASE_RIGHT);

    // P1 authored (right-hand)
    private static final WeaponSkillPose P1_RIGHT = new WeaponSkillPose(-116.21f, -56.43f, -74.08f, 1.63f, 7.95f, 1.13f);

    // P2 authored (right-hand)
    private static final WeaponSkillPose P2_RIGHT = new WeaponSkillPose(-75.27f, -15.47f, 77.96f, 1.88f, 3.95f, -2.87f);

    private static final float T1 = 0.08f; // snap to P1
    private static final float T2 = 0.38f; // powerful pass to P2

    @Override
    public boolean apply(PoseStack poseStack, HumanoidArm arm, float progress) {
        float t = Mth.clamp(progress, 0.0f, 1.0f);
        boolean right = arm == HumanoidArm.RIGHT;

        WeaponSkillPose base = right ? BASE_RIGHT : BASE_LEFT;
        WeaponSkillPose p1 = right ? P1_RIGHT : WeaponSkillPose.mirrorRightToLeft(P1_RIGHT);
        WeaponSkillPose p2 = right ? P2_RIGHT : WeaponSkillPose.mirrorRightToLeft(P2_RIGHT);

        WeaponSkillPose out;
        if (t <= T1) {
            out = p1; // instant snap, no transition
        } else if (t <= T2) {
            float u = (t - T1) / (T2 - T1);
            out = WeaponSkillPose.lerp(p1, p2, easeOutBack(u, 1.6f));
        } else {
            float u = (t - T2) / (1.0f - T2);
            out = WeaponSkillPose.lerp(p2, base, easeOutQuad(u));
        }

        WeaponSkillAnimationMath.applyDeltaFromBaseDisplay(poseStack, base, out);
        return true;
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
