package com.stardew.craft.client.weapon.animation;

import com.mojang.blaze3d.vertex.PoseStack;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public final class WeaponSkillAnimationMath {

    private WeaponSkillAnimationMath() {}

    public static void applyDeltaFromBaseDisplay(PoseStack poseStack, WeaponSkillPose base, WeaponSkillPose target) {
        Quaternionf rBase = eulerXYZDegreesToQuat(base.rx(), base.ry(), base.rz());
        Quaternionf rTarget = eulerXYZDegreesToQuat(target.rx(), target.ry(), target.rz());

        Quaternionf rDelta = new Quaternionf(rTarget);
        rDelta.mul(new Quaternionf(rBase).invert());

        Vector3f tBase = new Vector3f(base.tx() / 16.0F, base.ty() / 16.0F, base.tz() / 16.0F);
        Vector3f tTarget = new Vector3f(target.tx() / 16.0F, target.ty() / 16.0F, target.tz() / 16.0F);

        Vector3f tBaseRotated = new Vector3f(tBase).rotate(rDelta);
        Vector3f tDelta = new Vector3f(tTarget).sub(tBaseRotated);

        poseStack.translate(tDelta.x, tDelta.y, tDelta.z);
        poseStack.mulPose(rDelta);
    }

    public static void applyDeltaFromBaseDisplayWithPivot(PoseStack poseStack, Vector3f pivot,
                                                          WeaponSkillPose base, WeaponSkillPose target) {
        Quaternionf rBase = eulerXYZDegreesToQuat(base.rx(), base.ry(), base.rz());
        Quaternionf rTarget = eulerXYZDegreesToQuat(target.rx(), target.ry(), target.rz());

        Quaternionf rDelta = new Quaternionf(rTarget);
        rDelta.mul(new Quaternionf(rBase).invert());

        Vector3f tBase = new Vector3f(base.tx() / 16.0F, base.ty() / 16.0F, base.tz() / 16.0F);
        Vector3f tTarget = new Vector3f(target.tx() / 16.0F, target.ty() / 16.0F, target.tz() / 16.0F);

        Vector3f baseMinusPivot = new Vector3f(tBase).sub(pivot);
        Vector3f rotated = baseMinusPivot.rotate(rDelta);
        Vector3f tDeltaPivot = new Vector3f(tTarget).sub(pivot).sub(rotated);

        poseStack.translate(tDeltaPivot.x, tDeltaPivot.y, tDeltaPivot.z);
        poseStack.translate(pivot.x, pivot.y, pivot.z);
        poseStack.mulPose(rDelta);
        poseStack.translate(-pivot.x, -pivot.y, -pivot.z);
    }

    private static Quaternionf eulerXYZDegreesToQuat(float xDeg, float yDeg, float zDeg) {
        float x = xDeg * ((float) Math.PI / 180.0F);
        float y = yDeg * ((float) Math.PI / 180.0F);
        float z = zDeg * ((float) Math.PI / 180.0F);
        Quaternionf q = new Quaternionf();
        q.rotateX(x);
        q.rotateY(y);
        q.rotateZ(z);
        return q;
    }
}
