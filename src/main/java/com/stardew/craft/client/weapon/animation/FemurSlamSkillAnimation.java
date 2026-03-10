package com.stardew.craft.client.weapon.animation;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import org.joml.Vector3f;

public final class FemurSlamSkillAnimation implements WeaponSkillAnimation {

    private static final WeaponSkillPose BASE_RIGHT = new WeaponSkillPose(0f, -90f, 25f, 1.13f, 3.2f, 1.13f);
    private static final WeaponSkillPose BASE_LEFT = WeaponSkillPose.mirrorRightToLeft(BASE_RIGHT);

    private static final WeaponSkillPose P2_RIGHT = new WeaponSkillPose(-125f, -90f, -12f, 1.13f, 2.1f, -0.3f);
    private static final WeaponSkillPose P3_RIGHT = new WeaponSkillPose(-35f, -90f, -8f, 1.13f, 2.4f, -3.8f);
    private static final WeaponSkillPose P4_RIGHT = new WeaponSkillPose(70f, -90f, 12f, 1.13f, 1.6f, -6.8f);

    private static final float TOTAL_TICKS = 19.0f;
    private static final float CHARGE_TICKS = 7.0f;
    private static final float P2_TO_P3_TICKS = 2.0f;
    private static final float CHARGE_END = CHARGE_TICKS / TOTAL_TICKS;
    private static final float P3_START = (CHARGE_TICKS + P2_TO_P3_TICKS) / TOTAL_TICKS;

    private static final float CHARGE_SHAKE_DEG = 1.2f;
    private static final float CHARGE_SHAKE_SPEED = 6.0f;

    private static final Vector3f GRIP_PIVOT = new Vector3f(0.0f, -6.0f / 16.0f, 0.0f);

    @Override
    public boolean apply(PoseStack poseStack, HumanoidArm arm, float progress) {
        float t = Mth.clamp(progress, 0.0f, 1.0f);
        boolean right = arm == HumanoidArm.RIGHT;

        WeaponSkillPose base = right ? BASE_RIGHT : BASE_LEFT;
        WeaponSkillPose p1 = base;
        WeaponSkillPose p2 = right ? P2_RIGHT : WeaponSkillPose.mirrorRightToLeft(P2_RIGHT);
        WeaponSkillPose p3 = right ? P3_RIGHT : WeaponSkillPose.mirrorRightToLeft(P3_RIGHT);
        WeaponSkillPose p4 = right ? P4_RIGHT : WeaponSkillPose.mirrorRightToLeft(P4_RIGHT);

        WeaponSkillPose out;
        if (t <= CHARGE_END) {
            float u = t / CHARGE_END;
            WeaponSkillPose chargePose = applyChargeShake(p2, u, t);
            out = WeaponSkillPose.lerp(p1, chargePose, easeOutCubic(u));
        } else if (t <= P3_START) {
            float u = (t - CHARGE_END) / (P3_START - CHARGE_END);
            out = WeaponSkillPose.lerp(p2, p3, easeInCubic(u));
        } else {
            float u = (t - P3_START) / (1.0f - P3_START);
            out = WeaponSkillPose.lerp(p3, p4, easeInCubic(u));
        }

        WeaponSkillAnimationMath.applyDeltaFromBaseDisplayWithPivot(poseStack, GRIP_PIVOT, base, out);
        return true;
    }

    private static WeaponSkillPose applyChargeShake(WeaponSkillPose pose, float charge01, float progress) {
        float ticks = progress * TOTAL_TICKS;
        float time = ticks / 20.0f;
        float shake = Mth.sin(time * Mth.TWO_PI * CHARGE_SHAKE_SPEED);
        float amp = CHARGE_SHAKE_DEG * (0.35f + 0.65f * charge01);
        float ry = pose.ry() + shake * (amp * 0.35f);
        float rz = pose.rz() + shake * (amp * 0.55f);
        return new WeaponSkillPose(pose.rx(), ry, rz, pose.tx(), pose.ty(), pose.tz());
    }

    private static float easeOutCubic(float t) {
        t = Mth.clamp(t, 0.0f, 1.0f);
        float inv = 1.0f - t;
        return 1.0f - (inv * inv * inv);
    }

    private static float easeInCubic(float t) {
        t = Mth.clamp(t, 0.0f, 1.0f);
        return t * t * t;
    }
}
