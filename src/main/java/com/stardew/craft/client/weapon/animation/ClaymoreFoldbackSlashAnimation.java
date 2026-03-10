package com.stardew.craft.client.weapon.animation;

import com.mojang.blaze3d.vertex.PoseStack;
import org.joml.Vector3f;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;

public final class ClaymoreFoldbackSlashAnimation implements WeaponSkillAnimation {

    private static final WeaponSkillPose BASE_RIGHT = new WeaponSkillPose(0f, -90f, 25f, 1.13f, 3.2f, 1.13f);
    private static final WeaponSkillPose BASE_LEFT = WeaponSkillPose.mirrorRightToLeft(BASE_RIGHT);

    // 使用给定的 P1 / P2（右手为基准）
    private static final WeaponSkillPose P1_RIGHT = new WeaponSkillPose(-87.91f, -9.23f, -52.08f, 1.13f, 3.2f, 1.13f);
    private static final WeaponSkillPose P2_RIGHT = new WeaponSkillPose(-80.52f, -6.07f, 115.39f, -14.87f, 3.2f, 1.13f);

    private static final Vector3f HANDLE_PIVOT = new Vector3f(0.0f, -6.0f / 16.0f, 0.0f);

    // 第一段：默认->P1->P2，然后在 P2 停顿
    private static final float T1 = 0.25f;
    private static final float T2 = 0.50f;
    private static final float T3 = 0.82f;

    @Override
    public boolean apply(PoseStack poseStack, HumanoidArm arm, float progress) {
        float t = Mth.clamp(progress, 0.0f, 1.0f);
        boolean right = arm == HumanoidArm.RIGHT;

        WeaponSkillPose base = right ? BASE_RIGHT : BASE_LEFT;
        WeaponSkillPose p1 = right ? P1_RIGHT : WeaponSkillPose.mirrorRightToLeft(P1_RIGHT);
        WeaponSkillPose p2 = right ? P2_RIGHT : WeaponSkillPose.mirrorRightToLeft(P2_RIGHT);

        WeaponSkillPose out;
        if (t <= T1) {
            float u = t / T1;
            out = WeaponSkillPose.lerp(base, p1, easeOutQuad(u));
        } else if (t <= T2) {
            float u = (t - T1) / (T2 - T1);
            out = WeaponSkillPose.lerp(p1, p2, easeInCubic(u));
        } else if (t <= T3) {
            out = p2;
        } else {
            out = p2;
        }

        WeaponSkillAnimationMath.applyDeltaFromBaseDisplayWithPivot(poseStack, HANDLE_PIVOT, base, out);
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
