package com.stardew.craft.client.weapon.animation;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;

public final class CutlassCrescentSlashAnimation implements WeaponSkillAnimation {

    private static final WeaponSkillPose BASE_RIGHT = new WeaponSkillPose(0f, -90f, 25f, 1.13f, 3.2f, 1.13f);
    private static final WeaponSkillPose BASE_LEFT = WeaponSkillPose.mirrorRightToLeft(BASE_RIGHT);

    // 来自你给的三点（起点/中点/终点）
    private static final WeaponSkillPose P1_RIGHT = new WeaponSkillPose(-75.71f, 3.01f, 128.55f, -15.37f, 3.7f, -2.87f);
    private static final WeaponSkillPose P2_RIGHT = new WeaponSkillPose(-82.93f, -12.86f, 55.93f, -8.62f, 3.7f, -8.87f);
    private static final WeaponSkillPose P3_RIGHT = new WeaponSkillPose(-93.67f, -0.57f, -14.34f, 1.63f, 2.7f, -4.37f);

    private static final float T1 = 0.16f; // 默认 -> P3
    private static final float T2 = 0.50f; // P3 -> P1（跳过P2）
    @SuppressWarnings("unused")
    private static final float T3 = 0.65f; // P1 停留开始
    private static final float T4 = 0.78f; // P1 停留结束（约0.13s）

    @Override
    public boolean apply(PoseStack poseStack, HumanoidArm arm, float progress) {
        float t = Mth.clamp(progress, 0.0f, 1.0f);
        boolean right = arm == HumanoidArm.RIGHT;

        WeaponSkillPose base = right ? BASE_RIGHT : BASE_LEFT;
        WeaponSkillPose p1 = right ? P1_RIGHT : WeaponSkillPose.mirrorRightToLeft(P1_RIGHT);
        @SuppressWarnings("unused")
        WeaponSkillPose p2 = right ? P2_RIGHT : WeaponSkillPose.mirrorRightToLeft(P2_RIGHT);
        WeaponSkillPose p3 = right ? P3_RIGHT : WeaponSkillPose.mirrorRightToLeft(P3_RIGHT);

        WeaponSkillPose out;
        if (t <= T1) {
            // 加过渡：默认 -> P3
            float u = t / T1;
            out = WeaponSkillPose.lerp(base, p3, easeOutQuad(u));
        } else if (t <= T2) {
            float u = (t - T1) / (T2 - T1);
            out = WeaponSkillPose.lerp(p3, p1, easeInCubic(u));
        } else if (t <= T4) {
            // 停留在 P1
            out = p1;
        } else {
            // 短过渡：P1 -> 默认
            float u = (t - T4) / (1.0f - T4);
            out = WeaponSkillPose.lerp(p1, base, easeOutQuad(u));
        }

        WeaponSkillAnimationMath.applyDeltaFromBaseDisplay(poseStack, base, out);
        return true;
    }

    @SuppressWarnings("unused")
    private static WeaponSkillPose arcLerp(WeaponSkillPose a, WeaponSkillPose b, float u, boolean right) {
        float t = easeInOutSine(u);
        WeaponSkillPose base = WeaponSkillPose.lerp(a, b, t);

        // 画圆感：中段抬起与旋转形成弧线（两端归零）
        float arc = Mth.sin(u * Mth.PI);
        float arcRx = 12.0f * arc;
        float arcRz = (right ? 22.0f : -22.0f) * arc;
        float arcTx = (right ? 0.9f : -0.9f) * arc;
        float arcTy = 0.35f * arc;
        float arcTz = -1.4f * arc;

        return new WeaponSkillPose(
                base.rx() + arcRx,
                base.ry(),
                base.rz() + arcRz,
                base.tx() + arcTx,
                base.ty() + arcTy,
                base.tz() + arcTz
        );
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

    private static float easeInOutSine(float t) {
        t = Mth.clamp(t, 0.0f, 1.0f);
        return (float) (0.5 - 0.5 * Math.cos(Math.PI * t));
    }
}
