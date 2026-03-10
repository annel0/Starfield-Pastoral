package com.stardew.craft.client.weapon.animation;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.HumanoidArm;

public interface WeaponSkillAnimation {
    boolean apply(PoseStack poseStack, HumanoidArm arm, float progress);
}
