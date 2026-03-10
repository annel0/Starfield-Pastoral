package com.stardew.craft.client.weapon.animation;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.HumanoidArm;

public final class SteelSmallswordGuardAnimation implements WeaponSkillAnimation {

    private static final WeaponSkillPose BASE_RIGHT = new WeaponSkillPose(0f, -90f, 25f, 1.13f, 3.2f, 1.13f);
    private static final WeaponSkillPose BASE_LEFT = WeaponSkillPose.mirrorRightToLeft(BASE_RIGHT);

    // P1 guard pose (right-hand authored)
    private static final WeaponSkillPose P1_RIGHT = new WeaponSkillPose(0.1f, -4.45f, 98.99f, -1.87f, 3.2f, 1.13f);

    @Override
    public boolean apply(PoseStack poseStack, HumanoidArm arm, float progress) {
        boolean right = arm == HumanoidArm.RIGHT;

        WeaponSkillPose base = right ? BASE_RIGHT : BASE_LEFT;
        WeaponSkillPose p1 = right ? P1_RIGHT : WeaponSkillPose.mirrorRightToLeft(P1_RIGHT);

        WeaponSkillPose out;
        out = p1;

        WeaponSkillAnimationMath.applyDeltaFromBaseDisplay(poseStack, base, out);
        return true;
    }
}
